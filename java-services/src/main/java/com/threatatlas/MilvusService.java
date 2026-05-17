package com.threatatlas.service;

import io.milvus.client.MilvusServiceClient;
import io.milvus.param.ConnectParam;
import io.milvus.param.MetricType;
import io.milvus.param.R;
import io.milvus.param.dml.SearchParam;
import io.milvus.response.SearchResultsWrapper;
import io.milvus.grpc.SearchResults;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Enterprise Vector DB Connection.
 * Used when we need to scale beyond the local C++ FAISS index and hit
 * the standalone Milvus cluster defined in our docker-compose.yml.
 */
@Service
public class MilvusService {

    private static final Logger log = LoggerFactory.getLogger(MilvusService.class);

    // Pull these from application.yml (or default to localhost)
    @Value("${milvus.host:localhost}")
    private String host;

    @Value("${milvus.port:19530}")
    private int port;

    @Value("${milvus.collection:threat_index}")
    private String collectionName;

    private MilvusServiceClient milvusClient;

    /**
     * Boots up the connection to the Milvus cluster when Spring Boot starts.
     */
    @PostConstruct
    public void init() {
        log.info("[*] Connecting to Milvus Database at {}:{}...", host, port);
        try {
            ConnectParam connectParam = ConnectParam.newBuilder()
                    .withHost(host)
                    .withPort(port)
                    .build();
            this.milvusClient = new MilvusServiceClient(connectParam);
            log.info("[+] Milvus connection established.");
        } catch (Exception e) {
            log.error("[!] FATAL: Failed to connect to Milvus. Is your Docker container running?", e);
            // We don't throw an exception here because we might want the app to still boot 
            // and rely solely on the C++ FAISS engine instead.
        }
    }

    /**
     * Executes a vector search directly against Milvus and extracts the raw log snippets.
     */
    public List<String> searchLogs(List<Float> queryVector, int topK) {
        if (milvusClient == null) {
            log.warn("[!] Milvus client is disconnected. Falling back to empty results.");
            return Collections.emptyList();
        }

        log.debug("Executing Milvus search for top {} matches...", topK);

        // Milvus API requires passing vectors as a List of Lists (for batching).
        // Since we are only doing one query at a time, we wrap it.
        List<List<Float>> targetVectors = Collections.singletonList(queryVector);

        // Build the massive search parameter object
        SearchParam searchParam = SearchParam.newBuilder()
                .withCollectionName(collectionName)
                .withMetricType(MetricType.L2) // Must match the metric type you used in Python
                .withTopK(topK)
                .withVectors(targetVectors)
                .withVectorFieldName("embedding")
                // We explicitly ask Milvus to return the "raw_text" field so we can feed it to the LLM
                .addOutField("raw_text") 
                // nprobe tells Milvus how many clusters to search. Higher = more accurate but slower.
                .withParams("{\"nprobe\":10}") 
                .build();

        try {
            R<SearchResults> response = milvusClient.search(searchParam);

            if (response.getStatus() != R.Status.Success.getCode()) {
                log.error("[!] Milvus search failed: {}", response.getMessage());
                throw new RuntimeException("Milvus search failed: " + response.getMessage());
            }

            return extractTextResults(response.getData());

        } catch (Exception e) {
            log.error("[!] Exception during Milvus search: ", e);
            throw new RuntimeException("Database offline or search failed.", e);
        }
    }

    /**
     * Parses the ugly gRPC wrapper object returned by Milvus into a clean List of Java Strings.
     */
    private List<String> extractTextResults(SearchResults searchResults) {
        SearchResultsWrapper wrapper = new SearchResultsWrapper(searchResults.getResults());
        List<String> extractedLogs = new ArrayList<>();

        // If the database was empty or no close matches were found
        if (wrapper.getRowRecords().isEmpty()) {
            return extractedLogs;
        }

        // Loop through the results. 
        // Note: The '0' index is because we only sent one query vector in our batch.
        for (int i = 0; i < wrapper.getRowRecords().size(); i++) {
            // Get the field we explicitly requested earlier
            String rawText = (String) wrapper.getFieldData("raw_text", 0).get(i);
            extractedLogs.add(rawText);
        }

        return extractedLogs;
    }

    /**
     * Always clean up your network sockets.
     */
    @PreDestroy
    public void cleanup() {
        if (milvusClient != null) {
            log.info("[*] Closing Milvus Database connection...");
            milvusClient.close();
        }
    }
}
