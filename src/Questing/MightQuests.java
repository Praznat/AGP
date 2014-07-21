package Questing;

import java.util.*;

import AMath.Calc;
import Avatar.SubjectiveType;
import Defs.M_;
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
		public Set<FormArmy> getArmy();
		public void setArmy(Set<FormArmy> army);
	}
	
	public static PatronedQuestFactory getMinistryFactory() {return new PatronedQuestFactory(DefendPatron.class) {public Quest createFor(Clan c, Clan p) {return new DefendPatron(c, p);}};}
	
	// this should be the default patron quest WHEN NOT in same shire as Patron...
	// otherwise maybe Governor so become General
	// also what if patron=me?
	public static class DefendPatron extends PatronedQuest {
		public DefendPatron(Clan P, Clan patron) {super(P, patron);}

		@Override
		public void pursue() {
			// TODO standby for FormArmy / train (hah doesnt matter who patron is, always ready to fight!)
		}
		@Override
		public String description() {return "Defend " + (Me == patron ? "self" : patron.getNomen());}
		
	}
	
	public static class FormOwnArmy extends FormArmy {
		protected Set<FormArmy> army;
		private int lastArmySize = 0;
		private int timesLeft;
		public FormOwnArmy(Clan P) {
			super(P, null);
			if (army == null) army = new HashSet<FormArmy>();
			army.add(this);
			((InvolvesArmy) Me.MB.QuestStack.peek()).setArmy(army);
			timesLeft = Me.FB.getBeh(M_.PATIENCE) / 4 + 2;
		}
		@Override
		public void pursue() {
			if (!checkArmyStatus()) {return;}
			if (army.isEmpty()) {throw new IllegalStateException("damni ttt this check supposed to happen in super.pursue() already!");}
			final Quest tmpyTemp = upQuest();
			final Clan tempyMcWempy = ((AttackClanQuest) tmpyTemp).getTarget(); // upquest should always be attackclan
			Set<FormArmy> tmp = enemyArmy(tempyMcWempy);
			if (isReadyToFight(army)) {
				System.out.println("ready to fight at size = " + army.size() + " vs " + (tmp == null ? 1 : tmp.size()));
				((AttackClanQuest) upQuest()).beReady();
				success();
				return;
			}
			super.pursue();
			if (army.size() <= lastArmySize) timesLeft--;
			if (timesLeft <= 0) { // GIVE UP ( this stuff should be in the upper quest as it will be different between attack and defense
				System.out.println("giving up at army size = " + army.size() + " vs " + (tmp == null ? 1 : tmp.size()));
				giveUp();
				return;
			}
			lastArmySize = army.size();
		}
		private void giveUp() { // TODO should be run away, regroup, sue for peace?
			AttackClanQuest upQ = ((AttackClanQuest) upQuest());
			AttackClanQuest targetClanQ = (AttackClanQuest)upQ.getTarget().MB.QuestStack.getOfType(AttackClanQuest.class);
			if (targetClanQ != null) targetClanQ.win();

			Me.addReport(GobLog.backedDown()); // could be run away?
			upQ.lose();
		}
		private Set<FormArmy> enemyArmy(Clan enemy) {
			Set<FormArmy> enemyArmy = null;
			final Quest targetTopQuest = enemy.MB.QuestStack.peek();
			if (targetTopQuest != null && FormArmy.class.isAssignableFrom(targetTopQuest.getClass())) {enemyArmy = ((FormArmy) targetTopQuest).getArmy();}
			return enemyArmy;
		}
		private boolean isReadyToFight(Set<FormArmy> myArmy) {
			final Quest myTopQuest = upQuest();
			final Clan target = ((AttackClanQuest) myTopQuest).getTarget(); // upquest should always be attackclan
			return isReadyToFight(enemyArmy(target), target, myArmy, Me, timesLeft);
		}
		/** 
		 * TODO there should be many different ways of deciding this
		 * default is: compare current advantage to potential advantage and wait if current is less than potential
		 * and still have time left, only attacking if above confidence threshold
		 * */
		private static boolean isReadyToFight(Set<FormArmy> enemyArmy, Clan enemy, Set<FormArmy> myArmy, Clan me, int timeLeft) {
			double currentAdvantage = (myArmy.size() +1) / ((enemyArmy != null ? enemyArmy.size() : 1) +1); // the +1s shouldnt be necessary
			double ultimatePotentialAdvantage = (double)me.getMinionTotal() / enemy.getMinionTotal();
			double confidenceFactor = (me.FB.getBeh(M_.CONFIDENCE) + 15) / (me.FB.getBeh(M_.PARANOIA) + 15); //TODO overly simple
			if (timeLeft <= 1) { // cant wait anymore
				return currentAdvantage * confidenceFactor >= 1;
			}
			return currentAdvantage >= ultimatePotentialAdvantage && currentAdvantage * confidenceFactor >= 1;
		}
		public Set<FormArmy> getArmy() {return army;}
		@Override
		public String description() {
			return army!=null? "Form " + army.size() + "p army for self" : "Form army for self";
		}
	}
	public static class FormArmy extends Quest implements InvolvesArmy {
		protected FormArmy root;
		private boolean doneRecruiting = false;
		public FormArmy(Clan P, FormArmy root) {
			super(P);
			this.root = root;
			if (root != null) {
				Clan recruiter = root.getDoer();
				Me.addReport(GobLog.recruitForWar(recruiter, Me));
				if (Me != root.getDoer()) {recruiter.addReport(GobLog.recruitForWar(recruiter, Me));}
			}
		}
		protected void recruit() {
			if (Me.myOrder() != null) {
				Set<Clan> followers = Me.myOrder().getFollowers(Me, false, false);
				for (Clan f : followers) {
					final Quest topQuest = f.MB.QuestStack.peek();
					if (topQuest instanceof FormArmy) {continue;}
					// DOESNT COST TURN IF CANDIDATE'S QUEST IS ALREADY DEFENDPATRON (upside of standing army = instant formation of first tier)
					if (topQuest instanceof DefendPatron) {f.MB.newQ(new FormArmy(f, this));}
//			Contract.getInstance().enter(e, p) ?
					else {f.MB.newQ(new FormArmy(f, this)); return;}
				}
			}
			doneRecruiting = true;
		}
		protected boolean checkArmyStatus() {
			Set<FormArmy> army = getArmy();
			boolean isActive = army != null && !army.isEmpty();
			if (!isActive) finish();
			return isActive;
		}
		@Override
		public void pursue() {
			if (!checkArmyStatus()) {return;}
			if (root != null) { // root == null means this is FormOwnArmy
				Set<FormArmy> army = root.getArmy();
				if (army != null) {
					Clan recruiter = root.getDoer();
					if (Me.currentShire() == recruiter.currentShire()) {army.add(this);}
					else {Me.moveTowards(recruiter.currentShire());}
				}
			}
			
			if (!doneRecruiting) {recruit();} // just chill until disband?
		}
		@Override
		public Set<FormArmy> getArmy() {return root != null ? root.getArmy() : null;}
		@Override
		public void setArmy(Set<FormArmy> army) {} // this really shouldnt even be here
		@Override
		public String description() {
			Set<FormArmy> army = getArmy(); return army==null? "Form null army??" : "Form " + army.size() + "p army for " + root.getDoer().getFirstName();
		}
		
	}

	private static void chooseThreat(Clan subject, Clan object) {
		if (Commandments.INSTANCE.Xenophobia.getFor(subject).isSinful() || (subject.myOrder() != null && subject.myOrder() == object.myOrder())) {
			final boolean kill = !Commandments.INSTANCE.Murder.getFor(subject).isSinful();
			final boolean steal = !Commandments.INSTANCE.Theft.getFor(subject).isSinful();
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
		if (!Commandments.INSTANCE.Murder.getFor(opponent).isSinful()) {fear *= (pov.FB.getBeh(M_.MIERTE) / 5 + 1);}
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
			target.MB.newQ(new AttackClanQuest(target, Me));
		}

		@Override
		protected void report(boolean success) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public String description() {return "Make challenge" + (target!=null?" to "+target.getNomen():"");}
	
	}
	
	/** single attack, not drawn-out war */
	public static class AttackClanQuest extends TargetQuest implements InvolvesArmy {
		enum Status {FORMING, VICTORIOUS, DEFEATED, READY};
		private Status status = Status.FORMING;
		private Set<FormArmy> army;
		public AttackClanQuest(Clan P, Clan target) {super(P, target);}
		@Override
		public void pursue() {
			if (status == Status.FORMING) {Me.MB.newQ(new FormOwnArmy(Me));}
			//TODO include some code for moving to target shire
			else {
				if (army.isEmpty()) {onDisbandedArmy(Me, status); return;}
				BattleField.setupNewBattleField(Me, target, target.myShire());
				Quest enemyQ = target.MB.QuestStack.getOfType(AttackClanQuest.class);
				if (BattleField.wasVictorious(Me)) {win();   if (enemyQ != null) {((AttackClanQuest)enemyQ).lose();}}
				else {lose();   if (enemyQ != null) {((AttackClanQuest)enemyQ).win();}}
				return;
			}
		}
		@Override
		public String description() {
			return "Attack " + target.getNomen() + (army!=null?" (army:"+army.size()+")":"");
		}
		@Override
		public Set<FormArmy> getArmy() {return army;}
		@Override
		public void setArmy(Set<FormArmy> army) {this.army = army;}
		
		private void disband() {
			if (army == null) {return;} // maybe opponent gave up before you could even form army
			for (FormArmy fa : army) {
				fa.specialDelete();//disband everybody
			}
			Me.addReport(GobLog.disbanded());
			army.clear();
		}
		
		public void win() {
			status = Status.VICTORIOUS;
			disband();
		}
		public void lose() {
			status = Status.DEFEATED;
			disband();
		}
		public void beReady() {status = Status.READY;}
		
	}
	
	private static void onDisbandedArmy(Clan clan, AttackClanQuest.Status status) {
		clan.MB.finishQ();
	}
	
}
