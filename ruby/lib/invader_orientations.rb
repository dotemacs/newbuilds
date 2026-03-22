class InvaderOrientations
  private attr_reader :invader

  def initialize(invader)
    @invader = invader
  end

  def call
    rows = invader.rows
    rotated_90 = rotate_90(rows)
    rotated_180 = rotate_90(rotated_90)
    rotated_270 = rotate_90(rotated_180)

    [{ rotation: 0, rows: rows },
     { rotation: 90, rows: rotated_90 },
     { rotation: 180, rows: rotated_180 },
     { rotation: 270, rows: rotated_270 }]
      .uniq { |orientation| orientation[:rows] }
  end

  private

  def rotate_90(rows)
    rows.map(&:chars)
        .transpose
        .reverse
        .map(&:join)
  end
end
