describe InvaderOrientations do
  describe '#call' do
    let(:invader_rows) do
      ['-o-',
       'ooo']
    end
    let(:invader) { Invader.new(Grid.new(invader_rows)) }
    let(:invader_orientations) { described_class.new(invader) }

    it 'returns the rotations at 0, 90, 180 & 270 degrees' do
      orientations = [{ rotation: 0, mirrored: false, rows: ['-o-', 'ooo'] },
                      { rotation: 90, mirrored: false, rows: ['-o', 'oo', '-o'] },
                      { rotation: 180, mirrored: false, rows: ['ooo', '-o-'] },
                      { rotation: 270, mirrored: false, rows: ['o-', 'oo', 'o-'] }]
      expect(invader_orientations.call).to eq(orientations)
    end

    context 'when the invader is symmetrical' do
      let(:symmetrical_rows) do
        %w[oo
           oo].map(&:strip)
      end

      let(:invader) { Invader.new(Grid.new(symmetrical_rows)) }
      let(:invader_orientations) { described_class.new(invader) }

      it 'returns unique invaders only' do
        orientations = [{ rotation: 0, mirrored: false, rows: %w[oo oo] }]
        expect(invader_orientations.call).to eq(orientations)
      end
    end

    context 'when the invader is not symmetrical' do
      let(:non_symmetrical_rows) do
        ['oo-',
         '-o-',
         '--o']
      end

      let(:invader) { Invader.new(Grid.new(non_symmetrical_rows)) }
      let(:invader_orientations) { described_class.new(invader) }

      it 'returns both, mirrored and non mirrored, invaders' do
        orientations = [{ mirrored: false, rotation: 0,   rows: ['oo-', '-o-', '--o'] },
                        { mirrored: false, rotation: 90,  rows: ['--o', 'oo-', 'o--'] },
                        { mirrored: false, rotation: 180, rows: ['o--', '-o-', '-oo'] },
                        { mirrored: false, rotation: 270, rows: ['--o', '-oo', 'o--'] },
                        { mirrored: true,  rotation: 0,   rows: ['-oo', '-o-', 'o--'] },
                        { mirrored: true,  rotation: 90,  rows: ['o--', 'oo-', '--o'] },
                        { mirrored: true,  rotation: 180, rows: ['--o', '-o-', 'oo-'] },
                        { mirrored: true,  rotation: 270, rows: ['o--', '-oo', '--o'] }]
        expect(invader_orientations.call).to eq(orientations)
      end
    end
  end
end
