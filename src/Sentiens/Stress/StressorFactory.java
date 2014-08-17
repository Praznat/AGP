package Sentiens.Stress;

import Defs.SK_;
import Descriptions.GobLog;
import Ideology.Value;
import Questing.ImmigrationQuests;
import Questing.Knowledge.KnowledgeBlock;
import Questing.Might.WarQuest;
import Sentiens.Clan;
import Shirage.Shire;

public class StressorFactory {

	public static Stressor createShireStressor(Shire blamee, final Value whySucks) {
		return new Stressor(Stressor.ANNOYANCE, blamee) {
			@SuppressWarnings("rawtypes")
			@Override
			public boolean respond(Clan responder) {
				if (blamee != responder.myShire()) {
					System.out.println("blamed non-home shire");
					// TODO this should only happen when you are blaming a different shire.
					return true;
				}
				KnowledgeBlock kb = responder.myShire().getLibrary().findKnowledge(SK_.valToSK.get(whySucks));
				Shire newShire;
				if (kb != null) {
					kb.useKnowledge(responder, false);
					Shire bestShire = (Shire) kb.getXs()[0];
					if (bestShire == blamee) return false;
					else newShire = bestShire;
				} else {
					newShire = responder.myShire().getSomeNeighbor();
				}
				if (responder.myShire() != newShire) {responder.MB.newQ(new ImmigrationQuests.EmigrateQuest(responder, newShire));}
				return true;
			}
		};
	}
	
	
	
	public static Stressor createBloodVengeanceStressor(Clan blamee) {
		return new Stressor(Stressor.HATRED, blamee) {
			@Override
			public boolean respond(Clan responder) {
				// TODO dedicate to revenge!
				Clan target = (Clan) blamee;
				responder.addReport(GobLog.beginBloodVengeance(target));
				responder.MB.newQ(WarQuest.start(responder, target)); // TODO WarQuest needs to know this is to the death
				return false; // you never feel good cuz this just starts a long process of revenge
			}
		};
	}
	
}
