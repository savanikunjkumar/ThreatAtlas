package com.threatatlas;

import com.threatatlas.grpc.LogChunk;
import com.threatatlas.grpc.VectorComputeServiceGrpc;
import com.threatatlas.grpc.VectorResponse;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * The ThreatAtlas Load Tester.
 * We use this to blast the C++ gRPC server with concurrent requests
 * to see at what point it catches fire.
 */
@Component
public class EvalHarness implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(EvalHarness.class);

    // This annotation tells the Spring Boot gRPC starter to inject the stub.
    // Make sure your application.yml has grpc.client.cpp-engine.address configured!
    @GrpcClient("cpp-engine")
    private VectorComputeServiceGrpc.VectorComputeServiceBlockingStub cppBackend;

    // TODO: Pull these from a config property so we don't have to recompile to change the load.
    private static final int THREADS = 10;
    private static final int TOTAL_REQUESTS = 5000;

    @Override
    public void run(String... args) {
        // Quick hack: Only run the benchmark if we pass a specific flag, 
        // otherwise it will stress test the C++ engine every time Spring boots up.
        boolean shouldRun = false;
        for (String arg : args) {
            if (arg.equals("--run-eval")) shouldRun = true;
        }
        
        if (!shouldRun) {
            log.info("[EvalHarness] Sleeping. Pass '--run-eval' to trigger the load test.");
            return;
        }

        log.info("=====================================================");
        log.info("🚀 INITIATING THREATATLAS E2E BENCHMARK");
        log.info("=====================================================");

        try {
            warmupJvm();
            stressTestCppEngine();
        } catch (Exception e) {
            log.error("[!] Harness crashed. Is the C++ server actually running?", e);
        }
    }

    private void warmupJvm() {
        log.info("[*] Warming up JVM and gRPC channels (100 dummy requests)...");
        for (int i = 0; i < 100; i++) {
            LogChunk chunk = LogChunk.newBuilder()
                    .setSourceId("warmup-" + i)
                    .setRawText("This is just a dummy log to wake up the JIT compiler.")
                    .build();
            // We don't care about the result, just forcing the network I/O
            cppBackend.generateEmbedding(chunk);
        }
        log.info("[+] Warmup complete. The JIT compiler is now fully awake.");
    }

    private void stressTestCppEngine() throws InterruptedException {
        log.info("[*] Firing {} requests across {} concurrent threads...", TOTAL_REQUESTS, THREADS);

        ExecutorService executor = Executors.newFixedThreadPool(THREADS);
        CountDownLatch latch = new CountDownLatch(TOTAL_REQUESTS);
        
        // Thread-safe collections to gather our stats
        List<Long> latencies = Collections.synchronizedList(new ArrayList<>(TOTAL_REQUESTS));
        AtomicInteger failures = new AtomicInteger(0);

        long testStartTime = System.currentTimeMillis();

        for (int i = 0; i < TOTAL_REQUESTS; i++) {
            final int reqId = i;
            executor.submit(() -> {
                long start = System.nanoTime();
                try {
                    LogChunk chunk =
