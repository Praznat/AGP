package GUI.TextDisplay;

import java.awt.Graphics;

import Defs.M_;
import Descriptions.BehName;
import GUI.PopupAbstract;
import Sentiens.*;
import Sentiens.Law.Commandment;


public class MemScroll extends Papyrus {
	public MemScroll(PopupAbstract P) {
		super(P);
	}
	public void paint(Graphics g) {
		Clan clan = clan();
		if(clan==null){return;}
		super.paint(g);
		int r = 0; String s;
		for (M_ m : M_.values()) {
			s = BehName.describeMem(clan, m);
			if (s != "") {g.drawString(s, 2, 15+15*r++);}
		}
		r++;
		for (Commandment c : clan.FB.commandments.list) {
			g.drawString(c.toString(), 2, 15+15*r++);
		}
	}
}

