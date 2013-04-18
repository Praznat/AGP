package Sentiens;

import AMath.*;
import Defs.*;
import Descriptions.Naming;
import Game.*;
import Sentiens.Law.Commandments;
import Sentiens.Values.Value;

public class Ideology implements Defs {

	private static final int NUMPRESTS = P_.length();
	private static final int NUMBEHS = M_.length();
	private static final int NUMVALS = Values.All.length;
	
	private static final Value[] VALUEWOF = new Value[NUMVALS * 15];
	private static final int[] VALUECUMI = new int[NUMVALS];
	
	private static final Value[] tmpGarbage = new Value[NUMVALS];
	
	private byte[] condensed;
	private Value[] sancs = new Value[NUMVALS]; //list of sancs ordered from highest to lowest
	private int[] sancranks = new int[NUMVALS]; //rank of sancs in default sanc order
	private int creed;
	private Clan Me;
	public Commandments commandments;
	public Clan getEu() {return Me;}

	public Ideology(Clan i) {   
		Me = i;   initialize(defaultVars());   this.setPrs(P_.COMBAT.ordinal(), AGPmain.rand.nextInt(15));
		commandments = new Commandments();
	}
	
	public Ideology() {
		initialize(defaultVars());
	}
	
	public void initialize(int[] in) {
		condensed = new byte[(in.length+1)/2];
		for (int i = 0; i < in.length; i++) {
			setVar(i, in[i]);
		}
		defaultSancs();
		setCreed(AGPmain.rand.nextInt());
		
	}
	public int numVars() {return condensed.length * 2;}
	
	public void setVar(int plc, int val) {
		val = Math.min(15, Math.max(0, val)); //remove to speedup
		boolean odd = (plc%2==1);
		int mask = 0xf << (odd? 4 : 0);
		val = val << (odd? 0 : 4);
		int tmp = condensed[plc/2] & mask;
		condensed[plc/2] = (byte) (tmp | val);
	}
	public void chgVar(int plc, int amt) {
		setVar(plc, getVar(plc) + amt);
	}
	public int getVar(int plc) {
		return getVar(plc, condensed);
	}
	public static int getVar(int plc, byte[] V) {
		int mask = 0xf << ((plc%2==1)? 0 : 4);
		return (V[plc/2] & mask) >> ((plc%2==1)? 0 : 4);
	}
	public int[] getSancRanks() {return sancranks;}
	
	public int getFac(F_ f) {return getVar(F(f));}
	public void setFac(F_ f, int val) {setVar(F(f), val);}
	public int getBeh(M_ m) {return getVar(B(m));}
	public int getBeh(int plc) {return getVar(B(plc));}
	public void setBeh(M_ m, int val) {setVar(B(m), val);}
	public void setBeh(int plc, int val) {setVar(B(plc), val);}
	public int getPrs(P_ p) {return getVar(P(p));}
	public int getPrs(int plc) {return getVar(P(plc));}
	public void setPrs(int plc, int val) {setVar(P(plc), val);}
	public void setPrs(P_ p, int val) {setVar(P(p), val);}

	public void upPrest(P_ plc) {
		final int p = P(plc);
		final int cur = getVar(p);
		setVar(p, (int) (4.7 + 0.75 * (double)cur));
	}
	public void downPrest(P_ plc) {
		final int p = P(plc);
		final int cur = getVar(p);
		setVar(p, (int) (0.2 + 0.75 * (double)cur));
	}
	
	public static int P(int plc) {return plc;}
	public static int P(P_ p) {return p.ordinal();}
	public static int unB(int x) {return x - NUMPRESTS;}
	public static int B(int plc) {return plc + NUMPRESTS;}
	public static int B(M_ m) {return m.ordinal() + NUMPRESTS;}
	public static int unP(int x) {return x;}
	private static final int FSTARTPLC = NUMPRESTS + NUMBEHS;
	public static int F(int plc) {return plc + FSTARTPLC;}
	public static int F(F_ f) {return f.ordinal() + FSTARTPLC;}
	public static int unF(int x) {return x - FSTARTPLC;}
	
	public byte[] copyFs() {
		byte[] result = new byte[condensed.length - FSTARTPLC/2];
		System.arraycopy(condensed, FSTARTPLC/2, result, 0, result.length);
		return result;
	}
	

