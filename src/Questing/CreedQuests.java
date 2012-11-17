package Questing;

import Questing.PersecutionQuests.PersecuteHeretic;
import Questing.Quest.QuestFactory;
import Sentiens.Clan;

public class CreedQuests {
	public static QuestFactory getFactory() {return new QuestFactory(PersecuteHeretic.class) {public Quest createFor(Clan c) {return new PersecuteHeretic(c);}};}

}
