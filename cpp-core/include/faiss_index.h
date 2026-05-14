// cpp-core/include/faiss_index.h
#pragma once

#include <vector>
#include <memory>
#include <cstdint>
// Requires FAISS library
#include <faiss/IndexHNSW.h>

namespace threatatlas {

struct SearchResult {
    int64_t id;
    float distance;
};

class FaissIndexManager {
public:
    /**
     * @brief Initializes a local HNSW FAISS index.
     * @param dimension The vector dimension (e.g., 384 for MiniLM).
     */
    explicit FaissIndexManager(int dimension);

    /**
     * @brief Adds a vector to the in-memory FAISS index.
     * @param vec The embedding vector.
     * @param id A unique identifier for the chunk.
     */
    void add_vector(const std::vector<float>& vec, int64_t id);

    /**
     * @brief Performs an Approximate Nearest Neighbor (ANN) search.
     * @param query_vec The embedded query.
     * @param top_k The number of results to return.
     * @return A vector of SearchResult structs containing IDs and L2 distances.
     */
    std::vector<SearchResult> search(const std::vector<float>& query_vec, int top_k);

private:
    int dimension_;
    std::unique_ptr<faiss::IndexHNSWFlat> index_;
};

} // namespace threatatlas
