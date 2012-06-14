package Markets;

import Descriptions.Naming;
import Game.*;
import Sentiens.Clan;
import Shirage.Shire;


public class RentMarket extends MktO {
	private int bestplc; //place of first unused offer

	
	public RentMarket(int ggg, Shire h) {super(ggg, h);} 
	
	private void refresh() {
		for (int i = 0; i < offerlen; i++) {Offers[i].makeUnrented();} //reclaim
		bestplc = 0;
	}
	
	public int bestOffer() {
		if (offerlen == 0) {return NOASK;}
		else {return Offers[bestplc].px;}
	}
	
	protected void hitBid(Clan seller, int plc) {
		report += seller.getNomen() + " tries to hit bid for " + Naming.goodName(g) + RET;
		if (plc != -1 && transaction(Bids[plc].trader, seller, Bids[plc].px)) {
			removeBid(plc);   //no loss of asset
		} else {sellFair(seller);}
	}
	
	public void placeOffer(Clan doer, int px){
		 //must recalculate "best", not sure if working
		super.placeOffer(doer, px);
		
	}
	
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
	public void liftOffer(Clan buyer) {
		report += buyer.getNomen() + " tries to lift offer for " + Naming.goodName(g) + RET;
		if (unrentedLeft() && transaction(buyer, Offers[bestplc].trader, Offers[bestplc].px)) {
			Offers[bestplc].makeRented(); //designate as used
			findNextUnused();
		}   
		else {buyFair(buyer);}   ///????
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
	
	private void findNextUnused() {
		while (bestplc < Offers.length) {
			if (Offers[bestplc].isRented()) {bestplc++;}
			else {return;}
		}
	}
	
	protected void clearMarket() {
		//rent offers dont remove them
		
		refresh();
	}

}
