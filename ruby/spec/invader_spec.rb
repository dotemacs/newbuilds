describe Invader do
  let(:grid) { Grid.new(['-o-', 'ooo']) }

  subject(:invader) { described_class.new(grid) }

  describe '#grid' do
    it 'stores a grid' do
      expect(invader.grid).to eq(grid)
    end
  end

  describe '#width' do
    it 'returns the grid width' do
      expect(invader.width).to eq(3)
    end
  end

  describe '#height' do
    it 'returns the grid height' do
      expect(invader.height).to eq(2)
    end
  end

  describe '#at' do
    it 'returns the character at the given coordinates' do
      expect(invader.at(1, 0)).to eq('o')
      expect(invader.at(2, 1)).to eq('o')
    end
  end
end
