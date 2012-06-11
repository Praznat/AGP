package GUI;
import AMath.Calc;
import Game.AGPmain;


import javax.swing.*;

public class GUImain extends JFrame {
	private static final int WWID = 600;
	private static final int WHGT = 800;
	private JPanel MainPanel;
	public MapDisplay MD;
	public PopupFace GM;
	public PopupShire SM;
	private static final double MW = 0.4;
	private static final double MH = 0.85;
	public GUImain(String title) {
		super(title);
		MainPanel = new JPanel();
		this.getLayeredPane().add(MainPanel, new Integer(0));
	    this.setSize(WWID, WHGT);
	    this.setVisible(true);
	    System.out.println();
	}

	public void repaintEverything() {
		GM.loadClan();   SM.loadShire();
		//validate();
	}
	
	public void initializeMD() {
		MD = new MapDisplay();   MD.introduce();
		this.getLayeredPane().add(MD, new Integer(0));
		MD.setBounds(0, 0, getContentPane().getWidth(), getContentPane().getHeight());
		MD.createMap(5000, 5000); //5000
		MD.setVisible(true);
	}

	public void initializeGM() {
		GM = new PopupFace(this);
		this.getLayeredPane().add(GM, new Integer(2));
		int MWID = getContentPane().getWidth();
		int MHGT = getContentPane().getHeight();
		GM.setBounds((int)((1-MW)*MWID/7), (int)((1-MH)*MHGT/2), (int)(MW*MWID),  (int)(MH*MHGT));
		GM.setVisible(true);
	}
	public void initializeSM() {
		SM = new PopupShire(this);
		this.getLayeredPane().add(SM, new Integer(1));
		int MWID = getContentPane().getWidth();
		int MHGT = getContentPane().getHeight();
		SM.setBounds((int)((1-MW)*MWID*6/7), (int)((1-MH)*MHGT/2), (int)(MW*MWID),  (int)(MH*MHGT));
		SM.setVisible(true);
	}
	public void movePopup(PopupAbstract P, int x, int y) {
		P.setLocation(Calc.bound(x, 0, (int)(getContentPane().getWidth()*(1-MW))),
				Calc.bound(y, 0, (int)(getContentPane().getHeight()*(1-MH))));
	}
}
