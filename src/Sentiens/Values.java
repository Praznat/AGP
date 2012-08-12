package Sentiens;



import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import AMath.ArrayUtils;
import Defs.F_;
import Defs.M_;
import Defs.P_;
import Defs.Q_;
import Game.*;
import Game.Do.ClanAlone;
import Government.*;
import Government.Bureaus.Bureau;
import Markets.MktAbstract;

public class Values implements Defs {
	private static M_[] wealthMems = new M_[] {M_.BIDASKSPRD, M_.INVORTRD, M_.STMOMENTUM, M_.LTMOMENTUM, M_.MARGIN, M_.DISCRATE};
//	private static M_[] alignmentMems = new M_[] {M_.S_LOYALTY, M_.S_PATRIOTISM, M_.S_ZEAL};

//	private static M_[] WPWeights = new M_[] {M_.S_MONEY,M_.S_POPULARITY,M_.S_COMBAT,M_.S_NVASSALS,};
//	private static M_[] PBWeights = new M_[] {M_.S_SILVER, M_.S_MEAT, M_.S_NOSELEN, M_.S_EYESIZE, M_.S_HAIRLEN, M_.S_JAWWIDTH, M_.S_ART};
//	private static M_[] HGWeights = new M_[] {M_.S_PATRIOTISM,M_.S_LOYALTY,M_.S_PROMISCUITY,M_.S_GREED,M_.S_BLOODLUST,M_.S_MONUMENTS};
//	private static M_[] KWWeights = new M_[] {M_.S_KNOWLEDGE,M_.S_SACRIFICE,M_.S_HEALING,M_.S_SORCERY,M_.S_ZEAL,M_.S_AGE,M_.S_SKILL};
	
	private static int ord = 0;

	public static interface Assessable {
		/** returns comparison between current value and proposed value */
		public double evaluate(Clan evaluator, Clan proposer, int content);
	}
	public static interface Teachable {
		public void teach(Clan teacher, Clan student);
	}
	public static interface Preachable {
		public void preach(Clan preacher, Clan student, Clan benefactor);
	}
	public static interface Value extends Stressor.Causable, Avatar.SubjectivelyComparable {
		/** no longer used */
		public M_ getWeightMeme(Clan POV);
		/** no longer used */
		public int getWeighting(Clan POV);
		/** returns difference in prestige in this Value between A & B in eyes of POV */
		public double compare(Clan POV, Clan A, Clan B);
		public String description(Clan POV);
		public Q_ pursuit(Clan clan);
		public ClanAlone doPursuit(Clan clan);
		public Bureau getPreferredBureau();
		public double contentBuyable(Clan assessor, int millet);
		public int ordinal();
	}

	private static class ToDoValue implements Value {
		private int ordinal;
		public ToDoValue() {ordinal = ord++;}
		@Override
		public M_ getWeightMeme(Clan POV) {return null;}
		@Override
		public int getWeighting(Clan POV) {return 7;}
		@Override
		public double compare(Clan POV, Clan A, Clan B) {return 0;}
		@Override
		public String description(Clan POV) {return "Value yet undefined";}
		@Override
		public Q_ pursuit(Clan clan) {return Q_.NOTHING;}
		@Override
		public Bureau getPreferredBureau() {return null;}
		public double contentBuyable(Clan assessor, int millet) {return 0;}
		@Override
		public int ordinal() {return ordinal;}
		@Override
		public ClanAlone doPursuit(Clan clan) {return Do.NOTHING;}
	}
	private static abstract class AbstractValue implements Value {
		protected final M_ weighting;
		protected final String desc;
		protected final Q_ quest;
		protected final Bureau preferredBureau;
		private int ordinal;
		private ClanAlone doPursuit;
		public AbstractValue(M_ w, String d, Q_ q, Bureau b) {
			weighting = w;
			desc = d;
			quest = q;
			ordinal = ord++;
			doPursuit = Do.addQuest(quest);
			preferredBureau = b;
		}
		@Override
		public M_ getWeightMeme(Clan POV) {return weighting;}
		@Override
		public int getWeighting(Clan POV) {return POV.useBeh(weighting);}
		@Override
		public String description(Clan POV) {return desc;}
		@Override
		public Q_ pursuit(Clan clan) {return (quest != null ? quest : Q_.NOTHING);}
		@Override
		public Bureau getPreferredBureau() {return preferredBureau;}
		@Override
		public int ordinal() {return ordinal;}
		@Override
		public ClanAlone doPursuit(Clan clan) {return doPursuit;}
	}

