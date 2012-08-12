package Markets;


import Questing.Quest;
import Questing.WorkQuests.LaborQuest;
import Sentiens.Clan;
import Sentiens.GobLog;
import Shirage.Shire;
import AMath.Calc;
import Defs.M_;
import Descriptions.Naming;
import Game.*;

public class MktO extends MktAbstract {
	//this is for LIQUID NAMES i.e. not ones that are kept in private inv
	protected static double expandSZ = 0.75;
	protected static double contractSZ = 0.25;
	protected static final int DEFAULTPX = 100;
	protected static final int STARTSZ = 10;
	public static final double[] RATES = {0.01,0.0125,0.015,0.02,0.03,0.05,0.075,0.1,0.15,0.2,0.25,0.3,0.35,0.4,0.45,0.5};
	public static final int NOASK = Integer.MAX_VALUE;
	public static final int NOBID = 0;
	
	protected static String report;
	
	protected int LTAvg, STAvg, LastPX, MaxPX, MinPX, offerlen, bidlen, bb;
	protected Entry[] Bids, Offers;
	
	public MktO() {}
	public MktO(int ggg, Shire h) {
		g = ggg;
		Offers = new Entry[STARTSZ];
		Bids = new Entry[STARTSZ];
		for(int i = 0; i < STARTSZ; i++) {
			Offers[i] = new Entry();
			Bids[i] = new Entry();
		}
		LastPX = DEFAULTPX;
		LTAvg = DEFAULTPX;
		STAvg = DEFAULTPX;
		MaxPX = 0;   MinPX = Integer.MAX_VALUE;
		home = h;
	}
	
	public static int annuity(int val, Clan doer) {
		return Calc.roundy((double)val / RATES[doer.useBeh(M_.DISCRATE)]);
	}
	public static int interest(int val, Clan doer) {
		return Calc.roundy((double)val * RATES[doer.useBeh(M_.DISCRATE)]);
	}
	
	public int lastPrice() {return (smaVol() > 0 ? LastPX : -1);}
	public int stAvg() {return (smaVol() > 0 ? STAvg : -1);}
	public int ltAvg() {return (smaVol() > 0 ? LTAvg : -1);}
	public int getBidSz() {return bidlen;}
	public int getAskSz() {return offerlen;}
	public int bestOffer() {
		if (offerlen == 0) {return NOASK;}
		else {return Offers[0].px;}
	}
	public int bestBid() {
		int plc = bestBidPlc();  if(plc == -1) {return NOBID;}
		else {return Bids[plc].px;}
	}
	public int bestBidPlc() {
		for(int i = 0; i < bidlen; i++) {if (Bids[i].px < Bids[i].trader.getMillet()) {return i;}}
		return -1;
	}
	
	protected void updateAvgs(int p) {
		LTAvg = (int) Math.round(0.2 * p + 0.8 * LTAvg);
		STAvg = (int) Math.round(0.5 * p + 0.5 * STAvg);
		LastPX = p;
		MaxPX = (MaxPX > p ? MaxPX : p);
		MinPX = (MinPX < p ? MinPX : p);
	}

	public int buyablePX(Clan buyer) {
		return (offerlen == 0 ? estFairBid(buyer) : Offers[0].px);
	}
	public int sellablePX(Clan seller) {
		return estFairOffer(seller);
	}


