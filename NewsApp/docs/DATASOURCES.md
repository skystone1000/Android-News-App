# DATASOURCES.md — News providers & in-app API keys

> Canonical reference for the news data sources the app integrates, how keys are managed, and how
> free-tier usage is tracked. For the original build plan see [plan/plan_2_multi_source.md](plan/plan_2_multi_source.md).
> Last updated: 2026-06-24.

## Overview

The app is **source-agnostic**: every provider implements one `NewsSource` contract and is registered
with Hilt (`@IntoMap @StringKey`), so the rest of the app never depends on a concrete provider.
**One source is active at a time** (`NewsSourceProvider`, driven by `UserSettings.dataSourceId`).
Each provider's API key is **entered in-app and encrypted** — keys are never baked into the build.

## Supported providers

| Provider | `id` | Base URL | Auth param (query) | Headlines | Search | Paging model | Results root | Free tier |
|----------|------|----------|--------------------|-----------|--------|--------------|--------------|-----------|
| NewsAPI.org | `newsapi` | `https://newsapi.org/` | `apiKey` | `v2/top-headlines` | `v2/everything` | numeric `page` | `articles[]` | ~100/day (dev only) |
| NewsData.io | `newsdata` | `https://newsdata.io/` | `apikey` | `api/1/latest` | `api/1/latest?q=` | **token** cursor (`nextPage`) | `results[]` | ~200/day |
| GNews | `gnews` | `https://gnews.io/` | `apikey` | `api/v4/top-headlines` | `api/v4/search` | numeric `page` | `articles[]` | ~100/day |
| Currents | `currents` | `https://api.currentsapi.services/` | `apiKey` | `v1/latest-news` | `v1/search` | **none** (single page) | `news[]` | ~600/day |
| Mediastack | `mediastack` | `http://api.mediastack.com/` | `access_key` | `v1/news` | `v1/news?keywords=` | **offset** (`offset`/`limit`) | `data[]` | ~500/month, **HTTP only** |

> Quotas/endpoints are approximate — **verify against each provider's current docs**. The free-tier
> numbers live in `SourceCatalog` (`SourceQuota`) and drive the in-app usage meter; the per-provider
> signup URLs (for the "Get a key" link) live there too.

### Official documentation

| Provider | Docs |
|----------|------|
| NewsAPI.org | https://newsapi.org/docs |
| NewsData.io | https://newsdata.io/documentation |
| GNews | https://docs.gnews.io/ |
| Currents | https://currentsapi.services/en/docs/ |
| Mediastack | https://docs.apilayer.com/mediastack/docs/api-documentation |

### Provider notes
- **NewsAPI.org** — free key is **developer-only** per its TOS (no production traffic). On that plan
  deep pagination is capped (≈100 results total), so requesting a page beyond that returns a
  `maximumResultsReached` error — expected, not a bug.
- **NewsData.io** — cursor pagination: `page` is an opaque `nextPage` token, not a number. No "general"
  category, so the app maps `general → top`. Note: **error** responses return `results` as an *object*
  while success returns an *array*, so a bad key/quota error can fail JSON parsing rather than surface
  cleanly (potential hardening item).
- **GNews** — `max` controls page size; numeric `page`. The **free plan caps `max` at 10** and rejects
  larger values, so `GNewsSource` clamps the requested page size to 10 (`FREE_MAX_RESULTS`).
- **Currents** — per its OpenAPI spec, `v1/latest-news` accepts **only `language`** and `v1/search`
  only `keywords`/`language` (plus category/country/date filters); **neither supports pagination**.
  Sending `page_number`/`page_size`/`category` to `latest-news` makes the API return **HTTP 400**, so
  the source sends only the supported params and returns a single page (no category narrowing on the
  headlines feed).
- **Mediastack** — offset pagination (`offset`/`limit`); auth is the `access_key` **query** param
  against `http://api.mediastack.com`. Free tier is **HTTP only** (no TLS); a cleartext exception
  scoped to `api.mediastack.com` is declared in `res/xml/network_security_config.xml`. Upgrade to a
  paid HTTPS plan to remove it.

## How a source works

- Contract: `NewsSource.getNews(category, cursor, pageSize)` / `searchNews(query, cursor, pageSize)`
  returns `NewsPage(articles, nextCursor)`. The **cursor is a provider-agnostic string** — numeric
  providers encode the page (`"2"`), Mediastack encodes the offset, NewsData passes its token through.
  `NewsPagingSource` keys Paging 3 on this string (forward-only) and de-duplicates by URL.
- Each source reads its key **per call** from `ApiKeyStore`; a blank key throws `MissingApiKeyException`,
  surfaced as a paging error with an "add a key in Settings" message (`EmptyScreen`).
- Wire shapes are mapped to the domain `Article` via per-provider `toArticleOrNull()` mappers
  (`data/remote/dto/*`), which drop items missing a URL or title.

## Adding a new provider (recipe)

1. `data/remote/api/<X>Service.kt` — Retrofit interface (+ `BASE_URL`).
2. `data/remote/dto/<X>Dto.kt` — response DTOs + `toArticleOrNull()` mapper.
3. `data/remote/source/<X>Source.kt` — implement `NewsSource` (read key via `ApiKeyStore`, return `NewsPage`).
4. `di/NetworkModule` — provide the Retrofit instance; `di/SourceModule` — `@IntoMap @StringKey(<X>Source.ID)` binding.
5. `domain/model/SourceCatalog` — add `SourceMetadata` (display name, signup URL, key param hint, `SourceQuota`).
6. `data/remote/UsageInterceptor.sourceIdForHost` — map the provider host → id (for usage counting).

## API key handling

- **Storage:** `ApiKeyStore` / `EncryptedApiKeyStore` — `EncryptedSharedPreferences` with a Keystore-backed
  master key (AES-256-GCM values). Keys decrypt only in memory on use.
- **Entry:** Settings → "Manage sources & keys" row → the dedicated **`DataSourcesScreen`** — a masked
  field per provider (save/clear), a "Get a key" deep link, and an active-source selector enabled only
  once a key is saved. Clearing the active source's key falls back to another configured source (else
  the default).
- **Dev seed:** on first launch, `NewsApplication` seeds the store from `BuildConfig` keys
  (`NEWS_API_KEY`, `GNEWS_API_KEY` from `local.properties`) if unset — a no-op in production.
- **Log safety:** keys ride in query params, so `RedactingLoggingInterceptor` masks them in debug logs
  (OkHttp 4.x's `HttpLoggingInterceptor` can't redact query params).

## Free-tier usage tracking

- `ApiUsageStore` counts requests **on device** per provider against its `SourceQuota`, persisted in a
  DataStore (`api_usage`) with the window it belongs to. `QuotaWindow` rolls the count over at the local
  day (DAILY) or month (MONTHLY) boundary.
- Counting hook: `UsageInterceptor` (OkHttp) maps host → provider id and records each request
  fire-and-forget; when a provider returns `x-ratelimit-remaining`, that value reconciles the estimate.
- UI: `UsageMeter` on the Data Sources screen shows `used / limit`, a colour bar, and the reset time — labelled an
  **on-device estimate** (failed/cached requests or other-device usage won't match the provider exactly).
