package Game;

import Sentiens.Clan;
import Sentiens.Mem;
import Sentiens.Values;
import Sentiens.Values.Value;
//import Sentiens.Sanc;
import Shirage.Shire;
import AMath.Calc;

public class Realm {
	int shiresX;
	int shiresY;
	int startPop;
	public Shire[] shires;
	private Clan[] population;
	private int day;
	private Mem[] MemDefs;
//	private Sanc[] SancDefs;
	private Value[] ValueDefs;
	private Job[] JobDefs;
	private VarGetter[] PopVarGetters;
	private VarGetter[] MktVarGetters;
	
	//private int[][] jobs;
	private Clan Avatar;

	public Realm(int pX, int pY, int cN) {
		shiresX = pX;
		shiresY = pY;
		startPop = cN;
	}
	public static Realm makeRealm(int pX, int pY, int cN) {
		Realm newRealm = new Realm(pX, pY, cN);
		newRealm.generateShires(pX,pY);
		newRealm.generatePopulation(cN);
		//shdata = new ShireData[shires.length];
		//for (int s = 0; s < shdata.length; s++) {
		//	shdata[s] = new ShireData();
		//}
		newRealm.Avatar = newRealm.population[0];
		return newRealm;
	}
	public void setupDefs() {
		MemDefs = Mem.MemDefs();
//		SancDefs = Sanc.SancDefs();
		JobDefs = Job.JobDefs();
		PopVarGetters = VarGetter.popVGs();
		MktVarGetters = VarGetter.mktVGs();
	}
	public void goOnce() {
		int[] order = Calc.randomOrder(popSize());
		for (int p : order) {
			population[p].QB.pursue();
		}
	}
	public void go() {
		day = 0;
		
		if(true) {
			for (int t = 0; t < 1000; t++) { //roll through turns
				System.out.println("day " + t);
				
				for (int s = 0; s < shires.length; s++) {
					shires[s].newDay();
				}
				int[] order = Calc.randomOrder(popSize());
//				for (int p : order) {
//					//population[p].chooseLaborLeisure();//includes doing act
//				}
				for (int p : order) {
					population[p].eat();
				}
				
				//AGPmain.refreshGUI();
				day++;
			}
		}

	}
	
	private void generatePopulation(int C) {
		population = new Clan[C];
		for (int i = 0; i < C; i++) {
			Shire candidateHL = shires[AGPmain.rand.nextInt(shiresX * shiresY)];;
			while (candidateHL != null && candidateHL.isPopulateable()) {
				candidateHL = shires[AGPmain.rand.nextInt(shiresX * shiresY)];
			}
			population[i] = new Clan(candidateHL, i);
			//population[i] = new Clan(z % shiresX, (int)(z/shiresX), i);
		}
	}
	
	
	private void generateShires(int H, int V) {
		shires = new Shire[H*V];
		for (int x = 0; x < H; x++) {
			for (int y = 0; y < V; y++) {
				shires[x + y*H] = new Shire(x, y);
				shires[x + y*H].linkToPlot(AGPmain.mainGUI.MD.getPlotXY(x*3 + 1, y*3 + 1));
			}
		}
	}
	
	public void doCensus() {
		for (int s = shires.length - 1; s >= 0; s--) {
			shires[s].resetPopSize();
		}
		for (int i = popSize() - 1; i >= 0; i--) {
			population[i].myShire().incPopSize();
		}
		for (int s = shires.length - 1; s >= 0; s--) {
			shires[s].setupCensus();
		}
		Clan curClan;
		for (int i = popSize() - 1; i >= 0; i--) {
			curClan = population[i];
			curClan.myShire().addToCensus(curClan);
		}
	}

	public Clan getAvatar() {return Avatar;}
	public void setAvatar(Clan C) {Avatar = C;}
	public int getNumShires() {return shiresX*shiresY;}
	public Shire getShire(int x, int y) {return shires[x + y*shiresX];}
	public Shire getShire(int xy) {return shires[xy];}
	public Shire[] getShires() {return shires;}
	public Clan getClan(int ID) {return population[ID];}
	public Clan getRandClan() {return population[Calc.randBetween(0, popSize())];}
	public Clan[] getPopulation() {return population;}
	public int popSize() {return population.length;}
	public int getDay() {return day;}
	
	//public ProductDefs getProductDefs() {return products;}
	public Mem getMem(int m) {return MemDefs[m];}
//	public Value getValue(int v) {return Values.All[v];} //{return ValueDefs[v];}
//	public Value[] getValues() {return Values.All;} //{return ValueDefs;}
//	public Sanc getSanc(int s) {return SancDefs[s];}
	public Job getJob(int j) {return JobDefs[j];}
	public VarGetter[] getPopVarGetters() {return PopVarGetters;}
	public VarGetter getPopVarGetter(int i) {return PopVarGetters[i];}
	public VarGetter[] getMktVarGetters() {return MktVarGetters;}
	public VarGetter getMktVarGetter(int i) {return MktVarGetters[i];}

	
	

}