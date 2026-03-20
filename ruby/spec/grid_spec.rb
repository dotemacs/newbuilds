describe Grid do
  subject(:grid) { described_class.new(["abc", "def", "ghi"]) }

  describe '#width' do
    it 'returns the number of columns' do
      expect(grid.width).to eq(3)
    end
  end

  describe '#height' do
    it 'returns the number of rows' do
      expect(grid.height).to eq(3)
    end
  end

  describe '#at' do
    it 'returns the character at the given coordinates' do
      expect(grid.at(1, 0)).to eq('b')
      expect(grid.at(2, 2)).to eq('i')
    end
  end
end
