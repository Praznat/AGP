package Sentiens;

import AMath.Calc;
import Defs.M_;
import Defs.P_;
import Defs.Q_;
import Game.AGPmain;
import Game.Act;
import Game.Defs;
import Game.Job;
import Game.Naming;
import Game.XWeapon;
import Markets.*;

public class Quest implements Defs {
	public static final int MEMORY = 10;
	public static final int MAXSPACE = 10;
	public static final int WORKMEMORY = 30;
	protected static final int THIS = 0;
	protected static final int UP = 1;
	protected int[][] Qstack = new int[MEMORY][MAXSPACE];
	protected int[] WORKMEMO = new int [WORKMEMORY]; //stock id
	protected int[] WORKMEMOX = new int [WORKMEMORY];//stock count
	protected Clan Me;
	protected Act chosenAct;
	protected int AP;
	protected String Report = "";
	public boolean doReporting = false;
	
	public Quest(Clan i) {Me = i;  setChosenAct(Job.NullAct); resetWM(); resetAll();}

	protected void report() {if (doReporting) {System.out.print(getReport());   Report = "";}}
	protected void success() {
		Report+="   Quest Accomplished";
		report();   finishQ();
		if (Qstack[THIS][0] == -1) {Me.AB.catharsis(1);}
	}
	protected void success(Stressor.Causable relief) {Me.AB.relieveFrom(new Stressor(Stressor.ANNOYANCE, relief));   success();}
	protected void failure() {
		Report+=("   Quest [" + getQuestDescription(THIS) + "] Failed");
		report();   finishQ();
	}
	protected void failure(Stressor.Causable blamee) {Me.AB.add(new Stressor(Stressor.ANNOYANCE, blamee));   failure();}
	protected void finishQ() {
		for (int i = 0; i < MEMORY-1; i++) {CopyVfrom(Qstack[i], Qstack[i+1]);}
		nullifyEnd();
	}
	public Q_ toQ(int q) {
		return Q_.values()[q];
	}
	public void newQ(Q_ q) {
		newQ(q.ordinal());
	}
	public void newQ(int q) {
		if (nullEnd()) {
			for (int i = MEMORY-1; i > 0; i--) {CopyVfrom(Qstack[i], Qstack[i-1]);}
			initializeQ(0);   Qstack[THIS][0] = q;
		}   else {outofmemory();}
	}
	protected void outofmemory() {System.out.println("quest overload"); resetAll();}
	protected void initializeQ(int r) {for (int i = 0; i < MAXSPACE; i++) {Qstack[r][i] = -1;}}
	protected void resetAll() {for (int i = 0; i < MEMORY; i++) {initializeQ(i);}}
	protected void nullifyEnd() {initializeQ(MEMORY-1);}
	protected boolean nullEnd() {return Qstack[MEMORY-1][0] == -1;}
	protected void CopyVfrom(int[] to, int[] from) {
		for (int i = 0; i < MAXSPACE; i++) {to[i] = from[i];}
	}
	public int[] getQstack(int i) {return Qstack[i];}
	
	public void resetWM() {
		for(int i = 0; i < WORKMEMO.length; i++) {WORKMEMO[i] = E; WORKMEMOX[i] = 0;}
	}
	public void setWM(int g, int plc) {WORKMEMO[plc] = g;   WORKMEMOX[plc] = 0;}
	public void getG(int g) {
		for(int i = 0; i < WORKMEMO.length; i++) {
			if (getWM(i) == g) {WORKMEMOX[i]++;   return;}
		}
		((MktO)Me.myMkt(g)).sellFairAndRemoveBid(Me);   //in case not needed for work (not in WORKMEMO)
	}
	public void suspendG(int g) {
		for(int i = 0; i < WORKMEMO.length; i++) {
			if (getWM(i) == g) {WORKMEMO[i] = -Math.abs(WORKMEMO[i]);   i++;   return;}
		}
	}
	public int getWM(int i) {return Math.abs(WORKMEMO[i]);}
	public int getWMX(int i) {return WORKMEMOX[i];}
	public Act getChosenAct() {return chosenAct;}
	
	public void liquidateWM() {resetWM();}  //and sell all to market
	
