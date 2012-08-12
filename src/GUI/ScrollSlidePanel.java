package GUI;

import javax.swing.BoxLayout;

import GUI.TextDisplay.Papyrus;

public class ScrollSlidePanel extends ASlidePanel {
	
	public ScrollSlidePanel(PopupAbstract P, Papyrus[] S) {
		super(P);
		scroll = S;
		//setScrolls();
	}
	public void redefineClan() {
		setScrolls();
	}

	
}