	public void buyFair(Clan buyer) {
		int px = estFairBid(buyer);
		report += buyer.getNomen() + " tries to buy " + Naming.goodName(g) + " at fair price of " + px + RET;
		if(px >= bestOffer()) {liftOffer(buyer);}
		else {placeBid(buyer, px);}
	}
	public void sellFair(Clan seller) {
		int px = estFairOffer(seller);
		report += seller.getNomen() + " tries to sell " + Naming.goodName(g) + " at fair price of " + px + RET;
		//if(px <= bestBid() && px > NOBID) {hitBid(seller);}
		if(px<0) {
			System.out.println("SHWIIIIT");
//			boolean tmp = true;
			px = estFairOffer(seller);
			placeOffer(seller, px);
		}
		else {placeOffer(seller, px);}
	}
	public void sellFairAndRemoveBid(Clan seller) {
		removeBid(seller);
		Calc.p("NOOOOOOOOOOOOO");
		report += seller.getNomen() + " finds " + Naming.goodName(g) + " UNNECESSARY" + RET;
//		Log.info(seller.getNomen() + " finds " + Naming.goodName(g) + " UNNECESSARY");
		placeOffer(seller, estFairOffer(seller));
	}
	public void liftOffer(Clan buyer) {liftOffer(buyer, 0);}
	protected void liftOffer(Clan buyer, int plc) {
		report += buyer.getNomen() + " tries to lift offer for " + Naming.goodName(g) + RET;
		Clan seller = Offers[plc].trader;
		if (transaction(buyer, seller, Offers[plc].px)) {
			loseAsset(seller);   removeOffer(plc);   finish();
		} else {buyFair(buyer);}
	}
	public void hitBid(Clan seller) {hitBid(seller, bestBidPlc());}
	protected void hitBid(Clan seller, int plc) {
		report += seller.getNomen() + " tries to hit bid for " + Naming.goodName(g) + RET;
		if (plc != -1 && transaction(Bids[plc].trader, seller, Bids[plc].px)) {
			loseAsset(seller);   removeBid(plc);   finish();
		} else {sellFair(seller);}
	}
	protected boolean transaction(Clan buyer, Clan seller, int price) {
		if(seller == null || buyer == null) {return false;}
		seller.alterMillet(price);
		if (!buyer.alterMillet(-price)) {
			report += buyer.getNomen() + " has not enough millet to buy " + Naming.goodName(g) + RET;
			seller.alterMillet(-price); return false;
		}
		buyer.addReport(GobLog.transaction(g, price, true, seller));
		seller.addReport(GobLog.transaction(g, price, false, buyer));
		report += buyer.getNomen() + " buys " + Naming.goodName(this.g) + " from " + seller.getNomen() + " for " + price + RET;
		sendToInventory(buyer); //, price);
		updateAvgs(price);
//		Log.info(buyer.getNomen() + " buys " + Naming.goodName(this.g) + " from " + seller.getNomen() + " for " + price);
		return true;
	}
	protected void sendToInventory(Clan buyer) { //, int px) {
		gainAsset(buyer);
		Quest q = buyer.MB.QuestStack.peek();
		if (q instanceof LaborQuest) {((LaborQuest) q).getG(g); return;}
		q = buyer.MB.QuestStack.peekUp();  //might as well
		if (q instanceof LaborQuest) {((LaborQuest) q).getG(g); return;}
		sellFairAndRemoveBid(buyer);  //in case current (and previous) quest is not laborquest
	}
	protected int fairPX(Clan doer, double flow) {
		double TechPX = (STAvg + LTAvg + 2 * ((LastPX - STAvg) * doer.useBeh(M_.STMOMENTUM) + 
				(LastPX - LTAvg) * doer.useBeh(M_.LTMOMENTUM)) / 15) / 2;
		//if(bidlen + offerlen == 0) {flow = TechPX;}  //first disable TechPX if there has been no volume
		int C = doer.useBeh(M_.CONFIDENCE);
		int T = doer.useBeh(M_.TECHNICAL) + C;  int F = doer.useBeh(M_.FLOW) + 15 - C;
		int PX = Calc.AtoBbyRatio(TechPX, flow, T, T+F);
		int min = Assets.FVmin(doer, g);   int max = Assets.FVmax(doer, g);
//		report += doer.getNomen() + " estimates fair price for " + Naming.goodName(g) + " in following manner:" + RET;
//		report += PX + " = [(Flow=" + flow + ")*" + F + " + " + "(Tech=" + TechPX + ")*" + T + "] / " + (T+F);
//		report += (min == 0 && max == Integer.MAX_VALUE ? "" : ", bounded between " + min + " and " + max) + RET;
		return Math.min(Math.max(PX, min), max);
	}
	protected int estFairOffer(Clan doer) {
		int bestoffer = (offerlen > 0 ? bestOffer() : offerFromNowhere(doer));
		double FlowPX = addSpread(bestoffer, imbalance()*RATES[doer.useBeh(M_.BIDASKSPRD)]);
		return fairPX(doer, FlowPX);
	}
	protected int estFairBid(Clan doer) {
		int bestbid = (bidlen > 0 ? bestBid() : bidFromNowhere(doer));
		double FlowPX = addSpread(bestbid, imbalance()*RATES[doer.useBeh(M_.BIDASKSPRD)]);
		return Math.min(fairPX(doer, FlowPX), doer.getMillet());
	}
	//buy inputs at estFairOffer or market offer
	//sell outputs at estFairOffer
	//liquidate at estFairBid
	
