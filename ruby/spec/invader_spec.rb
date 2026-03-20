describe Invader do
  let(:grid) { Grid.new(["-o-", "ooo"]) }

  subject(:invader) { described_class.new(grid) }

  it 'stores a grid' do
    expect(invader.grid).to eq(grid)
  end
end
