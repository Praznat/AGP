package Sentiens;

import java.util.Date;
import java.util.Timer;

import Defs.P_;
import Game.AGPmain;
import Game.Naming;
import Game.XWeapon;

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
		private static final int LENGTH = 8;
		private Reportable[] book = new Reportable[LENGTH];
		public Book() {for(int i = 0; i < book.length; i++) {book[i] = blank();}}
		public void addReport(Reportable R) {for (int i = 0; i < book.length-1; i++) {book[i] = book[i+1];} book[book.length-1] = R;}
		public Reportable[] getBook() {return book;}
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
			public String out() {return date()+(buyNotSell? "Bought " : "Sold ") + Naming.goodName(g) + " at price of " + px + " "+(buyNotSell? "from" : "to")+" " + other.getNomen();}
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
	public static Reportable findMate(final Clan mate) {
		return new Reportable() {
			public String out() {return "Searched for mate " + (mate == null ? "but found nobody worthwhile.": "and fell in love with " + mate.getNomen());}
		};
	}
	public static Reportable findWeakling(final Clan mate) {
		return new Reportable() {
			public String out() {return "Searched for someone to harass " + (mate == null ? "but found nobody worthwhile.": "and found " + mate.getNomen());}
		};
	}

	public static Reportable handToHand(final Clan opponent, final boolean winorlose) {
		return new Reportable() {
			public String out() {return "Defeated " + (winorlose?"by ":"") + opponent.getNomen() +" in combat.";}
		};
	}
	
	public static Reportable compete4Mate(final Clan mate, final Clan rival, final double result) {
		return new Reportable() {
			public String out() {
				return (result > 0 ? "Won " + mate.getNomen() + "'s heart" + (rival!=mate?" away from " + rival.getNomen():"") :
					(result < 0 ? "Failed to win " + mate.getNomen() + "'s heart" + (rival!=mate?" away from " + rival.getNomen():"") : ""));
			}
		};
	}
	

	public static Reportable discovery(final int job) {
		return new Reportable() {
			public String out() {return "Discovered a passion for " + AGPmain.TheRealm.getJob(job).getDesc();}
		};
	}
	public static Reportable practice(final P_ skill) {
		return new Reportable() {
			public String out() {return "Practiced " + Naming.prestName(skill);}
		};
	}
	
	public static Reportable demandRespect(final Clan target, final boolean success) {
		return new Reportable() {
			public String out() {return (success ? "Paid respect by " : "Disrespected by ") + target.getNomen();}
		};
	}
}
