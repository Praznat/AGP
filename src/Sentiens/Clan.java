package Sentiens;

import java.util.*;

import AMath.Calc;
import Defs.*;
import Descriptions.*;
import Game.*;
import Government.Order;
import Markets.MktAbstract;
import Questing.Quest;
import Sentiens.GobLog.Book;
import Sentiens.GobLog.Reportable;
import Shirage.Shire;

public class Clan implements Defs, Stressor.Causable {
	protected static final int DMC = 10; //daily millet consumption
	//protected static final int MEMORY = 8;
	//bio
	protected byte[] name = new byte[2];
	protected boolean gender;
	private int age;

	protected int numSpawns;
	protected int suitor; //ID of suitor
	
	protected int ID;
	protected Shire currentShire;
	protected Job job, aspiration, backupJob;
	protected int[] assets;
	protected int[] inventory; //OBSOLETE
	private short specialweapon;
	protected int expIncome;
	protected Clan boss;
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
	
	private boolean active;
	protected boolean lastSuccess;
	//protected int act;
	protected int profitEMA;
	public Ideology FB;
	public Questy QB;
	public Amygdala AB;
	public Memory MB;
	protected int lastBeh, lastBehN;
	protected Book goblog = new Book();
	protected Collection<DeathListener> deathListeners = new ArrayList<DeathListener>();
	
	public Clan() {}
	public Clan(Shire place, int id) {
		currentShire = place;
		ID = id;
		boss = this;
		suitor = ID;
		assets = new int[numAssets];
		assets[millet] = 1000;
//		inventory = recalcInv();
//		expTerms = new int[][] {{0}, {}, {}, {}};
		backupJob = Job.HUNTERGATHERER;
		job = Job.FARMER;  setRandomJob();
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
		MB = new Memory();
	}
	

	public void pursue() {
		if (isActive()) {
			if (MB.QuestStack.empty()) {
				Q_ q = FB.randomValueInPriority().pursuit(this);
				Quest quest = Quest.QtoQuest(this, q);
				MB.QuestStack.add(quest);
			}
			MB.QuestStack.peek().pursue();
		}
		setActive(true);
	}
	
	public void die() {
		// stuff happens
		for (DeathListener dl : deathListeners) {dl.onDeathOf(this);}
		// remove from populations
	}
	
	public int getID() {return ID;}
	public int getShireID() {return myShire().getX() + myShire().getY() * AGPmain.getShiresX();}
	public Shire myShire() {return currentShire;}
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
	public Job getJob() {return job;}
	public void setJob(Job j) {backupJob = job; job = j;}
	public Job getAspiration() {return aspiration;}
	public void setAspiration(Job j) {aspiration = j;}
	
	public void breed(Clan mate) {numSpawns++;}
	
