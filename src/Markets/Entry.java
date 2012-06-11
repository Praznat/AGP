package Markets;

import Sentiens.Clan;

public class Entry {
	public static final int BIDDIR = -1;
	public static final int OFFERDIR = 1;
	public int px;
	public Clan trader;
	public Entry() {px = -1; trader = null;}
	public void set(int p, Clan t) {px = p; trader = t;}
	public void set(Entry e) {px = e.px; trader = e.trader;}
	public void setPX(int p) {px = p;}
	public void setTrader(Clan t) {trader = t;}

	public void makeRented() {setPX(-Math.abs(px));}
	public void makeUnrented() {setPX(Math.abs(px));}
	public boolean isRented() {return px < 0;}
}