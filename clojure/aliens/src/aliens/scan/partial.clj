(ns aliens.scan.partial
  (:require [aliens.grid :as grid]
            [aliens.score :as score]
            [aliens.transform :as transform]))

(defn get-all-invader-rotations
  [{:keys [invader] :as all}]
  (let [invader-height (grid/get-height invader)
        invader-width (grid/get-width invader)
        base {:rotation 0
              :invader invader
              :height invader-height
              :width invader-width}]
    (assoc all
           :invader-rotations
           (vec
            (for [rotation [0 90 180 270]]
              (case rotation
                0 base
                90 (let [invader (transform/rotate-90 invader)]
                     (merge base
                            {:invader invader
                             :rotation rotation
                             :height (grid/get-height invader)
                             :width (grid/get-width invader)}))
                180 (let [invader (transform/rotate-180 invader)]
                      (merge base
                             {:invader invader
                              :rotation rotation
                              :height (grid/get-height invader)
                              :width (grid/get-width invader)}))
                270 (let [invader (transform/rotate-270 invader)]
                      (merge base
                             {:invader invader
                              :rotation rotation
                              :height (grid/get-height invader)
                              :width (grid/get-width invader)}))))))))

(defn get-partial-visible-areas
  [{:keys [percent invader-rotations] :as all}]
  (assoc all
         :invader-rotations
         (vec
          (for [invader-rotation invader-rotations
                :let [invader-height (:height invader-rotation)
                      invader-width (:width invader-rotation)
                      invader-attrib-%->num #(-> (score/percent-of {:num %
                                                                    :percent percent})
                                                 Math/floor
                                                 int)
                      visible-height (invader-attrib-%->num invader-height)
                      visible-width (invader-attrib-%->num invader-width)
                      overhang-height (- invader-height visible-height)
                      overhang-width (- invader-width visible-width)]]
            (assoc invader-rotation
                   :partial-visible-areas
                   {:overhang-height overhang-height
                    :overhang-width overhang-width
                    :visible-height visible-height
                    :visible-width visible-width})))))

(defn get-partial-placements
  [{:keys [radar invader-rotations] :as all}]
  (let [radar-height (grid/get-height radar)
        radar-width (grid/get-width radar)
        base {:side :top
              :row 0
              :col 0}
        vertical-placements
        (fn [{:keys [height width partial-visible-areas]}]
          (let [{:keys [visible-height overhang-height]} partial-visible-areas]
            (concat
             (for [row (range (- overhang-height) 0)
                   col (range 0 (inc (- radar-width width)))]
               (assoc base
                      :row row
                      :col col
                      :radar-row 0
                      :radar-col col
                      :invader-row (- row)
                      :invader-col 0
                      :height (+ height row)
                      :width width))
             (for [row (range (- radar-height visible-height) radar-height)
                   col (range 0 (inc (- radar-width width)))
                   :let [visible-rows (- radar-height row)]
                   :when (>= visible-rows visible-height)]
               (assoc base
                      :side :bottom
                      :row row
                      :col col
                      :radar-row row
                      :radar-col col
                      :invader-row 0
                      :invader-col 0
                      :height visible-rows
                      :width width)))))
        horizontal-placements
        (fn [{:keys [height width partial-visible-areas]}]
          (let [{:keys [visible-width overhang-width]} partial-visible-areas]
            (concat
             (for [row (range 0 (inc (- radar-height height)))
                   col (range (- overhang-width) 0)]
               (assoc base
                      :side :left
                      :row row
                      :col col
                      :radar-row row
                      :radar-col 0
                      :invader-row 0
                      :invader-col (- col)
                      :height height
                      :width (+ width col)))
             (for [row (range 0 (inc (- radar-height height)))
                   col (range (- radar-width visible-width) radar-width)
                   :let [visible-cols (- radar-width col)]
                   :when (>= visible-cols visible-width)]
               (assoc base
                      :side :right
                      :row row
                      :col col
                      :radar-row row
                      :radar-col col
                      :invader-row 0
                      :invader-col 0
                      :height height
                      :width visible-cols)))))]
    (assoc all
           :invader-rotations
           (for [invader-rotation invader-rotations]
             (assoc invader-rotation
                    :placements
                    (vec (concat (vertical-placements invader-rotation)
                                 (horizontal-placements invader-rotation))))))))

(defn compare-partial-visible-invaders
  [{:keys [radar invader-rotations] :as all}]
  (let [score-placement
        (fn [{:keys [invader rotation placements] :as invader-rotation}]
          (assoc invader-rotation
                 :placements
                 (vec
                  (for [{:keys [radar-row radar-col invader-row invader-col height width]
                         :as placement}
                        placements
                        :let [radar-window (grid/window-at {:grid radar
                                                            :row radar-row
                                                            :col radar-col
                                                            :height height
                                                            :width width})
                              invader-window (grid/window-at {:grid invader
                                                              :row invader-row
                                                              :col invader-col
                                                              :height height
                                                              :width width})
                              {:keys [matched-cells possible-cells coverage]}
                              (score/score-scan invader-window radar-window)]]
                    (merge placement
                           {:kind :partial
                            :rotation rotation
                            :matched-cells matched-cells
                            :possible-cells possible-cells
                            :coverage coverage
                            :radar-window radar-window
                            :invader-window invader-window})))))]
    (assoc all
           :invader-rotations
           (map score-placement invader-rotations)
           :partial-matches
           (vec
            (mapcat :placements
                    (map score-placement invader-rotations))))))
