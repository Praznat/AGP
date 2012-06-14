package Sentiens;

import Defs.M_;
import Game.Defs;
import Game.Job;
import Sentiens.Values.Value;
import Shirage.Shire;

public class Stressor {
	public static interface Causable {}
	public static final int FEAR = 0;  //potential threat, detected by "patrolling" (ppl with high paranoia do more often)
	public static final int ANNOYANCE = 1;  //inability to succeed in quests
	public static final int INSULT = 2;  //something happens to lower your self worth
	public static final int LIFE_THREAT = 3;   //e.g. starvation
	public static final int HATRED = 4;  //immediately maxes out amygdala, calling enoughIsEnough()
	public static final int EXTREME_TRAUMA = 5;  //probably causes insanity
	public static final int PROMISE_BROKEN = 6;  //probably causes insanity
	private final int type;
	private Object target;
	
	public Stressor() {type = -1;}
	public Stressor(int t, Causable c) {type = t; target = c;}
	public Object getTarget() {return target;}
	public int getLevel(Clan doer) {
		// dont use useBeh please... it will be called several times in enoughIsEnough method
		switch (type) {
		case FEAR: return 2;
		case ANNOYANCE: return 1;
		case INSULT: return doer.FB.getBeh(M_.VANITY);
		case LIFE_THREAT: return 3 + doer.FB.getBeh(M_.MIERTE);
		case HATRED: return 16; //must be higher than others to avoid getting relieved
		case EXTREME_TRAUMA: return 50;
		case PROMISE_BROKEN: return doer.FB.getBeh(M_.STRICTNESS) / 3;
		default: return 0;
		}
	}
	public boolean respond(Clan responder) {  //TODO
		boolean success = false;
		if (target instanceof Clan) {
			if (type == HATRED) {}
			if (type == PROMISE_BROKEN) {}
			else {}
		}
		else if (target instanceof Shire) {
			success = responder.maybeEmigrate();
		}
		else if (target instanceof Value) {

		}
		else if (target instanceof Job) {
			responder.QB.newQ(Defs.FINDNEWJOB);
		}
		return success;
	}

	public boolean sameAndLessThan(Clan doer, Stressor other) {
		return (other.getTarget().equals(target) && getLevel(doer) <= other.getLevel(doer));
	}

}