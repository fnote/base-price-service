package com.sysco.rps.dto;

/**
 * @author Tharuka Jayalath
 * (C) 2020, Sysco Corporation
 * Created: 10/21/20. Wed 2020 18:50
 */
public class Metrics {

    private String poolId;
    private int acquiredSize;
    private int allocatedSize;
    private int idleSize;
    private int pendingAcquireSize;
    private int maxAllocatedSize;
    private int maxPendingAcquireSize;
    private boolean isDisposed;

    public String getPoolId() {
        return poolId;
    }

    public void setPoolId(String poolId) {
        this.poolId = poolId;
    }

    public int getAcquiredSize() {
        return acquiredSize;
    }

    public void setAcquiredSize(int acquiredSize) {
        this.acquiredSize = acquiredSize;
    }

    public int getAllocatedSize() {
        return allocatedSize;
    }

    public void setAllocatedSize(int allocatedSize) {
        this.allocatedSize = allocatedSize;
    }

    public int getIdleSize() {
        return idleSize;
    }

    public void setIdleSize(int idleSize) {
        this.idleSize = idleSize;
    }

    public int getPendingAcquireSize() {
        return pendingAcquireSize;
    }

    public void setPendingAcquireSize(int pendingAcquireSize) {
        this.pendingAcquireSize = pendingAcquireSize;
    }

    public int getMaxAllocatedSize() {
        return maxAllocatedSize;
    }

    public void setMaxAllocatedSize(int maxAllocatedSize) {
        this.maxAllocatedSize = maxAllocatedSize;
    }

    public int getMaxPendingAcquireSize() {
        return maxPendingAcquireSize;
    }

    public void setMaxPendingAcquireSize(int maxPendingAcquireSize) {
        this.maxPendingAcquireSize = maxPendingAcquireSize;
    }

    public boolean isDisposed() {
        return isDisposed;
    }

    public void setDisposed(boolean disposed) {
        isDisposed = disposed;
    }
}
