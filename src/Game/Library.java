package Game;

import Defs.K_;
import Government.Order;
import Questing.KnowledgeQuests.KnowledgeBlock;
import Sentiens.Values.Value;

@SuppressWarnings("rawtypes")
public class Library {
	private static final int START_CAPACITY = 10;
	private static final int MAX_CAPACITY = 30;
	private int capacity = START_CAPACITY;
	private KnowledgeBlock[] knowledge = new KnowledgeBlock[MAX_CAPACITY];
	private Order owningOrder;
	
	public void incCapacity() {if (capacity < MAX_CAPACITY) capacity++;}
	
	public int getCapacity() {return capacity * (owningOrder != null ? owningOrder.numShiresControlled() : 1);}
	
	public KnowledgeBlock findKnowledge(K_ k) {
		for (KnowledgeBlock kb : knowledge) {
			if (kb == null) return null;
			if (kb.isApplicableFor(k)) {return kb;}
		}	return null;
	}
	public void putKnowledge(KnowledgeBlock kb) {
		final int effectiveCapacity = getCapacity();
		for (int i = effectiveCapacity-1; i > 0; i--) {
			knowledge[i] = knowledge[i-1]; // shift everything right (forgetting last one)
		}
		knowledge[0] = kb;
	}
	public KnowledgeBlock getKnowledge(int i) {return knowledge[i];}
	
	@Override
	public String toString() {
		String s = ""; for (KnowledgeBlock kb : knowledge) {if (kb == null) break; s+=kb;}
		return s;
	}
}
