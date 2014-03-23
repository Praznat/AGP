package Questing;

import AMath.Calc;
import Avatar.SubjectiveType;
import Defs.*;
import Descriptions.*;
import Game.*;
import Markets.*;
import Questing.Quest.PatronedQuest;
import Questing.Quest.PatronedQuestFactory;
import Sentiens.*;
import Shirage.Shire;

public class PropertyQuests {
	public static PatronedQuestFactory getMinistryFactory() {return new PatronedQuestFactory(BuildWealthQuest.class) {public Quest createFor(Clan c, Clan p) {return new BuildWealthQuest(c, p);}};}
	
	//TODO make PATRONED
	public static class BuildWealthQuest extends PatronedQuest {
		private final int startCumInc, goalCumInc;
		public BuildWealthQuest(Clan clan, Clan patron) {
			super(clan, patron);
			startCumInc = clan.getCumulativeIncome();
			goalCumInc = (int) Math.min(Integer.MAX_VALUE / 2, clan.getNetAssetValue(clan));
		} //default is to double NAV
		@Override
		public void pursue() {
			if (Me.getCumulativeIncome() - startCumInc >= goalCumInc) {success(Me.myShire(), Me.getJob()); return;}
			if (Me.getCumulativeIncome() <= startCumInc && Calc.pPercent(80-4*Me.FB.getBeh(M_.PATIENCE))) { // 20-80% chance of give up
				failure(Me.myShire(), Me.getJob()); return;
			}
			final Job j = Me.getJob();
			if (j instanceof Ministry) {
				// ministry job
				((Ministry) j).getService().doit(Me);
			}
			else if (j == Job.TRADER) {Me.MB.newQ(new TradingQuest(Me));}
			else {Me.MB.newQ(new LaborQuest(Me));}
		}
		public String description() {return "Build Wealth" + (patron != Me ? " for " + patron.getNomen() : "");}
	}
	
	
	
	public static class LaborQuest extends Quest implements GoodsAcquirable {
		public static final int WORKMEMORY = 30;
		private int[] workmemo = new int [WORKMEMORY]; //stock id
		private int[] workmemoX = new int [WORKMEMORY];//stock count
		private int stage = 0;
		private int turnsLeft;
		private Labor chosenAct;

		public LaborQuest(Clan P) {
			super(P); setChosenAct((Labor) Job.NullAct); resetWM();
			turnsLeft = P.FB.getBeh(M_.PATIENCE) / 3 + 5;
		}
		@Override
		public String description() {return chosenAct + " stage " + stage;}
		
		@Override
		public void avatarPursue() {
			if (Me.getJob() instanceof Ministry) {Me.MB.finishQ(); return;}
			switch (stage) {
			case 0: avatarChooseAct(); break; //avatarDoInputs called by avatarChooseAct
			case 1: avatarDoInputs(); break;
			case 2: doWork(); break;
			case 3: doOutputs(); break;
			default: break;
			}		
		}
		
