# ==============================================================================
# ThreatAtlas C++ Core Engine Build Script
# ==============================================================================

cmake_minimum_required(VERSION 3.15)
project(ThreatAtlasCore VERSION 1.0.0 LANGUAGES CXX)

# We need C++17 for smart pointers, string views, and modern standard library features.
set(CMAKE_CXX_STANDARD 17)
set(CMAKE_CXX_STANDARD_REQUIRED ON)
set(CMAKE_CXX_EXTENSIONS OFF)

# Optimize for speed. This is a high-performance engine.
set(CMAKE_CXX_FLAGS_RELEASE "-O3 -march=native -DNDEBUG")

# ------------------------------------------------------------------------------
# 1. Dependencies (The "Please don't break" section)
# ------------------------------------------------------------------------------

# Find Protobuf and gRPC
# Note: Ensure you have installed grpc and protobuf via your package manager (brew, apt, vcpkg, etc.)
find_package(Threads REQUIRED)
find_package(Protobuf REQUIRED)
find_package(gRPC REQUIRED)

# Find FAISS
# Assuming FAISS is installed globally. If not, you might need to specify -Dfaiss_DIR
find_package(faiss REQUIRED)

# Find ONNX Runtime
# ONNX CMake integration is sometimes clunky. We manually find the path and library.
find_path(ONNXRUNTIME_INCLUDE_DIR onnxruntime_cxx_api.h PATH_SUFFIXES onnxruntime)
find_library(ONNXRUNTIME_LIBRARY NAMES onnxruntime)

if(NOT ONNXRUNTIME_INCLUDE_DIR OR NOT ONNXRUNTIME_LIBRARY)
    message(FATAL_ERROR "[!] ONNX Runtime not found. Please install it or set paths.")
endif()

# ------------------------------------------------------------------------------
# 2. Protobuf & gRPC Code Generation
# ------------------------------------------------------------------------------
# We don't write the networking boilerplate. We make Protoc write it for us.

set(PROTO_DIR "${CMAKE_CURRENT_SOURCE_DIR}/proto")
set(PROTO_FILE "${PROTO_DIR}/threatatlas.proto")
set(PROTO_OUT_DIR "${CMAKE_CURRENT_BINARY_DIR}/generated")
file(MAKE_DIRECTORY ${PROTO_OUT_DIR})

# Find the gRPC C++ plugin executable
find_program(gRPC_CPP_PLUGIN_EXECUTABLE grpc_cpp_plugin)

# Custom command to compile the .proto file into .pb.cc and .grpc.pb.cc files
add_custom_command(
    OUTPUT "${PROTO_OUT_DIR}/threatatlas.pb.cc"
           "${PROTO_OUT_DIR}/threatatlas.pb.h"
           "${PROTO_OUT_DIR}/threatatlas.grpc.pb.cc"
           "${PROTO_OUT_DIR}/threatatlas.grpc.pb.h"
    COMMAND ${Protobuf_PROTOC_EXECUTABLE}
    ARGS --grpc_out="${PROTO_OUT_DIR}"
         --cpp_out="${PROTO_OUT_DIR}"
         --plugin=protoc-gen-grpc="${gRPC_CPP_PLUGIN_EXECUTABLE}"
         -I "${PROTO_DIR}"
         "${PROTO_FILE}"
    DEPENDS "${PROTO_FILE}"
    COMMENT "Generating C++ gRPC bindings from threatatlas.proto..."
)

# ------------------------------------------------------------------------------
# 3. Build the Target
# ------------------------------------------------------------------------------

# Add our generated headers and our custom include directory to the path
include_directories(
    ${CMAKE_CURRENT_SOURCE_DIR}/include
    ${PROTO_OUT_DIR}
    ${ONNXRUNTIME_INCLUDE_DIR}
)

# Define the executable and hand it all the source files
add_executable(threat_engine_server
    src/main.cpp
    src/embedder.cpp
    src/faiss_index.cpp
    src/reranker.cpp
    src/grpc_server.cpp
    # Include the auto-generated gRPC files so they get compiled
    "${PROTO_OUT_DIR}/threatatlas.pb.cc"
    "${PROTO_OUT_DIR}/threatatlas.grpc.pb.cc"
)

# Link all the heavy libraries to our binary
target_link_libraries(threat_engine_server
    PRIVATE
    gRPC::grpc++
    gRPC::grpc
    protobuf::libprotobuf
    faiss::faiss
    ${ONNXRUNTIME_LIBRARY}
    Threads::Threads
)

# Optional: Copy the models folder to the build directory so main.cpp can find them easily
add_custom_command(TARGET threat_engine_server POST_BUILD
    COMMAND ${CMAKE_COMMAND} -E copy_directory
    "${CMAKE_CURRENT_SOURCE_DIR}/../rag-llm/models"
    "$<TARGET_FILE_DIR:threat_engine_server>/models"
    COMMENT "Copying ONNX models to the build directory..."
)
