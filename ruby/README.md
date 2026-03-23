# `newbuilds` Ruby test

See [the presentation for the reasoning](../../presentation/ruby.pdf)

## Basic lay of the land

- `lib/` contains the code
- `spec/` contains the specs

All code was linted with `rubocop` and all the specs pass.

To run the specs:

    bundle exec rspec

To manually run the code from the command line:

    ./run.rb

## Reasoning

I've implemented the invader scanning for the following:

- the original
- rotated invaders (90, 180 & 270 degrees)
- as well as mirrored invaders, for all of the above

After finding all the above, I then removed duplicate invaders, mostly
because all the invaders are symmetric. So at first considering the
mirrored wasn't that interesting. But when non symmetric invaders are
present, then you definitely get more varied shapes that need to be
considered.

## What I didn't implement

I didn't implement the edge case scanning, mostly because of the time
constraints. I did do the edge case scanning for Clojure, so my
approach here would've been the same:

- take the minimum visible percentage of the invader (e.g. if 50% is
  specified, only consider invaders which will be at least 50%
  visible)

- then work out what those (negative) coordinates would be, that way
  the matching code for finding the fully visible & partially visible
  invaders would be the same

- the edge cases along the side are one thing, where they'd only
  overlap in the corners, but that is an interesting position, since
  corners would have to be scanned also on their own, since they would
  be cut from two angles not just one angle like the cases along the
  edge.

## How would I handle the non implemented features mentioned above

I'd create a separate class that would only do matching and the
scanning class would only work out the invader's shapes/orientations.
