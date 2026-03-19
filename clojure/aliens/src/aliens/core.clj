(ns aliens.core
  (:require [aliens.data :as data]
            [aliens.scan.full :as full]
            [aliens.scan.partial :as partial]))

;;;; To get invaders in the radar data
;;;; that provide > 75% match
;; (filter #(> (:coverage %) 75) (full/scan-radar data/invader-a data/radar-data))

(defn scan-all
  []
  (->> [0 90 180 270]
       (mapcat #(full/scan-radar data/invader-a data/radar-data %))
       (filter #(> (:coverage %) 75))))

;; desired flow
;; to find matches which match 75% of the scans
;;
#_(->> {:radar data/radar-data :invader data/invader-a :percent 50}
       partial/get-all-invader-rotations
       partial/get-partial-visible-areas
       partial/get-partial-placements
       partial/compare-partial-visible-invaders
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
   (filter #(> (:coverage %) full-match-percent) (full/scan-radar invader radar))
   :partial-matches
   (->> {:radar radar :invader invader :percent partial-match-percent}
        partial/get-all-invader-rotations
        partial/get-partial-visible-areas
        partial/get-partial-placements
        partial/compare-partial-visible-invaders
        :partial-matches
        (filter #(> (:coverage %) partial-match-visible)))})

#_(full-scan {:radar data/radar-data
              :invader data/invader-a
              :full-match-percent 75
              :partial-match-percent 75
              :partial-match-visible 50})
