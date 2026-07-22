# cloud-itonami-iso3166-btn

Open ISO 3166 Blueprint for **BTN**: Kingdom of Bhutan --
**`:implemented`**.

This repository designs **and implements** a forkable OSS business for
an independent public-sector market-entry consultant: an already-
incorporated operator gets a **MarketEntry-LLM** advisor + independent
**Market-Entry Compliance Governor** to navigate Bhutan's public-
procurement registration, business/tax registration and FDI rules, so
the operator can win and service a government contract (or make a
foreign-direct investment) without hiring a full in-house compliance
department.

Built on this workspace's
[`langgraph`](https://github.com/kotoba-lang/langgraph) StateGraph
runtime (portable `.cljc`, supervised superstep loop, interrupts,
Datomic/in-mem checkpoints) -- the same actor pattern as every prior
actor in this fleet -- here it is **MarketEntry-LLM ⊣ Market-Entry
Compliance Governor**.

> **Why an actor layer at all?** An LLM is great at drafting an
> engagement summary, normalizing intake records, and checking whether
> a claimed engagement fee actually equals base-fee + months x
> monthly-rate -- but it has no notion of which jurisdiction's
> procurement/company/tax/FDI law is official, no license to actually
> draft or submit a real e-GP tender response or FDI Registration
> Certificate application, and no way to independently confirm on its
> own whether an engagement's declared FDI sector is barred by the FDI
> Rules and Regulations 2025's own Schedule III Negative List or
> whether Department of Revenue and Customs (DRC) Taxpayer Number
> (TPN) registration has actually been verified. Letting it draft or
> submit directly invites fabricated regulatory citations, an
> engagement-fee mismatch being filed, an FDI Company entering a
> barred sector, and an unverified TPN registration -- exposing the
> operator to real regulatory/legal liability. This project seals the
> MarketEntry-LLM into a single node and wraps it with an independent
> **Market-Entry Compliance Governor**, a human **approval workflow**,
> and an immutable **audit ledger**.

## Official surface

- **Public procurement**: e-GP (Electronic Government Procurement
  System, `egp.gov.bt`) -- a live self-service e-tendering portal
  (tender notices, Registered Bidder search, online Debarment List),
  operated by the Procurement Management and Development Division
  (PMDD), Department of Procurement and Properties (DPP), Ministry of
  Finance, governed by the **Procurement Rules and Regulations 2025**
  (PRR 2025, effective 1 July 2025, superseding PRR 2023 -- own
  PREFACE citation, Public Finance Act of Bhutan 2007 s.104(i) as
  amended by the Public Finance (Amendment) Act of Bhutan 2012).
  Bidder/Supplier/Consultant registration via Registering Authorities
  (PRR 2025 Chapter 2); works contractors register separately with the
  Bhutan Construction and Transport Authority (BCTA).
- **Business/company registration**: Corporate Regulatory Authority of
  Bhutan (CRA, `cra.gov.bt`) -- established as an independent entity
  effective 3 April 2024, operating under the **Ministry of Industry,
  Commerce and Employment (MoICE)** -- the CURRENT correct parent
  ministry. `MoICE` is the January-2023 successor to the "Ministry of
  Economic Affairs" (the ministry name still visible in the Companies
  Act of Bhutan, 2016's own 2016-era board-composition text, s.388-389
  -- this catalog cites that historical name only where the primary
  text itself uses it, and always resolves the CURRENT authority to
  MoICE, never to the defunct ministry). Legal basis: Companies Act of
  Bhutan, 2016, Chapter 14 ("The Regulatory Authority"), s.387.
- **Tax registration**: Department of Revenue and Customs (DRC,
  `drc.gov.bt`), Ministry of Finance -- the Taxpayer Number (**TPN**,
  never "TIN" -- that is a third-party gloss, not the Bhutanese
  statutory term), an 8-11 digit number for legal entities, applied
  for online via RAMIS (`ramis.drc.gov.bt`). Legal basis: **Income Tax
  Act of Bhutan, 2025**, Chapter 12 ss.296/299 (repeals the Income Tax
  Act of the Kingdom of Bhutan 2001 and its 2016/2020 amendments,
  effective 1 January 2026).
- **Foreign Direct Investment**: **Foreign Direct Investment Rules and
  Regulations 2025** (effective 18 July 2025), Department of Industry,
  MoICE -- own Foreword states it "consolidates the provisions of the
  FDI Policy 2019 and the FDI Regulations 2019 into a single,
  comprehensive document" and own Rule 3 repeals both. Rule 4: "FDI
  shall be allowed in all sectors except for those listed in the
  Negative List as provided in Schedule III". Rule 138(12) defines an
  "FDI Company" as >=20% foreign equity (>=10% for a foreign
  institutional investor).

