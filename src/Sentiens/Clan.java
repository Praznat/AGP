package Sentiens;

import Defs.M_;
import Defs.P_;
import Defs.Q_;
import Descriptions.GobName;
import Descriptions.Naming;
import Game.AGPmain;
import Game.Act;
import Game.Defs;
import Game.Goods;
import Game.Job;
import Game.Order;
import Markets.*;
import Questing.Quest;
import Questing.Quest.DefaultQuest;
import Questing.OrderQuests.LoyaltyQuest;
import Questing.PersecutionQuests.*;
import Questing.RomanceQuests.BreedQuest;
import Questing.WorkQuests.BuildWealthQuest;
import Sentiens.GobLog.Book;
import Sentiens.GobLog.Reportable;
import Shirage.Shire;
import AMath.Calc;

public class Clan implements Defs, Stressor.Causable, Order.Serveable {
	protected static final int DMC = 10; //daily millet consumption
	//protected static final int MEMORY = 8;
	//bio
	protected byte[] name = new byte[2];
	protected boolean gender;
	private int age;

	protected int numSpawns;
	protected int suitor; //ID of suitor
	
	protected int ID;
	protected int xloc;
	protected int yloc;
	protected int job;
	protected int[] assets;
	protected int[] inventory; //OBSOLETE
	private short specialweapon;
	protected int expIncome;
	//protected Clan Boss;
	protected Order order;
	protected int myB;
	protected int minionB;
	protected int subminionB;
	protected int minionN;
	protected int subminionN;
	protected int pointsBN;
	protected int numSerfs;
	//serfs farm, mine, cut trees, and build
	//serfs revolt against master if not fed
	//in case of revolt, master replaced with SERF CREED, all other serfs become family
	
	protected boolean lastSuccess;
	//protected int act;
	protected int profitEMA;
	protected int[][] expTerms; //expected terms (for job)
	public Ideology FB;
	public Questy QB;
	public Amygdala AB;
	public Memory MB;
	protected int lastBeh, lastBehN;
	protected Book goblog = new Book();
	
	public Clan() {}
	public Clan(Shire s, int id) {
		this(s.getX(), s.getY(), id);
	}
	public Clan(int x, int y, int id) {
		xloc = x;
		yloc = y;
		ID = id;
//		Boss = this;
		suitor = ID;
		assets = new int[numAssets];
		assets[millet] = 1000;
//		inventory = recalcInv();
//		expTerms = new int[][] {{0}, {}, {}, {}};
		job = FARMER;  setRandomJob();
		profitEMA = 0;
		setAge(AGPmain.rand.nextInt(100));
		specialweapon = (short) AGPmain.rand.nextInt(); //XWeapon.NULL;
		
		byte[] r = new byte[2];
		AGPmain.rand.nextBytes(r);
		name[0] = r[0]; name[1] = r[1];
		gender = AGPmain.rand.nextBoolean();
		FB = new Ideology(this);
		QB = new Questy(this);
		AB = new Amygdala(this);
		MB = new Memory(this);
	}
	

	public void pursue() {
		if (MB.QuestStack.empty()) {
			Q_ q = FB.randomValueInPriority().pursuit(this);
			Quest quest = Quest.QtoQuest(this, q);
			MB.QuestStack.add(quest);
		}
		MB.QuestStack.peek().pursue();
	}
	
	public int getID() {return ID;}
	public int getXloc() {return xloc;}
	public int getYloc() {return yloc;}
	public int getShireXY() {return xloc + yloc * AGPmain.getShiresX();}
	public Shire myShire() {return AGPmain.TheRealm.shires[xloc + yloc * AGPmain.getShiresX()];}
	public MktAbstract myMkt(int g) {return myShire().getMarket(g);}
	public int getAge() {return age;}
	public void setAge(int a) {age = a;}
	public boolean getGender() {return gender;}
	public void setGender(boolean g) {gender = g;}

