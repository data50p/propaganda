//
//	$Id: U.java,v 1.3 2000/02/22 09:48:40 lars Exp $
//

package fpg.sundry;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@SuppressWarnings("unchecked")
public class U {

    public static boolean isIn(int[] ia, int a) {
	for(int i = 0; i < ia.length; i++)
	    if ( ia[i] == a )
		return true;
	return false;
    }

    public static int howMany(int[] ia, int a) {
	int s = 0;
	for(int i = 0; i < ia.length; i++)
	    if ( ia[i] == a )
		s++;
	return s;
    }

    public static int sum(int[] ia) {
	int s = 0;
	for(int i = 0; i < ia.length; i++)
	    s += ia[i];
	return s;
    }

    public static int max(int[] ia) {
	return max(ia, 0);
    }

    public static int min(int[] ia) {
	return min(ia, 0);
    }

    public static int max(int[] ia, int offs) {
	int s = Integer.MIN_VALUE;
	for(int i = offs; i < ia.length; i++)
	    if ( ia[i] > s )
		s = ia[i];
	return s;
    }

    public static int min(int[] ia, int offs) {
	int s = Integer.MAX_VALUE;
	for(int i = offs; i < ia.length; i++)
	    if ( ia[i] < s )
		s = ia[i];
	return s;
    }

    public static int[] accum(int[] ia) {
	int[] ac = new int[ia.length];
	int s = 0;
	for(int i = 0; i < ia.length; i++) {
	    s += ia[i];
	    ac[i] = s;
	}
	return ac;
    }

    public static int mul(int[] ia) {
	int s = 1;
	for(int i = 0; i < ia.length; i++)
	    s *= ia[i];
	return s;
    }

    public static void add(int[] ia, int v) {
	for(int i = 0; i < ia.length; i++)
	    ia[i] += v;
    }

    public static int sum(byte[] ia) {
	int s = 0;
	for(int i = 0; i < ia.length; i++)
	    s += ia[i];
	return s;
    }


    public static int[] concat(int[][] iaa) {
	int l = 0;
	for(int i = 0; i < iaa.length; i++)
	    l += iaa[i].length;
	int[] ia = new int[l];
	l = 0;
	for(int i = 0; i < iaa.length; i++) {
	    System.arraycopy(iaa[i], 0, ia, l, iaa[i].length);
	    l += iaa[i].length;
	}
	return ia;
    }

	/** är alla samma */
    public static boolean allSame(int[] ia) {
	for(int i = 0; i < ia.length-1; i++)
	    if ( ia[i] != ia[i+1] )
		 return false;
	return true;
    }

	/** är alla samma parvis */
    public static boolean allSame(int[] ia, int[] ia2) {
	for(int i = 0; i < ia.length; i++)
	    if ( ia[i] != ia2[i] )
		 return false;
	return true;
    }

	/** byt slumpartat och sista */
    public static int[] swapWithLast(int[] ia) {
	int r = S.rand(ia.length);
	int a = ia[r];
	ia[r] = ia[ia.length-1];
	ia[ia.length-1] = a;
	return ia;
    }

	/** ia[1]-ia[1], ia[2]-ia[1],... */
    public static int[] diff(int[] ia) {
	int[] iad = new int[ia.length - 1];
	for(int i = 0; i < iad.length; i++)
	    iad[i] = ia[i+1] - ia[i];
	return iad;
    }

    public static int[] cnt(int[] ia) {
	int max = 0;
	for(int i = 0; i < ia.length; i++)
	    if ( ia[i] > max )
		max=ia[i];
	int[] iac = new int[max+1];
	for(int i = 0; i < ia.length; i++)
	    iac[ia[i]]++;
	return iac;
    }

	/** nia[n] = ia[n] % a */
    public static int[] rem(int[] ia, int a) {
	int[] iac = new int[ia.length];
	for(int i = 0; i < ia.length; i++)
	    iac[i] = ia[i] % a;
	return iac;
    }

