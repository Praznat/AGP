package Questing;

import java.util.Stack;

import Sentiens.Clan;

public class QStack<E> extends Stack<E> {
	private Clan parent;
	private int maxCapacity;
	public QStack(Clan P, int c) {parent = P; maxCapacity = c;}
	@Override
	public E push(E item) {
		if (this.elementCount >= maxCapacity) {explode(); return null;}
		else {return super.push(item);}
	}
	/** look at item right before latest item */
	public E peekUp() {return elementAt(size() - 2);}
	private void explode() {
		this.clear();
	}
}