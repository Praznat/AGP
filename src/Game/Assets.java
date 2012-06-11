package Game;

import Markets.MktO;
import Sentiens.Clan;

public class Assets implements Defs {
	
	public static int getRentGood(int asset) {
		switch (asset) {
		case land:   return rentland;
		case bovad: case donkey: case lobodonkey: return rentanimal;
		default: return -1;
		}
	}

	public static void gainXWeapon(Clan doer, short weapon) {
		//IF u like it better than ur current one
		equip(doer, weapon);
	}
	public static void loseXWeapon(Clan doer, short weapon) {
		unequip(doer, weapon);
	}
	public static void gain(Clan doer, int good) {
		gain(doer, good, 1);   // rent only if number of goods is 1
		// should be placing offer for good here (rather than after this method),
		// so that if it gets done you dont have to place the rent good
		int rg = getRentGood(good);
		if (rg != -1) {doer.myMkt(rg).sellFair(doer);}
	}
	public static void lose(Clan doer, int good) {
		lose(doer, good, 1);   // rent only if number of goods is 1
		int rg = getRentGood(good);
		if (rg != -1) {doer.myMkt(rg).removeOffer(doer);}
	}
	public static void gain(Clan doer, int good, int N) {
		doer.incAssets(good, N);
	}
	public static void lose(Clan doer, int good, int N) {
		doer.decAssets(good, N);
	}
	private static void equip(Clan doer, short w) {
		doer.setXWeapon(w);
	}
	private static void unequip(Clan doer, short w) {
		doer.setXWeapon(XWeapon.NULL);
		//make sure this doesnt conflict with actual weapon. will have to remove one weapon description at some point
	}
	public static int FVmin(Clan doer, int good) {
		switch (good) {
		case land:   return MktO.annuity(doer.myMkt(rentland).bestBid(), doer);
		case donkey:   return MktO.annuity(doer.myMkt(rentanimal).bestBid(), doer);
		case lobodonkey:   return doer.myMkt(donkey).bestBid();
		default: return 0;
		}
	}
	public static int FVmax(Clan doer, int good) {
		switch (good) {
		case rentland:   return MktO.interest(doer.myMkt(land).bestOffer(), doer);
		case rentanimal:   return MktO.interest(Math.max(doer.myMkt(lobodonkey).bestOffer(), doer.myMkt(bovad).bestOffer()), doer);
		default: return Integer.MAX_VALUE;
		}
	}
	public static double estimateNAV(Clan POV, Clan clan) {
		int sum = 0;
		for (int g = 0; g < numGoods; g++) {
			sum += clan.getAssets(g) * clan.myMkt(g).sellablePX(POV);
		}
		return sum;
	}
}
