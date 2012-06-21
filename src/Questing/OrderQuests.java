package Questing;

import Game.Order;
import Sentiens.Clan;

public class OrderQuests {
	public static class LoyaltyQuest extends Quest {

		public LoyaltyQuest(Clan P) {super(P);}

		@Override
		public void pursue() {
			Me.MB.finishQ();
			if (Me.myOrder() == null) {Order.create(Me);}
			else {Me.myOrder().getQuest(Me);}
		}
		
	}
	
	
	
}
