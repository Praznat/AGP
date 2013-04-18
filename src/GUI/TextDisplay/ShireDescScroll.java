package GUI.TextDisplay;

import java.awt.Graphics;

import GUI.*;
import Game.Library;
import Questing.KnowledgeQuests.KnowledgeBlock;
import Shirage.Shire;


public class ShireDescScroll extends Papyrus {	
	public ShireDescScroll(PopupAbstract P) {
		super(P);
	}
	public void paint(Graphics g) {
		final Shire shire = ((PopupShire)parent).getShire();
		if(shire==null){return;}
		final Library library = shire.getLibrary();
		super.paint(g);
		int i = 0;
		g.drawString("Knowledge", 2, 15+15*i++);
		for (; i < library.getCapacity(); i++) {
			@SuppressWarnings("rawtypes")
			final KnowledgeBlock kb = library.getKnowledge(i);
			if (library.getKnowledge(i) == null) {break;}
			g.drawString(kb.toString(), 2, 15+15*i);
		}
	}

}
