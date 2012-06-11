package Sentiens;

import Defs.M_;
import Defs.P_;
import Game.AGPmain;
import Game.Defs;

public abstract class Formula implements Defs {
	public Formula() {}
	public int value(Clan pov, Clan obj, int p) {
		return 0;
	}
	public String desc(Clan eu, String name) {return name;}
	
}





class NoseVal extends Formula {
	public NoseVal() {}
	public int value(Clan pov, Clan obj, int p) {
		return obj.FB.getBeh(M_.NOSELX) + obj.FB.getBeh(M_.NOSERX);
	}
}
class LipVal extends Formula {
	public LipVal() {}
	public int value(Clan pov, Clan obj, int p) {
		return obj.FB.getBeh(M_.MOUTHLH);
	}
}
class EyeVal extends Formula {
	public EyeVal() {}
	public int value(Clan pov, Clan obj, int p) {
		return obj.FB.getBeh(M_.EYELW) + obj.FB.getBeh(M_.EYERW);
	}
}
class MVal extends Formula {
	public MVal() {}
	public int value(Clan pov, Clan obj, int p) {
		return obj.FB.getPrs(P_.MARTIALP);
	}
}

class TVal extends Formula {
	public TVal() {}
	public int value(Clan pov, Clan obj, int p) {
		return 
		(1 - pov.FB.getBeh(M_.INVORTRD)) * obj.FB.getPrs(P_.TRADEP) + 
		pov.FB.getBeh(M_.INVORTRD) * obj.FB.getPrs(P_.INVESTP);
	}
}

class DVal extends Formula {
	public DVal() {} // discrete parameter
	public int value(Clan pov, Clan obj, int p) {
		if (obj.FB.getDeusInt() == pov.FB.getDeusInt()) {return 1;}
		return 0;
	}
	public String desc(Clan eu, String name) {
		return name + " for the " + eu.FB.getDeusName();
	}
}
class HLVal extends Formula {
	public HLVal() {} // discrete parameter
	public int value(Clan pov, Clan obj, int p) {
		if (obj.FB.getDisc(p) == pov.FB.getDisc(p)) {return 1;}
		return 0;
	}
	public String desc(Clan eu, String name) {
		return name + " to " + eu.myShire().getName();
	}
}
class JVal extends Formula {
	public JVal() {} // discrete parameter
	public int value(Clan pov, Clan obj, int p) {
		if (obj.FB.getDisc(p) == pov.FB.getDisc(p)) {return 1;}
		return 0;
	}
}
class RexVal extends Formula {
	public RexVal() {} // discrete parameter
	public int value(Clan pov, Clan obj, int p) {
		if (obj.getID() == pov.FB.getDisc(p)) {return 3;}
		if (obj.FB.getDisc(p) == pov.FB.getDisc(p)) {return 1;}
		else {return 0;}
	}
	public String desc(Clan eu, String name) {return name + " to " + 
		AGPmain.TheRealm.getClan(eu.FB.getDisc(LORD)).getNomen();
	}
}

class PVal extends Formula {
	public PVal() {}
	public int value(Clan pov, Clan obj, int p) {
		return obj.FB.getPrs(p);
	}
}

class GVal extends Formula {
	public GVal() {}
	public int value(Clan pov, Clan obj, int p) {
		return 0;
		// return obj.getSupply(p);
	}
}