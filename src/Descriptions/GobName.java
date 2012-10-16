package Descriptions;

import AMath.Calc;
import Defs.M_;
import Defs.P_;
import Game.AGPmain;
import Game.Defs;
import Game.Job;
import Sentiens.Clan;
import Sentiens.Ideology;
import Sentiens.Values;
import Sentiens.Values.Value;

public class GobName {
	public static String firstName(byte fn, byte ln, boolean gender) {
		int numFN = 63; int numLN = 65;
		int[] N = {Calc.squeezeByte(fn, 0, numFN),Calc.squeezeByte(ln, 0, numLN)};
		String[] FN = new String[numFN];
		String[] LN = new String[numLN];


		FN[0] = "A";
		FN[1] = "Aha";
		FN[2] = "Ba";
		FN[3] = "Bee";
		FN[4] = "Bla";
		FN[5] = "Blee";
		FN[6] = "Blo";
		FN[7] = "Blou";
		FN[8] = "Bloo";
		FN[9] = "Blu";
		FN[10] = "Bo";
		FN[11] = "Booba";
		FN[12] = "Bu";
		FN[13] = "Cha";
		FN[14] = "Chee";
		FN[15] = "Da";
		FN[16] = "Dee";
		FN[17] = "Doo";
		FN[18] = "Du";
		FN[19] = "Dwa";
		FN[20] = "E";
		FN[21] = "Ehe";
		FN[22] = "Flee";
		FN[23] = "Flo";
		FN[24] = "Flou";
		FN[25] = "Fna";
		FN[26] = "Fnee";
		FN[27] = "Fnoo";
		FN[28] = "Glee";
		FN[29] = "Glo";
		FN[30] = "Glu";
		FN[31] = "Ghee";
		FN[32] = "Gho";
		FN[33] = "Gree";
		FN[34] = "Ho";
		FN[35] = "Ja";
		FN[36] = "Kra";
		FN[37] = "Kree";
		FN[38] = "Ma";
		FN[39] = "Mee";
		FN[40] = "Na";
		FN[41] = "O";
		FN[42] = "Oho";
		FN[43] = "Pra";
		FN[44] = "Proo";
		FN[45] = "Pee";
		FN[46] = "Poko";
		FN[47] = "Poo";
		FN[48] = "Plee";
		FN[49] = "Ploo";
		FN[50] = "Qua";
		FN[51] = "Quee";
		FN[52] = "Roo";
		FN[53] = "Ru";
		FN[54] = "Smee";
		FN[55] = "Spa";
		FN[56] = "Twa";
		FN[57] = "Twee";
		FN[58] = "Vee";
		FN[59] = "Wee";
		FN[60] = "Zaza";
		FN[61] = "Zna";
		FN[62] = "Znee";

		LN[0] = "bar";
		LN[1] = "bax";
		LN[2] = "ber";
		LN[3] = "ble";
		LN[4] = "bo";
		LN[5] = "bonk";
		LN[6] = "bor";
		LN[7] = "bod";
		LN[8] = "cka";
		LN[9] = "ckle";
		LN[10] = "cky";
		LN[11] = "dle";
		LN[12] = "dd";
		LN[13] = "do";
		LN[14] = "dor";
		LN[15] = "duh";
		LN[16] = "dwan";
		LN[17] = "ga";
		LN[18] = "gar";
		LN[19] = "gle";
		LN[20] = "gump";
		LN[21] = "ham";
		LN[22] = "ho";
		LN[23] = "honk";
		LN[24] = "ja";
		LN[25] = "kan";
		LN[26] = "ll";
		LN[27] = "lm";
		LN[28] = "mm";
		LN[29] = "mak";
		LN[30] = "mnak";
		LN[31] = "mo";
		LN[32] = "mok";
		LN[33] = "mor";
		LN[34] = "mple";
		LN[35] = "nad";
		LN[36] = "nax";
		LN[37] = "njarm";
		LN[38] = "nk";
		LN[39] = "pan";
		LN[40] = "pat";
		LN[41] = "per";
		LN[42] = "ppy";
		LN[43] = "quod";
		LN[44] = "snax";
		LN[45] = "spat";
		LN[46] = "tmo";
		LN[47] = "ton";
		LN[48] = "twax";
		LN[49] = "twod";
		LN[50] = "ty";
		LN[51] = "nt";
		LN[52] = "vap";
		LN[53] = "x";
		LN[54] = "xum";
		LN[55] = "xy";
		LN[56] = "zle";
		LN[57] = "znax";
		LN[58] = "jack";
		LN[59] = (gender == Defs.FEMALE ? "rc" : "rcus");
		LN[60] = (gender == Defs.FEMALE ? "x" : "xus");
		LN[61] = (gender == Defs.FEMALE ? "n" : "nus");
		LN[62] = (gender == Defs.FEMALE ? "m" : "mus");
		LN[63] = (gender == Defs.FEMALE ? "lb" : "lbus");
		LN[64] = (gender == Defs.FEMALE ? "t" : "tus");

		String suffix = "";
		if (gender == Defs.FEMALE) {
			switch ((fn * ln) % 3) {
			case 0: suffix += "a"; break;
			case 1: suffix += "i"; break;
			case 2: suffix += "et"; break;
			case -1: suffix += "el"; break;
			case -2: suffix += "ra"; break;
			default: break;
			}
		}
		
		return FN[N[0]]+LN[N[1]]+suffix;
	}
	public static String fullName(Clan goblin) {return fullName(goblin, goblin.getFirstName(), 0);}
	private static String fullName(Clan goblin, String N, int i) {
		if (i >= 5) {return N;}
		Value V = goblin.FB.getValue(i);
		if (V == Values.ALLEGIANCE || V == Values.PROPERTY || V == Values.MIGHT || V == Values.INFLUENCE || V == Values.MIGHT || V == Values.SPLENDOR) {
			String title = (goblin.myOrder() == null ? "" : goblin.myOrder().getTitle(goblin));
			if (title != "") {
				N = title + " " + N;
				if (goblin.getJob() == Job.HUNTERGATHERER) {return N + " the Barbarian";}
				P_ bestP = highestPrest(goblin);
				if (bestP == null || goblin.FB.getPrs(bestP) != 15) {return N;}
				switch (bestP) {
				case TYRRP: return N + " the " + tyrantWord(goblin.getID(), goblin.FB.getBeh(M_.BLOODLUST) > 11);
				case STRENGTH: return N + " the Mighty";
				case RSPCP: return N + " the Great";
				default: return N;
				}
			}
			else {return fullName(goblin, N, i+1);}
		}
		if (V == Values.HEALTH) {return N + " the Glutton";}
		if (V == Values.EXPERTISE) {return N + " the " + goblin.getJob().getDesc();}
		
		return fullName(goblin, N, i+1);
	}
	
	private static String tyrantWord(int i, boolean bloody) {
		switch (i % 5) {
		case 0: return (bloody ? "Merciless" : "Conqueror");
		case 1: return (bloody ? "Cruel" : "Unstoppable");
		case 2: return (bloody ? "Tyrant" : "Mountain");
		case 3: return (bloody ? "Terrible" : "Mighty");
		default: return (bloody ? "Bloodthirsty" : "Vanquisher");
		}
	}
	private static P_ highestPrest(Clan goblin) {
		P_[] candidates = new P_[] {P_.TYRRP, P_.STRENGTH, P_.RSPCP};
		P_ highest = null;   int max = 10;   int b;
		for (P_ p : candidates) {b = goblin.FB.getPrs(p); if (goblin.FB.getPrs(p) > max) {max = b; highest = p;}}
		return highest;
	}
}
