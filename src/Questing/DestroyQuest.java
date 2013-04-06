package Questing;

import Defs.P_;
import Questing.PersecutionQuests.PersecuteAbstract;
import Questing.Quest.TargetQuest;
import Sentiens.Clan;
import Sentiens.GobLog;
import Sentiens.Stressor;

public class DestroyQuest extends TargetQuest {
	private RelationCondition victoryCondition;
	public DestroyQuest(Clan P, Clan T, RelationCondition c) {super(P, T); victoryCondition = c;}

	@Override
	public void pursue() {
		// TODO Auto-generated method stub
		if (target == null || victoryCondition.meetsReq(Me, target)) {success();}
		boolean winorlose = Me.FB.getPrs(P_.COMBAT) > target.FB.getPrs(P_.COMBAT);
		Me.addReport(GobLog.handToHand(target, winorlose));
		((PersecuteAbstract) upQuest()).failDestroy(); 
		if (winorlose) {target.AB.add(new Stressor(Stressor.INSULT, Me)); Me.FB.upPrest(P_.TYRRP); success();}
		else {Me.FB.downPrest(P_.TYRRP); failure(target);}
	}
	@Override
	public String description() {return "Destroy " + (target == null ? "Someone" : target.getNomen());}
}
