

#include <gtest/gtest.h>
#include <vector>
#include <string>
#include <cmath>
#include <algorithm>

// Assuming this is the header for your C++ FAISS wrapper class
// #include "../src/faiss_index.h" 

// ------------------------------------------------------------------------------
// MOCK CLASS (Replace with your actual faiss_index.h in production)
// ------------------------------------------------------------------------------
// We mock this so the CI pipeline can compile the tests even if FAISS 
// hasn't been fully linked yet.
struct SearchResult {
    std::string id;
    float distance;
};

class MockFaissIndex {
private:
    int dimension;
    std::vector<std::string> ids;
    std::vector<std::vector<float>> vectors;

    // Helper to calculate L2 (Euclidean) distance
    float calculate_l2(const std::vector<float>& a, const std::vector<float>& b) {
        float dist = 0.0f;
        for (size_t i = 0; i < a.size(); ++i) {
            dist += (a[i] - b[i]) * (a[i] - b[i]);
        }
        return dist;
    }

public:
    MockFaissIndex(int dim) : dimension(dim) {}

    void add_vector(const std::string& id, const std::vector<float>& vec) {
        if (vec.size() != dimension) {
            throw std::invalid_argument("Vector dimension mismatch.");
        }
        ids.push_back(id);
        vectors.push_back(vec);
    }

    std::vector<SearchResult> search(const std::vector<float>& query, int top_k) {
        if (query.size() != dimension) throw std::invalid_argument("Query dimension mismatch.");
        
        std::vector<SearchResult> results;
        for (size_t i = 0; i < vectors.size(); ++i) {
            results.push_back({ids[i], calculate_l2(query, vectors[i])});
        }

        // Sort ascending by distance (closer is better for L2)
        std::sort(results.begin(), results.end(), 
            [](const SearchResult& a, const SearchResult& b) {
                return a.distance < b.distance;
            });

        // Truncate to top_k
        if (results.size() > static_cast<size_t>(top_k)) {
            results.resize(top_k);
        }
        return results;
    }
    
    int size() const { return ids.size(); }
};

// ==============================================================================
// TEST SUITE
// ==============================================================================

class FaissIndexTest : public ::testing::Test {
protected:
    void SetUp() override {
        // all-MiniLM-L6-v2 outputs 384 dimensions
        dim = 384; 
    }

    int dim;
};

// ------------------------------------------------------------------------------
// TEST 1: Dimension Enforcement
// ------------------------------------------------------------------------------
TEST_F(FaissIndexTest, RejectsInvalidDimensions) {
    MockFaissIndex index(dim);
    
    // Create a garbage vector of size 10 instead of 384
    std::vector<float> bad_vector(10, 0.5f);

    // Ensure the database throws an error instead of corrupting memory
    EXPECT_THROW(index.add_vector("bad-log-1", bad_vector), std::invalid_argument);
    EXPECT_THROW(index.search(bad_vector, 5), std::invalid_argument);
}

// ------------------------------------------------------------------------------
// TEST 2: Exact Match Retrieval (The Math Test)
// ------------------------------------------------------------------------------
TEST_F(FaissIndexTest, FindsExactMatchWithZeroDistance) {
    MockFaissIndex index(dim);
    
    std::vector<float> target_vector(dim,
