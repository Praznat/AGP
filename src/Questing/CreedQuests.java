package Questing;

import AMath.Calc;
import Avatar.SubjectiveType;
import Questing.Quest.QuestFactory;
import Sentiens.*;
import Sentiens.Law.Commandment;

public class CreedQuests {
	public static QuestFactory getMinistryFactory() {return new QuestFactory(PriestQuest.class) {public Quest createFor(Clan c) {return new PriestQuest(c, c.getBoss());}};}

	public static class TweakCommandments extends Quest {
		private final Clan patron;
		public TweakCommandments(Clan P, Clan patron) {super(P); this.patron = patron;}
		@Override
		public String description() {return "Moral Reflection";}

		@Override
		public void pursue() {
			CognitiveDissonance.doMorals(patron, Me);
			Me.MB.finishQ();
		}
		@Override
		public void avatarPursue() {
			avatarConsole().showChoices(Me, Me.FB.commandments.list, SubjectiveType.NO_ORDER, new Calc.Listener() {
				@Override
				public void call(Object arg) {
					final Commandment c = ((Commandment) arg);
					final boolean newlyActive = !c.isActive();
					c.setActive(newlyActive);
					Me.addReport(GobLog.decidedMoral(c, newlyActive));
					Me.MB.finishQ();
				}
			});
		}
	}

	public static class PriestQuest extends Quest {
		private final Clan patron;
		public PriestQuest(Clan P) {this(P, P);}
		public PriestQuest(Clan P, Clan patron) {super(P); this.patron = patron;}
		@Override
		public String description() {return "Sacred Duty";}
		@Override
		public void pursue() {
			// choose between PersecuteCriminal, TweakCommandments
			replaceAndDoNewQuest(Me, new TweakCommandments(Me, patron));
		}
		
	}
	
}
