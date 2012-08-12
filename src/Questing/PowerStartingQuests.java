package Questing;

import Game.Defs;
import Sentiens.Clan;


public class PowerStartingQuests {

	public static class IndPowerQuest extends Quest {
		public IndPowerQuest(Clan P) {super(P);}
		@Override
		public void pursue() {
			if (Me.getAssets(Defs.meat) == 0) {
				// try to get meat
				return;
			}
			
			
			// alter relevant memes if dont conflict with honor code and honor code > this
		}
	}
}
