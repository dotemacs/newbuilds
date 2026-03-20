class Invader
  attr_reader :grid

  def initialize(grid)
    @grid = grid
  end

  def width
    grid.width
  end

  def height
    grid.height
  end

  def at(x, y)
    grid.at(x, y)
  end
end
