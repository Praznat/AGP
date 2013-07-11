package AMath;

import Defs.*;
import Descriptions.XWeapon;
import Game.*;
import Government.Order;
import Sentiens.*;
import Sentiens.Values.Value;

/**
 *  This class should be used for testing certain game concepts to make sure they are not broken
 */
public class Testing {
	
	protected static Realm testRealm;
	

	public static void main(String[] args) {
		doAllTests();
	}
	
	public static void reset() {
		testRealm = TestRealm.makeTestRealm(2, 2, 100);
		testRealm.doCensus();
		AGPmain.setRealm(testRealm);
	}
	
	public static void doAllTests() {
		System.out.println("starting tests");
		
		TestKnowledge.testWealthKnowledge();
		
		TestContracts.doAllContractTests();
		
		ideologyBasicFunctions();
		
		TestMarkets.testLogics();
		
		TestMarkets.normalMarketFunctions();
		
//		workInputManagement();
//		breeding();
//		ideologyInteractions();
		//naming();
		
		AGPmain.setLastRealm();
	}

	protected static Clan setClanMemMax(Clan c, M_ m) {c.FB.setBeh(m, 15); return c;}
	protected static Clan setClanMemMin(Clan c, M_ m) {c.FB.setBeh(m, 0); return c;}
	protected static Clan setClanMemMax(Clan c, P_ p) {c.FB.setPrs(p, 15); return c;}
	protected static Clan setClanMemMin(Clan c, P_ p) {c.FB.setPrs(p, 0); return c;}
	protected static void affirm(boolean b) {affirm(b, "could not affirm");}
	protected static void affirm(boolean b, String errorString) {if (!b) {throw new IllegalStateException(errorString);}}

	protected static void pursueUntilDone(Clan c) {
		int numQ = c.MB.QuestStack.size();
		while (c.MB.QuestStack.size() >= numQ) {
			c.pursue();
		}
	}
	
	public static void breeding() {
		reset();
	}
	
	public static void ideologyBasicFunctions() {
		reset();
		Clan greyjoy = testRealm.getClan(0);
		Value gold = greyjoy.FB.getValue(0);
		Value silver = greyjoy.FB.getValue(1);
		greyjoy.FB.upSanc(silver);
		affirm(greyjoy.FB.getValue(0) == silver);
		affirm(greyjoy.FB.getValue(1) == gold);
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
		Calc.p(theon.getNomen() + " belongs to " + theon.myOrder().getNationName() + " and follows " + theon.getBoss().getNomen());
		Calc.p(greyjoy.getNomen() + " belongs to " + greyjoy.myOrder().getNationName() + " and follows " + greyjoy.getBoss().getNomen());
		greyjoy.join(stark);
		Calc.p(theon.getNomen() + " belongs to " + theon.myOrder().getNationName() + " and follows " + theon.getBoss().getNomen());
		Calc.p(greyjoy.getNomen() + " belongs to " + greyjoy.myOrder().getNationName() + " and follows " + greyjoy.getBoss().getNomen());
		stark.join(targaryan);
		Calc.p(theon.getNomen() + " belongs to " + theon.myOrder().getNationName() + " and follows " + theon.getBoss().getNomen());
		Calc.p(greyjoy.getNomen() + " belongs to " + greyjoy.myOrder().getNationName() + " and follows " + greyjoy.getBoss().getNomen());
		targaryan.join(theon);
		Calc.p(theon.getNomen() + " belongs to " + theon.myOrder().getNationName() + " and follows " + theon.getBoss().getNomen());
		Calc.p(greyjoy.getNomen() + " belongs to " + greyjoy.myOrder().getNationName() + " and follows " + greyjoy.getBoss().getNomen());
		stannis.join(baratheon);
		Calc.p(stannis.getNomen() + " belongs to " + stannis.myOrder().getNationName() + " and follows " + stannis.getBoss().getNomen());
		Calc.p(baratheon.getNomen() + " belongs to " + baratheon.myOrder().getNationName() + " and follows " + baratheon.getBoss().getNomen());
		baratheon.join(stark);
		Calc.p(stannis.getNomen() + " belongs to " + stannis.myOrder().getNationName() + " and follows " + stannis.getBoss().getNomen());
		Calc.p(baratheon.getNomen() + " belongs to " + baratheon.myOrder().getNationName() + " and follows " + baratheon.getBoss().getNomen());
		
	}
	
	protected static void makePuritan(Clan c, Value v1, Value v2) {
		while(c.FB.getValue(0) != v2) {c.FB.upSanc(v2);}
		while(c.FB.getValue(0) != v1) {c.FB.upSanc(v1);}
		c.FB.setBeh(M_.STRICTNESS, 15);
	}

	public static void naming() {
		reset();
		for(int i = 0; i < 1000; i++) {
			System.out.println(XWeapon.weaponName(XWeapon.craftNewWeapon(0, 15)));
		}
		
	}
	

}
