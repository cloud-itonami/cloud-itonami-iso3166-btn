(ns marketentry.registry
  "Pure-function market-entry filing-draft + filing-submit record
  construction -- an append-only market-entry book-of-record draft.

  Like every sibling actor's registry, there is no single international
  reference-number standard for a public-procurement market-entry
  filing -- every jurisdiction assigns its own format. This namespace
  does NOT invent one; it builds a jurisdiction-scoped sequence number
  and validates the record's required fields, the same honest,
  non-fabricating discipline `marketentry.facts` uses.

  `engagement-fee-matches-claim?` is an HONEST reapplication of the
  SAME ground-truth-recompute DISCIPLINE sibling actors use (verify a
  claimed monetary total against the entity's own recorded quantity x
  unit fields), reapplied to a market-entry engagement fee line.

  `fdi-sector-restricted?` is THIS vertical's own new ground-truth
  check, grounding BTN's flagship governor check
  (`marketentry.governor/fdi-sector-restricted-violations`): the
  Foreign Direct Investment Rules and Regulations 2025 (own primary
  text, see `marketentry.facts`) Rule 4 bars FDI in any sector listed in
  Schedule III's Negative List. This is a DIFFERENT check SHAPE from
  every prior sibling: not a turnover-scaled formula (Bulgaria), not a
  flat statutory threshold (Albania), not a boolean registry-membership
  read of the SUPPLIER (Azerbaijan/Armenia/Bolivia), not a 3-tier
  contract-value classification (Antigua and Barbuda), not a
  bid-evaluation price-adjustment recompute (Benin), not a struck-off
  company-registry legal-capacity boolean of the SUPPLIER (Belize), not
  a validity-window expiry-date recompute (Barbados), and not a pure
  date-precedence/ordering check (Brunei) -- it is a SET-MEMBERSHIP
  check on the engagement's own declared FDI SECTOR (a string), not on
  any date, value, or the supplier's own registry status: is
  `:fdi-sector` a member of the (real, government-published,
  9-item) Negative List? The negative list itself is NEVER hardcoded
  here -- it is always passed in from `marketentry.facts`, the single
  source of truth, the same discipline `required-evidence-satisfied?`
  uses for evidence checklists.

  It is also entity-scope-gated, like Brunei's Part 9 check: a no-op
  (false) unless `:foreign-company?` is true, because the FDI Rules and
  Regulations 2025 only apply to an 'FDI Company' (Rule 138(12): >=20%
  foreign equity, or >=10% for a foreign institutional investor) -- a
  wholly-domestic Bhutanese business never has an FDI Negative List
  obligation at all. The check shape and the gate are independent axes:
  the gate is the SAME kind of entity-scope test Brunei uses, but the
  underlying recompute is a categorical set-membership test, not a
  date-precedence test.

  This namespace is pure data + pure functions -- no I/O, no network
  call to any real procurement, CRA, RAMIS or Invest Bhutan system. It
  builds the RECORD an operator would keep, not the act of submitting
  an e-GP tender response or an FDI Registration Certificate application
  itself (that is `marketentry.operation`'s `:filing/submit`, always
  human-gated -- see README Actuation)."
  (:require [clojure.string :as str]))

(defn- unsigned-certificate
  "Every certificate this actor produces is UNSIGNED -- signature is
  the market-entry operator's act, not this actor's."
  [kind subject record-id]
  {"@context" ["https://www.w3.org/ns/credentials/v2"]
   "type" ["VerifiableCredential" kind]
   "credentialSubject" {"id" subject "record" record-id}
   "proof" nil
   "issued_by_registry" false
   "status" "draft-unsigned"})

(defn- zero-pad [n w]
  (let [s (str n)]
    (str (apply str (repeat (max 0 (- w (count s))) "0")) s)))

