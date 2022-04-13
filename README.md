# WFLY-16256_reproducer
A simple reproducer for the WildFly Bug WFLY-16256

It uses the latest available WildFly version `26.0.1.Final`.

## How to run the reproducer
1) Build the WildFly bootable jar by running `mvn clean package`
2) Run the server with `java -Xmx64m -jar target/app-local-SNAPSHOT-bootable.jar`
   1) It's not strictly necessary to limit the heap space, but this way it's easier to spot the leaked instances in a dump
   2) Also the client will be done faster with filling the heap
3) Run the client with `java src/main/java/main/Main.java`

The client will fill the server heap up to a configured threshold value.

## Description
The reproducer consists of two parts which demonstrate how the memory leak occurs:

### Server
The server part offers a simple ReST endpoint under the following URL: 

`http://localhost:8080/rest/reproducer/configureClients`

The endpoint triggers the memory leak for x times by configuring tracing on a client via the `ClientTracingRegistrar`.
The amount of times the leak is triggered can be set via the query param `times`. 
The default value is 10000.

After it is done, the endpoint triggers a full garbage collection and calculates how many percent of the available heap is still occupied.
This value is then sent back to the client.

### Client
The client uses the endpoint to fill the heap up to a threshold percentage.

There are hard-coded constants for: 
* the target heap utilization in %
* the initial value of `times` the client uses 
* the amount of steps in which the heap is filled to the configured target percentage

The client is not very sophisticated, so it will not exactly reach the configured threshold, 
but for testing the leak it surely is sufficient.