# Synthara Research Analyst

Synthara is an enterprise-grade AI research analyst platform that orchestrates a multi‑stage research workflow to produce executive-ready market intelligence. It combines Embabel’s agent framework with Spring Boot, tool‑augmented LLMs, semantic memory, and report export pipelines.

## Key Features

- **Multi‑stage research workflow**: research → source extraction → competitor analysis → SWOT → executive summary → report generation → export → semantic memory storage.
- **Tool‑augmented analysis** with custom LLM tools for:
  - duplicate source detection
  - source credibility scoring
  - trend analysis
  - citation formatting
  - similarity search across prior research
  - report export utilities
- **Semantic memory** for storing and recalling prior research context.
- **Report export** to Markdown and optional PDF (via Pandoc + XeLaTeX).
- **Caching & progress tracking** backed by Redis.
- **MCP-based web research** via the Brave Search MCP server.
- **Containerized stack** with PostgreSQL + pgvector and Redis via Docker Compose.

## Architecture Overview

```
User Input
   │
   ▼
ResearchAnalystAgent (Embabel)
   │
   ▼
ResearchWorkflow
   ├─ ResearchTopicStage
   ├─ SourceExtractionStage
   ├─ CompetitorAnalysisStage
   ├─ SwotAnalysisStage
   ├─ ExecutiveSummaryStage
   ├─ ReportGenerationStage
   ├─ PdfExportStage
   └─ SemanticMemoryStorageStage
   │
   ▼
Outputs: Markdown/PDF report + semantic memory
```

### Core Modules

| Module | Purpose |
|---|---|
| `dev.synthara.research.agents` | Embabel agent entry point (`ResearchAnalystAgent`) |
| `dev.synthara.research.workflow` | Deterministic research pipeline and workflow state |
| `dev.synthara.research.tools` | Custom `@LlmTool` utilities (credibility, trends, citations, export, similarity) |
| `dev.synthara.research.memory` | Redis-backed caching and progress tracking |
| `dev.synthara.research.vectorstore` | Semantic memory store (in‑memory placeholder) |
| `dev.synthara.research.records` | Immutable record types for workflow data |

## Workflow Stages

1. **Research Topic** — gathers sources for the topic and synthesizes an initial summary.
2. **Source Extraction** — deduplicates sources, filters low‑quality items, ranks by credibility.
3. **Competitor Analysis** — builds competitive profiles and positioning.
4. **SWOT Analysis** — generates strengths, weaknesses, opportunities, threats, and recommendation.
5. **Executive Summary** — executive‑level narrative, findings, risks, opportunities.
6. **Report Generation** — builds a structured Markdown report.
7. **PDF Export** — optionally converts Markdown to PDF (requires Pandoc + XeLaTeX).
8. **Semantic Storage** — stores key insights for future retrieval.

## Prerequisites

- **Java 23**
- **Maven** (or use `./mvnw` / `mvnw.cmd`)
- **Node.js / npx** (for Brave Search MCP server)
- **PostgreSQL + pgvector** (Docker Compose recommended)
- **Redis**
- **API keys**:
  - `OPENAI_API_KEY`
  - `ANTHROPIC_API_KEY`
  - `BRAVE_API_KEY`

## Quick Start (Local)

```bash
export OPENAI_API_KEY=your-openai-key
export ANTHROPIC_API_KEY=your-anthropic-key
export BRAVE_API_KEY=your-brave-key

# Optional (if running Postgres/Redis locally)
export DB_USERNAME=postgres
export DB_PASSWORD=postgres
export REDIS_PASSWORD=

./mvnw spring-boot:run
```

## Quick Start (Docker)

```bash
docker compose up --build
```

This starts:
- PostgreSQL + pgvector (`init-db.sh` enables the `vector` extension)
- Redis
- Synthara app on port **8080**

## Configuration

Configuration is in `src/main/resources/application.yaml`.

### Synthara Settings

| Property | Default | Description |
|---|---|---|
| `synthara.research.output-dir` | `reports` | Report output directory |
| `synthara.research.max-sources` | `50` | Maximum sources to retain |
| `synthara.research.parallel-research` | `true` | Enable parallel execution |
| `synthara.research.cache-ttl-hours` | `24` | Cache TTL (hours) |

### Data Stores

| Property | Default | Description |
|---|---|---|
| `spring.datasource.url` | `jdbc:postgresql://localhost:5432/synthara_research` | Postgres connection |
| `spring.data.redis.host` | `localhost` | Redis host |
| `spring.data.redis.port` | `6379` | Redis port |

### MCP + LLM Models

| Property | Default | Description |
|---|---|---|
| `spring.ai.mcp.client.stdio.connections.brave-search-mcp` | — | Brave Search MCP config |
| `embabel.models.default-llm` | `claude-sonnet-4-6` | Default LLM |
| `embabel.models.llms.reviewer` | `claude-opus-4-6` | Reviewer LLM |

## Outputs

- **Markdown report** written to `reports/`.
- **PDF report** generated when Pandoc + XeLaTeX are available.
- **Semantic memory** stored for similarity retrieval (currently in‑memory; ready for pgvector integration).

## Tooling & LLM Utilities

Synthara exposes a suite of `@LlmTool` utilities for richer analysis:

- **DuplicateSourceDetectorTool** — dedupe and similarity checks
- **SourceCredibilityTool** — credibility scoring (0–100)
- **TrendAnalysisTool** — trend and signal analysis
- **CitationFormatterTool** — APA/MLA citation formatting
- **SimilaritySearchTool** — semantic search of past reports
- **ReportExportTool** — export Markdown files

## Development & Testing

Run tests with:

```bash
./mvnw test
```

> Note: builds require access to the Embabel snapshot repository (`https://repo.embabel.com/artifactory/libs-snapshot`).

## Troubleshooting

- **PDF export missing**: install `pandoc` and `xelatex`.
- **Dependency resolution failures**: ensure `repo.embabel.com` is reachable.
- **Brave Search errors**: verify `BRAVE_API_KEY` and `npx` availability.

## License

This project is provided as-is. Add your preferred license here.