	private void defaultSancs() {
		//sancs = new int[] {SZ, SS, TY, WL, HS, DE, HL, NS, AG, SC, EY, SA, BM, WP, MP, RX, FH, SH, RT, JB, LH};
		sancs = new Value[NUMVALS];
		int s = 0;   for (Value v : Values.All) {
			sancranks[v.ordinal()] = s;
			sancs[s++] = v;
		}
//		s = 0;   for (Value v : Values.All) {
//			for(int j = 0; j < NUMVALS; j++) {if (sancs[j] == v) {sancranks[s] = j;}}
//		}
		Values.All = ArrayUtils.shuffle(Value.class, Values.All); //reshuffle (this line just for testing)
	}
	public int[] defaultVars() {
		int[] V = new int[NUMBEHS + NUMPRESTS + M_.values().length];  //slow?
		for (int i = V.length - 1; i >= NUMPRESTS; i--) {V[i] = AGPmain.rand.nextInt(15);} //includes M_ and F_
		for (int i = NUMPRESTS - 1; i >= 0; i--) {V[i] = 0;} //AGPmain.rand.nextInt(15);}
		return V;
	}
	public int[] facelessVars() {  //FOR THE FACELESS MASSES!
		//retain only the face memes needed to calculate beauty
		//upgrade to faced only when met for first time by Avatar
		int[] V = new int[NUMPRESTS + M_.values().length];  //slow?
		for (int i = V.length - 1; i >= NUMPRESTS; i--) {V[i] = AGPmain.rand.nextInt(15);}
		for (int i = NUMPRESTS - 1; i >= 0; i--) {V[i] = 0;} //AGPmain.rand.nextInt(15);}
		return V;
	}
//	private void randomizeSancs() {
//		for (int i = 0; i < NUMVALS; i++) {
//			int s = AGPmain.rand.nextInt(NUMVALS);
//			sancs[i] = Values.All[s]; sancranks[s] = i; //sancvals[i] = 0;
//		}
//	}
	public void upSMeme(M_ sm) {
		int smval = getBeh(sm);
		if (smval < 15) {setBeh(sm, smval + 1);}
		else {
			int oval;   for (M_ om : M_.SMems()) {
				oval = getBeh(om);   if (oval != 0) {setBeh(om, oval - 1);}
			}
		}
	}
	public void downSMeme(M_ sm) {
		int smval = getBeh(sm);
		if (smval > 0) {setBeh(sm, smval - 1);}
		else {
			int oval;   for (M_ om : M_.SMems()) {
				oval = getBeh(om);   if (oval != 15) {setBeh(om, oval + 1);}
			}
		}
	}
	@Deprecated
	public Value randomValueByWeight2() {  //way slower than randomValueInPriority
		int L = Values.All.length;   M_ m = Values.All[0].getWeightMeme(Me);   int N = 0;
		VALUECUMI[0] = (m == null ? 0 : getBeh(m));
		for(int i = 1; i < L; i++) {
			m = Values.All[i].getWeightMeme(Me);
			N = VALUECUMI[i] = (m == null ? 0 : getBeh(m)) + N;
		}   //if(N<=0) {Calc.p("N=0"); return Values.NULL;}
		return Values.All[Calc.findLessThan(AGPmain.rand.nextInt(N), VALUECUMI)];
	}
	@Deprecated
	public Value randomValueByWeight1() {  //way slower than randomValueInPriority
		int N = 0;   int sofar = 0;
		for (Value V : Values.All) {
			if(V.getWeightMeme(Me) == null) {continue;}
			for (; N < sofar + getBeh(V.getWeightMeme(Me)); N++) {
				VALUEWOF[N] = V;
			}   sofar = N;
		}   return VALUEWOF[AGPmain.rand.nextInt(N)];
	}
	public boolean upSanc(Value s) {return upSanc(s.ordinal());}
	public boolean upSanc(int s) {
		int plc = sancranks[s];
		if (plc > 0) {
			sancranks[s] = plc - 1;   sancranks[sancs[plc-1].ordinal()] = plc;
			Value tmp = sancs[plc-1]; sancs[plc-1] = sancs[plc]; sancs[plc] = tmp;
			return true;
		}	else {return false;} //already top
	}
	public boolean downSanc(Value s) {return downSanc(s.ordinal());}
	public boolean downSanc(int s) {
		int plc = sancranks[s];
		if (plc < NUMVALS-1) {
			sancranks[s] = plc + 1;   sancranks[sancs[plc+1].ordinal()] = plc;
			Value tmp = sancs[plc+1]; sancs[plc+1] = sancs[plc]; sancs[plc] = tmp;
			return true;
		}	else {return false;} //already bottom
	}
	
	public Value randomValueInPriority() {
		final int i = AGPmain.rand.nextInt(17);
		if (i == 16) {return sancs[AGPmain.rand.nextInt(sancs.length)];} //rando factor
		int v = FSM[getBeh(M_.STRICTNESS)][i];
		return sancs[v];
	}
	public Value randomValueInPriorityOtherThan(Value not) {
		int v = FSM[getBeh(M_.STRICTNESS)][AGPmain.rand.nextInt(16)];
		boolean hitNot = false;
		for (int i = 0; i < sancs.length - 1; i++) {
			if (sancs[i] == not) {hitNot = true;}
			tmpGarbage[i] = sancs[i + (hitNot ? 1 : 0)];
		}
		return tmpGarbage[v];
	}
	public Value getValue(int i) {return sancs[i];}
	public Value[] getValues() {return sancs;}
	public int weightOfValue(Value V) {
		boolean hit = false; int N = 0;
		int[] R = FSM[getBeh(M_.STRICTNESS)];
		for (int i : R) {
			if (sancs[i] == V) {hit = true; N++;}
			else if (hit) {break;}
		}
		return N;
	}
	public Value[] valuesInPriority() {
		int[] fsm = FSM[getBeh(M_.STRICTNESS)];
		Value[] result = new Value[fsm.length];
		for (int i = 0; i < fsm.length; i++) {result[i] = sancs[fsm[i]];}
		return result;
	}
	public Value valueInPriority(int i) {
		return sancs[FSM[getBeh(M_.STRICTNESS)][i]];
	}
	public Value strongerOf(Value A, Value B) {
		for (Value v : sancs) {if (v == A) return A; else if (v == B) return B;} return null;
	}
	public int compareValues(Value A, Value B) {
		for (Value v : sancs) {if (v == A) return -1; else if (v == B) return 1;} return 0;
	}

