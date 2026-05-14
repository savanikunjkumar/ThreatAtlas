// cpp-core/src/faiss_index.cpp
#include "faiss_index.h"
#include <iostream>

namespace threatatlas {

FaissIndexManager::FaissIndexManager(int dimension) : dimension_(dimension) {
    // Initialize an HNSW (Hierarchical Navigable Small World) index with L2 distance
    // 32 is the number of connections per node (M).
    index_ = std::make_unique<faiss::IndexHNSWFlat>(dimension_, 32);
    
    // Optional: Train the index if required by the specific FAISS type, 
    // though HNSWFlat doesn't strictly require pre-training.
    index_->hnsw.efConstruction = 40; 
    std::cout << "[FAISS] Initialized HNSW index with dimension: " << dimension_ << std::endl;
}

void FaissIndexManager::add_vector(const std::vector<float>& vec, int64_t id) {
    if (vec.size() != dimension_) {
        throw std::invalid_argument("Vector dimension mismatch");
    }
    // FAISS expects arrays, so we pass the underlying pointer
    index_->add(1, vec.data());
    // Note: To map specific IDs, you'd wrap this in faiss::IndexIDMap
}

std::vector<SearchResult> FaissIndexManager::search(const std::vector<float>& query_vec, int top_k) {
    std::vector<float> distances(top_k);
    std::vector<faiss::idx_t> labels(top_k);

    // Run the search
    index_->search(1, query_vec.data(), top_k, distances.data(), labels.data());

    std::vector<SearchResult> results;
    for (int i = 0; i < top_k; ++i) {
        if (labels[i] != -1) { // -1 means no neighbor found
            results.push_back({labels[i], distances[i]});
        }
    }
    return results;
}

} // namespace threatatlas
