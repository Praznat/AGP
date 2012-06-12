package Sentiens;

import Game.AGPmain;
import Game.Do.*;
import Shirage.Shire;

public class Contract {
	private Clan contractor;
	private Questy duty;
	private Term[] terms;
	
	public void callDuty() {
		contractor.QB.newQ(0);  //whatever quest becomes
	}
	
	public double assess() {
		double sum = 0;
		for (Term T : terms) {
			T.setup();
			sum += T.clanAction.evaluate(contractor);
		}
		return sum;
	}
	public void fulfill() {
		for (Term T : terms) {
			T.setup();
			boolean success = T.clanAction.doit();
		}
	}
	
	public boolean preferableTo(Contract C) {
		return (assess() > C.assess());
	}
	
	
	public static class Term {
		private int content1, content2, content3, content4, content5;
		ClanAction clanAction;
		public void define(ClanAction ce) {clanAction = ce;}
		public void define(ClanAction ce, int c1) {clanAction = ce;content1 = c1;}
		public void define(ClanAction ce, int c1, int c2) {clanAction = ce;content1 = c1;content2 = c2;}
		public void define(ClanAction ce, int c1, int c2, int c3) {clanAction = ce;content1 = c1;content2 = c2;content3 = c3;}
		public void define(ClanAction ce, int c1, int c2, int c3, int c4) {clanAction = ce;content1 = c1;content2 = c2;content3 = c3;content4 = c4;}
		public void define(ClanAction ce, int c1, int c2, int c3, int c4, int c5) {clanAction = ce;content1 = c1;content2 = c2;content3 = c3;content4 = c4;content5 = c5;}
		public void setup() {
			if (clanAction instanceof ClanAlone) {
				((ClanAlone) clanAction).setup(clan(content1), content2);   return;
			}
			else if (clanAction instanceof ClanOnClan) {
				((ClanOnClan) clanAction).setup(clan(content1), clan(content2), content3);   return;
			}
			else if (clanAction instanceof ClanOnShire) {
				((ClanOnShire) clanAction).setup(clan(content1), shire(content2), content3);   return;
			}
		}
		private Clan clan(int i) {return AGPmain.TheRealm.getClan(i);}
		private Shire shire(int i) {return AGPmain.TheRealm.getShire(i);}
	}

	
}