	public int getNumGoods() {return Goods.numGoods;}
	public Clan getSuitor() {return AGPmain.TheRealm.getClan(suitor);}
	public void setSuitor(Clan C) {suitor = C.getID();}
	public byte[] getNameBytes() {return name;}
	public String getFirstName() {return GobName.firstName(name[0], name[1], gender);}
	public String getNomen() {return GobName.fullName(this);}
	public String getSancName() {return FB.getDeusName();}
	public Job getJob() {return AGPmain.TheRealm.getJob(job);}
	public int getJobInt() {return job;}
	public void setJob(int j) {job = j;}
	public int[][] getExpTerms() {return expTerms;}
	public void setExpTerms(int[][] ET) {expTerms = ET;}
	
	public void breed(Clan mate) {numSpawns++;}
	
	public Clan getBoss() {return FB.getRex();}
	public Clan getTopBoss() { //i think broken
		if (getBoss() == null) {Calc.p(""+1/0); return this;}
		if (this == getBoss()) {return this;}
		else {return getBoss().getTopBoss();}
	}
	public boolean isSomeBossOf(Clan him) {return isSomeBossOf(him, this);}  //false if self!
	private boolean isSomeBossOf(Clan him, Clan orig) {
		Clan hisBoss = him.getBoss();
		if (hisBoss == orig) {return true;}
		else if (hisBoss == null) {Calc.p(""+1/0); return false;}
		else if (hisBoss == him) {return false;}
		else {return isSomeBossOf(hisBoss, orig);}
	}
	public int getMinionNumber() {return minionN + subminionN;}
	public int getMinionPoints() {return minionB + subminionB;}
	public int getPointsBN() {return pointsBN;}
	public Order myOrder() {return order;}
	public void setOrder(Order o) {order = o;}
	public void joinOrder(Order newOrder) {
		if (newOrder == null) {Calc.p(""+1/0); return;}
		if (order == null) {newOrder.addMember(this);}
		else {order.moveTo(this, newOrder);}
	}
	public boolean join(Clan newBoss) {
		if (this.isSomeBossOf(newBoss)) {return false;}  //forget it if im already above him
		Clan oldBoss = this.getBoss();
		if (oldBoss != this) {oldBoss.chgMinionN(-1 -subminionN);}
		this.FB.setDisc(LORD, newBoss.FB.getDisc(LORD));
		if (newBoss != this) {newBoss.chgMinionN(1 + subminionN);}
		joinOrder(newBoss.myOrder());
		return true;
	}
	private void chgMinionN(int n) {
		minionN += Math.signum(n);   chgSubMinionN(n);
	}
	private void chgSubMinionN(int n) {
		subminionN += n;
		Clan Boss = getBoss();
		if (Boss != this) {Boss.chgSubMinionN(n);}
	}
	public void chgMyB(int b) {
		myB += b; ///WHAT IS B???  soldiers vanquished? people converted?
		Clan Boss = getBoss(); int id = Boss.getID();
		if (id != -1 && id != getID()) {Boss.chgMinionB(b);}
	}
	private void chgMinionB(int b) {
		minionB += b;   chgSubMinionB(b);
	}
	private void chgSubMinionB(int b) {
		subminionB += b;
		Clan Boss = getBoss(); int id = Boss.getID();
		if (id != -1 && id != getID()) {Boss.chgSubMinionB(b);}
	}
	private int getExpIncome() {return expIncome;}
//	public int calcPoints(Clan hypoBoss) {
//		Clan hypoTopBoss = hypoBoss.getTopBoss();
//		double P = (double)hypoTopBoss.useBeh(M_.PYRAMIDALITY)/15;
//		double L = (double)hypoTopBoss.useBeh(M_.LEADERSHIP)/15;
//		double M = (double)hypoTopBoss.useBeh(M_.MERITOCRACITY)/15;
//		double adjB = L*(P*subminionB + (1.0-P)*minionB) + (1.0-L)*myB;
//		double adjN = L*(P*subminionN + (1.0-P)*minionN) + (1.0-L)*1;
//		return (int) (Math.pow(adjB, M) * Math.pow(adjN, 1.0-M));
//	}
//	public int estimateWinnings(Clan hypoBoss) {
//		Clan hypoTopBoss = hypoBoss.getTopBoss();
//		int Winnings = hypoTopBoss.getExpIncome();
//		int TotalPoints = hypoTopBoss.getPointsBN();
//		int pointsBN = calcPoints(hypoTopBoss);
//		return Winnings * pointsBN / TotalPoints;
//	}

	
	public int getMillet() {return assets[millet];}
	public boolean alterMillet(int c) {
		if ((long) assets[millet] + c > Integer.MAX_VALUE) {assets[millet] = Integer.MAX_VALUE; System.out.println("max millet reached " + ID);}
		else if (assets[millet] + c < 0) {assets[millet] = 0; System.out.println("millet below zero " + ID + "job" + getJob() + getLastSuccess() + " $" + assets[millet] + "" + c); return false;}//System.out.println(1 / 0);}
		else {assets[millet] = assets[millet] + c; return true;}
		return false;
	}
	
