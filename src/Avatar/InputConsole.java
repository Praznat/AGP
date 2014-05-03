package Avatar;

import java.awt.event.*;

import javax.swing.JTextField;

import Game.AGPmain;
import Sentiens.Clan;
import Shirage.Shire;


public class InputConsole extends JTextField {

	private static final String CMD_FIND_CLAN = "find clan ";
	private static final String CMD_FIND_SHIRE = "find shire ";
	private static final String CMD_SHOW_RESOURCE = "show resource ";
	private static final String CMD_DEBUG_MARKETS = "debug markets ";
	
	private final KeyListener keyListener = new KeyListener() {
		@Override
		public void keyTyped(KeyEvent arg0) {}
		@Override
		public void keyReleased(KeyEvent arg0) {}
		@Override
		public void keyPressed(KeyEvent arg0) {
			if (arg0.getKeyCode() == KeyEvent.VK_ENTER) {
				doCommand();
			}
		}
	};
	
	public InputConsole() {
		this.addKeyListener(keyListener);
	}
	
	@Override
	public boolean isFocusable() {return true;}
	
	private void doCommand() {
		final String command = getText().toLowerCase();
		
		System.out.println(command);
		
		boolean didSomething =
				(doForCommand(command, CMD_FIND_CLAN, new InputThing() {public void doit(String input) {
					Clan c = AGPmain.TheRealm.getClan(input);
					System.out.println(c);
					if (c != null) AGPmain.mainGUI.GM.loadClan(c);
				}})) ||
				(doForCommand(command, CMD_FIND_SHIRE, new InputThing() {public void doit(String input) {
					Shire s = AGPmain.TheRealm.getShire(input);
					if (s != null) AGPmain.mainGUI.SM.loadShire(s);
				}})) ||
				(doForCommand(command, CMD_DEBUG_MARKETS, new InputThing() {public void doit(String input) {
					Shire s = AGPmain.TheRealm.getShire(input);
					if (s != null) for (int g = 1; g < Defs.Defs.numGoods; g++) {
						System.out.println(s.getMarket(g).getReport());
					}
				}})) ||
				(doForCommand(command, CMD_SHOW_RESOURCE, new InputThing() {public void doit(String input) {
				// TODO highlight resource
		}}));
			
		if (didSomething) return;
		
		switch (command) {
		default:
			setText("*NO SUCH COMMAND*");
		}
	}
	
	private boolean doForCommand(String cmd, String prefix, InputThing inputThing) {
		if (cmd.startsWith(prefix)) {
			String suffix = cmd.substring(prefix.length());
			inputThing.doit(suffix);
			return true;
		}
		return false;
	}
	
	interface InputThing {
		void doit(String input);
	}
}
