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


(defn get-partial-invader-sizes
  [{:keys [invader radar percent] :as all}]
  (let [full-invader-height (get-height invader)
        partial-invader-height (->round (percent-of {:num full-invader-height :percent percent}))
        full-invader-width (get-width invader)
        partial-invader-width (->round (percent-of {:num full-invader-width :percent percent}))
        full-radar-height (get-height radar)
        full-radar-width (get-width radar)

        partial-invader-sizes {:top {:height partial-invader-height
                                     :width full-invader-width}
                               :bottom {:height partial-invader-height
                                        :width full-invader-width}
                               :left {:height full-invader-height
                                      :width partial-invader-width}
                               :right {:height full-invader-height
                                       :width partial-invader-width}}]
    (merge all {:partial-invader-sizes partial-invader-sizes})))

(defn get-radar-edges
  [{:keys [radar partial-invader-sizes] :as all}]
  (let [radar-height (get-height radar)
        radar-width (get-width radar)
        radar-edges {:top (window-at {:grid radar
                                      :row 0
                                      :col 0
                                      :height (get-in partial-invader-sizes [:top :height])
                                      :width radar-width})
                     :bottom (window-at {:grid radar
                                         :row (- radar-height
                                                 (get-in partial-invader-sizes [:bottom :height]))
                                         :col 0
                                         :height (get-in partial-invader-sizes [:bottom :height])
                                         :width radar-width})
                     :left (window-at {:grid radar
                                       :row 0
                                       :col 0
                                       :height radar-height
                                       :width (get-in partial-invader-sizes [:left :width])})
                     :right (window-at {:grid radar
                                        :row 0
                                        :col (- radar-width
                                                (get-in partial-invader-sizes [:right :width]))
                                        :height radar-height
                                        :width (get-in partial-invader-sizes [:right :width])})}]
    (merge all {:radar-edges radar-edges})))

(defn get-partial-invaders
  [{:keys [invader partial-invader-sizes] :as all}]
  (let [invader-height (get-height invader)
        invader-width (get-width invader)
        partial-invaders {:top (window-at {:grid invader
                                           :row 0
                                           :col 0
                                           :height (get-in partial-invader-sizes [:top :height])
                                           :width invader-width})
                          :bottom (window-at {:grid invader
                                              :row (- invader-height (get-in partial-invader-sizes [:bottom :height]))
                                              :col 0
                                              :height (get-in partial-invader-sizes [:bottom :height])
                                              :width invader-width})
                          :left (window-at {:grid invader
                                            :row 0
                                            :col 0
                                            :height invader-height
                                            :width (get-in partial-invader-sizes [:left :width])})
                          :right (window-at {:grid invader
                                             :row 0
                                             :col (- invader-width (get-in partial-invader-sizes [:right :width]))
                                             :height invader-height
                                             :width (get-in partial-invader-sizes [:right :width])})}]
    (merge all {:partial-invaders partial-invaders})))

(defn find-edge-matches
  [{:keys [partial-invaders radar-edges]}]
  (let [options {:top [:top :bottom]
                 :bottom [:top :bottom]
                 :left [:left :right]
                 :right [:left :right]}]
    (for [[radar-side invader-sides] options
          invader-side invader-sides
          :let [radar-edge (get radar-edges radar-side)
                partial-invader (get partial-invaders invader-side)]
          match (scan-radar partial-invader radar-edge)]
      (assoc match
             :kind :partial
             :radar-side radar-side
             :invader-side invader-side))))

;; usage
#_(->> (get-partial-invader-sizes {:radar data/radar-data :invader data/invader-a :percent 50})
       get-radar-edges
       get-partial-invaders
       find-edge-matches
       (filter #(> (:coverage %) 75)))
