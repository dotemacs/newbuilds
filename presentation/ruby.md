# [fit] What was my **thinking**?

**Ruby code**

---

# Re-write

Since I already implemented the solution[1], this was a bit of a re-write


1. not a 100% solution

---

# How did I find it?

I like the fact that different languages take different approaches, due to their idioms

---

# Doing the first 90%

The task has plenty of rope to hang yourself and to show how you'd design and implement the solution

Doing the happy path is great

---

# The edge cases

The edge cases are where the project gets interesting

I settled for the solution that scans the edge cases, the invaders with the negative coordinates

---

# I've shown how that can be done in Clojure

So I've only settled on doing the happy path of invader scanning in Ruby

---

# Edge case handling

How would I do it, the same as in Clojure, but I'd obviously use classes instead of functions.

I'd introduce another class that would just do the comparison of the coordinates, be they full or partial invaders (those hanging off the grid)
