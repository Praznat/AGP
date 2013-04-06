package Questing;

import Defs.M_;
import Questing.Quest.PatronedQuest;
import Questing.Quest.PatronedQuestFactory;
import Questing.Quest.TransactionQuest;
import Sentiens.*;
import Sentiens.Values.Value;

public class MightQuests {
	public static PatronedQuestFactory getMinistryFactory() {return new PatronedQuestFactory(MightQuest.class) {public Quest createFor(Clan c) {return new MightQuest(c, c.getBoss());}};}
	
	public static class MightQuest extends PatronedQuest {
		public MightQuest(Clan P, Clan patron) {super(P, patron);}
		@Override
		public void pursue() {
			// decide between:
				// build army (by hiring, buying weapons)
				// military actions
			
			
			replaceAndDoNewQuest(Me, new PersecutionQuests.PersecuteInfidel(Me));
		}
	}
	
	public static boolean desiresFight(Clan pov, Clan opponent, boolean povIsDefender) {
		// his size includes everyone in his top boss's order (except mine if same order)
		// assumption of big boss stepping in on side of defender (not attacker)
		final Clan hisTopBoss = povIsDefender ? pov.getTopBoss() : opponent.getTopBoss();
		final int aos = povIsDefender ? -1 : 1;
		double confidence = 0, fear = 0;
		// signum because different values are on different scales i.e. not mixable
		double result = Math.signum(Values.MIGHT.compare(pov, opponent, pov) + aos*Values.MIGHT.compare(pov, hisTopBoss, pov));
		if (result > 0) {confidence += result;} else {fear += result;}
		for (int i = 0; i < pov.FB.getBeh(M_.SUPERST) / 5; i++) {
			Value v = pov.FB.randomValueInPriority();
			result = Math.signum(v.compare(pov, opponent, pov) + aos*v.compare(pov, hisTopBoss, pov));
			if (result > 0) {confidence += result;} else {fear += result;}
		}
		if (!opponent.FB.commandments.Murder.isActive()) {fear *= pov.FB.getBeh(M_.MIERTE);}
		confidence *= pov.FB.getBeh(M_.CONFIDENCE);
		return confidence > fear;
	}
	
	public static class ChallengeMight extends TransactionQuest {

		public ChallengeMight(Clan P) {
			super(P);
		}

		@Override
		protected FindTargetAbstract findWhat() {
			// TODO Auto-generated method stub
			return new FindTargetAbstract(Me, TargetQuest.getReasonableCandidates(Me), Me) {
				@Override
				public boolean meetsReq(Clan POV, Clan target) {
					return desiresFight(Me, target, false);
				}
				@Override
				protected int triesPerTurn() {return 1;} //expensive calc
			};
		}

		@Override
		protected void setContractDemand() {
			// TODO depends on value i.e. money, commandment conversion, allegiance, etc.
			
		}

		@Override
		protected void setContractOffer() {
			// wont attack
		}

		@Override
		protected void successCase() {
			// TODO Auto-generated method stub
			
		}

		@Override
		protected void failCase() {
			// TODO Auto-generated method stub
			
		}

		@Override
		protected void report(boolean success) {
			// TODO Auto-generated method stub
			
		}
	
	}
	
	
}
