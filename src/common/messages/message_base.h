/*------------------------------------------------------------------------------
 *
 *
 * Copyright (c) 2016, Pivotal.
 *
 *------------------------------------------------------------------------------
 */
#ifndef PLC_MESSAGE_BASE_H
#define PLC_MESSAGE_BASE_H

#include "../comm_utils.h"

#define base_message_content unsigned short msgtype;

typedef struct plcMessage {
    base_message_content
} plcMessage;

typedef struct {
    int   isnull;
    char *value;
} rawdata;

typedef enum {
    PLC_DATA_INT1    = 0,  // 1-byte integer
    PLC_DATA_INT2    = 1,  // 2-byte integer
    PLC_DATA_INT4    = 2,  // 4-byte integer
    PLC_DATA_INT8    = 3,  // 8-byte integer
    PLC_DATA_FLOAT4  = 4,  // 4-byte float
    PLC_DATA_FLOAT8  = 5,  // 8-byte float
    PLC_DATA_TEXT    = 6,  // Text - transferred as a set of bytes of predefined length,
                           //        stored as cstring
    PLC_DATA_ARRAY   = 7,  // Array - array type specification should follow
    PLC_DATA_UDT     = 8,  // User-defined type, specification to follow
    PLC_DATA_BYTEA   = 9,  // Arbitrary set of bytes, stored and transferred as length + data
    PLC_DATA_INVALID = 10  // Invalid data type
} plcDatatype;

typedef struct plcType plcType;

struct plcType {
    plcDatatype  type;
    short        nSubTypes;
    char        *typeName;
    plcType     *subTypes;
};

typedef struct {
    plcType  type;
    char    *name;
    rawdata  data;
} plcArgument;

int plc_get_type_length(plcDatatype dt);
const char* plc_get_type_name(plcDatatype dt);

#endif /* PLC_MESSAGE_BASE_H */