	protected int bidFromNowhere(Clan doer) {
		int fear = 15 - doer.useBeh(M_.PARANOIA);
		int bo = bestOffer();
		if(bo > MinPX) {return Calc.AtoBbyRatio(bo, MinPX, fear, 15);}
		else {return Calc.AtoBbyRatio(MinPX, bo, fear, 15);}
		//return Calc.roundy((Math.min(MinPX, bestOffer()) * risk + NOASK * (15-risk)) / 15);
	}
	protected int offerFromNowhere(Clan doer) {
		int fear = 15 - doer.useBeh(M_.PARANOIA);
		int bb = bestBid();
		if(bb < MaxPX) {return Calc.AtoBbyRatio(bb, MaxPX, fear, 15);}
		else {return Calc.AtoBbyRatio(MaxPX, bb, fear, 15);}
		//return Math.max(MaxPX, bestBid());
	}
	
	protected double imbalance() {
		//numbids vs numasks ... may be better to measure value than volume
		if(bidlen + offerlen == 0) {return 0;}
		else {return 2 * bidlen / (bidlen + offerlen) - 1;}
	}
	
	public void removeBid(int plc){
		for(int i = plc; i < bidlen; i++) {Bids[i].set(Bids[i+1]);}
		bidlenDown();
	}
	public void removeBid(Clan trader){
		int k = findBid(trader);
		if(k != -1) {removeBid(k);}
		else {Log.warning(trader.getNomen() + " ERROR removeBid(ENTRY NOT FOUND)");}
	}
	public void removeOffer(int plc){
		//loseAsset(Offers[plc].trader);   ALREADY IN TRANSACTION!
		for(int i = plc; i < offerlen; i++) {Offers[i].set(Offers[i+1]);}
		offerlenDown();
	}
	public void removeOffer(Clan trader){
		int k = findOffer(trader);
		if(k != -1) {removeOffer(k);}
		else {Log.warning(trader.getNomen() + " ERROR removeOffer(ENTRY NOT FOUND)");}
	}

	public void gainAsset(Clan gainer) {
		if(g < numAssets) {
			report += gainer.getNomen() + " gains " + Naming.goodName(g) + RET;
//			Log.info(gainer.getNomen() + " gains " + Naming.goodName(g));
			Assets.gain(gainer, g);
		}
	}
	public void loseAsset(Clan loser) {
		if(g < numAssets) {
			report += loser.getNomen() + " loses " + Naming.goodName(g) + RET;
//			Log.info(loser.getNomen() + " loses " + Naming.goodName(g, false, false));
			Assets.lose(loser, g);
		}
	}
	protected int findBid(Clan doer) {
		for(int i = 0; i < bidlen; i++) {
			if(Bids[i].trader.equals(doer)) {return i;}
		}  return -1;
	}
	protected int findOffer(Clan doer) {
		for(int i = 0; i < offerlen; i++) {
			if(Offers[i].trader.equals(doer)) {return i;}
		}  return -1;
	}

