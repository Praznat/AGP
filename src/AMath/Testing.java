package AMath;

import Defs.*;
import Descriptions.XWeapon;
import Game.*;
import Game.Do.SetupableThing;
import Government.Order;
import Markets.*;
import Questing.PropertyQuests.LaborQuest;
import Sentiens.*;
import Sentiens.GobLog.Reportable;
import Shirage.Shire;

/**
 *  This class should be used for testing certain game concepts to make sure they are not broken
 */
public class Testing {
	
	protected static Realm testRealm;
	

	public static void main(String[] args) {
		doAllTests();
	}
	
	public static void reset() {
		testRealm = Realm.makeRealm(2, 2, 100);
		testRealm.doCensus();
	}
	
	public static void doAllTests() {
		System.out.println("starting tests");
		
		TestMarkets.testLogics();
		
		TestMarkets.normalMarketFunctions();
		
//		workInputManagement();
//		breeding();
//		ideologyInteractions();
		//naming();
	}

	protected static Clan setClanMemMax(Clan c, M_ m) {c.FB.setBeh(m, 15); return c;}
	protected static Clan setClanMemMin(Clan c, M_ m) {c.FB.setBeh(m, 0); return c;}
	protected static Clan setClanMemMax(Clan c, P_ p) {c.FB.setPrs(p, 15); return c;}
	protected static Clan setClanMemMin(Clan c, P_ p) {c.FB.setPrs(p, 0); return c;}
	protected static void affirm(boolean b) {affirm(b, "could not affirm");}
	protected static void affirm(boolean b, String errorString) {if (!b) {throw new IllegalStateException(errorString);}}

	public static void breeding() {
		reset();
	}

	public static void ideologyInteractions() {
		reset();
		Clan greyjoy = testRealm.getClan(0);
		Clan stark = testRealm.getClan(1);
		Clan theon = testRealm.getClan(2);
		Clan targaryan = testRealm.getClan(3);
		Clan baratheon = testRealm.getClan(4);
		Clan stannis = testRealm.getClan(5);
		Calc.p("greyjoy="+greyjoy.getNomen());
		Calc.p("stark="+stark.getNomen());
		Calc.p("theon="+theon.getNomen());
		Calc.p("targaryan="+targaryan.getNomen());
		Calc.p("baratheon="+baratheon.getNomen());
		Calc.p("stannis="+stannis.getNomen());
		Order.createBy(greyjoy);
		Order.createBy(stark);
		Order.createBy(targaryan);
		Order.createBy(baratheon);
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
		reset();
		for(int i = 0; i < 1000; i++) {
			System.out.println(XWeapon.weaponName(XWeapon.craftNewWeapon(0, 15)));
		}
		
	}
	

}
