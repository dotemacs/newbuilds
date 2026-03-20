describe Radar do
  let(:grid) { Grid.new(["---", "-o-", "---"]) }

  subject(:radar) { described_class.new(grid) }

  it 'stores a grid' do
    expect(radar.grid).to eq(grid)
  end
end
