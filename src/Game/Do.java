package Game;

import Defs.P_;
import Sentiens.Clan;
import Sentiens.Values;
import Shirage.Shire;

public class Do {
	public static interface Thing {
		public boolean doit();
		public String description();
	}
	public static abstract class SetupableThing implements Thing {
		protected boolean setup = false;
		public boolean doit() {if (setup) {setup = false; return tryExecute();} else {return (1 / 0 == 8);}}
		protected abstract boolean tryExecute();
	}
	public static abstract class ClanAction extends SetupableThing {
		protected Clan doer;
		public abstract double evaluate(Clan POV);
	}
	public static abstract class ClanAlone extends ClanAction {
		protected int content;
		public void setup(Clan d, int c) {doer = d; content = c; setup = true;}
	}
	public static abstract class ClanOnClan extends ClanAction {
		protected Clan doee;   protected int content;
		public void setup(Clan d1, Clan d2, int c) {doer = d1; doee = d2; content = c; setup = true;}
	}
	public static abstract class ClanOnShire extends ClanAction {
		Shire place;   protected int content;
		public void setup(Clan d, Shire p, int c) {doer = d; place = p; content = c; setup = true;}
	}
	
	public static Thing ShowRandomGoblin = new Thing() {
		@Override
		public boolean doit() {
			AGPmain.mainGUI.GM.loadClan(AGPmain.TheRealm.getRandClan());   return true;
		}
		@Override
		public String description() {
			return "Random Goblin";
		}
	};
	public static Thing ShowRandomShire = new Thing() {
		@Override
		public boolean doit() {
			AGPmain.mainGUI.SM.loadShire(AGPmain.TheRealm.getRandClan().myShire());   return true;
		}
		@Override
		public String description() {
			return "Random Shire";
		}
	};
	public static Thing StepOnce = new Thing() {
		@Override
		public boolean doit() {
			AGPmain.TheRealm.goOnce();   return true;
		}
		@Override
		public String description() {
			return "Step Once";
		}
	};
	
	
	
	public static final ClanOnClan PAY_RESPECT = new ClanOnClan() {
		@Override
		public boolean tryExecute() {
			doer.FB.downPrest(P_.RSPCP);
			doee.FB.upPrest(P_.RSPCP);
			return true;
		}
		@Override
		public String description() {return doer.getNomen() + " pays " + content + " millet to " + doee.getNomen();}
		@Override
		public double evaluate(Clan POV) {
			if (POV != doer && POV != doee) return 0;
			int sign = (POV == doer ? -1 : 1);
			double out = Values.logComp(POV.getMillet() + sign * content, POV.getMillet());
			return Values.inIsolation(out, Values.POPULARITY, POV);
		}
	};
	public static abstract class PayTribute extends ClanOnClan {
		public void adjustContentToEqual(Clan POV, double value) {
			if (POV != doer) {return;} //only bothered writing case of doer being POV for now
			int curMillet = POV.getMillet();
			if (curMillet == 0) {content = 1;}  //or maybe the amount necessary to survive for a day?
			else {
				content = (int) Math.round(Math.exp(value) * (double)curMillet - curMillet);
			}
		}
	}
	public static final ClanOnClan PAY_TRIBUTE = new PayTribute() {
		@Override
		public boolean tryExecute() {
			if (doer.getMillet() >= content) {
				doer.decAssets(Defs.millet, content);
				doee.incAssets(Defs.millet, content);
				return true;
			}
			else {return false;}
		}
		@Override
		public String description() {return doer.getNomen() + " pays " + content + " millet to " + doee.getNomen();}
		@Override
		public double evaluate(Clan POV) {   //boss wont care
			if (POV != doer && POV != doee) return 0;
			int sign = (POV == doer ? -1 : 1);
			double out = Values.logComp(POV.getMillet() + sign * content, POV.getMillet());
			return Values.inIsolation(out, Values.WEALTH, POV);
		}
	};
	public static final ClanOnClan DECLARE_ALLEGIANCE = new ClanOnClan() {
		@Override
		public boolean tryExecute() {doer.join(doee);   return true;}
		@Override
		public String description() {return doer.getNomen() + " switches allegiance from " + doer.FB.getDiscName(Defs.LORD) + " to " + doee.getNomen();}
		@Override
		public double evaluate(Clan POV) {
			double out;
			if (POV == doer) {
				Clan curRex = doer.FB.getRex();
				if (curRex == doer || curRex == doee ||
						doee.isSomeBossOf(curRex)) {out = 0;}
				out = Values.inIsolation(Values.MINVAL, Values.LOYALTY, POV);
			}
			else if (POV == doee) {
				out = Values.logComp(doee.getMinionNumber() + 1, doee.getMinionNumber());
				out = Values.inIsolation(out, Values.NUMVASSALS, POV);
			}
			else {out = 0;}
			return out;
		}
	};
}
