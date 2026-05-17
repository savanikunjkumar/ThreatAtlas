#!/bin/bash
# ==============================================================================
# ThreatAtlas Model Downloader
# Fetches the ONNX-optimized models needed by the C++ engine.
# ==============================================================================

# Exit if any command fails
set -e

# Change to the directory where this script lives, so we always download to /models
cd "$(dirname "$0")"

GREEN='\033[0;32m'
YELLOW='\033[1;33m'
RED='\033[0;31m'
NC='\033[0m'

echo -e "${YELLOW}[*] Initializing model downloads from HuggingFace...${NC}"

# Check if curl or wget is installed
if command -v curl &> /dev/null; then
    DOWNLOAD_CMD="curl -L -o"
elif command -v wget &> /dev/null; then
    DOWNLOAD_CMD="wget -O"
else
    echo -e "${RED}[!] Error: Neither curl nor wget is installed. I can't download the models without them.${NC}"
    exit 1
fi

# ------------------------------------------------------------------------------
# Model 1: The Dense Embedder (Sentence Transformer)
# all-MiniLM-L6-v2 is the industry standard for fast, small text embeddings.
# ------------------------------------------------------------------------------
EMBEDDER_FILE="all-MiniLM-L6-v2.onnx"
EMBEDDER_URL="https://huggingface.co/Xenova/all-MiniLM-L6-v2/resolve/main/onnx/model.onnx"

if [ ! -f "$EMBEDDER_FILE" ]; then
    echo "Downloading Embedder: $EMBEDDER_FILE (~80MB)..."
    $DOWNLOAD_CMD "$EMBEDDER_FILE" "$EMBEDDER_URL"
    echo -e "${GREEN}✔ Embedder downloaded successfully.${NC}"
else
    echo -e "${GREEN}✔ Embedder already exists. Skipping.${NC}"
fi

# ------------------------------------------------------------------------------
# Model 2: The Reranker (Cross-Encoder)
# ms-marco-MiniLM-L-6-v2 computes exact query-document relevance scores.
# ------------------------------------------------------------------------------
# Note: Renaming it slightly to match what we hardcoded in main.cpp
RERANKER_FILE="ms-marco-MiniLM-L-6-v2-onnx"
RERANKER_URL="https://huggingface.co/Xenova/ms-marco-MiniLM-L-6-v2/resolve/main/onnx/model.onnx"

if [ ! -f "$RERANKER_FILE" ]; then
    echo "Downloading Reranker: $RERANKER_FILE (~80MB)..."
    $DOWNLOAD_CMD "$RERANKER_FILE" "$RERANKER_URL"
    echo -e "${GREEN}✔ Reranker downloaded successfully.${NC}"
else
    echo -e "${GREEN}✔ Reranker already exists. Skipping.${NC}"
fi

# ------------------------------------------------------------------------------
echo -e "\n${YELLOW}Done! Your C++ engine is now locked and loaded with ONNX models.${NC}"
echo "Run 'ls -lh' in this directory to verify file sizes."
