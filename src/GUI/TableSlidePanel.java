package GUI;

import javax.swing.BoxLayout;

import AMath.Calc;
import GUI.TextDisplay.*;
import Game.AGPmain;
import Game.VarGetter;


public class TableSlidePanel extends ASlidePanel {
	VarGetter[] baseVG;
	int type;
	public TableSlidePanel(PopupAbstract P, int t) {
		super(P);
		type = t;
		if (parent().initialized()) {redefineShire();}
	}
	public void redefineShire() {
		if (type == PopupShire.POPULATION) {scroll = Papyrus.shirepopS(parent());}
		if (type == PopupShire.MARKETS) {scroll = Papyrus.shiremktS(parent());}
		else {scroll = Papyrus.shirepopS(parent());}
		setScrolls();
	}
}
