describe InvaderOrientations do
  describe '#call' do
    let(:invader_rows) do
      ['-o-',
       'ooo']
    end

    let(:invader) { Invader.new(Grid.new(invader_rows)) }
    let(:invader_orientations) { described_class.new(invader) }

    # let(:invader_rows) do
    #   '-o-'
    #   'ooo'
    # end
    # let(:invader_orientations) { described_class.new(invader_rows) }

    it 'returns the rotations at 0, 90, 180 & 270 degrees' do
      orientations = [invader_rows,
                      ['-o',
                       'oo',
                       '-o'],
                      ['ooo',
                       '-o-'],
                      ['o-',
                       'oo',
                       'o-']]
      # [['-o-', 'ooo'], ['o-', 'oo', 'o-'], ['ooo', '-o-'], ['-o', 'oo', '-o']]
      expect(invader_orientations.call).to eq(orientations)
    end
  end
end