(defn compute-engagement-fee
  "The ground-truth engagement fee for `engagement`'s own `:base-fee`
  and `:monitoring-months` x `:monthly-rate` -- a single flat
  base + months x rate calculation, not a full pricing engine."
  [{:keys [base-fee monthly-rate monitoring-months]}]
  (+ (double base-fee)
     (* (double monthly-rate) (double monitoring-months))))

(defn engagement-fee-matches-claim?
  "Does `engagement`'s own `:claimed-fee` equal the independently
  recomputed `compute-engagement-fee`?"
  [{:keys [claimed-fee] :as engagement}]
  (== (double claimed-fee) (compute-engagement-fee engagement)))

(defn fdi-sector-restricted?
  "Is `engagement`'s own declared `:fdi-sector` a member of
  `negative-list` (the FDI Rules and Regulations 2025 Schedule III
  Negative List, sourced from `marketentry.facts` -- NEVER hardcoded
  here)?

  A no-op (false) unless `:foreign-company?` is true -- the FDI Rules
  and Regulations 2025, by their own Rule 138(12) 'FDI Company'
  definition (>=20% foreign equity, or >=10% for a foreign institutional
  investor), only govern FDI Companies. A wholly domestic Bhutanese
  business has no FDI Negative List obligation at all, regardless of
  which sector it declares -- the same 'missing entity scope means the
  check never fires' discipline `foreign-company-registration-late?`
  uses in the Brunei sibling. Missing `:fdi-sector` for an FDI Company
  is also never treated as restricted HERE (that is the
  `evidence-incomplete` check's job, upstream, where an assessment must
  already exist)."
  [{:keys [foreign-company? fdi-sector]} negative-list]
  (boolean
   (when (true? foreign-company?)
     (when (and fdi-sector (seq negative-list))
       (contains? (set negative-list) fdi-sector)))))

(defn register-draft
  "Validate + construct the FILING-DRAFT registration DRAFT -- the
  market-entry operator's own act of preparing an e-GP tender response
  / FDI Registration Certificate application package. Pure function --
  does not touch any real e-GP, CRA, RAMIS or Invest Bhutan system."
  [engagement-id jurisdiction sequence]
  (when-not (and engagement-id (not= engagement-id ""))
    (throw (ex-info "draft: engagement_id required" {})))
  (when-not (and jurisdiction (not= jurisdiction ""))
    (throw (ex-info "draft: jurisdiction required" {})))
  (when (< sequence 0)
    (throw (ex-info "draft: sequence must be >= 0" {})))
  (let [draft-number (str (str/upper-case jurisdiction) "-DFT-" (zero-pad sequence 6))
        record {"record_id" draft-number
                "kind" "filing-draft"
                "engagement_id" engagement-id
                "jurisdiction" jurisdiction
                "immutable" true}]
    {"record" record "draft_number" draft-number
     "certificate" (unsigned-certificate "FilingDraft" draft-number draft-number)}))

(defn register-submit
  "Validate + construct the FILING-SUBMIT registration DRAFT -- the
  market-entry operator's own act of actually submitting an e-GP tender
  response / FDI Registration Certificate application (always
  human-gated upstream)."
  [engagement-id jurisdiction sequence]
  (when-not (and engagement-id (not= engagement-id ""))
    (throw (ex-info "submit: engagement_id required" {})))
  (when-not (and jurisdiction (not= jurisdiction ""))
    (throw (ex-info "submit: jurisdiction required" {})))
  (when (< sequence 0)
    (throw (ex-info "submit: sequence must be >= 0" {})))
  (let [submit-number (str (str/upper-case jurisdiction) "-SUB-" (zero-pad sequence 6))
        record {"record_id" submit-number
                "kind" "filing-submit"
                "engagement_id" engagement-id
                "jurisdiction" jurisdiction
                "immutable" true}]
    {"record" record "submit_number" submit-number
     "certificate" (unsigned-certificate "FilingSubmit" submit-number submit-number)}))

(defn append [history result]
  (conj (vec history) (get result "record")))
