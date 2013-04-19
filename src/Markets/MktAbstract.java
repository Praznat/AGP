package Markets;

import java.util.logging.Logger;

import Game.*;
import Sentiens.Clan;
import Shirage.Shire;

public abstract class MktAbstract implements Defs {
	
	protected int g, smavol, todayvol;
	protected Shire home;
	
	public abstract int lastPrice();
	public abstract int stAvg();
	public abstract int ltAvg();
	public abstract int getBidSz();
	public abstract int getAskSz();
	public abstract int bestOffer();
	public abstract int bestBid();
	public int smaVol() {return smavol;}
	public abstract int sellablePX(Clan c);  //used in Logic
	public abstract int buyablePX(Clan c);  //used in Logic
	public abstract void buyFair(Clan buyer);
	public abstract void sellFair(Clan seller);
	public abstract void liftOffer(Clan buyer);
	public abstract void hitBid(Clan seller);
	public abstract void removeBid(int plc);
	public abstract void removeOffer(int plc);
	public abstract void chgOffer(int plc, int v);
	public abstract void chgBid(int plc, int v);
	protected abstract void auction();
	protected abstract void clearMarket();
	
	public void newDay() {
		clearMarket();
		smavol = (smavol + todayvol) / 2;
		todayvol = 0;
	}
	
	protected final Logger Log = Logger.getLogger(this.getClass().getName());

	public abstract void loseAsset(Clan me);
	public abstract void gainAsset(Clan me);


	
	
}