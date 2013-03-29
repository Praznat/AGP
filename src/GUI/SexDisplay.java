package GUI;

import java.awt.*;
import java.awt.event.MouseEvent;

import javax.swing.JFrame;

import AMath.Calc;
import Game.AGPmain;
import Sentiens.*;

public class SexDisplay {
	static final int CSIZE = 200;
	static final int WIDTH = CSIZE * 3;
	static final int HEIGHT = CSIZE;

	final JFrame MainPane;
	final Face father, mother, spawn;
	final Calc.MousePanel panel = new Calc.MousePanel() {
		@Override
		public void mouseClicked(MouseEvent arg0) {load();}
	};
	
	public SexDisplay() {
		MainPane = new JFrame("Family");
		father = new Face();
		mother = new Face();
		spawn = new Face();

		MainPane.setContentPane(panel);
		MainPane.getContentPane().setLayout(new GridLayout(1, 3));
		MainPane.getContentPane().add(father);
		MainPane.getContentPane().add(spawn);
		MainPane.getContentPane().add(mother);
		father.setPreferredSize(new Dimension(CSIZE, CSIZE));
		mother.setPreferredSize(new Dimension(CSIZE, CSIZE));
		spawn.setPreferredSize(new Dimension(CSIZE, CSIZE));
		MainPane.setSize(WIDTH,HEIGHT);
		MainPane.setVisible(true);
		load();
	}
	
	public void load() {
		final Clan father = AGPmain.mainGUI.AC.getAvatar();
		final Clan mother = AGPmain.mainGUI.GM.getClan();
		if (father == null || mother == null) return;
		load(father, mother);
	}
	
	public void load(Clan f, Clan m) {
		father.redefine(f);
		mother.redefine(m);
		final Ideology spawnFb = new Ideology();
		f.createHeir(spawnFb, m.FB.copyFs());
		spawn.redefine(spawnFb);
	}
}
