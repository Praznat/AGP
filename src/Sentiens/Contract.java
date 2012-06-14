package Sentiens;

import Defs.M_;
import Defs.Q_;
import Game.AGPmain;
import Game.Defs;
import Game.Do;
import Game.Do.*;
import Sentiens.Values.Assessable;
import Sentiens.Values.Value;
import Shirage.Shire;


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
	private static Term offer, demand;
	private Clan evaluator, proposer;
	public static final Contract O = new Contract(); 

	public void enter(Clan e, Clan p) {evaluator = e; proposer = p;}
	public void setOffer(Assessable metric, int content, ClanAction ac) {offer.setTerm(evaluator, proposer, metric, content, ac);}
	public void setDemand(Assessable metric, int content, ClanAction ac) {demand.setTerm(evaluator, proposer, metric, content, ac);}
	/** Once terms are set, evaluator adds evaluations (generally one neg and one pos) and returns true if > 0 */
	public boolean acceptable() {return offer.valueTerm() + demand.valueTerm() > 0;}
	public void enact() {
		if (!offer.tryAction()) {evaluator.AB.add(new Stressor(Stressor.PROMISE_BROKEN, proposer));}
		if (!demand.tryAction()) {proposer.AB.add(new Stressor(Stressor.PROMISE_BROKEN, evaluator));}
	}
	
	public void setOfferThreat() {setOffer(THREAT, 0, null);}
	public void setOfferPayment(int c) {setOffer(MONEY, c, Do.PAY_TRIBUTE); Do.PAY_TRIBUTE.setup(proposer, evaluator, c);}
	public void setOfferGratitude(int c) {setOffer((Assessable)Values.LOYALTY, c, null);}
	public void setOfferBlessing(int c) {setOffer((Assessable)Values.CREED, c, null);}

	public void setDemandTribute(int c) {setOffer(MONEY, -c, Do.PAY_TRIBUTE); Do.PAY_TRIBUTE.setup(evaluator, proposer, c);}
	public void setDemandRespect() {setOffer((Assessable)Values.POPULARITY, -1, Do.PAY_RESPECT); Do.PAY_RESPECT.setup(evaluator, proposer, 0);}
	public void setDemandAllegiance() {setOffer((Assessable)Values.LOYALTY, 0, Do.DECLARE_ALLEGIANCE); Do.DECLARE_ALLEGIANCE.setup(evaluator, proposer, 0);}
	public void setDemandConversion() {setOffer((Assessable)Values.CREED, 0, null);}
	public void setDemandExile() {setOffer(EXILE, 0, null);}
	public void setDemandQuest(Q_ q) {setOffer(NEWQUEST, q.ordinal(), null);}
	
	private int renegotiationTimes() {return Math.min(evaluator.useBeh(M_.PATIENCE), proposer.useBeh(M_.PATIENCE)) / 3;}
	
	
	public static final class Term {
		private Clan evaluator, proposer;
		private Assessable metric;
		private int content;
		private ClanAction agreedAction;
		public void setTerm(Clan e, Clan p, Assessable m, int c, ClanAction ac) {evaluator = e; proposer = p; metric = m; content = c; agreedAction = ac;}
		public double valueTerm() {return metric.evaluate(evaluator, proposer, content);}
		public boolean tryAction() {return agreedAction.doit();}
	}
	
	
	

	
	public static final Assessable MONEY = new Assessable() {
		@Override
		public double evaluate(Clan evaluator, Clan proposer, int content) { 
			return evaluator.FB.randomValueInPriority().contentBuyable(evaluator, content);
		}
	};
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
			if (evaluator.FB.randomValueInPriority() == Values.PATRIOTISM) {return Values.MINVAL;}
			int landval = evaluator.getAssets(Defs.land) * evaluator.myMkt(Defs.land).buyablePX(evaluator);
			return MONEY.evaluate(evaluator, proposer, -landval);
		}
	};
	

	
}
