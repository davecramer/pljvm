/*------------------------------------------------------------------------------
 *
 *
 * Copyright (c) 2016, Pivotal.
 *
 *------------------------------------------------------------------------------
 */
#ifndef PLC_MESSAGE_CALLREQ_H
#define PLC_MESSAGE_CALLREQ_H

#include "message_base.h"

typedef struct {
    char *src;  // source code of the procedure
    char *name; // name of procedure
} plcProcSrc;

typedef struct plcMsgCallreq {
    base_message_content;    // message_type ID
    unsigned int objectid;   // OID of the function
    int          hasChanged; // flag signaling the function has changed
    plcProcSrc   proc;       // procedure - its name and source code
    plcType      retType;    // function return type
    int          retset;     // whether the function is set-returning
    int          nargs;      // number of function arguments
    plcArgument *args;       // function arguments
} plcMsgCallreq;

/*
  Frees a callreq and all subfields of the struct, this function
  assumes ownership of all pointers in the struct and substructs
*/
void free_callreq(plcMsgCallreq *req, bool isShared, bool isSender);

#endif /* PLC_MESSAGE_CALLREQ_H */
