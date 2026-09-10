(ns statute.facts
  "General-law compliance catalog for the Kingdom of Bhutan (BTN) --
  extends this repo's existing `marketentry.facts` (public-procurement
  market-entry only, narrow scope) with a second, orthogonal catalog of
  statutes a company operating in this jurisdiction must generally
  track for compliance. Mirrors cloud-itonami-iso3166-aze/-bih/-jpn/
  -deu/-bgr/-blr/-bol/-blz/-atg/-brb/-brn's `statute.facts`
  (ADR-2607141700, cloud-itonami-compliance-fact-federation).

  Every entry below cites an OFFICIAL Bhutan government source,
  curl-verified 2026-07-22, and every citation reflects PRIMARY TEXT
  this iteration actually fetched (`curl` + `pdftotext -layout`) and
  read in full, not a secondary summary:

  - **The Companies Act of Bhutan, 2016** -- downloaded in full as a
    PDF directly from the Office of the Attorney General's (OAG) own
    official site (`oag.gov.bt/wp-content/uploads/2016/09/
    The-Companies-Act-2016.pdf`) and read via `pdftotext -layout`.
    HIGH confidence, primary text: this is the CURRENT Companies Act --
    OAG's own Acts index also lists an older 'Companies Act of the
    Kingdom of Bhutan, 2000', but every current administering body
    (CRA's own 'Laws & Regulations' page; the FDI Rules and Regulations
    2025's own Rule 12, Rule 76, Rule 95 references) cites only the
    2016 Act. Chapter 14 ('The Regulatory Authority'), s.387: 'The
    Office of the Registrar under this Act is hereby established as an
    autonomous Regulatory Authority... independent in exercise of its
    powers and functions... without fear, favour or prejudice'; the
    board (s.388-389) is chaired by the Secretary of the (2016-era)
    Ministry of Economic Affairs. This is the statute this repo's
    `marketentry.facts` `business-registration-spec-basis` cites for the
    Corporate Regulatory Authority of Bhutan (CRA)'s Chapter-14
    grounding, including the 'Regulatory Authority'/'Registrar'
    definitions this repo's business-registration finding is built on
    (see `marketentry.facts` namespace docstring for the honest gap:
    this iteration could not independently verify a separate instrument
    that renamed this body 'Corporate Regulatory Authority' -- the
    Act's own text uses only 'Regulatory Authority').
  - **The Labour and Employment Act of Bhutan, 2007** -- downloaded in
    full as a PDF directly from the Office of the Attorney General's
    own official site (`oag.gov.bt/wp-content/uploads/2010/05/
    Labour-and-Employment-Act-of-Bhutan-2007Both-Dzongkha-English.pdf`)
    and read via `pdftotext -layout`. HIGH confidence, primary text:
    own PREAMBLE, 'the Labour and Employment Act governing employment
    and working conditions have been enacted by the National Assembly
    of Bhutan during its 86th Session... corresponding to the Fourth
    Day of the First Month of the year 2007'; own Definitions: '\"
    Ministry\" means the Ministry of Labour and Human Resources';
    '\"Minister\" means Minister for Labour and Human Resources'. This
    is the ORIGINAL 2007-era ministry name -- this iteration separately
    confirmed via direct fetch that the Ministry of Industry, Commerce
    and Employment (MoICE, `www.moice.gov.bt`, live HTTP 200)'s own
    Dzongkha-language header reads 'Ministry of Labour, Human
    Resources' alongside its English 'Ministry of Industry, Commerce &
    Employment', and MoICE's own site lists a 'Department of Labour'
    among its departments -- strong corroborating (though not,
    by itself, a fetched machinery-of-government instrument this
    iteration independently read) evidence that the historical Ministry
    of Labour and Human Resources was merged into MoICE, whose
    Department of Labour is the CURRENT administering body. This
    iteration did NOT independently search for a post-2007 amendment or
    consolidation beyond what OAG's own Acts index lists (an honest,
    explicit gap -- OAG's own listing was demonstrably behind current
    practice elsewhere, e.g. it lists neither the Income Tax Act of
    Bhutan 2025 nor the FDI Rules and Regulations 2025, both of which
    this iteration found only via their own originating ministries'
    sites).
  - **The Public Finance Act of Bhutan, 2007 (as amended by the Public
    Finance (Amendment) Act of Bhutan, 2012)** -- downloaded in full as
    a PDF directly from the Office of the Attorney General's own
    official site and read via `pdftotext -layout`. HIGH confidence,
    primary text: s.104 ('Issuance of instructions'), own text: 'the
    Ministry of Finance may issues rules, manuals, directives,
    instructions or notifications on the specific matters as follows:
    ... (i) procurement system which is equitable, transparent,
    competitive and cost-effective'. This iteration separately fetched
    the 2012 Amendment Act's own primary text and confirmed it does NOT
    itself contain a section 104 (the amendment touches other specific
    sections only) -- Bhutanese drafting/citation convention (matched
    verbatim by the Procurement Rules and Regulations 2025's own
    PREFACE: 'Pursuant to Section 104(i) of the Public Finance
    (Amendment) Act of Bhutan 2012...') cites the whole consolidated Act
    by its most recent amending title. This is the statute this repo's
    `marketentry.facts` `:legal-basis` cites for the entire e-GP /
    Procurement Rules and Regulations 2025 procurement framework this
    vertical's `:owner-authority` chain (PMDD/DPP/Ministry of Finance)
    is grounded in.

  A law not in this table has NO spec-basis, full stop; extend
  `catalog`, do not invent an id/url.")

(def catalog
  "iso3 -> vector of statute entries. `:statute/url` + `:statute/law-number`
  are the citation the governor requires before any compliance-fact
  proposal referencing this law can commit."
  {"BTN"
   [{:statute/id "btn.companies-act-2016"
     :statute/title "The Companies Act of Bhutan, 2016"
     :statute/jurisdiction "BTN"
     :statute/kind :law
     :statute/law-number "Chapter 14 ('The Regulatory Authority'), s.387 'The Office of the Registrar under this Act is hereby established as an autonomous Regulatory Authority'; the CURRENT Companies Act, superseding the Companies Act of the Kingdom of Bhutan 2000 -- administered by the Corporate Regulatory Authority of Bhutan (CRA), the Chapter-14 'Regulatory Authority', which appoints the Registrar of Companies"
     :statute/url "https://oag.gov.bt/wp-content/uploads/2016/09/The-Companies-Act-2016.pdf"
     :statute/url-provenance :oag-official-site
     :statute/enacted-date "2016-01-01"
     :statute/retrieved-at "2026-07-22"
     :statute/topic #{:corporate-governance :incorporation}}
    {:statute/id "btn.labour-and-employment-act-2007"
     :statute/title "The Labour and Employment Act of Bhutan, 2007"
     :statute/jurisdiction "BTN"
     :statute/kind :law
     :statute/law-number "Enacted by the National Assembly of Bhutan, 86th Session, corresponding to 4 January 2007; own Definitions name the (2007-era) Ministry of Labour and Human Resources -- this iteration found corroborating evidence the successor administering body is the Department of Labour, Ministry of Industry, Commerce and Employment (MoICE)"
     :statute/url "https://oag.gov.bt/wp-content/uploads/2010/05/Labour-and-Employment-Act-of-Bhutan-2007Both-Dzongkha-English.pdf"
     :statute/url-provenance :oag-official-site
     :statute/enacted-date "2007-01-04"
     :statute/retrieved-at "2026-07-22"
     :statute/topic #{:labor :employment}}
    {:statute/id "btn.public-finance-act-2007"
     :statute/title "The Public Finance Act of Bhutan, 2007 (as amended by the Public Finance (Amendment) Act of Bhutan, 2012)"
     :statute/jurisdiction "BTN"
     :statute/kind :law
     :statute/law-number "s.104(i), own primary text: 'the Ministry of Finance may issue[]... instructions... on... procurement system which is equitable, transparent, competitive and cost-effective' -- the enabling clause the Procurement Rules and Regulations 2025's own PREFACE cites verbatim (via the Act's amending title); administered by the Ministry of Finance"
     :statute/url "https://oag.gov.bt/wp-content/uploads/2010/05/Public-Finance-Act-of-Bhutan-2007English-version.pdf"
     :statute/url-provenance :oag-official-site
     :statute/enacted-date "2007-01-01"
     :statute/retrieved-at "2026-07-22"
     :statute/topic #{:public-finance :procurement}}]})

(defn spec-basis
  "The jurisdiction's statute vector, or nil -- nil means NO spec-basis
  for that jurisdiction yet."
  [iso3]
  (get catalog iso3))

(defn coverage
  "Honest coverage report, same shape/discipline as `marketentry.facts/coverage`:
  never report a missing jurisdiction as covered."
  ([] (coverage (keys catalog)))
  ([iso3s]
   (let [have (filter catalog iso3s)
         missing (remove catalog iso3s)]
     {:requested (count iso3s)
      :covered (count have)
      :covered-jurisdictions (vec (sort have))
      :missing-jurisdictions (vec (sort missing))
      :note (str "cloud-itonami-iso3166-btn statute.facts Wave 0 (ADR-2607141700): "
                 (count (get catalog "BTN")) " BTN statutes seeded with an "
                 "official citation. Extend "
                 "`statute.facts/catalog`, never fabricate a law-id or URL.")})))

(defn by-topic
  "Statutes for `iso3` tagged with `topic` (e.g. :labor, :public-finance)."
  [iso3 topic]
  (filterv #(contains? (:statute/topic %) topic) (spec-basis iso3)))
