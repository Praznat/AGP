package Questing;

import java.util.HashMap;

import Defs.M_;
import Game.*;
import Government.Order;
import Questing.Quest.QuestFactory;
import Sentiens.Clan;

public class AllegianceQuests {
	public static QuestFactory getFactory() {return new QuestFactory(AllegianceQuest.class) {public Quest createFor(Clan c) {return new AllegianceQuest(c);}};}
	
	public static class AllegianceQuest extends Quest {
		public AllegianceQuest(Clan P) {super(P);}
		@Override
		public void pursue() {
			if (Me.getBoss() == Me) {replaceAndDoNewQuest(Me, new FindNewMaster(Me)); return;}
			// test if respect still strong
			double resp = Me.conversation(Me.getBoss());
			if (resp < 0 && AGPmain.rand.nextInt(17) > Me.useBeh(M_.PATIENCE)) {
				// desert ?
			}
			Ministry proposedMinistry = Me.getBoss().FB.randomValueInPriority().getMinistry();
			Service proposedService = proposedMinistry.getService();
			// first inquire if proposedMinistry can pay more than current job
			if (Me.getJob() != Job.NOBLE && proposedService.estimateProfit(Me) > Me.getProfitEMA()) {
				Me.setJob(proposedMinistry);
			}
			Me.MB.finishQ();
			if (proposedMinistry == Job.NOBLE) {return;} // TODO causes infinite loop if Allegiance is your only value. find something else for here
			proposedService.doit(Me); // do it even if it's not your job... this is ALLEGIANCE Quest
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
