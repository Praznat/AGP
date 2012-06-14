package Questing;

import Questing.Quest.FindTarget;
import Questing.Quest.FindTargetAbstract;
import Questing.Quest.TargetQuest;
import Sentiens.Clan;

public class PersecutionQuests {

	private static abstract class PersecuteAbstract extends TargetQuest {
		public PersecuteAbstract(Clan P) {super(P);}
		//perfect to test contracts... make demand, e.g. money, respect, conversion, etc. in exchange for retraction of physical threat
	}
	public static class PersecuteHeretic extends PersecuteAbstract {
		public PersecuteHeretic(Clan P) {super(P);}
		@Override
		public void pursue() {
			if (target == null) {Me.MB.newQ(new FindHeretic(Me)); return;}
		}
	}
	public static class PersecuteInfidel extends PersecuteAbstract {
		public PersecuteInfidel(Clan P) {super(P);}
		@Override
		public void pursue() {
			if (target == null) {Me.MB.newQ(new FindInfidel(Me)); return;}
		}
	}
	public static class PersecuteForeigner extends PersecuteAbstract {
		public PersecuteForeigner(Clan P) {super(P);}
		@Override
		public void pursue() {
			if (target == null) {Me.MB.newQ(new FindForeigner(Me)); return;}
		}
	}
	
	public static class FindHeretic extends FindTargetAbstract {
		public FindHeretic(Clan P) {super(P);}
		@Override
		public boolean meetsReq(Clan POV, Clan target) {
			return target.FB.getDeusInt() != POV.FB.getDeusInt();
		}
	}	
	public static class FindInfidel extends FindTargetAbstract {
		public FindInfidel(Clan P) {super(P);}
		@Override
		public boolean meetsReq(Clan POV, Clan target) {
			return target.FB.getRex() != POV.FB.getRex();
		}
	}	
	public static class FindForeigner extends FindTargetAbstract {
		public FindForeigner(Clan P) {super(P);}
		@Override
		public boolean meetsReq(Clan POV, Clan target) {
			return target.FB.getHomeland() != POV.FB.getHomeland();
		}
	}
}
