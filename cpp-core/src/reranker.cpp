// cpp-core/src/reranker.cpp
#include "reranker.h"
#include <iostream>

namespace threatatlas {

Reranker::Reranker(const std::string& model_path)
    : env_(ORT_LOGGING_LEVEL_WARNING, "ThreatAtlas-Reranker"),
      session_options_() {
    
    session_options_.SetIntraOpNumThreads(2);
    session_ = std::make_unique<Ort::Session>(env_, model_path.c_str(), session_options_);
    std::cout << "[Reranker] Cross-Encoder loaded: " << model_path << std::endl;
}

float Reranker::compute_score(const std::string& query, const std::string& document) {
    // 1. Tokenization: A cross encoder requires the query and document to be concatenated
    // e.g., [CLS] Query [SEP] Document [SEP]
    
    // MOCK INFERENCE:
    // In reality, run the ONNX session with the combined tokenized inputs
    // and extract the raw logit output.
    
    float mock_relevance_score = 0.85f; // Dummy score
    
    // Sigmoid function to convert raw logit to 0-1 probability
    float probability = 1.0f / (1.0f + std::exp(-mock_relevance_score));
    
    return probability;
}

} // namespace threatatlas
