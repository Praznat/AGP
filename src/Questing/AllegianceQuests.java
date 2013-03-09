package Questing;

import java.util.HashMap;

import AMath.Calc;
import Avatar.SubjectiveType;
import Defs.M_;
import Game.*;
import Government.Order;
import Questing.Quest.QuestFactory;
import Sentiens.*;

public class AllegianceQuests {
	public static QuestFactory getMinistryFactory() {return new QuestFactory(AllegianceQuest.class) {public Quest createFor(Clan c) {return new AllegianceQuest(c);}};}
	
	public static class AllegianceQuest extends Quest {
		public AllegianceQuest(Clan P) {super(P);}
		@Override
		public void pursue() {
			if (Me.getBoss() == Me) {replaceAndDoNewQuest(Me, new FindNewMaster(Me)); return;}
			// test if respect still strong (THIS STUFF SHOULD BE DONE BY WISEMAN)
//			double resp = Me.conversation(Me.getBoss());
//			if (resp < 0 && AGPmain.rand.nextInt(17) > Me.useBeh(M_.PATIENCE)) {
//				// desert ?
//			}
			Ministry proposedMinistry = getMinistryProposal(Me);
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
	private static Ministry getMinistryProposal(Clan clan) {
		Ministry proposedMinistry = clan.getBoss().FB.randomValueInPriority().getMinistry();
		if (proposedMinistry == Job.NOBLE) {
			Clan boss = clan.getBoss();
			if (boss != clan) {return getMinistryProposal(boss);}
			else {} //TODO handle what to do when top proposal is just Noble... same as above ... delete one or the other
		}
		return proposedMinistry;
	}

	public static class FindNewMaster extends Quest {
		private final HashMap<Clan, Double> respectMemo = new HashMap<Clan, Double>();
		private int attemptsLeft = 1 + Me.useBeh(M_.PATIENCE);
		public FindNewMaster(Clan P) {
			super(P);
		}
		@Override
		public String description() {return "Find new master";}
		
		@Override
		public void avatarPursue() {
			if (Me.getBoss() != Me) {throw new IllegalStateException("this quest is only for RONIN");}
			avatarConsole().showChoices(Me, Me.myShire().getCensus(), SubjectiveType.RESPECT_ORDER, new Calc.Listener() {
				@Override
				public void call(Object arg) {
					Clan clan = (Clan) arg;
					if (clan == Me && Me.myOrder() == null) {Order.createBy(Me); success(Me);}
					else {Me.join(clan);   success(clan);}
				}
			});
		}
		
		@Override
		public void pursue() {
			final Clan currBoss = Me.getBoss();
			if (currBoss != Me) {
				success(currBoss);
				Me.addReport(GobLog.findSomeone(currBoss, "same old master"));
				return;
			}
			if (attemptsLeft-- > 0) {
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
				if (max > 0) {
					Me.join(bestClan);
					success(bestClan);
					Me.addReport(GobLog.findSomeone(bestClan, "new master"));
				}
				else {
					if (Me.myOrder() == null) {Order.createBy(Me); success(Me); Me.addReport(GobLog.createOrder(false));}
					else {success(); Me.addReport(GobLog.createOrder(true));}
				}
			}
		}
	}

}
