#!/usr/bin/env ruby

require_relative 'lib/grid_file_reader'
require_relative 'lib/grid'
require_relative 'lib/invader'
require_relative 'lib/radar'
require_relative 'lib/scanner'
require_relative 'lib/invader_orientations'

invader = Invader.new(GridFileReader.new('invader-a.txt').call)
radar = Radar.new(GridFileReader.new('radar.txt').call)

scanner = Scanner.new(radar: radar, invaders: [invader], percent: 80)

puts scanner.call
