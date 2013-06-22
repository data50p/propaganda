package fpg.sundry;

import java.util.HashMap;
import java.util.Iterator;

public class HashCnt {
    HashMap hm;

    public HashCnt() {
	hm = new HashMap();
    }

    @SuppressWarnings("unchecked")
    public void inc(Object o) {
	Integer I;

	if ( (I = (Integer)hm.get(o)) == null ) {
	    hm.put(o, new Integer(1));
	    return;
	}
	hm.put(o, new Integer(I.intValue()+1));
    }

    @SuppressWarnings("unchecked")
    public void add(Object o, int a) {
	Integer I;

	if ( (I = (Integer)hm.get(o)) == null ) {
	    hm.put(o, new Integer(0));
	    return;
	}
	hm.put(o, new Integer(I.intValue()+a));
    }

    @SuppressWarnings("unchecked")
    public void clear(Object o) {
	hm.put(o, new Integer(0));
    }

    public int getCount(Object o) {
	@SuppressWarnings("unchecked") Integer I = (Integer)hm.get(o);
	if ( I == null )
	    return 0;
	return I.intValue();
    }

    @SuppressWarnings("unchecked")
    public Iterator keys() {
	return hm.keySet().iterator();
    }

    @SuppressWarnings("unchecked")
    @Override
    public String toString() {
	StringBuffer sb = new StringBuffer();

	Iterator it = hm.keySet().iterator();
	while(it.hasNext()) {
	    String k = (String)it.next();
	    Integer I = (Integer)hm.get(k);
	    sb.append(" " + k + '=' + S.padRight(""+I, 3, ' '));
	}

	return sb.toString();
    }

    @SuppressWarnings("unchecked")
    public String toStringSN() {
	StringBuffer sb = new StringBuffer();

	for(int i = 1; i < 60; i++) {
	    Iterator it = hm.keySet().iterator();
	    while(it.hasNext()) {
		String k = (String)it.next();
		Integer I = (Integer)hm.get(k);
		if ( ("" + i).equals(k) )
		    sb.append(" " + k + '=' + S.padRight(""+I, 3, ' '));
	    }
	}

	return sb.toString();
    }

    @SuppressWarnings("unchecked")
    public int[] asIA() {
	int ia[] = new int[60];
	
	for(int i = 0; i < 60; i++) {
	    Iterator it = hm.keySet().iterator();
	    while(it.hasNext()) {
		String k = (String)it.next();
		Integer I = (Integer)hm.get(k);
		if ( ("" + i).equals(k) )
		    ia[i] = I.intValue();
	    }
	}	
	return ia;
    }
}
