package Questing;

import AMath.Calc;
import Defs.*;
import Game.Defs;
import Questing.Quest.PatronedQuest;
import Questing.Quest.PatronedQuestFactory;
import Sentiens.Clan;

public class SplendorQuests {
	public static PatronedQuestFactory getMinistryFactory() {return new PatronedQuestFactory(UpgradeDomicileQuest.class) {public Quest createFor(Clan c) {return new UpgradeDomicileQuest(c, c.getBoss());}};}
	
	public static class UpgradeDomicileQuest extends PatronedQuest {
		private static int NOCONSTRBIDS = -1; //must be < 0
		private int numConstrs = NOCONSTRBIDS;
		private int turnsLeft, milletRecord;

		public UpgradeDomicileQuest(Clan P, Clan patron) {
			super(P, patron);
			turnsLeft = patron.FB.getBeh(M_.PATIENCE) / 3 + 5;
		}
		
		@Override
		public String description() {return "Upgrade Domicile";}

		@Override
		public void pursue() {
			if (buildForPatron()) {
				success();
			}
			else if (numConstrs == NOCONSTRBIDS) {
				numConstrs = 0;
				milletRecord = Me.getMillet();
				Me.myMkt(Defs.constr).liftOffer(Me);
			}
			else if (turnsLeft > 0) { turnsLeft --;}
			else { failure(Me.myShire()); }
		}
		
		public void incNumConstrs(int n) {numConstrs += n;}
		
		private boolean buildForPatron() {
			if (numConstrs <= 0) {return false;}
			Me.alterCumIncome(milletRecord - Me.getMillet()); // dont count this purchase as an operating loss
			milletRecord = 0;
			numConstrs = NOCONSTRBIDS;
			ExpertiseQuests.practiceSkill(Me, P_.ARTISTRY);
			final int max = 1 + Me.FB.getPrs(P_.ARTISTRY);
			final int producedSplendor = Calc.randBetween(max / 2, max);
			patron.chgSplendor(producedSplendor);
			return true;
		}
		
	}
}
