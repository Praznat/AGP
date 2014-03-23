package Questing;

import java.util.*;

import AMath.Calc;
import Avatar.*;
import Defs.*;
import Descriptions.GobLog;
import Game.*;
import Questing.Quest.PatronedQuest;
import Questing.Quest.PatronedQuestFactory;
import Sentiens.*;
import Sentiens.Values.Value;
import Shirage.Shire;

public class KnowledgeQuests {
	public static PatronedQuestFactory getMinistryFactory() {return new PatronedQuestFactory(KnowledgeQuest.class) {public Quest createFor(Clan c, Clan p) {return new KnowledgeQuest(c, p);}};}
	
	public static class KnowledgeQuest extends PatronedQuest {
		private final K_ kToStudy;
		public KnowledgeQuest(Clan P, Clan patron) {super(P, patron); kToStudy = valToK(patron.FB.randomValueInPriority());}
		public KnowledgeQuest(Clan P, Clan patron, K_ k) {super(P, patron); kToStudy = k;}

		@Override
		public void pursue() {
			pursueKnowledge(kToStudy);
		}
		
		@Override
		public void avatarPursue() {
			avatarConsole().showChoices("Choose quest", Me, K_.values(), SubjectiveType.NO_ORDER, new Calc.Listener() {
				@Override
				public void call(Object arg) {
					pursueKnowledge(valToK((Value) arg));
				}
			}, new Calc.Transformer<K_, String>() {
				@Override
				public String transform(K_ k) {
					return "Study " + k;
				}
			});
		}
		@SuppressWarnings("rawtypes")
		private void useKnowledgeBlock(KnowledgeBlock kb) {
			kb.useKnowledge(patron);
			finish();
		}
		@SuppressWarnings("rawtypes")
		public void pursueKnowledge(K_ k) {
			final KnowledgeBlock kb = patron.getRelevantLibrary().findKnowledge(k);
			if (kb != null) {useKnowledgeBlock(kb); return;}
			final Quest newQuest = kToKnowledgeQuest(k);
			if (newQuest == null) {finish(); return;} // eventually should never happen
			Me.MB.newQ(newQuest);
		}
		private K_ valToK(Value v) {
			if (v == Values.WEALTH) return K_.JOBS;
			else if (v == Values.INFLUENCE) return K_.POPVALS;
			else return K_.NADA;
		}
		private Quest kToKnowledgeQuest(K_ k) {
			switch(k) {
			case JOBS: return new ObservationQuest<Job>(Me, patron, new JobObserver());
			case POPVALS: return new ObservationQuest<Value>(Me, patron, new ValueObserver());
			default: return null;
			}
		}
		@Override
		public String description() {return "Pursue knowledge";}
	}
	public static class ObservationQuest<T> extends PatronedQuest {
		private static final int NUM_OBS_PER_TURN = 3;
		private final KnowledgeObserver<T> knowledgeObserver;
		private final Clan[] observationPopulation;
		private int turnsLeft;
		private transient int[] popv; //really transient??
		private transient int i = 0; //really transient??
		public ObservationQuest(Clan P, Clan patron, KnowledgeObserver<T> ko) {
			super(P, patron);
			knowledgeObserver = ko;
			turnsLeft = P.FB.getBeh(M_.PATIENCE) + 10; //between 10 and 25
			observationPopulation = TargetQuest.getReasonableCandidates(patron);
			popv = Calc.randomOrder(observationPopulation.length);
		}
		@Override
		public void pursue() {
			if (turnsLeft-- <= 0 || i >= observationPopulation.length) {
				final KnowledgeBlock<T> result = knowledgeObserver.createKnowledgeBlock(Me);
				patron.getRelevantLibrary().putKnowledge(result); // patron doesnt get credit but does get knowledge placement
				Me.addReport(GobLog.contributeKnowledge(result));
				((KnowledgeQuest)upQuest()).useKnowledgeBlock(result);
				success(); return;
			}
			final Clan target = observationPopulation[popv[i++]];
			for (int i = 0; i < NUM_OBS_PER_TURN; i++) {knowledgeObserver.observe(target);}
			Me.addReport(GobLog.observe(target));
		}
		@Override
		public String description() {return "Make observations";}
	}
	