	public Clan getBoss() {return boss;}
	public Clan getTopBoss() { //i think broken
		if (getBoss() == null) {Calc.p(""+1/0); return this;}
		if (this == getBoss()) {return this;}
		else {return getBoss().getTopBoss();}
	}
	public boolean isDirectBossOf(Clan him) {
		final Clan hisBoss = him.getBoss();
		return hisBoss != him && this == hisBoss;
	}
	public boolean isSomeBossOf(Clan him) {return isSomeBossOf(him, this);}  //false if self!
	private boolean isSomeBossOf(Clan him, Clan orig) {
		final Clan hisBoss = him.getBoss();
		if (hisBoss == orig) {return true;}
		else if (hisBoss == null) {Calc.p(""+1/0); return false;}
		else if (hisBoss == him) {return false;}
		else {return isSomeBossOf(hisBoss, orig);}
	}
	public int distanceFromTopBoss() {
		Clan boss = this; int k = 1;
		while (boss != boss.getBoss()) {
			boss = boss.getBoss(); k++;
		}	return k;
	}
	public int getMinionTotal() {return minionN + subminionN;}
	public int getMinionN() {return minionN;}
	public int getSubminionN() {return subminionN;}
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
		if (oldBoss != this) {oldBoss.removeMinion(this);}
		this.boss = newBoss;
		if (newBoss != this) {newBoss.addMinion(this);}
		if(newBoss.myOrder() == null) {Order.createBy(newBoss);}
		joinOrder(newBoss.myOrder());
		return true;
	}
	private void addMinion(Clan minion) {
		minionN++;   chgSubMinionN(1 + minion.getMinionTotal(), true);
	}
	private void removeMinion(Clan minion) {
		minionN--;   chgSubMinionN(-1 - minion.getMinionTotal(), true);
	}
	private void chgSubMinionN(int n, boolean first) {
		subminionN += n - (first ? Math.signum(n) : 0);
//		n += (first && n != 0 ? (n > 0 ? 1 : -1) : 0);
		Clan Boss = getBoss();
		if (Boss != this) {Boss.chgSubMinionN(n, false);}
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
		else if (assets[millet] + c < 0) {
			assets[millet] = 0; System.out.println("millet below zero " + getNomen() + " the " + getJob() + " $" + assets[millet] + " altered by " + c);
			throw new IllegalStateException(); // return false;
		}//System.out.println(1 / 0);}
		else {assets[millet] = assets[millet] + c; return true;}
		return false;
	}
	
	private void setRandomJob() {   //just for sample purposes!
		int n = AGPmain.rand.nextInt(12);
		switch (n) {
		case 0: job = Job.HUNTERGATHERER; break;
		case 1: job = Job.HUNTERGATHERER; break;
		case 2: job = Job.HERDER; break;
		case 3: job = Job.MINER; break;
		case 4: job = Job.MASON; break;
		default: job = Job.FARMER; break;
		}
		
	}
	
	public Act[] getJobActs() {return job.getActs();}
	public void setLastSuccess(boolean b) {lastSuccess = b;}
	public boolean getLastSuccess() {return lastSuccess;}
	public boolean isActive() {return active;}
	public void setActive(boolean a) {active = a;}
	public boolean isHungry() {return true;}
	public int getProfitEMA() {return profitEMA;}
	public void alterProfitEMA(double newProfit) {	//should do weekly recalc of change in NAV or something, not handle through work quest (cuz working just puts out offers)
		double speed = ((double)FB.getBeh(M_.LTMOMENTUM) + FB.getBeh(M_.STMOMENTUM) + 1) / 32; //
		profitEMA = (int) Math.round(speed * newProfit + (1 - speed) * profitEMA);
	}
	

	public long getNetAssetValue(Clan POV) {
		int sum = 0;   for (int g = 1; g < Defs.numAssets; g++) {
			int px = g != Defs.millet ? POV.myMkt(g).sellablePX(POV) : 1;
			sum += getAssets(g) * px;
		}	return sum;
	}
	public int[] getAssets() {return assets;}
	public int getAssets(int g) {return assets[g];}
	public void incAssets(int g, int x) {if (x < 0) {System.out.println("error incAssets x is negative");}
		assets[g] += x;
	}
	public void decAssets(int g, int x) {
		if (assets[g] - x < 0) {
			System.out.println(Naming.goodName(g, false, false) + " error negative assets in decAssets for " + getNomen());
		}
		else {assets[g] -= x;}
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


    public boolean eat() {
    	assets[Defs.millet] -= DMC;
    	if (assets[Defs.millet] < 0) {
    		assets[Defs.millet] = 0;
    		return false;
    		//starvation!
    	}	return true;
    }
    public boolean eatMeat() {
    	assets[Defs.meat]--;
    	if (assets[Defs.meat] < 0) {
    		assets[Defs.meat] = 0;
    		return false;
    	}	return true;
    }
    
    
    
    public void displayProfile() {

    }

    public double getCourage() {  // range 0-1
    	return (useBeh(M_.CONFIDENCE) + 30 - useBeh(M_.MIERTE) - useBeh(M_.PARANOIA)) / 45;
    }
    public double confuse(double in) {
    	//returns number between 50%-150% of original number at min arithmetic + max madness
    	double x = Math.abs((16 - FB.getPrs(P_.ARITHMETIC) + useBeh(M_.MADNESS)) * in / 64);
    	return in + (x == 0 ? 0 : - x + AGPmain.rand.nextDouble()*x*2);
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
//	public void raiseSanc(int s, Clan other) {
//		switch (s) {
//			case RX : other.FB.setDisc(LORD, FB.getDisc(LORD)); break;
//			case DE : other.FB.setDisc(CREED, FB.getDisc(CREED)); break;
//			case HL : int hl = FB.getDisc(HOMELAND);
//				if(hl == other.getShireXY()) {other.FB.setDisc(HOMELAND, FB.getDisc(HOMELAND));} break;
//			case JB : other.FB.setDisc(ASPIRATION, FB.getDisc(ASPIRATION)); break;
//			default : break;
//		}
//		other.FB.upSanc(s);
//	}
	private void glorf(int s, Clan other) {
		if (!other.iHigherPrest(P_.SANCP, this)) {
//			raiseSanc(s, other);   FB.upPrest(P_.PREACHP);
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
	@Deprecated
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
	public double conversation(Clan other) {
		if (this == other) {return 0;}
		double respect = FB.randomValueInPriority().compare(this, other, this);
		if (respect > 0) {FB.setBeh(lastBeh, other.FB.getBeh(lastBeh)); lastBehN = 0;}
		return respect;
	}
	
	
	public int useBeh(M_ m) {
		if (AGPmain.rand.nextDouble() < (double) 1 / ++lastBehN) {
			lastBeh = m.ordinal();
		}
		return FB.getBeh(m);
	}
	public Mem mem(int m) {return AGPmain.TheRealm.getMem(m);}
	
	public void addReport(Reportable R) {goblog.addReport(R);}
	public Reportable[] getLog() {return goblog.getBook();}
	
	@Override
	public String toString() {return getNomen();}
}
