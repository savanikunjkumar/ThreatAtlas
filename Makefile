# ==========================================
# ThreatAtlas Master Makefile
# ==========================================

# Variables
CPP_DIR = cpp-core
JAVA_DIR = java-services
PYTHON_DIR = rag-llm
NPROC := $(shell nproc 2>/dev/null || sysctl -n hw.ncpu 2>/dev/null || echo 4)

.PHONY: all help build-cpp build-java setup-python build-all clean up down test-java test-cpp

# Default target
all: help

help:
	@echo "🛡️  ThreatAtlas Command Center"
	@echo "==============================="
	@echo "make build-cpp    - Build the C++ core engine (CMake/Make)"
	@echo "make build-java   - Build the Java Spring Boot orchestrator (Maven + Protoc)"
	@echo "make setup-python - Setup Python virtual environment & install requirements"
	@echo "make build-all    - Build C++, Java, and setup Python in one go"
	@echo "make test-cpp     - Run C++ unit tests"
	@echo "make test-java    - Run Java Spring Boot tests"
	@echo "make up           - Start Milvus, MinIO, and etcd via Docker Compose"
	@echo "make down         - Stop and remove Docker containers"
	@echo "make clean        - Wipe all build artifacts across all languages"

# --- C++ Commands ---
build-cpp:
	@echo "\n🚀 Building C++ Core Engine..."
	mkdir -p $(CPP_DIR)/build
	cd $(CPP_DIR)/build && cmake .. && make -j$(NPROC)

test-cpp: build-cpp
	@echo "\n🧪 Running C++ Tests..."
	cd $(CPP_DIR)/build && ctest --output-on-failure

# --- Java Commands ---
build-java:
	@echo "\n☕ Building Java Orchestrator (Skipping tests for speed)..."
	cd $(JAVA_DIR) && mvn clean install -DskipTests

test-java:
	@echo "\n🧪 Running Java Tests..."
	cd $(JAVA_DIR) && mvn test

# --- Python Commands ---
setup-python:
	@echo "\n🐍 Setting up Python RAG LLM environment..."
	cd $(PYTHON_DIR) && \
	python3 -m venv venv && \
	. venv/bin/activate && \
	pip install --upgrade pip && \
	pip install -r requirements.txt
	@echo "Done! Run 'source $(PYTHON_DIR)/venv/bin/activate' to use."

# --- Unified Build ---
build-all: build-cpp build-java setup-python
	@echo "\n✨ All systems built successfully!"

# --- Docker / Infrastructure ---
up:
	@echo "\n🐳 Starting Milvus Vector Database Infrastructure..."
	docker compose up -d

down:
	@echo "\n🛑 Stopping Infrastructure..."
	docker compose down

# --- Cleanup ---
clean:
	@echo "\n🧹 Sweeping up build artifacts..."
	rm -rf $(CPP_DIR)/build
	cd $(JAVA_DIR) && mvn clean || true
	rm -rf $(PYTHON_DIR)/venv
	rm -rf $(PYTHON_DIR)/__pycache__
	@echo "Clean complete."
