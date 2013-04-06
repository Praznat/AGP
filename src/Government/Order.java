package Government;

import java.util.HashSet;
import java.util.Set;

import Descriptions.GobName;
import Game.Defs;
import Questing.PersecutionQuests.PersecuteInfidel;
import Sentiens.Clan;
import Shirage.Shire;

public class Order {
	

	private Clan ruler;
	private final byte[] founderName;
	private final boolean founderGender;
	
	public static Order createBy(Clan creator) {
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
		Set<Clan> movers = Ledger.getFollowers(clan, true, true);
		for (Clan mover : movers) {mover.setOrder(newOrder);}
		newOrder.addMembers(movers);
		this.members.removeAll(movers);
	}
	
	public Clan getRuler() {return ruler;}
	public int size() {return members.size();}
	
	public Set<Clan> getFollowers(Clan leader, boolean includeMe, boolean includeSubs) {return Ledger.getFollowers(leader, includeMe, includeSubs);}
	public Set<Clan> getFollowers(Clan leader, Shire place, boolean includeMe, boolean includeSubs) {return Ledger.getFollowers(leader, place, includeMe, includeSubs);}
	
	public static String getName(byte founderName1, byte founderName2, boolean founderGender) {
		String S = GobName.firstName(founderName1, founderName2, founderGender);
		if (S.endsWith("a") || S.endsWith("n") || S.endsWith("m")) {}
		else if (S.endsWith("us")) {S = S.substring(0, S.length()-2) + "a";}
		else {S += "a";}
		return S;
	}
	private String getName() {
		return getName(founderName[0], founderName[1], founderGender);
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
		int n = clan.getMinionTotal();
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

		public static Set<Clan> getFollowers(Clan leader, boolean includeMe, boolean includeSubs) {
			pop.clear();
			int N = leader.getMinionTotal() + (includeMe ? 1 : 0);
			for (Clan m : leader.myOrder().getMembers()) {
				if (N <= 0) {break;}
				if ((leader == m && includeMe) ||
						(includeSubs ? leader.isSomeBossOf(m) : leader.isDirectBossOf(m))) {N--; pop.add(m);}
			}
			return pop;
		}
		public static Set<Clan> getFollowers(Clan leader, Shire place, boolean includeMe, boolean includeSubs) {
			pop.clear();
			int N = leader.getMinionTotal() + (includeMe ? 1 : 0);
			for (Clan m : leader.myOrder().getMembers()) {
				if (N <= 0) {break;}
				if (m.myShire() == place && ((leader == m && includeMe) ||
						(includeSubs ? leader.isSomeBossOf(m) : leader.isDirectBossOf(m)))) {N--; pop.add(m);}
			}
			return pop;
		}
	}
	
	@Override
	public String toString() {return getNationName();}
	
}


