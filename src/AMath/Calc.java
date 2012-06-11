package AMath;

import Game.AGPmain;

public class Calc {
	
	public static void p(String s) {System.out.println(s);}
	
	//not extremely efficient
	public static short bitPart(short X, int start, int len) {
		if(start + len > 16) {return (short) (1/0);}
		int shift = 16 - start - len;
		int mask = ((~0) >>> (32 - len));
		return (short) ((X >>> shift) & mask);
	}
	public static short setBitPart(short X, int start, int len, int val) {
		if (val >= Math.pow(2, len)) {return (short) (1/0);}
		int shift = 16 - start - len;
		int mask = (~0) << 16 - start;
		mask = ~((~mask) & ((~0) << shift));
		return (short) ((X & mask) | (val << shift));
	}

	
	public static double roundy(double d, int e) {
		double p = Math.pow(10,e);
		return Math.round(d * p) / p;
	}
	public static int roundy(double d) {
		return (int) Math.round(d);
	}
	public static int AtoBbyRatio(int A, int B, int ratioToA, int denom) {
		long a = (long)A * ratioToA;
		long b = (long)B * (denom - ratioToA);
		int out = roundy((a + b) / denom);  //broken!
		return out;
	}
	public static int AtoBbyRatio(double A, double B, int ratioToA, int denom) {
		return roundy((A * ratioToA + B * (denom - ratioToA)) / denom);  //broken!
	}

	public static int sum(byte[] x) {
		int sum = 0;
		for (int i = 0; i < x.length; i++) {sum += x[i];}
		return sum;
	}
	public static int sum(int[] x) {
		int sum = 0;
		for (int i = 0; i < x.length; i++) {sum += x[i];}
		return sum;
	}
	public static double max(double[] x) {
		double max = 0;
		for (int i = 0; i < x.length; i++) {if (x[i] > max) {max = x[i];}}
		return max;
	}
	public static int max(int[] x) {
		int max = 0;
		for (int i = 0; i < x.length; i++) {if (x[i] > max) {max = x[i];}}
		return max;
	}
	public static double min(double[] x) {
		double min = Integer.MAX_VALUE;
		for (int i = 0; i < x.length; i++) {if (x[i] < min) {min = x[i];}}
		return min;
	}
	public static int min(int[] x) {
		int min = Integer.MAX_VALUE;
		for (int i = 0; i < x.length; i++) {if (x[i] < min) {min = x[i];}}
		return min;
	}
	public static int oneorzero(int x, int y) {
		if (x >= y) {return 1;}
		else {return 0;}
	}
	public static int bound(int x, int min, int max) {
		return Math.min(max, Math.max(min, x));
	}

	public static int[] bubbleSort(int[] in) {
		int[] x = new int[in.length];
		System.arraycopy(in, 0, x, 0, in.length);
		boolean moved = true; int tmp;
		while (moved) {
			moved = false;
			for (int i = 0; i < x.length-1; i++) {
				if (x[i]>x[i+1]) {tmp=x[i]; x[i]=x[i+1]; x[i+1]=tmp; moved = true;}
			}
		}
		return x;
	}
	public static int[] cropArray(int[] in, int start, int end) {
		int[] out = new int[end-start];
		System.arraycopy(in, start, out, 0, end-start);
		return out;
	}
	public static int[][] cropArray(int[][] in, int start, int end) {
		int[][] out = new int[end-start][];
		for (int i = 0; i < out.length; i++) {
			out[i] = new int[in[start + i].length];
			for (int j = 0; j < out[i].length; j++) {
				out[i][j] = in[start + i][j];
			}
		}
		return out;
	}
	public static byte[] booleize(byte[] x) {
		// returns 1 for positives, 0 for non-positives
		byte[] y = new byte[x.length];
		for ( int i = 0; i < x.length; i++) {
			if (x[i] > 0) {y[i] = 1;}
			else {y[i] = 0;}
		}
		return y;
	}
	public static double distance(int[] pt1, int[] pt2) {
		return Math.sqrt(Math.pow(pt2[0] - pt1[0], 2) + Math.pow(pt2[1] - pt1[1], 2));
	}
	public static boolean skillcalc (int s, int d) {
		if (Calc.pFraction(s, d + s) == 1) {return true;}
		else {return false;}
	}
	public static int skillpct (int s, int d) {
		return (100*s) / (d + s);
	}
	public static boolean pPercent (int num) {
		if (AGPmain.rand.nextInt(100) < num) {return true;}
		else {return false;}
	}
	/**  returns true at probability num / 15      */
	public static boolean pMem (int num) {
		if (AGPmain.rand.nextInt(15) < num) {return true;}
		else {return false;}
	}
	public static int iPercent (int num) {
		if (AGPmain.rand.nextInt(100) < num) {return 1;}
		else {return 0;}
	}
	public static byte pFraction (byte num, byte denom) {
		if (AGPmain.rand.nextInt(denom) < num) {return (byte) 1;}
		else {return (byte) 0;}
	}
	public static int pFraction (int num, int denom) {
		if (AGPmain.rand.nextInt(denom) < num) {return 1;}
		else {return 0;}
	}
	public static boolean discover (int denom) {
		if (denom >= 0) {
			if (AGPmain.rand.nextInt(denom) == 0) {return true;}
			else {return false;}
		}
		else {return false;}
	}
	public static byte pDivide (byte num, byte denom) {
		return (byte) ((num / denom) + pFraction((byte)(num%denom), denom));
	}
	public static int pDivide (int num, int denom) {
		return ((num / denom) + pFraction((num%denom), denom));
	}
	public static byte squeezeByte(byte in, byte min, byte max) {
		double x = (in + 128) * (max - min);
		x = x/256;
		return (byte) (min + Math.round(x));
	}
	public static int squeezeByte(byte in, int min, int max) {
		//not include max
		int x = (in + 128) * (max - min);
		x = x/256;
		return (min + x);
	}
	public static int intBound(double d) {
		return (int) Math.min(Integer.MAX_VALUE, Math.max(Integer.MIN_VALUE, Math.round(d)));
	}
	public static int randBetween(int low, int high) {
		return low + AGPmain.rand.nextInt(high - low);
	}

