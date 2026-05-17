#!/bin/bash
# ==============================================================================
# ThreatAtlas LLM Downloader
# Fetches the massive GGUF weights for local inference via llama.cpp.
# ==============================================================================

# Exit if any command fails
set -e

# Always run from the directory where this script lives
cd "$(dirname "$0")"

GREEN='\033[0;32m'
YELLOW='\033[1;33m'
RED='\033[0;31m'
NC='\033[0m'

echo -e "${YELLOW}[*] Preparing to pull the big brain (LLM)...${NC}"

# We need a downloader that supports resuming interrupted downloads.
# GGUF files are huge. A network blip shouldn't ruin your day.
if command -v wget &> /dev/null; then
    # -c means 'continue' if the file is partially downloaded
    DOWNLOAD_CMD="wget -c -O"
elif command -v curl &> /dev/null; then
    # -C - means 'resume from where it left off'
    DOWNLOAD_CMD="curl -C - -L -o"
else
    echo -e "${RED}[!] Error: You need wget or curl to download this beast.${NC}"
    exit 1
fi

# ------------------------------------------------------------------------------
# The LLM: Mistral-7B-Instruct (Q4_K_M Quantization)
# Why this one? It's fast, uncensored enough for security logs, and 
# Q4_K_M uses about 4.5GB of RAM. Perfect for local RAG.
# ------------------------------------------------------------------------------
LLM_FILE="mistral-7b-instruct-v0.2.Q4_K_M.gguf"
# Using a reliable HuggingFace mirror for GGUF files
LLM_URL="https://huggingface.co/TheBloke/Mistral-7B-Instruct-v0.2-GGUF/resolve/main/mistral-7b-instruct-v0.2.Q4_K_M.gguf"

if [ ! -f "$LLM_FILE" ]; then
    echo -e "Downloading LLM: ${YELLOW}$LLM_FILE (~4.3 GB)${NC}"
    echo "Grab a coffee, this is going to take a minute..."
    
    $DOWNLOAD_CMD "$LLM_FILE" "$LLM_URL"
    
    echo -e "${GREEN}✔ LLM downloaded successfully.${NC}"
else
    echo -e "${GREEN}✔ LLM weights ($LLM_FILE) already exist. Ready to rock.${NC}"
fi

# ------------------------------------------------------------------------------
echo -e "\n${YELLOW}Done! Check file size to ensure it completed (should be ~4.3GB):${NC}"
ls -lh $LLM_FILE
