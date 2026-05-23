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

## File Structure

```
Synthara/
├── src/main/
│   ├── java/dev/synthara/research/
│   │   ├── agents/                    # Embabel agent entry point and coordination
│   │   │   └── ResearchAnalystAgent.java
│   │   ├── workflow/                  # Deterministic research pipeline stages
│   │   │   ├── ResearchWorkflow.java         # Main orchestrator
│   │   │   ├── ResearchWorkflowState.java    # State container for workflow
│   │   │   ├── ResearchTopicStage.java       # Stage 1: gather and synthesize
│   │   │   ├── SourceExtractionStage.java    # Stage 2: dedup, filter, rank
│   │   │   ├── CompetitorAnalysisStage.java  # Stage 3: competitive profiles
│   │   │   ├── SwotAnalysisStage.java        # Stage 4: SWOT generation
│   │   │   ├── ExecutiveSummaryStage.java    # Stage 5: executive narrative
│   │   │   ├── ReportGenerationStage.java    # Stage 6: markdown report
│   │   │   ├── PdfExportStage.java           # Stage 7: PDF conversion
│   │   │   └── SemanticMemoryStorageStage.java # Stage 8: memory store
│   │   ├── tools/                    # LLM-accessible tools for agents
│   │   │   ├── DuplicateSourceDetectorTool.java      # Detect similar sources
│   │   │   ├── SourceCredibilityTool.java            # Score source credibility
│   │   │   ├── TrendAnalysisTool.java                # Analyze trends & signals
│   │   │   ├── CitationFormatterTool.java            # Format citations
│   │   │   ├── SimilaritySearchTool.java             # Search semantic memory
│   │   │   └── ReportExportTool.java                 # Export utilities
│   │   ├── records/                  # Immutable data types for workflow
│   │   │   ├── ResearchedTopic.java           # Research input & sources
│   │   │   ├── ResearchSource.java            # Individual source record
│   │   │   ├── StructuredSources.java         # Deduplicated & ranked sources
│   │   │   ├── CompetitorAnalysis.java        # Competitor profiles
│   │   │   ├── SwotAnalysis.java              # SWOT findings
│   │   │   ├── ExecutiveSummary.java          # Executive narrative
│   │   │   ├── MarkdownReport.java            # Generated markdown
│   │   │   ├── PublishedReport.java           # PDF metadata
│   │   │   └── StoredResearchMemory.java      # Semantic memory record
│   │   ├── memory/                   # Caching and state management
│   │   │   ├── ResearchCacheManager.java      # Redis-backed cache
│   │   │   └── ResearchExecutionManager.java  # Execution progress tracking
│   │   ├── vectorstore/              # Semantic search backend
│   │   │   └── SemanticMemoryStore.java       # Embedding store (pgvector)
│   │   ├── config/                   # Spring configuration
│   │   │   ├── ResearchConfig.java            # Main research config
│   │   │   ├── ResearchAsyncConfig.java       # Async executor config
│   │   │   └── ResearchRedisConfig.java       # Redis connection config
│   │   ├── exporters/                # Output generation
│   │   │   └── ReportExporter.java            # Markdown → PDF converter
│   │   └── personas/                 # Role definitions
│   │       └── ResearchPersonas.java          # Researcher personas
│   └── resources/
│       ├── application.yaml           # Main configuration file
│       └── templates/                 # Report templates (if any)
├── src/test/java/                     # Unit and integration tests
├── docker-compose.yml                 # PostgreSQL + Redis stack
├── Dockerfile                         # Application container
├── init-db.sh                         # Database initialization script
├── pom.xml                            # Maven project configuration
└── README.md                          # This file
```

## Detailed Architecture

### System Architecture Diagram

```
┌─────────────────────────────────────────────────────────────────┐
│                    Synthara Research Platform                    │
└─────────────────────────────────────────────────────────────────┘
                              ▲
                              │ (REST API)
                              │
                    ┌─────────▼──────────┐
                    │  Spring Boot App   │
                    │   (Port 8080)      │
                    └──────────┬─────────┘
                              │
        ┌─────────────────────┼─────────────────────┐
        │                     │                     │
        ▼                     ▼                     ▼
    ┌────────────┐      ┌──────────────┐      ┌─────────────┐
    │ PostgreSQL │      │    Redis     │      │ Embabel MCP │
    │ + pgvector │      │   (Cache)    │      │  (Brave)    │
    └────────────┘      └──────────────┘      └─────────────┘
        ▲                                            ▲
        │ (semantic                                 │ (web search
        │ memory)                                   queries)
        │                                           │
        └───────────────────────────────────────────┘
```

