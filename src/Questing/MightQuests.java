package Questing;

import java.util.*;

import AMath.Calc;
import Avatar.SubjectiveType;
import Defs.*;
import Descriptions.GobLog;
import Game.Contract;
import Questing.Quest.PatronedQuest;
import Questing.Quest.PatronedQuestFactory;
import Questing.Quest.TargetQuest;
import Questing.Quest.TransactionQuest;
import Sentiens.*;
import Sentiens.Law.Commandments;
import Sentiens.Values.Value;
import War.BattleField;

public class MightQuests {
	
	public static interface InvolvesArmy {
		public Set<Clan> getArmy();
		public void setArmy(Set<Clan> army);
	}
	
	public static PatronedQuestFactory getMinistryFactory() {return new PatronedQuestFactory(DefendPatron.class) {public Quest createFor(Clan c, Clan p) {return new DefendPatron(c, p);}};}
	
	// this should be the default patron quest WHEN NOT in same shire as Patron...
	// otherwise maybe Governor so become General
	public static class DefendPatron extends PatronedQuest {
		public DefendPatron(Clan P, Clan patron) {super(P, patron);}

		@Override
		public void pursue() {
			// TODO standby for FormArmy / train
		}
		@Override
		public String description() {return "Defend " + (Me == patron ? "self" : patron.getNomen());}
		
	}
	
	private static Set<Clan> getArmyOf(Clan c) {
		final Quest q = c.MB.QuestStack.peek();
		if (q instanceof FormArmy) {return ((FormArmy)q).getArmy();}
		else {return null;}
	}

	public static class FormOwnArmy extends FormArmy {
		private int lastArmySize = 0;
		public FormOwnArmy(Clan P) {super(P, P);}
		@Override
		public void pursue() {
			super.pursue();
			if (isReadyToFight(army, Me)) {
				((InvolvesArmy) upQuest()).setArmy(army);
				success();
				return;
			}
			if (army.size() <= lastArmySize) { // GIVE UP ( this stuff should be in the upper quest as it will be different between attack and defense
				for (Clan f : army) {
					if (f == Me) {continue;} // deal with me later
					final Quest topQuest = f.MB.QuestStack.peek();
					if (topQuest instanceof FormArmy) {topQuest.failure(patron);} //disband everybody
				}
				Me.addReport(GobLog.backedDown()); // could be run away if this is defense
				upQuest().finish(); // TODO or upQuest.changeStrategy()
				failure(Values.MIGHT); // not mighty enough (strictly speaking just not enough minions if just comparing army sizes)
				return;
			}
			lastArmySize = army.size();
		}
		private boolean isReadyToFight(Set<Clan> myArmy, Clan me) {
			final Quest myTopQuest = upQuest();
			if (TargetQuest.class.isAssignableFrom(myTopQuest.getClass())) {
				final Clan target = ((TargetQuest) myTopQuest).getTarget();
				Set<Clan> enemyArmy = null;
				final Quest targetTopQuest = target.MB.QuestStack.peek();
				if (targetTopQuest != null && FormArmy.class.isAssignableFrom(targetTopQuest.getClass())) {enemyArmy = ((FormArmy) targetTopQuest).getArmy();}
				return myArmy.size() * (me.FB.getBeh(M_.CONFIDENCE) + 5) >=
						(enemyArmy != null ? enemyArmy.size() : 1) * (me.FB.getBeh(M_.PARANOIA) + 5); //TODO overly simple
			}
			System.out.println("MightQuests THIS SHOULD NOT HAPPEN");
			return 1 / 0 < 8;
		}
	}
	public static class FormArmy extends PatronedQuest implements InvolvesArmy {
		protected final Set<Clan> army;
		private boolean doneRecruiting = false;
		public FormArmy(Clan P, Clan patron) {
			super(P, patron);
			if (patron == P) {army = new HashSet<Clan>();}
			else {
				army = getArmyOf(patron);
				if (army == null) {return;} // TODO will only form army if that is patron's top quest??
			}
			patron.addReport(GobLog.recruitForWar(Me));
			army.add(Me);
		}
		@Override
		public void pursue() {
			if (army == null) {finish();}
			if (doneRecruiting) {return;}
			if (Me.myOrder() != null) {
				Set<Clan> followers = Me.myOrder().getFollowers(Me, false, false);
				for (Clan f : followers) {
					final Quest topQuest = f.MB.QuestStack.peek();
					if (topQuest instanceof FormArmy) {continue;}
					// DOESNT COST TURN IF CANDIDATE'S QUEST IS ALREADY DEFENDPATRON (upside of standing army = instant formation of first tier)
					if (topQuest instanceof DefendPatron) {f.MB.newQ(new FormArmy(f, Me));}
//			Contract.getInstance().enter(e, p) ?
					else {f.MB.newQ(new FormArmy(f, patron)); return;}
				}
			}
			doneRecruiting = true;
		}
		public Set<Clan> getArmy() {return army;}
		public void setArmy(Set<Clan> army) {} // this really shouldnt even be here
		@Override
		public String description() {return army==null? "Form null army??" : "Form army (size:" + army.size() + ")";}
		
	}