**Currency note**: this catalog deliberately cites the 2025-vintage
primary instruments above (PRR 2025, FDI Rules and Regulations 2025,
Income Tax Act of Bhutan 2025) rather than the 2023/2019/2001-era
instruments an earlier or less thorough search would return -- every
citation in `src/marketentry/facts.cljc` was independently fetched
(`curl` + `pdftotext -layout`) and verbatim-checked against the actual
government-hosted PDF, not assumed from a secondary summary. See that
namespace's own docstring for the full per-citation provenance trail
and the honest gaps it explicitly declines to paper over (the
Debarment Rules 2023 Rule 54 duration clause renders only in Dzongkha
prose this iteration cannot fluently read; no separate instrument
renaming the Companies Act's "Regulatory Authority" to "Corporate
Regulatory Authority" was independently found, only CRA's own
branding).

## Checks

Six checks, in priority order, evaluated by `marketentry.governor` on
every `MarketEntry-LLM` proposal. The first five are HARD violations
a human approver cannot override; double-actuation guards are counted
separately. The confidence/actuation gate (item 6) is SOFT -- but see
Actuation below, `:filing/draft`/`:filing/submit` never auto-commit
regardless.

| # | Check | Grounds | Source |
|---|---|---|---|
| 1 | **Spec-basis** -- a `:jurisdiction/assess`/`:filing/draft`/`:filing/submit` proposal must cite an official source, never an invented one | `marketentry.facts/spec-basis` | e-GP / PRR 2025, CRA, DRC, FDI Rules 2025 (see Official surface above) |
| 2 | **Evidence incomplete** -- for draft/submit, the jurisdiction's full required-evidence checklist must be on file | CRA Certificate of Incorporation, DRC TPN, e-GP/BCTA registration, ACC Debarment List confirmation, FDI Registration Certificate | `marketentry.facts/required-evidence-satisfied?` |
| 3 | **FDI sector restricted** (flagship) -- for submit, when `:foreign-company? true`, independently recompute whether the declared `:fdi-sector` is a member of the FDI Rules and Regulations 2025 Schedule III Negative List (9 named categories); entity-scope-gated to an FDI Company (Rule 138(12)) so a domestic Bhutanese business is never gated | `marketentry.registry/fdi-sector-restricted?` | FDI Rules and Regulations 2025 Rule 4 + Schedule III |
| 4 | **Engagement fee mismatch** -- for submit, independently recompute `claimed-fee = base-fee + monthly-rate x monitoring-months` | `marketentry.registry/engagement-fee-matches-claim?` | ground-truth recompute (fleet-standard discipline) |
| 5 | **TPN registration unverified** -- for submit, when `:requires-tpn-registration? true`, independently check `:tpn-registered?` | `marketentry.governor/tpn-registration-unverified-violations` | Income Tax Act of Bhutan 2025 ss.296/299, DRC/RAMIS |
| 6 | **Confidence floor / actuation gate** (SOFT) -- LLM confidence below 0.6, or the op is `:filing/draft`/`:filing/submit` -> escalate to human | `marketentry.governor/check` | this vertical's own Trust Controls (`docs/business-model.md`) |

Two further double-actuation guards (`already-drafted`,
`already-submitted`) refuse to draft or submit the SAME engagement
twice, enforced off dedicated `:drafted?`/`:submitted?` booleans, never
a `:status` value.

The flagship check (3) is a genuinely different SHAPE from every prior
sibling in this fleet: a SET-MEMBERSHIP test on the engagement's own
declared sector (no date arithmetic, no value threshold, no supplier-
registry read) -- see `marketentry.registry`'s namespace docstring for
the full shape comparison.

## Actuation

**Drafting a real e-GP tender response / FDI Registration Certificate
application and submitting a real filing are never autonomous, at any
phase, by construction.** Two independent layers enforce this:

- `marketentry.governor`'s `high-stakes` set
  (`#{:actuation/draft-filing :actuation/submit-filing}`) always
  escalates, regardless of confidence.
- `marketentry.phase`'s phase table (`phase 0` through `phase 3`)
  never puts `:filing/draft` or `:filing/submit` in any phase's
  `:auto` set -- see `marketentry.phase`'s own docstring and
  `test/marketentry/phase_test.clj`'s `filing-submit-never-auto`.

The actor may intake an engagement, assess a jurisdiction and draft a
recommendation; a human market-entry operator is always the one who
actually files a draft or a submission. Grounded directly in this
blueprint's own [`docs/business-model.md`](docs/business-model.md) and
`marketentry.governor`'s own namespace docstring, which names this
vertical's Trust Controls verbatim: "any actual tender response or FDI
Registration Certificate submission requires Market-Entry Compliance
Governor clearance and always escalates to human sign-off"; "a false
or fabricated regulatory-requirement claim is a HARD hold". `:filing/
draft` and `:filing/submit` apply SEQUENTIALLY to the SAME engagement
record (draft first, submit later) -- matching every sibling
`market-entry-compliance-governor` actor's own sequential shape.

## Core Contract

```text
engagement intake + jurisdiction facts (marketentry.facts, spec-cited)
        |
        v
   ┌────────────────────┐   proposal      ┌──────────────────────────────┐
   │ MarketEntry-LLM     │ ─────────────▶ │ Market-Entry Compliance       │
   │ (sealed)            │  + citations    │ Governor (independent system) │
   └────────────────────┘                  │ spec-basis · evidence-        │
          │                commit ◀────────┤ incomplete · fdi-sector-      │
          │                                │ restricted (FLAGSHIP) ·       │
    record + ledger        escalate ◀──────┤ fee-mismatch · tpn-           │
          │            (ALWAYS for         │ unverified · already-         │
          │             draft/submit)      │ drafted/submitted             │
          ▼                                └──────────────────────────────┘
      human approval
