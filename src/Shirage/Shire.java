package Shirage;
import java.util.*;

import AMath.Calc;
import Descriptions.Naming;
import Game.*;
import Markets.*;
import Sentiens.*;

public class Shire extends AbstractShire implements Stressor.Causable {
	
	public static byte varMin = 1;
	public static byte varMax = 127;
	public static byte numVars = 40;
	public static byte numRecs = 40;
	public static byte numConds = 40;
	
	//VARS
	public static byte numF = 0;
	public static byte numB = 1;
	public static byte numD = 2;
	public static byte numBf = 3; //free range
	public static byte numDf = 4; //free range
	public static byte numT = 5;
	public static byte numMi = 6;
	public static byte numMs = 7;
	public static byte numQ = 8;
	public static byte weatherVar = 9;
	public static byte baseCapacityF = 10;
	public static byte capacityF = 11;
	public static byte capacityS1 = 12;
	public static byte productivityMi = 13;
	public static byte productivityMs = 14;
	public static byte productivityQ = 15;
	public static byte growthB = 16;
	public static byte growthD = 17;
	public static byte growthT = 18;
	public static byte productivityF = 19;
	//

	private Collection<Clan> census = new ArrayList<Clan>();
	private Plot linkedPlot;
	private Clan governor;
	
	private byte[] lastvars = new byte[numVars];
	private byte[] vars = new byte[numVars];
	
	private int[] resources = new int[numRecs];
	private int[] conditions = new int[numConds];
	public static final int MAXANIMALS = 1000; //at the end of the day, sum number of animals and randomly slay ones over limit
	public static final int MAXMINERALS = 10000;
	public static final int MAXTREES = 5000;
	public static final int MAXSPACE = 1000;
	public static final int WILDERNESS = 0; //space
	public static final int FERTILITY = 1;
	public static final int TREES = 2;
	public static final int WBOVADS = 10;
	public static final int WDONKEYS = 11;
	public static final int WYAKS = 12;
	public static final int WHORSES = 13;
	public static final int STONES = 4;
	public static final int IORE = 5;
	public static final int SORE = 6;
	public static final int WEATHERVOL = 10;

	private MktAbstract[] markets;
	private Library library = new Library();
	
	public Shire(int x, int y) {
		super(x, y);
		markets = generateMkts();
		

		initializeVars();
	}

	public void newDay() {
//		conditions[FERTILITY] = randWeather(conditions[FERTILITY]);
		//?change to seasonal?
		
//		System.arraycopy(vars, 0, lastvars, 0, numVars);

		newMarketDay();
	}
	
	public void newMarketDay() {for (MktAbstract mkt : markets) {mkt.newDay();}}
	
	public void newSeason() {
		capacityF = (byte) Math.max(0, baseCapacityF + AGPmain.rand.nextInt((int) weatherVar));
		vars[numB] += (vars[numB] * growthB) / 100;
		
		System.arraycopy(vars, 0, lastvars, 0, numVars);
		
		//resources[WBOVADS] += Calc.iPercent(10);
		//resources[WDONKEYS] += Calc.iPercent(10);
		//resources[WYAKS] += Calc.iPercent(5);
		//resources[WHORSES] += Calc.iPercent(10);
		//resources[TREES] += Calc.iPercent(70);
		conditions[FERTILITY] = randWeather(conditions[FERTILITY]);

		System.out.println("F:"+conditions[FERTILITY]);
	}

	public int randWeather(int orig) {
		int chg = AGPmain.rand.nextInt(2*conditions[WEATHERVOL]+1) - conditions[WEATHERVOL];
		return Math.max(0, Math.min(99, orig + chg));
	}



	public MktAbstract[] generateMkts() {
		MktAbstract[] M = new MktAbstract[Defs.numGoods];
		for (int i = 0; i < M.length; i++) {
			if(i != Defs.millet && i != Defs.rentland && i != Defs.rentanimal) {M[i] = new MktO(i, this);}
		}
		M[Defs.millet] = new FoodMarket(Defs.millet, this);
		M[Defs.rentland] = new RentMarket(Defs.rentland, this);
		M[Defs.rentanimal] = new RentMarket(Defs.rentanimal, this);
		return M;
	}


	public void initializeVars() {
		byte[] r = new byte[numVars];
		AGPmain.rand.nextBytes(r);
		for (int v = 0; v < numVars; v++) {
			vars[v] = Calc.squeezeByte(r[v], varMin, varMax);
		}
		vars[numB] = vars[numBf]; vars[numD] = vars[numDf];
		vars[numF] = (byte) Math.min(vars[numF], vars[capacityF]);
		
		randomizeVars();
	}
	
