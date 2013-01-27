package Avatar;

import java.awt.event.*;
import java.util.*;

import javax.swing.JButton;

import AMath.Calc;
import GUI.*;
import Game.AGPmain;
import Questing.Quest;
import Sentiens.*;
import Sentiens.Values.Value;

@SuppressWarnings({ "rawtypes", "unchecked", "serial" })
public class AvatarConsole extends APanel implements ActionListener {
	private Clan avatar;
	private final int DESWID = 200;
	private final int DESHGT = 200;
	private final int BUFFX = 5;
	private final int BUFFY = 5;
	private final int BUTTW = 150;
	private final int BUTTH = 20;
	private int numButtons = 0;

	private final String EDITAVATAR = "Edit Avatar";
	private final String POSSESS = "Possess Goblin";
	private final String AVATAR = "View Avatar";
	private final String NEWQUEST = "New Quest";
	private final String PURSUEQUEST = "Pursue Quest";
	private final String STEPONCE = "Step Once";
	private final String STEP100 = "100 Turns";
	
	private final SubjectiveComparator comparator;
	public final TreeSet choices;

	private AvatarConsole(GUImain P) {
		super(P);
		comparator = new SubjectiveComparator();
		choices = new TreeSet(comparator);
		setLayout(null);
		setButton(EDITAVATAR, -1);
		setButton(POSSESS, -1);
		setButton(AVATAR, KeyEvent.VK_A);
		setButton(NEWQUEST, KeyEvent.VK_Q);
		setButton(PURSUEQUEST, KeyEvent.VK_P);
		setButton(STEPONCE, KeyEvent.VK_S);
		setButton(STEP100, -1);
		
	}
	public static AvatarConsole create(GUImain P) {return new AvatarConsole(P);}
	public void setAvatar(Clan c) {avatar = c;}
	public Clan getAvatar() {return avatar;}
	public int getDesWid() {return DESWID;}
	public int getDesHgt() {return DESHGT;}
	

	public void showChoices(Clan POV, Object[] choices, SubjectiveType sct,
			Calc.Listener listener, Calc.Transformer transformer) {
		this.choices.clear();
		this.getComparator().setPOV(POV);
		switch (sct) {
		case ACT_PROFIT_ORDER: comparator.setComparator(comparator.ACT_PROFIT_ORDER); break;
		case RESPECT_ORDER: comparator.setComparator(comparator.RESPECT_ORDER); break;
		case VALUE_ORDER: comparator.setComparator(comparator.VALUE_ORDER); break;
		case QUEST_ORDER: comparator.setComparator(comparator.QUEST_ORDER); break;
		case NO_ORDER: comparator.setComparator(comparator.NO_ORDER); break;
		}
		for (Object choice : choices) {this.choices.add(choice);}
		new APopupMenu(this, this.choices, listener, transformer);
	}
	public void showChoices(Clan POV, Object[] choices,
			SubjectiveType sct, Calc.Listener listener) {
		showChoices(POV, choices, sct, listener, null);
	}
	public void showChoices(Clan POV, Collection choices,
			SubjectiveType sct, Calc.Listener listener, Calc.Transformer transformer) {
		showChoices(POV, choices.toArray(), sct, listener, transformer);
	}
	
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
	public SubjectiveComparator getComparator() {return ((SubjectiveComparator)choices.comparator());}

	private void newQuest() {
		this.showChoices(avatar, Values.All, SubjectiveType.VALUE_ORDER, new Calc.Listener() {
			@Override
			public void call(Object arg) {
				avatar.MB.newQ(Quest.QtoQuest(avatar, ((Value) arg).pursuit(avatar)));
			}
		}, new Calc.Transformer<Value, String>() {
			@Override
			public String transform(Value v) {
				return Quest.QtoQuest(avatar, v.pursuit(avatar)).description();
			}
		});
	}
	
	public void avatarPursue() {
		if (avatar.MB.QuestStack.empty()) {this.newQuest();}
		else {
			avatar.MB.QuestStack.peek().avatarPursue();
			avatar.setActive(false);
		}
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		if (AVATAR.equals(e.getActionCommand())) {
			AGPmain.mainGUI.GM.loadClan(avatar);
		}
		else if (EDITAVATAR.equals(e.getActionCommand())) {
			AGPmain.mainGUI.initializeEditor();
		}
		else if (POSSESS.equals(e.getActionCommand())) {
			setAvatar(AGPmain.mainGUI.GM.getClan());
		}
		else if (NEWQUEST.equals(e.getActionCommand())) {
			this.newQuest();
		}
		else if (PURSUEQUEST.equals(e.getActionCommand()) && avatar.isActive()) {
			avatarPursue();
		}
		else if (STEPONCE.equals(e.getActionCommand())) {
			AGPmain.TheRealm.goOnce();
		}
		else if (STEP100.equals(e.getActionCommand())) {
			for (int i = 0; i < 100; i++) {
				Calc.p("Day " + i);
				AGPmain.TheRealm.goOnce();
			}
		}
		AGPmain.mainGUI.GM.setState();
		AGPmain.mainGUI.SM.setState();
	}

}