```

No automated proposal can draft or submit a filing the governor
refuses, suppress a compliance record, or claim a jurisdiction's
requirements without an official citation.

## What this is NOT

- **Not the Royal Government of Bhutan.** This blueprint is an
  independent operator the government contracts with, or that bids
  into e-GP, or that makes an FDI application -- never the government
  itself, and never an official channel.
- **Not legal or tax advice.** Every regulatory claim must cite the
  official source (`marketentry.facts`) and route final filings to a
  Bhutan-licensed advisor or registered agent where the law requires
  licensed representation.

## Run

```bash
clojure -M:dev:run     # walk a clean intake -> assess -> draft -> submit lifecycle, plus HARD-hold scenarios
clojure -M:dev:test    # governor contract · phase invariants · store parity · registry conformance · facts coverage
clojure -M:lint        # clj-kondo (errors fail; CI mirrors this)
```

## Layout

| File | Role |
|---|---|
| `src/marketentry/store.cljc` | **Store** protocol -- `MemStore` ‖ `DatomicStore` (`langchain.db` + `kotoba-lang/langchain-store`, no hand-rolled EDN-blob codec) + append-only audit ledger + draft AND submit history (dual history) |
| `src/marketentry/registry.cljc` | Filing-draft/filing-submit record construction, `engagement-fee-matches-claim?` ground-truth recompute, `fdi-sector-restricted?` flagship SET-MEMBERSHIP check |
| `src/marketentry/facts.cljc` | Per-jurisdiction market-entry regulatory catalog with an official spec-basis citation per entry, honest coverage reporting |
| `src/marketentry/marketentryllm.cljc` | **MarketEntry-LLM** -- `mock-advisor`; intake/jurisdiction-assessment/draft/submit proposals |
| `src/marketentry/governor.cljc` | **Market-Entry Compliance Governor** -- 5 HARD checks + 2 double-actuation guards + 1 soft (confidence/actuation gate), see Checks above |
| `src/marketentry/phase.cljc` | **Phase 0→3** -- read-only → assisted intake → assisted assess → supervised (draft/submit always human) |
| `src/marketentry/operation.cljc` | **OperationActor** -- langgraph StateGraph |
| `src/marketentry/sim.cljc` | demo driver |
| `test/marketentry/*_test.clj` | governor contract · phase invariants · store parity · registry conformance · facts coverage |

## No robotics premise -- digital/data service exemption

Market-entry and procurement-compliance navigation is a pure
data/software service with no physical-domain work (portal
registration, document checklists, regulatory-change monitoring).
`blueprint.edn` sets `:itonami.blueprint/robotics false` and
`:required-technologies` lists only real capabilities (`:identity`,
`:forms`, `:dmn`, `:bpmn`, `:audit-ledger`), no `:robotics`.

## Capability layer

Required capabilities (`blueprint.edn`): `:identity`, `:forms`,
`:dmn`, `:bpmn`, `:audit-ledger`.

See [`docs/business-model.md`](docs/business-model.md) and
[`docs/operator-guide.md`](docs/operator-guide.md).

## Jurisdiction coverage (honest)

`marketentry.facts/coverage` reports how many requested jurisdictions
actually have an official spec-basis in `marketentry.facts/catalog`
-- currently BTN, USA and DEU are seeded. This is a starting catalog
to prove the governor contract end-to-end, not a claim of global
coverage. Adding a jurisdiction is additive: one map entry citing a
real official source -- never fabricate a jurisdiction's requirements
to make coverage look bigger.

## Maturity

`:implemented` -- see `blueprint.edn`'s
`:itonami.blueprint/implemented-slice` for the full promotion note.

## License

AGPL-3.0-or-later.

## Culture catalog

This repo carries a **country-level regional-culture catalog**
(ADR-2607171400 addendum 2, `cloud-itonami-municipality-culture-catalog`
Wave 1, in `com-junkawasaki/root`) -- national dishes, protected products,
beverages, crafts, festivals and heritage sites for Bhutan:

- `src/culture/facts.cljc` -- the catalog, source of truth (keyed by
  uppercase ISO3, mirroring `statute.facts`).
- `schema/culture.edn` -- DataScript schema.
- `data/culture-tx.edn` -- derived DataScript tx-data (regenerated from
  the catalog, never hand-edited).

City-level counterparts live in the `cloud-itonami-municipality-*` repos.
Same provenance discipline as the compliance catalogs: every entry cites a
source URL that was actually fetched and read on `:culture/retrieved-at`;
summaries state only what the cited source confirms. An item not in
`culture.facts/catalog` has no spec-basis -- never fabricate one.
