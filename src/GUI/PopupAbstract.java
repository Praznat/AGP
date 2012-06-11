package GUI;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import javax.swing.BorderFactory;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import GUI.TextDisplay.NameScroll;
import GUI.TextDisplay.Papyrus;

public class PopupAbstract extends APanel implements MouseListener, MouseMotionListener {

	protected GUImain parent;
	protected int tmpX, tmpY;
	protected NameScroll namebox;
	protected JPanel info;
	protected JComboBox cb;
	protected AconSelector slider;
	protected int curTab;
	protected boolean initialized;
	
	
	public PopupAbstract(GUImain P) {
		parent = P;
		namebox = new NameScroll(this, 12);
		add(namebox);
		setOpaque(false);
		info = new JPanel(new CardLayout());
		slider = new AconSelector(this);
		add(slider);
		cb = new JComboBox();
		add(cb);
		cb.setEditable(false);
		sp = new JScrollPane(info, JScrollPane.VERTICAL_SCROLLBAR_NEVER, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		add(sp);
		setBorder(BorderFactory.createLineBorder(Papyrus.BGCOL));
		setLayout(null);
		addMouseMotionListener(this);
		addMouseListener(this);
	}
	
	

	public void setBounds(int x, int y, int w, int h) {
		super.setBounds(x,y,w,h);
		namebox.setBounds(0,0,w,22);
	}

    public void scrollToPoint(Point P) {
    	ASlidePanel ASP = (ASlidePanel)info.getComponent(curTab);
    	P.setLocation(Math.max(Math.min(P.getX(),ASP.maxWidth() - sp.getViewport().getWidth()),0),
    			Math.max(Math.min(P.getY(),ASP.maxHeight() - sp.getViewport().getHeight()),0));
    	sp.getViewport().setViewPosition(P);
    	//System.out.print(P.getX()+","+P.getY());
    	//System.out.print("	" + ASP.maxWidth());
    	//System.out.print("	" + sp.getViewport().getWidth());
    	//System.out.print("	" + ASP.maxHeight());
    	//System.out.println("	" + sp.getViewport().getHeight());
    }
    
    public Dimension getViewportSize() {return sp.getViewport().getExtentSize();} //getViewSize() ??
    
    public void refreshAll() {
		info.validate();
		scrollToPoint(new Point(0,0));
		repaint();
	} 
    public void setState(String S, int i) {
    	curTab = i;
		CardLayout cl = (CardLayout)(info.getLayout());
		cl.show(info, S);
		refreshAll();
	} 
    public boolean initialized() {return initialized;}
	public void mouseMoved(MouseEvent e) {}
    public void mouseDragged(MouseEvent e) {
    	parent.movePopup(this, e.getXOnScreen() - tmpX, e.getYOnScreen() - tmpY);
    }
    public void mouseExited(MouseEvent e) {}
    public void mousePressed(MouseEvent e) {
    	tmpX = e.getXOnScreen() - getX();   tmpY = e.getYOnScreen() - getY();
    }
	public void mouseReleased(MouseEvent e) {}
    public void mouseEntered(MouseEvent e) {}
    public void mouseClicked(MouseEvent e) {}
}
