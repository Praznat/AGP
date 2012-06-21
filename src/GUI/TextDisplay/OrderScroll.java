package GUI.TextDisplay;

import java.awt.Graphics;
import java.util.Comparator;
import java.util.SortedSet;
import java.util.TreeSet;

import GUI.PopupAbstract;
import Sentiens.Clan;

public class OrderScroll extends Papyrus {
	private static final Comparator<Clan> NUMMINION_ORDER = new Comparator<Clan>() {
		@Override
		public int compare(Clan o1, Clan o2) {
			int s = (int)Math.signum(o1.getMinionNumber() - o2.getMinionNumber());
			if (s == 0) {return (int)Math.signum(o1.getID() - o2.getID());}
			else {return s;}
		}
	};
	private SortedSet<Clan> order = new TreeSet<Clan>(NUMMINION_ORDER);
	public OrderScroll(PopupAbstract P) {
		super(P);
	}
	public void paint(Graphics g) {
		Clan clan = clan();
		if(clan==null){return;}
		if(clan.myOrder() == null) {return;} //for now
		super.paint(g);
		int r = 0;
		String S;
		Clan ruler = clan().myOrder().getRuler();
		order.clear();
		order.addAll(clan().myOrder().getMembers());
		g.drawString(S = ruler.getNomen(), 2, BHGT+BHGT*r);
		g.drawString(S = ruler.getMinionNumber()+"", 20, BHGT+BHGT*r++);
		for(Clan c : order) {
			if (c == ruler) {continue;}
			g.drawString(S = c.getNomen(), 2, BHGT+BHGT*r);
			g.drawString(S = c.getMinionNumber()+"", 20, BHGT+BHGT*r++);
			refreshWid(g, S + 20);
		}
	}
	
	@Override
	public void calcRealizedSize() {
		if (clan() == null) {super.calcRealizedSize(); return;}
		if(clan().myOrder() == null) {return;}
		hgt = BHGT * clan().myOrder().size();
	}
	
	
}