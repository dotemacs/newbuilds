(ns aliens.scan.full
  (:require [aliens.grid :as grid]
            [aliens.score :as score]
            [aliens.transform :as transform]))

(defn window-for-invader
  [{:keys [invader radar row col]}]
  (grid/window-at {:grid radar
                   :row row
                   :col col
                   :height (grid/get-height invader)
                   :width (grid/get-width invader)}))

(defn scan-radar
  ([invader radar]
   (scan-radar invader radar 0))
  ([invader radar rotation]
   (let [invader (case rotation
                   0 invader
                   90 (transform/rotate-90 invader)
                   180 (transform/rotate-180 invader)
                   270 (transform/rotate-270 invader)
                   invader)
         max-row (inc (- (grid/get-height radar)
                         (grid/get-height invader)))
         max-col (inc (- (grid/get-width radar)
                         (grid/get-width invader)))
         invader-cells-count (score/count-o-cells invader)]
     (for [row (range max-row)
           col (range max-col)
           :let [sliding-window (window-for-invader {:invader invader :radar radar :row row :col col})
                 window-cells-count (score/count-o-cells sliding-window)
                 hits (score/count-hits invader sliding-window)]]
       {:kind :full
        :rotation rotation
        :row row
        :col col
        :matched-cells hits
        :coverage (if (pos? invader-cells-count)
                    (score/->% (/ hits invader-cells-count))
                    0.0)
        :radar-window sliding-window
        :invader-window invader
        :window-height max-row
        :window-width max-col
        :window-cells window-cells-count
        :invader-cells invader-cells-count}))))
