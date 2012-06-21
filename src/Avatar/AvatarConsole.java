package Avatar;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

import javax.swing.JButton;
import javax.swing.JPanel;

import Game.AGPmain;
import Sentiens.Clan;

public class AvatarConsole extends JPanel implements ActionListener {
	private Clan avatar;

	private JButton actionButton, profileButton;
	private AvatarConsole() {
		actionButton = new JButton();
		actionButton.addActionListener(this);
		actionButton.setMnemonic(KeyEvent.VK_S);
		setButton(actionButton, "New Quest");

		profileButton = new JButton();
		profileButton.addActionListener(this);
		profileButton.setMnemonic(KeyEvent.VK_A);
		setButton(profileButton, "View Avatar");
		
		
		add(actionButton);
		add(profileButton);
	}
	public static AvatarConsole create() {return new AvatarConsole();}
	public void setAvatar(Clan c) {avatar = c;}
	
	private void setButton(JButton B, String S) {
		B.setText(S);
		B.setActionCommand(S);
	}


	@Override
	public void actionPerformed(ActionEvent e) {
		if ("View Avatar".equals(e.getActionCommand())) {
			AGPmain.mainGUI.GM.loadClan(avatar);
		}
	}
	

}