	protected Act compareTrades(Clan doer, Act[] actSet) {
		Act curAct;   Act bestAct = Job.NullAct;   int bestPL = 0;
		for(int i = 0; i < actSet.length; i++) {
			curAct = actSet[i];
			int PL = Me.confuse(curAct.expOut(Me)[0] - curAct.expIn(Me)[0]);
			if (PL > bestPL) {bestPL = PL; bestAct = curAct;}
		}
		return bestAct;
	}
	
	public void setChosenAct(Act a) {
		//liquidate if it's a new act
		if (chosenAct != null && chosenAct.equals(a)) {} //do nothing if act is same
		else {
			liquidateWM();
			chosenAct = a;
		} //new WORKMEMO
	}
	
	
	
	public Clan rando() {
		Clan[] pop = Me.myShire().getCensus();
		if (pop.length == 0) {return null;}
		return pop[AGPmain.rand.nextInt(pop.length)];
	} 
	private Clan target(int i) {return AGPmain.TheRealm.getClan(Qstack[i][TARGET]);}
	private Clan pov(int i) {return AGPmain.TheRealm.getClan(Qstack[i][POV]);}
	private Clan patron(int i) {return (Qstack[i][PATRON] == -1 ? Me : AGPmain.TheRealm.getClan(Qstack[i][PATRON]));}
	private void designate(Clan he, int place, int layer) {Qstack[layer][place] = he.getID();}
	private boolean betterValueThan(Clan one, Clan two, int prest) {
		if (one.FB.getPrs(prest) > two.FB.getPrs(prest)) {return true;}   else {return false;}
	}
	private boolean demandRespect(Clan sub, Clan obj) {
		if (obj.FB.compareSanc(sub) >= 0) {return false;}	else {return true;}
	}
	private void successfulCourt(int i) {Qstack[i][COURTSLEFT]--;   Me.addReport(GobLog.successfulCourt(target(i)));}
	private boolean courtingDone() {return (Qstack[THIS][COURTSLEFT] == 0);}
	private void loseTime(int i) {Qstack[i][TIMELEFT]--;}
	private boolean outOfTime(int i) {return (Qstack[i][TIMELEFT] <= 0);}
	private void setTime(int T) {Qstack[THIS][TIMELEFT] = T;}
	private Sanc getSanc(int i) {return AGPmain.TheRealm.getSanc(Qstack[i][PREST]);}
	private boolean checkIfBetter(int i) {return (getSanc(i).compare(Me, target(i), pov(i)) > 0);}
	private void incStage() {Qstack[THIS][STAGE]++;}
	
	private void ChooseAct() {
		setChosenAct(compareTrades(Me, Me.getJobActs()));
		Report += "Chose to engage in " + chosenAct.getDesc() + RET;
		//fill WORKMEMO with every possible g in A:
		chosenAct.storeAllInputsInWM(Me);
		incStage();
	}
	private void DoInputs() {
		//
		
		// PROBLEM WITH MULTIPLE "OR" INPUTS (SEE BUTCHER) ?
		
		//
		int[] in = chosenAct.expIn(Me); //why is first number zero?
		int[] tmp = Calc.copyArray(in);
		int j;   int i = -1;   while (WORKMEMO[++i] != E) {
			int N = WORKMEMOX[i];
			j = 0;   while (tmp[++j] != E) { //goes through WM setting to E all nec inputs already owned
				if(WORKMEMO[i] == -tmp[j]) {in[j] = -Math.abs(in[j]);} //dont buy if already in market
				if(N>0 && tmp[j]==getWM(i)) {tmp[j] = 0;   N--;}  //0 should not be a good
			}
		}
		boolean go = true;   j = 0;   while (tmp[++j] != E) {if(tmp[j]!=0) {go=false; break;}}
		if (go) {   //in case all nec inputs owned
			i = -1;   while (WORKMEMO[++i] != E) {
				int wmg = getWM(i);
				MktAbstract mkt = Me.myMkt(wmg);
				j = 0;   while (in[++j] != E) {  //consume WM goods used in input:
					if(WORKMEMOX[i] == 0) {break;}
					//is this right?? setting in[j] to E even though the while loop stops at E?
					//set in[j] to 0 to correct this problem... see if it works
					if((in[j]) == wmg) {in[j] = 0;   WORKMEMOX[i]--;   mkt.loseAsset(Me);   Me.addReport(GobLog.consume(wmg));}
				}
				for (int k = WORKMEMOX[i]; k > 0; k--) {mkt.sellFair(Me);} //sell leftovers
			}
			incStage();   //move on
		}
		else {i = 0; while (in[++i] != E) {if(in[i] >= 0) {
			Me.myMkt(in[i]).liftOffer(Me);   suspendG(in[i]);
		}}} //dont lift in case of - (see above)
	}
	private void DoWork() {
		chosenAct.ponderOrLearn(Me);
		incStage();
	}
	private void DoOutputs() {
		int[] out = chosenAct.expOut(Me);
		int i = 1; for (; out[i] != E; i++) {
			int g = out[i];
			if (g == sword || g == mace) {
				short x = XWeapon.craftNewWeapon(g, Me.FB.getPrs(P_.SMITHING));
				if (x != XWeapon.NULL) {
					g = xweapon;
					Me.addReport(GobLog.produce(x));
					((XWeaponMarket) Me.myMkt(xweapon)).setUpTmpXP(x);
				}
			}
			if (g != xweapon) {Me.addReport(GobLog.produce(g));}
			Me.myMkt(g).gainAsset(Me);
			Me.myMkt(g).sellFair(Me);
		}
		Qstack[THIS][STAGE] = 0;
	}
	public void Finish() {
		resetWM();
		chosenAct = Job.NullAct;
	}
	
	
	
