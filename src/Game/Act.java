package Game;

import Sentiens.Clan;
import Avatar.SubjectivelyComparable;

public interface Act extends SubjectivelyComparable{
	public void doit(Clan doer);

	public int estimateProfit(Clan pOV);

	public String getDesc();

	public int getSkill(Clan clan);
}
