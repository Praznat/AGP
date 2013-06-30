package Game;
import java.applet.Applet;
import java.util.Random;

import javax.swing.*;

import AMath.*;
import GUI.GUImain;
import Sentiens.Clan;

public class AGPmain extends Applet {
	private static int initpop = 10000;
//	public static Random rand = new Random();
	public static Random rand = new Random(0);
	public static GUImain mainGUI;
	public static Realm TheRealm;
	
	
	//these are for shire naming... so they dont get new names every time u reload
	public static final int shireFPLen = 42;
	public static final int shireLPLen = 70;
	public static final int[] SR = Calc.randomOrder(shireFPLen*shireLPLen);
	/**
	 * @param args
	 */
	
	public static boolean isGoing = true;
	public static boolean AUTOPILOT = false;

	public static void turnOffAutopilot() {AUTOPILOT = false;}
	public static void turnOnAutopilot() {AUTOPILOT = true;}
	
	public void init() {
		
//		this.setBackground(Color.gray);
	}
	
	public static void setRealm(Realm r) {TheRealm = r;}
	
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
	
	public static void pause() {
		if(Thread.currentThread().getName().equals(TheRealm.getName())) {
			System.out.println("paused " + Thread.currentThread().getName());
			try {
				System.out.println("sleep start");
				Thread.sleep(100000000);
				throw new IllegalStateException("never unpaused!");
			} catch (InterruptedException e) {
				System.out.println("interrupted");
				Thread.interrupted();
			}
		}
		else {
			mainGUI.AC.showPlayButton();
			isGoing = false;
		}
	}
	public static void play() {
		if (isGoing) {TheRealm.interrupt();}
		mainGUI.AC.showPauseButton();
		isGoing = true;
	}

	public static int getShiresX() {return mainGUI != null ? mainGUI.MD.getTCols() / 2 : 100;}
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
	public static Clan getAvatar() {
		return mainGUI != null ? mainGUI.AC.getAvatar() : null;
	}
	
}


