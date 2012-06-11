package GUI;
import java.awt.*;
import java.awt.event.*; 
import javax.swing.*;

import GUI.TextDisplay.NameScroll;
import GUI.TextDisplay.Papyrus;
import Game.AGPmain;
import Sentiens.Clan;



public class PopupFace extends PopupAbstract {
	private Clan curClan;
	private Face portrait;
	private ScrollSlidePanel infobox1, infobox2, infobox3, infobox4, infobox5, infobox6;
	public final static String INFO1 = "BASIC INFO";
	public final static String INFO2 = "VALUES";
	public final static String INFO3 = "SKILLS & PRESTIGE";
	public final static String INFO4 = "BEHAVIORAL TENDENCIES";
	public final static String INFO5 = "QUEST";
	public final static String INFO6 = "ORDER";
	

	
	public PopupFace(GUImain P) {
		super(P);
		portrait = new Face();
		add(portrait);
		cb = new JComboBox(new String[] {INFO1, INFO2, INFO3, INFO4, INFO5, INFO6}); 
		infobox1 = new ScrollSlidePanel(this, Papyrus.basicS(this));
		infobox2 = new ScrollSlidePanel(this, Papyrus.sancS(this));
		infobox3 = new ScrollSlidePanel(this, Papyrus.prestS(this));
		infobox4 = new ScrollSlidePanel(this, Papyrus.behS(this));
		infobox5 = new ScrollSlidePanel(this, Papyrus.questS(this));
		infobox6 = new ScrollSlidePanel(this, Papyrus.prestS(this));
		info.add(infobox1, INFO1);
		info.add(infobox2, INFO2);
		info.add(infobox3, INFO3);
		info.add(infobox4, INFO4);
		info.add(infobox5, INFO5);
		info.add(infobox6, INFO6);
		slider.addCon(INFO1);
		slider.addCon(INFO2);
		slider.addCon(INFO3);
		slider.addCon(INFO4);
		slider.addCon(INFO5);
		slider.addCon(INFO6);
	}
	public void setBounds(int x, int y, int w, int h) {
		super.setBounds(x,y,w,h);
		portrait.setBounds(0,namebox.getHeight(), 2*w/3, 2*w/3);
		h = portrait.getY()+portrait.getHeight();
		slider.setBounds(0,h, w, 25);
		h = slider.getY()+slider.getHeight();
		sp.setBounds(0, h, w, getHeight()-h);
		slider.refresh();
	}
	

	public Clan getClan() {return curClan;}

    public void loadClan() {if (curClan != null) {loadClan(curClan);}}
    public void loadClan(Clan c) {
    	curClan = c;
    	namebox.setNomen(curClan.getNomen());
    	portrait.redefine(curClan);
    	infobox1.redefineClan();
    	infobox2.redefineClan();
    	infobox3.redefineClan();
    	infobox4.redefineClan();
    	infobox5.redefineClan();
    	infobox6.redefineClan();
    	initialized = true;
    }
	

    public void mouseClicked(MouseEvent e) {
    	AGPmain.TheRealm.goOnce();
    	loadClan(AGPmain.TheRealm.getRandClan());
    	AGPmain.mainGUI.SM.loadShire();
    }
}
