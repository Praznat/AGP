package GUI.TextDisplay;

import java.awt.Graphics;

import GUI.PopupAbstract;
import Questing.Quest;
import Sentiens.Clan;
import Sentiens.GobLog.Reportable;

public class QuestScroll extends Papyrus {
	private static final int REPORTSTART = 10;
	public QuestScroll(PopupAbstract P) {
		super(P);
	}
	public void paint(Graphics g) {
		Clan clan = clan();
		if(clan==null){return;}
		super.paint(g);
		int r = 0;
		String S;
		for(Quest q : clan.MB.QuestStack) { //reverse order?
			g.drawString(S = q.description(), 2, BHGT+BHGT*r++);
			refreshWid(g, S);
		}
		r = REPORTSTART;
		g.drawString("Recent actions:", 2, BHGT+BHGT*r++);
		Reportable[] report = clan.getLog();
		for (Reportable R : report) {
			S = R.out();
			if (S != "") {g.drawString(S, 2, BHGT+BHGT*r++);}
		}
	}
	
	@Override
	public void calcRealizedSize() {
		if (clan() == null) {super.calcRealizedSize(); return;}
		wid = getWidth();
		hgt = BHGT * (REPORTSTART + clan().getLog().length + 1);
	}
	
	
}