package Questing;

import Questing.Quest.QuestFactory;
import Sentiens.Clan;

public class InfluenceQuests {
	public static QuestFactory getFactory() {return new QuestFactory(PropagandaQuest.class) {public Quest createFor(Clan c) {return new PropagandaQuest(c);}};}
	
	public static class PropagandaQuest extends Quest {
		public PropagandaQuest(Clan P) {super(P);}
		@Override
		public void pursue() {
			// preach?
			replaceAndDoNewQuest(Me, new PersecutionQuests.PersecuteInfidel(Me));
		}
	}
	
	public static class PersonalInfluenceQuest extends Quest {
		public PersonalInfluenceQuest(Clan P) {super(P);}
		@Override
		public void pursue() {
			// preach?
		}
	}
}
