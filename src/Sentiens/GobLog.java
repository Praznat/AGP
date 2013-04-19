package Sentiens;

import java.util.Date;

import Defs.P_;
import Descriptions.*;
import Game.*;
import Questing.FaithQuests.ActOfFaith;
import Questing.KnowledgeQuests.KnowledgeBlock;
import Sentiens.Law.Commandment;

public class GobLog {
	
	/**
	 * will not be persistable in this format!
	 * @author alexanderbraylan
	 *
	 */
	public static interface Reportable {
		public String out();
	}
	
	public static class Book {
		private static final int LENGTH = 10;
		private Reportable[] book = new Reportable[LENGTH];
		public Book() {for(int i = 0; i < book.length; i++) {book[i] = blank();}}
		public void addReport(Reportable R) {for (int i = 0; i < book.length-1; i++) {book[i] = book[i+1];} book[book.length-1] = R;}
		public Reportable[] getBook() {return book;}
		@Override
		public String toString() {
			String out = "";
			for (Reportable r : book) {out += r.out() + ", ";}
			return out;
		}
	}

	private static final String date() {
		return (new Date()).getTime()+"";
	}
	
	private static Reportable blank() {
		return new Reportable() {
			public String out() {return "";}
		};
	}

	public static Reportable idle() {
		return new Reportable() {
			public String out() {return "Idled";}
		};
	}

//	public static Reportable buySellFair(final int g, final int px, final boolean buyNotSell) {
//		return new Reportable() {
//			public String out() {return "Tried to " + (buyNotSell? "buy " : "sell ") + Naming.goodName(g) + " at fair price of " + px;}
//		};
//	}

	public static Reportable limitOrder(final int g, final int px, final boolean buyNotSell) {
		return new Reportable() {
			public String out() {return "Placed " + (buyNotSell? "bid" : "offer") + " for " + Naming.goodName(g) + " at price of " + px;}
		};
	}

	public static Reportable transaction(final int g, final int px, final boolean buyNotSell, final Clan other) {
		return new Reportable() {
			public String out() {return (buyNotSell? "Bought " : "Sold ") + Naming.goodName(g) + " at price of " + px + " "+(buyNotSell? "from" : "to")+" " + other.getNomen();}
		};
	}

	public static Reportable consume(final int g) {
		return new Reportable() {
			public String out() {return "Consumed " + Naming.goodName(g);}
		};
	}

	public static Reportable produce(final int g) {
		return new Reportable() {
			public String out() {return "Produced " + Naming.goodName(g);}
		};
	}

	public static Reportable produce(final short W) {
		return new Reportable() {
			public String out() {return "Forged " + XWeapon.weaponName(W);}
		};
	}

	public static Reportable successfulCourt(final Clan mate) {
		return new Reportable() {
			public String out() {return "Courted " + mate.getNomen() + " successfully";}
		};
	}

	public static Reportable findSomeone(final Clan target, final String what) {
		return new Reportable() {
			public String out() {return "Searched for " + what + " " + (target == null ? "but found nobody worthwhile.": "and found " + target.getNomen());}
		};
	}

	public static Reportable createOrder(final boolean preexisting) {
		return new Reportable() {
			public String out() {return preexisting ? "Decided not to follow anyone." : "Decided to start own Order";}
		};
	}
	
	public static Reportable assignMinistry(final Ministry m, final Clan c, final Clan replaced) {
		return new Reportable() {
			public String out() {return "Assigned " + c.getNomen() + " to " + m.getDesc(c) + (replaced != null ? ", replacing " + replaced.getNomen() : "");}
		};
	}
	public static Reportable decidedMoral(final Commandment c, final boolean enabled) {
		return new Reportable() {
			public String out() {return "Decided that to " + c.getVerb() + " is " + (enabled ? "" : "not ") + "a sin";}
		};
	}
	public static Reportable converted(final Clan target, final boolean success) {
		return new Reportable() {
			public String out() {return (success ? "Converted " : "Failed to convert ") + target.getNomen();}
		};
	}
	public static Reportable wasConverted(final Clan converter, final boolean success) {
		return new Reportable() {
			public String out() {return (success ? "Bowed to conversion attempt by " : "Rejected conversion attempt by ") + converter.getNomen();}
		};
	}
	
	public static Reportable handToHand(final Clan opponent, final boolean winorlose) {
		return new Reportable() {
			public String out() {return "Defeated " + (winorlose?"":"by ") + opponent.getNomen() +" in combat.";}
		};
	}
	
	public static Reportable compete4Mate(final Clan mate, final Clan rival, final double result) {
		return new Reportable() {
			public String out() {
				return (result > 0 ? "Impressed " + mate.getNomen() + (rival!=mate?" more than " + rival.getNomen():"") :
					(result < 0 ? "Failed to impress " + mate.getNomen() + (rival!=mate?" more than " + rival.getNomen():"") : ""));
			}
		};
	}
	@SuppressWarnings("rawtypes")
	public static Reportable contributeKnowledge(final KnowledgeBlock kb) {
		return new Reportable() {
			public String out() {return "Contributed " + kb;}
		};
	}
	public static Reportable observe(final Clan observee) {
		return new Reportable() {
			public String out() {return "Collected data on " + observee.getNomen();}
		};
	}
	
	public static Reportable pray(final Clan prayer, final Clan prayee, final int mana, final ActOfFaith aof) {
		return new Reportable() {
			public String out() {return prayer.getNomen() + " generated " + mana + " mana" + (prayee!=prayer?" for "+prayee:"") + " through " + aof.desc();}
		};
	}
	
	public static Reportable build(final Clan subject, final int amt, final boolean transitive) {
		return new Reportable() {
			public String out() {return transitive ? ("Produced " + amt + " splendor for " + subject.getNomen()) :
				("Acquired " + amt + " splendor built by " + subject.getNomen());}
		};
	}

	public static Reportable discovery(final Job job) {
		return new Reportable() {
			public String out() {return "Dreamt of being a " + job.getDesc();}
		};
	}
	public static Reportable practice(final P_ skill, final boolean success) {
		return new Reportable() {
			public String out() {return Naming.prestName(skill) + (success ? " level up!" : " practiced");}
		};
	}
	
	public static Reportable demandRespect(final Clan target, final boolean success) {
		return new Reportable() {
			public String out() {return (success ? "Paid respect by " : "Disrespected by ") + target.getNomen();}
		};
	}
}
