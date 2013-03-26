package Sentiens;

public class Law {
	public static abstract class Commandment {
		private final String verb;
		private boolean active;
		private int transgressions;
		public Commandment(String verb) {this.verb = verb;}
		public boolean isActive() {return active;}
		public void setActive(boolean active) {this.active = active;}
		public String desc() {return "THOU SHALT NOT " + verb;}
		public String getVerb() {return verb;}
		public int getTransgressions() {return transgressions;}
		public void setTransgressions(int transgressions) {this.transgressions = transgressions;}
		public void commit() {this.transgressions++;}
		@Override
		public String toString() {
			return (active ? desc() : (verb + " OK")) + ": " + transgressions + " transgressions";
		}
	}
	public static class Commandments {
		public final Commandment Murder = Murder();
		public final Commandment Theft = Theft();
		public final Commandment Adultery = Adultery();
		public final Commandment Heresy = Heresy();
		public final Commandment Deception = Deception();
		public final Commandment Witchcraft = Witchcraft();
		public final Commandment Carnivory = Carnivory();
		public final Commandment[] list = new Commandment[] {Murder, Theft, Adultery, Heresy};
	}

	public static final Commandment Murder() {
		return new Commandment("KILL") {
			
		};
	}
	public static final Commandment Theft() {
		return new Commandment("STEAL") {
			
		};
	}
	public static final Commandment Adultery() {
		return new Commandment("BE PROMISCUOUS") {
			
		};
	}
	public static final Commandment Heresy() {
		return new Commandment("WORSHIP OTHER GODS") {
			
		};
	}
	public static final Commandment Deception() {
		return new Commandment("LIE") {
			
		};
	}
	public static final Commandment Witchcraft() {
		return new Commandment("PROPHECIZE") {
			
		};
	}
	public static final Commandment Carnivory() {
		return new Commandment("EAT MEAT") {
			
		};
	}
}
