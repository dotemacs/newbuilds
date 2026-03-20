class GridFileReader
  def initialize(path)
    @path = path
  end

  def call
    rows = File.readlines(@path, chomp: true)
    Grid.new(rows)
  end
end
