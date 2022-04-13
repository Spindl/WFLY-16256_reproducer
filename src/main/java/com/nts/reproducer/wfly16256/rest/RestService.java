package com.nts.reproducer.wfly16256.rest;

import org.eclipse.microprofile.opentracing.ClientTracingRegistrar;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;

import javax.enterprise.context.ApplicationScoped;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.Response;

@ApplicationScoped
@Path("reproducer")
@Produces("application/json")
public class RestService {

    private static final int DEFAULT_TIMES = 10000;

    private final Runtime runtime = Runtime.getRuntime();

    @GET
    @Path("configureClients")
    public Response createClientsWithTracing(@QueryParam("times") Integer times) {

        // Configure tracing on the ClientBuilder a bunch of times. This alone is sufficient to provoke the instance leak.
        for (int i = 0; i < (null != times ? times : DEFAULT_TIMES); i++) {
            ClientBuilder restClientBuilder = ResteasyClientBuilder.newBuilder();
            ClientTracingRegistrar.configure(restClientBuilder);
        }

        // Trigger a full Garbage Collection run to get the real blocked memory size later.
        System.gc();

        // Request the current heap size to calculate the utilization
        HeapStatisticJson heapStatistic =
                new HeapStatisticJson(runtime.totalMemory() - runtime.freeMemory(), runtime.totalMemory(), runtime.maxMemory());

        // Send everything back to the client
        return Response.ok().entity(heapStatistic).build();
    }
}
