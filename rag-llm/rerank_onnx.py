import os
import sys
import time
import math

# We need ONNX for the speed, and transformers for the tokenizer.
# ONNX only knows how to do math; it has no idea what a string is.
try:
    import onnxruntime as ort
    from transformers import AutoTokenizer
    import numpy as np
except ImportError:
    print("[!] FATAL: Missing dependencies for the reranker.")
    print("    Run: pip install onnxruntime transformers numpy")
    sys.exit(1)

# TODO: Hardcoded paths for now.
# This should point to the cross-encoder downloaded by your bash script.
MODEL_PATH = "models/ms-marco-MiniLM-L-6-v2-onnx"
# We pull the tokenizer config directly from HuggingFace
TOKENIZER_NAME = "cross-encoder/ms-marco-MiniLM-L-6-v2"

def sigmoid(x):
    """Cross-encoders output raw, unbounded logits. We need 0.0 to 1.0 percentages."""
    return 1 / (1 + np.exp(-x))

def boot_reranker():
    print(f"[*] Booting Cross-Encoder from {MODEL_PATH}...")
    if not os.path.exists(MODEL_PATH):
        print(f"[!] Error: Model not found at {MODEL_PATH}.")
        print("    Did you run models/download_encoder.sh?")
        sys.exit(1)

    # Fire up ONNX. Threading optimized for CPU bursts.
    sess_options = ort.SessionOptions()
    sess_options.intra_op_num_threads = 4
    
    session = ort.InferenceSession(MODEL_PATH, sess_options, providers=['CPUExecutionProvider'])
    
    # We still need HF to convert words into numbers for the ONNX model
    print(f"[*] Fetching tokenizer '{TOKENIZER_NAME}'...")
    tokenizer = AutoTokenizer.from_pretrained(TOKENIZER_NAME)
    
    print("[+] Reranker locked and loaded.")
    return session, tokenizer

def rerank_results(session, tokenizer, query, retrieved_docs):
    """
    Takes a query and a list of dictionary logs: [{'id': '1', 'text': '...'}]
    Returns the same list, sorted by absolute relevance.
    """
    if not retrieved_docs:
        return []

    print(f"\n[*] Reranking {len(retrieved_docs)} candidates for query: '{query}'")
    start_time = time.time()

    # Cross-encoders expect input as pairs: [[Query, Doc1], [Query, Doc2], ...]
    pairs = [[query, doc['text']] for doc in retrieved_docs]

    # Tokenize the whole batch at once
    inputs = tokenizer(pairs, padding=True, truncation=True, return_tensors="np", max_length=512)

    # Feed it to the ONNX graph
    onnx_inputs = {
        'input_ids': inputs['input_ids'].astype(np.int64),
        'attention_mask': inputs['attention_mask'].astype(np.int64),
        'token_type_ids': inputs['token_type_ids'].astype(np.int64)
    }

    # The output is a single array of raw logit scores
    raw_scores = session.run(None, onnx_inputs)[0]
    
    # Convert raw logits to probabilities (0.0 to 1.0)
    probabilities = sigmoid(raw_scores).flatten()

    # Zip the scores back into our dictionary and sort them descending
    for i, doc in enumerate(retrieved_docs):
        doc['relevance_score'] = float(probabilities[i])

    ranked_docs = sorted(retrieved_docs, key=lambda x: x['relevance_score'], reverse=True)
    
    elapsed = time.time() - start_time
    print(f"    (Reranked in {elapsed:.3f}s)")
    
    return ranked_docs

if __name__ == "__main__":
    # ---------------------------------------------------------
    # 2 AM SANITY CHECK BLOCK
    # Let's make sure the math actually works before we wire it up.
    # ---------------------------------------------------------
    test_query = "Did the user successfully escalate privileges to root?"
    
    # Notice how Doc 2 shares keywords but isn't the answer, 
    # while Doc 3 is the exact answer. FAISS might get this wrong, Reranker shouldn't.
    test_docs = [
        {"id": "log_1", "text": "May 14 10:00:01 server sshd: Connection closed by 192.168.1.5"},
        {"id": "log_2", "text": "User admin attempted to run sudo apt-get update but failed due to missing privileges."},
        {"id": "log_3", "text": "May 14 10:05:22 server sudo: admin : TTY=pts/0 ; PWD=/home/admin ; USER=root ; COMMAND=/bin/bash"}
    ]

    sess, tok = boot_reranker()
    final_results = rerank_results(sess, tok, test_query, test_docs)
    
    print("\n--- FINAL RANKINGS ---")
    for rank, doc in enumerate(final_results, 1):
        print(f"#{rank} | Score: {doc['relevance_score']:.4f} | ID: {doc['id']}")
        print(f"    > {doc['text'][:80]}...")