	public static byte[] allto(byte x, int N) {
		byte[] out = new byte[N];
		for (int i = 0; i < N; i++) {out[i] = x;}
		return out;
	}
	public static int[] allto(int x, int N) {
		int[] out = new int[N];
		for (int i = 0; i < N; i++) {out[i] = x;}
		return out;
	}
	public static int sgn(int x) {
		if (x>0) {return 1;}
		else if (x<0) {return -1;}
		else {return 0;}
	}
	public static byte byteUp(byte prop, byte chg) {
		prop += chg;
		if(prop<0) {return 127;}
		else {return prop;}
	}
	public static byte byteDown(byte prop, byte chg) {
		prop -= chg;
		if(prop<0) {return 0;}
		else {return prop;}
	}
	public static byte byteDown1(byte prop, byte chg) {
		prop -= chg;
		if(prop<1) {return 1;}
		else {return prop;}
	}

	
	public static int findLessThan(int i, int[] V) {
		int p = 0; int r = V.length; int q = (p + r) / 2;
		if (i >= V[r-1]) {return -1;}
		else {
			while(q > 0) {
				if(i >= V[q]) {p = q;}
				else if(i < V[q-1]) {r = q;}
				else {return q;}
				q = (p + r) / 2;
			}
			return 0;
		}
	}
	public static int wheelOfFortune(int[] props) {
		byte[] rand = new byte[1];
		AGPmain.rand.nextBytes(rand);
		int spin;
		int[] cumProps = new int[props.length];
		cumProps[0] = props[0];
		for (int i = 1; i < props.length; i++) {
			cumProps[i] = props[i] + cumProps[i-1];
		}
		spin = Calc.squeezeByte(rand[0], 0, cumProps[cumProps.length-1]);
		return findLessThan(spin, cumProps);
	}
	public static byte wheelOfFortune(byte[] props) {
		byte[] rand = new byte[1];
		AGPmain.rand.nextBytes(rand);
		int spin;
		int[] cumProps = new int[props.length];
		cumProps[0] = props[0];
		for (int i = 1; i < props.length; i++) {
			cumProps[i] = props[i] + cumProps[i-1];
		}
		spin = Calc.squeezeByte(rand[0], 0, cumProps[cumProps.length-1]);
		return (byte) findLessThan(spin, cumProps);
	}
	public static byte[] wheelOfFortune(byte[] props, byte k) {
		byte[] rand = new byte[k];
		AGPmain.rand.nextBytes(rand);
		int spin;
		int[] cumProps = new int[props.length];
		cumProps[0] = props[0];
		for (int i = 1; i < props.length; i++) {
			cumProps[i] = props[i] + cumProps[i-1];
		}
		byte[] answer = new byte[k];
		for (int i = 0; i < k; i++) {
			spin = Calc.squeezeByte(rand[i], 0, cumProps[cumProps.length-1]);
			answer[i] = (byte) findLessThan(spin, cumProps);
		}
		return answer;
	}
	
