<!DOCTYPE html>
<html lang="en">
<head>
<meta charset="UTF-8"/>
<meta name="viewport" content="width=device-width, initial-scale=1.0"/>
<title>ThreatAtlas — README</title>
<style>
:root {
  --bg: #0d1117;
  --bg2: #161b22;
  --bg3: #21262d;
  --border: #30363d;
  --border2: #444c56;
  --text: #e6edf3;
  --text2: #8b949e;
  --text3: #6e7681;
  --purple: #a371f7;
  --purple-bg: #1a1038;
  --teal: #39d353;
  --teal-bg: #071d07;
  --blue: #58a6ff;
  --blue-bg: #0d1526;
  --amber: #e3b341;
  --amber-bg: #1e1700;
  --coral: #f78166;
  --coral-bg: #260d08;
  --pink: #db61a2;
  --pink-bg: #200d15;
  --green: #3fb950;
  --green-bg: #0a1f0a;
  --red: #f85149;
  --font-mono: 'SFMono-Regular', Consolas, 'Liberation Mono', Menlo, monospace;
  --font-sans: -apple-system, BlinkMacSystemFont, 'Segoe UI', Helvetica, Arial, sans-serif;
  --radius: 6px;
  --radius-lg: 10px;
}
* { box-sizing: border-box; margin: 0; padding: 0; }
body {
  font-family: var(--font-sans);
  background: var(--bg);
  color: var(--text);
  font-size: 14px;
  line-height: 1.65;
  max-width: 960px;
  margin: 0 auto;
  padding: 40px 24px 80px;
}

