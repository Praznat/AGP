package GUI;

import java.awt.Component;
import java.awt.event.*;
import java.util.Set;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

import Game.AGPmain;
import Game.Do;



public class APopupMenu extends JPopupMenu {

	public APopupMenu(Component parent, Do.Thing... dothings) {
		for (Do.Thing D : dothings) {
			add(AMenuItem.createNew(D));
		}
		show(parent, parent.getMousePosition().x-5, parent.getMousePosition().y-5);
	}
	public APopupMenu(Component parent, Iterable<? extends Do.Thing> dothings) {
		for (Do.Thing D : dothings) {
			add(AMenuItem.createNew(D));
		}
		show(parent, parent.getMousePosition().x-5, parent.getMousePosition().y-5);
	}


}

class AMenuItem extends JMenuItem {

	public AMenuItem(String s) {super(s);}
	
	
	public static AMenuItem createNew(final Do.Thing A) {
		AMenuItem AMI = new AMenuItem(A.description());
		AMI.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {A.doit();   AGPmain.mainGUI.repaintEverything();}
		});
		return AMI;
	}

}