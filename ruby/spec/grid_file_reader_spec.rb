describe GridFileReader do
  describe '#call' do
    let(:path) { 'radar.txt' }

    it 'reads the file and returns a Grid' do
      grid = described_class.new(path).call

      expect(grid).to be_a(Grid)
    end
  end
end
