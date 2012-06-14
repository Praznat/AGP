package AMath;

import Descriptions.XWeapon;
import Game.AGPmain;
import Game.Defs;
import Sentiens.Clan;
import Sentiens.GobLog;
import Sentiens.GobLog.Reportable;

/**
 *  This class should be used for testing certain game concepts to make sure they are not broken
 */
public class Testing {
	

	public static void doAllTests() {
		normalMarketFunctions();
		workInputManagement();
		breeding();
		ideologyInteractions();
		//naming();
	}

	public static void workInputManagement() {
		Clan guy = AGPmain.TheRealm.getClan(0);
		Clan other = AGPmain.TheRealm.getClan(1);
		guy.addReport(GobLog.transaction(1, 9, true, other));
		other.addReport(GobLog.transaction(1, 9, false, guy));
		for (Reportable R : guy.getLog()) {
			System.out.println(R.out());
		}
		for (Reportable R : other.getLog()) {
			System.out.println(R.out());
		}
		for (Reportable R : guy.getLog()) {
			System.out.println(R.out());
		}
		for (Reportable R : other.getLog()) {
			System.out.println(R.out());
		}
		//FAAIL!!!!! on date()
	}
	
	public static void normalMarketFunctions() {

	}

	public static void breeding() {
		
	}

	public static void ideologyInteractions() {
		
	}

	public static void naming() {
		for(int i = 0; i < 1000; i++) {
			System.out.println(XWeapon.weaponName(XWeapon.craftNewWeapon(0, 15)));
		}
		
	}
}