### Research Workflow Architecture

```
Input: Research Topic
      │
      ▼
┌─────────────────────────────────────────────────────────┐
│ ResearchAnalystAgent (Embabel Entry Point)              │
│  └─ Coordinates workflow execution                      │
└─────────────────────┬───────────────────────────────────┘
                      │
                      ▼
           ┌──────────────────────┐
           │  ResearchWorkflow    │
           │ (Stage Orchestrator) │
           └──────────┬───────────┘
                      │
        ┌─────────────┴─────────────┬──────────────┬──────────────┐
        │                           │              │              │
        ▼                           ▼              ▼              ▼
    Stage 1             Stage 2              Stage 3          Stage 4
  Research Topic     Source Extract      Competitor        SWOT
   [Topics]          Analysis           Analysis          Analysis
     │                  │                  │                 │
     │ (sources)        │ (ranked)         │ (profiles)      │ (SWOT)
     │                  │                  │                 │
     └──────────────────┴──────────────────┴─────────────────┤
                                                             │
                                                             ▼
                                                        Stage 5
                                                  Executive Summary
                                                       │
                                                       │ (narrative)
                                                       ▼
                                                    Stage 6
                                                  Report Gen
                                                   [Markdown]
                                                       │
                    ┌──────────────────────────────────┼──────────────────┐
                    │                                  │                  │
                    ▼                                  ▼                  ▼
                  Stage 7                        Stage 8             Report Output
                  PDF Export              Semantic Memory Storage   [Markdown/PDF]
                  [PDF]                   [pgvector]
```

### Component Interaction Diagram

```
┌─────────────────────────────────────┐
│ ResearchAnalystAgent                │ ◄─── User Query
│ (Embabel Orchestrator)              │
└─────────────────┬───────────────────┘
                  │
                  ├─────────┬──────────────────┐
                  │         │                  │
                  ▼         ▼                  ▼
        ┌──────────────┐  ┌───────┐  ┌─────────────────┐
        │ Workflow     │  │Tools  │  │ Personas        │
        │ Stages       │  │       │  │ (Researchers)   │
        └──────────────┘  │  ├─ DuplicateDetector
                          │  ├─ CredibilityScorer
                  ┌───────┤  ├─ TrendAnalyzer
                  │       │  ├─ CitationFormatter
                  │       │  ├─ SimilaritySearch
                  ▼       │  └─ ReportExporter
        ┌──────────────┐  └───────┘
        │Records (Data │
        │ Types)       │
        └──────────────┘
             ▲
             │
        ┌────┴────────────────────┐
        │                         │
        ▼                         ▼
   ┌─────────────┐        ┌──────────────┐
   │ResearchCache│        │SemanticMemory│
   │(Redis)      │        │Store(pgvector)
   └─────────────┘        └──────────────┘
```

### Data Flow Through Stages

