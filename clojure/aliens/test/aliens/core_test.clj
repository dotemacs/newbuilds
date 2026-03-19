(ns aliens.core-test
  (:require [clojure.test :refer [deftest is]]
            [aliens.core :as aliens]
            [aliens.data :as data]))

(deftest full-scan-test
  (is (map? (aliens/full-scan {:radar data/radar-data
                               :invader data/invader-a
                               :full-match-percent 75
                               :partial-match-percent 75
                               :partial-match-visible 50}))))
