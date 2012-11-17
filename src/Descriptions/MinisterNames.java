package Descriptions;

import Game.*;
import Sentiens.Clan;

public class MinisterNames {
	public static interface MinistryNamer {
		public String nameFor(Clan c);
	}
	
	public static final String getMinistryName(Ministry m, Clan c) {
		if (m == Job.NOBLE) {
			if (c.getMinionTotal() > 20) {return "Noble";}
			else {return "Minion";}
		}
		else {return "Servant";}
	}
	
}
