// cpp-core/include/reranker.h
#pragma once

#include <string>
#include <memory>
#include <onnxruntime_cxx_api.h>

namespace threatatlas {

class Reranker {
public:
    /**
     * @brief Initializes the ONNX cross-encoder model.
     * @param model_path Path to the cross-encoder .onnx file.
     */
    explicit Reranker(const std::string& model_path);

    /**
     * @brief Computes a relevance score between a query and a retrieved document.
     * @param query The user's natural language query.
     * @param document The retrieved log chunk.
     * @return A probability score (0.0 to 1.0) indicating relevance.
     */
    float compute_score(const std::string& query, const std::string& document);

private:
    Ort::Env env_;
    Ort::SessionOptions session_options_;
    std::unique_ptr<Ort::Session> session_;
};

} // namespace threatatlas
