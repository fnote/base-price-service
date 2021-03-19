package com.sysco.rps.config;

/**
 * Bean for holding Cluster information.
 *
 * @author Tharuka Jayalath
 * (C) 2021, Sysco Corporation
 * Created: 3/18/21. Thu 14:00
 */
public class Cluster {

    private final String id;
    private final String readerEndpoint;
    private final String writerEndpoint;

    public Cluster(String id, String readerEndpoint, String writerEndpoint) {
        this.id = id;
        this.readerEndpoint = readerEndpoint;
        this.writerEndpoint = writerEndpoint;
    }

    public String getId() {
        return id;
    }

    public String getReaderEndpoint() {
        return readerEndpoint;
    }

    public String getWriterEndpoint() {
        return writerEndpoint;
    }

}
