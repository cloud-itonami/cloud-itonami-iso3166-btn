(ns statute.facts-test
  (:require [clojure.string :as str]
            [clojure.test :refer [deftest is]]
            [statute.facts :as facts]))

(deftest btn-has-spec-basis
  (let [sb (facts/spec-basis "BTN")]
    (is (= 3 (count sb)))
    (is (every? #(str/starts-with? (:statute/url %) "https://") sb))
    (is (every? :statute/law-number sb))))

(deftest unknown-jurisdiction-has-no-spec-basis
  (is (nil? (facts/spec-basis "ATL")))
  (is (nil? (facts/spec-basis "ZZZ"))))

(deftest coverage-is-honest
  (let [c (facts/coverage ["BTN" "JPN" "ATL"])]
    (is (= 3 (:requested c)))
    (is (= 1 (:covered c)))
    (is (= ["ATL" "JPN"] (:missing-jurisdictions c)))))

(deftest by-topic-filters
  (is (= ["btn.labour-and-employment-act-2007"]
         (mapv :statute/id (facts/by-topic "BTN" :labor))))
  (is (= ["btn.public-finance-act-2007"]
         (mapv :statute/id (facts/by-topic "BTN" :procurement))))
  (is (empty? (facts/by-topic "BTN" :environment)))
  (is (empty? (facts/by-topic "ATL" :labor))))
