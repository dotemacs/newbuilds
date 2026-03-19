(ns aliens.transform
  (:require [aliens.grid :as grid]))

(defn rotate-90
  [invader]
  (let [height (grid/get-height invader)
        width (grid/get-width invader)]
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
