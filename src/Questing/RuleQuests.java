package Questing;

import Game.Order;
import Sentiens.Clan;

public class RuleQuests {
	public static class LoyaltyQuest extends Quest {

		public LoyaltyQuest(Clan P) {super(P);}

		@Override
		public void pursue() {
			if (Me.myOrder() == null) {Order.create(Me);}
			else {Me.myOrder().getQuest(Me);}
		}
		
	}
	
}
