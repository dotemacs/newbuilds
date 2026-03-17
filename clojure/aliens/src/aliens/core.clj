(ns aliens.core
  (:require [aliens.data :as data]))


(defn window-at
  [{:keys [grid row col height width]}]
  (mapv (fn [r]
          (subs (nth grid r) col (+ col width)))
        (range row (+ row height))))

(defn get-height
  [data]
  (count data))

(defn get-width
  [data]
  (count (first data)))

(defn window-for-invader
  [{:keys [invader radar row col]}]
  (window-at {:grid radar
              :row row
              :col col
              :height (get-height invader)
              :width (get-width invader)}))

(defn count-o-cells
  [data]
  (->> data
       (apply concat)
       (filter #{\o})
       count))

(defn count-hits
  "Count where `invader`'s `o` matches sliding `window`'s `o`."
  [invader window]
  (count
   (for [r (range (get-height invader))
         c (range (get-width invader))
         :when (and (= \o (get-in invader [r c]))
                    (= \o (get-in window  [r c])))]
     [r c])))

(defn ->%
  [value]
  (-> value
      double
      (* 100)))

(defn rotate-90
  [invader]
  (let [height (get-height invader)
        width (get-width invader)]
    (mapv (fn [col]
            (apply str
                   (for [row (range (dec height) -1 -1)]
                     (get-in invader [row col]))))
          (range width))))

(defn rotate-180
  [invader]
  (-> invader
      rotate-90
      rotate-90))

(defn rotate-270
  [invader]
  (-> invader
      rotate-180
      rotate-90))

(defn scan-radar
  ([invader radar]
   (scan-radar invader radar 0))
  ([invader radar rotation]
   (let [invader (case rotation
                   0 invader
                   90 (rotate-90 invader)
                   180 (rotate-180 invader)
                   270 (rotate-270 invader)
                   invader)
         max-row (inc (- (get-height radar)
                         (get-height invader)))
         max-col (inc (- (get-width radar)
                         (get-width invader)))
         invader-cells-count (count-o-cells invader)]
     (for [row (range max-row)
           col (range max-col)
           :let [sliding-window (window-for-invader {:invader invader :radar radar :row row :col col})
                 window-cells-count (count-o-cells sliding-window)
                 hits (count-hits invader sliding-window)]]
       {:kind :full
        :rotation rotation
        :row row
        :col col
        :matched-cells hits
        :coverage (if (pos? invader-cells-count)
                    (->% (/ hits invader-cells-count))
                    0.0)
        :radar-window sliding-window
        :invader-window invader
        :window-height max-row
        :window-width max-col
        :window-cells window-cells-count
        :invader-cells invader-cells-count}))))

(defn ->round
  [value]
  (-> value
      Math/ceil
      int))

(defn score-scan
  [invader-window radar-window]
  (let [matched-cells (count-hits invader-window radar-window)
        possible-cells (count-o-cells invader-window)]
    {:matched-cells matched-cells
     :possible-cells possible-cells
     :coverage (if (pos? possible-cells)
                 (->% (/ matched-cells possible-cells))
                 0.0)}))

;;;; To get invaders in the radar data
;;;; that provide > 75% match
;; (filter #(> (:coverage %) 75) (scan-radar data/invader-a data/radar-data))


(defn scan-all
  []
  (->> [0 90 180 270]
       (mapcat #(scan-radar data/invader-a data/radar-data %))
       (filter #(> (:coverage %) 75))))


(defn percent-of
  [{:keys [num percent]}]
  (* num (/ percent 100.0)))


(defn get-all-invader-rotations
  [{:keys [radar invader percent] :as all}]
  (let [invader-height (get-height invader)
        invader-width (get-width invader)
        base {:rotation 0
              :invader invader
              :height invader-height
              :width invader-width}]
    (assoc all
           :invader-rotations
           (for [rotation [0 90 180 270]]
             (case rotation
               0 base
               90 (let [invader (rotate-90 invader)]
                    (merge base
                           {:invader invader
                            :rotation rotation
                            :height (get-height invader)
                            :width (get-width invader)}))
               180 (let [invader (rotate-180 invader)]
                     (merge base
                            {:invader invader
                             :rotation rotation
                             :height (get-height invader)
                             :width (get-width invader)}))
               270 (let [invader (rotate-270 invader)]
                     (merge base
                            {:invader invader
                             :rotation rotation
                             :height (get-height invader)
                             :width (get-width invader)})))))))

;; - take each rotated invader
;; - compute minimum visible size from percent
;; - convert that into allowed overhang
;; - generate legal edge placements
;; - for each placement, return the radar overlap rectangle and the matching invader overlap rectangle

(defn get-partial-visible-areas
  [{:keys [radar invader percent invader-rotations] :as all}]
  (assoc all
         :invader-rotations
         (for [invader-rotation invader-rotations
               :let [invader-height (:height invader-rotation)
                     invader-width (:width invader-rotation)
                     invader-attrib-%->num #(-> (percent-of {:num %
                                                             :percent percent})
                                                Math/floor ;; intentional
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
                   :visible-width visible-width}))))

(defn get-partial-placements
  [{:keys [radar invader-rotations] :as all}]
  (let [radar-height (get-height radar)
        radar-width (get-width radar)
        base {:side :top
              :row 0
              :col 0}
        vertical-placements
        (fn [{:keys [height width partial-visible-areas]}]
          (let [{:keys [visible-height overhang-height]} partial-visible-areas]
            (concat
             ;; top edge
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

             ;; bottom edge
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
             ;; left edge
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

             ;; right edge
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
                        :let [radar-window (window-at {:grid radar
                                                       :row radar-row
                                                       :col radar-col
                                                       :height height
                                                       :width width})
                              invader-window (window-at {:grid invader
                                                         :row invader-row
                                                         :col invader-col
                                                         :height height
                                                         :width width})
                              {:keys [matched-cells possible-cells coverage]}
                              (score-scan invader-window radar-window)]]
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

;; desired flow
;; to find matches which match 75% of the scans
;;
#_(->> {:radar data/radar-data :invader data/invader-a :percent 50}
       get-all-invader-rotations
       get-partial-visible-areas
       get-partial-placements
       compare-partial-visible-invaders
       :partial-matches
       (filter #(> (:coverage %) 75)))


(defn full-scan
  "Scan the radar data for invaders, returning full matches and partial matches.
  `invader` is the invader data.
  `radar` is the radar data.
  `full-match-percent` represents the percentage of matches that
  should have, in order to be shown.
  `partial-match-percent`, the same as above, but for matches that are
  partially shown in the radar data.
  `partial-match-visible` is the percentage of the invader that should
  be present in the radar, for it to be even considered for
  comparison. For example if the value is set to 60, anything under it
  won't be considered."
  [{:keys [invader radar full-match-percent partial-match-percent partial-match-visible]}]
  {:full-matches
   (filter #(> (:coverage %) full-match-percent) (scan-radar invader radar))
   :partial-matches
   (->> {:radar radar :invader invader :percent partial-match-percent}
        get-all-invader-rotations
        get-partial-visible-areas
        get-partial-placements
        compare-partial-visible-invaders
        :partial-matches
        (filter #(> (:coverage %) partial-match-visible)))})

#_(full-scan {:radar data/radar-data
              :invader data/invader-a
              :full-match-percent 75
              :partial-match-percent 75
              :partial-match-visible 50})
