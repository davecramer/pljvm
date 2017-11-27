/*------------------------------------------------------------------------------
 *
 *
 * Copyright (c) 2016, Pivotal.
 *
 *------------------------------------------------------------------------------
 */


/* Postgres Headers */
#include "postgres.h"
#include "fmgr.h"
#include "funcapi.h"
#include "miscadmin.h"
#include "executor/spi.h"
#include "commands/trigger.h"


/* PLjvm Headers */
#include "common/comm_channel.h"
#include "common/messages/messages.h"
#include "message_fns.h"
#include "sqlhandler.h"
#include "plc_typeio.h"
#include "pljvm.h"

#ifdef PG_MODULE_MAGIC
PG_MODULE_MAGIC;
#endif

static int port;
static char *host;

PG_FUNCTION_INFO_V1(pljvm_call_handler);

static Datum pljvm_call_hook(PG_FUNCTION_ARGS);
static plcProcResult *pljvm_get_result(FunctionCallInfo  fcinfo,
                                             plcProcInfo      *pinfo);
static Datum pljvm_process_result(FunctionCallInfo  fcinfo,
                                        plcProcInfo      *pinfo,
                                        plcProcResult    *presult);
static void pljvm_process_exception(plcMsgError *msg);
static void pljvm_process_sql(plcMsgSQL *msg, plcConn* conn);
static void pljvm_process_log(plcMsgLog *log);

Datum pljvm_call_handler(PG_FUNCTION_ARGS) {
    Datum datumreturn = (Datum) 0;
    MemoryContext oldMC = NULL;
    int ret;

    /* TODO: handle trigger requests as well */
    if (CALLED_AS_TRIGGER(fcinfo)) {
        elog(ERROR, "PL/Container does not support triggers");
        return datumreturn;
    }

    /* save caller's context */
    oldMC = pl_container_caller_context;
    pl_container_caller_context = CurrentMemoryContext;

    /* Create a new memory context and switch to it */
    ret = SPI_connect();
    if (ret != SPI_OK_CONNECT)
        elog(ERROR, "[pljvm] SPI connect error: %d (%s)", ret,
             SPI_result_code_string(ret));

    /* We need to cover this in try-catch block to catch the even of user
     * requesting the query termination. In this case we should forcefully
     * kill the container and reset its information
     */
    PG_TRY();
    {
        datumreturn = pljvm_call_hook(fcinfo);
    }
    PG_CATCH();
    {
        /* If the reason is Cancel or Termination */
        if (InterruptPending || QueryCancelPending
#ifdef GP_VERSION
        		|| QueryFinishPending
#endif
				) {
        }
        PG_RE_THROW();
    }
    PG_END_TRY();

    /* Return to old memory context */
    ret = SPI_finish();
    if (ret != SPI_OK_FINISH)
        elog(ERROR, "[pljvm] SPI finish error: %d (%s)", ret,
             SPI_result_code_string(ret));

    pl_container_caller_context = oldMC;
    return datumreturn;
}

static Datum pljvm_call_hook(PG_FUNCTION_ARGS) {
    Datum                     result = (Datum) 0;
    plcProcInfo              *pinfo;
    bool                      bFirstTimeCall = true;
    FuncCallContext *volatile funcctx = NULL;
    MemoryContext             oldcontext = NULL;
    plcProcResult            *presult = NULL;

    /* By default we return NULL */
    fcinfo->isnull = true;

    /* Get procedure info from cache or compose it based on catalog */
    pinfo = get_proc_info(fcinfo);

    /* If we have a set-returning function */
    if (fcinfo->flinfo->fn_retset) {
        /* First Call setup */
        if (SRF_IS_FIRSTCALL()) {
            funcctx = SRF_FIRSTCALL_INIT();
        } else {
            bFirstTimeCall = false;
        }

        /* Every call setup */
        funcctx = SRF_PERCALL_SETUP();
        Assert(funcctx != NULL);

        /* SRF initializes special context shared between function calls */
        oldcontext = MemoryContextSwitchTo(funcctx->multi_call_memory_ctx);
    } else {
        oldcontext = MemoryContextSwitchTo(pl_container_caller_context);
    }

    /* First time call for SRF or just a call of scalar function */
    if (bFirstTimeCall) {
        presult = pljvm_get_result(fcinfo, pinfo);
        if (fcinfo->flinfo->fn_retset) {
            funcctx->user_fctx = (void*)presult;
        }
    } else {
        presult = (plcProcResult*)funcctx->user_fctx;
    }

    if (presult == NULL) {
        MemoryContextSwitchTo(oldcontext);
        return result;
    }
    /* If we processed all the rows or the function returned 0 rows we can return immediately */
    if (presult->resrow >= presult->resmsg->rows) {
        free_result(presult->resmsg, false);
        pfree(presult);
        MemoryContextSwitchTo(oldcontext);
        SRF_RETURN_DONE(funcctx);
    }

    /* Process the result message from client */
    result = pljvm_process_result(fcinfo, pinfo, presult);

    presult->resrow += 1;
    MemoryContextSwitchTo(oldcontext);

    if (fcinfo->flinfo->fn_retset) {
        SRF_RETURN_NEXT(funcctx, result);
    } else {
        free_result(presult->resmsg, false);
        pfree(presult);
    }

    return result;
}

