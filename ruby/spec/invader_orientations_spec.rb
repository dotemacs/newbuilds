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

    context 'if the invader is symmetrical' do
      let(:symmetrical_rows) do
        %w[oo
           oo].map(&:strip)
      end

      let(:invader) { Invader.new(Grid.new(symmetrical_rows)) }
      let(:invader_orientations) { described_class.new(invader) }

      it 'returns unique rows only' do
        orientations = [{ rotation: 0, mirrored: false, rows: %w[oo oo] }]
        expect(invader_orientations.call).to eq(orientations)
      end
    end
  end
end
