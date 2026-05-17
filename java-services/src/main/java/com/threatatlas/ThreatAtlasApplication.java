package com.threatatlas.controller;

import com.threatatlas.grpc.SearchResponse;
import com.threatatlas.service.VectorEngineClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * The API Gateway for ThreatAtlas.
 * Your frontend (React/Vue/Postman) talks to this.
 * This talks to C++ (via gRPC) and Python (via sub-processes or APIs).
 */
@RestController
@RequestMapping("/api/v1/threats")
@CrossOrigin(origins = "*") // Open for local dev testing
public class ThreatController {

    private static final Logger log = LoggerFactory.getLogger(ThreatController.class);

    private final VectorEngineClient vectorEngineClient;

    @Autowired
    public ThreatController(VectorEngineClient vectorEngineClient) {
        this.vectorEngineClient = vectorEngineClient;
    }

    /**
     * HEALTH CHECK: Just to make sure the Spring Boot server is alive.
     */
    @GetMapping("/ping")
    public ResponseEntity<String> ping() {
        return ResponseEntity.ok("ThreatAtlas API is online and waiting for instructions.");
    }

    /**
     * ENDPOINT: Perform a high-speed vector search for similar threats.
     * * Expects JSON body:
     * {
     * "query": "SSH brute force attempts from 192.168.1.100",
     * "topK": 10
     * }
     */
    @PostMapping("/search")
    public ResponseEntity<?> searchThreats(@RequestBody Map<String, Object> payload) {
        try {
            String query = (String) payload.getOrDefault("query", "");
            int topK = (int) payload.getOrDefault("topK", 5);

            if (query.isEmpty()) {
                return ResponseEntity.badRequest().body("Error: 'query' cannot be empty.");
            }

            log.info("[API] Received search request: '{}' (top_k={})", query, topK);

            // Step 1: Turn the text query into a dense vector using the C++ ONNX engine
            // (We pass a dummy ID because this is a query, not a log ingestion)
            List<Float> embeddedQuery = vectorEngineClient.getEmbedding("web-query-" + System.currentTimeMillis(), query);

            // Step 2: Search the FAISS index inside the C++ engine
            SearchResponse searchResults = vectorEngineClient.searchSimilarThreats(embeddedQuery, topK);

            // TODO: In a full pipeline, we would now pass these 'searchResults' 
            // to our Python LLM script to generate a human-readable summary.
            // For now, we return the raw candidate IDs and distances to the web client.

            return ResponseEntity.ok(searchResults.getCandidatesList());

        } catch (Exception e) {
            log.error("[API] Internal Server Error during search.", e);
            return ResponseEntity.internalServerError().body("Vector Engine failure: " + e.getMessage());
        }
    }
}
