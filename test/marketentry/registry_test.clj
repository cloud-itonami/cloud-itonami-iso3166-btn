(ns marketentry.registry-test
  (:require [clojure.test :refer [deftest is testing]]
            [marketentry.registry :as registry]))

(def negative-list
  ["News Media"
   "Distribution Services including Dealership, Wholesale, Retail and Micro Trade"
   "Standalone Mines for Sale of Minerals in Primary or Raw Form"
   "Mineral processing and beneficiation upto crushing and powdering"
   "Hotel 3 Star and Below"
   "General Health Services"
   "Industries that do not meet the Certificate of Origin requirements"
   "Real Estate Businesses"
   "Activities in the Prohibited List of the Royal Government"])

(deftest engagement-fee-recompute
  (let [e {:base-fee 500000 :monthly-rate 30000 :monitoring-months 12 :claimed-fee 860000.0}]
    (is (== 860000.0 (registry/compute-engagement-fee e)))
    (is (true? (registry/engagement-fee-matches-claim? e))))
  (let [bad {:base-fee 500000 :monthly-rate 30000 :monitoring-months 12 :claimed-fee 999000.0}]
    (is (false? (registry/engagement-fee-matches-claim? bad)))))

(deftest register-draft-and-submit
  (let [d (registry/register-draft "eng-1" "BTN" 0)
        s (registry/register-submit "eng-1" "BTN" 0)]
    (is (= "BTN-DFT-000000" (get d "draft_number")))
    (is (= "BTN-SUB-000000" (get s "submit_number")))
    (is (nil? (get-in d ["certificate" "proof"])))
    (is (= "draft-unsigned" (get-in s ["certificate" "status"])))))

(deftest register-requires-ids
  (is (thrown? Exception (registry/register-draft "" "BTN" 0)))
  (is (thrown? Exception (registry/register-submit "eng-1" "" 0))))

(deftest fdi-sector-restricted-recompute
  (testing "FDI Rules and Regulations 2025 Rule 4 -- sector on Schedule III Negative List bars an FDI Company, a pure set-membership test, no arithmetic"
    (is (true? (registry/fdi-sector-restricted?
                {:foreign-company? true :fdi-sector "Real Estate Businesses"}
                negative-list))
        "declared sector is on the Negative List -> restricted")
    (is (false? (registry/fdi-sector-restricted?
                 {:foreign-company? true :fdi-sector "IT/IT Enabled Services (Outside IT Park)"}
                 negative-list))
        "declared sector is NOT on the Negative List -> not restricted"))
  (testing "entity-scope-gated: a no-op (false) unless :foreign-company? is true, the FDI Rules and Regulations 2025 (Rule 138(12)) only govern an FDI Company"
    (is (false? (registry/fdi-sector-restricted?
                 {:foreign-company? false :fdi-sector "Real Estate Businesses"}
                 negative-list))
        "domestic company declaring a Negative-List sector is still not restricted -- the FDI regime never applies to it"))
  (testing "missing :fdi-sector, or an empty/nil negative list, is never treated as restricted here (evidence-incomplete's job)"
    (is (false? (registry/fdi-sector-restricted?
                 {:foreign-company? true} negative-list)))
    (is (false? (registry/fdi-sector-restricted?
                 {:foreign-company? true :fdi-sector "Real Estate Businesses"} [])))
    (is (false? (registry/fdi-sector-restricted?
                 {:foreign-company? true :fdi-sector "Real Estate Businesses"} nil)))))
