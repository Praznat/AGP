package Questing;

import java.util.*;

import AMath.Calc;
import Avatar.*;
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
			final Value v = patron.FB.randomValueInPriority();
			pursueKnowledge(v);
		}
		
		@Override
		public void avatarPursue() {
			avatarConsole().showChoices(Me, Values.All, SubjectiveType.VALUE_ORDER, new Calc.Listener() {
				@Override
				public void call(Object arg) {
					pursueKnowledge((Value) arg);
				}
			}, new Calc.Transformer<Value, String>() {
				@Override
				public String transform(Value v) {
					return "Study " + v.description(Me);
				}
			});
		}
		@SuppressWarnings("rawtypes")
		private void useKnowledgeBlock(KnowledgeBlock kb) {
			kb.useKnowledge(patron);
		}
		@SuppressWarnings("rawtypes")
		public void pursueKnowledge(Value v) {
			final KnowledgeBlock kb = patron.getRelevantLibrary().findKnowledge(v);
			if (kb != null) {useKnowledgeBlock(kb); finish(); return;}
			final Quest newQuest = valueToKnowledgeQuest(v);
			if (newQuest == null) {finish(); return;} // eventually should never happen
			Me.MB.newQ(newQuest);
		}
		private Quest valueToKnowledgeQuest(Value v) {
			if (v == Values.WEALTH) {
				return new ObservationQuest<Job>(Me, patron, new JobObserver());
			}
			else if (v == Values.INFLUENCE) {
				return new ObservationQuest<Value>(Me, patron, new ValueObserver());
			}
			else return null;
		}
		@Override
		public String description() {return "Pursue knowledge";}
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
			if (turnsLeft-- <= 0 || i >= observationPopulation.length) {
				final KnowledgeBlock<T> result = knowledgeObserver.createKnowledgeBlock(Me);
				patron.getRelevantLibrary().putKnowledge(result); // patron doesnt get credit but does get knowledge placement
				Me.addReport(GobLog.contributeKnowledge(result));
				((KnowledgeQuest)upQuest()).useKnowledgeBlock(result);
				success(); return;
			}
			final Clan target = observationPopulation[popv[i++]];
			knowledgeObserver.observe(target);
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
		@SuppressWarnings("rawtypes")
		protected Class getKnowledgeClass() {return x[0].getClass();}
		public void useKnowledge(Clan user) {
			if (user != AGPmain.mainGUI.AC.getAvatar()) {alterBrain(user);} // should it not be automatic?
			if (discoverer != null) discoverer.incKnowledgeAttribution();
		}
		protected abstract void alterBrain(Clan user);
		public boolean isApplicableFor(Value v) {return v == relVal();}
		protected abstract Value relVal();
	}
	private static class Top3Block<T> extends KnowledgeBlock<T> {
		public Top3Block(Clan clan, Map<T, Integer> map) {
			super(clan);
			int gold = Integer.MIN_VALUE, silver = Integer.MIN_VALUE, bronze = Integer.MIN_VALUE;
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
		@Override
		public void alterBrain(Clan user) {
			if (Value.class.isAssignableFrom(getKnowledgeClass())) {
				if (user.FB.upSanc((Value)x[0])) {return;} // if goldV is not already 1st, increase it, continue if already top
				final Value s = (Value)x[1]; final Value b = (Value)x[2];
				if (user.FB.getValue(1) != s) {user.FB.upSanc(s); return;} // if silver is not already 2nd, increase it
				if (user.FB.getValue(2) != b) {user.FB.upSanc(b); return;} // if bronze is not already 3nd, increase it
			}
			else if (Job.class.isAssignableFrom(getKnowledgeClass())) {
				if (y[0] > user.getAvgIncome()) {user.setJob((Job)x[0]);}
			}
		}
		@Override
		protected Value relVal() {
			if (Value.class.isAssignableFrom(getKnowledgeClass())) {return Values.INFLUENCE;}
			if (Job.class.isAssignableFrom(getKnowledgeClass())) {return Values.WEALTH;}
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
			final int x = c.getAvgIncome(); // NAV ?
			final Integer oldX = map.get(j); // from possible previous Clans
			map.put(j, oldX == null ? x : oldX + x);
		}
		@Override
		public KnowledgeBlock<Job> createKnowledgeBlock(Clan creator) {
			return new Top3Block<Job>(creator, map);
		}
	}
	
}
