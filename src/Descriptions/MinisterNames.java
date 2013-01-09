package Descriptions;

import Game.*;
import Sentiens.Clan;

public class MinisterNames {
	public static interface MinistryNamer {
		public String nameFor(Clan c);
	}
	
//	JUDGE, NOBLE, HISTORIAN, PHILOSOPHER, GUILDMASTER, SORCEROR, VIZIER, GENERAL, TREASURER, COURTESAN, APOTHECARY, ARCHITECT

	
	public static final String getMinistryName(Ministry m, Clan c) {
		if (m == Job.JUDGE) {
			return c.getMinionTotal() > 20 ? "Judge" : ("Priest" + (c.getGender() == Defs.FEMALE ? "ess" : ""));
		}
		else if (m == Job.NOBLE) {
			return c.getMinionTotal() > 20 ? "Noble" : "Minion";
		}
		else if (m == Job.HISTORIAN) {
			return c.getMinionTotal() > 20 ? "Historian" : "Bard";
		}
		else if (m == Job.PHILOSOPHER) {
			return c.getMinionTotal() > 20 ? "Philosopher" : "Scribe";
		}
		else if (m == Job.GUILDMASTER) {
			return c.getMinionTotal() > 20 ? "Guildmaster" : "Specialist";
		}
		else if (m == Job.SORCEROR) {
			return c.getMinionTotal() > 20 ? "Sorcer" + (c.getGender() == Defs.FEMALE ? "ess" : "or") : "Shaman";
		}
		else if (m == Job.VIZIER) {
			return c.getMinionTotal() > 20 ? "Vizier" : "Bureaucrat";
		}
		else if (m == Job.GENERAL) {
			return c.getMinionTotal() > 20 ? "General" : "Soldier";
		}
		else if (m == Job.TREASURER) {
			return c.getMinionTotal() > 20 ? "Treasurer" : "Accountant";
		}
		else if (m == Job.COURTESAN) {
			return c.getMinionTotal() > 20 ? "Courtesan" : "Consort";
		}
		else if (m == Job.APOTHECARY) {
			return c.getMinionTotal() > 20 ? "Apothecary" : "Healer";
		}
		else if (m == Job.ARCHITECT) {
			return c.getMinionTotal() > 20 ? "Architect" : "Builder";
		}
		else {return "Servant";}
	}
	
	
}
