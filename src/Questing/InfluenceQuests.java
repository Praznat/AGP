package Questing;

import java.util.*;

import AMath.Calc;
import Avatar.*;
import Defs.M_;
import Game.*;
import Government.Order;
import Questing.Quest.QuestFactory;
import Sentiens.*;
import Sentiens.Values.Value;

public class InfluenceQuests {
	public static QuestFactory getMinistryFactory() {return new QuestFactory(InfluenceQuest.class) {public Quest createFor(Clan c) {return new InfluenceQuest(c, c.getBoss());}};}
	
	public static class PropagandaQuest extends Quest {
		public PropagandaQuest(Clan P) {super(P);}
		@Override
		public void pursue() {
			// preach?
			replaceAndDoNewQuest(Me, new PersecutionQuests.PersecuteInfidel(Me));
		}
	}
	
	public static class InfluenceQuest extends Quest {
		private Clan patron, rival;
		private Order myOrder, rivalOrder;
		private int numFollowers, rivalFollowers;
		public InfluenceQuest(Clan P) {this(P, P);}
		public InfluenceQuest(Clan P, Clan patron) {super(P); this.patron = patron;}
		@Override
		public String description() {return "Expand influence";}

		@Override
		public void avatarPursue() {
			if (!(patron == Me || patron.isSomeBossOf(Me))) {Me.MB.finishQ();   return;}
			commonPursuit();
			final GradedQuest[] options = {new GradedQuest(new PersecutionQuests.PersecuteInfidel(Me), rivalFollowers - numFollowers),
					new GradedQuest(new OrganizeMinistries(Me), numFollowers - rivalFollowers)};
			avatarConsole().showChoices(Me, options, SubjectiveType.QUEST_ORDER, new Calc.Listener() {
				@Override
				public void call(Object arg) {
					replaceAndDoNewQuest(Me, ((GradedQuest) arg).getQuest());
				}
			});
		}
		
		@Override
		public void pursue() {
			if (!(patron == Me || patron.isSomeBossOf(Me))) {Me.MB.finishQ();   return;}
			commonPursuit();
			if (numFollowers <= rivalFollowers) {
				replaceAndDoNewQuest(Me, new PersecutionQuests.PersecuteInfidel(Me));
			}
			else {
				replaceAndDoNewQuest(Me, new OrganizeMinistries(Me));
			}
		}
		private void commonPursuit() {
			rival = Calc.oneOf(AGPmain.TheRealm.getPopulation());
			myOrder = patron.myOrder();
			rivalOrder = rival.myOrder();
			if (myOrder == null) {myOrder = Order.createBy(patron); patron.addReport(GobLog.createOrder(false));}
			numFollowers = myOrder.getFollowers(Me, false, true).size();
			rivalFollowers = rivalOrder == null ? 0 : rivalOrder.getFollowers(rival, false, true).size();
		}
	}
	
	public static class OrganizeMinistries extends Quest {
		private final ArrayList<Clan> followers;
		private final HashMap<Ministry, Integer> numEach = new HashMap<Ministry, Integer>();
		private int movesLeft = 1 + Me.useBeh(M_.PATIENCE);
		public OrganizeMinistries(Clan P) {
			super(P);
			followers = new ArrayList<Clan>();
			final Order order = Me.myOrder();
			if (order != null) {followers.addAll(order.getFollowers(Me, false, false));}
		}
		@Override
		public String description() {return "Assign subordinates";}
		
		@Override
		public void avatarPursue() {
			if (Math.min(movesLeft--, followers.size()) <= 0) {
				Me.MB.finishQ();   return;
			}
			Calc.Transformer<Clan, String> describer = new Calc.Transformer<Clan, String>() {
				@Override
				public String transform(Clan c) {
					return c.getNomen() + " : " + c.getJob().getDesc(c);
				}
			};
			avatarConsole().showChoices(Me, followers, SubjectiveType.RESPECT_ORDER, new Calc.Listener() {
				@Override
				public void call(Object arg) {
					final Clan clan = (Clan)arg;
					avatarConsole().showChoices(Me, Values.All, SubjectiveType.VALUE_ORDER, new Calc.Listener() {
						@Override
						public void call(Object arg) {
							clan.setJob(((Value)arg).getMinistry());
							followers.remove(clan);
						}
					}, new Calc.Transformer<Value, String>() {
						@Override
						public String transform(Value v) {
							return v.getMinistry().getDesc(clan);
						}
					});
				}
			}, describer);
		}
		
		@Override
		public void pursue() {
			if (Math.min(movesLeft--, followers.size()) <= 0) {
				Me.MB.finishQ();   return;
			}
			if (numEach.isEmpty()) {
				determineNumEach();
				return;
			}
			final int i = AGPmain.rand.nextInt(followers.size());
			final Clan c = followers.get(i);
			if (Me.isDirectBossOf(c)) {
				final Value v = Me.FB.randomValueInPriority();
				final Ministry m = v.getMinistry();
				final Integer N = numEach.get(m);
				final int n = N != null ? N : 0;
				
				if (n > 0) {
					c.setJob(m);
					Me.addReport(GobLog.assignMinistry(m, c, null));
					numEach.put(m, n-1);
				}
				else {
					final ArrayList<Clan> ministers = findFollowersWithMinistry(m);
					if (ministers.isEmpty()) {return;} //TODO wait why
					final Clan alt = ministers.get(AGPmain.rand.nextInt(ministers.size()));
					if (v.compare(Me, c, alt) > 0) {
						alt.setJob(Job.NOBLE); // ALLEGIANCE ministry is default (minion)
						c.setJob(m);
						Me.addReport(GobLog.assignMinistry(m, c, alt));
					}
				}
			}
			followers.remove(i);
		}
		private void determineNumEach() {
			numEach.clear();
			int assignableLeft = Me.getMinionN();
			final Value[] sncs = new Value[Ideology.FSM[0].length];
			final double[] pcts = new double[Ideology.FSM[0].length];
			Me.FB.getSancPcts(sncs, pcts);
			for (int i = 0; i < sncs.length; i++) {
				final int n = Math.min((int) Math.round(pcts[i] * Me.getMinionN()), assignableLeft);
				if (n <= 0) {return;}
				numEach.put(sncs[i].getMinistry(), n);
				assignableLeft -= n;
				if (assignableLeft == 0) {return;}
			}
		}
		private ArrayList<Clan> findFollowersWithMinistry(Ministry m) {
			ArrayList<Clan> result = new ArrayList<Clan>();
			Collection<Clan> f = Me.myOrder().getFollowers(Me, false, false);
			for (Clan c : f) {
				if (c.getJob() == m) {result.add(c);}
			}
			return result;
		}
	}
	
}