	private void setRandomJob() {   //just for sample purposes!
		int n = AGPmain.rand.nextInt(12);
		switch (n) {
		case 0: job = HUNTERGATHERER; break;
		case 1: job = HUNTERGATHERER; break;
		case 2: job = HERDER; break;
		case 3: job = MINER; break;
		case 4: job = MASON; break;
		default: job = FARMER; break;
		}
		
	}
	
	
	//public void setAct(int a) {act = a;}
	//public int getAct() {return act;}
	public Act[] getJobActs() {return AGPmain.TheRealm.getJob(job).getActs();}
	public void setLastSuccess(boolean b) {lastSuccess = b;}
	public boolean getLastSuccess() {return lastSuccess;}
	public boolean isHungry() {return true;}
	public int getProfitEMA() {return profitEMA;}
	public void alterProfitEMA(int m) {
		int p = 50; // =myCreed().continuouses[ADAPTSPEED];
		profitEMA = (p * m + (100 - p) * profitEMA) / 100;
	}
	
	
	public int[] getAssets() {return assets;}
	public int getAssets(int g) {return assets[g];}
	public void incAssets(int p, int x) {if (x < 0) {System.out.println("error incAssets x is negative");}
		assets[p] += x;
	}
	public void decAssets(int p, int x) {
		if (assets[p] - x < 0) {
			System.out.println(Naming.goodName(p, false, false) + " error negative assets in decAssets for " + getNomen());
		}
		else {assets[p] -= x;}
	}
	public short getXWeapon() {return specialweapon;}
	public void setXWeapon(short w) {specialweapon = w;}
	public int[] recalcInv() {
		int count = 0; int k = 0;
		for (int i = 0; i < assets.length; i++) {if (assets[i] > 0) {count++;}}
		int[] INV = new int[count]; 
		for (int i = 0; i < assets.length; i++) {if (assets[i] > 0) {INV[k++] = i;}}
		return INV;
	}

	public boolean maybeEmigrate() {
		emigrate();
		return true;
	}
	private void emigrate() {}
	
	
	
	//OBSOLETE
	public int Buy(int good, int num) {
		int sum = 0;
		for (int i = 0; i < num; i++) {sum += Buy1(good);}
		// returns number bought successfully
		return sum;
	}
	public int Sell(int good, int num) {
		int sum = 0;
		for (int i = 0; i < num; i++) {sum += Sell1(good);}
		// returns number sold successfully
		return sum;
	}
	public void buyFail() {}
	public void sellFail() {}
	public int Buy1(int good) {return 0;}
	public int Sell1(int good) {return 0;}
	//end OBSOLETE


    public void eat() {
    	assets[Goods.millet] -= DMC;
    	if (assets[Goods.millet] < 0) {
    		assets[Goods.millet] = 0;
    		//starvation!
    	}
    }
    
    
    
    public void displayProfile() {

    }

