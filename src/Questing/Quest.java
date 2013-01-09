package Questing;

import AMath.Calc;
import Avatar.*;
import Defs.Q_;
import GUI.APopupMenu;
import Game.*;
import Game.Do.ClanOnClan;
import Questing.AllegianceQuests.AllegianceQuest;
import Questing.PersecutionQuests.PersecuteForeigner;
import Questing.PersecutionQuests.PersecuteHeretic;
import Questing.PersecutionQuests.PersecuteInfidel;
import Questing.PowerStartingQuests.IndPowerQuest;
import Questing.RomanceQuests.BreedQuest;
import Questing.PropertyQuests.*;
import Sentiens.*;

public abstract class Quest {
	protected Clan Me;
	public Quest(Clan P) {Me = P;}
	public abstract void pursue();
	public void avatarPursue() {pursue();}  //default leaves it to AI
	
	protected void success() {Me.MB.finishQ();   if (Me.MB.QuestStack.empty()) {Me.AB.catharsis(1);}}
	protected void success(Stressor.Causable relief) {Me.AB.relieveFrom(new Stressor(Stressor.ANNOYANCE, relief));   success();}
	protected void failure() {Me.MB.finishQ();}
	protected void failure(Stressor.Causable blamee) {Me.AB.add(new Stressor(Stressor.ANNOYANCE, blamee));   failure();}
	protected Quest upQuest() {return Me.MB.QuestStack.peekUp();}
	public String shortName() {return description();}
	public String description() {return "Undefined Quest";}
	@Override
	public String toString() {return description();}
	
	protected static AvatarConsole avatarConsole() {return AGPmain.mainGUI != null ? AGPmain.mainGUI.AC : null;}
	public Clan avatar() {return avatarConsole().getAvatar();}
		
	public static class DefaultQuest extends Quest {
		public static QuestFactory getFactory() {return new QuestFactory(DefaultQuest.class) {public Quest createFor(Clan c) {return new DefaultQuest(c);}};}
		
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
		public void setTarget(Clan c) {
			((TargetQuest) upQuest()).setTarget(c);  //must be called by TargetQuest
		}
		@Override
		public void pursue() {
			Clan[] pop = Me.myShire().getCensus();
			for (int i = Math.min(TRIESPERTURN, pop.length); i > 0; i--) {
				Clan candidate = pop[AGPmain.rand.nextInt(pop.length)];
				if(meetsReq(Me, candidate)) {
					setTarget(candidate);
					success(Me.myShire()); return;
				}
			}
			failure(Me.myShire());
		}
		@Override
		public void avatarPursue() {
			avatarConsole().showChoices(Me, Me.myShire().getCensus(), SubjectivelyComparable.Type.RESPECT_ORDER, new Calc.Listener() {
				@Override
				public void call(Object arg) {
					FindTargetAbstract.this.setTarget((Clan) arg);
					Me.MB.finishQ();
				}
			});
		}
	}

	public static abstract class QuestFactory {
		private Class<? extends Quest> questType;
		public QuestFactory(Class<? extends Quest> clasz) {questType = clasz;}
		public abstract Quest createFor(Clan c);
		public Class<? extends Quest> getQuestType() {return questType;}
	}
	
	protected static void replaceAndDoNewQuest(Clan c, Quest newQuest) {
		c.MB.finishQ();
		c.MB.newQ(newQuest);
		if (c == avatarConsole().getAvatar()) {avatarConsole().avatarPursue();}
		else {c.pursue();}
	}
	
	public static Quest QtoQuest(Clan clan, Q_ q) {
		if (q == null) {return new DefaultQuest(clan);}
		Quest quest;
		switch(q) {
		case BREED: quest = new BreedQuest(clan); break;
		case BUILDWEALTH: quest = new BuildWealthQuest(clan); break;
		case LOYALTYQUEST: quest = new AllegianceQuest(clan); break;
		case BUILDPOPULARITY: quest = new InfluenceQuests.InfluenceQuest(clan); break;
		case INDPOWERQUEST: quest = new IndPowerQuest(clan); break;
		case PERSECUTEHERETIC: quest = new PersecuteHeretic(clan); break;
		case PERSECUTEINFIDEL: quest = new PersecuteInfidel(clan); break;
		case PERSECUTEFOREIGNER: quest = new PersecuteForeigner(clan); break;
		default: quest = new DefaultQuest(clan); break;
		}
		return quest;
	}
	
	public static class GradedQuest implements SubjectivelyComparable {
		private final Quest quest;
		private final double rating;
		public GradedQuest(Quest quest, double rating) {this.quest = quest; this.rating = rating;}
		public Quest getQuest() {return quest;}
		public double getRating() {return rating;}
		@Override
		public String toString() {return quest.description();}
	}
}
