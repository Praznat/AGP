package GUI;

import java.awt.Point;

import javax.swing.JPanel;
import javax.swing.JScrollPane;

public class APanel extends JPanel {
	protected JScrollPane sp;
	protected ASlidePanel content;
	public APanel() {
		super();
	}
    public void scrollToPoint(Point P) {
    	//P.setLocation(Math.min(Math.max(P.getX(),0),content.maxWidth()-sp.getViewport().getWidth()),
    	//		Math.min(Math.max(P.getY(),0),content.maxHeight()-sp.getViewport().getHeight()));
    	//sp.getViewport().setViewPosition(P);
    }
    public void setContent(ASlidePanel C) {
    	content = C;   
    	sp = new JScrollPane(content, JScrollPane.VERTICAL_SCROLLBAR_NEVER, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
    	add(sp);
	}
}