	// Knowledge :
	// Spirituality : find out what prayStyles and "nature" memes correlate to high holiness levels
	// Freedom :
	// Beauty : find out what values ppl tend to have in shire / order?
	// Wealth : find out what jobs and "business" memes correlate with NAV / earningsMA
	// Expertise :
	// Allegiance : find out what values ppl tend to have in shire / order
	// Influence : find out what values ppl tend to have in shire / order?
	// Righteousness : find out what "nature" memes correlate to low sin levels
	// Might : find out what "war" memes correlate with BATTLEP
	// Legacy : "normalize" other people's legacies
	// Comfort :
	
	
	/**
	 * for storage in library
	 */
	public abstract static class KnowledgeBlock<T> {
		protected Object[] x;
		protected int[] y;
		protected int obsUsed, date;
		private final Clan discoverer;
		public KnowledgeBlock(Clan clan) {discoverer = clan;}
		public Object[] getXs() {return x;}
		public int[] getYs() {return y;}
		public int getNumObservationsUsed() {return obsUsed;}
		public int getDateRecorded() {return date;}
		public Clan getDiscoverer() {return discoverer;}
		@Override
		public String toString() {
			String s = "";
			for (int i = 0; i < x.length; i++) {
				final Object o = x[i];
				if (o == null) {break;}
				s += o + ":" + y[i] + "; ";
			}
			return s + " at day " + date + " by " + discoverer;
		}
		@SuppressWarnings("rawtypes")
		protected Class getKnowledgeClass() {return x[0].getClass();}
		public void useKnowledge(Clan user) {
			if (user != AGPmain.getAvatar()) {alterBrain(user);} // should it not be automatic?
			if (discoverer != null) discoverer.incKnowledgeAttribution();
		}
		protected abstract void alterBrain(Clan user);
		public boolean isApplicableFor(K_ k) {return k == relK();}
		public abstract K_ relK();
	}
	private static class Top3Block<T> extends KnowledgeBlock<T> {
		public Top3Block(Clan clan, Map<T, Integer> map) {
			super(clan);
			int gold = Integer.MIN_VALUE, silver = Integer.MIN_VALUE, bronze = Integer.MIN_VALUE;
			T goldO = null, silverO = null, bronzeV = null;
			for (Map.Entry<T, Integer> entry : map.entrySet()) {
				final T v = entry.getKey(); final int i = entry.getValue();
				if (i > gold) {bronze = silver; bronzeV = silverO; silver = gold; silverO = goldO; gold = i; goldO = v;}
				else if (i > silver) {bronze = silver; bronzeV = silverO; silver = i; silverO = v;}
				else if (i > bronze) {bronze = i; bronzeV = v;}
			}
			x = new Object[] {goldO, silverO, bronzeV};
			y = new int[] {gold, silver, bronze};
			obsUsed = map.size(); date = AGPmain.TheRealm.getDay();
		}
		@Override
		public void alterBrain(Clan user) {
			if (Value.class.isAssignableFrom(getKnowledgeClass())) {
				if (user.FB.upSanc((Value)x[0])) {return;} // if goldV is not already 1st, increase it, continue if already top
				final Value s = (Value)x[1]; final Value b = (Value)x[2];
				if (user.FB.getValue(1) != s) {user.FB.upSanc(s); return;} // if silver is not already 2nd, increase it
				if (user.FB.getValue(2) != b) {user.FB.upSanc(b); return;} // if bronze is not already 3nd, increase it
			}
			else if (Job.class.isAssignableFrom(getKnowledgeClass())) {
				// this means neighboring shires' KBs wont get "used" (no wisdom attribution to discoverer) IS THIS RIGHT?
				// probably? if they all get used, that means this is way more likely to get used than other types of KB
				// so u would always want to go around researching jobs in other shires rather than anything else to up ur attribution...
				Calc.ThreeObjects<Shire, Object, Integer> bestO = bestInShires(user, relK(), user.getAvgIncome(), true, true, true);
				if (bestO != null) {
					user.setJob((Job)bestO.get2nd());
					Shire newShire = bestO.get1st();
					if (newShire != user.myShire()) {user.MB.newQ(new ImmigrationQuests.EmigrateQuest(user, newShire));}
				}
			}
		}
		@Override
		public K_ relK() {
			if (Value.class.isAssignableFrom(getKnowledgeClass())) {return K_.POPVALS;}
			if (Job.class.isAssignableFrom(getKnowledgeClass())) {return K_.JOBS;}
			else return null;
		}
	}

	private static abstract class KnowledgeObserver<T> {
		protected Map<T, Integer> map = new HashMap<T, Integer>();
		public abstract void observe(Clan c);
		public abstract KnowledgeBlock<T> createKnowledgeBlock(Clan creator);
	}
	
	private static class ValueObserver extends KnowledgeObserver<Value> {
		@Override
		public void observe(Clan c) {
			Value lastV = null; int x = 0;
			for (Value v : c.FB.valuesInPriority()) {
				if (v != lastV) {
					if(lastV != null) {
						final Integer oldX = map.get(v); // from possible previous Clans
						map.put(v, oldX == null ? x : oldX + x);
					} // next value
					lastV = v; x = 1;
				}
				else {x++;} // same value
			}
		}
		@Override
		public KnowledgeBlock<Value> createKnowledgeBlock(Clan creator) {
			return new Top3Block<Value>(creator, map);
		}
	}
	
	private static class JobObserver extends KnowledgeObserver<Job> {
		@Override
		public void observe(Clan c) {
			final Job j = c.getJob();
			final int x = (int)Math.round(c.getAvgIncome()); // NAV ?
			final Integer oldX = map.get(j); // from possible previous Clans
			map.put(j, oldX == null ? x : oldX + x);
		}
		@Override
		public KnowledgeBlock<Job> createKnowledgeBlock(Clan creator) {
			return new Top3Block<Job>(creator, map);
		}
	}
	
	@SuppressWarnings("rawtypes")
	private static Calc.ThreeObjects<Shire, Object, Integer> bestInShires(
			Clan clan, K_ kType, double threshold, boolean hiOrLo, boolean areaOrPath, boolean stopAtNoLibrary) {

		clan.myShire().getNeighbors(true);
		final int numShiresToLookAt = clan.FB.getBeh(M_.PATIENCE) / 3 + 2; // one for myshire
		Shire lastShire = null; Shire bestShire = null;
		double best = threshold; Object bestO = null; 
		for (int i = 0; i < numShiresToLookAt; i++) {
			final Shire newShire = lastShire == null ? clan.myShire() :
				(areaOrPath ? clan.myShire().getSomeNeighbor() : lastShire.getSomeNeighbor());
			if (newShire == null) {continue;} // edge
			final KnowledgeBlock kb = newShire.getLibrary().findKnowledge(kType);
			if (kb == null) {if (stopAtNoLibrary) {break;} else {continue;}}
			int newPay = kb.getYs()[0];
			int n = hiOrLo ? 1 : -1;
			if (n*newPay > n*best) {
				best = newPay; bestO = kb.getXs()[0]; bestShire = newShire;
			}
		}
		return bestO != null ? new Calc.ThreeObjects<Shire, Object, Integer>(bestShire, bestO, (int)best) : null;
	}
	
}