```
┌────────────────────────────┐
│ ResearchRequest            │
│ {                          │
│   topic: string            │
│   context: string (opt)    │
│ }                          │
└────────────┬───────────────┘
             │
             ▼
┌────────────────────────────┐
│ Stage 1: ResearchTopic     │
│ Input: ResearchRequest     │
│ Output: ResearchedTopic    │
│ - Executes web search      │
│ - Synthesizes initial data │
│ - Returns: sources, summary│
└────────────┬───────────────┘
             │
             ▼
┌────────────────────────────┐
│ Stage 2: SourceExtraction  │
│ Input: ResearchedTopic     │
│ Output: StructuredSources  │
│ - Deduplicates sources     │
│ - Scores credibility       │
│ - Filters by quality       │
│ - Returns: ranked sources  │
└────────────┬───────────────┘
             │
             ▼
┌────────────────────────────┐
│ Stage 3: CompetitorAnalysis│
│ Input: StructuredSources   │
│ Output: CompetitorAnalysis │
│ - Identifies competitors   │
│ - Builds profiles          │
│ - Analyzes positioning     │
└────────────┬───────────────┘
             │
             ▼
┌────────────────────────────┐
│ Stage 4: SwotAnalysis      │
│ Input: CompetitorAnalysis  │
│ Output: SwotAnalysis       │
│ - Strengths/Weaknesses     │
│ - Opportunities/Threats    │
│ - Recommendations          │
└────────────┬───────────────┘
             │
             ▼
┌────────────────────────────┐
│ Stage 5: ExecutiveSummary  │
│ Input: SwotAnalysis        │
│ Output: ExecutiveSummary   │
│ - Executive narrative      │
│ - Key findings             │
│ - Risk/opportunity summary │
└────────────┬───────────────┘
             │
             ▼
┌────────────────────────────┐
│ Stage 6: ReportGeneration  │
│ Input: ExecutiveSummary    │
│ Output: MarkdownReport     │
│ - Structured markdown      │
│ - Formatted sections       │
│ - Ready for export         │
└────────────┬───────────────┘
             │
        ┌────┴────────────────┐
        │                     │
        ▼                     ▼
 ┌─────────────┐      ┌──────────────┐
 │ Stage 7:    │      │ Stage 8:     │
 │ PdfExport   │      │ SemanticMem  │
 │             │      │ Storage      │
 │Input:Markdown│      │              │
 │Output: PDF  │      │Stores vectors│
 └─────────────┘      │for future    │
                      │retrieval     │
                      └──────────────┘
                            │
                            ▼
                      ┌──────────────┐
                      │ Final Output │
                      │ - PDF report │
                      │ - Semantic   │
                      │   memory     │
                      └──────────────┘
```

## Module Details

### Agents Module (`dev.synthara.research.agents`)
- **ResearchAnalystAgent**: Entry point that uses the Embabel agent framework to orchestrate the research workflow. Manages tool invocations and workflow progression.

### Workflow Module (`dev.synthara.research.workflow`)
- **ResearchWorkflow**: Main orchestrator that executes stages sequentially and manages state transitions.
- **ResearchWorkflowState**: Container holding all stage outputs and intermediate data.
- **Stage Classes**: Eight specialized stages (ResearchTopic, SourceExtraction, CompetitorAnalysis, SwotAnalysis, ExecutiveSummary, ReportGeneration, PdfExport, SemanticMemoryStorage).

### Tools Module (`dev.synthara.research.tools`)
Tools are exposed via `@LlmTool` annotation for LLM access:
- **DuplicateSourceDetectorTool**: Identifies and deduplicates similar sources using similarity metrics.
- **SourceCredibilityTool**: Scores sources 0-100 based on domain authority, publication date, and other factors.
- **TrendAnalysisTool**: Identifies trends, patterns, and signals in research data.
- **CitationFormatterTool**: Formats citations in APA, MLA, Chicago styles.
- **SimilaritySearchTool**: Searches semantic memory for similar past research using embeddings.
- **ReportExportTool**: Exports markdown to various formats.

### Records Module (`dev.synthara.research.records`)
Immutable data containers representing workflow state:
- **ResearchedTopic**: Initial research with sources and synthesis.
- **ResearchSource**: Individual source record with metadata.
- **StructuredSources**: Deduplicated and ranked sources.
- **CompetitorAnalysis**: Competitive profiles and positioning.
- **SwotAnalysis**: Strengths, weaknesses, opportunities, threats, and recommendations.
- **ExecutiveSummary**: Executive-level findings and narrative.
- **MarkdownReport**: Final structured report.
- **PublishedReport**: PDF metadata and export details.
- **StoredResearchMemory**: Embedding and semantic memory records.

### Memory Module (`dev.synthara.research.memory`)
- **ResearchCacheManager**: Manages caching via Redis for performance optimization.
- **ResearchExecutionManager**: Tracks execution progress and state.

### Vectorstore Module (`dev.synthara.research.vectorstore`)
- **SemanticMemoryStore**: In-memory implementation with ready pgvector integration for similarity search.

