package AMath;

import Defs.*;
import Game.Contract;
import Questing.*;
import Questing.Quest.TargetQuest;
import Sentiens.*;
import Sentiens.Values.Value;
import Shirage.Shire;

public class TestContracts extends Testing {

	private static Shire s;
	private static Clan a;
	private static Clan b;
	
	public static void doAllContractTests() {
		testChallengeMightQuest();
		testChallengeMightQuest();
		testChallengeMightQuest();
		testChallengeMightQuest();
		testChallengeMightQuest();
		testChallengeMightQuest();
		testDemandTribute();
		testDemandService();
		testOfferThreat();
		testDemandService();
		testOfferThreat();
		testDemandService();
		testOfferThreat();
		testDemandService();
		testOfferThreat();
	}
	
	private static void resetContracts() {
		reset();
		s = testRealm.getClan(0).myShire();
		a = s.getCensus(0);
		b = s.getCensus(1);
		a.setActive(true);
		b.setActive(true);
		a.setMillet(1000);
		b.setMillet(1000);
		a.FB.setPrs(P_.COMBAT, 0);
		b.FB.setPrs(P_.COMBAT, 0);
	}
	private static void testDemandTribute() {
		resetContracts();
		makePuritan(a, Values.WEALTH, Values.RIGHTEOUSNESS);
		final int originalMillet = 1000;
		a.setMillet(originalMillet);
		Contract.getNewContract(a, b);
		final int demandedMillet = 500;
		Contract.getInstance().demandTribute(demandedMillet);
		final double theoretical = a.FB.weightOfValue(Values.WEALTH) * Values.logComp(originalMillet - demandedMillet, originalMillet);
		affirm(Contract.getInstance().getDemandValue() == theoretical);
	}
	
	private static void testDemandService() {
		resetContracts();
		System.out.println(a.getNomen() + " vs " + b.getNomen());
		Contract.getNewContract(a, b);
		Contract.getInstance().demandService(Values.COMFORT);
		System.out.println("service value: " + Contract.getInstance().getDemandValue());
	}
	
	private static void testOfferThreat() {
		resetContracts();
		System.out.println(a.getNomen() + " vs " + b.getNomen());
		Contract.getNewContract(a, b);
		Contract.getInstance().threatenMight();
		System.out.println("threaten might value: " + Contract.getInstance().getDemandValue());
		Contract.getNewContract(a, b);
		Contract.getInstance().threatenLife();
		System.out.println("threaten life value: " + Contract.getInstance().getDemandValue());
		Contract.getNewContract(a, b);
		Contract.getInstance().threatenLifeAndProperty();
		System.out.println("threaten life and property value: " + Contract.getInstance().getDemandValue());
		Contract.getNewContract(a, b);
		Contract.getInstance().threatenProperty();
		System.out.println("threaten property value: " + Contract.getInstance().getDemandValue());
		Contract.getNewContract(a, b);
		Contract.getInstance().threatenLineage();
		System.out.println("threaten lineage value: " + Contract.getInstance().getDemandValue());
	}
	

	private static void testChallengeMightQuest() {
		resetContracts();
		a.MB.newQ(new MightQuests.ChallengeMight(a));
		((TargetQuest)a.MB.QuestStack.peek()).setTarget(b);
		makePuritan(a, Values.WEALTH, Values.RIGHTEOUSNESS);
		makePuritan(b, Values.WEALTH, Values.RIGHTEOUSNESS);
		a.pursue();
		final double demandVal = Contract.getInstance().getDemandValue();
		final double offerVal = Contract.getInstance().getOfferValue();
		System.out.println("demand value: " + demandVal);
		System.out.println("offer value: " + offerVal);
		affirm(demandVal < 0);
		affirm(offerVal > 10);
	}
	
	private static void makePuritan(Clan c, Value v1, Value v2) {
		while(c.FB.getValue(0) != v2) {c.FB.upSanc(v2);}
		while(c.FB.getValue(0) != v1) {c.FB.upSanc(v1);}
		c.FB.setBeh(M_.STRICTNESS, 15);
	}
	
}
