class Grid
  attr_reader :rows

  def initialize(rows)
    @rows = rows
  end

  def width
    rows.first.length
  end

  def height
    rows.length
  end

  def at(x, y)
    rows[y][x]
  end
end