### Config Module (`dev.synthara.research.config`)
- **ResearchConfig**: Main application configuration and bean definitions.
- **ResearchAsyncConfig**: Async executor configuration for parallel execution.
- **ResearchRedisConfig**: Redis connection and serialization configuration.

### Exporters Module (`dev.synthara.research.exporters`)
- **ReportExporter**: Converts markdown reports to PDF using Pandoc and XeLaTeX.

### Personas Module (`dev.synthara.research.personas`)
- **ResearchPersonas**: Defines system prompts and personas for different researcher roles.

## Integration Points

### External Services

#### OpenAI & Anthropic LLMs
- Default: Claude Sonnet 4.6
- Reviewer: Claude Opus 4.6
- Configured via Spring AI
- API keys: `OPENAI_API_KEY`, `ANTHROPIC_API_KEY`

#### Brave Search MCP
- Web search integration via Model Context Protocol (MCP)
- Stdio-based connection to MCP server
- Returns real-time search results for research topics
- Configured via `spring.ai.mcp.client.stdio.connections.brave-search-mcp`
- Requires: `BRAVE_API_KEY`

#### PostgreSQL + pgvector
- Primary data store with vector extension
- Semantic embeddings for similarity search
- Connection: `spring.datasource.url`
- Vector table for StoredResearchMemory records

#### Redis
- Caching layer for performance
- Progress tracking across workflow stages
- TTL-based expiration (configurable)
- Connection: `spring.data.redis.host`, `spring.data.redis.port`

### API Endpoints
Primary interaction via agent framework. REST endpoints managed by Spring Boot auto-configuration:
- Standard Spring Boot endpoints (health, metrics, etc.)
- Custom endpoints for research submission and report retrieval (if configured)

## Deployment Architectures

### Local Development
```
Host Machine
├─ JVM (Port 8080)
├─ PostgreSQL (Port 5432)
└─ Redis (Port 6379)
```

### Docker Compose (Recommended for Testing)
```
docker-compose.yml orchestrates:
├─ synthara (Spring Boot app) → Port 8080
├─ postgres (PostgreSQL + pgvector) → Port 5432
└─ redis (Redis) → Port 6379
```

### Production Deployment
Recommended Kubernetes setup:
```
├─ Deployment: Synthara (Spring Boot)
├─ StatefulSet: PostgreSQL with pgvector
├─ StatefulSet: Redis
├─ ConfigMap: application.yaml
├─ Secret: API keys (OPENAI_API_KEY, etc.)
└─ Service: Synthara (LoadBalancer)
```

## Workflow Execution Flow

1. **User submits research topic** → ResearchAnalystAgent receives request
2. **Stage 1 (ResearchTopic)** → Web search via Brave MCP, initial synthesis
3. **Stage 2 (SourceExtraction)** → Dedup sources, score credibility, rank quality
4. **Stage 3 (CompetitorAnalysis)** → Identify competitors, build profiles
5. **Stage 4 (SwotAnalysis)** → Analyze strengths, weaknesses, opportunities, threats
6. **Stage 5 (ExecutiveSummary)** → Generate executive narrative
7. **Stage 6 (ReportGeneration)** → Compile structured markdown report
8. **Stage 7 (PdfExport)** → Convert to PDF (optional, requires Pandoc)
9. **Stage 8 (SemanticMemoryStorage)** → Store embeddings in pgvector for future retrieval
10. **Output** → Report files (markdown/PDF) + semantic memory indexed

## Caching & Performance

### Redis Caching
- Research results cached with TTL (default: 24 hours)
- Intermediate stage outputs cached for recovery
- Progress tracking for long-running workflows
- Configuration: `synthara.research.cache-ttl-hours`

### Parallel Execution
- Stages can execute in parallel where dependencies allow
- Async executors managed by ResearchAsyncConfig
- Enable/disable via `synthara.research.parallel-research`
- Thread pool: configurable via Spring properties

### Database Indexing
- PostgreSQL indexes on common queries
- pgvector indexes for approximate nearest neighbor (ANN) search
- Query optimization for semantic similarity lookups

## License

This project is provided as-is. Add your preferred license here.
