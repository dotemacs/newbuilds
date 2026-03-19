(ns aliens.scan.full-test
  (:require [clojure.test :refer [deftest is]]
            [aliens.data :as data]
            [aliens.scan.full :as full]))

(deftest window-for-invader-test
  (let [radar ["-o" "-o"]
        invader ["o" "o"]
        window (full/window-for-invader {:radar radar
                                         :invader invader
                                         :row 0
                                         :col 1})]
    (is (= invader window))))

(deftest scan-radar-test
  (is (= {:rotation 0,
          :invader-cells 46,
          :coverage 15.21739130434783,
          :matched-cells 7,
          :col 0,
          :kind :full,
          :window-height 43,
          :radar-window ["----o--oo--"
                         "--o-o-----o"
                         "--o--------"
                         "-------o--o"
                         "------o---o"
                         "-o--o-----o"
                         "o----------"
                         "--o--------"],
          :window-cells 16,
          :invader-window ["--o-----o--"
                           "---o---o---"
                           "--ooooooo--"
                           "-oo-ooo-oo-"
                           "ooooooooooo"
                           "o-ooooooo-o"
                           "o-o-----o-o"
                           "---oo-oo---"],
          :row 0,
          :window-width 90}
         (first (full/scan-radar data/invader-a data/radar-data)))))
