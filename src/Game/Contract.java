package Game;

import Defs.*;
import Game.Do.ClanAction;
import Sentiens.*;
import Sentiens.Values.Assessable;


//I WILL: (proposer)   proposer initiates from Quest pursue() method
//pay you millet (maximum total assets immediately liquidateable)
//pay you loyalty/zeal points
//not attack you
//give family member?

//IF YOU: (receiver)
//pay tribute
//pay respect
//switch allegiance
//change job
//leave shire
//convert creed
//do quest


public class Contract {   // AND/OR combination of Terms
	private Term offer, demand;
	private Clan evaluator, proposer;
	private static Contract theContract = new Contract();
	
	private Contract() {offer = new Term(); demand = new Term();}
	public static Contract getInstance() {return theContract;}
	

	public void enter(Clan e, Clan p) {evaluator = e; proposer = p;}
	public void setOffer(Assessable metric, int content, ClanAction ac) {offer.setTerm(evaluator, proposer, metric, content, ac);}
	public void setDemand(Assessable metric, int content, ClanAction ac) {demand.setTerm(evaluator, proposer, metric, content, ac);}
	/** Once terms are set, evaluator adds evaluations (generally one neg and one pos) and returns true if > 0 */
	public boolean acceptable() {return offer.valueTerm() + demand.valueTerm() > 0;}
	public void enact() {
		if (!offer.tryAction()) {evaluator.AB.add(new Stressor(Stressor.PROMISE_BROKEN, proposer));}
		if (!demand.tryAction()) {proposer.AB.add(new Stressor(Stressor.PROMISE_BROKEN, evaluator));}
	}
	
	public void setOfferThreat() {setOffer(THREAT, 0, Do.NOTHING); Do.NOTHING.setup(proposer, 0);}
	public void setOfferPayment(int c) {setOffer((Assessable)Values.PROPERTY, c, Do.PAY_TRIBUTE); Do.PAY_TRIBUTE.setup(proposer, evaluator, c);}
	public void setOfferGratitude(int c) {setOffer((Assessable)Values.ALLEGIANCE, c, null);}
	public void setOfferBlessing(int c) {setOffer((Assessable)Values.CREED, c, null);}

	public void setDemandTribute(int c) {setDemand((Assessable)Values.PROPERTY, -c, Do.PAY_TRIBUTE); Do.PAY_TRIBUTE.setup(evaluator, proposer, c);}
	public void setDemandRespect() {setDemand((Assessable)Values.INFLUENCE, -1, Do.PAY_RESPECT); Do.PAY_RESPECT.setup(evaluator, proposer, 0);}
	public void setDemandAllegiance() {setDemand((Assessable)Values.ALLEGIANCE, 0, Do.DECLARE_ALLEGIANCE); Do.DECLARE_ALLEGIANCE.setup(evaluator, proposer, 0);}
	/**
	 * TODO: probably needs a special term for violating Heresy law
	 */
	public void setDemandConversion() {setDemand((Assessable)Values.CREED, 0, Do.CONVERT_TO_CREED); Do.CONVERT_TO_CREED.setup(evaluator, proposer, 0);}
	public void setDemandQuest(Q_ q) {setDemand(NEWQUEST, q.ordinal(), null);}
	
	private int renegotiationTimes() {return Math.min(evaluator.useBeh(M_.PATIENCE), proposer.useBeh(M_.PATIENCE)) / 3;}
	
	
	private static final class Term {
		private Clan evaluator, proposer;
		private Assessable metric;
		private int content;
		private ClanAction agreedAction;
		public void setTerm(Clan e, Clan p, Assessable m, int c, ClanAction ac) {evaluator = e; proposer = p; metric = m; content = c; agreedAction = ac;}
		public double valueTerm() {return metric.evaluate(evaluator, proposer, content);}
		public boolean tryAction() {return agreedAction.doit();}
	}
	
	
	public static final Assessable THREAT = new Assessable() {
		@Override
		public double evaluate(Clan evaluator, Clan proposer, int content) {   //returns perceived amount of value from avoiding this threat
			return evaluator.FB.randomValueInPriority().compare(evaluator, proposer, evaluator) * (1 - evaluator.getCourage());
		}
	};
	public static final Assessable NEWQUEST = new Assessable() {   //put proposer's quest "content" on top of current quest
		@Override
		public double evaluate(Clan evaluator, Clan proposer, int content) {   //for now, less desirable the less you like proposer
			return evaluator.FB.randomValueInPriority().compare(evaluator, proposer, evaluator) - Values.MAXVAL;
		}
	};
	public static final Assessable EXILE = new Assessable() {
		@Override
		public double evaluate(Clan evaluator, Clan proposer, int content) {
//			if (evaluator.FB.randomValueInPriority() == Values.PATRIOTISM) {return Values.MINVAL;}
			int landval = evaluator.getAssets(Defs.land) * evaluator.myMkt(Defs.land).buyablePX(evaluator);
			return ((Assessable)Values.PROPERTY).evaluate(evaluator, proposer, -landval);
		}
	};
	

	
}
