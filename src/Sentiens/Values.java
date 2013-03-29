package Sentiens;



import java.util.*;

import AMath.ArrayUtils;
import Defs.*;
import Game.*;
import Game.Do.ClanAlone;
import Questing.ExpertiseQuests;
import Sentiens.Law.Commandment;

public class Values implements Defs {
	
	private static int ord = 0;

	public static interface Assessable {
		/** returns comparison between current value and proposed value */
		public double evaluate(Clan evaluator, Clan proposer, int content);
	}
	public static interface Value extends Stressor.Causable {
		/** no longer used */
		public M_ getWeightMeme(Clan POV);
		/** no longer used */
		public int getWeighting(Clan POV);
		/** returns difference in prestige in this Value between A & B in eyes of POV */
		public double compare(Clan POV, Clan A, Clan B);
		public String description(Clan POV);
		public Q_ pursuit(Clan clan);
		public ClanAlone doPursuit(Clan clan);
		public Ministry getMinistry();
		public P_[] getRelevantSkills();
//		public double contentBuyable(Clan assessor, int millet);
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
		public Ministry getMinistry() {return null;}
		@Override
		public P_[] getRelevantSkills() {return new P_[] {};}
		@Override
		public int ordinal() {return ordinal;}
		@Override
		public ClanAlone doPursuit(Clan clan) {return Do.NOTHING;}
	}
	private static abstract class AbstractValue implements Value {
		protected final M_ weighting;
		protected final String desc;
		protected final Q_ quest;
		protected final Ministry ministry;
		protected final P_[] relSkills;
		private int ordinal;
		private ClanAlone doPursuit;
		public AbstractValue(M_ w, String d, Q_ q, Ministry j, P_[] rs) {
			weighting = w;
			desc = d;
			quest = q;
			ordinal = ord++;
			doPursuit = Do.addQuest(quest);
			ministry = j;
			relSkills = rs;
		}
		@Override
		public M_ getWeightMeme(Clan POV) {return weighting;}
		@Override
		public int getWeighting(Clan POV) {return POV.useBeh(weighting);}
		@Override
		public String description(Clan POV) {return desc;}
		@Override
		public String toString() {return description(null);}
		@Override
		public Q_ pursuit(Clan clan) {return (quest != null ? quest : Q_.NOTHING);}
		@Override
		public Ministry getMinistry() {return ministry;}
		@Override
		public P_[] getRelevantSkills() {return relSkills;}
		@Override
		public int ordinal() {return ordinal;}
		@Override
		public ClanAlone doPursuit(Clan clan) {return doPursuit;}
	}

	public static abstract class ValuatableValue extends AbstractValue implements Assessable {
		public ValuatableValue(M_ w, String d, Q_ q, Ministry j, P_[] rs) {super(w, d, q, j, rs);}
		@Override
		public double compare(Clan POV, Clan A, Clan B) {
			return compare(value(POV, A), value(POV, B));
		}
		public double compare(double A, double B) {return logComp(A, B);}
		protected double evaluateContent(Clan evaluator, Clan proposer, int content, double curval) {return content + curval;}
		@Override
		public final double evaluate(Clan evaluator, Clan proposer, int content) {
			double curVal = value(evaluator, evaluator);
			return evaluator.FB.weightOfValue(this) * compare(evaluateContent(evaluator, proposer, content, curVal), curVal);
		}
		protected abstract int value(Clan POV, Clan clan);
	}

	public static final Value NULL = new ToDoValue();

	public static final Value MIGHT = new ValuatableValue(M_.S_NVASSALS, "Power - Might", Q_.RECRUIT, Job.GENERAL,
			new P_[] {P_.COMBAT, P_.MARKSMANSHIP, P_.STRENGTH}) {
		@Override
		protected int value(Clan POV, Clan clan) {return 0;}    // TODO this should be about shires controlled
	};

	public static final Value PROPERTY = new ValuatableValue(M_.S_MONEY, "Power - Property", Q_.BUILDWEALTH, Job.TREASURER,
			new P_[] {P_.CARPENTRY, P_.SMITHING, P_.MASONRY, P_.ARTISTRY, P_.LOBOTOMY}) {
		@Override
		protected int value(Clan POV, Clan clan) {
			return (int) Math.min(clan.getNetAssetValue(POV), Integer.MAX_VALUE/2);
		}
	};
	public static final Value INFLUENCE = new ValuatableValue(M_.S_POPULARITY, "Power - Influence", Q_.BUILDPOPULARITY, Job.VIZIER, new P_[] {}) {
		@Override
		protected int value(Clan POV, Clan clan) {return clan.getMinionTotal();} // * P_.RSPCT ??
	};

	//TODO VIRTUE "Honor - Virtue"

