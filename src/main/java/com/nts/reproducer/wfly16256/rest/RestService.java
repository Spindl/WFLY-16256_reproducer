package com.nts.reproducer.wfly16256.rest;

import org.eclipse.microprofile.opentracing.ClientTracingRegistrar;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;

import javax.enterprise.context.ApplicationScoped;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.client.ClientBuilder;

@ApplicationScoped
@Path("reproducer")
@Produces("application/json")
public class RestService {

    private static final int DEFAULT_TIMES = 10000;

    private final Runtime runtime = Runtime.getRuntime();

    @GET
    @Path("configureClients")
    public double createClientsWithTracing(@QueryParam("times") Integer times) {

        // Configure tracing on the ClientBuilder a bunch of times. This alone is sufficient to provoke the instance leak.
        for (int i = 0; i < (null != times ? times : DEFAULT_TIMES); i++) {
            ClientBuilder restClientBuilder = ResteasyClientBuilder.newBuilder();
            // This call creates the instance of the Tracer that is never released
            ClientTracingRegistrar.configure(restClientBuilder);
        }

        // Trigger a full Garbage Collection run to get the real blocked memory size later.
        System.gc();
        // Calculate the heap utilization and send it back to the client
        return ((1d * (runtime.totalMemory() - runtime.freeMemory())) / runtime.maxMemory()) * 100;
    }
}
