package Questing;

import Avatar.AvatarConsole;
import Defs.Q_;
import GUI.APopupMenu;
import Game.*;
import Game.Do.ClanAlone;
import Game.Do.ClanOnClan;
import Questing.PersecutionQuests.PersecuteForeigner;
import Questing.PersecutionQuests.PersecuteHeretic;
import Questing.PersecutionQuests.PersecuteInfidel;
import Questing.PowerStartingQuests.IndPowerQuest;
import Questing.RomanceQuests.BreedQuest;
import Questing.OrderQuests.LoyaltyQuest;
import Questing.WorkQuests.BuildWealthQuest;
import Sentiens.*;
import Sentiens.Values.Value;

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
	
	protected AvatarConsole avatarConsole = AGPmain.mainGUI.AC;
	public Clan avatar() {return avatarConsole.getAvatar();}
		
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
			Clan[] pop = Me.myShire().getCensus();
			avatarConsole.choices.clear();
			avatarConsole.getComparator().setPOV(Me);
			avatarConsole.getComparator().setComparator(avatarConsole.getComparator().RESPECT_ORDER);
			ClanOnClan action;
			for (Clan c : pop) {
				if(meetsReq(Me, c)) {
					action = Do.chooseTarget(c);
					action.setup(Me, c, 0);
					if (avatarConsole.choices.containsValue(action)) {continue;}
					avatarConsole.choices.put(c, action);
				}
			}
			new APopupMenu(avatarConsole, avatarConsole.choices.values());
		}
	}
	
	
	
	public static Quest QtoQuest(Clan clan, Q_ q) {
		if (q == null) {return new DefaultQuest(clan);}
		Quest quest;
		switch(q) {
		case BREED: quest = new BreedQuest(clan); break;
		case BUILDWEALTH: quest = new BuildWealthQuest(clan); break;
		case LOYALTYQUEST: quest = new LoyaltyQuest(clan); break;
		case INDPOWERQUEST: quest = new IndPowerQuest(clan); break;
		case PERSECUTEHERETIC: quest = new PersecuteHeretic(clan); break;
		case PERSECUTEINFIDEL: quest = new PersecuteInfidel(clan); break;
		case PERSECUTEFOREIGNER: quest = new PersecuteForeigner(clan); break;
		default: quest = new DefaultQuest(clan); break;
		}
		return quest;
	}
}
