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

    public Metrics(String poolId, boolean isDisposed) {
        this.poolId = poolId;
        this.isDisposed = isDisposed;
    }

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

    public static class Builder {
        private String poolId;
        private int acquiredSize;
        private int allocatedSize;
        private int idleSize;
        private int pendingAcquireSize;
        private int maxAllocatedSize;
        private int maxPendingAcquireSize;
        private boolean isDisposed;

        public Builder(String poolId, boolean isDisposed) {
            this.poolId = poolId;
            this.isDisposed = isDisposed;
        }

        public Builder acquiredSize(int acquiredSize) {
            this.acquiredSize = acquiredSize;
            return this;
        }

        public Builder allocatedSize(int allocatedSize) {
            this.allocatedSize = allocatedSize;
            return this;
        }

        public Builder idleSize(int idleSize) {
            this.idleSize = idleSize;
            return this;
        }

        public Builder pendingAcquireSize(int pendingAcquireSize) {
            this.pendingAcquireSize = pendingAcquireSize;
            return this;
        }

        public Builder maxAllocatedSize(int maxAllocatedSize) {
            this.maxAllocatedSize = maxAllocatedSize;
            return this;
        }

        public Builder maxPendingAcquireSize(int maxPendingAcquireSize) {
            this.maxPendingAcquireSize = maxPendingAcquireSize;
            return this;
        }

        public Metrics build() {
            Metrics metrics = new Metrics(this.poolId, this.isDisposed);
            metrics.setAcquiredSize(this.acquiredSize);
            metrics.setAllocatedSize(this.allocatedSize);
            metrics.setIdleSize(this.idleSize);
            metrics.setPendingAcquireSize(this.pendingAcquireSize);
            metrics.setMaxAllocatedSize(this.maxAllocatedSize);
            metrics.setMaxPendingAcquireSize(this.maxPendingAcquireSize);
            return metrics;
        }

    }
}