	public void randomizeVars() {
		resources[WBOVADS] = Calc.randBetween(0, 200);
		resources[WDONKEYS] = Calc.randBetween(0, 100);
		resources[WYAKS] = Calc.randBetween(0, 50);
		resources[WHORSES] = Calc.randBetween(0, 200);
		resources[STONES] = Calc.randBetween(0, 100000);
		resources[IORE] = Calc.randBetween(0, 10000);
		resources[SORE] = Calc.randBetween(0, 5000);
		resources[TREES] = Calc.randBetween(0, 100000);
		resources[WILDERNESS] = MAXSPACE;
		
		conditions[WBOVADS] = Calc.randBetween(1, 4);
		conditions[WDONKEYS] = Calc.randBetween(1, 4);
		conditions[WYAKS] = Calc.randBetween(1, 4);
		conditions[WHORSES] = Calc.randBetween(1, 4);
		conditions[STONES] = resources[STONES] / 1000;
		conditions[IORE] = resources[IORE] / 100;
		conditions[SORE] = resources[SORE] / 50;
		conditions[TREES] = Calc.randBetween(1, 4);
		conditions[WILDERNESS] = resources[WILDERNESS] / MAXSPACE;
		conditions[FERTILITY] = Calc.randBetween(0, 100);
		conditions[WEATHERVOL] = Calc.randBetween(1, 4);
	}
	
	
	public int getX() {return xcoor;}
	public int getY() {return ycoor;}
	private static int getID(int x, int y) {return x + y * AGPmain.getShiresX();}
	public int getID() {return getID(xcoor, ycoor);}
	public int distanceFrom(Shire S) {
		if (S == this) {return 0;}
		//should ideally count how many steps to get to S, but for now...
		return (int) Math.round(Math.sqrt(Math.pow(S.getX() - this.getX(), 2) + Math.pow(S.getY() - this.getY(), 2)));
	}
	public void setLinkedPlot(Plot p) {linkedPlot = p;}
	public void linkToPlot(Plot p) {
		setLinkedPlot(p);
		p.linkHoodToShire(this);
	}
	public boolean isPopulateable() {
		if (linkedPlot == null) {return false;}
		return !(linkedPlot.isOcean() || linkedPlot.isNull());
	}
	public void addToCensus(Clan c) {census.add(c);}
	public int getPopsize() {return census.size();}
	public Collection<Clan> getCensus() {return census;}
	public Clan getCensus(int i) {return ((ArrayList<Clan>)census).get(i);}
	public Clan getRandOfCensus() {return ((ArrayList<Clan>)census).get(AGPmain.rand.nextInt(census.size()));}
	
	public static String getName(int x, int y) {return Naming.randShireName(getID(x, y));}
	public String getName() {return Naming.randShireName(getID());}
	public MktAbstract getMarket(int g) {return markets[g];}
	public Library getLibrary() {return library;}
	public int getResource(int r) {
		if (r >= 0) {return resources[r];}
		else {return Integer.MAX_VALUE;}
	}
	public int getCondition(int c) {
		switch (c) {
			case IORE:
			case SORE: return 100 * resources[c] / MAXMINERALS;
			case STONES: return Math.min(100, 100 * resources[c] / MAXMINERALS);
			case TREES: return 100 * resources[c] / MAXTREES;
			case WILDERNESS: return 100 * resources[c] / MAXSPACE;
			default: return conditions[c];
		}
	}
	public boolean decResource(int r) {
		if (r >= 0) {
			if (resources[r] > 0) {resources[r]--; return true;}
			else {return false;}
		}
		else {return true;}
	}
	
	
	public void incVar(byte v, byte x) {vars[v] = Calc.byteUp(vars[v], x);}
	public void decVar(byte v, byte x) {vars[v] = Calc.byteDown(vars[v], x);}
	public void setVar(byte v, byte x) {vars[v] = x;}
	public int[] getBidOffer(int g) {
		int[] BO = {markets[g].bestBid(), markets[g].bestOffer()};
		return BO;
	}
	public byte[] getVars() {return vars;}
	public byte[] getLastVars() {return lastvars;}
	public byte getLastVar(int v) {return lastvars[v];}
	public byte getGrowthB() {return growthB;}
	public byte getGrowthD() {return growthD;}
	public byte getGrowthT() {return growthT;}
	


	public Plot getLinkedPlot() {return linkedPlot;}

	public Clan getGovernor() {return governor;}

	public void setGovernor(Clan clan) {this.governor = clan;}
	
	
}

