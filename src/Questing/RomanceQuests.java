package Questing;

import Sentiens.Clan;
import Sentiens.Values;
import AMath.Calc;
import Defs.M_;
import Game.AGPmain;
import Game.Defs;
import Questing.Quest.FindTarget;
import Questing.Quest.TargetQuest;

public class RomanceQuests {

	public static class BreedQuest extends TargetQuest {
		private int courtsLeft = Defs.E;
		private int failsLeft = Defs.E;
		public BreedQuest(Clan P) {super(P); resetFails();}
		@Override
		public void pursue() {
			if (courtsLeft == 0) {success(Me); return;}
			if (failsLeft == 0) {failure(); return;}
			if (target == null) {Me.MB.newQ(new FindMate(Me)); failsLeft--; return;}
			resetFails();   //previous fails were for finding target
			if (courtsLeft == Defs.E) {courtsLeft = 16 - target.useBeh(M_.PROMISCUITY);}
			Me.MB.newQ(new Compete4MateQuest(Me, target));
		}
		private void resetFails() {failsLeft = 1 + Me.useBeh(M_.PATIENCE);}
		public void courtSucceeded() {courtsLeft--;}
		public void courtFailed() {failsLeft--;}
	}

	public static class FindMate extends Quest implements FindTarget {
		private static final int TRIESPERTURN = 3;
		public FindMate(Clan P) {super(P);}
		@Override
		public void pursue() {
			Clan[] pop = Me.myShire().getCensus();
			for (int i = Math.min(TRIESPERTURN, pop.length); i > 0; i--) {
				Clan candidate = pop[AGPmain.rand.nextInt(pop.length)];
				if(meetsReq(Me, candidate)) {
					((BreedQuest) upQuest()).setTarget(candidate);
					success(Me.myShire()); break;
				}
			}
			failure(Me.myShire());
		}
		@Override
		public boolean meetsReq(Clan POV, Clan target) {
			return POV.getGender() != target.getGender() &&
			POV.FB.randSancInPriority().compare(POV, target, POV) + Values.MAXVAL * POV.useBeh(M_.PROMISCUITY) / 15 > 0;
		}
	}



	public static class Compete4MateQuest extends TargetQuest {
		public Compete4MateQuest(Clan P, Clan T) {super(P, T);}

		@Override
		public void pursue() {
			Clan rival = target.getSuitor();
			if (rival == Me) {success(); return;}
			double diff = target.FB.randSancInPriority().compare(target, Me, rival);
			if (rival != target) {diff -= Values.MAXVAL * (15 - target.useBeh(M_.PROMISCUITY)) / 15;} //if shes not single, ur penalized the less promiscuous she is
			if (diff > 0) {success();}
			else {failure();}  // but try other tricks such as work or preach
		}
		private double evaluateDifficulty() {
			return 0; //used to determine whether to continue to court or whether to work or preach etc
		}
		@Override
		public void success() {((BreedQuest) upQuest()).courtSucceeded(); super.success();}
		@Override
		public void failure() {((BreedQuest) upQuest()).courtFailed(); super.failure();}
	}
	
}
