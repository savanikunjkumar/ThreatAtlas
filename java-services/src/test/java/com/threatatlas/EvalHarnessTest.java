package com.threatatlas;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfSystemProperty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

/**
 * CI/CD Integration Test Suite.
 * We don't want this running every time someone hits "Build" in their IDE,
 * because if the C++ gRPC engine isn't running, the build will fail.
 */
@SpringBootTest
class EvalHarnessTest {

    @Autowired
    private EvalHarness evalHarness;

    @Test
    void contextLoads() {
        // The ultimate sanity check. 
        // If the Spring DI container can't even inject the gRPC stubs and boot, 
        // we have syntax errors or missing dependencies.
        System.out.println("[+] Spring Context booted successfully.");
    }

    @Test
    // This is the magic. This test is completely ignored UNLESS you run:
    // mvn test -Dtest=EvalHarnessTest -Drun.benchmark=true
    @EnabledIfSystemProperty(named = "run.benchmark", matches = "true")
    void executeFullLoadTest() {
        System.out.println("=====================================================");
        System.out.println("[*] CI/CD Pipeline triggered the Eval Harness...");
        System.out.println("=====================================================");

        // We wrap the runner in an assertion. 
        // If the C++ engine segfaults or drops connections under load, 
        // this throws an exception, fails the test, and blocks the GitHub PR.
        assertDoesNotThrow(() -> {
            // Pass the flag to wake up the runner
            evalHarness.run("--run-eval");
        }, "FATAL: The C++ Vector Engine crashed or timed out under load!");
    }
}
