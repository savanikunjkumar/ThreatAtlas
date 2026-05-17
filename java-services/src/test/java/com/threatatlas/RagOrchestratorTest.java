package com.threatatlas;

import com.threatatlas.grpc.SearchCandidate;
import com.threatatlas.grpc.SearchResponse;
import com.threatatlas.service.LlmClient;
import com.threatatlas.service.VectorEngineClient;
import com.threatatlas.service.RagOrchestrator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for the main RAG Orchestrator.
 * We mock both the C++ gRPC client and the Python LLM HTTP client.
 * We only care about testing the Java routing logic here.
 */
@ExtendWith(MockitoExtension.class)
class RagOrchestratorTest {

    @Mock
    private VectorEngineClient vectorEngineClient;

    @Mock
    private LlmClient llmClient;

    // The class we are actually testing (you'll need to create this next!)
    @InjectMocks
    private RagOrchestrator ragOrchestrator;

    @Test
    @DisplayName("Should successfully route query to Vector DB and then to LLM")
    void testHappyPathRagFlow() {
        // 1. Setup Mock C++ Vector Engine Behavior
        String query = "Who initiated the SSH brute force?";
        
        when(vectorEngineClient.getEmbedding(anyString(), eq(query)))
                .thenReturn(Arrays.asList(0.1f, 0.5f, -0.2f)); // Mock vector
                
        SearchResponse mockSearchResponse = SearchResponse.newBuilder()
                .addCandidates(SearchCandidate.newBuilder()
                        .setSourceId("auth.log-1")
                        .setSnippet("Failed password for root from 10.0.0.5")
                        .build())
                .build();
                
        when(vectorEngineClient.searchSimilarThreats(anyList(), anyInt()))
                .thenReturn(mockSearchResponse);

        // 2. Setup Mock Python LLM Behavior
        String expectedReport = "The brute force was initiated by 10.0.0.5 targeting the root account.";
        when(llmClient.generateReport(eq(query), anyList()))
                .thenReturn(expectedReport);

        // 3. Execute the Orchestrator
        String actualReport = ragOrchestrator.generateThreatReport(query);

        // 4. Verify the exact sequence of events happened
        assertNotNull(actualReport);
        assertEquals(expectedReport, actualReport);
        
        // Ensure we didn't accidentally skip a step
        verify(vectorEngineClient, times(1)).getEmbedding(anyString(), eq(query));
        verify(vectorEngineClient, times(1)).searchSimilarThreats(anyList(), anyInt());
        verify(llmClient, times(1)).generateReport(eq(query), anyList());
        
        System.out.println("[+] Happy Path RAG Flow Test Passed.");
    }

    @Test
    @DisplayName("Should short-circuit and NOT call LLM if Vector DB finds nothing")
    void testShortCircuitOnEmptyVectorResults() {
        // 1. Setup Mock C++ Vector Engine Behavior (Returns Empty List)
        when(vectorEngineClient.getEmbedding(anyString(), anyString()))
                .thenReturn(Arrays.asList(0.1f));
                
        SearchResponse emptyResponse = SearchResponse.newBuilder().build();
        when(vectorEngineClient.searchSimilarThreats(anyList(), anyInt()))
                .thenReturn(emptyResponse);

        // 2. Execute Orchestrator
        String result = ragOrchestrator.generateThreatReport("Weird query that matches zero logs");

        // 3. Verify
        //
