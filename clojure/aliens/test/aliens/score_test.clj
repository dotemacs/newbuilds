(ns aliens.score-test
  (:require [clojure.test :refer [deftest is testing]]
            [aliens.scan.full :as full]
            [aliens.score :as score]))

(deftest count-o-cells-test
  (testing "the counting of o's in `data`"
    (is (= 2 (score/count-o-cells ["-o" "-o"])))))

(deftest count-hits-test
  (let [radar ["-o" "-o"]
        invader ["o" "o"]
        window (full/window-for-invader {:invader invader
                                         :radar radar
                                         :row 0
                                         :col 1})]
    (is (= 2 (score/count-hits invader window)))
    (is (not (= 3 (score/count-hits invader window))))))

(deftest ->%-test
  (is (= (score/->% 0.50) 50.0)))

(deftest percent-of-test
  (is (= 50.0 (score/percent-of {:num 100 :percent 50})))
  (is (= 99.0 (score/percent-of {:num 100 :percent 99}))))
