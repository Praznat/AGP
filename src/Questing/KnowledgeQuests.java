package Questing;

import java.util.*;

import AMath.Calc;
import Defs.M_;
import Game.*;
import Questing.Quest.PatronedQuest;
import Questing.Quest.PatronedQuestFactory;
import Sentiens.*;
import Sentiens.Values.Value;

public class KnowledgeQuests {
	public static PatronedQuestFactory getMinistryFactory() {return new PatronedQuestFactory(KnowledgeQuest.class) {public Quest createFor(Clan c) {return new KnowledgeQuest(c, c.getBoss());}};}
	
	public static class KnowledgeQuest extends PatronedQuest {
		public KnowledgeQuest(Clan P, Clan patron) {super(P, patron);}

		@Override
		public void pursue() {
			// TODO Auto-generated method stub
			final Value v = patron.FB.randomValueInPriority();
			if (v == Values.WEALTH) {
				replaceAndDoNewQuest(Me, new ObservationQuest<Job>(Me, patron, new JobObserver()));
			}
			else if (v == Values.INFLUENCE) {
				replaceAndDoNewQuest(Me, new ObservationQuest<Value>(Me, patron, new ValueObserver()));
			}
		}
		
	}
	public static class ObservationQuest<T> extends PatronedQuest {
		private final KnowledgeObserver<T> knowledgeObserver;
		private final Clan[] observationPopulation;
		private int turnsLeft;
		private transient int[] popv;
		private transient int i = 0;
		public ObservationQuest(Clan P, Clan patron, KnowledgeObserver<T> ko) {
			super(P, patron);
			knowledgeObserver = ko;
			turnsLeft = P.FB.getBeh(M_.PATIENCE) + 10; //between 10 and 25
			observationPopulation = TargetQuest.getReasonableCandidates(patron);
			popv = Calc.randomOrder(observationPopulation.length);
		}
		@Override
		public void pursue() {
			if (turnsLeft-- <= 0) {
				final KnowledgeBlock<T> result = knowledgeObserver.createKnowledgeBlock();
				Me.contributeKnowledge(result, patron.myShire());
				Me.addReport(GobLog.contributeKnowledge(result));
				success(); return;
			}
			final Clan target = observationPopulation[popv[i++]];
			knowledgeObserver.observe(target);
			Me.addReport(GobLog.observe(target));
		}
	}
	
	// Knowledge :
	// Expertise :
	// Spirituality : find out what prayStyles and "nature" memes correlate to high holiness levels
	// Righteousness : find out what "nature" memes correlate to low sin levels
	// Legacy : "normalize" other people's legacies
	// Allegiance : find out what values ppl tend to have in shire / order
	// Influence : find out what values ppl tend to have in shire / order?
	// Wealth : find out what jobs and "business" memes correlate with NAV / earningsMA
	// Might : find out what "war" memes correlate with BATTLEP
	// Freedom :
	// Beauty : find out what values ppl tend to have in shire / order?
	// Comfort :
	
	/**
	 * for storage in library
	 */
	public static class KnowledgeBlock<T> {
		protected Object[] x;
		protected int[] y;
		protected int obsUsed, date;
		public Object[] getXs() {return x;}
		public int getNumObservationsUsed() {return obsUsed;}
		public int getDateRecorded() {return date;}
		@Override
		public String toString() {
			String s = "";
			for (int i = 0; i < x.length; i++) {
				final Object o = x[i];
				if (o == null) {break;}
				s += o + ":" + y[i] + "; ";
			}
			return s;
		}
	}
	private static class Top3Block<T> extends KnowledgeBlock<T> {
		public Top3Block(Map<T, Integer> map) {
			int gold = 0, silver = 0, bronze = 0;
			T goldV = null, silverV = null, bronzeV = null;
			for (Map.Entry<T, Integer> entry : map.entrySet()) {
				final T v = entry.getKey(); final int i = entry.getValue();
				if (i > gold) {bronze = silver; bronzeV = silverV; silver = gold; silverV = goldV; gold = i; goldV = v;}
				else if (i > silver) {bronze = silver; bronzeV = silverV; silver = i; silverV = v;}
				else if (i > bronze) {bronze = i; bronzeV = v;}
			}
			x = new Object[] {goldV, silverV, bronzeV};
			y = new int[] {gold, silver, bronze};
			obsUsed = map.size(); date = AGPmain.TheRealm.getDay();
		}
	}

	private static abstract class KnowledgeObserver<T> {
		protected Map<T, Integer> map = new HashMap<T, Integer>();
		public abstract void observe(Clan c);
		public abstract KnowledgeBlock<T> createKnowledgeBlock();
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
		public KnowledgeBlock<Value> createKnowledgeBlock() {
			return new Top3Block<Value>(map);
		}
	}
	
	private static class JobObserver extends KnowledgeObserver<Job> {
		@Override
		public void observe(Clan c) {
			final Job j = c.getJob();
			final int x = c.getProfitEMA(); // NAV ?
			final Integer oldX = map.get(j); // from possible previous Clans
			map.put(j, oldX == null ? x : oldX + x);
		}
		@Override
		public KnowledgeBlock<Job> createKnowledgeBlock() {
			return new Top3Block<Job>(map);
		}
	}
	
	private static class DataModel {
		
	}
	private static class ContinuousCorrelation {
		private DataBlob[] indVars;
	}
	private static class DataBlob {
		private final DataType dataType;
		private int sum, numObs;
		public DataBlob(DataType dataType) {this.dataType = dataType;}
		public void addObs(int obs) {sum += obs; numObs++;}
		public double getAvg() {return ((double) sum) / numObs;}
	}
	private static class DataType {
		private boolean isDiscrete;
		public DataType(boolean isDiscrete) {this.isDiscrete = isDiscrete;}
		public boolean isDiscrete() {return isDiscrete;}
	}
	private static final DataType MEME = new DataType(false);
	private static final DataType PRES = new DataType(false);
	private static final DataType VAL = new DataType(true);
	private static final DataType PRAY = new DataType(true);
	private static final DataType JOB = new DataType(true);
	
}
