package Questing;

import java.util.Stack;

import Questing.Quest.QuestRetrievalQuest;
import Sentiens.Clan;

@SuppressWarnings("serial")
public class QStack extends Stack<Quest> {
	private int maxCapacity;
	public QStack(int c) {maxCapacity = c;}
	public boolean prioritizeExistingMemberOfType(Class<? extends Quest> clasz) {
		for (int i = this.size() - 1; i >= 0; i--) {
			Quest member = this.get(i);
			if (member.getClass().isAssignableFrom(clasz)) {
				this.remove(member);
				this.push(member);
				return true;
			}
		}
		return false; //not found
	}
	@Override
	public Quest peek() {
		if (isEmpty()) {return null;}
		else {return super.peek();}
	}
	@Override
	public Quest push(Quest item) {
		if (this.elementCount >= maxCapacity) {explode();}
		return super.push(item);
	}
	/** look at item right before latest item */
	public Quest peekUp() {return elementAt(size() - 2);}
	private void explode() {
		Clan clan = null;
		@SuppressWarnings("unchecked")
		Class<? extends Quest>[] oldQuests = new Class[maxCapacity];
		int i = 0; for (Quest q : this) {
			clan = q.getDoer();
			oldQuests[i++] = q.getClass();
		}
		System.out.println(clan.getNomen() + " QUEST EXPLOSION!!! " + this);
		this.clear();
		super.push(new QuestRetrievalQuest(clan, oldQuests));
	}
}