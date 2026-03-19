(ns aliens.grid-test
  (:require [clojure.test :refer [deftest is testing]]
            [aliens.data :as data]
            [aliens.grid :as grid]))

(deftest window-at-test
  (testing "grabs a grid at coordinates"
    (let [window (grid/window-at {:grid data/radar-data
                                  :row 1
                                  :col 1
                                  :height 2
                                  :width 2})]
      (is (= ["-o" "-o"] window)))))

(deftest get-height-test
  (is (= 2 (grid/get-height ["-o" "-o"]))))

(deftest get-width-test
  (is (= 2 (grid/get-width ["-o" "-o"]))))
