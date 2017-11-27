/*
Copyright 1994 The PL-J Project. All rights reserved.

Redistribution and use in source and binary forms, with or without modification,
are permitted provided that the following conditions are met:

   1. Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
   2. Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer
   in the documentation and/or other materials provided with the distribution.

THIS SOFTWARE IS PROVIDED BY THE PL-J PROJECT ``AS IS'' AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL
THE PL-J PROJECT OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS;
   OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
   OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED
   OF THE POSSIBILITY OF SUCH DAMAGE.

The views and conclusions contained in the software and documentation are those of the authors and should not be
interpreted as representing official policies, either expressed or implied, of the PL-J Project.
*/

/**
 * file:            commm_messages.c
 * author:            PostgreSQL developement group.
 * author:            Laszlo Hornyak
 */

#include <stdlib.h>

#include "comm_utils.h"
#include "messages/messages.h"

/* Recursive function to free up the type structure */
static void free_type(plcType *typArr) {
    if (typArr->typeName != NULL) {
        pfree(typArr->typeName);
    }
    if (typArr->nSubTypes > 0) {
        int i = 0;
        for (i = 0; i < typArr->nSubTypes; i++) {
            free_type(&typArr->subTypes[i]);
        }
        pfree(typArr->subTypes);
    }
}

void free_callreq(plcMsgCallreq *req, bool isShared, bool isSender) {
    int i;

    if (!isShared) {
        /* free the procedure */
        pfree(req->proc.name);
        pfree(req->proc.src);
    }

    /* free the arguments */
    for (i = 0; i < req->nargs; i++) {
        if (!isShared && req->args[i].name != NULL) {
            pfree(req->args[i].name);
        }
        if (req->args[i].data.value != NULL) {
            // For UDT we need to free up internal structures
            if (req->args[i].type.type == PLC_DATA_UDT) {
                plc_free_udt((plcUDT*)req->args[i].data.value, &req->args[i].type, isSender);
            }

            /* For arrays on receiver side we need to free up their data,
             * while on the sender side cleanup is managed by comm_channel */
            if (!isSender && req->args[i].type.type == PLC_DATA_ARRAY) {
                plc_free_array((plcArray*)req->args[i].data.value, &req->args[i].type, isSender);
            } else {
                pfree(req->args[i].data.value);
            }
        }
        free_type(&req->args[i].type);
    }
    pfree(req->args);

    free_type(&req->retType);

    /* free the top-level request */
    pfree(req);
}

void free_result(plcMsgResult *res, bool isSender) {
    int i, j;

    /* free the data array */
    if (res->data != NULL) {
        for (i = 0; i < res->rows; i++) {
            /* this can happen if for some reason we abort sending the result early */
            if (res->data[i] != NULL){
                for (j = 0; j < res->cols; j++) {
                    /* free the data if it is not null */
                    if (res->data[i][j].value != NULL) {
                        // For UDT we need to free up internal structures
                        if (res->types[j].type == PLC_DATA_UDT) {
                            plc_free_udt((plcUDT*)res->data[i][j].value, &res->types[j], isSender);
                        }

                        /* For arrays on receiver side we need to free up their data,
                         * while on the sender side cleanup is managed by comm_channel */
                        if (!isSender && res->types[j].type == PLC_DATA_ARRAY) {
                            plc_free_array((plcArray*)res->data[i][j].value, &res->types[j], isSender);
                        } else {
                            pfree(res->data[i][j].value);
                        }
                    }
                }
                /* free the row */
                pfree(res->data[i]);
            }
        }
        pfree(res->data);
    }

    /* free the types and names arrays */
    for (i = 0; i < res->cols; i++) {
        if (res->names[i] != NULL) {
            pfree(res->names[i]);
        }
        free_type(&res->types[i]);
    }
    pfree(res->types);
    pfree(res->names);

    pfree(res);
}