	/** nia[n] = ia[n] / a */
    public static int[] div(int[] ia, int a) {
	int[] iac = new int[ia.length];
	for(int i = 0; i < ia.length; i++)
	    iac[i] = ia[i] / a;
	return iac;
    }

    public static int[] mul(int[] ia, int a) {
	int[] iac = new int[ia.length];
	for(int i = 0; i < ia.length; i++)
	    iac[i] = ia[i] * a;
	return iac;
    }

    public static String[] from(String[] sa, int ix) {
 	String[] nsa = new String[sa.length-ix];
 	System.arraycopy(sa, ix, nsa, 0, nsa.length);
 	return nsa;
    }

    public static int[] copy(int[] ia) {
	return copy(ia, ia.length);
    }

    public static int[] copy(int[] ia, int len) {
	if ( len > ia.length )
	    len = ia.length;
	int[] iac = new int[len];
	System.arraycopy(ia, 0, iac, 0, len);
	return iac;
    }

    public static void scrambleIntArr(int[] arr) {
	for(int i = 0; i < arr.length; i++) {
	    int b = S.rand(arr.length);
	    int t = arr[i];
	    arr[i] = arr[b];
	    arr[b] = t;
	}
    }

    public static List filter(List li, Lambda la) {
	List nl = new ArrayList();

	Iterator it = li.iterator();
	while (it.hasNext()) {
	    Object o = it.next();
	    Boolean B = (Boolean)la.eval(o);
	    if ( B.booleanValue() )
		nl.add(o);
	}

	return nl;
    }

    public static List filter(List li, Lambda la, Object oa) {
	List nl = new ArrayList();

	Iterator it = li.iterator();
	while (it.hasNext()) {
	    Object o = it.next();
	    Boolean B = (Boolean)la.eval(o, oa);
	    if ( B.booleanValue() )
		nl.add(o);
	}

	return nl;
    }

    public static List map(List li, Lambda la) {
	List nl = new ArrayList();

	Iterator it = li.iterator();
	while (it.hasNext()) {
	    Object o = it.next();
	    Object r = la.eval(o);
	    nl.add(r);
	}

	return nl;
    }

    public static List map(List li, Lambda la, Object oa) {
	List nl = new ArrayList();

	Iterator it = li.iterator();
	while (it.hasNext()) {
	    Object o = it.next();
	    Object r = la.eval(o, oa);
	    nl.add(r);
	}

	return nl;
    }

    public static Object reduce(Lambda la, Object oz, List li) {
	Iterator it = li.iterator();
	while (it.hasNext()) {
	    Object o = it.next();
	    oz = la.eval(oz, o);
	}

	return oz;
    }

    public static Object itlist(Lambda la, List li, Object oz) {
	Iterator it = li.iterator();
	while (it.hasNext()) {
	    Object o = it.next();
	    oz = la.eval(oz, o);
	}

	return oz;
    }

    public static Object revitlist(Lambda la, List li, Object oz) {
	for(int i = li.size()-1; i >= 0; i--) {
	    Object o = li.get(i);
	    oz = la.eval(o, oz);
	}

	return oz;
    }

    public static String[] flatten(List[] liA) { // List<String>[]

	int cnt = 0;
	for(int i = 0; i < liA.length; i++) {
	    List li = liA[i];
	    cnt += li.size();
	}

	String[] sa = new String[cnt];
	int ix = 0;
	for(int i = 0; i < liA.length; i++) {
	    List li = liA[i];
	    Iterator it = li.iterator();
	    while(it.hasNext()) {
		sa[ix++] = (String)it.next();
	    }
	}
	return sa;
    }

    public static Integer[] flattenI(List[] liA) { // List<Integer>[]

	int cnt = 0;
	for(int i = 0; i < liA.length; i++) {
	    List li = liA[i];
	    cnt += li.size();
	}

	Integer[] sa = new Integer[cnt];
	int ix = 0;
	for(int i = 0; i < liA.length; i++) {
	    List li = liA[i];
	    Iterator it = li.iterator();
	    while(it.hasNext()) {
		sa[ix++] = (Integer)it.next();
	    }
	}
	return sa;
    }
}
