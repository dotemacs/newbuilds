describe Radar do
  let(:grid) { Grid.new(["---", "-o-", "---"]) }

  subject(:radar) { described_class.new(grid) }

  describe '#grid' do
    it 'stores a grid' do
      expect(radar.grid).to eq(grid)
    end
  end

  describe '#width' do
    it 'returns the grid width' do
      expect(radar.width).to eq(3)
    end
  end

  describe '#height' do
    it 'returns the grid height' do
      expect(radar.height).to eq(3)
    end
  end

  describe '#at' do
    it 'returns the character at the given coordinates' do
      expect(radar.at(0, 0)).to eq('-')
      expect(radar.at(1, 1)).to eq('o')
    end
  end
end
