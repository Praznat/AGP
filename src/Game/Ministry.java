package Game;

import Questing.Quest;

public class Ministry extends Job {

	public Ministry(Quest.QuestFactory questFactory) {
		super("", new Service(questFactory));
	}

}
