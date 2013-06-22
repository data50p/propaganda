package fpg.sundry;

import java.io.*;
import java.security.*;

public class RandVec implements Serializable {
    /**
     * 
     */
    private static final long serialVersionUID = -3415080438898637215L;
    int[] ia;
    private int[] accum;
    private int sum;
    final private int maxsum;
    private boolean invalid = false;

    private byte[] seed;
    private SecureRandom sec_rand;

    private int rand(int a) {
	return sec_rand.nextInt(a);
    }

    /**
       <strong>Note!</strong> The argument ia is cloned internaly.

       @args ia Array indicating how many of the different number that will be returned totaly.
       I.e. [1,2,3] might return 0,1,1,2,2,2
    */
    public RandVec(int[] ia) {
	accum = new int[ia.length];
	init(ia);
	maxsum = sum;
    }

	/**
	   Same as RandVec(new int[] {num<sub>0</sub>, num<sub>1</sub>, num<sub>max-1</sub>});
	 */
    public RandVec(int max, int num) {
	int ia[] = new int[max];
	for(int i = 0; i < ia.length; i++)
	    ia[i] = num;
	accum = new int[ia.length];
	init(ia);
	maxsum = sum;
    }

    private void init(int[] ia) {
	seed = SecureRandom.getSeed(8);
	try {
	    sec_rand = SecureRandom.getInstance("SHA1PRNG", "SUN");
	    sec_rand.setSeed(seed);
	    this.ia = ia.clone(); // DO NOT USE ORIGINAL array
	    sum = U.sum(this.ia);

	    mkAccum();
// 	    if ( sum != accum[this.ia.length-1] )
// 		throw new Error("RandVec: sum != accum");
	} catch (NoSuchProviderException ex) {
	    throw new Error("RandVec: " + ex);
	} catch (NoSuchAlgorithmException ex) {
	    throw new Error("RandVec: " + ex);
	}
    }

    @Override
    public Object clone() {
	if ( invalid ) {
	    throw new RuntimeException("RandVec.clone(): Object invalid!");
	}
	RandVec nrv = new RandVec(ia);
	try {
	    nrv.seed = seed;
	    nrv.sec_rand = SecureRandom.getInstance("SHA1PRNG", "SUN");
	    nrv.sec_rand.setSeed(seed);
	    return nrv;
	} catch (NoSuchProviderException ex) {
	    throw new Error("RandVec: " + ex);
	} catch (NoSuchAlgorithmException ex) {
	    throw new RuntimeException("RandVec.clone() " + ex);
	}
    }

    public int size() {
	return maxsum;
    }

    public void unget(int a) {
 	ia[a]++;
 	sum++;
 	mkAccum();
    }

    public void newSeed() {
	try {
	    seed = SecureRandom.getSeed(8);
	    sec_rand = SecureRandom.getInstance("SHA1PRNG", "SUN");
	    sec_rand.setSeed(seed);
	} catch (NoSuchProviderException ex) {
	    throw new Error("RandVec: " + ex);
	} catch (NoSuchAlgorithmException ex) {
	    throw new RuntimeException("RandVec.newSeed(): " + ex);
	}
    }

    public boolean hasNext() {
	return sum > 0;
    }

    public int getNext() {
  	if ( sum == 0 )
 	    throw new Error();
	int r = this.rand(sum);
	for(int i = 0; i < accum.length; i++)
	    if ( r < accum[i] ) {
		invalid = true;
		ia[i]--;
		sum--;
		for(int ii = i; ii < accum.length; ii++)
		    accum[ii]--;
		return i;
	    }
	throw new Error("RandVec: can't get number");
    }

    public int[] getAsIntArray() {
	if ( invalid ) {
	    throw new RuntimeException("RandVec.getAsIntArray(): Object invalid!");
	}

	int[] ia = new int[maxsum];

	for(int i = 0; i < ia.length; i++)
	    ia[i] = getNext();

	return ia;
    }

    public int[] getAsIntArray(int n) {
	if ( sum < n )
	    throw new RuntimeException("RandVec.getAsIntArray(): Can't get that many! " + n + ' ' + sum);

	int[] ia = new int[n];

	for(int i = 0; i < ia.length; i++)
	    ia[i] = getNext();

	return ia;
    }

    private void mkAccum() {
	int a = accum[0] = ia[0];
	for(int i = 1; i < ia.length; i++) {
	    a = accum[i] = a + ia[i];
	}
	assert sum == accum[this.ia.length-1] :
	    "RandVec: sum != accum: " + sum + ' ' + accum[this.ia.length-1];
    }

    @Override
    public String toString() {
	return "RandVec{" +
	    "sum=" + sum +
	    ", maxsum=" + maxsum +
	    ", seed=" + seed +
	    ", sec_rand=" + sec_rand +
	    ", ia[]=" + S.arrToString(ia) +
	    "}";
    }
	/**
	   Same as toString, but do not print the seed.
	 */
    public String toOpenString() {
	return "RandVec{" +
	    "sum=" + sum +
	    ", maxsum=" + maxsum +
	    ", ia[]=" + S.arrToString(ia) +
	    "}";
    }

    static public void main(String[] args) {
	RandVec rv = new RandVec(new int[] {   1,
					       2,
					       4,
					       10
	});
	System.out.println("----- " + rv);
	for(int i = 0; i < rv.size(); i++) {
	    int a = rv.getNext();
	    if ( S.rand(3) == 0 ) {
		rv.unget(a);
		i--;
		System.out.print(" u " + a);
		continue;
	    }
	    System.out.println(" a " + a);
	}

	RandVec rvo = new RandVec(new int[] {   1,
						2,
						4,
						10
	});
	RandVec rvc = (RandVec)rvo.clone();

	System.out.println("\n-----  new " + rvo);
	for(int i = 0; i < rvo.size(); i++) {
	    int a = rvo.getNext();
	    System.out.print(" " + a);
	}
	System.out.println("\n----- cloned " + rvc);
	for(int i = 0; i < rvc.size(); i++) {
	    int a = rvc.getNext();
	    System.out.print(" " + a);
	}

	RandVec rv2 = new RandVec(5, 3);
	System.out.println("\n----- 5-3 " + rv2);
	for(int i = 0; i < rv2.size(); i++) {
	    int a = rv2.getNext();
	    System.out.print(" " + a);
	}

	rv2 = new RandVec(5, 3);
	System.out.println("\n----- 5-3 " + rv2);
	System.out.println("" + S.a2s(rv2.getAsIntArray()));

	rv2 = new RandVec(5, 3);
	System.out.println("\n----- 5-3 " + rv2);
	System.out.println("" + S.a2s(rv2.getAsIntArray(5)));
	System.out.println("" + S.a2s(rv2.getAsIntArray(5)));
	System.out.println("" + S.a2s(rv2.getAsIntArray(5)));
	try {
	    System.out.println("" + S.a2s(rv2.getAsIntArray(5)));
	} catch (Exception ex) {
	    System.out.println("" + ex);
	}

	rvo = new RandVec(5, 3);
	rvc = (RandVec)rvo.clone();

	System.out.println("\n" + rvo + ' ' + rvc);
    }
}
