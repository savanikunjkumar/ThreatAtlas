import os
import sys
import time

# Let's see if you actually installed the heavy dependencies.
# If you have a GPU, FOR THE LOVE OF GOD install this with hardware acceleration:
# Mac: CMAKE_ARGS="-DLLAMA_METAL=on" pip install llama-cpp-python
# Nvidia: CMAKE_ARGS="-DLLAMA_CUBLAS=on" pip install llama-cpp-python
try:
    from llama_cpp import Llama
except ImportError:
    print("[!] FATAL: You need llama-cpp-python installed.")
    print("    Run: pip install llama-cpp-python")
    sys.exit(1)

# TODO: Move these to a .env file later when we productionize this.
# Pointing to the GGUF model we downloaded via the bash script.
MODEL_PATH = "models/mistral-7b-instruct-v0.2.Q4_K_M.gguf"

# The System Prompt. This is where we put the AI in a chokehold so it doesn't hallucinate.
SYSTEM_PROMPT = """You are ThreatAtlas, an elite Tier 3 SOC Analyst. 
You are analyzing raw system logs and network events to answer the user's query.
RULES:
1. ONLY use the information provided in the CONTEXT LOGS. 
2. If the logs do not contain the answer, say exactly: "Insufficient telemetry to determine."
3. You MUST cite the [SOURCE_ID] for every claim you make. Do not guess.
"""

def boot_llm():
    """Loads the massive model into memory/VRAM."""
    print(f"[*] Booting local LLM from {MODEL_PATH}...")
    if not os.path.exists(MODEL_PATH):
        print(f"[!] Error: Model not found at {MODEL_PATH}.")
        print("    Did you forget to run models/download_llm.sh?")
        sys.exit(1)
        
    # n_ctx=2048: Context window. 2048 is plenty for a question + ~5 log chunks.
    # n_gpu_layers=-1: Offload everything to the GPU so your CPU doesn't catch fire.
    print("    (Allocating memory, this might take a sec...)")
    llm = Llama(model_path=MODEL_PATH, n_ctx=2048, n_gpu_layers=-1, verbose=False)
    print("[+] LLM loaded and ready.")
    return llm

def build_prompt(query, retrieved_logs):
    """
    Mistral uses the strict <s>[INST] instruction [/INST] format. 
    If you mess this up, the AI acts like a drunk toddler.
    """
    context_block = ""
    for log in retrieved_logs:
        context_block += f"\n[SOURCE_ID: {log['id']}]\n{log['text']}\n"
        
    prompt = f"<s>[INST] {SYSTEM_PROMPT}\n\nCONTEXT LOGS:{context_block}\n\nUSER QUERY: {query} [/INST]"
    return prompt

def generate_report(llm, query, retrieved_logs):
    prompt = build_prompt(query, retrieved_logs)
    
    print(f"\n[*] Generating Threat Report for query: '{query}'")
    print("    Thinking...\n")
    start_time = time.time()
    
    # Hit the model. Temperature 0.1 because we want facts, not creative writing.
    response = llm(
        prompt, 
        max_tokens=512, 
        temperature=0.1, 
        stop=["</s>", "[INST]"], 
        echo=False
    )
    
    elapsed = time.time() - start_time
    answer = response["choices"][0]["text"].strip()
    
    print("="*60)
    print("🛡️  THREATATLAS INTELLIGENCE REPORT")
    print("="*60)
    print(answer)
    print("="*60)
    print(f"(Inference completed in {elapsed:.2f}s)")
    
    return answer

if __name__ == "__main__":
    # ---------------------------------------------------------
    # WEEKEND TEST BLOCK
    # In the real app, the Java Orchestrator calls this script 
    # and passes the logs retrieved by the C++ engine.
    # For now, we mock it so we can test the LLM in isolation.
    # ---------------------------------------------------------
    
    dummy_query = "What IP address is performing the SSH brute force attack, and what user are they targeting?"
    
    dummy_logs = [
        {
            "id": "auth.log-L1042",
            "text": "May 14 10:32:01 server1 sshd[1020]: Failed password for invalid user admin from 192.168.1.105 port 54322 ssh2"
        },
        {
            "id": "auth.log-L1043",
            "text": "May 14 10:32:03 server1 sshd[1022]: Failed password for invalid user root from 192.168.1.105 port 54324 ssh2"
        },
        {
            "id": "auth.log-L1044",
            "text": "May 14 10:32:05 server1 sshd[1025]: Accepted password for root from 192.168.1.105 port 54326 ssh2"
        }
    ]
    
    llm_instance = boot_llm()
    generate_report(llm_instance, dummy_query, dummy_logs)
