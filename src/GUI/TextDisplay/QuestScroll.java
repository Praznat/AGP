package GUI.TextDisplay;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;

import Descriptions.Naming;
import GUI.PopupAbstract;
import Game.AGPmain;
import Game.Defs;
import Sentiens.Clan;
import Sentiens.Questy;
import Sentiens.GobLog.Reportable;

public class QuestScroll extends Papyrus {
	public QuestScroll(PopupAbstract P) {
		super(P);
	}
	public void paint(Graphics g) {
		Clan clan = clan();
		if(clan==null){return;}
		super.paint(g);
		int r = 0;
		String S;
		for(int i = 0; i < Questy.MEMORY; i++) {
			if(clan.QB.getQuest(i) == -1) {break;}
			g.drawString(S = clan.QB.getQuestDescription(i), 2, 15+15*r++);
			refreshWid(g, S);
		}   r++;
		if(clan.QB.getWM(0) != Defs.E) {g.drawString("Working goods:", 2, 15+15*r++);}
		for(int i = 0; i < Questy.WORKMEMORY; i++) {
			int good = clan.QB.getWM(i);   if(good == Defs.E) {break;}
			int num = clan.QB.getWMX(i);
			g.drawString(S = (num + " " + Naming.goodName(clan.QB.getWM(i), (num != 1), false)), 2, 15+15*r++);
			refreshWid(g, S);
		}   r++;
		g.drawString("Recent actions:", 2, 15+15*r++);
		Reportable[] report = clan.getLog();
		for (Reportable R : report) {
			g.drawString(R.out(), 2, 15+15*r++);
		}
	}
}