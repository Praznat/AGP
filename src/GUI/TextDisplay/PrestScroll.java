package GUI.TextDisplay;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;

import Defs.P_;
import Descriptions.Naming;
import GUI.PopupAbstract;
import Sentiens.Clan;




public class PrestScroll extends Papyrus {
	private final Color FILLCOL = new Color(50,150,255);
	private final P_ refP;
	public PrestScroll(PopupAbstract P, int r) {
		super(P); refP = P_.values()[r];   //setRef(r);
	}
	public void paint(Graphics g) {
		Clan clan = clan();
		if(clan==null){return;}
		super.paint(g);
		int w = getWidth();   int h = getHeight();
		g.drawString(Naming.prestName(refP), 2, h-3);
		g.drawRect(0, 0, w*31/32, h);
		g.setColor(FILLCOL);
		g.fillRect(w/2, 1, w*clan.FB.getPrs(refP)/32, h-1);
	}
	public void calcRealizedSize() {
		wid = getWidth();
		hgt = BHGT;
	}
}