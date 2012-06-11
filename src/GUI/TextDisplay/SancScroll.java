package GUI.TextDisplay;

import java.awt.Dimension;
import java.awt.Graphics;

import GUI.PopupAbstract;
import Game.AGPmain;
import Sentiens.Clan;
import Sentiens.Ideology;


public class SancScroll extends Papyrus {	
	int[] sncs = new int[Ideology.FSM[0].length];
	double[] pcts = new double[Ideology.FSM[0].length];
	int c;
	public SancScroll(PopupAbstract P) {
		super(P);
	}
	public void paint(Graphics g) {
		Clan clan = clan();
		if(clan==null){return;}
		super.paint(g);
		c = clan.FB.getSancPcts(sncs, pcts);
		for (int i = 0; i < c; i++) {
			g.drawString(pcts[i]+"%   " + AGPmain.TheRealm.getSanc(
					sncs[i]).description(clan), 2, 15+15*i);
		}
	}

}
