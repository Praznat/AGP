package War;

import java.util.Set;

import Questing.*;
import Questing.WarQuests.FormArmy;
import Sentiens.Clan;

public class WarLogic {

	public static boolean decideMove(Clan decider, Set<Clan> deciderArmy, Clan enemy) {
		Set<FormArmy> enemyArmy = null;
		final Quest targetTopQuest = enemy.MB.QuestStack.peek();
		if (targetTopQuest != null && FormArmy.class.isAssignableFrom(targetTopQuest.getClass())) {enemyArmy = ((FormArmy) targetTopQuest).getArmy();}
		
		int estEnemyImmediateStr = (enemyArmy != null ? enemyArmy.size() : 1);
		int estEnemyMinionStr = enemy.getMinionTotal();
		int estEnemyOrderStr = enemy.myOrder().size();
		
		return true;
	}
	
}
