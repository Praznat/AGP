package Sentiens;

import java.util.*;

import Sentiens.Values.ValuatableValue;
import Sentiens.Values.Value;

public class Legacy {
	private final Map<Value, Integer> valueBins = new HashMap<Value, Integer>();
	private final Clan Me;
	
	public Legacy(Clan source) {
		Me = source;
		startFromScratch();
	}
	
	private void startFromScratch() {
		for (Value v : Values.All) {
			valueBins.put(v, 0);
		}
	}
	
	public void reenforce(ValuatableValue v, double rate) {
		final int curr = valueBins.get(v);
		final int diff = v.value(Me, Me) - curr;
		valueBins.put(v, newValue(curr, diff, rate));
	}
	public boolean reenforceIfPositive(ValuatableValue v, double rate) {
		final int curr = valueBins.get(v);
		final int diff = v.value(Me, Me) - curr;
		if (diff < 0) {return false;}
		valueBins.put(v, newValue(curr, diff, rate));
		return true;
	}
	
	private void reenforceBest(double rate) { //unused?
		int bestDiff = 0, bestCurr = 0; Value bestVal = null;
		for (Value v : valueBins.keySet()) {
			final int curr = valueBins.get(v);
			final int diff = ((ValuatableValue) v).value(Me, Me) - curr;
			if (diff > bestDiff) {bestDiff = diff; bestCurr = curr; bestVal = v;}
		}
		if (bestVal != null) {valueBins.put(bestVal, newValue(bestCurr, bestDiff, rate));}
	}
	
	private int newValue(int curr, int diff, double rate) {
		return (int)Math.round(rate * diff + curr);
	}
	
	public String improveDesc(Value v, double rate) {
		final int curr = valueBins.get(v);
		final int diff = ((ValuatableValue) v).value(Me, Me) - curr;
		return curr + " -> " + newValue(curr, diff, rate);
	}
	
}
