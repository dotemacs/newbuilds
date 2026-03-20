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

        expect(scanner.call).to eq([{ x: 1, y: 1, invader: invader, score: 100 }])
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

        expect(scanner.call).to eq([{ x: 1, y: 1, invader: invader, score: 83 }])
      end
    end
  end
end
