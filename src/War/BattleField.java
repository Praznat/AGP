package War;

import java.awt.Rectangle;
import java.util.*;

import Defs.P_;
import Sentiens.Clan;
import Shirage.Shire;
import War.CombatDefs.BattleStats;

public class BattleField {
	public static final BattleField INSTANCE = new BattleField();
	private Army defenseArmy;
	private Army offenseArmy;
	private Rectangle field = new Rectangle(1000, 500);
	private HashMap<Clan, BattleResult> results = new HashMap<Clan, BattleResult>();
	
	public static void setupNewBattleField(Clan attacker, Clan defender, Shire location) {
		INSTANCE.defenseArmy.clear();   INSTANCE.offenseArmy.clear();
		INSTANCE.defenseArmy = createArmyFrom(defender.myOrder().getFollowers(defender, location, false));
		INSTANCE.offenseArmy = createArmyFrom(attacker.myOrder().getFollowers(attacker, location, false));
		determineFormation(defender, INSTANCE.defenseArmy);
		determineFormation(attacker, INSTANCE.offenseArmy);
		
		INSTANCE.go();
	}
	public static Army createArmyFrom(Set<Clan> clans) {
		Army army = new Army();
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
	
	public Rectangle getField() {return field;}
	
	private static enum EndStatus {DEAD, WOUNDED, RETREATED, CAPTURED, VICTORIOUS}
	private class BattleResult {
		int kills, wins, flights;
		EndStatus endStatus;
	}
}
