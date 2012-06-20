package Game;

import java.util.HashSet;
import java.util.Set;

import Descriptions.GobName;
import Questing.PersecutionQuests.PersecuteInfidel;
import Sentiens.Clan;

public class Order {
	
	private static Set<Clan> moversLedger = new HashSet<Clan>();

	public interface Serveable {}
	private Serveable seatOfPower;   //clan, shire, or creed
	private Clan ruler;
	private final byte[] founderName;
	private final boolean founderGender;
	
	public static Order create(Clan creator) {
		Order newOrder = new Order(creator);
		creator.joinOrder(newOrder);
		return newOrder;
	}
	private Order(Clan creator) {
		ruler = creator;
		founderName = creator.getNameBytes();
		founderGender = creator.getGender();
		members = new HashSet<Clan>();
	}


	
	private Set<Clan> members;
	public Set<Clan> getMembers() {return members;}
	private void addMembers(Set<Clan> s) {members.addAll(s);} 
	public void addMember(Clan m) {members.add(m); m.setOrder(this);}
	public void removeMember(Clan m) {if (m == ruler) {selfDestruct(); return;} else {members.remove(m);}}
	public void selfDestruct() {members.clear();}
	public void moveTo(Clan clan, Order newOrder) {
		int N = clan.getMinionNumber() + 1;
		for (Clan m : members) {
			if (N <= 0) {break;}
			if (clan == m || clan.isSomeBossOf(m)) {N--; moversLedger.add(m); m.setOrder(newOrder);}
		}
		newOrder.addMembers(moversLedger);
		this.members.removeAll(moversLedger);
		moversLedger.clear();
	}
	
	public Clan getRuler() {return ruler;}
	
	
	public void getQuest(Clan requester) {
		requester.MB.newQ(new PersecuteInfidel(requester));
	}
	
	private String getName() {
		String S = GobName.firstName(founderName[0], founderName[1], founderGender);
		if (S.endsWith("a")) {}
		else if (S.endsWith("us")) {S = S.substring(0, S.length()-2) + "a";}
		else {S += "a";}
		return S;
	}
	public String getFollowerName() {
		String S = getName();
		return (S.endsWith("n") || S.endsWith("m") ? S + "ese" : S + "n");
	}
	public String getNationName() {
		return "Nation of " + getName();
	}
	public String getTitle(Clan clan) {
		if (clan == getRuler()) {
			int n = members.size();
			if (n  > 50) {return (clan.getGender() == Defs.FEMALE ? "Empress" : "Emperor");}
			if (n  > 20) {return (clan.getGender() == Defs.FEMALE ? "Queen" : "King");}
		}
		int n = clan.getMinionNumber();
		if (n  > 10) {return (clan.getGender() == Defs.FEMALE ? "Lady" : "Lord");}
		if (n  > 5) {return (clan.getGender() == Defs.FEMALE ? "Madam" : "Sir");}
		return "";
	}
	
	

	public boolean preferableOver(Clan pov) {
		return preferableOver(null, pov);
	}
	public boolean preferableOver(Order other, Clan pov) {
		return false;
	}
	
	
}
