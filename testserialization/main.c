/*
 * main.c
 *
 *  Created on: Nov 13, 2017
 *      Author: davec
 */
#include <assert.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>

#include <arpa/inet.h>
#include <netdb.h> /* getprotobyname */
#include <netinet/in.h>
#include <sys/socket.h>
#include <unistd.h>

#include "common/comm_connectivity.h"
#include "common/comm_channel.h"
#include "common/messages/messages.h"
#define TEST_SIZE 1024


int mainx(int argc, char **argv)
{


    plcConn *send = plcConnect(8000);

	
	plcMsgCallreq *req = (plcMsgCallreq *)malloc(sizeof( plcMsgCallreq));
	req->msgtype = MT_CALLREQ;
	req->proc.name="addExact";
	req->proc.src = "org.postgresql.plj.Add.add";
	req->hasChanged = 0;
	req->objectid=205;
	req->retType.type = PLC_DATA_INT4;
	req->retType.nSubTypes = 0;
	req->retType.typeName="INT";
	req->retType.subTypes=NULL;
	
	req->nargs = 2;
	req->args = (plcArgument *)malloc(7*sizeof(plcArgument));

	req->args[0].name = "arg1";
	req->args[0].type.type=PLC_DATA_INT4;
	req->args[0].type.nSubTypes = 0;
	req->args[0].type.typeName="INT4";
	req->args[0].type.subTypes=NULL;
	req->args[0].data.isnull=0;	
    int arg1 = 15;
	req->args[0].data.value=&arg1;

	req->args[1].name="arg2";
	req->args[1].type.type=PLC_DATA_INT4;
	req->args[1].type.nSubTypes = 0;
	req->args[1].type.typeName="INT1";
	req->args[1].type.subTypes=NULL;
	req->args[1].data.isnull=0;	
	int arg2 = 20;
	req->args[1].data.value=(char *)&arg2;

    pljvm_channel_send(send, (plcMessage *)req);
	plcMessage *msg;
	pljvm_channel_receive(send, &msg);
	
	plcMsgResult *msgResult = (plcMsgResult *)msg;
	
	printf ("Received message with %d rows and %d cols\n", msgResult->rows, msgResult->cols);
	
	int cols = msgResult->cols;
	for (int i=0; i< cols; i++) {
		printf("Column %s, Type %d, %s\n", msgResult->names[i], msgResult->types[i].type, msgResult->types[i].typeName);
	}
	rawdata *dptr = *msgResult->data;
	int len;
	for ( int row=0; row < msgResult->rows; row ++) {
		for (int col = 0; col < msgResult->cols; col++) {
			switch (msgResult->types[col].type) {
				case PLC_DATA_INT1: // 1-byte integer
					printf (" %d \n", *((bool*)dptr->value));
					dptr++;
					break; 
				case PLC_DATA_INT2:// 2-byte integer
					printf (" %d \n", *((short*)dptr->value));
					dptr++;
					break; 
				case PLC_DATA_INT4:// 4-byte integer
					printf (" %d \n", *((int*)dptr->value));
					dptr++;
					break;
				case PLC_DATA_INT8:// 8-byte integer
					printf (" %ld \n", *((long*)dptr->value));
					dptr++;
					break;
		
				case PLC_DATA_FLOAT4:// 4-byte float
					printf (" %f \n", *((float*)dptr->value));
					dptr++;
					break;

				case PLC_DATA_FLOAT8:// 8-byte float
					printf (" %lf \n", *((double*)dptr->value));
					dptr++;
					break;

				case PLC_DATA_TEXT: // string
					printf(" %s \n", dptr->value);
					dptr++;
					break;
			}
			
		}
	}
	plcDisconnect(send);
	return 0;
}
