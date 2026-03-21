describe InvaderOrientations do
  describe '#call' do
    let(:invader_rows) do
      ['-o-',
       'ooo']
    end
    let(:invader) { Invader.new(Grid.new(invader_rows)) }
    let(:invader_orientations) { described_class.new(invader) }

    it 'returns the rotations at 0, 90, 180 & 270 degrees' do
      orientations = [{ rotation: 0, rows: ['-o-', 'ooo'] },
                      { rotation: 90, rows: ['-o', 'oo', '-o'] },
                      { rotation: 180, rows: ['ooo', '-o-'] },
                      { rotation: 270, rows: ['o-', 'oo', 'o-'] }]
      expect(invader_orientations.call).to eq(orientations)
    end
  end
end
