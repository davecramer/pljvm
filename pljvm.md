# PLJVM PostgreSQL procedural language for JAVA

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
    - working on UDT's but have the issue of how to figure out the postgresql type -> java type mapping


## What problems does this solve?

- synchronous notification of data changes
- alternatives pl/java [Logical Decoding](https://github.com/davecramer/LogicalDecode).
- what makes it so special it can be used with any [JVM based language](https://en.wikipedia.org/wiki/List_of_JVM_languages)
- performance 
   - looks like the overhead is around 1ms. 
   - 10000 iterations of calling a sql function to add two numbers takes .193 ms per call
   - ```CREATE OR REPLACE FUNCTION sql_add(i int4, j int4) RETURNS int4 AS $$
        select i+j;
        $$ LANGUAGE sql;```
   - 10000 iterations of calling a pljvm function to add two numbers takes 1.105 ms per call
   - ```CREATE OR REPLACE FUNCTION pljvm_add(i int4, j int4) RETURNS int4 AS $$
        org.postgresql.plj.test.Int.add
        $$ LANGUAGE pljvm;```

- need help with 
- classpath separation
    - since calls are being handled by the same JVM don't want to leak data from one postgresql process to another
- library or full fledged application?
    - currently the library could be used by any JVM easily
- how to pass UDT's where do I get the package name from
    - the problem is that we can't pass package information along with the name of the UDT
- autoboxing ?
    - how to call a method defined as public int add(int i, int j) or public Integer add(Integer i, Integer j)
    
