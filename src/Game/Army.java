package Game;

import Sentiens.Clan;

public class Army {
	private int xloc, yloc;
	private int CommanderID;
	private Order order;
	private byte cause; //loyalty to commander, nationalism, god, survival, etc.
	private byte nextmove;
	private int fatigue;
	private int millet;
	private int[] arms;
	private int[] spoils;
	
	public Clan Commander() {return AGPmain.TheRealm.getClan(CommanderID);}
	
	
	class Order {
		private int objx, objy; //occupy space
		private int objID; //kill or protect someone
		private boolean aggstance; //pursue or avoid conflict
		
	}
	
}
