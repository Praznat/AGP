package AMath;

import Descriptions.XWeapon;
import Game.AGPmain;
import Game.Defs;
import Game.Order;
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
		if (true) return;
		Clan greyjoy = AGPmain.TheRealm.getClan(0);
		Clan stark = AGPmain.TheRealm.getClan(1);
		Clan theon = AGPmain.TheRealm.getClan(2);
		Clan targaryan = AGPmain.TheRealm.getClan(3);
		Clan baratheon = AGPmain.TheRealm.getClan(4);
		Clan stannis = AGPmain.TheRealm.getClan(5);
		Calc.p("greyjoy="+greyjoy.getNomen());
		Calc.p("stark="+stark.getNomen());
		Calc.p("theon="+theon.getNomen());
		Calc.p("targaryan="+targaryan.getNomen());
		Calc.p("baratheon="+baratheon.getNomen());
		Calc.p("stannis="+stannis.getNomen());
		Order.create(greyjoy);
		Order.create(stark);
		Order.create(targaryan);
		Order.create(baratheon);
		theon.join(greyjoy);
		Calc.p(theon.getNomen() + " belongs to " + theon.myOrder().getNationName() + " and follows " + theon.FB.getRex().getNomen());
		Calc.p(greyjoy.getNomen() + " belongs to " + greyjoy.myOrder().getNationName() + " and follows " + greyjoy.FB.getRex().getNomen());
		greyjoy.join(stark);
		Calc.p(theon.getNomen() + " belongs to " + theon.myOrder().getNationName() + " and follows " + theon.FB.getRex().getNomen());
		Calc.p(greyjoy.getNomen() + " belongs to " + greyjoy.myOrder().getNationName() + " and follows " + greyjoy.FB.getRex().getNomen());
		stark.join(targaryan);
		Calc.p(theon.getNomen() + " belongs to " + theon.myOrder().getNationName() + " and follows " + theon.FB.getRex().getNomen());
		Calc.p(greyjoy.getNomen() + " belongs to " + greyjoy.myOrder().getNationName() + " and follows " + greyjoy.FB.getRex().getNomen());
		targaryan.join(theon);
		Calc.p(theon.getNomen() + " belongs to " + theon.myOrder().getNationName() + " and follows " + theon.FB.getRex().getNomen());
		Calc.p(greyjoy.getNomen() + " belongs to " + greyjoy.myOrder().getNationName() + " and follows " + greyjoy.FB.getRex().getNomen());
		stannis.join(baratheon);
		Calc.p(stannis.getNomen() + " belongs to " + stannis.myOrder().getNationName() + " and follows " + stannis.FB.getRex().getNomen());
		Calc.p(baratheon.getNomen() + " belongs to " + baratheon.myOrder().getNationName() + " and follows " + baratheon.FB.getRex().getNomen());
		baratheon.join(stark);
		Calc.p(stannis.getNomen() + " belongs to " + stannis.myOrder().getNationName() + " and follows " + stannis.FB.getRex().getNomen());
		Calc.p(baratheon.getNomen() + " belongs to " + baratheon.myOrder().getNationName() + " and follows " + baratheon.FB.getRex().getNomen());
		
	}

	public static void naming() {
		for(int i = 0; i < 1000; i++) {
			System.out.println(XWeapon.weaponName(XWeapon.craftNewWeapon(0, 15)));
		}
		
	}
}
