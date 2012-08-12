package GUI.TextDisplay;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;

import Descriptions.Naming;
import Descriptions.XWeapon;
import GUI.PopupAbstract;
import Game.*;
import Sentiens.Clan;

public class DiscScroll extends Papyrus {
	private ClickableString homelandCS;
	
	public DiscScroll(PopupAbstract P) {
		super(P);
	}
	public void paint(Graphics g) {
		if(clan()==null){return;}
		Clan clan = clan();
		super.paint(g);
		int r = 0;
		write(g, clan.getNomen(), r++); //convert all g.drawStrings to write
		g.drawString(" the " + clan.getJob().getDesc(), 2, 15+15*r++);
		addToClickStrings(clan.myShire(), 20, 15+15*r, g);
		g.drawString(" of ", 2, 15+15*r++);
		if (clan.FB.getDisc(Defs.HOMELAND) != clan.myShire().getXY()) {
			g.drawString(" resident of " + clan.myShire().getName(), 2, 15+15*r++);
		}
		if(clan.FB.getDisc(Defs.LORD) != clan.getID()) {
			g.drawString(" follower of " + clan.FB.getDiscName(Defs.LORD), 2, 15+15*r++);
		}
		g.drawString(" believer in the " + clan.FB.getDiscName(Defs.CREED), 2, 15+15*r++);

		g.drawString("Age: " + clan.getAge(), 2, 15+15*r++);
		r++;r++;
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