void free_error(plcMsgError *msg) {
    if (msg != NULL) {
        if (msg->message != NULL) {
            pfree(msg->message);
        }
        if (msg->stacktrace != NULL) {
            pfree(msg->stacktrace);
        }
        pfree(msg);
    }
}

plcArray *plc_alloc_array(int ndims) {
    plcArray *arr;
    arr = (plcArray*)pmalloc(sizeof(plcArray));
    arr->meta = (plcArrayMeta*)pmalloc(sizeof(plcArrayMeta));
    arr->meta->ndims = ndims;
    arr->meta->dims  = NULL;
    if (ndims > 0)
        arr->meta->dims = (int*)pmalloc(ndims * sizeof(int));
    arr->meta->size = 0;
    return arr;
}

void plc_free_array(plcArray *arr, plcType *type, bool isSender) {
    int i;
    if (arr != NULL) {
        if (arr->meta->type == PLC_DATA_TEXT || arr->meta->type == PLC_DATA_BYTEA) {
            for (i = 0; i < arr->meta->size; i++) {
                if ( ((char**)arr->data)[i] != NULL ) {
                    pfree(((char**)arr->data)[i]);
                }
            }
        }
        if (arr->meta->type == PLC_DATA_UDT) {
            for (i = 0; i < arr->meta->size; i++) {
                if ( ((char**)arr->data)[i] != NULL ) {
                    plc_free_udt(((plcUDT**)arr->data)[i], &type->subTypes[0], isSender);
                    pfree(((plcUDT**)arr->data)[i]);
                }
            }
        }
        if (arr->meta->size > 0) {
            pfree(arr->data);
            pfree(arr->nulls);
        }
        if (arr->meta->ndims > 0) {
            pfree(arr->meta->dims);
        }
        pfree(arr->meta);
        pfree(arr);
    }
}

plcUDT *plc_alloc_udt(int nargs) {
    plcUDT *res;

    res = pmalloc(sizeof(plcUDT));
    res->data = pmalloc(nargs * sizeof(rawdata));

    return res;
}

void plc_free_udt(plcUDT *udt, plcType *type, bool isSender) {
    int i;

    for (i = 0; i < type->nSubTypes; i++) {
        if (!udt->data[i].isnull) {
            if (!isSender && type->subTypes[i].type == PLC_DATA_ARRAY) {
                plc_free_array((plcArray*)udt->data[i].value, &type->subTypes[i], isSender);
            } else {
                pfree(udt->data[i].value);
            }
        }
    }

    pfree(udt->data);
}

int plc_get_type_length(plcDatatype dt) {
    int res = 0;
    switch (dt) {
        case PLC_DATA_INT1:
            res = 1;
            break;
        case PLC_DATA_INT2:
            res = 2;
            break;
        case PLC_DATA_INT4:
            res = 4;
            break;
        case PLC_DATA_INT8:
            res = 8;
            break;
        case PLC_DATA_FLOAT4:
            res = 4;
            break;
        case PLC_DATA_FLOAT8:
            res = 8;
            break;
        case PLC_DATA_TEXT:
        case PLC_DATA_UDT:
        case PLC_DATA_BYTEA:
            /* 8 = the size of pointer */
            res = 8;
            break;
        case PLC_DATA_ARRAY:
        default:
            lprintf(ERROR, "Type %s [%d] cannot be passed plc_get_type_length function",
                           plc_get_type_name(dt), (int)dt);
            break;
    }
    return res;
}

const char *plc_get_type_name(plcDatatype dt) {
    const char * types[] = {"PLC_DATA_INT1", "PLC_DATA_INT2", "PLC_DATA_INT4", "PLC_DATA_INT8",
                            "PLC_DATA_FLOAT4", "PLC_DATA_FLOAT8",
                            "PLC_DATA_TEXT",
                            "PLC_DATA_ARRAY",
                            "PLC_DATA_UDT",
                            "PLC_DATA_BYTEA",
                            "PLC_DATA_INVALID"};
    return (dt >= 0 && dt <= 10) ? types[dt] : "UNKNOWN";
}