package Questing;

import Questing.Quest.QuestFactory;
import Sentiens.Clan;

public class MightQuests {
	public static QuestFactory getFactory() {return new QuestFactory(MightQuest.class) {public Quest createFor(Clan c) {return new MightQuest(c);}};}
	
	public static class MightQuest extends Quest {
		public MightQuest(Clan P) {super(P);}
		@Override
		public void pursue() {
			// decide between:
				// build army (by hiring, buying weapons)
				// military actions
			
			
			replaceAndDoNewQuest(Me, new PersecutionQuests.PersecuteInfidel(Me));
		}
	}
	
}
