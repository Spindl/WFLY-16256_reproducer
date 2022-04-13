package main;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class Main {

    private static final int INITIAL_BATCH_SIZE = 10000;
    private static final int TARGET_HEAP_UTILIZATION = 80;
    private static final int STEPS_TO_TARGET = 5;

    public static void main(String[] args) throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();

        double initiallyUsedHeap = triggerLeak(client, 1);

        System.out.printf("Initially, %.2f%% of heap were used%n", initiallyUsedHeap);

        double heapUtilizationAfterFirstBatch = step(client, INITIAL_BATCH_SIZE);
        int initialBatchHeapUsage = (int) Math.ceil(heapUtilizationAfterFirstBatch - initiallyUsedHeap);
        System.out.printf("%d instances ~= %d%% heap%n", INITIAL_BATCH_SIZE, initialBatchHeapUsage);

        int stepBatchSize =
                (int) (((INITIAL_BATCH_SIZE / initialBatchHeapUsage) * (TARGET_HEAP_UTILIZATION - heapUtilizationAfterFirstBatch)) / STEPS_TO_TARGET);
        System.out.printf("Sending %d more batches with size %d to reach target utilization %d%%%n", STEPS_TO_TARGET, stepBatchSize, TARGET_HEAP_UTILIZATION);

        for (int i = 0; i < STEPS_TO_TARGET; i++) {
            step(client, stepBatchSize);
        }
    }

    private static double step(HttpClient client, int batchSize) throws IOException, InterruptedException {
        System.out.printf("Triggering memory leak for %d instances...", batchSize);
        double currentlyUsedHeap = triggerLeak(client, batchSize);
        System.out.printf("now %.2f%% of heap are blocked.%n", currentlyUsedHeap);
        return currentlyUsedHeap;
    }

    private static double triggerLeak(HttpClient client, int times) throws IOException, InterruptedException {
        HttpRequest request =
                HttpRequest.newBuilder().uri(URI.create("http://localhost:8080/rest/reproducer/configureClients?times=" + times)).GET().build();

        return Double.parseDouble(client.send(request, HttpResponse.BodyHandlers.ofString()).body());
    }
}
