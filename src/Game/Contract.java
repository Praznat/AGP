package Game;

import java.util.*;

import AMath.Calc;
import Avatar.SubjectiveType;
import Defs.P_;
import Questing.MightQuests;
import Sentiens.*;
import Sentiens.GobLog.Reportable;
import Sentiens.Values.Assessable;
import Sentiens.Values.Value;

/**
 * Contract Singleton must be evaluated and decided on immediately
 * (because it is singleton, >1 contract must not be considered at one time)
 */
public class Contract {   // AND/OR combination of Terms??
	private static Contract theContract = new Contract();
	private List<DealTerm> offers = new ArrayList<DealTerm>();
	private List<DealTerm> demands = new ArrayList<DealTerm>();
	private Clan evaluator, proposer;
	private boolean accepted;
	
	public static Contract getNewContract(Clan evaluator, Clan proposer) {
		theContract.evaluator = evaluator;
		theContract.proposer = proposer;
		theContract.offers.clear();
		theContract.demands.clear();
		theContract.accepted = false;
		return theContract;
	}
	public static Contract getInstance() {return theContract;}
	
	/** Once terms are set, evaluator adds evaluations (generally one neg and one pos) and returns true if > 0 */
	public boolean acceptable() {
		final double demandValue = getDemandValue();
		final double offerValue = getOfferValue();
		accepted = demandValue + offerValue > 0;
		if (AGPmain.mainGUI != null && evaluator == AGPmain.mainGUI.AC.getAvatar()) {avatarChooseAcceptable(accepted);}
		evaluator.addReport(GobLog.contractOutcome(proposer, evaluator, accepted, demandValue, offerValue));
		proposer.addReport(GobLog.contractOutcome(proposer, evaluator, accepted, demandValue, offerValue));
		return accepted;
	}

	private double getTermValue(Collection<DealTerm> terms) {
		double termValue = 0;
		for (DealTerm dt : terms) {termValue += dt.getEvaluation();}
		return termValue;
	}
	public double getDemandValue() {return getTermValue(demands);}
	public double getOfferValue() {return getTermValue(offers);}
	
	private void avatarChooseAcceptable(boolean aiWouldAccept) {
		String prompt = "";
		for (DealTerm dt : demands) {prompt += dt.loggy().out() + ";";}
		for (DealTerm dt : offers) {prompt += dt.loggy().out() + ";";}
		final Boolean[] choices = aiWouldAccept ? new Boolean[] {Boolean.TRUE, Boolean.FALSE} : new Boolean[] {Boolean.FALSE, Boolean.TRUE};
		AGPmain.mainGUI.AC.showChoices(prompt, evaluator, choices, SubjectiveType.NO_ORDER, new Calc.Listener() {
			@Override
			public void call(Object arg) {
				accepted = ((Boolean)arg).booleanValue();
			}
		}, new Calc.Transformer<Boolean, String>() {
			@Override
			public String transform(Boolean b) {
				return b.booleanValue() ? "Accept terms" : "Reject terms";
			}
		});
	}
	public void enact() {
		for (DealTerm offer : offers) {if (!offer.doit()) {evaluator.AB.add(new Stressor(Stressor.PROMISE_BROKEN, proposer));}}
		for (DealTerm demand : demands) {if (!demand.doit()) {proposer.AB.add(new Stressor(Stressor.PROMISE_BROKEN, evaluator));}}
	}

	protected abstract class DealTerm {
		public double getEvaluation() {evaluator.addReport(loggy()); proposer.addReport(loggy()); return evaluate();}
		protected abstract double evaluate();
		protected abstract boolean doit();
		protected abstract Reportable loggy();
	}
	public void demandTribute(int millet) {demands.add(new DemandTributeTerm(millet));}
	protected class DemandTributeTerm extends DealTerm {
		private final int millet;
		public DemandTributeTerm(int millet) {this.millet = millet;}
		@Override
		protected double evaluate() {
			return ((Assessable)Values.WEALTH).evaluate(evaluator, null, -Math.min(millet, evaluator.getMillet())); // evaluator never assumes debt
		}
		@Override
		protected boolean doit() {
			boolean enough = evaluator.getMillet() >= millet;
			int amt = (enough ? millet : evaluator.getMillet());
			evaluator.decAssets(Defs.millet, amt);
			proposer.incAssets(Defs.millet, amt);
			return enough;
		}
		protected Reportable loggy() {return GobLog.dealTermTribute(proposer, evaluator, millet);}
	}
	public void demandAllegiance() {demands.add(new DemandAllegianceTerm());}
	protected class DemandAllegianceTerm extends DealTerm {
		@Override
		protected double evaluate() {
			return ((Assessable)Values.ALLEGIANCE).evaluate(evaluator, proposer, 0);
		}
		@Override
		protected boolean doit() {evaluator.join(proposer); return true;}
		protected Reportable loggy() {return GobLog.dealTermAllegiance(proposer, evaluator);}
	}
	//TODO offer allegiance (valuate by INFLUENCE)
	public void demandRepentance() {demands.add(new DemandRepentanceTerm());}
	protected class DemandRepentanceTerm extends DealTerm {
		@Override
		protected double evaluate() {
			return ((Assessable)Values.RIGHTEOUSNESS).evaluate(evaluator, proposer, 0);
		}
		@Override
		protected boolean doit() {return true;} // TODO adopt commandments
		protected Reportable loggy() {return GobLog.dealTermRepentance(proposer, evaluator);}
	}
	//TODO offer service (maybe what minions do when they roll ALLEGIANCE?)
	public void demandService(Value v) {demands.add(new DemandServiceTerm(v));}
	protected class DemandServiceTerm extends DealTerm {
		private final Value val;
		public DemandServiceTerm(Value v) {val = v;}
		@Override
		protected double evaluate() { //for now, less desirable the less you like proposer
			return evaluator.FB.randomValueInPriority().compare(evaluator, proposer, evaluator) - Values.MAXVAL;
		}
		@Override
		protected boolean doit() {evaluator.MB.newQ(Ministry.getPatronQuestFactoryForValue(val).createFor(evaluator, proposer)); return true;}
		protected Reportable loggy() {return GobLog.dealTermService(proposer, evaluator, val);}
	}
	
