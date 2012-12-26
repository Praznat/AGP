package GUI;

import java.awt.Component;
import java.awt.event.*;
import java.util.Collection;

import javax.swing.*;

import AMath.Calc;
import Game.*;

public class APopupMenu extends JPopupMenu {

	public APopupMenu(Component parent, Collection<? extends Object> choices, Calc.Listener listener) {
		this(parent, choices.toArray(), listener);
	}
	public APopupMenu(Component parent, Object[] choices, Calc.Listener listener) {
		for (Object obj : choices) {
			add(AMenuItem.createNew(obj, listener));
		}
		int x, y;
		if (parent != null && parent.getMousePosition() != null) {
			x = parent.getMousePosition().x-5;
			y = parent.getMousePosition().y-5;
		}	else {x = 0; y = 0;}
		show(parent, x, y);
	}
}

class AMenuItem extends JMenuItem {

	public AMenuItem(String s) {super(s);}
	
	public static AMenuItem createNew(final Object obj, final Calc.Listener listener) {
		AMenuItem AMI = new AMenuItem(obj.toString());
		AMI.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {listener.call(obj);   AGPmain.mainGUI.repaintEverything();}
		});
		return AMI;
	}

}