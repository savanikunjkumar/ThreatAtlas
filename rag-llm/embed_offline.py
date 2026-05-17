import os
import json
import time

# You'll need to run: pip install pymilvus transformers onnxruntime
try:
    from pymilvus import connections, utility, FieldSchema, CollectionSchema, DataType, Collection
except ImportError:
    print("[!] Missing pymilvus. Run: pip install pymilvus")
    exit(1)

# ==============================================================================
# Configuration
# ==============================================================================
MILVUS_HOST = 'localhost'
MILVUS_PORT = '19530'
COLLECTION_NAME = 'threat_index'
DIMENSION = 384  # Matches all-MiniLM-L6-v2 output
BATCH_SIZE = 500 # Don't insert 1 by 1. That's a crime against network I/O.

def setup_milvus():
    """Connects to Milvus and wipes/recreates the schema."""
    print(f"[*] Connecting to Milvus at {MILVUS_HOST}:{MILVUS_PORT}...")
    try:
        connections.connect("default", host=MILVUS_HOST, port=MILVUS_PORT)
    except Exception as e:
        print(f"[!] Failed to connect to Milvus. Did you run 'make up'? Error: {e}")
        exit(1)

    if utility.has_collection(COLLECTION_NAME):
        print(f"[*] Dropping existing collection '{COLLECTION_NAME}' for a clean slate...")
        utility.drop_collection(COLLECTION_NAME)

    # Define the schema
    fields = [
        FieldSchema(name="chunk_id", dtype=DataType.VARCHAR, is_primary=True, auto_id=False, max_length=200),
        FieldSchema(name="raw_text", dtype=DataType.VARCHAR, max_length=65535), # Store text for the RAG prompt
        FieldSchema(name="embedding", dtype=DataType.FLOAT_VECTOR, dim=DIMENSION)
    ]
    
    schema = CollectionSchema(fields, description="ThreatAtlas Offline Batch Index")
    collection = Collection(name=COLLECTION_NAME, schema=schema)
    
    # Create an IVF_PQ index. It's much faster than HNSW for massive batch searches in Milvus.
    index_params = {
        "metric_type": "L2",
        "index_type": "IVF_FLAT", 
        "params": {"nlist": 128}
    }
    collection.create_index(field_name="embedding", index_params=index_params)
    print(f"[*] Collection '{COLLECTION_NAME}' created and indexed.")
    
    return collection

def mock_onnx_embedding(text):
    """
    TODO: Wire up actual ONNX Runtime here.
    For now, generating a deterministic mock vector based on text length 
    so the script doesn't crash if you haven't downloaded the models yet.
    """
    import random
    random.seed(len(text))
    return [random.uniform(-1.0, 1.0) for _ in range(DIMENSION)]

def batch_embed_and_upsert(filepath, collection):
    print(f"[*] Starting offline batch processing for: {filepath}")
    
    if not os.path.exists(filepath):
        print(f"[!] Can't find {filepath}. Did you run ingest.py first?")
        return

    buffer_ids = []
    buffer_texts = []
    buffer_vectors = []
    
    total_inserted = 0
    start_time = time.time()

    with open(filepath, 'r', encoding='utf-8') as f:
        for line_num, line in enumerate(f):
            # Simulating reading the output from ingest.py
            # In reality, ingest.py should probably write JSONL with id and chunk
            try:
                data = json.loads(line)
                chunk_id = f"mock-id-{line_num}"
                raw_text = data.get('description', 'dummy text')
                
                # 1. Generate Embedding (This is where the CPU/GPU burns)
                vector = mock_onnx_embedding(raw_text)
                
                # 2. Add to buffer
                buffer_ids.append(chunk_id)
                buffer_texts.append(raw_text)
                buffer_vectors.append(vector)
                
                # 3. Flush buffer to Milvus if it hits BATCH_SIZE
                if len(buffer_ids) >= BATCH_SIZE:
                    collection.insert([buffer_ids, buffer_texts, buffer_vectors])
                    total_inserted += len(buffer_ids)
                    print(f" -> Flushed {len(buffer_ids)} vectors to Milvus. Total: {total_inserted}")
                    
                    # Clear buffers
                    buffer_ids, buffer_texts, buffer_vectors = [], [], []
                    
            except json.JSONDecodeError:
                continue

    # Flush any remaining items in the buffer
    if buffer_ids:
        collection.insert([buffer_ids, buffer_texts, buffer_vectors])
        total_inserted += len(buffer_ids)
        print(f" -> Flushed final {len(buffer_ids)} vectors.")

    collection.flush()
    elapsed = time.time() - start_time
    print(f"\n[+] Success! Upserted {total_inserted} vectors in {elapsed:.2f} seconds.")

if __name__ == "__main__":
    # Point this to whatever sample data you have
    sample_data = "../datasets/sample_cve.jsonl"
    
    # 1. Boot up the DB connection
    col = setup_milvus()
    
    # 2. Start crunching vectors
    batch_embed_and_upsert(sample_data, col)
