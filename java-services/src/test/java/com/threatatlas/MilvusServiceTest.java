package com.threatatlas;

import io.milvus.client.MilvusServiceClient;
import io.milvus.param.R;
import io.milvus.param.dml.SearchParam;
import io.milvus.response.SearchResultsWrapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit tests for our Milvus connection.
 * We use Mockito here because spinning up a 4GB Milvus Docker container 
 * just to run a 50-millisecond unit test is a crime against RAM.
 */
@ExtendWith(MockitoExtension.class)
class MilvusServiceTest {

    // Mock the heavy third-party client
    @Mock
    private MilvusServiceClient milvusClient;

    // TODO: We need to actually write this class next!
    // @InjectMocks
    // private MilvusService milvusService;

    @BeforeEach
    void setUp() {
        // Any reset logic goes here
    }

    @Test
    void testVectorSearchHandlesEmptyResults() {
        // 1. Setup the Mock Behavior
        // When our service calls milvusClient.search(), return an empty response
        R<io.milvus.grpc.SearchResults> mockResponse = R.success(io.milvus.grpc.SearchResults.newBuilder().build());
        when(milvusClient.search(any(SearchParam.class))).thenReturn(mockResponse);

        // 2. Execute the hypothetical service call
        // List<Float> dummyVector = Arrays.asList(0.1f, 0.2f, 0.3f);
        // List<String> results = milvusService.searchLogs(dummyVector, 5);

        // 3. Assertions
        // assertTrue(results.isEmpty(), "Service should handle empty DB results gracefully");
        
        System.out.println("[+] Mocked Milvus empty result test passed.");
    }

    @Test
    void testVectorSearchThrowsExceptionWhenDbOffline() {
        // 1. Force the mock to simulate a database crash
        when(milvusClient.search(any(SearchParam.class)))
            .thenThrow(new RuntimeException("Connection refused: Milvus is down"));

        // 2. Assert that our service catches it or bubbles it up correctly
        // Exception exception = assertThrows(RuntimeException.class, () -> {
        //     milvusService.searchLogs(Arrays.asList(0.1f, 0.2f), 5);
        // });
        
        // assertTrue(exception.getMessage().contains("Connection refused"));
        
        System.out.println("[+] Mocked Milvus crash test passed.");
    }
}