static plcProcResult *pljvm_get_result(FunctionCallInfo  fcinfo,
                                             plcProcInfo      *pinfo) {
    plcConn       *conn;
    int            message_type;
    plcMsgCallreq *req    = NULL;
    plcProcResult *result = NULL;

    conn = plcConnect(8000);

    if (conn != NULL) {
        req = pljvm_create_call(fcinfo, pinfo);
        pljvm_channel_send(conn, (plcMessage*)req);
        free_callreq(req, true, true);

        while (1) {
            int res = 0;
            plcMessage *answer;

            res = pljvm_channel_receive(conn, &answer);
            if (res < 0) {
                elog(ERROR, "Error receiving data from the client, %d", res);
                break;
            }

            message_type = answer->msgtype;
            switch (message_type) {
                case MT_RESULT:
                    result = (plcProcResult*)pmalloc(sizeof(plcProcResult));
                    result->resmsg = (plcMsgResult*)answer;
                    result->resrow = 0;
                    break;
                case MT_EXCEPTION:
                    pljvm_process_exception((plcMsgError*)answer);
                    break;
                case MT_SQL:
                    pljvm_process_sql((plcMsgSQL*)answer, conn);
                    break;
                case MT_LOG:
                    pljvm_process_log((plcMsgLog*)answer);
                    break;
                default:
                    elog(ERROR, "Received unhandled message with type id %d "
                    "from client", message_type);
                    break;
            }

            if (message_type != MT_SQL && message_type != MT_LOG)
                break;
        }
        plcDisconnect(conn);
    }
    return result;
}

/*
 * Processing client results message
 */
static Datum pljvm_process_result(FunctionCallInfo  fcinfo,
                                        plcProcInfo      *pinfo,
                                        plcProcResult    *presult) {
    Datum         result = (Datum) 0;
    plcMsgResult *resmsg = presult->resmsg;

    if (resmsg->cols > 1) {
        elog(ERROR, "Functions returning multiple columns are not supported yet");
        return result;
    }

    if (resmsg->rows == 0) {
        return result;
    }

    if (presult->resrow >= resmsg->rows) {
        ereport(ERROR,
             (errcode(ERRCODE_CARDINALITY_VIOLATION),
             errmsg( "Trying to access result row %d of the %d-rows result set",
                     presult->resrow, resmsg->rows)));
        return result;
    }

    if (resmsg->data[presult->resrow][0].isnull == 0) {
        fcinfo->isnull = false;
        result = pinfo->rettype.infunc(resmsg->data[presult->resrow][0].value, &pinfo->rettype);
    }

    return result;
}

/*
 * Processing client log message
 */
static void pljvm_process_log(plcMsgLog *log) {
    ereport(log->level,
            (errcode(ERRCODE_NO_DATA),
                    errmsg("%s", log->message)));
}

/*
 * Processing client SQL query message
 */
static void pljvm_process_sql(plcMsgSQL *msg, plcConn* conn) {
    plcMessage *res;
    volatile MemoryContext oldcontext;
    volatile ResourceOwner oldowner;

    oldcontext = CurrentMemoryContext;
    oldowner = CurrentResourceOwner;
    MemoryContextSwitchTo(pl_container_caller_context);

    res = handle_sql_message(msg);
    if (res != NULL) {
        pljvm_channel_send(conn, res);
        switch (res->msgtype) {
            case MT_RESULT:
                free_result((plcMsgResult*)res, true);
                break;
            case MT_CALLREQ:
                free_callreq((plcMsgCallreq*)res, true, true);
                break;
            default:
                ereport(ERROR,
                      (errcode(ERRCODE_RAISE_EXCEPTION),
                      errmsg( "Returning message type '%c' from SPI call is not implemented", res->msgtype)));
        }
    }

    MemoryContextSwitchTo(oldcontext);
    CurrentResourceOwner = oldowner;

    /*
     * AtEOSubXact_SPI() should not have popped any SPI context, but just
     * in case it did, make sure we remain connected.
     */
    SPI_restore_connection();
}

/*
 * Processing client exception message
 */
static void pljvm_process_exception(plcMsgError *msg) {
    if (msg->stacktrace != NULL) {
        ereport(ERROR,
                (errcode(ERRCODE_RAISE_EXCEPTION),
                 errmsg("PL/JVM client exception occurred: \n %s \n %s", msg->message, msg->stacktrace)));
    } else {
        ereport(ERROR,
                (errcode(ERRCODE_RAISE_EXCEPTION),
                 errmsg("PL/JVM client exception occurred: \n %s", msg->message)));
    }
    free_error(msg);
}