	private static abstract class TeachableValue extends ValuatableValue implements Teachable {
		public TeachableValue(M_ w, String d, Q_ q, Bureau b) {super(w, d, q, b);}
		protected abstract M_[] relMems();
		@Override
		public void teach(Clan teacher, Clan student) {
			// TODO test if student respects teacher
			M_ lesson = ArrayUtils.randomIndexOf(relMems()); //get random one
			student.FB.setBeh(lesson, teacher.useBeh(lesson));
		}
	}
	private static abstract class ValuatableValue extends AbstractValue implements Assessable {
		public ValuatableValue(M_ w, String d, Q_ q, Bureau b) {super(w, d, q, b);}
		@Override
		public double compare(Clan POV, Clan A, Clan B) {
			return compare(value(POV, A), value(POV, B));
		}
		public double compare(double A, double B) {return logComp(A, B);}
		public double evaluateContent(Clan evaluator, Clan proposer, int content, double curval) {return content + curval;}
		@Override
		public final double evaluate(Clan evaluator, Clan proposer, int content) {
			double curVal = value(evaluator, evaluator);
			return compare(evaluateContent(evaluator, proposer, content, curVal), curVal);
		}
		protected abstract int value(Clan POV, Clan clan);
		@Override
		public double contentBuyable(Clan assessor, int millet) {return 0;}
	}
	private static abstract class FBitValuatableValue extends ValuatableValue {
		public FBitValuatableValue(M_ w, String d, Q_ q, Bureau b) {super(w, d, q, b);}
		@Override
		public double compare(double A, double B) {return fourbitComp((int)A, (int)B);}
	}
	private static class ViceValue extends FBitValuatableValue {
		protected final M_ meme;
		public ViceValue(M_ w, String d, M_ m, Q_ q, Bureau b) {super(w, d, q, b); meme = m;}
		@Override
		public int getWeighting(Clan POV) {return -super.getWeighting(POV);}
		@Override
		protected int value(Clan POV, Clan clan) {return clan.FB.getBeh(meme);}
	}
	

	public static final Value NULL = new ToDoValue();


	
	public static final Value DOMINION = new ValuatableValue(M_.S_NVASSALS, "Power - Dominion", Q_.RECRUIT, Bureaus.INTERIOR) {
		@Override
		protected int value(Clan POV, Clan clan) {return clan.getTotalMinionNumber();}    // TODO this should be about shires not minions!!!
		@Override
		public double contentBuyable(Clan assessor, int millet) {return 0;} //TODO Figure out fair (min since quality of vassal ignored) price of hire
	};

	public static final Value WEALTH = new TeachableValue(M_.S_MONEY, "Power - Wealth", Q_.BUILDWEALTH, Bureaus.ECONOMY) {
		@Override
		protected M_[] relMems() {return wealthMems;}
		@Override
		protected int value(Clan POV, Clan clan) {
			int sum = 0;   for (int g = 0; g < Defs.numAssets; g++) {
				int px = POV.myMkt(g).sellablePX(POV);
				sum += clan.getAssets(g) * px;
			}   return sum;
		}
		@Override
		public double contentBuyable(Clan assessor, int millet) {return 1;}
	};
	public static final Value INFLUENCE = new FBitValuatableValue(M_.S_POPULARITY, "Power - Influence", Q_.BUILDPOPULARITY, Bureaus.INTERIOR) {
		@Override
		protected int value(Clan POV, Clan clan) {return clan.FB.getPrs(P_.RSPCP);}
		@Override
		public double contentBuyable(Clan assessor, int millet) {return 0;} //TODO Figure out popularity of wealth mem
	};