	public static void printArray(byte[] V) {
		for(int i = 0; i < V.length; i++) {System.out.println(V[i]);}
	}
	public static void printArrayH(byte[] V) {
		for(int i = 0; i < V.length; i++) {System.out.print(V[i] + "	");}
		System.out.println();
	}
	public static void printArray(int[] V) {
		for(int i = 0; i < V.length; i++) {System.out.println(V[i]);}
	}
	public static void printArrayH(int[] V) {
		for(int i = 0; i < V.length; i++) {System.out.print(V[i] + "	");}
		System.out.println();
	}
	public static void printArray(byte[][] M) {
		for(int i = 0; i < M.length; i++) { for(int j = 0; j < M[i].length; j++) {
			System.out.print(M[i][j] + "	");
		}System.out.println();}
	}
	public static void printArray(String[][] M) {
		for(int i = 0; i < M.length; i++) { for(int j = 0; j < M[i].length; j++) {
			System.out.print(M[i][j] + "	");
		}System.out.println();}
	}
	public static void printArray(int[][] M) {
		for(int i = 0; i < M.length; i++) { for(int j = 0; j < M[i].length; j++) {
			System.out.print(M[i][j] + "	");
		}System.out.println();}
	}
	
	

    public static byte[][] transpose (byte[][] inM) {
    	byte[][] outM = new byte[inM[0].length][];  //assumption that all vectors are equal length
    	for (int i = 0; i < outM.length; i++) {
    		outM[i] = new byte[inM.length];
    		for (int j = 0; j < outM[i].length; j++) {
    			outM[i][j] = inM[j][i];
    		}
    	}
    	return outM;
    }
    public static int[][] transpose (int[][] inM) {
    	int[][] outM = new int[inM[0].length][];  //assumption that all vectors are equal length
    	for (int i = 0; i < outM.length; i++) {
    		outM[i] = new int[inM.length];
    		for (int j = 0; j < outM[i].length; j++) {
    			outM[i][j] = inM[j][i];
    		}
    	}
    	return outM;
    }
    public static String[][] transpose (String[][] inM) {
    	String[][] outM = new String[inM[0].length][];  //assumption that all vectors are equal length
    	for (int i = 0; i < outM.length; i++) {
    		outM[i] = new String[inM.length];
    		for (int j = 0; j < outM[i].length; j++) {
    			outM[i][j] = inM[j][i];
    		}
    	}
    	return outM;
    }

    public static int[] normalOrder(int L) {
    	int[] V = new int[L];
    	for (int i = 0; i < L; i++) {V[i] = i;}
    	return V;
    }
    public static int[] randomOrder(int L) {
    	int[] V = new int[L];
    	for (int i = 0; i < L; i++) {V[i] = i;}
    	for (int i = V.length - 1; i > 0; i--) {
    		int j = AGPmain.rand.nextInt(i);
    		int tmp = V[j];
    		V[j] = V[i];
    		V[i] = tmp;
    	}
    	return V;
    }
    public static void shuffle(int[] pop) {
    	for (int i = pop.length - 1; i > 0; i--) {
    		int j = AGPmain.rand.nextInt(i);
    		int tmp = pop[j];
    		pop[j] = pop[i];
    		pop[i] = tmp;
    	}
    }
    public static void shuffle(int[] pop, int d) {
    	for (int i = d - 1; i > 0; i--) {
    		int j = AGPmain.rand.nextInt(i);
    		int tmp = pop[j];
    		pop[j] = pop[i];
    		pop[i] = tmp;
    	}
    }
	public static int[] combineArrays(int[] v1, int[] v2) {
		int[] v3 = new int[v1.length + v2.length];
		System.arraycopy(v1, 0, v3, 0, v1.length);
		System.arraycopy(v2, 0, v3, v1.length, v2.length);
		return v3;
	}
	public static boolean[] appendToArray(boolean m, boolean[] V) {
		boolean[] VV = new boolean[V.length+1];
		for (int i = 0; i < V.length; i++) {VV[i] = V[i];}
		VV[V.length] = m;
		return VV;
	}
	public static int[] between(int[] xy1, int[] xy2, int N, int D) {
		return new int[] {xy1[0]+(xy2[0]-xy1[0])*N/D, xy1[1]+(xy2[1]-xy1[1])*N/D};
	}
	public static int[] offsetArray(int[] V, int off) {
		int[] out = new int[V.length];
		for (int i = 0; i < out.length; i++) {out[i] = V[i] + off;}
		return out;
	}
	
	public static double[] fractal(double R, int L) {
		//R must be 0~1
		int k = 1;   double r;
		double[] F = new double[L];
		while (k < L/2) {
			for(int i = 0; i < k; i++) {
				r = R * (2*Math.random() - 1);
				for (int j = (L/k)/2 - 1; j >= 0 ; j--) {
					F[j + L/k*i] += r*j/((L/k)/2);
					F[j + L/k*i + (L/k)/2] += r - r*j/((L/k)/2);
				}
			}   R = R * R;   k = k * 2;
		}   return F;
	}

	public static int[] copyArray(int[] in) {
		int[] out = new int[in.length];
		System.arraycopy(in, 0, out, 0, in.length);
		return out;
	}

	
	public static final boolean equal(Object a, Object b) {
		return a == null ? (b == null) : a.equals(b);
	}
	
}