	protected abstract class ThreatTerm extends DealTerm {
		protected abstract int wgtOfValsToLose();
		@Override
		protected double evaluate() {
			// TODO must also consider probability of escaping... otherwise will always yield to most savage opponents
			final double pDefeat = 1.0 - MightQuests.expPvictory(evaluator, proposer, true);
			return (wgtOfValsToLose() * Values.MAXVAL + ((Assessable)Values.MIGHT).evaluate(evaluator, proposer, 1)) * pDefeat + //what you would have lost
					evaluator.FB.weightOfValue(Values.MIGHT) * chgInBattlePifAvoid(); // - "shame points" for not fighting
			//TODO - opportunity cost of gaining BATTLEP
		}
		@Override
		protected boolean doit() {
			// proposer back off (no actual change in BATTLEP for proposer)
			evaluator.FB.setPrs(P_.BATTLEP, newBattlePifAvoid());
			return true;
		}
		protected int newBattlePifAvoid() {
			return Math.min(proposer.FB.getPrs(P_.BATTLEP), evaluator.FB.getPrs(P_.BATTLEP) - 1);
		}
		protected int chgInBattlePifAvoid() {
			final int myP = evaluator.FB.getPrs(P_.BATTLEP);
			return Math.min(proposer.FB.getPrs(P_.BATTLEP) - 1, myP) - myP;
		}
		protected Reportable loggy() {return GobLog.dealTermThreat(proposer, evaluator);}
	}
	public void threatenMight() {offers.add(new ThreatenMightTerm());}
	protected class ThreatenMightTerm extends ThreatTerm { // steal is sin, kill is sin
		@Override
		protected int wgtOfValsToLose() {return 0;}
	}
	public void threatenProperty() {offers.add(new ThreatenPropertyTerm());}
	protected class ThreatenPropertyTerm extends ThreatTerm { // steal ok, kill is sin
		@Override
		protected int wgtOfValsToLose() {
			return evaluator.FB.sumWeightOfValues(Values.WEALTH, Values.COMFORT);
		}
	}
	public void threatenLife() {offers.add(new ThreatenLifeTerm());}
	protected class ThreatenLifeTerm extends ThreatTerm { // steal is sin, kill ok
		@Override
		protected int wgtOfValsToLose() {
			return evaluator.FB.sumWeightOfValues(Values.BEAUTY, Values.FREEDOM) +
					evaluator.FB.sumWeightOfValues(Values.WEALTH, Values.COMFORT) * (100 - evaluator.FB.getSancPct(Values.LEGACY));
		}
	}
	public void threatenLifeAndProperty() {offers.add(new ThreatenTotalSubversionTerm());}
	protected class ThreatenTotalSubversionTerm extends ThreatTerm { // steal ok, kill ok
		@Override
		protected int wgtOfValsToLose() {
			return evaluator.FB.sumWeightOfValues(Values.WEALTH, Values.COMFORT, Values.BEAUTY, Values.FREEDOM);
		}
	}
	public void threatenLineage() {offers.add(new ThreatenTotalAnnihilationTerm());}
	protected class ThreatenTotalAnnihilationTerm extends ThreatTerm { // steal ok, kill ok, sever lineage ok
		@Override
		protected int wgtOfValsToLose() {
			return evaluator.FB.sumWeightOfValues(Values.WEALTH, Values.COMFORT, Values.BEAUTY, Values.FREEDOM, Values.LEGACY);
		}
	}
}
