// cpp-core/include/grpc_server.h
#pragma once

#include <string>
#include <memory>
#include "embedder.h"
#include "faiss_index.h"

namespace threatatlas {

/**
 * @brief Starts the gRPC server to listen for Java orchestration requests.
 * @param server_address The IP and port to bind to (e.g., "0.0.0.0:50051").
 * @param emb Shared pointer to the initialized Embedder instance.
 * @param idx Shared pointer to the initialized FaissIndexManager instance.
 */
void run_grpc_server(const std::string& server_address, 
                     std::shared_ptr<Embedder> emb, 
                     std::shared_ptr<FaissIndexManager> idx);

} // namespace threatatlas
