class InvaderOrientations
  private attr_reader :invader

  def initialize(invader)
    @invader = invader
  end

  def call
    rows = invader.rows

     (rotations_for(rows, mirrored: false) +
      rotations_for(mirror(rows), mirrored: true))
       .uniq { |invader| invader[:rows] }
  end

  private

  def rotations_for(rows, mirrored:)
    rotated_90 = rotate_90(rows)
    rotated_180 = rotate_90(rotated_90)
    rotated_270 = rotate_90(rotated_180)

    [orientation(rotation: 0, mirrored: mirrored, rows: rows),
     orientation(rotation: 90, mirrored: mirrored, rows: rotated_90),
     orientation(rotation: 180, mirrored: mirrored, rows: rotated_180),
     orientation(rotation: 270, mirrored: mirrored, rows: rotated_270)]
  end

  def orientation(rotation:, mirrored:, rows:)
    { rotation: rotation, mirrored: mirrored, rows: rows }
  end

  def rotate_90(rows)
    rows.map(&:chars)
        .transpose
        .reverse
        .map(&:join)
  end

  def mirror(rows)
    rows.map(&:reverse)
  end
end
