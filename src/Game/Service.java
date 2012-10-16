package Game;

import Questing.Quest;
import Sentiens.Clan;

public class Service implements Act {
	Quest.QuestFactory questFactory;
	public Service(Quest.QuestFactory q) {
		questFactory = q;
	}

	@Override
	public void doit(Clan doer) {
		doer.MB.QuestStack.prioritizeExistingMemberOfType(questFactory.getQuestType());
		doer.pursue(); //TODO maybe some logic about prioritizing previous quest
	}

	@Override
	public int estimateProfit(Clan pOV) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public String getDesc() {
		return "Service";
	}

	@Override
	public int getSkill(Clan clan) {
		// TODO Auto-generated method stub
		return 0;
	}

}
