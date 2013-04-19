package Game;

import java.util.*;

import AMath.Calc;
import Sentiens.*;
import Sentiens.Values.Value;
import Shirage.*;

public class Realm {
	int shiresX;
	int shiresY;
	int startPop;
	public Shire[] shires;
	private Clan[] population;
	private List<Clan> waitingForImmigration = new ArrayList<Clan>();
	private int day;
	private Mem[] MemDefs;
	//	private Sanc[] SancDefs;
	private Value[] ValueDefs;
	private VarGetter[] PopVarGetters;
	private VarGetter[] MktVarGetters;

	//private int[][] jobs;
//	private Clan Avatar;

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
//		newRealm.Avatar = newRealm.population[0];
		return newRealm;
	}
	public void setupDefs() {
		MemDefs = Mem.MemDefs();
		//		SancDefs = Sanc.SancDefs();
		PopVarGetters = VarGetter.popVGs();
		MktVarGetters = VarGetter.mktVGs();
	}
	public void goOnce() {
		day++;
		int[] order = Calc.randomOrder(popSize());
		for (int i = 0; i < 1; i++) {
			for (int p : order) {
				population[p].pursue();
			}
			for (Shire s : shires) {
				s.newDay();
			}
		}
		setNewImmigrations();
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
			while (candidateHL == null || !candidateHL.isPopulateable()) {
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
				if (AGPmain.mainGUI!=null && AGPmain.mainGUI.MD!=null) {
					shires[x + y*H].linkToPlot(AGPmain.mainGUI.MD.getPlotXY(x*2 + (y%2), y*2 + 1));
				}	else {
					Plot p = new Plot(0.5); p.makeLand();
					shires[x + y*H].setLinkedPlot(p);
				}
			}
		}
	}

	public void doCensus() {
		Clan curClan;
		for (int i = popSize() - 1; i >= 0; i--) {
			curClan = population[i];
			curClan.myShire().addToCensus(curClan);
		}
	}
	
	public void addToWaitingForImmigration(Clan c) {
		waitingForImmigration.add(c);
	}
	public void setNewImmigrations() {
		for (Clan clan : waitingForImmigration) {
			//TODO add to shire, remove from old shire
		}
		waitingForImmigration.clear();
	}

//	public Clan getAvatar() {return Avatar;}
//	public void setAvatar(Clan C) {Avatar = C;}
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
	public VarGetter[] getPopVarGetters() {return PopVarGetters;}
	public VarGetter getPopVarGetter(int i) {return PopVarGetters[i];}
	public VarGetter[] getMktVarGetters() {return MktVarGetters;}
	public VarGetter getMktVarGetter(int i) {return MktVarGetters[i];}




}