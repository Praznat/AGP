package Questing;

import Game.AGPmain;
import Questing.RomanceQuests.BreedQuest;
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
		
	public static class DefaultQuest extends Quest {
		public DefaultQuest(Clan P) {super(P);}
		@Override
		public void pursue() {Me.addReport(GobLog.idle());}
	}
	public static abstract class TargetQuest extends Quest {
		protected Clan target;
		public TargetQuest(Clan P) {super(P);}
		public TargetQuest(Clan P, Clan T) {super(P); target = T;}
		public void setTarget(Clan t) {target = t;}
	}
	public static interface FindTarget {
		public boolean meetsReq(Clan POV, Clan target);
	}
	public static abstract class FindTargetAbstract extends Quest implements FindTarget {
		protected static final int TRIESPERTURN = 3;
		public FindTargetAbstract(Clan P) {super(P);}
		@Override
		public void pursue() {
			Clan[] pop = Me.myShire().getCensus();
			for (int i = Math.min(TRIESPERTURN, pop.length); i > 0; i--) {
				Clan candidate = pop[AGPmain.rand.nextInt(pop.length)];
				if(meetsReq(Me, candidate)) {
					((TargetQuest) upQuest()).setTarget(candidate);  //must be called by TargetQuest
					success(Me.myShire()); break;
				}
			}
			failure(Me.myShire());
		}
	}
}