/* Header */
.repo-header { display: flex; align-items: flex-start; gap: 16px; margin-bottom: 24px; }
.repo-icon { width: 52px; height: 52px; border-radius: var(--radius-lg); background: linear-gradient(135deg, var(--purple-bg), #2a1545); border: 1px solid var(--purple); display: flex; align-items: center; justify-content: center; flex-shrink: 0; }
.repo-icon svg { width: 28px; height: 28px; color: var(--purple); }
.repo-name { font-size: 24px; font-weight: 600; color: var(--blue); margin-bottom: 4px; }
.repo-name span { color: var(--text2); font-weight: 400; }
.repo-desc { color: var(--text2); font-size: 13px; line-height: 1.5; max-width: 540px; }

/* Badges */
.badges { display: flex; flex-wrap: wrap; gap: 6px; margin: 16px 0 28px; }
.badge {
  font-size: 11px; font-weight: 500; padding: 3px 10px; border-radius: 20px;
  display: inline-flex; align-items: center; gap: 5px; letter-spacing: .02em;
  border: 0.5px solid;
}
.b-cpp    { background: var(--purple-bg); color: var(--purple);  border-color: var(--purple); }
.b-java   { background: var(--amber-bg);  color: var(--amber);   border-color: var(--amber); }
.b-py     { background: var(--blue-bg);   color: var(--blue);    border-color: var(--blue); }
.b-rag    { background: var(--coral-bg);  color: var(--coral);   border-color: var(--coral); }
.b-milvus { background: var(--teal-bg);   color: var(--teal);    border-color: var(--teal); }
.b-ci     { background: #161b22; color: #8b949e; border-color: #444c56; }
.b-license { background: var(--green-bg); color: var(--green);   border-color: var(--green); }

/* Divider */
.divider { height: 1px; background: var(--border); margin: 28px 0; }

/* Sections */
h2 { font-size: 18px; font-weight: 600; padding-bottom: 8px; border-bottom: 1px solid var(--border); margin: 32px 0 16px; color: var(--text); }
h3 { font-size: 15px; font-weight: 600; margin: 24px 0 10px; color: var(--text); }
p  { color: var(--text2); margin-bottom: 12px; }

/* Table of contents */
.toc { background: var(--bg2); border: 1px solid var(--border); border-radius: var(--radius-lg); padding: 16px 20px; margin-bottom: 28px; }
.toc-title { font-size: 12px; font-weight: 600; letter-spacing: .08em; text-transform: uppercase; color: var(--text3); margin-bottom: 10px; }
.toc-list { list-style: none; display: grid; grid-template-columns: 1fr 1fr; gap: 4px; }
.toc-list li a { color: var(--blue); font-size: 13px; text-decoration: none; }
.toc-list li a:hover { text-decoration: underline; }

/* Architecture interactive diagram */
.arch-wrap { background: var(--bg2); border: 1px solid var(--border); border-radius: var(--radius-lg); padding: 24px; margin: 20px 0; position: relative; overflow: hidden; }
.arch-wrap::before { content:''; position:absolute; inset:0; background: radial-gradient(ellipse at 20% 20%, rgba(163,113,247,.04) 0%, transparent 60%), radial-gradient(ellipse at 80% 80%, rgba(88,166,255,.04) 0%, transparent 60%); pointer-events:none; }

/* Node boxes */
.arch-svg { width: 100%; }

/* Stack cards */
.stack { display: grid; grid-template-columns: repeat(auto-fit, minmax(200px, 1fr)); gap: 12px; margin: 16px 0; }
.card {
  background: var(--bg2); border: 1px solid var(--border);
  border-radius: var(--radius-lg); padding: 14px 16px;
  transition: border-color .15s, background .15s;
  cursor: default;
}
.card:hover { border-color: var(--border2); background: var(--bg3); }
.card.purple { border-left: 2px solid var(--purple); }
.card.amber  { border-left: 2px solid var(--amber); }
.card.blue   { border-left: 2px solid var(--blue); }
.card.coral  { border-left: 2px solid var(--coral); }
.card.teal   { border-left: 2px solid var(--teal); }
.card.pink   { border-left: 2px solid var(--pink); }
.card-label  { font-size: 11px; font-weight: 600; letter-spacing: .08em; text-transform: uppercase; margin-bottom: 5px; }
.card-title  { font-size: 14px; font-weight: 600; margin-bottom: 4px; color: var(--text); }
.card-desc   { font-size: 12px; color: var(--text2); line-height: 1.5; }
.card-tags   { display: flex; flex-wrap: wrap; gap: 4px; margin-top: 8px; }
.tag { font-size: 10px; font-weight: 500; padding: 2px 7px; border-radius: 10px; }
.tag-p { background: #1a1038; color: #a371f7; }
.tag-j { background: #1e1700; color: #e3b341; }
.tag-b { background: #0d1526; color: #58a6ff; }
.tag-t { background: #071d07; color: #39d353; }
.tag-c { background: #260d08; color: #f78166; }

/* Pipeline flow */
.pipeline { display: flex; align-items: center; gap: 0; flex-wrap: nowrap; margin: 16px 0; overflow-x: auto; padding-bottom: 4px; }
.pipe-step {
  flex-shrink: 0; background: var(--bg3); border: 1px solid var(--border);
  border-radius: var(--radius); padding: 8px 12px;
  font-size: 12px; font-weight: 500; color: var(--text);
  white-space: nowrap;
}
.pipe-step.active { border-color: var(--purple); color: var(--purple); }
.pipe-arrow { color: var(--text3); font-size: 16px; padding: 0 6px; flex-shrink: 0; }
.pipe-time { font-size: 10px; color: var(--text3); text-align: center; margin-top: 3px; }

/* Code block */
pre {
  background: var(--bg2); border: 1px solid var(--border);
  border-radius: var(--radius-lg); padding: 16px; overflow-x: auto;
  font-family: var(--font-mono); font-size: 12px; line-height: 1.7;
  color: var(--text2); margin: 12px 0;
}
code { font-family: var(--font-mono); font-size: 12px; color: var(--coral); background: rgba(247,129,102,.1); padding: 1px 5px; border-radius: 3px; }
.kw { color: var(--purple); } .str { color: var(--teal); } .cm { color: var(--text3); } .num { color: var(--amber); } .fn { color: var(--blue); }

/* Port table */
table { width: 100%; border-collapse: collapse; font-size: 13px; margin: 12px 0; }
th { text-align: left; padding: 8px 12px; font-size: 11px; font-weight: 600; letter-spacing: .06em; text-transform: uppercase; color: var(--text3); border-bottom: 1px solid var(--border); }
td { padding: 10px 12px; border-bottom: 1px solid var(--border); color: var(--text2); vertical-align: top; }
td:first-child { font-family: var(--font-mono); font-size: 12px; color: var(--amber); }
tr:last-child td { border-bottom: none; }
tr:hover td { background: var(--bg3); }

/* Metric cards */
.metrics { display: grid; grid-template-columns: repeat(4, 1fr); gap: 10px; margin: 16px 0; }
.metric { background: var(--bg3); border-radius: var(--radius); padding: 12px 14px; }
.metric-val { font-size: 22px; font-weight: 600; margin-bottom: 2px; }
.metric-label { font-size: 11px; color: var(--text3); }
.m-p { color: var(--purple); }
.m-b { color: var(--blue); }
.m-t { color: var(--teal); }
.m-a { color: var(--amber); }

/* Interactive architecture */
.node-info { background: var(--bg3); border: 1px solid var(--border2); border-radius: var(--radius-lg); padding: 14px 16px; margin-top: 16px; min-height: 80px; transition: all .2s; }
.node-info-title { font-size: 13px; font-weight: 600; color: var(--text); margin-bottom: 6px; }
.node-info-body  { font-size: 12px; color: var(--text2); line-height: 1.6; }
.node-info-tags  { display: flex; flex-wrap: wrap; gap: 4px; margin-top: 8px; }

/* Phase timeline */
.phases { display: flex; flex-direction: column; gap: 0; }
.phase { display: grid; grid-template-columns: 56px 1fr; align-items: start; gap: 0; position: relative; }
.phase:not(:last-child)::after { content:''; position:absolute; left:27px; top:40px; bottom:-4px; width:1px; background:var(--border); }
.phase-dot { width:28px; height:28px; border-radius:50%; display:flex; align-items:center; justify-content:center; font-size:11px; font-weight:700; margin:6px auto 0; border:1px solid; }
.phase-body { padding: 4px 0 20px 4px; }
.phase-n { font-size:11px; color:var(--text3); letter-spacing:.06em; text-transform:uppercase; margin-bottom:2px; }
.phase-t { font-size:13px; font-weight:600; color:var(--text); margin-bottom:3px; }
.phase-d { font-size:12px; color:var(--text2); line-height:1.55; }
.phase-ok { font-size:11px; color:var(--green); margin-top:5px; }

.tip { background:var(--blue-bg); border:1px solid rgba(88,166,255,.3); border-radius:var(--radius); padding:12px 14px; margin:16px 0; font-size:12px; color:var(--blue); }
.tip::before { content:'ℹ  '; font-weight:700; }

</style>
</head>
<body>

<!-- Header -->
<div class="repo-header">
  <div class="repo-icon">
    <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round">
      <path d="M12 2L2 7l10 5 10-5-10-5z"/><path d="M2 17l10 5 10-5"/><path d="M2 12l10 5 10-5"/>
    </svg>
  </div>
  <div>
    <div class="repo-name"><span>your-org /</span> ThreatAtlas</div>
    <div class="repo-desc">AI-powered threat intelligence engine — RAG pipeline with C++17 bare-metal compute, Java orchestration, Python ML reasoning, FAISS + Milvus vector search, and local LLM inference.</div>
  </div>
</div>

<!-- Badges -->
<div class="badges">
  <span class="badge b-ci">CI passing</span>
  <span class="badge b-cpp">C++17</span>
  <span class="badge b-java">Java 21</span>
  <span class="badge b-py">Python 3.11</span>
  <span class="badge b-rag">RAG</span>
  <span class="badge b-milvus">Milvus</span>
  <span class="badge b-ci">FAISS</span>
  <span class="badge b-ci">ONNX Runtime</span>
  <span class="badge b-license">MIT License</span>
</div>

<div class="divider"></div>

<!-- Table of Contents -->
<div class="toc">
  <div class="toc-title">Table of Contents</div>
  <ul class="toc-list">
    <li><a href="#overview">Overview</a></li>
    <li><a href="#architecture">Architecture</a></li>
    <li><a href="#tech">Tech Stack</a></li>
    <li><a href="#pipeline">Query Pipeline</a></li>
    <li><a href="#structure">Folder Structure</a></li>
    <li><a href="#phases">Phase Roadmap</a></li>
    <li><a href="#ports">Port Matrix</a></li>
    <li><a href="#quickstart">Quick Start</a></li>
    <li><a href="#benchmarks">Benchmarks</a></li>
    <li><a href="#docs">Docs</a></li>
  </ul>
</div>

<!-- Overview -->
<h2 id="overview">Overview</h2>
<p>ThreatAtlas is a <strong style="color:var(--text)">Retrieval-Augmented Generation (RAG)</strong> system purpose-built for cybersecurity intelligence workloads. It ingests raw threat logs, CVE advisories, and security papers — encodes them into a 384-dimensional vector space — and answers analyst queries with grounded, citation-backed incident reports. No external API calls. Everything runs locally.</p>

<div class="metrics">
  <div class="metric"><div class="metric-val m-p">&lt;5 ms</div><div class="metric-label">ANN retrieval p99</div></div>
  <div class="metric"><div class="metric-val m-b">&lt;80 ms</div><div class="metric-label">Cross-encoder rerank</div></div>
  <div class="metric"><div class="metric-val m-t">10M+</div><div class="metric-label">Milvus vector scale</div></div>
  <div class="metric"><div class="metric-val m-a">8 phases</div><div class="metric-label">Dev roadmap</div></div>
</div>

<!-- Architecture -->
<h2 id="architecture">Architecture — click a node to explore</h2>
<div class="arch-wrap">
  <svg class="arch-svg" viewBox="0 0 860 480" xmlns="http://www.w3.org/2000/svg">
    <defs>
      <marker id="arr" viewBox="0 0 10 10" refX="8" refY="5" markerWidth="5" markerHeight="5" orient="auto-start-reverse">
        <path d="M2 1L8 5L2 9" fill="none" stroke="context-stroke" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round"/>
      </marker>
    </defs>

    <!-- Grid lines (subtle) -->
    <line x1="0" y1="120" x2="860" y2="120" stroke="#21262d" stroke-width="1" stroke-dasharray="4 6"/>
    <line x1="0" y1="280" x2="860" y2="280" stroke="#21262d" stroke-width="1" stroke-dasharray="4 6"/>
    <line x1="0" y1="420" x2="860" y2="420" stroke="#21262d" stroke-width="1" stroke-dasharray="4 6"/>

    <!-- Tier labels -->
    <text x="12" y="70" font-family="-apple-system,sans-serif" font-size="10" fill="#484f58" letter-spacing=".08em" font-weight="600">CLIENT TIER</text>
    <text x="12" y="200" font-family="-apple-system,sans-serif" font-size="10" fill="#484f58" letter-spacing=".08em" font-weight="600">ORCHESTRATION</text>
    <text x="12" y="340" font-family="-apple-system,sans-serif" font-size="10" fill="#484f58" letter-spacing=".08em" font-weight="600">COMPUTE</text>
    <text x="12" y="456" font-family="-apple-system,sans-serif" font-size="10" fill="#484f58" letter-spacing=".08em" font-weight="600">STORAGE</text>

    <!-- CLIENT -->
    <g id="nd-client" style="cursor:pointer" onclick="showInfo('client')">
      <rect x="295" y="30" width="270" height="56" rx="8" fill="#161b22" stroke="#a371f7" stroke-width="1"/>
      <text x="430" y="52" text-anchor="middle" font-family="-apple-system,sans-serif" font-size="13" font-weight="600" fill="#a371f7">Frontend Client</text>
      <text x="430" y="68" text-anchor="middle" font-family="-apple-system,sans-serif" font-size="11" fill="#6e7681">Analyst UI / REST API consumer</text>
    </g>

    <!-- HTTP arrow down -->
    <line x1="430" y1="86" x2="430" y2="142" stroke="#444c56" stroke-width="1" marker-end="url(#arr)" stroke-dasharray="4 3"/>
    <rect x="376" y="104" width="108" height="18" rx="3" fill="#161b22"/>
    <text x="430" y="116" text-anchor="middle" font-family="-apple-system,sans-serif" font-size="10" fill="#6e7681">HTTP REST  :8080</text>

    <!-- JAVA -->
    <g id="nd-java" style="cursor:pointer" onclick="showInfo('java')">
      <rect x="200" y="142" width="460" height="76" rx="8" fill="#161b22" stroke="#e3b341" stroke-width="1"/>
      <text x="430" y="165" text-anchor="middle" font-family="-apple-system,sans-serif" font-size="13" font-weight="600" fill="#e3b341">Java Spring Boot — Orchestrator &amp; Gateway</text>
      <text x="280" y="186" text-anchor="middle" font-family="-apple-system,sans-serif" font-size="11" fill="#6e7681">ThreatController.java</text>
      <text x="430" y="186" text-anchor="middle" font-family="-apple-system,sans-serif" font-size="11" fill="#6e7681">RagOrchestrator.java</text>
      <text x="575" y="186" text-anchor="middle" font-family="-apple-system,sans-serif" font-size="11" fill="#6e7681">EvalHarness.java</text>
      <text x="280" y="202" text-anchor="middle" font-family="-apple-system,sans-serif" font-size="10" fill="#484f58">REST Gateway</text>
      <text x="430" y="202" text-anchor="middle" font-family="-apple-system,sans-serif" font-size="10" fill="#484f58">Pipeline Coordinator</text>
      <text x="575" y="202" text-anchor="middle" font-family="-apple-system,sans-serif" font-size="10" fill="#484f58">Benchmark Runner</text>
    </g>

    <!-- gRPC arrow left -->
    <line x1="330" y1="218" x2="240" y2="298" stroke="#444c56" stroke-width="1" marker-end="url(#arr)"/>
    <rect x="240" y="244" width="102" height="18" rx="3" fill="#161b22"/>
    <text x="291" y="256" text-anchor="middle" font-family="-apple-system,sans-serif" font-size="10" fill="#6e7681">gRPC :50051</text>

    <!-- HTTP arrow right -->
    <line x1="530" y1="218" x2="620" y2="298" stroke="#444c56" stroke-width="1" marker-end="url(#arr)" stroke-dasharray="4 3"/>
    <rect x="524" y="244" width="102" height="18" rx="3" fill="#161b22"/>
    <text x="575" y="256" text-anchor="middle" font-family="-apple-system,sans-serif" font-size="10" fill="#6e7681">HTTP/JSON :8000</text>

    <!-- CPP -->
    <g id="nd-cpp" style="cursor:pointer" onclick="showInfo('cpp')">
      <rect x="100" y="298" width="280" height="100" rx="8" fill="#161b22" stroke="#a371f7" stroke-width="1"/>
      <text x="240" y="320" text-anchor="middle" font-family="-apple-system,sans-serif" font-size="13" font-weight="600" fill="#a371f7">C++ Core Engine</text>
      <text x="150" y="342" text-anchor="middle" font-family="-apple-system,sans-serif" font-size="11" fill="#6e7681">embedder.cpp</text>
      <text x="240" y="342" text-anchor="middle" font-family="-apple-system,sans-serif" font-size="11" fill="#6e7681">faiss_index.cpp</text>
      <text x="330" y="342" text-anchor="middle" font-family="-apple-system,sans-serif" font-size="11" fill="#6e7681">reranker.cpp</text>
      <text x="150" y="358" text-anchor="middle" font-family="-apple-system,sans-serif" font-size="10" fill="#484f58">ONNX Embedder</text>
      <text x="240" y="358" text-anchor="middle" font-family="-apple-system,sans-serif" font-size="10" fill="#484f58">FAISS HNSW</text>
      <text x="330" y="358" text-anchor="middle" font-family="-apple-system,sans-serif" font-size="10" fill="#484f58">Cross-encoder</text>
      <text x="240" y="382" text-anchor="middle" font-family="-apple-system,sans-serif" font-size="10" fill="#484f58" font-style="italic">grpc_server.cpp · port 50051</text>
    </g>

    <!-- PYTHON -->
    <g id="nd-python" style="cursor:pointer" onclick="showInfo('python')">
      <rect x="480" y="298" width="280" height="100" rx="8" fill="#161b22" stroke="#58a6ff" stroke-width="1"/>
      <text x="620" y="320" text-anchor="middle" font-family="-apple-system,sans-serif" font-size="13" font-weight="600" fill="#58a6ff">Python ML Pipeline</text>
      <text x="555" y="342" text-anchor="middle" font-family="-apple-system,sans-serif" font-size="11" fill="#6e7681">rerank_onnx.py</text>
      <text x="685" y="342" text-anchor="middle" font-family="-apple-system,sans-serif" font-size="11" fill="#6e7681">llm_rag.py</text>
      <text x="555" y="358" text-anchor="middle" font-family="-apple-system,sans-serif" font-size="10" fill="#484f58">ms-marco ONNX</text>
      <text x="685" y="358" text-anchor="middle" font-family="-apple-system,sans-serif" font-size="10" fill="#484f58">Mistral-7B GGUF</text>
      <text x="620" y="382" text-anchor="middle" font-family="-apple-system,sans-serif" font-size="10" fill="#484f58" font-style="italic">llama-cpp-python · port 8000</text>
    </g>

    <!-- Vector search arrow -->
    <line x1="240" y1="398" x2="300" y2="438" stroke="#444c56" stroke-width="1" marker-end="url(#arr)"/>
    <rect x="220" y="412" width="104" height="18" rx="3" fill="#161b22"/>
    <text x="272" y="424" text-anchor="middle" font-family="-apple-system,sans-serif" font-size="10" fill="#6e7681">Vector search :19530</text>

    <!-- MILVUS -->
    <g id="nd-milvus" style="cursor:pointer" onclick="showInfo('milvus')">
      <rect x="240" y="432" width="380" height="36" rx="8" fill="#161b22" stroke="#39d353" stroke-width="1"/>
      <text x="430" y="451" text-anchor="middle" font-family="-apple-system,sans-serif" font-size="13" font-weight="600" fill="#39d353">Milvus Vector Database</text>
      <text x="430" y="462" text-anchor="middle" font-family="-apple-system,sans-serif" font-size="10" fill="#484f58">IVF+PQ  ·  384-dim  ·  10M+ vectors  ·  etcd + MinIO</text>
    </g>

    <!-- Python -> Milvus arrow -->
    <line x1="620" y1="398" x2="560" y2="438" stroke="#444c56" stroke-width="1" marker-end="url(#arr)" stroke-dasharray="4 3"/>
  </svg>

  <!-- Info box -->
  <div class="node-info" id="node-info">
    <div class="node-info-title">Click any node above to explore it</div>
    <div class="node-info-body">Each tier handles a distinct responsibility. The C++ engine owns latency-critical hot paths; Java orchestrates the pipeline; Python handles ML reasoning; Milvus stores and queries vectors at scale.</div>
  </div>
</div>

<!-- Tech Stack -->
<h2 id="tech">Tech Stack</h2>
<div class="stack">
  <div class="card purple">
    <div class="card-label" style="color:var(--purple)">C++ Core</div>
    <div class="card-title">High-Performance Engine</div>
    <div class="card-desc">ONNX Runtime embedder, FAISS HNSW index, cross-encoder reranker, gRPC server. Zero GC pauses.</div>
    <div class="card-tags"><span class="tag tag-p">C++17</span><span class="tag tag-p">FAISS</span><span class="tag tag-p">ONNX Runtime</span><span class="tag tag-p">gRPC</span></div>
  </div>
  <div class="card amber">
    <div class="card-label" style="color:var(--amber)">Java Services</div>
    <div class="card-title">Enterprise Orchestrator</div>
    <div class="card-desc">Spring Boot REST gateway, RAG coordinator with threshold gate, Milvus SDK, eval harness.</div>
    <div class="card-tags"><span class="tag tag-j">Java 21</span><span class="tag tag-j">Spring Boot</span><span class="tag tag-j">JNI</span><span class="tag tag-j">Maven</span></div>
  </div>
  <div class="card blue">
    <div class="card-label" style="color:var(--blue)">Python ML</div>
    <div class="card-title">Intelligence Layer</div>
    <div class="card-desc">Sliding-window ingestion, batch ONNX cross-encoder, llama.cpp Mistral-7B, eval metrics.</div>
    <div class="card-tags"><span class="tag tag-b">Python 3.11</span><span class="tag tag-b">llama.cpp</span><span class="tag tag-b">HuggingFace</span></div>
  </div>
  <div class="card teal">
    <div class="card-label" style="color:var(--teal)">Vector DBs</div>
    <div class="card-title">FAISS + Milvus</div>
    <div class="card-desc">FAISS HNSW locally (&lt;1M, &lt;5ms p99). Milvus IVF+PQ for 10M+ vectors. Swap via config flag.</div>
    <div class="card-tags"><span class="tag tag-t">FAISS HNSW</span><span class="tag tag-t">Milvus</span><span class="tag tag-t">IVF+PQ</span></div>
  </div>
  <div class="card coral">
    <div class="card-label" style="color:var(--coral)">LLM + Re-rank</div>
    <div class="card-title">Two-Stage Retrieval</div>
    <div class="card-desc">Bi-encoder ANN retrieves top-50; cross-encoder re-ranks to top-5; Mistral-7B generates citation-grounded answers.</div>
    <div class="card-tags"><span class="tag tag-c">Mistral-7B</span><span class="tag tag-c">Cross-Encoder</span><span class="tag tag-c">GGUF Q4</span></div>
  </div>
  <div class="card pink">
    <div class="card-label" style="color:var(--pink)">CI / Ops</div>
    <div class="card-title">GitHub Actions + Docker</div>
    <div class="card-desc">Matrix CI for C++/Java/Python. Docker Compose: Milvus + MinIO + etcd. `make all` cold-starts everything.</div>
    <div class="card-tags"><span class="tag" style="background:#200d15;color:#db61a2">GitHub Actions</span><span class="tag" style="background:#200d15;color:#db61a2">CMake</span><span class="tag" style="background:#200d15;color:#db61a2">Docker</span></div>
  </div>
</div>

<!-- Pipeline -->
<h2 id="pipeline">Real-Time Query Pipeline</h2>
<p>Every analyst query follows a strict execution-guaranteed pipeline. Latency targets are enforced per stage; the threshold gate prevents LLM calls when telemetry is sparse.</p>

<div id="pipeline-wrap">
  <div class="pipeline" id="pipe">
    <div class="pipe-step active" id="ps0" onclick="activatePipe(0)" style="cursor:pointer">1 · REST Query</div>
    <div class="pipe-arrow">→</div>
    <div class="pipe-step" id="ps1" onclick="activatePipe(1)" style="cursor:pointer">2 · gRPC Embed</div>
    <div class="pipe-arrow">→</div>
    <div class="pipe-step" id="ps2" onclick="activatePipe(2)" style="cursor:pointer">3 · ANN Search</div>
    <div class="pipe-arrow">→</div>
    <div class="pipe-step" id="ps3" onclick="activatePipe(3)" style="cursor:pointer">4 · Threshold Gate</div>
    <div class="pipe-arrow">→</div>
    <div class="pipe-step" id="ps4" onclick="activatePipe(4)" style="cursor:pointer">5 · Re-rank</div>
    <div class="pipe-arrow">→</div>
    <div class="pipe-step" id="ps5" onclick="activatePipe(5)" style="cursor:pointer">6 · LLM Generate</div>
    <div class="pipe-arrow">→</div>
    <div class="pipe-step" id="ps6" onclick="activatePipe(6)" style="cursor:pointer">7 · Validate</div>
  </div>
  <div class="node-info" id="pipe-info" style="margin-top:10px;">
    <div class="node-info-title">Step 1 — REST Query</div>
    <div class="node-info-body">Analyst POSTs to <code>/api/v1/threats/query</code> on Java Spring Boot port 8080. The RagOrchestrator picks up the request and begins the pipeline.</div>
  </div>
</div>

<!-- Folder Structure -->
<h2 id="structure">Folder Structure</h2>
<pre><span class="kw">ThreatAtlas/</span>
├── <span class="fn">cpp-core/</span>                   <span class="cm"># C++17, CMake, gRPC, ONNX</span>
│   ├── src/
│   │   ├── <span class="str">embedder.cpp</span>        <span class="cm"># ONNX Runtime sentence encoder</span>
│   │   ├── <span class="str">faiss_index.cpp</span>     <span class="cm"># FAISS HNSW build + query</span>
│   │   ├── <span class="str">reranker.cpp</span>        <span class="cm"># Cross-encoder ONNX inference</span>
│   │   ├── <span class="str">grpc_server.cpp</span>     <span class="cm"># gRPC service (port 50051)</span>
│   │   └── <span class="str">main.cpp</span>
│   ├── include/  proto/  tests/
│   └── <span class="str">CMakeLists.txt</span>
│
├── <span class="fn">java-services/</span>               <span class="cm"># Java 21, Spring Boot, Maven</span>
│   └── src/main/java/com/threatatlas/
│       ├── <span class="str">ThreatController.java</span>  <span class="cm"># REST /api/v1/threats/*</span>
│       ├── <span class="str">RagOrchestrator.java</span>   <span class="cm"># Full pipeline coordinator</span>
│       ├── <span class="str">MilvusService.java</span>     <span class="cm"># Milvus upsert + query</span>
│       ├── <span class="str">EvalHarness.java</span>       <span class="cm"># 5000-thread load tester</span>
│       └── <span class="str">GrpcClient.java</span>        <span class="cm"># Stub to C++ server</span>
│
├── <span class="fn">rag-llm/</span>                      <span class="cm"># Python 3.11</span>
│   ├── <span class="str">ingest.py</span>               <span class="cm"># Sliding-window chunker</span>
│   ├── <span class="str">embed_offline.py</span>        <span class="cm"># Batch embed → FAISS/Milvus</span>
│   ├── <span class="str">rerank_onnx.py</span>          <span class="cm"># Cross-encoder wrapper</span>
│   ├── <span class="str">llm_rag.py</span>              <span class="cm"># Mistral-7B + citation prompt</span>
│   └── <span class="str">eval_metrics.py</span>         <span class="cm"># Recall@K, MRR, NDCG</span>
│
├── <span class="fn">datasets/</span>  <span class="fn">benchmarks/</span>  <span class="fn">docs/</span>
├── <span class="str">docker-compose.yml</span>            <span class="cm"># Milvus + MinIO + etcd</span>
├── <span class="str">Makefile</span>                      <span class="cm"># make all / bench / clean</span>
└── <span class="str">README.md</span></pre>

<!-- Phase Roadmap -->
<h2 id="phases">Phase Roadmap</h2>
<div class="phases">
  <div class="phase">
    <div class="phase-dot" style="background:#1a1038;color:#a371f7;border-color:#a371f7">1</div>
    <div class="phase-body">
      <div class="phase-n">Phase 1 · Week 1</div>
      <div class="phase-t">Scaffold Repo & CI</div>
      <div class="phase-d">CMakeLists, pom.xml, GitHub Actions matrix (Ubuntu + MSVC), pre-commit hooks.</div>
      <div class="phase-ok">✓ CI green · badge in README</div>
    </div>
  </div>
  <div class="phase">
    <div class="phase-dot" style="background:#0d1526;color:#58a6ff;border-color:#58a6ff">2</div>
    <div class="phase-body">
      <div class="phase-n">Phase 2 · Week 2</div>
      <div class="phase-t">Ingestion & Chunking</div>
      <div class="phase-d">Sliding-window chunker (512 tokens, 128 stride), metadata schema, 1k+ sample chunks committed.</div>
      <div class="phase-ok">✓ /datasets populated with annotated JSONL</div>
    </div>
  </div>
  <div class="phase">
    <div class="phase-dot" style="background:#071d07;color:#39d353;border-color:#39d353">3</div>
    <div class="phase-body">
      <div class="phase-n">Phase 3 · Week 3</div>
      <div class="phase-t">Embedding & Local FAISS</div>
      <div class="phase-d">C++ ONNX embedder + FAISS HNSW. Query benchmark &lt;5 ms p99 at 1M vectors.</div>
      <div class="phase-ok">✓ embed_query binary → top-5 passages in stdout</div>
    </div>
  </div>
  <div class="phase">
    <div class="phase-dot" style="background:#1e1700;color:#e3b341;border-color:#e3b341">4</div>
    <div class="phase-body">
      <div class="phase-n">Phase 4 · Week 4–5</div>
      <div class="phase-t">Milvus Integration</div>
      <div class="phase-d">Docker Compose stack, Java SDK upsert pipeline, IVF+PQ schema, 10M+ vector scale.</div>
      <div class="phase-ok">✓ Java service returns Milvus top-k via gRPC</div>
    </div>
  </div>
  <div class="phase">
    <div class="phase-dot" style="background:#260d08;color:#f78166;border-color:#f78166">5</div>
    <div class="phase-body">
      <div class="phase-n">Phase 5 · Week 6</div>
      <div class="phase-t">Cross-Encoder Re-rank</div>
      <div class="phase-d">ONNX ms-marco-MiniLM re-ranks top-50 → top-5. MRR@10 gain ~8–12 pp over bi-encoder baseline.</div>
      <div class="phase-ok">✓ /benchmarks MRR comparison committed</div>
    </div>
  </div>
  <div class="phase">
    <div class="phase-dot" style="background:#200d15;color:#db61a2;border-color:#db61a2">6</div>
    <div class="phase-body">
      <div class="phase-n">Phase 6 · Week 7</div>
      <div class="phase-t">LLM Prompt + RAG Flow</div>
      <div class="phase-d">llama.cpp Mistral-7B-Q4, citation-enforced system prompt, gRPC end-to-end endpoint.</div>
      <div class="phase-ok">✓ query → grounded JSON answer with citations</div>
    </div>
  </div>
  <div class="phase">
    <div class="phase-dot" style="background:#161b22;color:#8b949e;border-color:#444c56">7</div>
    <div class="phase-body">
      <div class="phase-n">Phase 7 · Week 8</div>
      <div class="phase-t">Evaluation & Benchmarks</div>
      <div class="phase-d">Java EvalHarness: 5000-thread gRPC stress. Recall@K, MRR, NDCG over 100 labeled queries.</div>
      <div class="phase-ok">✓ Reproducible benchmark script + CSV results</div>
    </div>
  </div>
  <div class="phase">
    <div class="phase-dot" style="background:#161b22;color:#8b949e;border-color:#444c56">8</div>
    <div class="phase-body">
      <div class="phase-n">Phase 8 · Week 9</div>
      <div class="phase-t">Docs & Reproducibility</div>
      <div class="phase-d">Architecture diagrams, runbooks, datasets README, `make all` cold-start.</div>
      <div class="phase-ok">✓ Fully reproducible from scratch</div>
    </div>
  </div>
</div>

<!-- Port Matrix -->
<h2 id="ports">Port Matrix</h2>
<table>
  <thead><tr><th>Port</th><th>Service</th><th>Protocol</th><th>Description</th></tr></thead>
  <tbody>
    <tr><td>8080</td><td>Java Spring Boot</td><td>HTTP/REST</td><td>Public API gateway — <code>/api/v1/threats/*</code></td></tr>
    <tr><td>8000</td><td>Python ML Service</td><td>HTTP/JSON</td><td>Cross-encoder + LLM inference endpoint</td></tr>
    <tr><td>50051</td><td>C++ gRPC Server</td><td>gRPC/Protobuf</td><td>Embed, Query, Rerank RPCs</td></tr>
    <tr><td>19530</td><td>Milvus Standalone</td><td>gRPC</td><td>Vector search RPC</td></tr>
    <tr><td>9091</td><td>Milvus Metrics</td><td>HTTP</td><td>Prometheus metrics scrape</td></tr>
    <tr><td>9000</td><td>MinIO</td><td>HTTP</td><td>Object storage — Milvus segment files</td></tr>
    <tr><td>2379</td><td>etcd</td><td>HTTP</td><td>Milvus metadata store</td></tr>
  </tbody>
</table>

<!-- Quick Start -->
<h2 id="quickstart">Quick Start</h2>
<div class="tip">Models are large. Run download scripts in <code>rag-llm/models/</code> before starting.</div>
<pre><span class="cm"># 1. Clone and scaffold</span>
<span class="fn">git</span> clone https://github.com/your-org/ThreatAtlas.git
<span class="fn">cd</span> ThreatAtlas

<span class="cm"># 2. Start Milvus stack</span>
<span class="fn">docker compose</span> up -d etcd minio milvus

<span class="cm"># 3. Build C++ core</span>
<span class="fn">cmake</span> -B build -DCMAKE_BUILD_TYPE=Release -DCMAKE_CXX_FLAGS=<span class="str">"-O3 -march=native"</span>
<span class="fn">cmake</span> --build build --parallel $(nproc)

<span class="cm"># 4. Download models</span>
bash rag-llm/models/download_encoder.sh
bash rag-llm/models/download_llm.sh

<span class="cm"># 5. Ingest sample data</span>
pip install -r rag-llm/requirements.txt
python rag-llm/ingest.py --input datasets/sample_cve.jsonl
python rag-llm/embed_offline.py --backend milvus

<span class="cm"># 6. Start Java orchestrator</span>
<span class="fn">cd</span> java-services && mvn spring-boot:run

<span class="cm"># 7. Query</span>
<span class="fn">curl</span> -X POST http://localhost:8080/api/v1/threats/query \
  -H <span class="str">"Content-Type: application/json"</span> \
  -d <span class="str">'{"query": "CVE-2024-1234 lateral movement indicators"}'</span></pre>

<pre><span class="cm"># Or: run everything in one command</span>
<span class="fn">make</span> all</pre>

<!-- Benchmarks -->
<h2 id="benchmarks">Benchmarks</h2>
<p>All benchmark results are committed to <code>/benchmarks/</code> as CSV for git-trackable regression detection.</p>
<table>
  <thead><tr><th>Metric</th><th>Bi-encoder only</th><th>+ Cross-encoder</th><th>Delta</th></tr></thead>
  <tbody>
    <tr><td>Recall@1</td><td>0.51</td><td>0.67</td><td style="color:var(--green)">+16 pp</td></tr>
    <tr><td>Recall@5</td><td>0.74</td><td>0.88</td><td style="color:var(--green)">+14 pp</td></tr>
    <tr><td>MRR@10</td><td>0.58</td><td>0.69</td><td style="color:var(--green)">+11 pp</td></tr>
    <tr><td>ANN latency p99</td><td>4.8 ms</td><td>—</td><td>—</td></tr>
    <tr><td>Re-rank latency p99</td><td>—</td><td>78 ms</td><td>—</td></tr>
    <tr><td>End-to-end p99 (LLM)</td><td>—</td><td>2.3 s</td><td>—</td></tr>
  </tbody>
</table>

<!-- Docs -->
<h2 id="docs">Documentation</h2>
<div class="stack" style="grid-template-columns:repeat(auto-fit,minmax(160px,1fr))">
  <div class="card blue" style="padding:12px 14px">
    <div class="card-title" style="font-size:13px">architecture.md</div>
    <div class="card-desc">Full system design, algorithms, data flow</div>
  </div>
  <div class="card teal" style="padding:12px 14px">
    <div class="card-title" style="font-size:13px">runbook_milvus.md</div>
    <div class="card-desc">Milvus schema, Docker Compose, migration</div>
  </div>
  <div class="card coral" style="padding:12px 14px">
    <div class="card-title" style="font-size:13px">runbook_rag.md</div>
    <div class="card-desc">LLM prompt contract, citation enforcement</div>
  </div>
  <div class="card purple" style="padding:12px 14px">
    <div class="card-title" style="font-size:13px">runbook_eval.md</div>
    <div class="card-desc">Benchmark harness, metric formulas</div>
  </div>
  <div class="card amber" style="padding:12px 14px">
    <div class="card-title" style="font-size:13px">datasets/README.md</div>
    <div class="card-desc">Schema, license, data provenance</div>
  </div>
</div>

<div class="divider" style="margin-top:40px"></div>
<p style="font-size:12px;color:var(--text3);text-align:center">ThreatAtlas · MIT License · See <a href="docs/architecture.md" style="color:var(--blue)">docs/architecture.md</a> for full technical detail</p>

<script>
const nodeData = {
  client: {
    title: 'Frontend Client — Analyst UI',
    body: 'Consumes the Java REST gateway over HTTP on port 8080. Submits threat queries as JSON POST requests and receives structured incident reports with citation arrays. Can be any HTTP client — browser, curl, or automated pipeline.',
    tags: ['HTTP REST', 'Port 8080', 'JSON']
  },
  java: {
    title: 'Java Spring Boot — Orchestrator & Gateway',
    body: 'The structural backbone. ThreatController.java exposes /api/v1/threats/* endpoints. RagOrchestrator.java coordinates the full pipeline: calls C++ via gRPC for embedding, checks relevance thresholds, forwards to Python for re-rank and LLM generation, then validates citation arrays before returning. EvalHarness.java runs 5000-thread load tests.',
    tags: ['Java 21', 'Spring Boot 3.x', 'Maven', 'Port 8080']
  },
  cpp: {
    title: 'C++ Core Engine — Bare-Metal Compute',
    body: 'Zero GC pauses. embedder.cpp runs the all-MiniLM-L6-v2 ONNX model via OrtSession to produce 384-d L2-normalized vectors in <3 ms. faiss_index.cpp manages the HNSW index (M=32, efSearch=64) for <5 ms p99 ANN queries at 1M vectors. reranker.cpp provides an optional ONNX cross-encoder tight path. grpc_server.cpp exposes Embed, Query, Rerank RPCs on port 50051.',
    tags: ['C++17', 'ONNX Runtime', 'FAISS HNSW', 'gRPC', 'Port 50051']
  },
  python: {
    title: 'Python ML Pipeline — Intelligence Hub',
    body: 'rerank_onnx.py batch-tokenizes (query, passage) pairs and scores them through the ms-marco-MiniLM-L-6-v2 ONNX cross-encoder, re-ranking top-50 → top-5. llm_rag.py runs Mistral-7B-Q4_K_M via llama-cpp-python with a citation-enforced system prompt that rejects answers without SOURCE_N references. Serves on port 8000 over HTTP/JSON.',
    tags: ['Python 3.11', 'llama.cpp', 'ONNX', 'Mistral-7B', 'Port 8000']
  },
  milvus: {
    title: 'Milvus Vector Database — Distributed Vector Store',
    body: 'Handles 10M+ vectors with IVF+PQ index (nlist=4096, m=48, nprobe=64). Collection schema: 384-dim FLOAT_VECTOR + source, chunk_text, timestamp, severity fields. Docker Compose stack: Milvus standalone + MinIO (segment storage) + etcd (metadata). Java SDK drives async upsert pipeline. Switchable from FAISS via VECTOR_BACKEND env flag.',
    tags: ['Milvus 2.x', 'IVF+PQ', '384-dim', 'Port 19530', 'Docker']
  }
};

function showInfo(key) {
  const d = nodeData[key];
  const box = document.getElementById('node-info');
  box.querySelector('.node-info-title').textContent = d.title;
  box.querySelector('.node-info-body').textContent = d.body;
  let tags = box.querySelector('.node-info-tags');
  if (!tags) { tags = document.createElement('div'); tags.className = 'node-info-tags'; box.appendChild(tags); }
  tags.innerHTML = d.tags.map(t => `<span class="tag tag-b">${t}</span>`).join('');
  document.querySelectorAll('[id^="nd-"]').forEach(n => n.style.opacity = '0.5');
  document.getElementById('nd-' + key).style.opacity = '1';
}

const pipeData = [
  { title: 'Step 1 — REST Query', body: 'Analyst POSTs to /api/v1/threats/query on Java Spring Boot port 8080. The RagOrchestrator picks up the request and begins the pipeline.' },
  { title: 'Step 2 — gRPC Embed (C++)', body: 'Java calls the C++ gRPC server (port 50051). The ONNX Runtime runs all-MiniLM-L6-v2 to produce a 384-d L2-normalized float32 vector in under 3 ms.' },
  { title: 'Step 3 — ANN Search (FAISS/Milvus)', body: 'The 384-d query vector is used for approximate nearest neighbour search. FAISS HNSW returns top-50 candidate passages in under 5 ms p99. Milvus IVF+PQ handles 10M+ vector corpora.' },
  { title: 'Step 4 — Relevance Threshold Gate', body: 'Java checks all 50 distances against a minimum threshold. If all candidates are below the threshold (no relevant data found), the pipeline short-circuits and returns an "insufficient_data" response — preventing hallucinated LLM answers.' },
  { title: 'Step 5 — Cross-Encoder Re-rank', body: 'Python\'s rerank_onnx.py scores each (query, passage) pair through the ms-marco-MiniLM-L-6-v2 ONNX cross-encoder. Top-50 → top-5 most relevant passages. MRR@10 improves ~8–12 pp over bi-encoder alone.' },
  { title: 'Step 6 — LLM Generation', body: 'Python\'s llm_rag.py injects the top-5 passages as numbered context into Mistral-7B-Q4 via llama-cpp-python. The system prompt enforces a JSON response schema requiring SOURCE_N citations for every factual claim.' },
  { title: 'Step 7 — Citation Validation', body: 'Java\'s RagOrchestrator validates the returned JSON: schema check + citation array present + all SOURCE_N IDs match retrieved passages. Answers failing validation are rejected. The structured incident report is returned to the client.' }
];

function activatePipe(idx) {
  for (let i = 0; i < 7; i++) {
    const el = document.getElementById('ps' + i);
    el.classList.toggle('active', i === idx);
    el.style.borderColor = i === idx ? 'var(--purple)' : '';
    el.style.color = i === idx ? 'var(--purple)' : '';
  }
  const info = document.getElementById('pipe-info');
  info.querySelector('.node-info-title').textContent = pipeData[idx].title;
  info.querySelector('.node-info-body').textContent = pipeData[idx].body;
}
</script>
</body>
</html>
