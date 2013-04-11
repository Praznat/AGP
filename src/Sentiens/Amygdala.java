package Sentiens;

import java.util.*;

import Defs.M_;

public class Amygdala {

	private final Clan parent;
	private final Collection<Stressor> stressors;
	public Amygdala(Clan P) {
		parent = P;
		stressors = new ArrayList<Stressor>();
	}
	public void add(Stressor S) {
		if(getStressLevel() > 1) {
			enoughIsEnough(S);
		}
		else {stressors.add(S);}
	}
	/** put veins in goblins eyes based on this !!*/
	public double getStressLevel() {   //can be above 1 if failing to respond to stress
		double sum = 0;   double denom = parent.FB.getBeh(M_.TEMPER);
		for (Stressor stressor : stressors) {sum += stressor.getLevel(parent);}
		return sum / denom;
	}
	public void enoughIsEnough(Stressor lastStress) {
		if (lastStress.respond(parent)) {   //respond to this one
			relieveFrom(lastStress);
		}   else {
			stressors.add(lastStress); // if false too often this will get huge and cause stack overflow
		}
	}
	public void relieveFrom(Stressor stress) {
		Stressor[] removeStressors = new Stressor[stressors.size()];  int removeN = 0;
		for (Stressor stressor : stressors) {   //remove all stressors same and less than this one
			if (stressor.sameAndLessThan(parent, stress)) {
				removeStressors[removeN++] = stressor;   //stressors.remove(stressor);
			}
		}
		for (removeN--; removeN >= 0; removeN--) {stressors.remove(removeStressors[removeN]);}
	}
	public void catharsis(int threshold) {
		Stressor[] removeStressors = new Stressor[stressors.size()];  int removeN = 0;
		for (Stressor stressor : stressors) {   //remove all stressors same and less than this one
			if (stressor.getLevel(parent) <= threshold) {
				removeStressors[removeN++] = stressor;   //stressors.remove(stressor);
			}
		}
		for (removeN--; removeN >= 0; removeN--) {stressors.remove(removeStressors[removeN]);}
	}
	
	@Override
	public String toString() {return stressors.toString();}
}

