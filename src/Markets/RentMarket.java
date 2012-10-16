package Markets;

import AMath.Calc;
import Defs.M_;
import Descriptions.Naming;
import Game.*;
import Questing.Quest;
import Questing.WorkQuests.LaborQuest;
import Sentiens.Clan;
import Shirage.Shire;


public class RentMarket extends MktO {
	private int bestplc = offerlen; //place of first unused offer

	
	public RentMarket(int ggg, Shire h) {super(ggg, h);} 
	
	private void refresh() {
		for (int i = 0; i < offerlen; i++) {Offers[i].makeUnrented();} //reclaim
		bestplc = 0;
	}
	
	public int bestOffer() {
		if (!unrentedLeft()) {return NOASK;}
		else {return Offers[bestplc].px;}
	}
	
	@Override
	protected int estFairOffer(Clan doer) {
		int bestoffer = (unrentedLeft() ? bestOffer() : offerFromNowhere(doer));
		double FlowPX = addSpread(bestoffer, imbalance()*RATES[doer.useBeh(M_.BIDASKSPRD)]);
		return fairPX(doer, FlowPX);
	}
	@Override
	protected void hitBid(Clan seller, int plc) {
		Entry bid = Bids[plc];
		int k = findPlcInV(bid.px, Offers, offerlen, Entry.OFFERDIR);
		offerlenUp();
		for(int i = offerlen; i > k; i--) {Offers[i].set(Offers[i-1]);}
		Offers[k].set(bid.px, seller);
		Offers[k].makeRented();
//		if (k == bestplc) {findNextUnused();}
		if (bid.trader == seller) {selfTransaction(seller, plc, Entry.BIDDIR); finish(); return;}
		report += seller.getNomen() + " tries to hit bid for " + Naming.goodName(g) + RET;
		int oldbidlen = bidlen;
		Clan tmpbuyer = bid.trader;
		if (plc != -1 && transaction(bid.trader, seller, bid.px)) {
			if (oldbidlen != bidlen) {
				Calc.p("transaction fucked up");
			}
			removeBid(plc);  //no loss of asset
		} else {sellFair(seller);}
	}
	
	public int placeOffer(Clan doer, int px){
		 //must recalculate "best", not sure if working
		int k = super.placeOffer(doer, px);
		bestplc = Math.min(bestplc, k);
		if (k == Integer.MAX_VALUE) {bestplc++;} // hit bid case
		finish();
		return k;
	}
	
	@Override
	public void sellFairAndRemoveBid(Clan seller) {}
	
	public int chgOffer(int plc, int v) {
		int oldplc = plc;
		int newplc = chgEntry(plc, v, Offers, Entry.OFFERDIR);
		if(oldplc < bestplc && bestplc <= newplc) {bestplc--;}
		else if(oldplc > bestplc && bestplc >= newplc) {bestplc++;}
		return v;
	}

	private boolean unrentedLeft() {
		return bestplc < offerlen;
	}
	@Override
	public void removeOffers(int num) { //only used in clear market
		int i; for (i = 0; i < num; i++) {
			Offers[i].makeRented();
		}
		bestplc = i;
	}
	@Override
	public void removeOffer(int plc){
		super.removeOffer(plc);
		if(plc == bestplc) {findNextUnused();}
		else if (plc < bestplc) {bestplc--;}
	}
	@Override
	public void liftOffer(Clan buyer) {
		if (bestplc != solveBestPlc()) {
			finish();
			Calc.p("Serious problem");
		}
		if (!unrentedLeft()) {buyFair(buyer); return;}
		Entry offer = Offers[bestplc];
		if (offer.trader == buyer) {selfTransaction(buyer, bestplc, Entry.OFFERDIR); finish(); return;}
		report += buyer.getNomen() + " tries to lift offer for " + Naming.goodName(g) + RET;
		if (transaction(buyer, offer.trader, offer.px)) {
			offer.makeRented(); //designate as used
			findNextUnused();
		} else {buyFair(buyer);}
	}
	
	@Override
	protected void selfTransaction(Clan clan, int plc, int bidorask) {
		if (bidorask == Entry.BIDDIR) {removeBid(plc);}
		else if (bidorask == Entry.OFFERDIR) {Offers[plc].makeRented(); findNextUnused();}
		else {throw new IllegalArgumentException();}
		report += clan.getNomen() + " takes own " + (bidorask == Entry.BIDDIR ? "bid" : "offer") + " of " + Naming.goodName(g) + " from market" + RET;
		getG(clan);
	}
	
	/**
	protected boolean transaction(Clan buyer, Clan seller, int price) {
		if(seller == null || buyer == null) {return false;}
		seller.alterMillet(price);
		if (!buyer.alterMillet(-price)) {seller.alterMillet(-price); return false;}
		sendToInventory(buyer); //    OK for rent market
		Log.info(buyer.getNomen() + " buys " + Naming.goodName(this.g, false, false) + " from " + seller.getNomen() + " for " + price);
		return true;
	}
	**/
	private int solveBestPlc() {
		int best = 0;
		while (best < offerlen) {
			if (Offers[best].isRented()) {best++;}
			else {break;}
		}
		return best;
	}
	private void findNextUnused() {
		while (bestplc < offerlen) {
			if (Offers[bestplc].isRented()) {bestplc++;}
			else {return;}
		}
	}
	@Override
	public void clearMarket() {
		//rent offers dont remove them
		refresh();
		auction();
	}
	
	@Override
	public String toString() {return bestplc + super.toString();}

}
