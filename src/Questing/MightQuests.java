package Questing;

import AMath.Calc;
import Avatar.SubjectiveType;
import Defs.M_;
import Game.Contract;
import Questing.Quest.PatronedQuest;
import Questing.Quest.PatronedQuestFactory;
import Questing.Quest.TransactionQuest;
import Sentiens.*;
import Sentiens.Law.Commandments;
import Sentiens.Values.Value;

public class MightQuests {
	
	public static PatronedQuestFactory getMinistryFactory() {return new PatronedQuestFactory(DefendPatron.class) {public Quest createFor(Clan c, Clan p) {return new DefendPatron(c, p);}};}
	
	// this should be the default patron quest WHEN NOT in same shire as Patron...
	// otherwise maybe Governor so become General
	// also what if patron=me?
	public static class DefendPatron extends PatronedQuest {
		public DefendPatron(Clan P, Clan patron) {super(P, patron);}

		@Override
		public void pursue() {
			// TODO standby for FormArmy / train (hah doesnt matter who patron is, always ready to fight!)
		}
		@Override
		public String description() {return "Defend " + (Me == patron ? "self" : patron.getNomen());}
		
	}
	


	private static void chooseThreat(Clan subject, Clan object) {
		if (Commandments.INSTANCE.Xenophobia.getFor(subject).isSinful() || (subject.myOrder() != null && subject.myOrder() == object.myOrder())) {
			final boolean kill = !Commandments.INSTANCE.Murder.getFor(subject).isSinful();
			final boolean steal = !Commandments.INSTANCE.Theft.getFor(subject).isSinful();
			if (kill && steal) {Contract.getInstance().threatenLifeAndProperty();} // threaten annihilation
			else if (kill) {Contract.getInstance().threatenLife();} // threaten life
			else if (steal) {Contract.getInstance().threatenProperty();} // threaten property
			else {Contract.getInstance().threatenMight();} // threaten honor
		}
		else {Contract.getInstance().threatenLineage();} // threaten 2nd degree annihilation
	}
	public static boolean desiresFight(Clan pov, Clan opponent, boolean povIsDefender) {
		return expPvictory(pov, opponent, povIsDefender) > 0.5;
	}
	public static double expPvictory(Clan pov, Clan opponent, boolean povIsDefender) {
		// his size includes everyone in his top boss's order (except mine if same order)
		// assumption of big boss stepping in on side of defender (not attacker)
		final Clan hisTopBoss = povIsDefender ? pov.getTopBoss() : opponent.getTopBoss();
		final int aos = povIsDefender ? -1 : 1;
		double confidence = 1, fear = 1;
		// signum because different values are on different scales i.e. not mixable
		double result = Math.signum(Values.MIGHT.compare(pov, opponent, pov) + aos*Values.MIGHT.compare(pov, hisTopBoss, pov));
		if (result > 0) {confidence += result;} else {fear += result;}
		for (int i = 0; i < pov.FB.getBeh(M_.SUPERST) / 5; i++) {
			Value v = pov.FB.randomValueInPriority();
			result = Math.signum(v.compare(pov, opponent, pov) + aos*v.compare(pov, hisTopBoss, pov));
			if (result > 0) {confidence += result;} else {fear -= result;}
		}
		if (!Commandments.INSTANCE.Murder.getFor(opponent).isSinful()) {fear *= (pov.FB.getBeh(M_.MIERTE) / 5 + 1);}
		confidence *= (pov.FB.getBeh(M_.CONFIDENCE) / 5 + 1);
		return confidence / (confidence + fear);
	}
	
	public static class ChallengeMight extends TransactionQuest {
		private final Value val;

		public ChallengeMight(Clan P) {super(P); val = P.FB.randomValueInPriority();}
		public ChallengeMight(Clan P, Value v) {super(P); val = v;}

		@Override
		protected FindTargetAbstract findWhat() {
			return new FindTargetAbstract(Me, TargetQuest.getReasonableCandidates(Me), Me) {
				@Override
				public boolean meetsReq(Clan POV, Clan target) {
					return target != Me && desiresFight(Me, target, false);
				}
				@Override
				protected int triesPerTurn() {return 1;} //expensive calc
			};
		}
		private void setDemandFromValue(Value v) {
			if (v == Values.WEALTH) {
				final int millet = Math.min(Me.getMillet(), target.getMillet()); //TODO figure it out
				contract().demandTribute(millet);
			}
			else if (v == Values.INFLUENCE || v == Values.ALLEGIANCE) {
				contract().demandAllegiance();
			}
			else if (v == Values.RIGHTEOUSNESS) {contract().demandRepentance();}
			// TODO beauty should be give up mate if mate is desired
			else if (v == Values.MIGHT) {
				// TODO just fight
			}
			else {contract().demandService(v);}
		}
		@Override
		protected void setContractDemand() {
			setDemandFromValue(val);
		}
		@Override
		protected void avatarSetContractDemand() {
			avatarConsole().showChoices("Choose demand", Me, Values.All,
					SubjectiveType.VALUE_ORDER, new Calc.Listener() {
				@Override
				public void call(Object arg) {
					setDemandFromValue((Value)arg);
				}
			}, new Calc.Transformer<Value, String>() {
				@Override
				public String transform(Value v) {
					if (v == Values.WEALTH) {return "Demand Tribute";}
					else if (v == Values.INFLUENCE || v == Values.ALLEGIANCE) {
						return "Demand Allegiance";
					}
					else if (v == Values.RIGHTEOUSNESS) {return "Demand Repentance";}
					else if (v == Values.MIGHT) {
						return "Demand Unconditional Surrender";
					}
					else {return "Demand " + v.getMinistry().getDesc(target) + " Service";}
				}
			});
		}

		@Override
		protected void setContractOffer() {chooseThreat(Me, target);}

		@Override
		protected void successCase() {this.success(target);}

		@Override
		protected void failCase() {
			replaceAndDoNewQuest(Me, new WarQuests.WarQuest(Me, target));
			target.MB.newQ(new WarQuests.WarQuest(target, Me));
		}

		@Override
		protected void report(boolean success) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public String description() {return "Make challenge" + (target!=null?" to "+target.getNomen():"");}
	
	}


	

	
}
