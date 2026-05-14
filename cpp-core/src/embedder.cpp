// cpp-core/src/embedder.cpp
#include "embedder.h"
#include <iostream>
#include <stdexcept>

namespace threatatlas {

Embedder::Embedder(const std::string& model_path) 
    : env_(ORT_LOGGING_LEVEL_WARNING, "ThreatAtlas-Embedder"),
      session_options_() {
    
    // Optimize for performance
    session_options_.SetIntraOpNumThreads(4);
    session_options_.SetGraphOptimizationLevel(GraphOptimizationLevel::ORT_ENABLE_ALL);

    try {
        session_ = std::make_unique<Ort::Session>(env_, model_path.c_str(), session_options_);
        std::cout << "[Embedder] ONNX Model loaded successfully: " << model_path << std::endl;
    } catch (const Ort::Exception& e) {
        std::cerr << "[Embedder] Failed to load model: " << e.what() << std::endl;
        throw;
    }
}

std::vector<float> Embedder::generate_embedding(const std::string& text) {
    // 1. Tokenization (Placeholder - usually done via a C++ huggingface tokenizer library)
    // std::vector<int64_t> input_ids = tokenize(text);
    
    // MOCK DATA: For demonstration, returning a dummy 384-dimensional vector
    // In production, you allocate Ort::Value tensors here, run session_->Run(), 
    // and extract the output tensor (usually the mean-pooled hidden state).
    
    std::vector<float> embedding(384, 0.1f); // Assuming 384 dim model like MiniLM
    
    // Example of how the ONNX call looks:
    // auto output_tensors = session_->Run(Ort::RunOptions{nullptr}, input_node_names.data(),
    //                                     &input_tensor, 1, output_node_names.data(), 1);
    // float* floatarr = output_tensors.front().GetTensorMutableData<float>();
    
    return embedding;
}

} // namespace threatatlas