	protected void placeBid(Clan doer, int px){
		if(px<0){px = 1/0;}
		doer.addReport(GobLog.limitOrder(g, px, true));
		report += doer.getNomen() + " places bid for " + Naming.goodName(g) + " at " + px + RET;
		if(px >= bestOffer()) {liftOffer(doer); return;}
		int k = findPlcInV(px, Bids, bidlen, -1);
		bidlenUp();
		for(int i = bidlen; i > k; i--) {Bids[i].set(Bids[i-1]);}
		Bids[k].set(px, doer);
		finish();
//		Log.info(doer.getNomen() + " places bid for " + Naming.goodName(this.g, false, false) + " at " + px);
	}
	public void placeOffer(Clan doer, int px){
		if(px<NOBID){px = 1/0;}
		doer.addReport(GobLog.limitOrder(g, px, false));
		report += doer.getNomen() + " places offer for " + Naming.goodName(g) + " at " + px + RET;
		int bbp = bestBidPlc();
		if(bidlen > 0 && bbp >= NOBID && px <= Bids[bbp].px) {hitBid(doer); return;}
		int k = findPlcInV(px, Offers, offerlen, 1);
		offerlenUp();
		for(int i = offerlen; i > k; i--) {Offers[i].set(Offers[i-1]);}
		Offers[k].set(px, doer);
		finish();
//		Log.info(doer.getNomen() + " places offer for " + Naming.goodName(g) + " at " + px);
	}
	protected final void bidlenDown() {bidlen--;}
	protected final void bidlenUp() {
		if (bidlen++ > Bids.length*expandSZ) {
			Entry[] tmp = new Entry[2*bidlen];
			System.arraycopy(Bids,0,tmp,0,Bids.length);
			for(int i = Bids.length; i < tmp.length; i++) {tmp[i] = new Entry();}
			Bids = tmp;
		}
	}
	protected final void offerlenDown() {offerlen--;}
	protected final void offerlenUp() {
		if (offerlen++ > Offers.length*expandSZ) {
			Entry[] tmp = new Entry[2*offerlen];
			System.arraycopy(Offers,0,tmp,0,Offers.length);
			for(int i = Offers.length; i < tmp.length; i++) {tmp[i] = new Entry();}
			Offers = tmp;
		}
	}
	protected int vchg(int plc, int px, Entry[] V, int dir) {
		int newplc = findPlcInV(px, V, (dir>0?offerlen:bidlen), dir);
		Clan oldie = V[plc].trader;
		if(newplc < plc) {
			for(int i = plc; i > newplc; i--) {V[i].set(V[i-1]);}
			V[newplc].set(px, oldie);
		}
		else if (newplc > plc) {
			for(int i = plc; i < newplc; i++) {V[i].set(V[i+1]);}
			V[newplc-1].set(px, oldie);
		}
		return newplc;
	}
	protected int chgEntry(int plc, int px, Entry[] bidorask, int dir) {return vchg(plc, px, bidorask, dir);}
	protected int chgBid(int plc, int px) {return vchg(plc, px, Bids, Entry.BIDDIR);}
	protected int chgOffer(int plc, int px) {return vchg(plc, px, Offers, Entry.OFFERDIR);}
	public void auction() {
		for (int i = 0; i < bidlen; i++) {if(Bids[i].trader!=null){chgBid(i, estFairBid(Bids[i].trader));}}
		for (int i = 0; i < offerlen; i++) {if(Offers[i].trader!=null){chgOffer(i, estFairOffer(Offers[i].trader));}}
		clearMarket();   //clear mkt!!!!!
	}
	protected void clearMarket() {}   //dont forget to clear bids&offers from same trader (who isnt market maker)
	protected int addSpread(int px, double s) {return (int) Math.round((double)px * (1+s));}
	
	public String[][] getBaikai() {
		String[][] B = new String[1+offerlen+bidlen][3];
		B[0][0] = "trader";B[0][1] = "px";B[0][2] = "trader";
		int k = 1;
		for (int i = offerlen-1; i>=0; i--) {
			B[k][0] = (Offers[i].trader!=null ? ""+ Offers[i].trader.getNomen() : "");
			B[k][1] = (Offers[i].px != -1 ? ""+ Offers[i].px : "");
			B[k++][2] = "";
		}
		for (int i = 0; i<bidlen; i++) {
			B[k][0] = "";
			B[k][1] = (Bids[i].px != -1 ? ""+ Bids[i].px : "");
			B[k++][2] = (Bids[i].trader!=null ? ""+ Bids[i].trader.getNomen() : "");
		}
		return B;
	}
	public void printBaikai() {
		System.out.println(Naming.goodName(g) + " BAIKAI");
		System.out.println("Offers: "+offerlen);
		System.out.println("Bids: "+bidlen);
		Calc.printArray(getBaikai());
	}
	
	protected static int findPlcInV(int x, Entry[] V, int Vlen, int dir) {
		if(Vlen==0){return 0;}
		// dir+ for asks, dir- for bids
		//int dir = (int)Math.signum(V[Vlen-1].px - V[0].px);
		int lo = 0; int hi = Vlen - 1;
		int mid; int midpx;  int cur = -1;
		while (true) {
			midpx = V[mid =(lo+hi)/2].px;
			if(cur==mid) {break;} else {cur = mid;}
			if (dir*x < dir*midpx) {hi = cur;}
			else if (dir*x > dir*midpx) {lo = cur;}
		}
		while (dir*V[cur].px <= dir*x) {cur++; if(cur==0||cur==Vlen){break;}}
		return cur;
	}
	
	/*
	 * use when string of market actions is finished to get complete detailed briefing
	 */
	public void finish() {
		//Log.info(report + RET);
		report = "";
	}

}

