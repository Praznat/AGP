package GUI;

import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import javax.swing.JPanel;
import javax.swing.JScrollPane;

public class APanel extends JPanel implements MouseListener, MouseMotionListener {
	
	protected GUImain parent;
	protected int tmpX, tmpY;
	
	public APanel(GUImain P) {
		super();
		parent = P;
		addMouseListeners();
	}
	
	
	protected void addMouseListeners() {
		addMouseMotionListener(this);
		addMouseListener(this);
	}
	
	protected void dragged(MouseEvent e) {
		parent.movePopup(this, e.getXOnScreen() - tmpX, e.getYOnScreen() - tmpY);
	}

	protected void pressed(MouseEvent e) {
		tmpX = e.getXOnScreen() - getX();   tmpY = e.getYOnScreen() - getY();
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		dragged(e);
	}
	
	@Override
	public void mousePressed(MouseEvent e) {
		pressed(e);
	}

	@Override
	public void mouseExited(MouseEvent e) {}
	
	@Override
	public void mouseMoved(MouseEvent arg0) {}


	@Override
	public void mouseClicked(MouseEvent arg0) {}


	@Override
	public void mouseEntered(MouseEvent arg0) {}


	@Override
	public void mouseReleased(MouseEvent arg0) {}

}