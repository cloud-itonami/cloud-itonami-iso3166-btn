(ns marketentry.facts-test
  (:require [clojure.test :refer [deftest is testing]]
            [marketentry.facts :as facts]))

(deftest btn-has-spec-basis
  (let [sb (facts/spec-basis "BTN")]
    (is (some? sb))
    (is (string? (:provenance sb)))
    (is (seq (:required-evidence sb)))
    (is (some? (facts/corporate-number-spec-basis "BTN")))
    (is (some? (facts/business-registration-spec-basis "BTN")))
    (is (some? (facts/fdi-negative-list-spec-basis "BTN")))))

(deftest btn-rep-spec-basis-is-honest-nil
  (testing "BTN's rep-spec-basis is nil -- this iteration found a real debarred-supplier mechanism (the Debarment Rules 2023) but its duration clause rendered only in Dzongkha prose this iteration cannot fluently read, not a negative finding within primary text actually read"
    (is (nil? (facts/rep-spec-basis "BTN")))))

(deftest btn-business-registration-is-a-different-body-from-tax-and-procurement
  (testing "business/company registration (CRA) and tax registration (DRC) are administered by different authorities under different ministries -- see namespace docstring"
    (let [reg (facts/business-registration-spec-basis "BTN")
          tax (facts/corporate-number-spec-basis "BTN")]
      (is (some? reg))
      (is (some? tax))
      (is (not= (:business-registration-owner-authority reg)
                (:corporate-number-owner-authority tax))))))

(deftest btn-fdi-negative-list-is-the-flagship-spec-basis
  (testing "the FDI Rules and Regulations 2025 Schedule III Negative List is a real, government-published, enumerated set -- not fabricated"
    (let [nl (facts/fdi-negative-list-spec-basis "BTN")]
      (is (some? nl))
      (is (= 9 (count (:fdi-negative-list nl))))
      (is (contains? (set (:fdi-negative-list nl)) "Real Estate Businesses"))
      (is (contains? (set (:fdi-negative-list nl)) "News Media")))))

(deftest unknown-jurisdiction-has-no-spec-basis
  (is (nil? (facts/spec-basis "ATL")))
  (is (nil? (facts/spec-basis "ZZZ")))
  (is (nil? (facts/business-registration-spec-basis "ATL")))
  (is (nil? (facts/fdi-negative-list-spec-basis "ATL"))))

(deftest required-evidence-satisfied
  (let [sb (facts/spec-basis "BTN")
        all (:required-evidence sb)]
    (is (true? (facts/required-evidence-satisfied? "BTN" all)))
    (is (not (facts/required-evidence-satisfied? "BTN" (take 1 all))))
    (is (nil? (facts/required-evidence-satisfied? "ATL" all)))))

(deftest coverage-is-honest
  (let [c (facts/coverage ["BTN" "USA" "ATL"])]
    (is (= 3 (:requested c)))
    (is (= 2 (:covered c)))
    (is (= ["ATL"] (:missing-jurisdictions c)))))
