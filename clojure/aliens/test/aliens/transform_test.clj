(ns aliens.transform-test
  (:require [clojure.test :refer [deftest is testing]]
            [aliens.data :as data]
            [aliens.transform :as transform]))

(deftest rotations-test
  (testing "90 degree rotation"
    (is (= (transform/rotate-90 data/invader-a)
           ["-ooo----"
            "---oo---"
            "-ooooo-o"
            "o-oo-oo-"
            "o-oooo--"
            "--oooo--"
            "o-oooo--"
            "o-oo-oo-"
            "-ooooo-o"
            "---oo---"
            "-ooo----"])))
  (testing "180 degree rotation"
    (is (= (transform/rotate-180 data/invader-a)
           ["---oo-oo---"
            "o-o-----o-o"
            "o-ooooooo-o"
            "ooooooooooo"
            "-oo-ooo-oo-"
            "--ooooooo--"
            "---o---o---"
            "--o-----o--"])))
  (testing "270 degree rotation"
    (is (= (transform/rotate-270 data/invader-a)
           ["----ooo-"
            "---oo---"
            "o-ooooo-"
            "-oo-oo-o"
            "--oooo-o"
            "--oooo--"
            "--oooo-o"
            "-oo-oo-o"
            "o-ooooo-"
            "---oo---"
            "----ooo-"]))))