	public static final Value MIGHT = new ValuatableValue(M_.S_THREAT, "Power - Might", null, Bureaus.MILITARY) {
		@Override
		protected int value(Clan POV, Clan clan) {   //maybe add human sacrifice?
			double result = (15 - clan.FB.getBeh(M_.MIERTE)) + clan.FB.getBeh(M_.BLOODLUST) + clan.FB.getBeh(M_.MADNESS) + 
					2 * (clan.FB.getPrs(P_.TYRRP) + clan.FB.getPrs(P_.STRENGTH) + clan.FB.getPrs(P_.MARTIALP));
			return (int) Math.round(result / (1 + POV.myShire().distanceFrom(clan.myShire()))); //might be better with discount rate
		}
	};
	

	//TODO VIRTUE "Honor - Virtue"
	public static final Value PROMISCUITY = new ViceValue(M_.S_PROMISCUITY, "Honor (Chastity)", M_.PROMISCUITY, Q_.PERSECUTEADULTERER, Bureaus.INTERIOR);
	public static final Value GREED = new ToDoValue();
	public static final Value BLOODLUST = new ViceValue(M_.S_BLOODLUST, "Honor (Mercy)", M_.BLOODLUST, Q_.PERSECUTETYRANT, Bureaus.INTERIOR);
	
	public static final Value LOYALTY = new ValuatableValue(M_.S_LOYALTY, "", Q_.LOYALTYQUEST, Bureaus.INTERIOR) {
		@Override
		public String description(Clan POV) {return "Honor - Loyalty" + (POV != null && POV != POV.FB.getRex() ? " to " + POV.FB.getDiscName(Defs.LORD) : "");}
		@Override
		protected int value(Clan POV, Clan clan) {
			return (clan.FB.getRex() == POV.FB.getRex() ? clan.FB.getDiscPts(Defs.LORD) : 0);
		}
		@Override
		public double evaluateContent(Clan evaluator, Clan proposer, int content, double curval) {
			return content + (proposer.FB.getRex() == evaluator.FB.getRex() ? curval : 0);
		}
	};
	public static final Value PATRIOTISM = new ValuatableValue(M_.S_PATRIOTISM, "", Q_.PERSECUTEFOREIGNER, Bureaus.MILITARY) {
		@Override
		public String description(Clan POV) {return "Honor - Patriotism" + (POV != null ?  " to " + POV.FB.getDiscName(Defs.HOMELAND) : "");}
		private int adjustByDistance(int x, int distance) {return x / (distance + 1);}
		@Override
		protected int value(Clan POV, Clan clan) {
			return adjustByDistance(clan.FB.getDiscPts(Defs.HOMELAND), (clan.FB.getHomeland().distanceFrom(POV.FB.getHomeland())));
		}
		@Override
		public double evaluateContent(Clan evaluator, Clan proposer, int content, double curval) {
			return curval + adjustByDistance(content, proposer.FB.getHomeland().distanceFrom(evaluator.FB.getHomeland()));
		}
	};
	public static final Value ZEAL = new ValuatableValue(M_.S_ZEAL, "", Q_.PERSECUTEHERETIC, Bureaus.RELIGION) {
		@Override
		public String description(Clan POV) {return "Honor - Zeal" + (POV != null ? " for the " + POV.FB.getDiscName(Defs.CREED) : "");}
		@Override
		protected int value(Clan POV, Clan clan) {
			return (clan.FB.getDisc(Defs.CREED) == POV.FB.getDisc(Defs.CREED) ? clan.FB.getDiscPts(Defs.CREED) : 0);
		}
		@Override
		public double evaluateContent(Clan evaluator, Clan proposer, int content, double curval) {
			return content + (proposer.FB.getDisc(Defs.CREED) == evaluator.FB.getDisc(Defs.CREED) ? curval : 0);
		}
	};
	
	
	public static final Value FASHION = new ValuatableValue(M_.S_SILVER, "Luxury - Fashion", null, Bureaus.ECONOMY) {
		@Override
		protected int value(Clan POV, Clan clan) {return clan.getAssets(Defs.jewelry);}
		@Override
		public double contentBuyable(Clan assessor, int millet) {
			MktAbstract mkt = assessor.myMkt(Defs.jewelry);
			return Math.min(millet / mkt.buyablePX(assessor), mkt.getAskSz());
		}
	};
	public static final Value ARCHITECTURE = new ValuatableValue(M_.S_THREAT, "Luxury - Architecture", null, Bureaus.MILITARY) {
		@Override
		protected int value(Clan POV, Clan clan) {
			return clan.getAssets(Defs.land);  //should be MONUMENTS
		}
	};
	public static final Value BEAUTY = new ValuatableValue(M_.S_NOSELEN, "Luxury - Beauty", Q_.BREED, Bureaus.ECONOMY) {
		@Override
		protected int value(Clan POV, Clan clan) {
			int result = clan.FB.getFac(F_.NOSELX) + clan.FB.getFac(F_.NOSERX);
			result += clan.FB.getFac(F_.EYELW);
			result += 15 - 2 * Math.abs(7 - clan.FB.getFac(F_.MOUTHJW));
			result += Math.abs(clan.FB.getBeh(M_.OCD) - clan.FB.getFac(F_.HAIRL));
			return result;
		}
	};
	public static final Value HEALTH = new ToDoValue();
	
	
	public static final Value SKILL = new ValuatableValue(M_.S_SKILL, "Wisdom (Profession Mastery)", Q_.DREAMJOB, Bureaus.ECONOMY) {
		@Override
		protected int value(Clan POV, Clan clan) {
			double sum = 0;   for (Act a : clan.getJobActs()) {sum += a.getSkill(clan);}
			return (int) Math.round(sum / clan.getJobActs().length);
		}
	};
	public static final Value KNOWLEDGE = new ToDoValue();
	public static final Value EXPERIENCE = new ToDoValue();   //pursue is try not to die?
	public static final Value GLORY = new ToDoValue();


	
	
	
	
	
	

