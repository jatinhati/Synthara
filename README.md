# Synthara - AI Research Analyst Platform

Synthara is an enterprise-grade multi-agent AI Research Analyst platform with semantic memory, built with Spring Boot and the [Embabel Agent Framework](https://repo.embabel.com). Give it a topic or target domain, and it executes a comprehensive 8-stage research, analysis, and report generation pipeline—using both MCP-based web search and custom Java analysis tools along the way, storing findings in a semantic vector memory and exporting high-quality reports.

## Features

- **8-Stage Autonomous Workflow**: Fully automated research pipeline that runs through:
  1. **Topic Research**: Web research via Tavily Search.
  2. **Source Extraction**: Structured metadata extraction from raw sources.
  3. **Competitor Analysis**: Identifying players, market shares, and strategies.
  4. **SWOT Analysis**: Creating structured SWOT matrix.
  5. **Executive Summary**: Synthesizing consulting-grade analysis.
  6. **Report Generation**: Generating detailed Markdown report.
  7. **PDF Export**: Exporting to clean publication-ready format.
  8. **Semantic Storage**: Indexing reports in PostgreSQL (with `pgvector`) for future context retrieval.
- **Local & Fast**: Configured to run fully locally using **Ollama** (`qwen2.5:3b`) for both drafting and reviewing/analysis.
- **MCP Tool Integration**: Leverages Model Context Protocol (MCP) using the Tavily Search server.
- **Semantic Memory**: High-performance vector embeddings and search using PostgreSQL `pgvector` and Redis caching.

---

## Quick Start

### 1. Start Infrastructure

Use the provided Docker Compose file to boot PostgreSQL (with pgvector extension enabled) and Redis:

```bash
docker-compose up -d
```

### 2. Configure Environment

Create a `.env` file or export the following environment variables:

```bash
# Target the local Ollama instance (defaults to http://localhost:11434)
export OLLAMA_BASE_URL=http://localhost:11434

# Tavily API Key for web searches
export TAVILY_API_KEY=your-tavily-key
```

### 3. Run the Platform

Boot the interactive shell using Maven:

```bash
./mvnw spring-boot:run
```

The application launches an interactive shell. Type `x "your research topic"` to kick off the autonomous multi-agent analyst!

---

## Configuration

Configuration is located in `src/main/resources/application.yaml`:

| Property | Default | Description |
|---|---|---|
| `OLLAMA_BASE_URL` | `http://localhost:11434` | Ollama connection endpoint |
| `TAVILY_API_KEY` | — | Tavily Search API key (env variable) |
| `synthara.research.output-dir` | `reports` | Directory where finished reports are saved |
| `synthara.research.max-sources` | `50` | Maximum source citations gathered |
| `synthara.research.parallel-research` | `true` | Enable parallel phase analysis |
| `embabel.models.default-llm` | `qwen2.5:3b` | Local Ollama model for analyst phases |
| `embabel.models.llms.reviewer` | `qwen2.5:3b` | Local Ollama model for SWOT & review phases |

---

## How It Works

`ResearchAnalystAgent` defines the core agent actions. The agent relies on a `ResearchWorkflow` orchestrator to pass the state through the stages:

### Tool Integration

- **MCP Tools**: Web queries are powered by the Tavily Search MCP server configured under `spring.ai.mcp.client.stdio.connections`. The server is launched on demand via Node.js/`npx`.
- **Custom Java Tools**:
  - `CitationFormatterTool` — Custom Spring `@Component` annotated with `@LlmTool` to dynamically format source citations (APA/MLA style).
  - `ReportExportTool` — Utility tool to export reports to disk.

---

## Shell Commands

The application boots in an interactive Spring Shell. Type `help` to see all available commands. The most useful commands are:

| Command | Description |
|---|---|
| `x <topic>` | Execute the research agent with a given topic (e.g., `x "Artificial Intelligence in Healthcare"`) |
| `x -p <topic>` | Run and print the raw prompts sent to Ollama |
| `agents` | List all registered agents |
| `actions` | List all available agent actions |
| `models` | List configured language models |
| `blackboard` / `bb` | Inspect active workflow states / working memory |
| `clear` | Clear the active blackboard |

---

## Tech Stack

- **Spring Boot 3.5**
- **Embabel Agent Framework 0.4.0**
- **Spring AI 1.1.4**
- **PostgreSQL & pgvector**
- **Redis**
- **Ollama (`qwen2.5:3b`)**
- **Java 23**
