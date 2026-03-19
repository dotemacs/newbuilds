(ns aliens.grid)

(defn get-height
  [data]
  (count data))

(defn get-width
  [data]
  (count (first data)))

(defn window-at
  [{:keys [grid row col height width]}]
  (mapv (fn [r]
          (subs (nth grid r) col (+ col width)))
        (range row (+ row height))))
