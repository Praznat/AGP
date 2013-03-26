package Questing;

import Defs.*;
import Game.AGPmain;
import Questing.Quest.PatronedQuest;
import Questing.Quest.QuestFactory;
import Sentiens.Clan;

public class FaithQuests {
	
	public static QuestFactory getMinistryFactory() {return new QuestFactory(ContactQuest.class) {public Quest createFor(Clan c) {return new ContactQuest(c, c.getBoss());}};}

	/**
	 * bless patron : inc patron's holiness
	 * self : inc self's holiness
	 */
	public static class ContactQuest extends PatronedQuest {

		public ContactQuest(Clan P, Clan patron) {super(P, patron);}

		@Override
		public String description() {return "Engage Spirituality";}

		@Override
		public void pursue() {
			pray(Me, patron);
		}
		
		public static void pray(Clan prayer, Clan prayee) {
			int mana = decidePrayStile(prayer, prayee).conduct(prayer, prayee);
			prayee.chgHoliness(AGPmain.rand.nextInt(Math.max(0, mana)));
		}
		private static ActOfFaith decidePrayStile(Clan prayer, Clan prayee) {
			return null; // might need to call on philosopher... else random?
		}
		
	}
	
	public static interface ActOfFaith {
		public String desc();
		public int conduct(Clan subject, Clan object);
	}
	public static ActOfFaith INCANTATE = new ActOfFaith() {
		public String desc() {return "Unholy Trance";}
		public int conduct(Clan subject, Clan object) {
			return subject.FB.getBeh(M_.MADNESS) * object.FB.getBeh(M_.SUPERST) / 16;
		}
	};
	public static ActOfFaith RITUAL = new ActOfFaith() {
		public String desc() {return "Festive Dance Ritual";}
		public int conduct(Clan subject, Clan object) {
			final int danceSkill = subject.FB.getPrs(P_.DANCE);
			ExpertiseQuests.practiceSkill(subject, P_.DANCE);
			return danceSkill * object.FB.getBeh(M_.EXTROVERSION) / 16;
		}
	};
	public static ActOfFaith ONEWITHNATURE = new ActOfFaith() {
		public String desc() {return "Communion with Nature";}
		public int conduct(Clan subject, Clan object) {
			return object.FB.getBeh(M_.RESPENV); // TODO * %wilderness in prayee shire
		}
	};
	public static ActOfFaith ONEWITHGOD = new ActOfFaith() {
		public String desc() {return "Communion with God";}
		public int conduct(Clan subject, Clan object) {
			return 16 - object.FB.getBeh(M_.RESPENV); // TODO feed the statue in the temple...
		}
	};
	public static ActOfFaith SACRIFICE = new ActOfFaith() {
		public String desc() {return "Ritual Sacrifice";}
		public int conduct(Clan subject, Clan object) {
			return subject.FB.getBeh(M_.BLOODLUST); // TODO buy sacrificial goods, dont do if SIN
		}
	};
}
