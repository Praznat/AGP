package GUI.TextDisplay;

import java.awt.Dimension;
import java.awt.Graphics;

import GUI.PopupAbstract;
import Game.AGPmain;
import Sentiens.Clan;


public class MemScroll extends Papyrus {
	public MemScroll(PopupAbstract P, int r) {
		super(P); setRef(r);
	}
	public void paint(Graphics g) {
		Clan clan = clan();
		if(clan==null){return;}
		super.paint(g);
		int w = getWidth();   int h = getHeight();
		String name = (AGPmain.TheRealm.getMem(reference) == null ? "" :
			AGPmain.TheRealm.getMem(reference).getName());
		g.drawString(name, 2, h-3);
		g.drawRect(0, 0, w*31/32, h);
		g.fillRect(w/2 + w*clan.FB.getBeh(reference)/32, 1, 3, h-1);
	}

	public void calcRealizedSize() {
		wid = getWidth();
		hgt = BHGT;
	}
}

