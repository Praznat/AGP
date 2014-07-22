package War;

import java.awt.Rectangle;
import java.util.*;

import Defs.P_;
import Descriptions.*;
import Descriptions.GobLog.Reportable;
import Questing.*;
import Questing.WarQuests.FormArmy;
import Questing.WarQuests.InvolvesArmy;
import Sentiens.Clan;
import Shirage.Shire;
import War.CombatDefs.BattleStats;

public class BattleField {
	public static final BattleField INSTANCE = new BattleField();
	private final Rectangle field = new Rectangle(1000, 500);
	private final HashMap<Clan, BattleResult> results = new HashMap<Clan, BattleResult>();
	private Army defenseArmy = new Army();
	private Army offenseArmy = new Army();

	public static void setupNewBattleField(Clan attacker, Clan defender, Shire location) {
		createArmyFrom(defender, INSTANCE.defenseArmy, location);
		createArmyFrom(attacker, INSTANCE.offenseArmy, location);
		determineFormation(defender, INSTANCE.defenseArmy);
		determineFormation(attacker, INSTANCE.offenseArmy);
		
		INSTANCE.go();
		boolean attackerWins = wasVictorious(attacker);
		 // TODO make sure changing prest here is correct thing to do
		if (attackerWins) {attacker.FB.upPrest(P_.BATTLEP); defender.FB.downPrest(P_.BATTLEP);}
		else {defender.FB.upPrest(P_.BATTLEP); attacker.FB.downPrest(P_.BATTLEP);}
		final Reportable resultLog = GobLog.battleResult(attacker, defender, INSTANCE.offenseArmy.size(), INSTANCE.defenseArmy.size(), attackerWins);
		attacker.addReport(resultLog); defender.addReport(resultLog);
				
	}
	private static void createArmyFrom(Clan clan, Army army, Shire location) {
		final QStack qs = clan.MB.QuestStack;
		Set<Clan> clanArmy = new HashSet<Clan>();
		for (Quest q : qs) {
			if (InvolvesArmy.class.isAssignableFrom(q.getClass())) {
				for (FormArmy fa : ((InvolvesArmy)q).getArmy()) {clanArmy.add(fa.getDoer());}
			}
		}
		createArmyFrom(clanArmy, army);
	}
	private static Army createArmyFrom(Set<Clan> clans, Army army) {
		army.clear();
		for (Clan c : clans) {
			Warrior w = new Warrior();
			w.setRefClan(c);
			army.add(w);
		}
		return army;
	}
	
	private void go() {
		BattleStats attackStats = new BattleStats();
		BattleStats defenseStats = new BattleStats();
		for (Warrior w : offenseArmy) {attackStats.computeFriendly(w);}
		for (Warrior w : defenseArmy) {defenseStats.computeFriendly(w);}
		boolean attackerWin = BattleStats.attackerWinsExchange(attackStats, defenseStats);
		results.clear();
		BattleResult result;
		for (Warrior w : offenseArmy) {
			result = new BattleResult();
			result.endStatus = attackerWin ? EndStatus.VICTORIOUS : EndStatus.RETREATED;
			results.put(w.getRefClan(), result);
		}
		for (Warrior w : defenseArmy) {
			result = new BattleResult();
			result.endStatus = attackerWin ? EndStatus.CAPTURED : EndStatus.VICTORIOUS;
			results.put(w.getRefClan(), result);
		}
	}
	
	private void stepOnce() {
		
	}
	
	
	public static void determineFormation(Clan clan, Army army) {
		
	}
	
	public static boolean wasVictorious(Clan me) {
		return INSTANCE.results.get(me).endStatus == EndStatus.VICTORIOUS;
	}
	
	public Rectangle getField() {return field;}
	
	private static enum EndStatus {DEAD, WOUNDED, RETREATED, CAPTURED, VICTORIOUS}
	private class BattleResult {
		int kills, wins, flights;
		EndStatus endStatus;
	}
}
