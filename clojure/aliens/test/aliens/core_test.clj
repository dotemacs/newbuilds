(ns aliens.core-test
  (:require [clojure.test :refer [deftest is testing]]
            [aliens.core :as aliens]
            [aliens.data :as data]))

(deftest window-at-test
  (testing "grabs a grid at coordinates"
    (let [window (aliens/window-at {:grid data/radar-data
                                    :row 1
                                    :col 1
                                    :height 2
                                    :width 2})]
      (is (= ["-o" "-o"] window)))))

(deftest get-height-test
  (is (= 2 (aliens/get-height ["-o" "-o"]))))

(deftest get-width-test
  (is (= 2 (aliens/get-width ["-o" "-o"]))))

(deftest window-for-invader-test
  (let [radar ["-o" "-o"]
        invader ["o" "o"]
        window (aliens/window-for-invader {:radar radar
                                           :invader invader
                                           :row 0
                                           :col 1})]
    (is (= invader window))))

(deftest count-o-cells-test
  (testing "the counting of o's in `data`"
    (is (= 2 (aliens/count-o-cells ["-o" "-o"])))))

(deftest count-hits-test
  (let [radar ["-o" "-o"]
        invader ["o" "o"]
        window (aliens/window-for-invader {:invader invader
                                           :radar radar
                                           :row 0
                                           :col 1})]
    (is (= 2 (aliens/count-hits invader window)))
    (is (not (= 3 (aliens/count-hits invader window))))))

(deftest ->%-test
  (is (= (aliens/->% 0.50) 50.0)))

(deftest rotations-test
  (testing "90 degree rotation"
    (is (= (aliens/rotate-90 data/invader-a)
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
    (is (= (aliens/rotate-180 data/invader-a)
           ["---oo-oo---"
            "o-o-----o-o"
            "o-ooooooo-o"
            "ooooooooooo"
            "-oo-ooo-oo-"
            "--ooooooo--"
            "---o---o---"
            "--o-----o--"])))
  (testing "270 degree rotation"
    (is (= (aliens/rotate-270 data/invader-a)
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
         (first (aliens/scan-radar data/invader-a data/radar-data)))))

(deftest percent-of-test
  (is (= 50.0 (aliens/percent-of {:num 100 :percent 50})))
  (is (= 99.0 (aliens/percent-of {:num 100 :percent 99}))))

(deftest get-all-invader-rotations-test
  (is (= (-> (aliens/get-all-invader-rotations {:radar data/radar-data
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
  (is (= (->> (aliens/get-all-invader-rotations {:radar data/radar-data
                                                 :invader data/invader-a
                                                 :percent 50})
              aliens/get-partial-visible-areas
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
              aliens/get-all-invader-rotations
              aliens/get-partial-visible-areas
              aliens/get-partial-placements
              :invader-rotations
              (mapv #(count (:placements %))))
         [751 851 751 851])))

(deftest compare-partial-visible-invaders
  (is (= (->> {:radar data/radar-data
               :invader data/invader-a
               :percent 50}
              aliens/get-all-invader-rotations
              aliens/get-partial-visible-areas
              aliens/get-partial-placements
              aliens/compare-partial-visible-invaders
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
