## PL/JVM

This is an implementation of a trusted language execution engine that executes
code in any JVM via an RPC mechanism

### Requirements

Any JVM based language

### Building PL/JVM Language


1. Make and install it: `make clean && make && make install`



### Running the tests
 From plj-java run `maven exec:java`
 Then from the main dir run `make installcheck`
