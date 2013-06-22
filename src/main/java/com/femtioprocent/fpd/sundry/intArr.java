package com.femtioprocent.fpd.sundry;

public class intArr {
    int[] ia = new int[10];
    int ix;
    
    public intArr() {
    }

    public void add(int a) {
	if ( ix < ia.length )
	    ia[ix++] = a;
	else {
	    int[] nia = new int[ia.length * 2];
	    System.arraycopy(ia, 0, nia, 0, ia.length);
	    ia = nia;
	    add(a);
	}
    }

    public int[] getIA() {
	int[] nia = new int[ix];
	System.arraycopy(ia, 0, nia, 0, ix);
	return nia;
    }

    @Override
    public String toString() {
	int[] nia = new int[ix];
	System.arraycopy(ia, 0, nia, 0, ix);
	return '[' + S.a2s(nia) + ']';
    }
}
