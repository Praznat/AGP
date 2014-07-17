package AMath;

import java.util.*;

import Defs.*;
import Questing.*;
import Questing.MightQuests.AttackClanQuest;
import Questing.MightQuests.FormArmy;
import Questing.MightQuests.InvolvesArmy;
import Sentiens.*;
import Shirage.Shire;

public class TestCombat extends Testing {
	
	private static Shire wei;
	
	private static Clan caocao;
	private static Clan lubu;
	private static Clan zhangliao;
	private static Clan dianwei;
	private static Clan xiahoudun;
	
	private static Clan liubei;
	private static Clan zhangfei;
	private static Clan guanyu;

	private static Clan dongzhuo;
	
	private static Collection<Clan> everyone = new ArrayList<Clan>();
	
	public static void doAllCombatTests() {
		testAttack(0);
		testAttack(1);
		testStandingArmy(0);
		
		restoreFilteredCensuses();
	}
	
	private static void resetFighters() {
		reset();
		wei = testRealm.getClan(0).myShire();
		everyone.clear();
		everyone.add(caocao = wei.getCensus(0)); caocao.overrideName("Caocao");
		everyone.add(zhangliao = wei.getCensus(1)); zhangliao.overrideName("Zhangliao");
		everyone.add(dianwei = wei.getCensus(2)); dianwei.overrideName("Dianwei");
		everyone.add(xiahoudun = wei.getCensus(3)); xiahoudun.overrideName("Xiahoudun");
		everyone.add(liubei = wei.getCensus(4)); liubei.overrideName("Liubei");
		everyone.add(zhangfei = wei.getCensus(5)); zhangfei.overrideName("Zhangfei");
		everyone.add(guanyu = wei.getCensus(6)); guanyu.overrideName("Guanyu");
		everyone.add(lubu = wei.getCensus(7)); lubu.overrideName("Lubu");
		everyone.add(dongzhuo = wei.getCensus(8)); dongzhuo.overrideName("Dongzhuo");

		dianwei.join(caocao);
		xiahoudun.join(caocao);

		zhangfei.join(liubei);
		guanyu.join(liubei);
		
		lubu.join(dongzhuo);
		zhangliao.join(lubu);
		
		for (Clan c : everyone) resetFighter(c);
	}
	
	private static void resetFighter(Clan c) {
		c.setActive(true);
		c.MB.newQ(new Quest.DefaultQuest(c));
		c.FB.setBeh(M_.CONFIDENCE, 7);
		c.FB.setBeh(M_.PARANOIA, 7);
		c.FB.setPrs(P_.COMBAT, 10);
	}
	
	private static boolean containsOf(Collection<FormArmy> army, Clan clan) {
		for (FormArmy fa : army) if (fa.getDoer() == clan) return true;
		return false;
	}

	private static void testAttack(int postSituation) {
		resetFighters();
		caocao.MB.newQ(new MightQuests.AttackClanQuest(caocao, liubei));
//		pursueUntilDone(caocao);
		caocao.pursue(); //AttackClanQuest
		caocao.pursue(); //FormOwnArmyQuest
		Set<FormArmy> a = ((InvolvesArmy)caocao.MB.QuestStack.peek()).getArmy();
		affirm(a.size() == 1 && containsOf(a, caocao)); // just leader for now
		dianwei.pursue();
		xiahoudun.pursue();
		affirm(a.size() == 2 && containsOf(a, caocao)); // only got one minion cuz not standing army

		switch (postSituation) {
		case 0: testAttackerGivesUp(); break;
		case 1: testAttackerWinsFight(); break;
		default: break;
		}
	}

	private static void testStandingArmy(int postSituation) { //uses DefendPatron
		resetFighters();
		formup(caocao, liubei, dianwei, xiahoudun);
		Set<FormArmy> a = ((InvolvesArmy)caocao.MB.QuestStack.peek()).getArmy();
		affirm(containsOf(a, dianwei) && containsOf(a, xiahoudun));
		
		switch (postSituation) {
		case 0: testHungryWarrior(); break;
		default: break;
		}
	}

	private static void testAttackerGivesUp() { // test attacker disbands if outpowered
		formup(liubei, caocao, guanyu, zhangfei);
		pursueUntilDone(caocao);
		dianwei.pursue();
		xiahoudun.pursue();
		affirm(dianwei.MB.QuestStack.getOfType(FormArmy.class) == null);
		affirm(xiahoudun.MB.QuestStack.getOfType(FormArmy.class) == null);
		// test winner disbands:
		liubei.pursue();
		affirm(liubei.MB.QuestStack.getOfType(FormArmy.class) == null);
		affirm(liubei.MB.QuestStack.getOfType(AttackClanQuest.class) == null);
	}
	
	private static void testAttackerWinsFight() {
		int p1 = caocao.FB.getPrs(P_.BATTLEP);
		formup(liubei, caocao); // just liubei
		pursueUntilDone(caocao);
		int p2 = caocao.FB.getPrs(P_.BATTLEP);
		affirm(p2 > p1);
	}
	
	private static void formup(Clan leader, Clan enemy, Clan... followers) {
		leader.MB.newQ(new MightQuests.AttackClanQuest(leader, enemy));
		for (Clan f : followers) f.MB.newQ(new MightQuests.DefendPatron(f, leader));
		leader.pursue(); //AttackClanQuest
		leader.pursue(); //FormOwnArmyQuest
		for (Clan f : followers) f.pursue();
	}
	
	private static void testHungryWarrior() {
		Clan.DMC = 3;
		Set<FormArmy> a = ((InvolvesArmy)caocao.MB.QuestStack.peek()).getArmy();
		affirm(containsOf(a, xiahoudun));
		xiahoudun.pursue();
		xiahoudun.pursue();
		xiahoudun.pursue();
		xiahoudun.pursue();
		xiahoudun.setMillet(Clan.DMC * 2);
		long m1 = xiahoudun.getBoss().getMillet();
		xiahoudun.pursue(); // collected money from boss
		long m2 = xiahoudun.getBoss().getMillet();
		affirm(containsOf(a, xiahoudun));
		affirm(m1 > m2);
	}

}
