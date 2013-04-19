package Questing;

import Game.AGPmain;
import Sentiens.Clan;

public class ImmigrationQuests {
	public static class EmigrateQuest extends Quest {

		public EmigrateQuest(Clan P) {super(P);}

		@Override
		public void pursue() {
			// TODO Auto-generated method stub
			AGPmain.TheRealm.addToWaitingForImmigration(Me); // when it happens
		}
		@Override
		public String description() {return "Wander out in search of a better home";}
	}
	public static class FleeQuest extends Quest {

		public FleeQuest(Clan P) {super(P);}

		@Override
		public void pursue() {
			// TODO Auto-generated method stub
		}
		@Override
		public String description() {return "Flee from danger";}
	}
}
