package Game;
import java.applet.Applet;
import java.awt.Color;
import java.util.Random;

import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import AMath.Calc;
import AMath.Testing;
import GUI.GUImain;
import Sentiens.Values;
import Sentiens.Values.Value;

public class AGPmain extends Applet {
	private static int initpop = 10000;
	public static Random rand = new Random();
	//static Random rand = new Random(0);
	public static GUImain mainGUI;
	public static Realm TheRealm;
	
	
	//these are for shire naming... so they dont get new names every time u reload
	public static final int shireFPLen = 42;
	public static final int shireLPLen = 70;
	public static final int[] SR = Calc.randomOrder(shireFPLen*shireLPLen);
	/**
	 * @param args
	 */
	

	
	
	
	public void init() {
		
//		this.setBackground(Color.gray);
	}
	
	public void start() {
		

		setLookAndFeel();
		
		long start = System.nanoTime();  
		
		mainGUI = new GUImain("AGP");
		mainGUI.initializeMD();
		TheRealm = Realm.makeRealm(getShiresX(), getShiresY(), initpop);
		TheRealm.setupDefs();
		TheRealm.doCensus();
		//mainGUI.initializeTD(TheRealm.getShireData());
		mainGUI.initializeGM();
		mainGUI.initializeSM();
		mainGUI.initializeAC(TheRealm.getClan(0));
		Do.ShowRandomGoblin.doit();
		Do.ShowRandomShire.doit();
		mainGUI.GM.setState();
		mainGUI.SM.setState();
		

		//TheRealm.go();

		
		long elapsedTime = System.nanoTime() - start;

		System.out.println("Elapsedtime: " + (double)elapsedTime / 1000000000 + " seconds");
		

		Testing.doAllTests();


		
	}
	
	public static int getShiresX() {return mainGUI.MD.getTCols() / 2;}
	public static int getShiresY() {return mainGUI.MD.getTRows() / 2;}

	private static void setLookAndFeel() {
		try {
			UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (UnsupportedLookAndFeelException e) {
			e.printStackTrace();
		}
	}
	
}