	public void setInitStack() {
		int q;
		for (int i = Me.useBeh(M_.PATIENCE); i >= 0; i--) {
			q = Me.FB.SancInPriority(AGPmain.rand.nextInt(16)).getQuest();
			if (q != -1) {newQ(q); setTime(Me.useBeh(M_.PATIENCE)); return;}
		}
		newQ(Q_.NOTHING.ordinal());
	}
	
	
	
	
	
	
	
	
	
	

	
	
	
	
	
	
	

	
	
	public void pursue() {
		switch(toQ(Qstack[THIS][0])) {
		case NOTHING: setInitStack();   break;
		case WORK:
			switch (Qstack[THIS][STAGE]) {
			case 0: ChooseAct();
			case 1: DoInputs(); break;
			case 2: DoWork(); break;
			case 3: DoOutputs(); break;
			default: break;
			} report(); break;
		case BREED: //TYPE/TARGET/TIMELEFT/COURTSLEFT
			if (courtingDone()) {Me.breed(target(THIS)); Me.FB.upPrest(P_.SEXP); success(Me); break;}
			if (outOfTime(THIS)) {Me.FB.downPrest(P_.SEXP); failure(Me); break;}
			if (Qstack[THIS][TARGET] == -1) {newQ(FINDMATE); AP++; Qstack[THIS][TIMELEFT] = Me.useBeh(M_.PATIENCE)+1; break;}
			Clan crush = target(THIS);
			Clan rival = crush.getSuitor();
			if (rival.getID() != Me.getID()) {
				newQ(COMPETE4MATE);
				designate(crush, POV, THIS);
				designate(rival, TARGET, THIS);
				Qstack[THIS][TIMELEFT] = 1; //for COMPETE4MATE (unnecessary?)
			} else {successfulCourt(THIS);}
			break;
		case FINDMATE: //TYPE/null/TIMELEFT
			Clan potmate = rando();
			if ((potmate.getGender() != Me.getGender()) && Me.FB.compareSanc(potmate) < 1) {
				designate(potmate, TARGET, UP);
				Qstack[UP][COURTSLEFT] = 16 - potmate.useBeh(M_.PROMISCUITY);
				Me.addReport(GobLog.findMate(potmate));
				success(Me.myShire());   break;
			} else {Me.addReport(GobLog.findMate(null));   loseTime(THIS);}
			if (outOfTime(THIS)) {failure(Me.myShire());}   break;
		case FINDWEAKLING: //TYPE/null/TIMELEFT
			Clan weakling = rando();
			if (Me.FB.compareSanc(weakling) < 0) {
				designate(weakling, TARGET, UP);
				Me.addReport(GobLog.findWeakling(weakling));
				success(Me.myShire());   break;
			} else {Me.addReport(GobLog.findWeakling(null));   loseTime(THIS);}
			if (outOfTime(THIS)) {failure(Me.myShire());}   break;
		case COMPETE4MATE: //TYPE/TARGET/TIMELEFT/PREST/POV/
			Clan love = pov(THIS);   Clan competition = love.getSuitor();
			int diff;   Sanc S;   int q;
			for (int k = love.useBeh(M_.PATIENCE); k >= 0; k--) {
				Qstack[THIS][PREST] = love.FB.sancInPriority(AGPmain.rand.nextInt(16));
				S = getSanc(THIS);
				diff = S.compare(Me, competition, love);
				Me.addReport(GobLog.compete4Mate(love, competition, diff));
				if (diff == 0) {
					q = S.getQuest();   Report += "~ " + S.description(love) + " ";
					if (q != -1 && q != BREED) {
						Report += "Ready to compete against " + competition.getNomen() + " in " +
								S.description(love) + RET;
						report();   newQ(q);   break;
					}
				}
				else if (diff == -1) {
					// add subvalue stressor
					Report += "~ " + love.getNomen() + " prefers " + competition.getNomen() + " over you for " + Naming.possessive(competition) + " " +
							S.description(love) + RET;   break;
				} //go to failure
				else if (diff == 1) {
					// relieve subvalue stressor
					Report += "~ " + love.getNomen() + " prefers you over " + competition.getNomen() + " for your " +
							S.description(love) + System.getProperty("line.separator");
					successfulCourt(UP); pov(THIS).setSuitor(Me); success();   break;
				}
			}
			if (Qstack[THIS][0] == COMPETE4MATE) {loseTime(UP); failure(competition);} //skipped if theres new Q
			break;
		case FINDMINION: //TYPE/TARGET/TIMELEFT/PREST
			if (Qstack[THIS][TARGET] == -1) {Qstack[THIS][TARGET] = rando().getID(); break;}
			if (outOfTime(THIS)) {designate(target(THIS), TARGET, UP); finishQ(); AP++; break;}
			loseTime(THIS);
			Clan candidate = rando();
			if(betterValueThan(candidate, target(THIS), Qstack[THIS][PREST])) {designate(candidate, TARGET, THIS); finishQ();}
			break;
		case PREACH: //TYPE/TARGET/TIMELEFT/null/PATRON
			Me.prch(patron(THIS), rando());
			if(outOfTime(THIS)) {finishQ();}
			break;
		case BUILDWEALTH: //TYPE/TARGET/TIMELEFT/null/PATRON/
			if(Qstack[UP][0] == COMPETE4MATE && checkIfBetter(UP)) {success();}
			newQ(WORK);  Qstack[THIS][STAGE] = 0;
			break;
		case TERRORIZE: //TYPE/TARGET/TIMELEFT/null/PATRON/
			if(Qstack[THIS][TARGET] == -1) {newQ(FINDWEAKLING); AP++; Qstack[THIS][TIMELEFT] = Me.useBeh(M_.PATIENCE)+1; break;}
			Clan victim = target(THIS);
			boolean respected = demandRespect(Me, victim);
			Me.addReport(GobLog.demandRespect(victim, respected));
			if (respected) {Me.FB.upPrest(P_.TYRRP);  success(victim); break;}   else {Me.FB.downPrest(P_.RSPCP);}
			Report += "You attempt to best " + victim.getNomen() + " in combat." + System.getProperty("line.separator");
			Report += "Your " + Me.FB.getPrs(P_.MARTIALP) + " vs " + Naming.possessive(victim) + " " + victim.FB.getPrs(P_.MARTIALP) + System.getProperty("line.separator");
			final boolean won = betterValueThan(Me, victim, P_.MARTIALP.ordinal());
			Me.addReport(GobLog.handToHand(victim, won));
			if(won) {Me.FB.upPrest(P_.TYRRP);   success(victim); break;}
			Me.FB.downPrest(P_.TYRRP);   failure(victim); break;
		default: break;
		}
		if (doReporting) {System.out.println(getQuestDescription(THIS));}
		
	}
	public int getQuest(int line) {return Qstack[line][0];}
	public String getQuestDescription(int line) {return Naming.QstackDes(Me, line);}
	public String getReport() {return Report;}
	
}
