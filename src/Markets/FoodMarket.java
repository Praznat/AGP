package Markets;

import Game.*;
import Sentiens.Clan;
import Shirage.Shire;

public class FoodMarket extends MktAbstract {
	public static final int MILLETVAL = 100;
	public static final int MEATVAL = 300;  //may not use
	private int val;

	public FoodMarket(int ggg, Shire h) {
		g = ggg;
		home = h;
		switch (ggg) {
		case Defs.millet: val = MILLETVAL; break;
		case Defs.meat: val = MEATVAL; break;
		default: val = 0; break;
		}
	} 
	



	public void gainAsset(Clan me) {} //nothing happens (called in Quest DoOutputs)
	private void sellFood(Clan seller) {
		Assets.gain(seller, g, val);
	}
	
	public void sellFair(Clan seller) {
		sellFood(seller);
	}
	public int sellablePX(Clan seller) {
		return val;
	}

	public void hitBid(Clan seller) {sellFood(seller);}
	protected void hitBid(Clan seller, int plc) {sellFood(seller);}
	
	
	
	private void error() {Log.warning("Attempting illegal action in Food Market for " + Naming.goodName(g, true, true));}
	
	public int lastPrice() {return val;}
	public int stAvg() {return val;}
	public int ltAvg() {return val;}
	protected void updateAvgs(int p) {}
	public int getBidSz() {return Integer.MAX_VALUE;}
	public int getAskSz() {return 0;}
	public int bestOffer() {return val;}
	public int bestBid() {return val;}
	public int buyablePX(Clan c) {error();return val;}
	public void buyFair(Clan buyer) {error();}
	public void liftOffer(Clan buyer) {error();}
	public void removeBid(int plc) {error();}
	public void removeOffer(int plc) {error();}
	public void removeOffer(Clan c) {error();}
	protected int chgOffer(int plc, int v) {error();return 0;}
	protected int chgBid(int plc, int v) {error();return 0;}
	public void auction() {} // do nothing
	protected void clearMarket() {error();}
	public void loseAsset(Clan me) {error();}
}
