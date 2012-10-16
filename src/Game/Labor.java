package Game;

import Questing.WorkQuests.LaborQuest;
import Sentiens.Clan;
import Sentiens.GobLog;
import AMath.Calc;
import Avatar.SubjectivelyComparable;
import Defs.M_;
import Defs.P_;

public class Labor implements Act, Defs {

	private static final int MAXACTS = 10; // max acts in a job

	private static int[] tmpOut = new int[1 + MAXACTS];
	private static int[] tmpIn = new int[1 + MAXACTS];

	private String desc;
	private Logic input;
	private Logic output;
	private int[] allPossibleInputs = {}; // one for each logic
	private int envc, envr, chance; // skill can point to prestige or quest!
	private Job njob;
	private P_ skill;

	public Labor(String n, Logic in, Logic out, P_ s, Job J, int denom) {
		desc = n;
		input = in;
		output = out;
		skill = s;
		njob = J;
		chance = denom;
	}

	public Labor(String n, Logic in, Logic out, P_ s, int ec, int er, Job J,
			int denom) {
		desc = n;
		input = in;
		output = out;
		skill = s;
		envc = ec;
		envr = er;
		njob = J;
		chance = denom;
	}

	public Labor(String n, Logic g, P_ s, Job J, int denom) {
		desc = n;
		input = g;
		output = g;
		skill = s;
		njob = J;
		chance = denom;
	}

	public int[] expOut(Clan doer) {
		// {fill txpP with probE * probS * expPrice, determined outGs, Es}
		int[] tmp = output.getBest(doer, Logic.SELL);
		int k = 0;
		while (tmp[k] != E) {
			tmpOut[k] = tmp[k++];
		}
		tmpOut[k] = E;
		return tmpOut;
	}

	public int[] expIn(Clan doer) {
		// {fill txpL with sum of expPrices, determined inGs, Es}
		int[] tmp = input.getBest(doer, Logic.BUY);
		int k = 0;
		while (tmp[k] != E) {
			tmpIn[k] = tmp[k++];
		}
		tmpIn[k] = E;
		return tmpIn;
	}
	
	public int estimateProfit(Clan doer) {
		return doer.confuse(this.expOut(doer)[0] - this.expIn(doer)[0]);
	}

	public void storeAllInputsInWM(Clan doer) {
		// fill WORKMEMO with allPossibleInputs
		LaborQuest lq = (LaborQuest) doer.MB.QuestStack.peek();
		for (int i = 0; i < allPossibleInputs.length; i++) {
			lq.setWM(allPossibleInputs[i], i);
		}
		lq.setWM(E, allPossibleInputs.length);
	}

	private int inAll(int g) { // can improve speed by branching search from mid
		int i = 0;
		for (; i < allPossibleInputs.length; i++) {
			if (allPossibleInputs[i] == g) {
				return i;
			}
		}
		return E;
	}

	public void addToAll(int g) {
		int i = inAll(g);
		if (i != E) {
			allPossibleInputs[i] = g;
		} else {
			int[] tmp = new int[allPossibleInputs.length + 1];
			System.arraycopy(allPossibleInputs, 0, tmp, 0,
					allPossibleInputs.length);
			tmp[allPossibleInputs.length] = g;
			allPossibleInputs = tmp;
		}
	}

	public static void socializeOrIntrospect(Clan doer) {
		if (Calc.pMem(doer.useBeh(M_.EXTROVERSION))) {
			socialize(doer);
		} else {
			introspect(doer);
		}
	}

	private static void socialize(Clan doer) {
		// discuss lastMeme
	}

	private static void introspect(Clan doer) {
		// randomize lastMeme
	}
	
	@Override
	public void doit(Clan doer) {
		ponderOrLearn(doer);
	}

	private void ponderOrLearn(Clan doer) {
		if (Calc.pMem(doer.useBeh(M_.MADNESS))) {
			ponder(doer);
		} else {
			learnSkill(doer);
		}
	}

	private void ponder(Clan doer) {
		int C = chance;
		Job J = njob;
		if (J == null) {
			C = (16 - doer.useBeh(M_.RESPENV)) * 500;
			J = Job.HUNTERGATHERER;
		}
		if (AGPmain.rand.nextInt(C) == 0) {
			doer.addReport(GobLog.discovery(J));
			doer.setAspiration(J);
		}
	}

	private void learnSkill(Clan doer) {
		final int meatModifier = (doer.eatMeat() ? 2 : 1);
		final int pctStrDown = 5 / meatModifier;
		final int pctStrUp = 15 * meatModifier;
		boolean success = false;
		if (skill == null) {
			if (Calc.pPercent(pctStrDown)) {
				doer.FB.downPrest(P_.STRENGTH);
			}
			return; // strength down
		}
		switch (skill) {
		case STRENGTH:
			if (Calc.pPercent(pctStrUp)) {
				success = true; doer.FB.upPrest(P_.STRENGTH);
			}
			break; // strength up
		case ARTISTRY:
		case LOBOTOMY:
			if (Calc.pPercent(pctStrDown)) {
				doer.FB.downPrest(P_.STRENGTH);
			} // strength down
		case MARKSMANSHIP:
		case MASONRY:
		case CARPENTRY:
		case SMITHING:
			if (Calc.pPercent(16 - doer.FB.getPrs(skill))) {
				success = true; doer.FB.upPrest(skill);
			}
			break; // strength unchanged
		default:
			break;
		}
		if (success) {doer.addReport(GobLog.practice(skill));}
	}

	public P_ skill() {
		return skill;
	}

	@Override
	public int getSkill(Clan clan) {
		return (skill == null ? 0 : clan.FB.getPrs(skill));
	}

	@Override
	public String getDesc() {
		return desc;
	}
	@Override
	public String toString() {
		return desc;
	}

	public static Labor newCraft(String n, Logic in, Logic out, P_ s, Job J,
			int denom) {
		Labor newAct = new Labor(n, in, out, s, J, denom);
		in.addNodesToAll(newAct);
		return newAct;
	}

	public static Labor newReapC(String n, Logic in, Logic out, P_ s, int c,
			Job J, int denom) {
		Labor newAct = new Labor(n, in, out, s, c, E, J, denom);
		in.addNodesToAll(newAct);
		return newAct;
	}

	public static Labor newReapR(String n, Logic in, Logic out, P_ s, int r,
			Job J, int denom) {
		Labor newAct = new Labor(n, in, out, s, E, r, J, denom);
		in.addNodesToAll(newAct);
		return newAct;
	}

	public static Labor newTrade(String n, Logic g, Job J, int denom) {
		Labor newAct = new Labor(n, g, null, J, denom);
		// g.addNodesToAll(newAct);
		return newAct;
	}

}