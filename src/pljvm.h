/*------------------------------------------------------------------------------
 *
 *
 * Copyright (c) 2016, Pivotal.
 *
 *------------------------------------------------------------------------------
 */

#ifndef PLC_PLCONTAINER_H
#define PLC_PLCONTAINER_H

#include "fmgr.h"

#define UNUSED __attribute__ (( unused ))

MemoryContext pl_container_caller_context;

/* entrypoint for all pljvm procedures */
Datum pljvm_call_handler(PG_FUNCTION_ARGS);

#endif /* PLC_PLCONTAINER_H */
