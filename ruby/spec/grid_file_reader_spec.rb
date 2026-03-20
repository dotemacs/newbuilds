describe GridFileReader do
  describe '#call' do
    let(:path) { 'radar.txt' }

    it 'reads the file and returns a Grid' do
      grid = described_class.new(path).call

      expect(grid).to be_a(Grid)
      # expect(grid.height).to eq(2)
      # expect(grid.width).to eq(3)
      # expect(grid.at(0, 0)).to eq('a')
      # expect(grid.at(2, 1)).to eq('f')
    end
  end
end
