package Sentiens;

import Game.AGPmain;
import Sentiens.Law.Commandment;

public class CognitiveDissonance {
	public static void doMorals(Clan clan) {
		doMorals(clan, clan);
	}
	public static void doMorals(Clan teacher, Clan student) {
		Commandment[] studentCommandments = student.FB.commandments.list;
		Commandment[] teacherCommandments = teacher.FB.commandments.list;
		int cardinal = -1;
		//teachers sins are not bad
		int worst = 0; 
		for (int i = 0; i < teacherCommandments.length; i++) {
			final int t = teacherCommandments[i].getTransgressions();
			if (studentCommandments[i].isActive() && t > worst) {worst = t; cardinal = i;}
		}
		if (cardinal >= 0) {setCommandment(student, studentCommandments[cardinal], false);}
		//teachers purities are good
		double best = 1; cardinal = -1;
		for (int i = 0; i < teacherCommandments.length; i++) {
			final double este = teacherCommandments[i].getTransgressions() == 0 ? AGPmain.rand.nextDouble() : 1;
			if (!studentCommandments[i].isActive() && este < best) {best = este; cardinal = i;}
		}
		if (cardinal >= 0) {setCommandment(student, studentCommandments[cardinal], true);}
	}
	private static void setCommandment(Clan clan, Commandment com, boolean enable) {
		com.setActive(enable);
		clan.addReport(GobLog.decidedMoral(com, enable));
	}
	public static void doValues(Clan clan, Clan other) {
		
	}
}
