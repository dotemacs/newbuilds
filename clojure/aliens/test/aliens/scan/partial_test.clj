(ns aliens.scan.partial-test
  (:require [clojure.test :refer [deftest is]]
            [aliens.data :as data]
            [aliens.scan.partial :as partial]))

(deftest get-all-invader-rotations-test
  (is (= (-> (partial/get-all-invader-rotations {:radar data/radar-data
                                                 :invader data/invader-a
                                                 :percent 50})
             :invader-rotations)
         [{:rotation 0,
           :invader ["--o-----o--"
                     "---o---o---"
                     "--ooooooo--"
                     "-oo-ooo-oo-"
                     "ooooooooooo"
                     "o-ooooooo-o"
                     "o-o-----o-o"
                     "---oo-oo---"],
           :height 8,
           :width 11}
          {:rotation 90,
           :invader ["-ooo----"
                     "---oo---"
                     "-ooooo-o"
                     "o-oo-oo-"
                     "o-oooo--"
                     "--oooo--"
                     "o-oooo--"
                     "o-oo-oo-"
                     "-ooooo-o"
                     "---oo---"
                     "-ooo----"],
           :height 11,
           :width 8}
          {:rotation 180,
           :invader ["---oo-oo---"
                     "o-o-----o-o"
                     "o-ooooooo-o"
                     "ooooooooooo"
                     "-oo-ooo-oo-"
                     "--ooooooo--"
                     "---o---o---"
                     "--o-----o--"],
           :height 8,
           :width 11}
          {:rotation 270,
           :invader ["----ooo-"
                     "---oo---"
                     "o-ooooo-"
                     "-oo-oo-o"
                     "--oooo-o"
                     "--oooo--"
                     "--oooo-o"
                     "-oo-oo-o"
                     "o-ooooo-"
                     "---oo---"
                     "----ooo-"],
           :height 11,
           :width 8}])))

(deftest get-partial-visible-areas-test
  (is (= (->> (partial/get-all-invader-rotations {:radar data/radar-data
                                                  :invader data/invader-a
                                                  :percent 50})
              partial/get-partial-visible-areas
              :invader-rotations
              (map :partial-visible-areas))
         '({:overhang-height 4,
            :overhang-width 6,
            :visible-height 4,
            :visible-width 5}
           {:overhang-height 6,
            :overhang-width 4,
            :visible-height 5,
            :visible-width 4}
           {:overhang-height 4,
            :overhang-width 6,
            :visible-height 4,
            :visible-width 5}
           {:overhang-height 6,
            :overhang-width 4,
            :visible-height 5,
            :visible-width 4}))))

(deftest get-partial-placements-test
  (is (= (->> {:radar data/radar-data
               :invader data/invader-a
               :percent 50}
              partial/get-all-invader-rotations
              partial/get-partial-visible-areas
              partial/get-partial-placements
              :invader-rotations
              (mapv #(count (:placements %))))
         [751 851 751 851])))

(deftest compare-partial-visible-invaders
  (is (= (->> {:radar data/radar-data
               :invader data/invader-a
               :percent 50}
              partial/get-all-invader-rotations
              partial/get-partial-visible-areas
              partial/get-partial-placements
              partial/compare-partial-visible-invaders
              :partial-matches
              (filter #(> (:coverage %) 75))
              first)
         {:col 12,
          :coverage 77.14285714285714,
          :height 5,
          :invader-col 0,
          :invader-row 3,
          :invader-window
          ["-oo-ooo-oo-"
           "ooooooooooo"
           "o-ooooooo-o"
           "o-o-----o-o"
           "---oo-oo---"],
          :kind :partial,
          :matched-cells 27,
          :possible-cells 35,
          :radar-col 12,
          :radar-row 0,
          :radar-window
          ["-o--ooo--oo"
           "oooooo-oooo"
           "o-ooo-oo-oo"
           "oooo--o-oo-"
           "ooo-ooo----"],
          :rotation 0,
          :row -3,
          :side :top,
          :width 11})))
