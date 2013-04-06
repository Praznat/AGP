package GUI.TextDisplay;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;

import AMath.Calc;
import GUI.PopupAbstract;
import GUI.PopupShire;
import Game.AGPmain;
import Game.VarGetter;
import Sentiens.Clan;
import Shirage.Shire;


public class ClanRowScroll extends TableRowScroll {
	
	private Clan[] pop;
	
	public ClanRowScroll(PopupAbstract P) {
		super(P);
		numCols = ((PopupShire) parent).selectedVGLength(PopupShire.POPULATION);
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
		if (g != null) {paintComponents(g);}
	}

	
	public void calcRealizedSize() {
		pop = ((PopupShire)parent).getShire().getCensus();
		if (pop == null) {System.out.println("NO DAMN POP!");}
		if (vars() == null) {System.out.println("NO DAMN VARS!");}
		setupTableLabels(PopupShire.POPULATION, pop.length, vars());
		String S;
		for (int r = 1; r < contents.length; r++) {
			String[] R = contents[r];
			Clan clan = pop[r-1];
			for (int col = 0; col < R.length; col++) {
				int v = ((PopupShire) parent).getVG(PopupShire.POPULATION, col);
				S = var(v).getVarString(clan) + " ";
				R[col] = S;
				adjustWidth(col, S);
			}
		}
		calcSize();
	}

	protected static VarGetter var(int i) {return AGPmain.TheRealm.getPopVarGetter(i);}
	protected static VarGetter[] vars() {return AGPmain.TheRealm.getPopVarGetters();}
	
}

