package GUI.TextDisplay;

import java.awt.Graphics;
import java.util.Comparator;
import java.util.SortedSet;
import java.util.TreeSet;

import GUI.PopupAbstract;
import GUI.PopupShire;
import Game.AGPmain;
import Game.VarGetter;
import Sentiens.Clan;

public class OrderRowScroll extends TableRowScroll {
	
	private static final Comparator<Clan> NUMMINION_ORDER = new Comparator<Clan>() {
		@Override
		public int compare(Clan o1, Clan o2) {
			int s = (int)Math.signum(o2.getMinionNumber() - o1.getMinionNumber());
			if (s == 0) {return (int)Math.signum(o1.getID() - o2.getID());}
			else {return s;}
		}
	};
	private Clan[] pop;
	private SortedSet<Clan> order = new TreeSet<Clan>(NUMMINION_ORDER);
	private final String[] LABELS = new String[] {"Clan", "Minions", "Leader", "Shire"};
	
	public OrderRowScroll(PopupAbstract P) {
		super(P);
		numCols = LABELS.length;
	}
	

	public void paint(Graphics g) {
		super.paint(g);
		int x = 0;
		for (int col = 0; col < numCols; col++) {
			if (contents[0][col].equals("Name")) {
				g.drawString("Name", x + 2, BHGT+BHGT*0);
				for (int r = 1; r < contents.length; r++) {
					addToClickStrings(pop[r-1], x + 2, BHGT+BHGT*r, g);
				}
			}
			x += widths[col];
		}
		paintComponents(g);
	}

	
	public void calcRealizedSize() {
		Clan clan = clan();
		if (clan == null) {return;}
		boolean hasOrder = true;
		if (clan.myOrder() == null) {hasOrder = false;}
		if (hasOrder) {
			order.clear(); order.addAll(clan.myOrder().getMembers());
			pop = new Clan[order.size()];
			int i = 0; for (Clan o : order) {pop[i++] = o;}
		}
		else {pop = new Clan[] {clan};}
		setupTableLabels(PopupShire.POPULATION, pop.length, LABELS);
		for (int r = 1; r < contents.length; r++) {
			String[] R = contents[r];
			Clan c = pop[r-1];   int i = 0;
			R[i] = c.getNomen(); adjustWidth(i, R[i]); i++;
			R[i] = c.getMinionNumber()+""; adjustWidth(i, R[i]); i++;
			R[i] = c.getBoss().getNomen(); adjustWidth(i, R[i]); i++;
			R[i] = c.myShire().getName(); adjustWidth(i, R[i]); i++;
		}
		calcSize();
	}

	protected static VarGetter var(int i) {return AGPmain.TheRealm.getPopVarGetter(i);}
	protected static VarGetter[] vars() {return AGPmain.TheRealm.getPopVarGetters();}
	
}
