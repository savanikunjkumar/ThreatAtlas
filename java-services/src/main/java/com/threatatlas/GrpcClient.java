package com.threatatlas.service;

import com.threatatlas.grpc.LogChunk;
import com.threatatlas.grpc.SearchQuery;
import com.threatatlas.grpc.SearchResponse;
import com.threatatlas.grpc.VectorComputeServiceGrpc;
import com.threatatlas.grpc.VectorResponse;
import io.grpc.StatusRuntimeException;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

/**
 * The dedicated client for talking to our C++ Core Engine.
 * This abstracts away all the ugly Protobuf builders and gRPC network logic
 * so our REST controllers can just call clean Java methods.
 */
@Service
public class VectorEngineClient {

    private static final Logger log = LoggerFactory.getLogger(VectorEngineClient.class);

    // Binds to the configuration in application.yml (e.g., localhost:50051)
    @GrpcClient("cpp-engine")
    private VectorComputeServiceGrpc.VectorComputeServiceBlockingStub cppBackend;

    /**
     * Sends a raw log string to C++ to be converted into a dense vector via ONNX.
     */
    public List<Float> getEmbedding(String sourceId, String rawText) {
        log.debug("Requesting embedding for source: {}", sourceId);
        
        try {
            LogChunk request = LogChunk.newBuilder()
                    .setSourceId(sourceId)
                    .setTimestamp(Instant.now().toString())
                    .setRawText(rawText)
                    .build();

            // Fire the network call
            VectorResponse response = cppBackend.generateEmbedding(request);
            
            return response.getEmbeddingsList();
            
        } catch (StatusRuntimeException e) {
            log.error("[!] gRPC connection to C++ engine failed during embedding generation.", e);
            throw new RuntimeException("Vector Engine is down. Cannot generate embeddings.", e);
        }
    }

    /**
     * Sends a vector to C++ to perform an ultra-fast FAISS/HNSW search.
     */
    public SearchResponse searchSimilarThreats(List<Float> queryVector, int topK) {
        log.debug("Executing vector search for top {} candidates...", topK);
        
        try {
            SearchQuery request = SearchQuery.newBuilder()
                    .addAllQueryVector(queryVector)
                    .setTopK(topK)
                    .build();

            // Fire the network call
            return cppBackend.searchThreatIndex(request);
            
        } catch (StatusRuntimeException e) {
            log.error("[!] gRPC connection to C++ engine failed during vector search.", e);
            throw new RuntimeException("Vector Engine is down. Cannot perform threat search.", e);
        }
    }
}
