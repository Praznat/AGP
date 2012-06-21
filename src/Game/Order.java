package Game;

import java.util.HashSet;
import java.util.Set;

import Descriptions.GobName;
import Questing.PersecutionQuests.PersecuteInfidel;
import Sentiens.Clan;
import Shirage.Shire;

public class Order {
	

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
		Set<Clan> movers = Ledger.getFollowers(clan, true);
		for (Clan mover : movers) {mover.setOrder(newOrder);}
		newOrder.addMembers(movers);
		this.members.removeAll(movers);
	}
	
	public Clan getRuler() {return ruler;}
	public int size() {return members.size();}
	
	public Set<Clan> getFollowers(Clan leader, boolean includeMe) {return Ledger.getFollowers(leader, includeMe);}
	public Set<Clan> getFollowers(Clan leader, Shire place, boolean includeMe) {return Ledger.getFollowers(leader, place, includeMe);}
	
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
	
	
	private static class Ledger {
		private static Set<Clan> pop = new HashSet<Clan>();

		public static Set<Clan> getFollowers(Clan leader, boolean includeMe) {
			pop.clear();
			int N = leader.getMinionNumber() + (includeMe ? 1 : 0);
			for (Clan m : leader.myOrder().getMembers()) {
				if (N <= 0) {break;}
				if ((leader == m && includeMe) || leader.isSomeBossOf(m)) {N--; pop.add(m);}
			}
			return pop;
		}
		public static Set<Clan> getFollowers(Clan leader, Shire place, boolean includeMe) {
			pop.clear();
			int N = leader.getMinionNumber() + (includeMe ? 1 : 0);
			for (Clan m : leader.myOrder().getMembers()) {
				if (N <= 0) {break;}
				if (m.myShire() == place && ((leader == m && includeMe) || leader.isSomeBossOf(m))) {N--; pop.add(m);}
			}
			return pop;
		}
	}
	
	
}


