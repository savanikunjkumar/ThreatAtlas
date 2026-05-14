// cpp-core/include/embedder.h
#pragma once

#include <string>
#include <vector>
#include <memory>
// Requires ONNX Runtime C++ API
#include <onnxruntime_cxx_api.h>

namespace threatatlas {

class Embedder {
public:
    /**
     * @brief Initializes the ONNX runtime session with a local model.
     * @param model_path Path to the .onnx model file (e.g., all-MiniLM-L6-v2)
     */
    explicit Embedder(const std::string& model_path);

    /**
     * @brief Converts raw text into a dense vector embedding.
     * @param text The log chunk or document to embed.
     * @return A flat vector of floats representing the embedding.
     */
    std::vector<float> generate_embedding(const std::string& text);

private:
    Ort::Env env_;
    Ort::SessionOptions session_options_;
    std::unique_ptr<Ort::Session> session_;
};

} // namespace threatatlas
