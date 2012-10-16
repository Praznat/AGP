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
		public int getTransgressions() {return transgressions;}
		public void setTransgressions(int transgressions) {this.transgressions = transgressions;}
		public void commit() {this.transgressions++;}
	}
	public static class Commandments {
		public Commandment Murder = Murder();
		public Commandment Theft = Theft();
		public Commandment Adultery = Adultery();
		public Commandment[] list = new Commandment[] {Murder, Theft, Adultery};
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
	public static final Commandment Deception() {
		return new Commandment("LIE") {
			
		};
	}
	public static final Commandment Prophecy() {
		return new Commandment("PROPHECIZE") {
			
		};
	}
	public static final Commandment Carnivorize() {
		return new Commandment("EAT MEAT") {
			
		};
	}
}