package Sentiens;

import AMath.ArrayUtils;
import Defs.M_;
import Game.Do;
import Game.Do.ClanAction;
import Game.Do.PayTribute;
import Markets.MktO;
import Sentiens.Contract.Term;
import Sentiens.Values.Assessable;
import Sentiens.Values.Value;


public class ExpertAI {
	//HAGGLING
	
	private ClanAction anchorProposal;  //this is what he likes to do, and what will guess first by default
	
	private static final ClanAction[] possProposals = {
		Do.DECLARE_ALLEGIANCE,
		Do.PAY_TRIBUTE,
		Do.PAY_RESPECT
	};

	public static void roughlyPickInitOffer(Clan subject, Clan object, ClanAction desireOfSubject) {
		double value = desireOfSubject.evaluate(subject);
		Term term = new Term();
		term.define(ArrayUtils.randomIndexOf(possProposals), subject.getID(), object.getID());
		term.setup();

		if (term.clanAction == Do.PAY_TRIBUTE) {
			((PayTribute) term.clanAction).adjustContentToEqual(subject, addMinusSpread(subject, value));
		}
		term.clanAction.evaluate(subject);

	}
	public static void haggle(Clan subject, Clan object, Term curTerm) {
		if (curTerm.clanAction == Do.PAY_TRIBUTE) {
			
		}
	}
	/** Used to compare contracts in common term (millet) */
	public static double getMaxValueOfMoney(Clan clan, int millet) {return getMaxValueOfMoney(clan, millet, Values.AllValues);}
	public static double getMaxValueOfMoney(Clan clan, int millet, Value[] regardedValues) {return getMaxValueOfMoney(clan, millet, Values.AllValues, null);}
	public static double getMaxValueOfMoney(Clan clan, int millet, Value[] regardedValues, Value ignoreValue) {
		double max = 0;
		for (Value V : regardedValues) {
			if (V == null || V == ignoreValue) {continue;} //so for example ignore Loyalty milletvalue when someone is trying to buy your loyalty
			max = Math.max(max, V.contentBuyable(clan, millet));
		}
		return max;
	}
	
	protected static double addMinusSpread(Clan doer, double val) {return addSpread(val, -MktO.RATES[doer.useBeh(M_.BIDASKSPRD)]);}
	protected static double addPlusSpread(Clan doer, double val) {return addSpread(val, MktO.RATES[doer.useBeh(M_.BIDASKSPRD)]);}
	protected static double addSpread(double val, double s) {return Math.round(val * (1+s));}
	
}
