package Game;

import java.util.HashSet;
import java.util.Set;

import Sentiens.Clan;

public class Order {

	public static Order create(Clan creator) {
		return new Order();
	}
	private Order() {members = new HashSet<Clan>();}

	public enum Objective {
		PROTECT_AVENGE,
		DEFEND_RETAKE,
		DESTROY,
		PROSELYTIZE,
		GOVERN
	}

	private Set<Clan> members;
	public void addMember(Clan m) {members.add(m);}
	public void removeMember(Clan m) {members.remove(m);}
	
	

	public boolean preferableOver(Clan pov) {
		return preferableOver(null, pov);
	}
	public boolean preferableOver(Order other, Clan pov) {
		return false;
	}
	
}
