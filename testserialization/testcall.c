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


int main(int argc, char **argv)
{

	free(plc_top_alloc(100));

    	plcConn *send = plcConnect(8000);

	char *value1 = (char *)malloc(TEST_SIZE);
		int i;
		for (i=0; i< TEST_SIZE-1;i++){
			value1[i]= 'A' + random() % 26;
		}
		value1[TEST_SIZE-1]='\0';
    // this is the child
	plcMsgCallreq *req = (plcMsgCallreq *)malloc(sizeof( plcMsgCallreq));
	req->msgtype = MT_CALLREQ;
	req->proc.name="foobar";
	req->proc.src = "function definition";
	req->hasChanged = 0;
	req->objectid=205;
	req->retType.type = PLC_DATA_TEXT;
	req->retType.nSubTypes = 0;
	req->retType.typeName="TEXT";
	req->retType.subTypes=NULL;
	
	req->nargs = 8;
	req->args = (plcArgument *)malloc(8*sizeof(plcArgument));

	req->args[0].name = "arg1";
	req->args[0].type.type=PLC_DATA_TEXT;
	req->args[0].type.nSubTypes = 0;
	req->args[0].type.typeName="TEXT";
	req->args[0].type.subTypes=NULL;
	req->args[0].data.isnull=0;	
	req->args[0].data.value=value1;

	req->args[1].name="arg2";
	req->args[1].type.type=PLC_DATA_INT1;
	req->args[1].type.nSubTypes = 0;
	req->args[1].type.typeName="INT1";
	req->args[1].type.subTypes=NULL;
	req->args[1].data.isnull=0;	
	unsigned char b = 127;
	req->args[1].data.value=(char *)&b;

	req->args[2].name="arg3";
	req->args[2].type.type=PLC_DATA_INT2;
	req->args[2].type.nSubTypes = 0;
	req->args[2].type.typeName="INT2";
	req->args[2].type.subTypes=NULL;
	req->args[2].data.isnull=0;	
	unsigned short s = 300;
	req->args[2].data.value=(char *)&s;
	
	req->args[3].name="arg4";
	req->args[3].type.type=PLC_DATA_INT4;
	req->args[3].type.nSubTypes = 0;
	req->args[3].type.typeName="INT1";
	req->args[3].type.subTypes=NULL;
	req->args[3].data.isnull=0;	
	unsigned int ival = 33330;
	req->args[3].data.value=(char *)&ival;
	
	req->args[4].name="arg5";
	req->args[4].type.type=PLC_DATA_INT8;
	req->args[4].type.nSubTypes = 0;
	req->args[4].type.typeName="INT1";
	req->args[4].type.subTypes=NULL;
	req->args[4].data.isnull=0;	
	unsigned long l = 333330;
	req->args[4].data.value=(char *)&l;

	req->args[5].name="arg6";
	req->args[5].type.type = PLC_DATA_FLOAT8;
	req->args[5].type.nSubTypes = 0;
	req->args[5].type.typeName="FLOAT8";
	req->args[5].type.subTypes=NULL;
	req->args[5].data.isnull=0;	
	double dval = 3456.5;
	req->args[5].data.value=(char *)&dval;

	req->args[6].name="arg7";
	req->args[6].type.type = PLC_DATA_BYTEA;
	req->args[6].type.nSubTypes = 0;
	req->args[6].type.typeName="bytea";
	req->args[6].type.subTypes=NULL;
	req->args[6].data.isnull=0;	
	unsigned char *bytea = malloc(20+4);
	int *iptr = bytea;
	*iptr = 20;
	unsigned char *ptr = bytea+sizeof(int);
	for ( int i=0; i < 20; i++) *ptr++ = i;
	req->args[6].data.value=(char *)bytea;

	req->args[7].name="arg8";
	req->args[7].type.type = PLC_DATA_FLOAT4;
	req->args[7].type.nSubTypes = 0;
	req->args[7].type.typeName="FLOAT4";
	req->args[7].type.subTypes=NULL;
	req->args[7].data.isnull=0;
	float fval = 1245.5;
	req->args[7].data.value=(char *)&fval;

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
	
	return 0;
}
