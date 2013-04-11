package Questing;

import AMath.Calc;
import Avatar.*;
import Defs.*;
import Game.*;
import Questing.AllegianceQuests.AllegianceQuest;
import Questing.PersecutionQuests.PersecuteHeretic;
import Questing.PersecutionQuests.PersecuteInfidel;
import Questing.PowerStartingQuests.IndPowerQuest;
import Questing.PropertyQuests.BuildWealthQuest;
import Questing.RomanceQuests.BreedQuest;
import Sentiens.*;
import Sentiens.Stressor.Causable;

public abstract class Quest {
	protected Clan Me;
	public Quest(Clan P) {Me = P;}
	public abstract void pursue();
	public void avatarPursue() {pursue();}  //default leaves it to AI
	
	protected void success() {Me.MB.finishQ();   if (Me.MB.QuestStack.empty()) {Me.AB.catharsis(1);}}
	protected void success(Stressor.Causable relief) {Me.AB.relieveFrom(new Stressor(Stressor.ANNOYANCE, relief));   success();}
	protected void finish() {Me.MB.finishQ();}
	protected void failure(Stressor.Causable blamee) {Me.AB.add(new Stressor(Stressor.ANNOYANCE, blamee));   finish();}
	protected Quest upQuest() {return Me.MB.QuestStack.peekUp();}
	public String shortName() {return description();}
	public String description() {return "Undefined Quest";}
	@Override
	public String toString() {return description();}
	
	protected static AvatarConsole avatarConsole() {return AGPmain.mainGUI != null ? AGPmain.mainGUI.AC : null;}
	public Clan avatar() {return avatarConsole().getAvatar();}
		
	public static class DefaultQuest extends PatronedQuest {
		public static PatronedQuestFactory getMinistryFactory() {return new PatronedQuestFactory(DefaultQuest.class) {public Quest createFor(Clan c) {return new DefaultQuest(c);}};}
		
		public DefaultQuest(Clan P) {super(P);}
		@Override
		public void pursue() {Me.addReport(GobLog.idle()); Me.MB.finishQ();}
	}
	public static abstract class PatronedQuest extends Quest {
		protected Clan patron;
		public PatronedQuest(Clan P) {this(P, P);}
		public PatronedQuest(Clan P, Clan patron) {super(P); this.patron = patron;}
	}
	public static abstract class TargetQuest extends Quest {
		protected Clan target;
		public TargetQuest(Clan P) {super(P);}
		public TargetQuest(Clan P, Clan T) {super(P); target = T;}
		public void setTarget(Clan t) {target = t;}
		protected Clan getTarget() {return target;}
		public static Clan[] getReasonableCandidates(Clan pov) {
			if (pov == pov.myShire().getGovernor()) {
				// TODO return neighbor governors...
			}
			return pov.myShire().getCensus();
		}
	}
	public static interface RelationCondition {
		public boolean meetsReq(Clan POV, Clan target);
	}
	public static abstract class FindTargetAbstract extends Quest implements RelationCondition {
		protected final Clan[] candidates;
		protected final Causable blameSource;
		public FindTargetAbstract(Clan P) {this(P, TargetQuest.getReasonableCandidates(P), P.myShire());}
		public FindTargetAbstract(Clan P, Clan[] candidates, Causable c) {
			super(P);
			this.candidates = candidates;
			this.blameSource = c;
		}
		public void setTarget(Clan c) {
			((TargetQuest) upQuest()).setTarget(c);  //must be called by TargetQuest
		}
		@Override
		public void pursue() {
			for (int i = Math.min(triesPerTurn(), candidates.length); i > 0; i--) {
				Clan candidate = candidates[AGPmain.rand.nextInt(candidates.length)];
				if(meetsReq(Me, candidate)) {
					setTarget(candidate);
					success(Me.myShire()); return;
				}
			}
			failure(Me.myShire());
		}
		@Override
		public void avatarPursue() {
			avatarConsole().showChoices(Me, Me.myShire().getCensus(), SubjectiveType.RESPECT_ORDER, new Calc.Listener() {
				@Override
				public void call(Object arg) {
					FindTargetAbstract.this.setTarget((Clan) arg);
					Me.MB.finishQ();
				}
			});
		}
		protected int triesPerTurn() {return 3;} //maybe size of pub?
	}

	public static abstract class TransactionQuest extends TargetQuest {
		protected int timesLeft;
		public TransactionQuest(Clan P) {super(P); timesLeft = Me.useBeh(M_.PATIENCE) / 3 + 3;}
		@Override
		public void pursue() {
			if (timesLeft == 0) {return;}  //no one found
			if (target == null) {Me.MB.newQ(findWhat()); timesLeft--; return;}
			Contract.getInstance().enter(target, Me);
			setContractDemand();
			setContractOffer();
			boolean accepted = Contract.getInstance().acceptable();
			if (accepted) {
				Contract.getInstance().enact();
				successCase();
			}
			else {failCase();}
			report(accepted);
			return;
		}
		protected abstract FindTargetAbstract findWhat();
		protected abstract void setContractDemand();
		protected abstract void setContractOffer();
		protected abstract void successCase();
		protected abstract void failCase();
		protected abstract void report(boolean success);
	}

	public static abstract class PatronedQuestFactory {
		private Class<? extends PatronedQuest> questType;
		public PatronedQuestFactory(Class<? extends PatronedQuest> clasz) {questType = clasz;}
		public abstract Quest createFor(Clan c);
		public Class<? extends PatronedQuest> getQuestType() {return questType;}
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
		case CREEDQUEST: quest = new CreedQuests.PriestQuest(clan, clan); break;
		case LOYALTYQUEST: quest = new AllegianceQuest(clan); break;
		case SPLENDORQUEST: quest = new SplendorQuests.UpgradeDomicileQuest(clan, clan); break;
		case FAITHQUEST: quest = new FaithQuests.ContactQuest(clan, clan); break;
		case LEGACYQUEST: quest = new LegacyQuests.LegacyQuest(clan, clan); break;
		case KNOWLEDGEQUEST: quest = new KnowledgeQuests.KnowledgeQuest(clan, clan); break;
		case BUILDPOPULARITY: quest = new InfluenceQuests.InfluenceQuest(clan, clan); break;
		case INDPOWERQUEST: quest = new IndPowerQuest(clan); break;
		case TRAIN: quest = new ExpertiseQuests.LearnQuest(clan); break;
		case PERSECUTEHERETIC: quest = new PersecuteHeretic(clan); break;
		case PERSECUTEINFIDEL: quest = new PersecuteInfidel(clan); break;
		default: quest = new DefaultQuest(clan); break;
		}
		return quest;
	}
	
	public static class GradedQuest {
		private final Quest quest;
		private final double rating;
		public GradedQuest(Quest quest, double rating) {this.quest = quest; this.rating = rating;}
		public Quest getQuest() {return quest;}
		public double getRating() {return rating;}
		@Override
		public String toString() {return quest.description();}
	}
}
