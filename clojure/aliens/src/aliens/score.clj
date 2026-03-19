(ns aliens.score
  (:require [aliens.grid :as grid]))

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
   (for [r (range (grid/get-height invader))
         c (range (grid/get-width invader))
         :when (and (= \o (get-in invader [r c]))
                    (= \o (get-in window  [r c])))]
     [r c])))

(defn ->%
  [value]
  (-> value
      double
      (* 100)))

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

(defn percent-of
  [{:keys [num percent]}]
  (* num (/ percent 100.0)))
