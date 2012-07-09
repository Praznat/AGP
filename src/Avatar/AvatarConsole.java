package Avatar;

import java.awt.event.*;
import java.util.TreeMap;

import javax.swing.JButton;

import GUI.*;
import Game.*;
import Game.Do.ClanAction;
import Game.Do.ClanAlone;
import Sentiens.*;
import Sentiens.Values.Value;

public class AvatarConsole extends APanel implements ActionListener {
	private Clan avatar;
	private final int DESWID = 200;
	private final int DESHGT = 200;
	private final int BUFFX = 5;
	private final int BUFFY = 5;
	private final int BUTTW = 150;
	private final int BUTTH = 20;
	private int numButtons = 0;

	private final String AVATAR = "View Avatar";
	private final String NEWQUEST = "New Quest";
	private final String PURSUEQUEST = "Pursue Quest";
	
	private final SubjectiveComparator<SubjectivelyComparable> comparator;
	public final TreeMap<SubjectivelyComparable, ClanAction> choices;

	private AvatarConsole(GUImain P) {
		super(P);
		comparator = new SubjectiveComparator<SubjectivelyComparable>();
		choices = new TreeMap<SubjectivelyComparable, ClanAction>(comparator);
		setLayout(null);
		setButton(AVATAR, KeyEvent.VK_A);
		setButton(NEWQUEST, KeyEvent.VK_Q);
		setButton(PURSUEQUEST, KeyEvent.VK_P);
		
	}
	public static AvatarConsole create(GUImain P) {return new AvatarConsole(P);}
	public void setAvatar(Clan c) {avatar = c;}
	public Clan getAvatar() {return avatar;}
	public int getDesWid() {return DESWID;}
	public int getDesHgt() {return DESHGT;}
	
	private void setButton(String S, int KE) {
		JButton B = new JButton();
		B.setText(S);
		B.setActionCommand(S);
		B.setMnemonic(KE);
		B.addActionListener(this);
		add(B);
		B.setBounds(BUFFX, BUFFY + numButtons * (BUTTH + BUFFY), BUTTW, BUTTH);
		numButtons++;
	}
	@SuppressWarnings("unchecked")
	public SubjectiveComparator<SubjectivelyComparable> getComparator() {return ((SubjectiveComparator<SubjectivelyComparable>)choices.comparator());}

	private void newQuest() {
		choices.clear();
		getComparator().setPOV(avatar);
		getComparator().setComparator(comparator.VALUE_ORDER);
		for (Value V : Values.All) {
			ClanAlone action = V.doPursuit(avatar);
			action.setup(avatar);
			if (choices.containsValue(action)) {continue;}
			choices.put(V, action);
		}
		new APopupMenu(this, choices.values());
	}
	

	@Override
	public void actionPerformed(ActionEvent e) {
		if (AVATAR.equals(e.getActionCommand())) {
			AGPmain.mainGUI.GM.loadClan(avatar);
		}
		else if (NEWQUEST.equals(e.getActionCommand())) {
			newQuest();
		}
		else if (PURSUEQUEST.equals(e.getActionCommand())) {
			if (avatar.MB.QuestStack.empty()) {newQuest();}
			else {avatar.MB.QuestStack.peek().avatarPursue();}
		}
		AGPmain.mainGUI.GM.refreshAll();
	}
	

}