	public static final Value CREED = new ValuatableValue(null, "Honor - Creed", Q_.CREEDQUEST, Job.JUDGE, new P_[] {}) {
		@Override
		public double compare(double A, double B) {return logCompNeg(A, B);}
		@Override
		protected int value(Clan POV, Clan clan) {   //maybe add human sacrifice?
			int sins = 0;
			Commandment[] myCommandments = POV.FB.commandments.list;
			Commandment[] hisCommandments = clan.FB.commandments.list;
			for (int i = 0; i < myCommandments.length; i++) {
				if (myCommandments[i].isActive()) {sins += hisCommandments[i].getTransgressions();}
			}
			return -sins;
		}
	};
	
	public static final Value ALLEGIANCE = new ValuatableValue(M_.S_LOYALTY, "", Q_.LOYALTYQUEST, Job.NOBLE, new P_[] {}) {
		@Override
		public String description(Clan POV) {return "Honor - Allegiance" + (POV != null && POV != POV.getBoss() ? " to " + POV.getBoss() : "");}
		@Override
		public double compare(double A, double B) {return logCompNeg(A, B);}
		@Override
		protected int value(Clan POV, Clan clan) { // how far from same Order boss are you
			return -(clan.myOrder() == POV.myOrder() ? clan.distanceFromTopBoss() : Integer.MAX_VALUE/2);
		}
		@Override
		protected double evaluateContent(Clan evaluator, Clan proposer, int content, double curval) {
			return value(evaluator, proposer);
		}
	};
	
	public static final Value LEGACY = new ValuatableValue(null, "Honor - Legacy", Q_.LEGACYQUEST, Job.HISTORIAN, new P_[] {}) {
		@Override
		protected int value(Clan POV, Clan clan) {   //maybe add human sacrifice?
			//TODO
			return 0;
		}
	};
	
	public static final Value SPLENDOR = new ValuatableValue(M_.S_THREAT, "Luxury - Splendor", Q_.SPLENDORQUEST, Job.ARCHITECT,
			new P_[] {P_.MASONRY, P_.ARTISTRY}) {
		@Override
		protected int value(Clan POV, Clan clan) {
			return clan.getSplendor();  //should be MONUMENTS
		}
	};
	public static final Value BEAUTY = new ValuatableValue(M_.S_NOSELEN, "Luxury - Beauty", Q_.BREED, Job.COURTESAN, new P_[] {}) {
		@Override
		protected int value(Clan POV, Clan clan) {
			int result = clan.FB.getFac(F_.NOSELX) + clan.FB.getFac(F_.NOSERX);
			result += clan.FB.getFac(F_.EYELW);
			result -= 2 * Math.abs(7 - clan.FB.getFac(F_.MOUTHJW));
			result -= Math.abs(25 - clan.getAge()); // age
			result += Math.abs(clan.FB.getBeh(M_.OCD) - clan.FB.getFac(F_.HAIRL));
			result += Math.min(15, clan.getAssets(Defs.jewelry)); // no need for more than 15 jewelry
			return result;
		}
	};
	public static final Value HEALTH = new ValuatableValue(M_.S_SKILL, "Luxury - Health", Q_.BREED, Job.APOTHECARY, new P_[] {}) {
		@Override
		protected int value(Clan POV, Clan clan) {
			//TODO
			return clan.getAge();
		}
	};
	
	
	public static final Value EXPERTISE = new ValuatableValue(M_.S_SKILL, "Mind - Expertise", Q_.TRAIN, Job.TUTOR, new P_[] {}) {
		@Override
		protected int value(Clan POV, Clan clan) {
			double sum = 0;   for (P_ s : ExpertiseQuests.ALLSKILLS) {sum += clan.FB.getPrs(s);}
			return (int) Math.round(sum / ExpertiseQuests.ALLSKILLS.length);
		}
	};
	public static final Value WISDOM = new ValuatableValue(M_.S_SKILL, "Mind - Wisdom", Q_.BREED, Job.PHILOSOPHER, new P_[] {P_.ARITHMETIC}) {
		@Override
		protected int value(Clan POV, Clan clan) {   //maybe add human sacrifice?
			//TODO
			return 0;
		}
	};
	public static final Value FAITH = new ValuatableValue(M_.S_SKILL, "Mind - Faith", Q_.FAITHQUEST, Job.SORCEROR, new P_[] {}) {
		@Override
		protected int value(Clan POV, Clan clan) {   //maybe add human sacrifice?
			//TODO
			return 0;
		}
	};

	
	
	
	
	
	

	private static final Value[] AllValues = new Value[] {
		PROPERTY, INFLUENCE, MIGHT, SPLENDOR, HEALTH, BEAUTY,
		ALLEGIANCE, CREED, LEGACY, WISDOM, FAITH, EXPERTISE
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
	public static double logCompNeg(final double a, final double b) {
		return Math.min(Math.max(Math.log((b-1) / (a-1)), MINVAL), MAXVAL);
	}
	public static double fourbitComp(int a, int b) {
		return ((double) (a - b) * MAXVAL) / (double) 15;
	}

//	public static double inIsolation(double in, Value value, Clan POV) {
//		return in * (double)value.getWeighting(POV) / (double)((CompoundValue)VALUE_SYSTEM).sumWeights(POV);
//	}
	
	
	

}
