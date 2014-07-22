package Questing;

import java.util.*;

import Defs.*;
import Descriptions.*;
import Descriptions.GobLog.Reportable;
import Questing.MightQuests.DefendPatron;
import Questing.Quest.TargetQuest;
import Questing.WarQuests.WarQuest.Status;
import Sentiens.Clan;
import War.BattleField;

public class WarQuests {
	
	public static interface InvolvesArmy {
		public Set<FormArmy> getArmy();
		public void setArmy(Set<FormArmy> army);
		public Clan getDoer(); // as in Quest
	}

	/** single attack, not drawn-out war */
	public static class WarQuest extends TargetQuest implements InvolvesArmy {
		enum Status {FORMING, MANEUVERING, REGROUPING, VICTORIOUS, DEFEATED, DEFER};
		private Status status = Status.FORMING;
		private Set<Clan> enemies = new HashSet<Clan>();
		private Set<FormArmy> army = new HashSet<FormArmy>();
		public WarQuest(Clan P, Clan target) {
			super(P, target);
			WarQuest preexisting = (WarQuest)Me.MB.QuestStack.getOfType(WarQuest.class);
			if (preexisting != null) {
				preexisting.enemies.add(target);
				preexisting.chooseCurrentTarget();
				status = Status.DEFER; // get rid of this quest
			}
		}
		private void chooseCurrentTarget() { // TODO figure out which enemy to prioritise
			for (Clan enemy : enemies) target = enemy;
		}
		@Override
		public void pursue() {
			switch (status) {
			case DEFER: finish(); break;
			case FORMING: Me.MB.newQ(new FormOwnArmy(Me, this)); break;
			case MANEUVERING: Me.MB.newQ(new ManeuverForBattle(Me, this)); break;
			default: if (army.isEmpty()) {onDisbandedArmy(Me, status); break;}
			}
			//TODO include some code for moving to target shire
//			else {
//				if (army.isEmpty()) {onDisbandedArmy(Me, status); return;}
//				BattleField.setupNewBattleField(Me, target, target.myShire());
//				Quest enemyQ = target.MB.QuestStack.getOfType(AttackClanQuest.class);
//				if (BattleField.wasVictorious(Me)) {win();   if (enemyQ != null) {((AttackClanQuest)enemyQ).lose();}}
//				else {lose();   if (enemyQ != null) {((AttackClanQuest)enemyQ).win();}}
//				return;
//			}
		}
		@Override
		public String description() {
			if (status == Status.DEFER) return "deferred";
			return "Attack " + target.getNomen() + (army!=null?" (army:"+army.size()+")":"") + "<"+status;
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
			enemies.remove(target); 
			if (enemies.isEmpty()) {disband();}
			else {chooseCurrentTarget();}
		}
		public void lose() {
			status = Status.DEFEATED;
			disband();
		}
		public void beReady() {status = Status.MANEUVERING;}
		
