package Game;
import java.applet.Applet;
import java.awt.Color;
import java.util.Random;
import AMath.Calc;
import AMath.Testing;
import GUI.FaceGoblin;
import GUI.GUImain;

public class AGPmain extends Applet {
	private static int initpop = 10000;
	public static Random rand = new Random();
	//static Random rand = new Random(0);
	public static GUImain mainGUI;
	public static Realm TheRealm;
	public static FaceGoblin FG;
	
	
	//these are for shire naming... so they dont get new names every time u reload
	public static final int shireFPLen = 41;
	public static final int shireLPLen = 69;
	public static final int[] SR = Calc.randomOrder(shireFPLen*shireLPLen);
	/**
	 * @param args
	 */
	

	
	
	
	public void init() {
		this.setBackground(Color.gray);
	}
	
	public void start() {

		long start = System.nanoTime();  

		
		
		mainGUI = new GUImain("AGP");
		mainGUI.initializeMD();
		TheRealm = Realm.makeRealm(getShiresX(), getShiresY(), initpop);
		TheRealm.setupDefs();
		TheRealm.doCensus();
		//mainGUI.initializeTD(TheRealm.getShireData());
		mainGUI.initializeGM();
		mainGUI.initializeSM();
		

		//TheRealm.go();

		
		long elapsedTime = System.nanoTime() - start;

		System.out.println("Elapsedtime: " + (double)elapsedTime / 1000000000 + " seconds");
		

		Testing.doAllTests();


		
	}
	
	public static int getShiresX() {return mainGUI.MD.getTCols() / 3;}
	public static int getShiresY() {return mainGUI.MD.getTRows() / 3;}

	
}