	private static final Value[] AllValues = new Value[] {
		WEALTH, INFLUENCE, DOMINION, MIGHT,
		FASHION, HEALTH, BEAUTY,
		LOYALTY, PATRIOTISM, PROMISCUITY, GREED, BLOODLUST, ARCHITECTURE,
		KNOWLEDGE, ZEAL, GLORY, EXPERIENCE, SKILL
	};
	private static Value[] filterTodos(Value[] varray) {
		Set<Value> set = new HashSet<Value>();
		for (Value v : varray) {if (!(v instanceof ToDoValue)) {set.add(v);}}
		Value[] result = new Value[set.size()];
		int i = 0; for(Value v : set) {result[i++] = v;}
		return result;
	}
	
	public static Value[] All = ArrayUtils.shuffle(Value.class, filterTodos(AllValues)); //final
//	public static final Value[] All = ArrayUtils.orderByComparator(Value.class, AllValues, new Comparator<Value>() {
//		@Override
//		public int compare(Value v1, Value v2) {return (int) Math.signum(v1.ordinal() - v2.ordinal());}
//	});
	
	public static final int MAXVAL = 10;
	public static final int MINVAL = -MAXVAL;

//	private static int countLand(Clan clan) {return 0;}
//	private static int countAnimals(Clan clan) {return 0;}
//	private static int countVassals(Clan clan) {return 0;}
//	private static int countWeapons(Clan clan) {return 0;}  //LOBODONKEY IS WEAPON!
	
	public static double logComp(int a, int b) {
		return logComp((double) a, (double) b);
	}
	public static double logComp(final double a, final double b) {
		return Math.min(Math.max(Math.log((a+1) / (b+1)), MINVAL), MAXVAL);
	}
	public static double fourbitComp(int a, int b) {
		return ((double) (a - b) * MAXVAL) / (double) 15;
	}

//	public static double inIsolation(double in, Value value, Clan POV) {
//		return in * (double)value.getWeighting(POV) / (double)((CompoundValue)VALUE_SYSTEM).sumWeights(POV);
//	}
	
	
	

}
