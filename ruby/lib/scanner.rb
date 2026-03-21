class Scanner
  private attr_reader :radar, :invaders, :percent

  def initialize(radar:, invaders:, percent:)
    @radar = radar
    @invaders = invaders
    @percent = percent
  end

  def call
    invaders.flat_map do |invader|
      InvaderOrientations.new(invader).call.flat_map do |rows|
        oriented_invader = Invader.new(Grid.new(rows))
        scan_for(oriented_invader)
      end
    end
  end

  private

  def scan_for(invader)
    (0..(radar.height - invader.height)).flat_map do |row|
      detections_in_row(invader, row)
    end
  end

  def detections_in_row(invader, row)
    (0..(radar.width - invader.width)).filter_map do |column|
      detection_for(invader, column, row)
    end
  end

  def fits_in_radar?(invader)
    radar.width >= invader.width && radar.height >= invader.height
  end

  def radar_section(invader, column, row)
    invader.height.times.map do |invader_row|
      invader.width.times.map do |invader_column|
        radar.at(column + invader_column, row + invader_row)
      end.join
    end
  end

  def detection_for(invader, column, row)
    score = score_for(invader, column, row)
    return if score < percent

    { x: column, y: row, invader: invader.rows, score: score,
      radar_window: radar_section(invader, column, row) }
  end

  def score_for(invader, column, row)
    total_cells = invader.width * invader.height
    matched_cells = 0

    invader.height.times do |invader_height|
      invader.width.times do |invader_width|
        matched_cells += 1 if cells_match?(invader, column, row, invader_width, invader_height)
      end
    end

    ((matched_cells.to_f / total_cells) * 100).round
  end

  def cells_match?(invader, column, row, invader_width, invader_height)
    invader_cell = invader.at(invader_width, invader_height)
    radar_cell = radar.at(column + invader_width, row + invader_height)
    invader_cell == radar_cell
  end
end
