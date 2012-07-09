package Game;

import AMath.Calc;
import Defs.*;
import Questing.*;
import Questing.Quest.FindTargetAbstract;
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
		@Override
		public boolean equals(Object t) {
			String des = description();
			return (des.length() != 0 && des.equals(t.toString()));
		}
		@Override
		public String toString() {return description();}
	}
	public static abstract class ClanAction extends SetupableThing {
		protected Clan doer;
		public Clan getDoer() {return doer;}
	}
	public static abstract class ClanAlone extends ClanAction {
		protected int content;
		public void setup(Clan d) {doer = d; setup = true;}
		public void setup(Clan d, int c) {doer = d; content = c; setup = true;}
		public int getContent() {return content;}
	}
	public static abstract class ClanOnClan extends ClanAction {
		protected Clan doee;   protected int content;
		public void setup(Clan d1, Clan d2, int c) {doer = d1; doee = d2; content = c; setup = true;}
		public Clan getDoee() {return doee;}
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
	

	public static final ClanAlone NOTHING = new ClanAlone() {
		@Override
		public String description() {return "Nothing";}
		@Override
		protected boolean tryExecute() {return true;}
	};

	public static final ClanAlone addQuest(final Q_ quest) {
		return new ClanAlone() {
			@Override
			public String description() {return Quest.QtoQuest(doer, quest).description();}
			@Override
			protected boolean tryExecute() {doer.MB.newQ(Quest.QtoQuest(doer, quest)); return true;}
		};
	}
	public static final ClanOnClan chooseTarget(final Clan target) {
		return new ClanOnClan() {
			@Override
			protected boolean tryExecute() {
				Quest q = doer.MB.QuestStack.peek();
				if (q instanceof FindTargetAbstract) {
					((FindTargetAbstract) q).setTarget(doee);
					doer.MB.QuestStack.pop();
					return true;
				}
				else return false;
			}
			@Override
			public String description() {return doee.getNomen();}
		};
	}
	public static final ClanOnClan PAY_RESPECT = new ClanOnClan() {
		@Override
		protected boolean tryExecute() {
			doer.FB.downPrest(P_.RSPCP);
			doee.FB.upPrest(P_.RSPCP);
			return true;
		}
		@Override
		public String description() {return doer.getNomen() + " pays " + content + " millet to " + doee.getNomen();}
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
		protected boolean tryExecute() {
			boolean enough = doer.getMillet() >= content;
			int amt = (enough ? content : doer.getMillet());
			doer.decAssets(Defs.millet, amt);
			doee.incAssets(Defs.millet, amt);
			return enough;
		}
		@Override
		public String description() {return doer.getNomen() + " pays " + Math.min(content, doer.getMillet()) + " millet to " + doee.getNomen();}
	};
	public static final ClanOnClan DECLARE_ALLEGIANCE = new ClanOnClan() {
		@Override
		protected boolean tryExecute() {doer.join(doee);   return true;}
		@Override
		public String description() {return doer.getNomen() + " switches allegiance from " + doer.FB.getDiscName(Defs.LORD) + " to " + doee.getNomen();}
	};
	public static final ClanOnClan CONVERT_TO_CREED = new ClanOnClan() {
		@Override
		protected boolean tryExecute() {doer.FB.setDisc(Defs.CREED, doee.FB.getDeusInt());   return true;}
		@Override
		public String description() {return doer.getNomen() + " becomes a follower of the " + doee.FB.getDiscName(Defs.CREED);}
	};
	public static final ClanAlone NATURALIZE_HERE = new ClanAlone() {
		@Override
		protected boolean tryExecute() {doer.FB.setDisc(Defs.HOMELAND, doer.myShire().getXY());   return true;}
		@Override
		public String description() {return doer.getNomen() + " becomes native of " + doer.myShire().getName();}
	};
}
