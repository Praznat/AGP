package Questing.Wealth;

import Questing.Quest.PatronedQuest;
import Sentiens.Clan;

public class IntelligentLaborQuest extends PatronedQuest {

	public IntelligentLaborQuest(Clan P, Clan patron) {
		super(P, patron);
	}

	@Override
	public void pursue() {
		
	}

	@Override
	public String description() {
		return "Work";
	}

}
