describe Scanner do
  describe '#call' do
    invader_rows = ['-o-',
                    'ooo']
    let(:invader) { Invader.new(Grid.new(invader_rows)) }

    context 'when the radar contains an exact match' do
      let(:radar) do
        radar_rows = ['-----',
                      '--o--',
                      '-ooo-',
                      '-----']
        Radar.new(Grid.new(radar_rows))
      end

      it 'returns the match' do
        scanner = described_class.new(radar: radar, invaders: [invader], percent: 100)

        expect(scanner.call).to eq([{ x: 1, y: 1, invader: invader.rows, score: 100, radar_window: ['-o-', 'ooo'], rotation: 0}])
      end
    end

    context 'when the radar contains a partial match' do
      let(:radar) do
        radar_rows = ['-----',
                      '--o--',
                      '-oo--',
                      '-----']
        Radar.new(Grid.new(radar_rows))
      end

      it 'filters out matches below the percent cutoff' do
        scanner = described_class.new(radar: radar, invaders: [invader], percent: 100)

        expect(scanner.call).to eq([])
      end

      it 'keeps matches that meet the percent cutoff' do
        scanner = described_class.new(radar: radar, invaders: [invader], percent: 83)

        expect(scanner.call).to eq([{ x: 1, y: 1, invader: invader.rows, score: 83, radar_window: ['-o-', 'oo-'], rotation: 0 },
                                    { x: 1, y: 1, invader: ['-o', 'oo', '-o'], score: 83, radar_window: ['-o', 'oo', '--'], rotation: 90 }])
      end
    end

    context 'when the radar contains a 90 degree rotated invader' do
      let(:radar) do
        radar_rows = ['----',
                      '--o-',
                      '-oo-',
                      '--o-']
        Radar.new(Grid.new(radar_rows))
      end

      it 'returns the rotated match' do
        scanner = described_class.new(radar: radar, invaders: [invader], percent: 100)

        expect(scanner.call).to eq([{ x: 1, y: 1, invader: ['-o', 'oo', '-o'], score: 100,
                                      radar_window: ['-o', 'oo', '-o'], rotation: 90 }])
      end
    end
  end
end
