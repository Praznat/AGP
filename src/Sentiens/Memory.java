package Sentiens;


import Game.Defs;
import Questing.QStack;
import Questing.Quest;


public class Memory implements Defs {
	private static final int QMEMORY = 10;
	private final Clan Me;
	public QStack<Quest> QuestStack;

	public Memory(Clan P) {
		Me = P;
		QuestStack = new QStack<Quest>(Me, QMEMORY);
	}
	
	
	public void newQ(Quest q) {QuestStack.push(q);}
	public void finishQ() {QuestStack.pop();}
	
	
	
}