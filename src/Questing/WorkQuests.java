package Questing;

import AMath.Calc;
import Defs.P_;
import Descriptions.XWeapon;
import GUI.APopupMenu;
import Game.*;
import Game.Do.ClanAlone;
import Markets.MktAbstract;
import Markets.MktO;
import Markets.XWeaponMarket;
import Sentiens.Clan;
import Sentiens.GobLog;

public class WorkQuests {

	
	public static class BuildWealthQuest extends Quest {
		public BuildWealthQuest(Clan P) {super(P);}
		@Override
		public void pursue() {Me.MB.newQ(new LaborQuest(Me));}
		public String description() {return "Build Wealth";}
	}
	
	
	
	public static class LaborQuest extends Quest {
		public static final int WORKMEMORY = 30;
		private int[] workmemo = new int [WORKMEMORY]; //stock id
		private int[] workmemoX = new int [WORKMEMORY];//stock count
		private int stage = 0;
		private Labor chosenAct;
		
		public LaborQuest(Clan P) {
			super(P); setChosenAct((Labor) Job.NullAct); resetWM();
		}
		@Override
		public String description() {return chosenAct + " stage " + stage;}

		@Override
		public void avatarPursue() {
			switch (stage) {
			case 0: avatarChooseAct();
			case 1: avatarDoInputs(); break;
			case 2: doWork(); break;
			case 3: doOutputs(); break;
			default: break;
			}		
		}
		
		@Override
		public void pursue() {
			switch (stage) {
			case 0: chooseAct();
			case 1: doInputs(); break;
			case 2: doWork(); break;
			case 3: doOutputs(); break;
			default: break;
			}		
		}
		
		public void resetWM() {
			for(int i = 0; i < workmemo.length; i++) {workmemo[i] = Defs.E; workmemoX[i] = 0;}
		}
		public void setWM(int g, int plc) {workmemo[plc] = g;   workmemoX[plc] = 0;}
		public void getG(int g) {
			for(int i = 0; i < workmemo.length; i++) {
				if (getAbsWM(i) == g) {workmemoX[i]++;   return;}
			}
			((MktO)Me.myMkt(g)).sellFairAndRemoveBid(Me);   //in case not needed for work (not in WORKMEMO)
		}
		public void suspendG(int g) {
			for(int i = 0; i < workmemo.length; i++) {
				if (workmemo[i] == g) {workmemo[i] = -g;   i++;   return;}
			}
		}
		public int[] getWM() {return workmemo;}
		public int[] getWMX() {return workmemoX;}
		public int getAbsWM(int i) {return Math.abs(workmemo[i]);}
		public int getWMX(int i) {return workmemoX[i];}
		public Labor getChosenAct() {return chosenAct;}
		
		public void liquidateWM() {resetWM();}  // TODO and sell all to market
		public void setChosenAct(Labor a) {
			//liquidate if it's a new act
			if (chosenAct != null && chosenAct.equals(a)) {} //do nothing if act is same
			else {
				liquidateWM();
				chosenAct = a;
			} //new WORKMEMO
			if (chosenAct != Job.NullAct) {chosenAct.storeAllInputsInWM(Me);}
		}
		private Labor compareTrades() {
			Act[] actSet = Me.getJobActs();
			Labor curAct;   Labor bestAct = (Labor) Job.NullAct;   int bestPL = 0;
			for(int i = 0; i < actSet.length; i++) {
				curAct = (Labor) actSet[i];
				int PL = Me.confuse(curAct.expOut(Me)[0] - curAct.expIn(Me)[0]);
				if (PL > bestPL) {bestPL = PL; bestAct = curAct;}
			}
			return bestAct;
		}
		private void avatarChooseAct() {
			avatarConsole.choices.clear();
			avatarConsole.getComparator().setPOV(Me);
			avatarConsole.getComparator().setComparator(avatarConsole.getComparator().ACT_PROFIT_ORDER);
			ClanAlone action;
			for(Act act : Me.getJobActs()) {
				action = Do.setChosenAct((Labor) act);
				action.setup(Me);
				avatarConsole.choices.put(act, action);
			}
			new APopupMenu(avatarConsole, avatarConsole.choices.values());
			stage++;
		}
		private void chooseAct() {
			setChosenAct(compareTrades());
			//fill WORKMEMO with every possible g in A:
			stage++;
		}
		private void avatarDoInputs() {
			doInputs();
		}
		private void doInputs() {
			//

			// PROBLEM WITH MULTIPLE "OR" INPUTS (SEE BUTCHER) ?
			
			//
			int[] totalNeeded = chosenAct.expIn(Me); //why is first number zero?
			int[] stillNeeded = Calc.copyArray(totalNeeded);
			int j;   int i = -1;   while (workmemo[++i] != Defs.E) {
				int N = workmemoX[i];
				j = 0;   while (stillNeeded[++j] != Defs.E) { //goes through WM setting to E all nec inputs already owned
					if(workmemo[i] == -stillNeeded[j]) {totalNeeded[j] = -Math.abs(totalNeeded[j]);} //mark as dont buy if already in market
					if(N>0 && stillNeeded[j]==getAbsWM(i)) {stillNeeded[j] = 0;   N--;}  //0 should not be a good
				}
			}
			boolean go = true;   j = 0;   while (stillNeeded[++j] != Defs.E) {if(stillNeeded[j]!=0) {go=false; break;}}
			if (go) {   //in case all nec inputs owned
				i = -1;   while (workmemo[++i] != Defs.E) {
					int wmg = getAbsWM(i);
					MktAbstract mkt = Me.myMkt(wmg);
					j = 0;   while (totalNeeded[++j] != Defs.E) {  //consume WM goods used in input:
						if(workmemoX[i] == 0) {break;}
						//is this right?? setting in[j] to E even though the while loop stops at E?
						//set in[j] to 0 to correct this problem... see if it works
						if(Math.abs(totalNeeded[j]) == wmg) {totalNeeded[j] = 0;   workmemoX[i]--;   mkt.loseAsset(Me);   Me.addReport(GobLog.consume(wmg));}
					}
					for (int k = workmemoX[i]; k > 0; k--) {mkt.sellFair(Me);} //sell leftovers
				}
				stage++;   //move on
			}
			else {i = 0; while (totalNeeded[++i] != Defs.E) {if(totalNeeded[i] >= 0) {
				Me.myMkt(totalNeeded[i]).liftOffer(Me);   suspendG(totalNeeded[i]);
			}}} //dont lift in case of - (see above)
		}
		private void doWork() {
			chosenAct.doit(Me);
			stage++;
		}
		private void doOutputs() {
			int[] out = chosenAct.expOut(Me);
			int i = 1; for (; out[i] != Defs.E; i++) {
				int g = out[i];
				if (g == Defs.sword || g == Defs.mace) {
					short x = XWeapon.craftNewWeapon(g, Me.FB.getPrs(P_.SMITHING));
					if (x != XWeapon.NULL) {
						g = Defs.xweapon;
						Me.addReport(GobLog.produce(x));
						((XWeaponMarket) Me.myMkt(Defs.xweapon)).setUpTmpXP(x);
					}
				}
				if (g != Defs.xweapon) {Me.addReport(GobLog.produce(g));}
				Me.myMkt(g).gainAsset(Me);
				Me.myMkt(g).sellFair(Me);
			}
			stage = 0;
		}

	};
	
}
