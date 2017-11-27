/*------------------------------------------------------------------------------
 *
 *
 * Copyright (c) 2016, Pivotal.
 *
 *------------------------------------------------------------------------------
 */
#ifndef PLC_COMM_CONNECTIVITY_H
#define PLC_COMM_CONNECTIVITY_H

#include <stddef.h>

#define PLC_BUFFER_SIZE 8192
#define PLC_BUFFER_MIN_FREE 200
#define PLC_INPUT_BUFFER 0
#define PLC_OUTPUT_BUFFER 1

typedef struct plcBuffer {
    char *data;
    int   pStart;
    int   pEnd;
    int   bufSize;
} plcBuffer;

typedef struct plcConn {
    int sock;
    plcBuffer* buffer[2];
} plcConn;

plcConn * plcConnect(int port);
plcConn * plcConnInit(int sock);
void plcDisconnect(plcConn *conn);

int plcBufferAppend (plcConn *conn, char *prt, size_t len);
int plcBufferRead (plcConn *conn, char *resBuffer, size_t len);
int plcBufferReceive (plcConn *conn, size_t nBytes);
int plcBufferFlush (plcConn *conn);

#endif /* PLC_COMM_CONNECTIVITY_H */
