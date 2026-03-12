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

(defn scan-radar
  [invader radar]
  (let [max-row (inc (- (get-height radar)
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
       :row row
       :col col
       :matched-cells hits
       :coverage (->% (/ hits invader-cells-count))
       :radar-window sliding-window
       :invader-window invader
       :window-height max-row
       :window-width max-col
       :window-cells window-cells-count
       :invader-cells invader-cells-count})))

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
