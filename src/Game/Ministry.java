package Game;

import Questing.Quest;
import Sentiens.Clan;

public class Ministry extends Job {

	public Ministry(Quest.QuestFactory questFactory) {
		super("Citizen", new Service(questFactory));
	}
	
	@Override
	public String getDesc(Clan c) {return "";}
	
	public Service getService() {return (Service)acts[0];}

}
