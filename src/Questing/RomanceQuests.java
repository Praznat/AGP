package Questing;

import Defs.M_;
import Game.Defs;
import Questing.Quest.FindTargetAbstract;
import Questing.Quest.PatronedQuest;
import Questing.Quest.PatronedQuestFactory;
import Questing.Quest.RelationCondition;
import Questing.Quest.TargetQuest;
import Sentiens.*;
import Sentiens.Stressor.Causable;

public class RomanceQuests {
	public static PatronedQuestFactory getMinistryFactory() {return new PatronedQuestFactory(BreedWithBossQuest.class) {public Quest createFor(Clan c) {return new BreedWithBossQuest(c);}};}

	public static class BreedWithBossQuest extends PatronedQuest {
		public BreedWithBossQuest(Clan P) {super(P, P.getBoss());}
		@Override
		public void pursue() {
			replaceAndDoNewQuest(Me, new BreedQuest(Me));
		}
	}
	public static class BreedQuest extends TargetQuest {
		private int courtsLeft = Defs.E;
		private int failsLeft = Defs.E;
		public BreedQuest(Clan P) {super(P); resetFails();}
		public BreedQuest(Clan P, Clan target) {this(P); this.target = target;}
		@Override
		public void pursue() {
			if (courtsLeft == 0) {success(Me); return;}
			if (failsLeft == 0) {failure(Me); return;}
			if (target == null) {Me.MB.newQ(new FindMate(Me)); failsLeft--; return;}  //failsleft-- cuz it means findmate failed
			resetFails();   //previous fails were for finding target
			if (courtsLeft == Defs.E) {courtsLeft = 16 - target.useBeh(M_.PROMISCUITY);}
			Me.MB.newQ(new Compete4MateQuest(Me, target));
		}
		private void resetFails() {failsLeft = Me.useBeh(M_.PATIENCE) / 3 + 1;}
		public void courtSucceeded() {courtsLeft--;}
		public void courtFailed() {failsLeft--;}
		@Override
		public String shortName() {return "Breed";}
		@Override
		public String description() {return "Breed with " + (target==null ? "someone" : target.getNomen());}
	}

	public static class FindMate extends FindTargetAbstract implements RelationCondition {
		public FindMate(Clan P) {super(P);}
		@Override
		public boolean meetsReq(Clan POV, Clan target) {
			boolean success = POV.getGender() != target.getGender() &&
			POV.FB.randomValueInPriority().compare(POV, target, POV) + Values.MAXVAL * POV.useBeh(M_.PROMISCUITY) / 15 > 0;
			Me.addReport(GobLog.findSomeone((success ? target : null), "mate"));
			return success;
		}
		@Override
		public String shortName() {return "Find Mate";}
		@Override
		public String description() {return "Find suitable mate";}
	}



	public static class Compete4MateQuest extends TargetQuest {
		public Compete4MateQuest(Clan P, Clan T) {super(P, T);}

		@Override
		public void pursue() {
			// set suitor somewhere??
			Clan rival = target.getSuitor();
			if (rival == null) {rival = target;}
			if (rival == Me) {success(); return;}
			double diff = target.FB.randomValueInPriority().compare(target, Me, rival);
			if (rival != target) {diff -= Values.MAXVAL * (15 - target.useBeh(M_.PROMISCUITY)) / 15;} //if shes not single, ur penalized the less promiscuous she is
			Me.addReport(GobLog.compete4Mate(target, rival, diff));
			if (diff > 0) {success(target);}
			else {failure(rival);}  // but try other tricks such as work or preach
		}
		private double evaluateDifficulty() {
			return 0; //used to determine whether to continue to court or whether to work or preach etc
		}
		@Override
		public void success(Causable blamee) {((BreedQuest) upQuest()).courtSucceeded(); super.success(blamee);}
		@Override
		public void failure(Causable blamee) {((BreedQuest) upQuest()).courtFailed(); super.failure(blamee);}
		@Override
		public String shortName() {Clan rival = target.getSuitor(); return (rival==Me || rival==null ? "Court" : "Compete");}
		@Override
		public String description() {Clan rival = target.getSuitor(); return (rival==Me || rival==null ? "Court " + target.getNomen() : 
			"Steal " + target.getNomen() + "'s heart away from " + rival.getNomen());}
	}
	
}
