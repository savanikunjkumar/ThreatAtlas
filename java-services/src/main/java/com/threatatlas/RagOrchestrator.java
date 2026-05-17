package com.threatatlas.service;

import com.threatatlas.grpc.SearchCandidate;
import com.threatatlas.grpc.SearchResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * The Central Nervous System of ThreatAtlas.
 * This service coordinates the multi-language dance between the Java API, 
 * the C++ Vector Engine, and the Python LLM.
 */
@Service
public class RagOrchestrator {

    private static final Logger log = LoggerFactory.getLogger(RagOrchestrator.class);

    private final VectorEngineClient vectorEngineClient;
    private final LlmClient llmClient;

    @Autowired
    public RagOrchestrator(VectorEngineClient vectorEngineClient, LlmClient llmClient) {
        this.vectorEngineClient = vectorEngineClient;
        this.llmClient = llmClient;
    }

    /**
     * Executes the full RAG pipeline: Embed -> Search -> Generate.
     */
    public String generateThreatReport(String query) {
        log.info("=====================================================");
        log.info("🔍 INITIATING RAG PIPELINE: '{}'", query);
        log.info("=====================================================");

        long startTime = System.currentTimeMillis();

        try {
            // STEP 1: Turn the user's english question into a mathematical vector
            log.debug("[1/3] Asking C++ Engine to embed query...");
            List<Float> queryVector = vectorEngineClient.getEmbedding("rag-query", query);

            // STEP 2: Search the high-speed index for the top 5 most relevant log chunks
            log.debug("[2/3] Executing ultra-fast FAISS search via C++...");
            // We hardcode topK=5 here, but you could pull this from a config file.
            SearchResponse searchResponse = vectorEngineClient.searchSimilarThreats(queryVector, 5);
            List<SearchCandidate> candidates = searchResponse.getCandidatesList();

            // STEP 3: The Short-Circuit (Cost Saver)
            // If the database has no idea what you're talking about, DO NOT WAKE UP THE LLM.
            // Inference is expensive and slow. Just fail fast.
            if (candidates.isEmpty()) {
                log.warn("[!] Vector DB returned 0 results. Short-circuiting LLM call.");
                return "Insufficient telemetry to determine. No matching logs found in the index.";
            }

            log.info("[+] Found {} relevant log candidates. Waking up the LLM...", candidates.size());

            // STEP 4: Hand the raw logs to the Python ML process to generate a human-readable report
            log.debug("[3/3] Generating final threat report...");
            String finalReport = llmClient.generateReport(query, candidates);

            long duration = System.currentTimeMillis() - startTime;
            log.info("[+] RAG Pipeline completed successfully in {} ms.", duration);

            return finalReport;

        } catch (Exception e) {
            log.error("[!] FATAL ERROR in RAG Pipeline: ", e);
            return "System Error: The ThreatAtlas cognitive engine is currently offline.";
        }
    }
}
