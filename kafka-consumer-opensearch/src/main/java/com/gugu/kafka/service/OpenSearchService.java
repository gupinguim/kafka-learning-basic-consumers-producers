package com.gugu.kafka.service;

import java.io.IOException;
import java.net.URI;

import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.DefaultConnectionKeepAliveStrategy;
import org.opensearch.action.bulk.BulkRequest;
import org.opensearch.action.bulk.BulkResponse;
import org.opensearch.action.index.IndexRequest;
import org.opensearch.client.RequestOptions;
import org.opensearch.client.RestClient;
import org.opensearch.client.RestHighLevelClient;
import org.opensearch.client.indices.CreateIndexRequest;
import org.opensearch.client.indices.GetIndexRequest;
import org.opensearch.common.xcontent.XContentType;
import org.springframework.stereotype.Service;

import lombok.extern.java.Log;

@Service
@Log
public class OpenSearchService {

    private RestHighLevelClient openSearchClient;
    private BulkRequest bulkRequest = new BulkRequest();

    public OpenSearchService() {
        // we build a URI from the connection string
        RestHighLevelClient restHighLevelClient;
        URI connUri = URI.create("http://localhost:9200");
        log.info("Connecting to OpenSearch at " + connUri.getHost() + ":" + connUri.getPort());
        // extract login information if it exists
        String userInfo = connUri.getUserInfo();

        if (userInfo == null) {
            // REST client without security
            restHighLevelClient = new RestHighLevelClient(
                    RestClient.builder(new HttpHost(connUri.getHost(), connUri.getPort(), "http")));

        } else {
            // REST client with security
            String[] auth = userInfo.split(":");

            CredentialsProvider cp = new BasicCredentialsProvider();
            cp.setCredentials(AuthScope.ANY, new UsernamePasswordCredentials(auth[0], auth[1]));

            restHighLevelClient = new RestHighLevelClient(
                    RestClient.builder(new HttpHost(connUri.getHost(), connUri.getPort(), connUri.getScheme()))
                            .setHttpClientConfigCallback(
                                    httpAsyncClientBuilder -> httpAsyncClientBuilder.setDefaultCredentialsProvider(cp)
                                            .setKeepAliveStrategy(new DefaultConnectionKeepAliveStrategy())));

        }

        openSearchClient = restHighLevelClient;

        boolean indexExists;
        try {
            indexExists = openSearchClient.indices().exists(new GetIndexRequest("wikimedia"),
                    RequestOptions.DEFAULT);

            if (!indexExists) {
                CreateIndexRequest createIndexRequest = new CreateIndexRequest("wikimedia");
                openSearchClient.indices().create(createIndexRequest, RequestOptions.DEFAULT);
                indexExists = true;
            } else {
                log.info("The Wikimedia Index already exits");
            }
        } catch (IOException e) {
            log.severe("Error checking if the Wikimedia Index exists: " + e.getMessage());
            indexExists = false;
        }

        if (indexExists)
            log.info("The Wikimedia Index has been created or was already created!");
        else
            log.severe("There was an error creating the Wikimedia Index!");
    }

    public boolean sendMessage(String message, String id) throws Exception {
        try {
            log.info("Sending message to OpenSearch: " + id);
            IndexRequest indexRequest = new IndexRequest("wikimedia")
                    .source(message, XContentType.JSON)
                    .id(id);
            bulkRequest.add(indexRequest);

            if (bulkRequest.numberOfActions() > 0) {
                log.info("Sending bulk request to OpenSearch.");
                BulkResponse bulkResponse = openSearchClient.bulk(bulkRequest, RequestOptions.DEFAULT);
                log.info("Inserted " + bulkResponse.getItems().length + " record(s).");

                return true;
            }
            log.info("No records to insert yet, bulk is accumulating.");
            return false;
        } catch (Exception e) {
            log.severe("Error sending message to OpenSearch: " + e.getMessage());
            throw e;
        }

    }

    public void dispose() {
        try {
            log.info("Closing OpenSearch connection.");
            openSearchClient.close();
        } catch (IOException e) {
            log.severe("Error closing OpenSearch connection: " + e.getMessage());
        } 
        finally {
            log.info("Ended OpenSearch connection.");
        }
    }
}
