package War;

import java.util.Set;

import Game.Defs;
import Sentiens.Clan;
import Shirage.Shire;

public class Combat {

	public static void engage(Clan attacker, Clan defender, Shire location) {
		Set<Clan> defenseArmy = defender.myOrder().getFollowers(defender, location, false); //not include general
		Set<Clan> offenseArmy = attacker.myOrder().getFollowers(attacker, location, false); //not include general
		determineFormation(defender, defenseArmy);
		determineFormation(attacker, offenseArmy);
		
		
	}
	
	
	public static void determineFormation(Clan clan, Set<Clan> army) {}
	
	public static FormationType[] formationTypes = setupFormationDefinitions();
	public static final FormationType SCORPION = new FormationType();
	public static final FormationType WEDGE = new FormationType();
	public static final FormationType BOX = new FormationType();
	
	private static FormationType[] setupFormationDefinitions() {
		SCORPION.vanguard.setPreferences(5, FODDER, PHALANX, FOOT);
		SCORPION.lwing.setPreferences(10, DONKEY_KNIGHT, CHARGERS, CAVALRY, MISSILE_CAVALRY);
		SCORPION.rwing.setPreferences(10, DONKEY_KNIGHT, CHARGERS, CAVALRY, MISSILE_CAVALRY);
		SCORPION.center.setPreferences(5, PHALANX, FOOT);
		SCORPION.rear.setPreferences(5, MISSILE, PHALANX);

		WEDGE.vanguard.setPreferences(2, DONKEY_KNIGHT, CHARGERS, PHALANX);
		WEDGE.lwing.setPreferences(0, FODDER);
		WEDGE.rwing.setPreferences(0, FODDER);
		WEDGE.center.setPreferences(3, CHARGERS, PHALANX, FOOT);
		WEDGE.rear.setPreferences(4, PHALANX, FODDER);
		WEDGE.vanguard.setGeneral(WEDGE);

		BOX.vanguard.setPreferences(5, PHALANX, DONKEY_KNIGHT, FOOT, FODDER);
		BOX.lwing.setPreferences(5, PHALANX, DONKEY_KNIGHT, FOOT, FODDER);
		BOX.rwing.setPreferences(5, PHALANX, DONKEY_KNIGHT, FOOT, FODDER);
		BOX.center.setPreferences(2, MISSILE_CAVALRY, MISSILE);
		BOX.rear.setPreferences(3, MISSILE, CAVALRY);
		
		return new FormationType[] {SCORPION, WEDGE, BOX};
	}
	
	private static class FormationType {
		private Part general;
		private Part vanguard, lwing, rwing, center, rear;
		private int priority = 0;
		public FormationType () {
			vanguard = new Part(); lwing = new Part(); rwing = new Part(); center = new Part(); rear = new Part();
			rear.setGeneral(this);  //default
		}
		public Part getGeneralsPart() {return general;}
		
		private static class Part {
			private int advancement; //starting proximity to enemy
			private int size; //preferred size (units allocated to part based on this)
			private SoldierType[] typePreference;
			public void setPreferences(int s, SoldierType... st) {size = s; typePreference = st;}
			public void setGeneral(FormationType parent) {parent.general = this;}
		}
	}
	
	
	public static class SoldierType {
		private Armor armor;
		private Edge edge;
		private Range range;
		private Mount mount;
		private SoldierType() {}
		public static SoldierType create() {return new SoldierType();}
		public SoldierType light() {armor = Armor.LIGHT; return this;}
		public SoldierType heavy() {armor = Armor.HEAVY; return this;}
		public SoldierType foot() {mount = Mount.INFANTRY; return this;}
		public SoldierType mounted() {mount = Mount.CAVALRY; return this;}
		public SoldierType melee() {range = Range.MELEE; return this;}
		public SoldierType missile() {range = Range.MISSILE; return this;}
		public SoldierType gun() {range = Range.MISSILE; edge = Edge.BLUNT; return this;}
		public SoldierType bow() {range = Range.MISSILE; edge = Edge.SHARP; return this;}
		
		public boolean canFulfill(Clan clan) {
			if (armor != null && armor == Armor.HEAVY && clan.getAssets(Defs.armor) <= 0) {return false;}
			if (mount != null && mount == Mount.CAVALRY && clan.getAssets(Defs.lobodonkey) <= 0) {return false;}
			if (range != null) {
				if (edge == null) {
					if (range == Range.MISSILE && (clan.getAssets(Defs.bow) <= 0 && clan.getAssets(Defs.gun) <= 0)) {return false;}
					if (range == Range.MELEE && (clan.getAssets(Defs.sword) <= 0 && clan.getAssets(Defs.mace) <= 0 && clan.getAssets(Defs.xweapon) <= 0)) {return false;}
				}
				else if (edge == Edge.BLUNT && clan.getAssets(Defs.gun) <= 0) {return false;}
				else if (edge == Edge.SHARP && clan.getAssets(Defs.bow) <= 0) {return false;}
			}
			return true;
		}
		
	}
	
	public static SoldierType DONKEY_KNIGHT = SoldierType.create().heavy().mounted().melee();
	public static SoldierType CHARGERS = SoldierType.create().mounted().melee();
	public static SoldierType MISSILE_CAVALRY = SoldierType.create().mounted().missile();
	public static SoldierType CAVALRY = SoldierType.create().mounted();
	public static SoldierType MISSILE = SoldierType.create().missile();
	public static SoldierType PHALANX = SoldierType.create().heavy().foot().melee();
	public static SoldierType FODDER = SoldierType.create().light().foot();
	public static SoldierType FOOT = SoldierType.create().foot();
	public static SoldierType ARCHERS = SoldierType.create().bow();
	public static SoldierType GUNNERS = SoldierType.create().gun();
	

	public static enum Armor {LIGHT, HEAVY}
	public static enum Edge {BLUNT, SHARP}
	public static enum Range {MELEE, MISSILE}
	public static enum Mount {INFANTRY, CAVALRY}
	
}
