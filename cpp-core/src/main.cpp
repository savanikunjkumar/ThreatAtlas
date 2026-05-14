// cpp-core/src/main.cpp
#include <iostream>
#include <memory>
#include "embedder.h"
#include "faiss_index.h"
#include "reranker.h"
#include "grpc_server.h"

using namespace threatatlas;

int main(int argc, char** argv) {
    std::cout << "Starting ThreatAtlas Core Compute Engine..." << std::endl;

    try {
        // 1. Initialize the Embedder (Point to your downloaded ONNX model)
        // Ensure you have a models/ directory mapped correctly later.
        auto embedder = std::make_shared<Embedder>("models/all-MiniLM-L6-v2.onnx");

        // 2. Initialize FAISS Index (MiniLM uses 384 dimensions)
        auto faiss_index = std::make_shared<FaissIndexManager>(384);

        // 3. Initialize Cross-Encoder Reranker
        auto reranker = std::make_shared<Reranker>("models/ms-marco-MiniLM-L-6-v2-onnx");

        // 4. Start the gRPC Server to listen for Java requests
        std::string server_address("0.0.0.0:50051");
        run_grpc_server(server_address, embedder, faiss_index);

    } catch (const std::exception& e) {
        std::cerr << "Fatal Error in Core Engine: " << e.what() << std::endl;
        return 1;
    }

    return 0;
}
