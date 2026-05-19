
#include <gtest/gtest.h>
#include <vector>
#include <string>
#include <algorithm>

// Assuming this is the header for your C++ Reranker wrapper
// #include "../src/reranker.h" 

// ------------------------------------------------------------------------------
// DATA STRUCTURES (Usually defined in your reranker.h)
// ------------------------------------------------------------------------------
struct Document {
    std::string id;
    std::string text;
};

struct ScoredDocument {
    std::string id;
    std::string text;
    float relevance_score;
};

// ------------------------------------------------------------------------------
// MOCK CLASS
// ------------------------------------------------------------------------------
// Simulates the ONNX cross-encoder model for ultra-fast CI/CD testing.
class MockReranker {
public:
    MockReranker(const std::string& model_path) {
        if (model_path.empty()) {
            throw std::invalid_argument("Model path cannot be empty");
        }
    }

    std::vector<ScoredDocument> rerank(const std::string& query, const std::vector<Document>& docs) {
        if (docs.empty()) return {};

        std::vector<ScoredDocument> results;
        
        // Fake the AI scoring: if the document contains the query word, give it a high score
        for (const auto& doc : docs) {
            float score = 0.1f; // Default low score
            
            // Extremely basic substring check to simulate semantic match
            if (doc.text.find("root") != std::string::npos && query.find("root") != std::string::npos) {
                score = 0.95f; 
            } else if (doc.text.find("ssh") != std::string::npos) {
                score = 0.60f;
            }

            results.push_back({doc.id, doc.text, score});
        }

        // The reranker MUST sort descending (highest score first)
        std::sort(results.begin(), results.end(), 
            [](const ScoredDocument& a, const ScoredDocument& b) {
                return a.relevance_score > b.relevance_score;
            });

        return results;
    }
};

// ==============================================================================
// TEST SUITE
// ==============================================================================

class RerankerTest : public ::testing::Test {
protected:
    void SetUp() override {
        model_path = "../../rag-llm/models/ms-marco-MiniLM-L-6-v2-onnx";
    }

    std::string model_path;
};

// ------------------------------------------------------------------------------
// TEST 1: Sorting Enforcement
// ------------------------------------------------------------------------------
TEST_F(RerankerTest, SortsDocumentsDescendingByScore) {
    MockReranker reranker(model_path);
    
    std::string query = "Did the user get root access?";
    std::vector<Document> input_docs = {
        {"doc-1", "Connection closed by 192.168.1.5"},                 // Expected Score: 0.1
        {"doc-2", "User admin attempted ssh login"},                    // Expected Score: 0.6
        {"doc-3", "sudo: admin : USER=root ; COMMAND=/bin/bash"}        // Expected Score: 0.95
    };

    auto results = reranker.rerank(query, input_docs);

    ASSERT_EQ(results.size(), 3);
    
    // The exact answer should have bubbled to the very top
    EXPECT_EQ(results[0].id, "doc-3");
    EXPECT_FLOAT_EQ(results[0].relevance_score, 0.95f);
    
    // The completely irrelevant log should be dead last
    EXPECT_EQ(results[2].id, "doc-1");
    EXPECT_FLOAT_EQ(results[2].relevance_score, 0.1f);
}

// ------------------------------------------------------------------------------
// TEST 2: Conservation of Documents
// ------------------------------------------------------------------------------
TEST_F(RerankerTest, ReturnsExactSameNumberOfDocuments) {
    MockReranker reranker(model_path);
    
    std::vector<Document> input_docs = {
        {"doc-1", "Log A"}, {"doc-2", "Log B"}, {"doc-3", "Log C"}, {"doc-4", "Log D"}
    };

    auto results = reranker.rerank("dummy query", input_docs);

    // The reranker's job is not to filter out documents, just to score and sort them.
    // Filtering is the Vector DB's job.
    EXPECT_EQ(results.size(), 4) << "Reranker dropped or hallucinated documents!";
}

// ------------------------------------------------------------------------------
// TEST 3: Edge Cases
// ------------------------------------------------------------------------------
TEST_F(RerankerTest, HandlesEmptyDocumentListsGracefully) {
    MockReranker reranker(model_path);
    
    std::vector<Document> empty_docs = {};
    
    // This shouldn't throw an out-of-bounds exception or segfault
    auto results = reranker.rerank("Where is the threat?", empty_docs);

    EXPECT_TRUE(results.empty());
}
