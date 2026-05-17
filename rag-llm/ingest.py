import os
import json
# import grpc  # Uncomment this when we wire up the C++ backend

# TODO: Move these to a config file or .env later. 
# Hardcoding for now just to get the pipeline breathing.
CHUNK_SIZE = 512
OVERLAP = 50

def slide_window(text, chunk_size, overlap):
    """
    Standard sliding window chunker. 
    Syslogs are messy and don't care about polite sentence boundaries, 
    so we just blindly chop them up with a little overlap so we don't lose context.
    """
    chunks = []
    
    # Quick sanity check
    if not text.strip():
        return chunks
        
    words = text.split()
    # Step through the text, pulling overlapping chunks
    for i in range(0, len(words), chunk_size - overlap):
        chunk = " ".join(words[i:i + chunk_size])
        chunks.append(chunk)
        
    return chunks

def process_file(filepath):
    print(f"[*] Spinning up ingestion for: {filepath}")
    
    if not os.path.exists(filepath):
        print(f"[!] Error: Dude, {filepath} doesn't exist. Check your paths.")
        return

    with open(filepath, 'r', encoding='utf-8') as f:
        # Assuming JSONL format for CVEs right now. 
        # We'll probably need to write a dirty regex parser for raw .log files later.
        for line_num, line in enumerate(f):
            try:
                data = json.loads(line)
                # Grab whatever looks like the main text
                raw_text = data.get('description', '') or data.get('log_msg', '')
                
                # Chop it up
                chunks = slide_window(raw_text, CHUNK_SIZE, OVERLAP)
                
                for idx, chunk in enumerate(chunks):
                    # Crafting a unique ID. This is crucial so when the LLM hallucinates, 
                    # we can trace the exact line of code/log it looked at.
                    source_id = f"{os.path.basename(filepath)}-L{line_num}-C{idx}"
                    
                    # TODO: Actually fire this over gRPC to our C++ VectorComputeService
                    # stub.GenerateEmbedding(LogChunk(source_id=source_id, raw_text=chunk))
                    
                    print(f" -> Queued for embedding: {source_id}")
                    
            except json.JSONDecodeError:
                print(f"[!] Line {line_num} is garbage JSON. Skipping.")
                continue

if __name__ == "__main__":
    # Local test run
    sample_file = "../datasets/sample_cve.jsonl"
    
    # Touch a dummy file just so the script doesn't crash if you haven't made datasets yet
    os.makedirs(os.path.dirname(sample_file), exist_ok=True)
    if not os.path.exists(sample_file):
        with open(sample_file, 'w') as temp:
            temp.write('{"description": "Buffer overflow in obscure SSH library."}\n')
            
    process_file(sample_file)
