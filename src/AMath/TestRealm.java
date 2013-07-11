package AMath;

import Game.*;
import Shirage.*;

public class TestRealm extends Realm {
	public TestRealm(int pX, int pY, int cN) {
		super(pX, pY, cN);
	}

	@Override
	protected void generateShires(int H, int V) {
		shires = new TestShire[H*V];
		for (int x = 0; x < H; x++) {
			for (int y = 0; y < V; y++) {
				shires[x + y*H] = new TestShire(x, y);
				Plot p = new Plot(0.5); p.makeLand();
				shires[x + y*H].setLinkedPlot(p);
			}
		}
	}
}
