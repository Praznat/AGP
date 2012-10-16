package Questing;

import java.util.Stack;

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
		return false;
	}
	@Override
	public Quest push(Quest item) {
		if (this.elementCount >= maxCapacity) {explode(); return null;}
		else {return super.push(item);}
	}
	/** look at item right before latest item */
	public Quest peekUp() {return elementAt(size() - 2);}
	private void explode() {
		this.clear();
	}
}