		@Override
		public void pursue() {
			if (Me.getJob() instanceof Ministry) {Me.MB.finishQ(); return;}
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
		public void alterG(int g, int n) {
			for(int i = 0; i < workmemo.length; i++) {
				if (getAbsWM(i) == g) {
					int newWMX = workmemoX[i] + n;
					workmemoX[i] = newWMX < 0 ? 0 : newWMX;
					return;
				}
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
		public int getStage() {return stage;}
		
		public void liquidateWM() {resetWM();}  // TODO and sell all to market
		public void setChosenAct(Labor a) {
			//liquidate if it's a new act
			//do nothing if act is same
			if (chosenAct == null || !chosenAct.equals(a)) {
				liquidateWM();
				chosenAct = a;
			} //new WORKMEMO
			if (chosenAct != Job.NullAct) {chosenAct.storeAllInputsInWM(Me);}
		}
		private Labor compareTrades() {
			final Act[] actSet = Me.getJobActs();
			Labor curAct;   Labor bestAct = (Labor) Job.NullAct;   int bestPL = 0;//Integer.MIN_VALUE / 2;
			for(int i = 0; i < actSet.length; i++) {
				curAct = (Labor) actSet[i];
				final double expOut = curAct.expOut(Me)[0];
				final double expIn = curAct.expIn(Me)[0];
				double PL = expOut - expIn; //cuz of weird shit with MAX_INTEGER
				PL = Me.confuse(PL);
//				System.out.println(Me + " " + PL + "=" + expOut + "-" + expIn + (expIn < expOut));
				final int expTime = 1;
				PL /= expTime;
				if (PL > bestPL) {bestPL = (int)Math.round(PL); bestAct = curAct;}
			}
//			System.out.println(bestPL);
			return bestAct;
		}
		private void avatarChooseAct() {
			avatarConsole().showChoices("Choose labor", Me, Me.getJobActs(), SubjectiveType.ACT_PROFIT_ORDER, new Calc.Listener() {
				@Override
				public void call(Object arg) {
					Quest q = Me.MB.QuestStack.peek();
					if (q instanceof LaborQuest) {((LaborQuest) q).setChosenAct((Labor) arg);}
					stage++;
					avatarDoInputs();
				}
			});
		}
		private void chooseAct() {
			setChosenAct(compareTrades());
			if (chosenAct == Job.NullAct) {
				failure(Me.myShire()); //LaborQuest fails Shire, BuildWealth fails Shire and Job
			}
			//fill WORKMEMO with every possible g in A:
			stage++;
		}
		private void avatarDoInputs() {
			doInputs();
		}
		private void doInputs() {
			if (turnsLeft <= 0) { // untested
				int i = -1;   while (workmemo[++i] != Defs.E) { //liquidate
					MktO mkt = (MktO)Me.myMkt(getAbsWM(i));
					mkt.removeBids(Me); // TODO no idea if this works.. pls test
					for (int k = workmemoX[i]; k > 0; k--) {mkt.sellFair(Me);} //sell leftovers
				}
				failure(Me.myShire()); return;
			}
			turnsLeft--;
			//

			// PROBLEM WITH MULTIPLE "OR" INPUTS (SEE BUTCHER) ?
			
			//buy cheap input, but then in next round of doInputs, offer on recently bought cheap input is higher
			//than alternative, so it doesnt go in totalNeeded..
			int[] totalNeeded = chosenAct.expIn(Me); //first number zero for expProfit
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
			success();
		}

	};
	
	
	
	
	
	/**
	 * trader initiates quest choosing a random route to go on and number of rounds to go on that route.
	 * at movement in the route, trader chooses best good and places bid at Shire
	 * which when hit will trigger a sell in the Shire with best seen value for good
	 * @author alexanderbraylan
	 *
	 */
	public static class TradingQuest extends Quest implements GoodsAcquirable {
		Shire[] route;
		int plcInRoute = 0;
		Trade favTrade = Job.TradeC; // TODO
		int[] sellPlcs = new int[Defs.numGoods];
		int numRounds;
		
		public TradingQuest(Clan P) {
			super(P);
			route = new Shire[2 + P.FB.getBeh(M_.WANDERLUST) / 4];
			route[0] = Me.myShire();
			for (int i = 1; i < route.length; i++) route[i] = route[i-1].getSomeNeighbor();
			for (int i = 0; i < sellPlcs.length; i++) sellPlcs[i] = Defs.E;
			numRounds = 3 + Me.FB.getBeh(M_.PATIENCE) / 5;
		}

		@Override
		public void pursue() {
			Shire lastShire = route[plcInRoute < route.length ? plcInRoute : (route.length - 2 - plcInRoute % route.length)];
			plcInRoute++;
			int newshireplc = plcInRoute < route.length ? plcInRoute : (route.length - 2 - plcInRoute % route.length);
			Shire newShire = route[newshireplc];
			int[] prospects = scoutShire(lastShire, newShire, favTrade);
			int g = prospects[0]; int buyPx = prospects[1]; int sellPx = prospects[2];
			if (sellPx > 0) {
				((MktO)lastShire.getMarket(g)).placeBid(Me, buyPx);
				if (sellPlcs[g] == Defs.E || sellPx >= ((MktO)route[sellPlcs[g]].getMarket(g)).riskySellPX(Me)) {
					sellPlcs[g] = newshireplc;
				}
			}
			Me.setCurrentShire(newShire);
			if (plcInRoute == route.length * 2 - 2) {finishUp(); return;}
		}
		
		private void finishUp() {
			plcInRoute = 0;
			if(--numRounds == 0) success();
		}
		
		/** find best good, its cost at buyShire and value at sellShire */
		private int[] scoutShire(Shire buyShire, Shire sellShire, Trade trade) {
			int bestG = -1; double bestTrade = 0; double bestCost = 0; double bestValue = 0;
			for (int g : trade.getGoods()) {
				final int value = ((MktO)sellShire.getMarket(g)).riskySellPX(Me);
				int cost = buyShire.getMarket(g).buyablePX(Me);
				// offer to buy to make money at cost = between 1/3 and 2/3 of value)
				if (cost == MktO.NOASK) cost = 2 * value / (3 + Me.FB.getBeh(M_.BIDASKSPRD) / 5);
				// calc profit before you limit cost to money you own
				final double expProfit = Me.confuse((double)value - (double)cost);
				cost = Math.min(cost, Me.getMillet());
				if (expProfit >= bestTrade) {bestG = g; bestTrade = expProfit; bestCost = cost; bestValue = value;}
			}
			return new int[] {bestG, (int)Math.round(bestCost), (int)Math.round(bestValue)};
		}

		@Override
		public String description() {return "Trading";}

		@Override
		public void alterG(int good, int num) {
			int p = sellPlcs[good];
			Shire sellShire = p < 0 ? Me.currentShire() : route[p];
			for (int i = 0; i < num; i++) sellShire.getMarket(good).sellFair(Me);
		}
		
	}
	
}
