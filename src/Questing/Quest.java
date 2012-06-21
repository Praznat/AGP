package Questing;

import Defs.Q_;
import Game.AGPmain;
import Questing.PersecutionQuests.PersecuteForeigner;
import Questing.PersecutionQuests.PersecuteHeretic;
import Questing.PersecutionQuests.PersecuteInfidel;
import Questing.RomanceQuests.BreedQuest;
import Questing.OrderQuests.LoyaltyQuest;
import Questing.WorkQuests.BuildWealthQuest;
import Sentiens.Clan;
import Sentiens.GobLog;
import Sentiens.Stressor;

public abstract class Quest {
	protected Clan Me;
	public Quest(Clan P) {Me = P;}
	public abstract void pursue();
	
	protected void success() {Me.MB.finishQ();   if (Me.MB.QuestStack.empty()) {Me.AB.catharsis(1);}}
	protected void success(Stressor.Causable relief) {Me.AB.relieveFrom(new Stressor(Stressor.ANNOYANCE, relief));   success();}
	protected void failure() {Me.MB.finishQ();}
	protected void failure(Stressor.Causable blamee) {Me.AB.add(new Stressor(Stressor.ANNOYANCE, blamee));   failure();}
	protected Quest upQuest() {return Me.MB.QuestStack.peekUp();}
	public String shortName() {return description();}
	public String description() {return "Undefined Quest";}
		
	public static class DefaultQuest extends Quest {
		public DefaultQuest(Clan P) {super(P);}
		@Override
		public void pursue() {Me.addReport(GobLog.idle()); Me.MB.finishQ();}
	}
	public static abstract class TargetQuest extends Quest {
		protected Clan target;
		public TargetQuest(Clan P) {super(P);}
		public TargetQuest(Clan P, Clan T) {super(P); target = T;}
		public void setTarget(Clan t) {target = t;}
		protected Clan getTarget() {return target;}
	}
	public static interface FindTarget {
		public boolean meetsReq(Clan POV, Clan target);
	}
	public static abstract class FindTargetAbstract extends Quest implements FindTarget {
		protected static final int TRIESPERTURN = 3; //maybe size of pub?
		public FindTargetAbstract(Clan P) {super(P);}
		@Override
		public void pursue() {
			Clan[] pop = Me.myShire().getCensus();
			for (int i = Math.min(TRIESPERTURN, pop.length); i > 0; i--) {
				Clan candidate = pop[AGPmain.rand.nextInt(pop.length)];
				if(meetsReq(Me, candidate)) {
					((TargetQuest) upQuest()).setTarget(candidate);  //must be called by TargetQuest
					success(Me.myShire()); return;
				}
			}
			failure(Me.myShire());
		}
	}
	
	
	
	public static Quest QtoQuest(Clan clan, Q_ q) {
		Quest quest;
		switch(q) {
		case BREED: quest = new BreedQuest(clan); break;
		case BUILDWEALTH: quest = new BuildWealthQuest(clan); break;
		case LOYALTYQUEST: quest = new LoyaltyQuest(clan); break;
		case PERSECUTEHERETIC: quest = new PersecuteHeretic(clan); break;
		case PERSECUTEINFIDEL: quest = new PersecuteInfidel(clan); break;
		case PERSECUTEFOREIGNER: quest = new PersecuteForeigner(clan); break;
		default: quest = new DefaultQuest(clan); break;
		}
		return quest;
	}
}
