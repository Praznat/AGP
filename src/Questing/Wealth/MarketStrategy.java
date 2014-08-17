package Questing.Wealth;

import java.util.HashSet;

import Markets.Entry;

/** keeps track of bids and offers during wealth quest */
public class MarketStrategy {
	private final int memory;
	private final MarketMemory bidsMemory = new MarketMemory();
	private final MarketMemory offersMemory = new MarketMemory();
	
	public MarketStrategy(int memory) {
		this.memory = memory;
	}
	
	
	private class MarketMemory extends HashSet<Entry> {
		@Override
		public boolean add(Entry e) {
			return (this.size() >= memory && this.add(e));
		}
	}
}