    public double getCourage() {  // range 0-1
    	return (useBeh(M_.CONFIDENCE) + 30 - useBeh(M_.MIERTE) - useBeh(M_.PARANOIA)) / 45;
    }
    public int confuse(int in) {
    	//returns number between 50%-150% of original number at min arithmetic + max madness
    	int x = Math.abs((16 - FB.getPrs(P_.ARITHMETICP) + useBeh(M_.MADNESS)) * in / 64);
    	return in + (x == 0 ? 0 : - x + AGPmain.rand.nextInt(x * 2));
    }
	public boolean iHigherMem(int m, Clan other) {
		return iHigherPrest(mem(m).getPrestige(), other);
	}
    
	public boolean iHigherPrest(P_ p, Clan other) {
		boolean iHigherSanc = (FB.compareSanc(other) >= 0);
		if (p == P_.SANCP) {
			return iHigherSanc;
		}
		else {
			int adj = useBeh(M_.SUPERST);
			return (FB.getPrs(p) + (iHigherSanc?adj:-adj) - other.FB.getPrs(p) >= 0 ? true : false);
		}
	}
	
	public void prch(Clan patron, Clan other) {
		int[] euR = patron.FB.getSancRanks();
		int[] eleR = other.FB.getSancRanks();
		int max = 0; int k = 0; int cur = 0;
		for (int i = euR.length-1; i>=0; i--) {
			cur = Math.abs(eleR[i] - euR[i]);
			if (cur >= max) {
				max = cur; k = i;
			}
		}
		cur = eleR[k] - euR[k];
		if (cur > 0) {glorf(k, other);}
		else if (cur < 0) {dnounce(k, other);}
	}
	public void raiseSanc(int s, Clan other) {
		switch (s) {
			case RX : other.FB.setDisc(LORD, FB.getDisc(LORD)); break;
			case DE : other.FB.setDisc(CREED, FB.getDisc(CREED)); break;
			case HL : int hl = FB.getDisc(HOMELAND);
				if(hl == other.getShireXY()) {other.FB.setDisc(HOMELAND, FB.getDisc(HOMELAND));} break;
			case JB : other.FB.setDisc(ASPIRATION, FB.getDisc(ASPIRATION)); break;
			default : break;
		}
		other.FB.upSanc(s);
	}
	private void glorf(int s, Clan other) {
		if (!other.iHigherPrest(P_.SANCP, this)) {
			raiseSanc(s, other);   FB.upPrest(P_.PREACHP);
		}   else {FB.downPrest(P_.PREACHP);}
	}
	private void dnounce(int s, Clan other) {
		if (!other.iHigherPrest(P_.SANCP, this)) {
			other.FB.downSanc(s);   FB.upPrest(P_.PREACHP);
		}   else {FB.downPrest(P_.PREACHP);}
	}
	public void compSanc(boolean iHiSanc, Clan other) {
		if (iHiSanc) {
			FB.upPrest(P_.CONFP);
			other.FB.downPrest(P_.RSPCP);
		}
		else {
			FB.downPrest(P_.CONFP);
			other.FB.upPrest(P_.RSPCP);
		}
	}
	public void discourse(Clan other) {
		boolean iHi = iHigherMem(lastBeh, other);
		if (lastBeh == 0) {
			compSanc(iHi, other);
			other.compSanc(iHi, this);
		}
		else {
			if (iHi) {other.FB.setBeh(lastBeh, FB.getBeh(lastBeh));}
			else {FB.setBeh(lastBeh, other.FB.getBeh(lastBeh));}
		}
		lastBehN = 0;
	}
	
	
	public int useBeh(M_ m) {
		if (Math.random() < (double) 1 / ++lastBehN) {
			lastBeh = m.ordinal();
		}
		return FB.getBeh(m);
	}
	public Mem mem(int m) {return AGPmain.TheRealm.getMem(m);}
	
	public void addReport(Reportable R) {goblog.addReport(R);}
	public Reportable[] getLog() {return goblog.getBook();}
	
}
