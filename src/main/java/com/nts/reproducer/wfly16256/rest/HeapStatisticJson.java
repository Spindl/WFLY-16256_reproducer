package com.nts.reproducer.wfly16256.rest;

public class HeapStatisticJson {

    private long usedHeap;
    private long committedHeap;
    private long maxHeap;
    private double heapUtilizationPercent;

    public HeapStatisticJson() {
    }

    public HeapStatisticJson(long usedHeap, long committedHeap, long maxHeap) {
        this.usedHeap = usedHeap;
        this.committedHeap = committedHeap;
        this.maxHeap = maxHeap;
        this.heapUtilizationPercent = (1.0 * this.usedHeap) / this.maxHeap;
    }

    public long getUsedHeap() {
        return usedHeap;
    }

    public void setUsedHeap(long usedHeap) {
        this.usedHeap = usedHeap;
    }

    public long getCommittedHeap() {
        return committedHeap;
    }

    public void setCommittedHeap(long committedHeap) {
        this.committedHeap = committedHeap;
    }

    public long getMaxHeap() {
        return maxHeap;
    }

    public void setMaxHeap(long maxHeap) {
        this.maxHeap = maxHeap;
    }

    public double getHeapUtilizationPercent() {
        return heapUtilizationPercent;
    }

    public void setHeapUtilizationPercent(double heapUtilizationPercent) {
        this.heapUtilizationPercent = heapUtilizationPercent;
    }
}