	public int compareSanc(Clan other) { //true if eu > ele
		int k;   int ihi;
		for(int i = getEu().useBeh(M_.PATIENCE); i >= 0; i--) {
			k = AGPmain.rand.nextInt(16);
			ihi = (int) Math.signum(sancs[k].compare(getEu(), getEu(), other));
			switch (ihi) {
				case 1: return 1;
				case -1: return -1;
				default: break;
			}
		}
		return 0;
	}
	public void reflect(Value sanc, Clan other) {
		int euvsele = (int) Math.signum(sanc.compare(getEu(), getEu(), other));
		switch (euvsele) {
		case -1: downSanc(sanc);   break;
		case 0: break;
		case 1: upSanc(sanc);   break;
		}
	}
	public void reflect(Clan other) {
		reflect(ArrayUtils.randomIndexOf(Values.All), other);
	}
//	public void reflectOLD(Clan other) {
//		int euvsele = 0; boolean lostonce = false; boolean tiedonce = false;
//		int skip = 1 + getEu().useBeh(M_.MADNESS);
//		int k = 0;
//		for(int i = getEu().useBeh(M_.PATIENCE); i >= 0; i--) {
//			euvsele = SancInPriority(k).compare(getEu(), other, getEu());
//			if (euvsele < 0) {lostonce = true;}
//			else if (euvsele == 0) {tiedonce = true;}
//			if (((euvsele >= 0) && (lostonce)) || ((euvsele > 0) && (tiedonce))) {
//				upSanc(sancs[k]);   return;
//			}
//			k += AGPmain.rand.nextInt(skip) + 1;
//			if (k >= numSancs) {return;}
//		}
//	}
	public static final int[][] FSM = {
		{0,0,0,0,0,0,0,0,0,0,0,0,1,1,1,1},
		{0,0,0,0,0,0,0,0,0,0,1,1,1,1,2,2},
		{0,0,0,0,0,0,0,0,0,1,1,1,1,2,2,2},
		{0,0,0,0,0,0,0,0,1,1,1,1,2,2,2,3},
		{0,0,0,0,0,0,0,1,1,1,1,2,2,2,3,3},
		{0,0,0,0,0,0,1,1,1,1,2,2,2,3,3,4},
		{0,0,0,0,0,1,1,1,1,2,2,2,3,3,4,4},
		{0,0,0,0,1,1,1,1,2,2,2,3,3,3,4,4},
		{0,0,0,0,1,1,1,1,2,2,3,3,4,4,5,5},
		{0,0,0,1,1,1,2,2,2,3,3,3,4,4,5,6},
		{0,0,0,1,1,1,2,2,2,3,3,4,4,5,5,6},
		{0,0,0,1,1,1,2,2,3,3,4,4,5,5,6,7},
		{0,0,0,1,1,1,2,2,3,3,4,4,5,6,7,8},
		{0,0,0,1,1,2,2,3,3,4,4,5,5,6,7,8},
		{0,0,0,1,1,2,2,3,3,4,4,5,6,7,8,9},
		{0,0,1,1,2,2,3,3,4,4,5,5,6,7,8,9},
	};
	public int getSancPct(Value sanc) {
		int count = 0;   int L = FSM[0].length;
		for (int i = 0; i < L; i++) {
			if (valueInPriority(i) == sanc) {count++;}
		}
		return (int) Math.round(100 * (double)count / L);
	}
	public int getSancPcts(Value[] Sncs, double[] Pcts) {
		int c = 1;   int n = 1;   int L = FSM[0].length;
		for (int i = 1; i < L; i++) {
			if (valueInPriority(i) != valueInPriority(i-1)) {
				Sncs[c - 1] = valueInPriority(i-1);
				Pcts[c - 1] = 100*(double) n / L;
				c++;   n = 1;
			} else {n++;}
		}
		Sncs[c-1] = valueInPriority(L-1);
		Pcts[c-1] = 100*(double) n / L;
		return c;
	}

	public String getDeusName() {
		int [] deus = getDeusV();
		return Naming.randGoblinSanctityName(deus);
	}
	public int[] getDeusV() {
		int [] deus = new int[3];
		deus[2] = (byte) (creed & 63);
		deus[1] = (byte) ((creed % 63) & 63);
		deus[0] = 0;
		return deus;
	}
	public int getDeusInt() {
		return ((creed % 63) & 63) * 100 + (creed & 63);
	}

	public int getCreed() {
		return creed;
	}

	public void setCreed(int creed) {
		this.creed = creed;
	}
	
	
}


