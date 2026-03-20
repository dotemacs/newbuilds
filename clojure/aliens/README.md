# `newbuilds` Clojure test

Also see [the presentation for the reasoning](../presentation/clojure.pdf)

The code is split in two:

- full scans for the invaders
- partial scans for the invaders

The partial scans are the scans for the edge cases, where the invader
does not fit in the radar window.

To run it, use this at the REPL:

```clojure
(full-scan {:radar data/radar-data
            :invader data/invader-a
            :full-match-percent 75
            :partial-match-percent 75
            :partial-match-visible 50})
```

This would scan `invader-a` (one of the invaders), against the radar
data.

The parameter explanation:

- `:full-match-percent` since there will be scans across the whole of
  the radar data, the scans have percentage attached to them
  signifying how much they match. This argument allows you to specify
  the minimum percentage of the scans that should match.

- `partial-match-percent` this is the same but for partial matches,
  the invaders that "hang off" the radar data (that aren't fully
  within the window of the radar).

- `partial-match-visible` this parameter allows you to specify how
   much is the invader allowed to hang off the radar window, before it
   can be even considered for a scan. For example if you specify 50%,
   then any invader not at least 50% within the radar window will be
   not considered.
