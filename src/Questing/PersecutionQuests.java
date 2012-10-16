package Questing;

import AMath.Calc;
import Defs.M_;
import Game.Contract;
import Game.Defs;
import Questing.Quest.FindTarget;
import Questing.Quest.FindTargetAbstract;
import Questing.Quest.TargetQuest;
import Sentiens.Clan;
import Sentiens.GobLog;
import Sentiens.Stressor;

public class PersecutionQuests {

	private static final FindTarget EXILECONDITION = new FindTarget() {
		@Override
		public boolean meetsReq(Clan POV, Clan target) {
			return POV.myShire()!=target.myShire();
		}
	};

	public static abstract class PersecuteAbstract extends TargetQuest {
		protected int timesLeft = Defs.E;
		protected FindTargetAbstract findWhat;
		public PersecuteAbstract(Clan P) {super(P); resetTime();}
		//perfect to test contracts... make demand, e.g. money, respect, conversion, etc. in exchange for retraction of physical threat
		@Override
		public void pursue() {
			if (timesLeft == 0) {success(); return;}  //"success" cuz no one to persecute
			if (target == null) {Me.MB.newQ(findWhat); timesLeft--; return;}
			Contract.getInstance().enter(target, Me);
			setContractDemand();
			Contract.getInstance().setOfferThreat();  //wait is this the only difference with "recruit"?
			boolean accepted = Contract.getInstance().acceptable();
			if (accepted) {
				Me.addReport(GobLog.converted(target, accepted));
				target.addReport(GobLog.wasConverted(Me, accepted));
				Contract.getInstance().enact();
				this.success(target); //relieves previous anger from target
			}
			else {Me.MB.newQ(new DestroyQuest(Me, target, EXILECONDITION));}
			return;
		}
		public void failDestroy() {timesLeft--;}
		protected abstract void setContractDemand();
		private void resetTime() {timesLeft = 1 + Me.useBeh(M_.PATIENCE);}
		@Override
		public String shortName() {return "Persecute";}
		protected void addLog() {} //add ultimatum report
	}
	public static class PersecuteHeretic extends PersecuteAbstract {
		public PersecuteHeretic(Clan P) {super(P); findWhat = new FindHeretic(Me);}
		@Override
		protected void setContractDemand() {} // TODO ???
		@Override
		public String description() {return "Persecute " + (target == null ? "Heretic" : target.getNomen());}
	}
	public static class PersecuteInfidel extends PersecuteAbstract {
		public PersecuteInfidel(Clan P) {super(P); findWhat = new FindInfidel(Me);}
		@Override
		protected void setContractDemand() {Contract.getInstance().setDemandAllegiance();}
		@Override
		public String description() {return "Persecute " + (target == null ? "Infidel" : target.getNomen());}
	}
	public static class PersecuteForeigner extends PersecuteAbstract {
		public PersecuteForeigner(Clan P) {super(P); findWhat = new FindForeigner(Me);}
		@Override
		protected void setContractDemand() {Contract.getInstance().setDemandExile();}
		@Override
		public String description() {return "Persecute " + (target == null ? "Foreigner" : target.getNomen());}
	}

	public static class FindHeretic extends FindTargetAbstract {
		public FindHeretic(Clan P) {super(P);}
		@Override
		public boolean meetsReq(Clan POV, Clan target) {
			boolean success = target.FB.getDeusInt() != POV.FB.getDeusInt();
			Me.addReport(GobLog.findSomeone((success ? target : null), "heretic"));
			return success;
		}
		@Override
		public String description() {return "Find Heretic";}
		@Override
		protected void failure(Stressor.Causable blamee) {failure();}
	}	
	public static class FindInfidel extends FindTargetAbstract {
		public FindInfidel(Clan P) {super(P);}
		@Override
		public boolean meetsReq(Clan POV, Clan target) {
			boolean success = target.myOrder() != POV.myOrder();
			Me.addReport(GobLog.findSomeone((success ? target : null),"infidel"));
			return success;
		}
		@Override
		public String description() {return "Find Infidel";}
		@Override
		protected void failure(Stressor.Causable blamee) {failure();}
	}	
	public static class FindForeigner extends FindTargetAbstract {
		public FindForeigner(Clan P) {super(P);}
		@Override
		public boolean meetsReq(Clan POV, Clan target) {
			boolean success = target.FB.getHomeland() != POV.FB.getHomeland();
			Me.addReport(GobLog.findSomeone((success ? target : null),"foreigner"));
			return success;
		}
		@Override
		public String description() {return "Find Foreigner";}
		@Override
		protected void failure(Stressor.Causable blamee) {failure();}
	}
}
