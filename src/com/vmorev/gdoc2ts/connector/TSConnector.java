package com.vmorev.gdoc2ts.connector;

import terrastore.client.BucketOperation;
import terrastore.client.TerrastoreClient;
import terrastore.client.connection.resteasy.HTTPConnectionFactory;

/**
 * User: vmorev
 * Date: 11/13/11 1:37 PM
 */
public class TSConnector {
    private TerrastoreClient client;

    public TSConnector(String host) {
        client = new TerrastoreClient(host, new HTTPConnectionFactory());
    }

    public BucketOperation getDocuments(String folderName) {
        return client.bucket(folderName);
    }
}