	private static void chooseThreat(Clan subject, Clan object) {
		final Commandments commandments = subject.FB.commandments;
		if (commandments.Xenophobia.isSinful() || (subject.myOrder() != null && subject.myOrder() == object.myOrder())) {
			final boolean kill = !commandments.Murder.isSinful();
			final boolean steal = !commandments.Theft.isSinful();
			if (kill && steal) {Contract.getInstance().threatenLifeAndProperty();} // threaten annihilation
			else if (kill) {Contract.getInstance().threatenLife();} // threaten life
			else if (steal) {Contract.getInstance().threatenProperty();} // threaten property
			else {Contract.getInstance().threatenMight();} // threaten honor
		}
		else {Contract.getInstance().threatenLineage();} // threaten 2nd degree annihilation
	}
	public static boolean desiresFight(Clan pov, Clan opponent, boolean povIsDefender) {
		return expPvictory(pov, opponent, povIsDefender) > 0.5;
	}
	public static double expPvictory(Clan pov, Clan opponent, boolean povIsDefender) {
		// his size includes everyone in his top boss's order (except mine if same order)
		// assumption of big boss stepping in on side of defender (not attacker)
		final Clan hisTopBoss = povIsDefender ? pov.getTopBoss() : opponent.getTopBoss();
		final int aos = povIsDefender ? -1 : 1;
		double confidence = 1, fear = 1;
		// signum because different values are on different scales i.e. not mixable
		double result = Math.signum(Values.MIGHT.compare(pov, opponent, pov) + aos*Values.MIGHT.compare(pov, hisTopBoss, pov));
		if (result > 0) {confidence += result;} else {fear += result;}
		for (int i = 0; i < pov.FB.getBeh(M_.SUPERST) / 5; i++) {
			Value v = pov.FB.randomValueInPriority();
			result = Math.signum(v.compare(pov, opponent, pov) + aos*v.compare(pov, hisTopBoss, pov));
			if (result > 0) {confidence += result;} else {fear -= result;}
		}
		if (!opponent.FB.commandments.Murder.isSinful()) {fear *= (pov.FB.getBeh(M_.MIERTE) / 5 + 1);}
		confidence *= (pov.FB.getBeh(M_.CONFIDENCE) / 5 + 1);
		return confidence / (confidence + fear);
	}
	
	public static class ChallengeMight extends TransactionQuest {
		private final Value val;

		public ChallengeMight(Clan P) {super(P); val = P.FB.randomValueInPriority();}
		public ChallengeMight(Clan P, Value v) {super(P); val = v;}

		@Override
		protected FindTargetAbstract findWhat() {
			return new FindTargetAbstract(Me, TargetQuest.getReasonableCandidates(Me), Me) {
				@Override
				public boolean meetsReq(Clan POV, Clan target) {
					return target != Me && desiresFight(Me, target, false);
				}
				@Override
				protected int triesPerTurn() {return 1;} //expensive calc
			};
		}
		private void setDemandFromValue(Value v) {
			if (v == Values.WEALTH) {
				final int millet = Math.min(Me.getMillet(), target.getMillet()); //TODO figure it out
				contract().demandTribute(millet);
			}
			else if (v == Values.INFLUENCE || v == Values.ALLEGIANCE) {
				contract().demandAllegiance();
			}
			else if (v == Values.RIGHTEOUSNESS) {contract().demandRepentance();}
			// TODO beauty should be give up mate if mate is desired
			else if (v == Values.MIGHT) {
				// TODO just fight
			}
			else {contract().demandService(v);}
		}
		@Override
		protected void setContractDemand() {
			setDemandFromValue(val);
		}
		@Override
		protected void avatarSetContractDemand() {
			avatarConsole().showChoices("Choose demand", Me, Values.All,
					SubjectiveType.VALUE_ORDER, new Calc.Listener() {
				@Override
				public void call(Object arg) {
					setDemandFromValue((Value)arg);
				}
			}, new Calc.Transformer<Value, String>() {
				@Override
				public String transform(Value v) {
					if (v == Values.WEALTH) {return "Demand Tribute";}
					else if (v == Values.INFLUENCE || v == Values.ALLEGIANCE) {
						return "Demand Allegiance";
					}
					else if (v == Values.RIGHTEOUSNESS) {return "Demand Repentance";}
					else if (v == Values.MIGHT) {
						return "Demand Unconditional Surrender";
					}
					else {return "Demand " + v.getMinistry().getDesc(target) + " Service";}
				}
			});
		}

		@Override
		protected void setContractOffer() {chooseThreat(Me, target);}

		@Override
		protected void successCase() {this.success(target);}

		@Override
		protected void failCase() {
			replaceAndDoNewQuest(Me, new AttackClanQuest(Me, target));
		}

		@Override
		protected void report(boolean success) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public String description() {return "Make challenge" + (target!=null?" to "+target.getNomen():"");}
	
	}
	
	public static class AttackClanQuest extends TargetQuest implements InvolvesArmy {
		private Set<Clan> army;
		public AttackClanQuest(Clan P, Clan target) {super(P, target);}
		@Override
		public void pursue() {
			if (army == null) {Me.MB.newQ(new FormOwnArmy(Me));}
			//TODO include some code for moving to target shire
			else {
				BattleField.setupNewBattleField(Me, target, target.myShire());
				if (BattleField.wasVictorious(Me)) {success(Values.MIGHT);}
				else {failure(Values.MIGHT);}
				return;
			}
		}
		@Override
		public String description() {
			return "Attack " + target.getNomen() + (army!=null?" (army:"+army.size()+")":"");
		}
		@Override
		public Set<Clan> getArmy() {return army;}
		@Override
		public void setArmy(Set<Clan> army) {this.army = army;}
		
	}
	
	
}
