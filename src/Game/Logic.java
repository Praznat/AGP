package Game;

import Sentiens.Clan;
import Sentiens.Questy;

public class Logic {
	protected static final int BUY = 0;
	protected static final int SELL = 1;
	protected static final int OBTAIN = 2;
	protected static final int FORFEIT = 3;
	protected static final int E = Defs.E;
	
	
	
	protected Logic[] subLogics;
	protected int[] X = new int[Questy.WORKMEMORY]; //one for each logic
	

	
	public boolean sufficient(Clan doer) {return false;} //dont ask from here, use workmemo
	
	public int[] getBest(Clan doer, int type) {X[0] = 0; X[1] = E; return X;}
	

	public void addNodesToAll(Act a) {
		if (subLogics != null) {
			for(int i = 0; i < subLogics.length; i++) {subLogics[i].addNodesToAll(a);}
		}
	}

	
	protected boolean better(int p1, int type) {
		if(X[0] < 0) {return true;}
		if(type == BUY || type == FORFEIT) {return p1<X[0];}
		else if (type == SELL || type == OBTAIN) {return p1>X[0];}
		else return false;
	}
	
	public static Logic[] L(int... inputs) {
		Logic[] output = new Logic[inputs.length];
		for (int i = 0; i < output.length; i++) {output[i] = new Node(inputs[i]);}
		return output;
	}
	
}

class And extends Logic {
	public And(Logic... subs) {subLogics = subs;}
	public And(int... subs) {subLogics = L(subs);}
	
	public boolean sufficient(Clan doer) { //WRONG! must maintain global count
		for (int i = 0; i < subLogics.length; i++) {
			if(subLogics[i].sufficient(doer) == false) {return false;}
		}   return true;
	}
	public int[] getBest(Clan doer, int type) {
		X[0] = 0;   int p = 1;   int k;
		for (int i = 0; i < subLogics.length; i++) {
			int[] tmp = subLogics[i].getBest(doer, type);
			X[0] = (int) Math.min((long) X[0] + tmp[0], Integer.MAX_VALUE);  //add cost from sublogic i
			k = 1;   while (tmp[k] != E) {X[p++] = tmp[k++];}
		}      X[p] = E;
		return X;
	}

}

class Or extends Logic {
	public Or(Logic... subs) {subLogics = subs;}
	public Or(int... subs) {subLogics = L(subs);}
	
	public boolean sufficient(Clan doer) { //WRONG! must maintain global count
		for (int i = 0; i < subLogics.length; i++) {
			if(subLogics[i].sufficient(doer) == true) {return true;}
		}   return false;
	}
	
	public int[] getBest(Clan doer, int type) {
		X[0] = E;   int k = 0;
		for (int i = 0; i < subLogics.length; i++) {
			int[] tmp = subLogics[i].getBest(doer, type);
			if (better(tmp[0], type)) {
				X[0] = tmp[0];   k = 1;   while (tmp[k] != E) {X[k] = tmp[k++];}
			}
		}   X[k] = E;
		return X;
	}
	

}

class Mult extends Logic {
	int mult;
	public Mult(int m, Logic subs) {mult = m; subLogics = new Logic[] {subs};}
	public Mult(int m, int subs) {mult = m; subLogics = L(subs);}
	
	
	public int[] getBest(Clan doer, int type) {
		int p = 1;   int k;
		int[] tmp = subLogics[0].getBest(doer, type);
		X[0] = mult * (int) Math.min((long) tmp[0], Integer.MAX_VALUE);
		for (int i = 0; i < mult; i++) {
			k = 1;   while (tmp[k] != E) {X[p++] = tmp[k++];}
		}      X[p] = E;
		return X;
	}
	

	
}

class Node extends Logic {
	int node;
	public Node(int input) {node = input;}
	private int Cost(Clan doer) {return doer.myMkt(node).buyablePX(doer);}
	private int Value(Clan doer) {return doer.myMkt(node).sellablePX(doer);}
	public int[] getBest(Clan doer, int type) {
		switch (type) {
		case BUY: X[0] = Cost(doer); break;
		case SELL: X[0] = Value(doer); break;
		case OBTAIN: break;
		case FORFEIT: break;
		default: break;
		}
		X[1] = node;   X[2] = E;   return X;
	}
	public void addNodesToAll(Act a) {a.addToAll(node);}

}

class Nada extends Logic {
	//just leave empty, inherited from Logic
}