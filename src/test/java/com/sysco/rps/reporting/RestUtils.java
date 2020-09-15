package com.sysco.rps.reporting;

import org.apache.http.HttpStatus;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.TrustAllStrategy;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;

/**
 * Utilities to call a REST API
 *
 * @author Sanjaya Amarasinghe
 * @copyright (C) 2020, Sysco Corporation
 * @doc
 * @end Created : 15. Sep 2020 11:25
 */
class RestUtils {
    private static final Logger LOGGER = LoggerFactory.getLogger(RestUtils.class);

    static String sendPOST(String url, String data) throws IOException, KeyStoreException, NoSuchAlgorithmException, KeyManagementException {

        String result;
        HttpPost post = new HttpPost(url);

        post.setEntity(new StringEntity(data));
        post.addHeader("Content-Type", "application/json");
        post.addHeader("Accept", "application/json");

        try (CloseableHttpClient httpClient = HttpClients
              .custom()
              .setSSLContext(new SSLContextBuilder().loadTrustMaterial(null, TrustAllStrategy.INSTANCE).build())
              .setSSLHostnameVerifier(NoopHostnameVerifier.INSTANCE)
              .build();
             CloseableHttpResponse response = httpClient.execute(post)) {

            result = EntityUtils.toString(response.getEntity());
            int statusCode = response.getStatusLine().getStatusCode();

            if (statusCode == HttpStatus.SC_OK) {
                LOGGER.info("Request successful");
            } else {
                LOGGER.info("Request failed. HTTP Status {}, Response {}", statusCode, response);
            }

        }

        return result;
    }

}
