package Questing;

import java.util.HashMap;

import Defs.M_;
import Game.AGPmain;
import Government.Order;
import Questing.Quest.QuestFactory;
import Sentiens.Clan;

public class AllegianceQuests {
	public static QuestFactory getFactory() {return new QuestFactory(AllegianceQuest.class) {public Quest createFor(Clan c) {return new AllegianceQuest(c);}};}
	
	
	public static class AllegianceQuest extends Quest {
		public AllegianceQuest(Clan P) {super(P);}
		@Override
		public void pursue() {
			// TODO Auto-generated method stub
		}
	}

	public static class FindNewMaster extends Quest {
		private final HashMap<Clan, Double> respectMemo = new HashMap<Clan, Double>();
		private int attemptsLeft = 1 + Me.useBeh(M_.PATIENCE);
		public FindNewMaster(Clan P) {
			super(P);
		}

		@Override
		public void pursue() {
			if (Me.getBoss() != Me) {throw new IllegalStateException("this quest is only for RONIN");}
			if (--attemptsLeft >= 0) {
				Clan[] pop = Me.myShire().getCensus();
				Clan candidate = pop[AGPmain.rand.nextInt(pop.length)];
				double resp = Me.conversation(candidate);
				if (Me != candidate && !Me.isSomeBossOf(candidate)) {
					Double d = respectMemo.get(candidate);
					if (d == null) {respectMemo.put(candidate, resp);}
					else {respectMemo.put(candidate, resp + d);}
				}
			}
			else {
				Clan bestClan = null; double max = 0;
				for (Clan c : respectMemo.keySet()) {
					double d = respectMemo.get(c);
					if (d > max) {max = d; bestClan = c;}
				}
				if (max > 0) {Me.join(bestClan);   success(bestClan);}
				else {
					if (Me.myOrder() == null) {Order.createBy(Me); success(Me);}
					else {success();}
				}
			}
		}
		
	}
}
