/*------------------------------------------------------------------------------
 *
 *
 * Copyright (c) 2016, Pivotal.
 *
 *------------------------------------------------------------------------------
 */


#ifndef PLC_SQLHANDLER_H
#define PLC_SQLHANDLER_H

#include "common/messages/messages.h"

plcMessage *handle_sql_message(plcMsgSQL *msg);

#endif /* PLC_SQLHANDLER_H */
