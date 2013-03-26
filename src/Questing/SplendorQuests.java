package Questing;

import AMath.Calc;
import Defs.P_;
import Game.Defs;
import Questing.Quest.PatronedQuest;
import Questing.Quest.QuestFactory;
import Sentiens.Clan;

public class SplendorQuests {
	public static QuestFactory getMinistryFactory() {return new QuestFactory(UpgradeDomicileQuest.class) {public Quest createFor(Clan c) {return new UpgradeDomicileQuest(c, c.getBoss());}};}
	
	public static class UpgradeDomicileQuest extends PatronedQuest {

		public UpgradeDomicileQuest(Clan P, Clan patron) {super(P, patron);}
		
		@Override
		public String description() {return "Upgrade Domicile";}

		@Override
		public void pursue() {
			if (buildForPatron()) {
				success();
			}
			else {
				// put some bids for construction
				Me.myMkt(Defs.constr).liftOffer(Me); // what to do at MktO.alterWMG ??
			}
		}
		
		private boolean buildForPatron() {
			if (Me.getAssets(Defs.constr) == 0) {return false;}
			Me.decAssets(Defs.constr, 1);
			ExpertiseQuests.practiceSkill(Me, P_.ARTISTRY);
			final int max = 1 + Me.FB.getPrs(P_.ARTISTRY);
			final int producedSplendor = Calc.randBetween(max / 2, max);
			patron.chgSplendor(producedSplendor);
			return true;
		}
		
	}
}
