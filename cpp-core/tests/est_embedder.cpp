// ==============================================================================
// ThreatAtlas C++ Core: Embedder Unit Tests
// ==============================================================================

#include <gtest/gtest.h>
#include <vector>
#include <string>
#include <cmath>

// Assuming this is the header for your C++ ONNX embedder class
// #include "../src/embedder.h" 

// ------------------------------------------------------------------------------
// MOCK CLASS (Remove this and include your actual embedder.h in production)
// ------------------------------------------------------------------------------
// We mock this here so the test compiles immediately even if you haven't 
// fully fleshed out the onnxruntime C++ API wrapper yet.
class MockEmbedder {
public:
    MockEmbedder(const std::string& model_path) {
        if (model_path.empty()) {
            throw std::runtime_error("Model path cannot be empty");
        }
    }

    std::vector<float> generate_embedding(const std::string& text) {
        // all-MiniLM-L6-v2 always outputs exactly 384 dimensions
        std::vector<float> vec(384, 0.0f); 
        
        // Let's fake some math so it's not just zeros
        for(int i = 0; i < 384; i++) {
            vec[i] = std::sin(text.length() + i) * 0.5f; 
        }
        return vec;
    }
};

// ==============================================================================
// TEST SUITE
// ==============================================================================

class EmbedderTest : public ::testing::Test {
protected:
    // This runs before every single test
    void SetUp() override {
        // Hardcoded path to the model downloaded by our bash script
        model_path = "../../rag-llm/models/all-MiniLM-L6-v2.onnx";
    }

    std::string model_path;
};

// ------------------------------------------------------------------------------
// TEST 1: Dimensionality Check
// ------------------------------------------------------------------------------
TEST_F(EmbedderTest, OutputVectorHasCorrectDimensions) {
    MockEmbedder embedder(model_path);
    
    std::string test_log = "May 14 10:32:01 server1 sshd[1020]: Failed password for root";
    std::vector<float> result = embedder.generate_embedding(test_log);

    // If this fails, the vector DB will completely reject the payload
    EXPECT_EQ(result.size(), 384) << "Vector dimension mismatch! Expected 384.";
}

// ------------------------------------------------------------------------------
// TEST 2: Determinism Check
// ------------------------------------------------------------------------------
TEST_F(EmbedderTest, SameInputProducesSameEmbedding) {
    MockEmbedder embedder(model_path);
    
    std::string test_log = "Suspicious activity detected on port 443";
    
    std::vector<float> result1 = embedder.generate_embedding(test_log);
    std::vector<float> result2 = embedder.generate_embedding(test_log);

    ASSERT_EQ(result1.size(), result2.size());

    // Check that every single float is exactly identical
    for (size_t i = 0; i < result1.size(); ++i) {
        EXPECT_FLOAT_EQ(result1[i], result2[i]) << "Vectors diverged at index " << i;
    }
}

// ------------------------------------------------------------------------------
// TEST 3: Edge Cases
// ------------------------------------------------------------------------------
TEST_F(EmbedderTest, HandlesEmptyStringsGracefully) {
    MockEmbedder embedder(model_path);
    
    // Passing an empty string shouldn't crash the ONNX runtime
    std::vector<float> result = embedder.generate_embedding("");

    EXPECT_EQ(result.size(), 384);
}

// ------------------------------------------------------------------------------
// MAIN
// ------------------------------------------------------------------------------
int main(int argc, char **argv) {
    ::testing::InitGoogleTest(&argc, argv);
    std::cout << "[*] Booting C++ Embedder Test Suite..." << std::endl;
    return RUN_ALL_TESTS();
}
