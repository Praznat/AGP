package GUI;
import java.awt.*;

import AMath.Calc;
import Game.Do;

import java.awt.event.*;
import java.awt.image.BufferedImage;

import Shirage.Plot;
import Shirage.XPlot;
import javax.swing.*;

public class MapDisplay extends JPanel implements MouseListener, MouseMotionListener {

	private int TotRows, TotCols;
	private int N = 4000;
	private int Ns = 2000;
	private int WSize = 18;
	private int HSize = 13;
	private final double R2C = 1;
	private Plot[] plots;
	private Plot ExPlot;
	private double wamt = 0.25;
	private double samt = 0;
	private final double wfp = 1;
	private final double wtr = 0.2;
	private final double str = 0.1;
	private int[][] PlotOrder;
	private BufferedImage offscreen;

	private static String pdes = "";
	private int maxW, maxH, tmpX = 0, tmpY = 0;
	
	public MapDisplay() {
		ExPlot = new XPlot(0);
		addMouseListener(this);
		addMouseMotionListener(this);
	}

	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		paint(g);
	}
	public void createMap(int T, int r) {
		pdes = "CREATING MAP...";
		for (int i = 0; i<T; i++) {
			updateW();
			if(i % r == 0) {repaint();}
		} PlotOrder = orderPlotsByVal(); paintBGCanvas();  repaint(); pdes = "";
	}
	public void paintBGCanvas() {
		Graphics2D g = offscreen.createGraphics();
		
		for (int r = 0; r < TotRows; r++) {
    		for (int i = TotCols-1; i >= 0; i--) {
    			int c = PlotOrder[r][i];
    			getPlotXY(c, r).drawPlot(g);
    		}
		}
		for (int r = 0; r < TotRows; r++) {
    		for (int i = TotCols-1; i >= 0; i--) {
    			int c = PlotOrder[r][i];
    			getPlotXY(c, r).drawPlot(g);
    		}
		}
	}
	
	public void paint(Graphics gx) {
		gx.setColor(new Color(150,120,110));
		gx.fillRect(0, 0, getWidth(), getHeight());

		gx.drawImage(offscreen.getSubimage(tmpX,tmpY,getWidth(),getHeight()),  0,0,getWidth(),getHeight(),this);
		
		gx.setColor(Color.red);
		gx.drawString(pdes, 30, 30);
	}
	
	public void validate() {
		super.validate();
		repaint();
	}
	
	public void introduce() {
		validate();
		plots = new Plot[N+1];
		TotRows = (int) (Math.sqrt((double) N * R2C) + 0.9999);
		TotCols = N / TotRows;
		
		for (int i = 0; i < N; i++) {
			plots[i] = new Plot(Math.random());
			plots[i].setXY(i%TotCols, i/TotCols);
		}
		plots[N] = ExPlot;


		Plot[] landplots = new Plot[Ns];
		int k = 1;   int[] order = Calc.normalOrder(Ns);
		landplots[0] = getPlotXY(TotCols/2, TotRows/2);
		Plot nextP;   int[] ord6 = Calc.normalOrder(6);
		while (k < Ns) {
			Calc.shuffle(order, k);
			for(int i = 0; i < k; i++) {
				nextP = spawnPlot(landplots[order[i]], ord6);
				if (!nextP.isNull()) {
					landplots[k++] = nextP; break;
				}
			}
		}
		for (int i = 0; i < N; i++) {if (plots[i].isOcean()) {plots[i] = ExPlot;}}
		
		smoothPlots();  smoothPlots();  smoothPlots();
		smoothPlots();  smoothPlots();  smoothPlots();
		wamt = 0.25;
		samt = 0;
		wamt = wamt * N;
		PlotOrder = orderPlotsByVal();
		maxW = TotW();
		maxH = TotH();
		offscreen = new BufferedImage(TotW(),TotH(), BufferedImage.TYPE_INT_ARGB);
	}

	public int W() {return WSize;}
	public int H() {return HSize;}
	public int getTRows() {return TotRows;}
	public int getTCols() {return TotCols;}
	public int TotW() {return WSize*TotCols;}
	public int TotH() {return HSize*TotRows;}
	private int hsX() {return this.getWidth() / 2;}
	private int hsY() {return this.getHeight() / 2;}
	
	
	public Plot spawnPlot(Plot P, int[] ord){
		if(Math.random() < 0.0001) {Calc.shuffle(ord);}
		P.refreshHood();
		Plot N;
		for (int i = P.myHood().length - 1; i >= 0; i--) {
			N = P.myHood()[ord[i]];
			if(N.isOcean()) {
				N.makeLand(); return N;
			}
		}
		return ExPlot;
	}
	

	
	public Plot getPlotXY(int x, int y) {
		if(x<0 || y<0 || x>=TotCols || y>TotRows || (y*TotCols + x >= plots.length)) {return ExPlot;}
		else {return plots[y*TotCols + x];}
	}
	public void smoothPlots() {
		double[] tmp = new double[plots.length];
		for(int i = 0; i < tmp.length; i++) {tmp[i] = plots[i].getHoodAvgVal();}
		for(int i = 0; i < tmp.length; i++) {plots[i].setValue(tmp[i]);}
	}
	public void WRise() {
		for (int i = 0; i < plots.length; i++) {
			wamt += plots[i].evaporate(wtr);
		}
	}
	public void WFall() {
		double wpp = wamt / plots.length;
		for (int i = 0; i < plots.length; i++) {
			if(Math.random() < wfp) {
				plots[i].chgW(wpp);
				wamt -= wpp;
			}
		}
	}
	public void SCycle() {
		double er;
		for (int i = 0; i < N; i++) {
			if(!plots[i].isNull()) {
				er = Math.min(plots[i].getWFlow() * str, plots[i].getValue() - plots[i].getRBM());
				plots[i].chgValue(-er);
				samt += er;
			}
		}
		double spp = samt / Ns;
		for (int i = 0; i < N; i++) {
			if(!plots[i].isNull()) {
				plots[i].chgValue(spp);
				samt -= spp;
			}
		}
	}
	
	public void updateW() {
		WFall();
		for(int i = 0; i < plots.length; i++) {plots[i].flow();}
		for(int i = 0; i < plots.length; i++) {plots[i].emptyHyp();}
		SCycle();
		WRise();
	}

	public double totalW() {
		double sum = 0;
		for(int i = 0; i < plots.length; i++) {
			sum += plots[i].getWVal();
		}
		return sum + wamt;
	}
	
	
	
    public int[][] orderPlotsByVal() {
    	int[][] Order = new int[TotRows][TotCols];
    	double[] vals = new double[TotCols];
    	for (int r = 0; r < TotRows; r++) {
    		for (int c = 0; c < TotCols; c++) {
    			vals[c] = getPlotXY(c, r).getValue();
    		}   Order[r] = order(vals);
    	}   return Order;
    }
    
    public int[] order(double[] vals) {
    	//returns order from lowest to highest
    	int[] O = new int[vals.length];
    	for(int i = 0; i < O.length; i++) {O[i] = i;}
		boolean switched = true;   double tmp;
		while (switched) {   switched = false;
			for (int i = 0; i < vals.length-1; i++) {
				if (vals[i] < vals[i+1]) {
					tmp = vals[i+1]; vals[i+1] = vals[i]; vals[i] = tmp;
					tmp = O[i+1]; O[i+1] = O[i]; O[i] = (int)tmp;
					switched = true;
		} } }
		return O;
    }

	public int getTmpX() {return 0;} //return tmpX;}
	public int getTmpY() {return 0;} //return tmpY;}
	
    public void mousePressed(MouseEvent e) {
    	if (e.getX() < 20 && e.getY() < 20) {
    		new APopupMenu(this, Do.StepOnce, Do.ShowRandomGoblin, Do.ShowRandomShire);
    		return;
    	}
    	tmpX = Math.min(Math.max(tmpX + e.getX() - hsX(), 0), maxW - getWidth());
    	tmpY = Math.min(Math.max(tmpY + e.getY() - hsY(), 0), maxH - getHeight());
    	for (int i = 0; i < plots.length; i++) {plots[i].setGradients();}
    	repaint();
    }
	public void mouseReleased(MouseEvent e) {}
    public void mouseEntered(MouseEvent e) {}
    public void mouseClicked(MouseEvent e) {}
    public void mouseMoved(MouseEvent e) {}
    public void mouseDragged(MouseEvent e) {}
    public void mouseExited(MouseEvent e) {}
    
}




