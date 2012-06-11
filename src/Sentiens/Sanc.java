//package Sentiens;
//
//import Defs.M_;
//import Defs.P_;
//import Game.Defs;
//
//
//
//public class Sanc implements Defs {
//	private Formula Val;
//	private int pointer, initQ;
//	private String name;
//	public Sanc(String S, Formula V, int p, int q) {
//		name = S; Val = V; pointer = p; initQ = q;
//	}
//	public Sanc(String S, Formula V, P_ p, int q) {
//		this(S, V, p.ordinal(), q);
//	}
//	public int value(Clan pov, Clan obj, int p) {
//		return Val.value(pov, obj, pointer);
//	}
//	public String description(Clan eu) {
//		return Val.desc(eu, name);
//	}
//	public int getQuest() {return initQ;}
//	public String getName() {return name;}
//	public int compare(Clan eu, Clan ele, Clan pov) {
//		int euvsele = value(pov, eu, pointer);
//		int buff = euvsele * (1 - pov.FB.getBeh(M_.STRICTNESS)/8);
//		euvsele = euvsele - value(pov, ele, pointer);
//		if ((euvsele < -buff)) {return -1;}
//		else if (euvsele > buff/2) {return 1;}
//		else {return 0;}
//	}
//	public boolean pursue(Clan doer) {
//		if(true) { //(continuous && mutable) {
//			
//			return true;
//		}
//			return false; // and move to next one
//	}
//	
//	public static Sanc[] SancDefs() {
//		Sanc[] S = new Sanc[numSancs];
//		S[DE] = new Sanc("Zeal", new DVal(), CREED, -1);
//		S[AG] = new Sanc("Military power", new MVal(), -1, -1);
//		S[WL] = new Sanc("Wealth", new TVal(), -1, BUILDWEALTH);
//		S[SS] = new Sanc("Popularity", new PVal(), P_.RSPCP, -1);
//		S[TY] = new Sanc("Tyranny", new PVal(), P_.TYRRP, TERRORIZE);
//		S[RX] = new Sanc("Loyalty", new RexVal(), LORD, -1);
//		S[NS] = new Sanc("Physical Attractiveness", new NoseVal(), -1, -1);
//		S[HL] = new Sanc("Patriotism", new HLVal(), HOMELAND, -1);
//		S[SZ] = new Sanc("Fruitfulness", new PVal(), P_.SEXP, BREED);
//		S[SH] = new Sanc("Homonid sacrifice", new GVal(), -1, -1);
//		S[SA] = new Sanc("Animal sacrifice", new GVal(), -1, -1);
//		S[WP] = new Sanc("Divination", new PVal(), P_.WPREDICTION, -1);
//		S[MP] = new Sanc("Supernatural Power", new PVal(), P_.MPREDICTION, -1); //maybe combo of this and adjacent 2
//		S[HS] = new Sanc("Healing", new PVal(), P_.HEALP, -1);
//		S[FH] = new Sanc("Charity", new GVal(), -1, -1);
//		S[BM] = new Sanc("Monumental glory", new GVal(), -1, -1);
//		S[RT] = new Sanc("Artistry", new GVal(), -1, -1);
//		S[SC] = new Sanc("Self-confidence", new PVal(), P_.CONFP, -1);
//		S[JB] = new Sanc("Job caste", new JVal(), ASPIRATION, -1);
//		S[LH] = new Sanc("Lid thickness", new LipVal(), -1, -1);
//		S[EY] = new Sanc("Eye width", new LipVal(), -1, -1);
//		
//		return S;
//	}
//}