		private static void onDisbandedArmy(Clan clan, WarQuest.Status status) {
			clan.MB.finishQ();
		}
	}
	
	
	
	
	
	
	
	
	public static class FormOwnArmy extends FormArmy {
		protected WarQuest root;
		private int lastArmySize = 0;
		private int timesLeft;
		public FormOwnArmy(Clan P, WarQuest root) {
			super(P, null);
			this.root = root;
			root.getArmy().add(this);
			timesLeft = Me.FB.getBeh(M_.PATIENCE) / 4 + 2;
		}
		@Override
		public void pursue() {
			if (root.status != Status.FORMING) {finish(); Me.pursue(); return;}
			if (!checkArmyStatus()) {return;} // really?
			Set<FormArmy> army = root.getArmy();
			if (army.isEmpty()) {throw new IllegalStateException("damni ttt this check supposed to happen in super.pursue() already!");}
			final Clan tempyMcWempy = root.getTarget();
			Set<FormArmy> tmp = enemyArmy(tempyMcWempy);
			if (isReadyToFight(army)) {
				System.out.println("ready to fight at size = " + army.size() + " vs " + (tmp == null ? 1 : tmp.size()));
				root.beReady();
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
			Clan target = root.getTarget();
			WarQuest targetClanQ = (WarQuest)target.MB.QuestStack.getOfType(WarQuest.class);
			if (targetClanQ != null) targetClanQ.win();

			Reportable log = GobLog.backedDown(Me, target);
			Me.addReport(log); // could be run away?
			target.addReport(log); // could be run away?
			root.lose();
		}
		private Set<FormArmy> enemyArmy(Clan enemy) {
			FormArmy enemyFa = (FormArmy)enemy.MB.QuestStack.getOfType(FormArmy.class);
			return enemyFa != null ? enemyFa.getArmy() : null;
		}
		private boolean isReadyToFight(Set<FormArmy> myArmy) {
			final Clan target = root.getTarget();
			return isReadyToFight(enemyArmy(target), target, myArmy, Me, timesLeft);
		}
		/** 
		 * TODO there should be many different ways of deciding this
		 * default is: compare current advantage to potential advantage and wait if current is less than potential
		 * and still have time left, only attacking if above confidence threshold
		 * */
		private static boolean isReadyToFight(Set<FormArmy> enemyArmy, Clan enemy, Set<FormArmy> myArmy, Clan me, int timeLeft) {
			double currentAdvantage = (double)(myArmy.size() +1) / ((enemyArmy != null ? enemyArmy.size() : 1) +1); // the +1s shouldnt be necessary
			double ultimatePotentialAdvantage = ((double)me.getMinionTotal() + 1) / (enemy.getMinionTotal() + 1); // these +1s are good
			double confidenceFactor = (double)(me.FB.getPrs(P_.BATTLEP) + 1) / (enemy.FB.getPrs(P_.BATTLEP) + 1) *
					(me.FB.getBeh(M_.CONFIDENCE) + 15) / (me.FB.getBeh(M_.PARANOIA) + 15); //TODO overly simple
			
			if (timeLeft <= 1) { // cant wait anymore
				return currentAdvantage * confidenceFactor >= 1;
			}
			return currentAdvantage >= ultimatePotentialAdvantage && currentAdvantage * confidenceFactor >= 1;
		}
		public Set<FormArmy> getArmy() {return root.getArmy();}
		@Override
		public String description() {
			return root.getArmy()!=null? "Form " + root.getArmy().size() + "p army for self" : "Form army for self";
		}
	}
	
	
	
	
	public static class FormArmy extends Quest implements InvolvesArmy {
		protected InvolvesArmy root;
		private boolean doneRecruiting = false;
		public FormArmy(Clan P, InvolvesArmy root) {
			super(P);
			this.root = root;
			if (root != null) {
				Clan recruiter = root.getDoer();
				Me.addReport(GobLog.recruitForWar(recruiter, Me));
				if (Me != root.getDoer()) {recruiter.addReport(GobLog.recruitForWar(recruiter, Me));}
			}
		}
		static boolean recruit(Clan recruiter, InvolvesArmy root) {
			if (recruiter.myOrder() != null) {
				Set<Clan> followers = recruiter.myOrder().getFollowers(recruiter, false, false);
				//TODO should get all DefendPatrons first... otherwise guy with some defenders but lots of non defendes has low chan
				for (Iterator<Clan> iter = followers.iterator(); iter.hasNext();) {
					Clan f = iter.next();
					final Quest topQuest = f.MB.QuestStack.peek();
					
					if (topQuest instanceof FormArmy) {iter.remove();   continue;}
					// DOESNT COST TURN IF CANDIDATE'S QUEST IS ALREADY DEFENDPATRON (upside of standing army = instant formation of first tier)
					if (topQuest instanceof DefendPatron) {iter.remove();   f.MB.newQ(new FormArmy(f, root));}
//			Contract.getInstance().enter(e, p) ?
				}
				for (Clan f : followers) {f.MB.newQ(new FormArmy(f, root)); return false;}
			}
			return true;
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
			
			if (!doneRecruiting) {doneRecruiting = recruit(Me, this);} // just chill until disband?
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
	
	
	
	
	
	
	
	public static class ManeuverForBattle extends Quest {
		private WarQuest root;
		private int timeToSetUp = 1;
		public ManeuverForBattle(Clan P, WarQuest root) {
			super(P);
			this.root = root;
		}
		@Override
		public void pursue() {
			if (root.status != Status.MANEUVERING) {finish(); Me.pursue(); return;}
			if (timeToSetUp > 0) {timeToSetUp--; return;}
			Clan target = root.getTarget();
			BattleField.setupNewBattleField(Me, target, target.myShire());
			Quest enemyQ = target.MB.QuestStack.getOfType(WarQuest.class);
			if (BattleField.wasVictorious(Me)) {root.win();   if (enemyQ != null) {((WarQuest)enemyQ).lose();}}
			else {root.lose();   if (enemyQ != null) {((WarQuest)enemyQ).win();}}
			finish();
		}
		@Override
		public String description() {
			return "Organizing army for attack";
		}
	}
	
}
