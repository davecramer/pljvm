#PLJVM PostgreSQL procedural language for JAVA

## PL/JVM enables calling arbitrary java methods on objects running in your code.
- came from [PL/J](https://github.com/codehaus/plj) 
- pl/j was written 14 years ago by Laszlo Hornyak
- competed with pl/java and lost the VHS/Betamax discussion
- since then while working at Pivotal I helped write [pl/container](https://github.com/greenplum-db/plcontainer)
- PL/Container - GPDB execution sandboxing using docker containers for Python and R
- all of these provide what is referred to as a "trusted" language in PostgreSQL. 
- trusted languages are prevented from harming the database by either directly accessing the filesystem or reading others data.
- also PostgreSQL allows developers to create trusted language functions without being a superuser.

## Why ?
- well mostly because I could and I had some free time.
- almost all of the work on the server was done and debugged
- netty makes it somewhat easy (after you grok netty).


## Under the covers
- On the server side you create a language called pl/jvm
- languages are nothing more than a call handler written in C which intercepts a call and has information about 
    - return type
    - arguments
    - function definition

- Once control is transferred to the function handler then the fun begins
    - create a call request object with the arguments
    - send the call request to the Java program using a binary protocol which is very similar to PostgreSQL protocol
	- control is transferred to the Java code which 
	    - decodes the request
        - instantiates the function and calls the method defined in the function call
        - encodes the response and sends it back
        - returns control back to the C code in the server
	- currently the code can deal with any base type and arrays of base types

## What problems does this solve?

- synchronous notification of data changes
- alternatives pl/java logical replication.
- what makes it so special it can be used with any jvm based language
- performance
- need help with 
- classpath separation
- library or full fledged application?
- how to pass UDT's where do I get the package name from
- autoboxing ?