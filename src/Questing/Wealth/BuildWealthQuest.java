package Questing.Wealth;

import AMath.Calc;
import Defs.M_;
import Game.*;
import Ideology.Values;
import Questing.*;
import Questing.Quest.PatronedQuest;
import Sentiens.Clan;
import Sentiens.Stress.StressorFactory;

public class BuildWealthQuest extends PatronedQuest {
	public static PatronedQuestFactory getMinistryFactory() {return new PatronedQuestFactory(BuildWealthQuest.class) {public Quest createFor(Clan c, Clan p) {return new BuildWealthQuest(c, p);}};}
	
	private final int startCumInc, goalCumInc;
	public BuildWealthQuest(Clan clan, Clan patron) {
		super(clan, patron);
		startCumInc = clan.getCumulativeIncome();
		goalCumInc = (int) Math.min(Integer.MAX_VALUE / 2, clan.getNetAssetValue(clan));
	} //default is to double NAV
	@Override
	public void pursue() {
		if (Me.getCumulativeIncome() - startCumInc >= goalCumInc) {success(Me.myShire(), Me.getJob()); return;}
		if (Me.getCumulativeIncome() <= startCumInc && Calc.pPercent(80-4*Me.FB.getBeh(M_.PATIENCE))) { // 20-80% chance of give up
			// TODO blame shire or blame trading behs? if library has behs XOR wealthyshire knowledge then use what it has
			failure(StressorFactory.createShireStressor(Me.myShire(), Values.WEALTH)); return;
		}
		final Job j = Me.getJob();
		if (j instanceof Ministry) {
			// ministry job
			((Ministry) j).getService().doit(Me);
		}
		else if (j == Job.TRADER) {Me.MB.newQ(new TradingQuest(Me));}
		else {Me.MB.newQ(new LaborQuest(Me));}
	}
	public String description() {return "Build Wealth" + (patron != Me ? " for " + patron.getNomen() : "");}
}