package GUI.TextDisplay;

import java.awt.Graphics;

import Descriptions.*;
import GUI.PopupAbstract;
import Game.Defs;
import Sentiens.Clan;

public class DiscScroll extends Papyrus {
	
	public DiscScroll(PopupAbstract P) {
		super(P);
	}
	public void paint(Graphics g) {
		if(clan()==null){return;}
		Clan clan = clan();
		super.paint(g);
		int r = 0;
		write(g, clan.getNomen(), r++); //convert all g.drawStrings to write
		g.drawString(" the " + clan.getJob().getDesc(clan), 2, 15+15*r++);
		addToClickStrings(clan.myShire(), 20, 15+15*r, g);
		g.drawString(" of ", 2, 15+15*r++);
		if(clan.getBoss() != clan) {
			g.drawString(" follower of " + clan.getBoss().getNomen(), 2, 15+15*r++);
		}
		g.drawString(" believer in the " + clan.FB.getDeusName(), 2, 15+15*r++);

		g.drawString("Age: " + clan.getAge(), 2, 15+15*r++);
		r++;r++;
		write(g, "Avg Profit:" + clan.getAvgIncome(), r++);
		write(g, "NAV:" + clan.getNetAssetValue(clan), r++);
		write(g, "ASSETS:", r++);
		for (int i = 0; i < Defs.numAssets; i++) {
			int n = clan.getAssets(i);
			if (n != 0) {write(g, n + " " + Naming.goodName(i, (n != 1), false), r++);}
		}
		r++;   write(g, "WEAPON:", r++);
		write(g, XWeapon.weaponName(clan.getXWeapon()), r++);
		//permanent possessions:
		

		paintComponents(g);
	}
	private void write(Graphics g, String S, int r) {
		g.drawString(S, 2, BHGT+BHGT*r++);
	}
	
}