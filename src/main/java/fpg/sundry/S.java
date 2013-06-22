//
//	$Id: S.java,v 1.3 2000/02/22 09:48:40 lars Exp $
//

package fpg.sundry;

import java.io.*;
import java.util.*;

import java.security.*;

/**
   Convenient functions for the effective programmer.
   <p>
   Many tedius task are handled here. This class is expected to be used everywhere.
 */
final public class S {

    /**
       Sleep for some milliseconds.
       Might return bofore timeout if it is interrupted.
       @param a how many millisecunds to sleep.
     */
    public static void m_sleep(int a) {
	try {
	    Thread.sleep(a);
	} catch (InterruptedException e) {}
    }

    private static Stack pw_stack = new Stack();

    /**
       Set a flag to make all output printed by S.p() to be flushed on the spot.
     */
    public static void setAutoFlush() {
	System.out.flush();
	PrintStream ps = new PrintStream(System.out, true);
	System.out.flush();
	System.setOut(ps);
    }

    /**
       Change the standard output and save the old on top of a stack.
       @param pw the new PrintStream to use
     */
    @SuppressWarnings("unchecked")
    public static void pushStdout(PrintStream ps) {
	System.out.flush();
	PrintStream ops = System.out;
	System.setOut(ps);
	pw_stack.push(ops);
// 	pw_stack.push(opw);
    }

    /**
       Change the standard output to what is on top of stack and pop it.
     */
    @SuppressWarnings("unchecked")
    public static void popStdout() {
	System.out.flush();
 	PrintStream ps = (PrintStream)pw_stack.pop();
 	System.setOut(ps);
    }

    public static PrintWriter setStdpw(PrintWriter pw) {
	S.pL("NOT SUPPORTED setStdpw");
	System.exit(1);


// 	//	if ( auto_flush )
// 	    S.pw.flush();
// 	PrintWriter opw = S.pw;
// 	if ( pw == null )
// 	    pw = new PrintWriter(System.out);
// 	S.pw = pw;
// 	return opw;
	return null;
    }

    public static void pushStdpw(PrintWriter pw) {
	S.pL("NOT SUPPORTED setStdpw");
	System.exit(1);
// 	PrintWriter opw = S.setStdpw(pw);
// 	pw_stack.push(opw);
    }

    public static void popStdpw() {
	S.pL("NOT SUPPORTED popStdpw");
	System.exit(1);
// 	PrintWriter pw = (PrintWriter)pw_stack.pop();
// 	S.setStdpw(pw);
    }

    public static PrintWriter getStdpw() {
	S.pL("NOT SUPPORTED getStdpw");
	System.exit(1);
	//	return pw;
	return null;
    }

    /**
       Change System.out encoding to UTF-8.
     */
    public static void utf8() {
	try {
	    System.out.flush();
	    OutputStream os = System.out;
	    PrintStream ps = new PrintStream(os, true, "UTF-8");
	    System.out.flush();
	    System.setOut(ps);
	} catch (UnsupportedEncodingException ex) {
	}
    }

    /**
       Same as: System.out.println(s);
    */
    public static final void p(String s) {
	System.out.println(s);
    }

    /**
       Same as: System.out.print(s);
    */
    public static final void p_(String s) {
	System.out.print(s);
    }

    /**
       Same as: System.out.flush();
    */
    public static final void flush() {
	System.out.flush();
    }

    /**
       Same as: System.err.println(s);
    */
    public static final void pL(String s) {
        System.err.println("" + s);
    }

    /**
       Same as: System.err.println(s);
    */
    public static final void pe(String s) {
	System.err.println(s);
    }

    /**
       Same as: System.err.print(s);
    */
    public static final void pe_(String s) {
	System.err.print(s);
    }

    /**
       Same as: pw.println(s);
    */
    public static final void p(PrintWriter pw, String s) {
	pw.println(s);
    }

    /**
       Same as: pw.print(s);
    */
    public static final void p_(PrintWriter pw, String s) {
	pw.print(s);
    }

// - - - - - - - - - - - - - - - - - - - - - - - - - - -
    private static int prio_level = 0;

    /**
	Println on System.err if priority is hi enough.
	@param prio print only if prio is below level
	@param s String to print
     */
    public static void pt(int prio, String s) {
	if ( prio <= prio_level )
	    pL(s);
    }
    /**
	Print on System.err if priority is hi enough.
	@param prio print only if prio is below level
	@param s String to print
     */
    public static void pt_(int prio, String s) {
	if ( prio <= prio_level )
	    pe_(s);
    }
    /**
       Set the priority level.
       Default priority level is 0.
       @param prio_level priority level.
       @return old priority level
     */
    public static int setPrintPrio(int prio_level) {
	int r = prio_level;
	S.prio_level = prio_level;
	return r;
    }

// - - - - - - - - - - - - - - - - - - - - - - - - - - -

    /**
       Open a new BufferedReader.
       @param fn the file name of opened file
       @return the opened BufferedReader or null
     */
    public static BufferedReader fopenr(String fn) {
	try {
	    FileReader fr = new FileReader(fn);
	    return new BufferedReader(fr);
	} catch (Exception ex) {
	    return null;
	}
    }

    /**
       Create a new PrintWriter from a file name.
       @param fn file name
       @return created PrintWriter or null
     */
    public static PrintWriter createPrintWriter(String fn) {
	return createPrintWriter(fn, false);
    }

    /**
       Create a new PrintWriter from a file name.
       Append at end of file.
       @param fn file name
       @return created PrintWriter or null
     */
    public static PrintWriter createPrintWriter(String fn, boolean append) {
	try {
	    PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(fn, append), 2000));
	    return pw;
	} catch (IOException ex) {
	    return null;
	}
    }

    /**
       Create a new PrintWriter from a OutputStream.
       @param os OutputStream
       @return created PrintWriter or null
     */
    public static PrintWriter createPrintWriter(OutputStream os) {
	PrintWriter pw = new PrintWriter(new OutputStreamWriter(new BufferedOutputStream(os)));
	return pw;
    }

    /**
       Create a new PrintWriter from a OutputStream. Use UTF-8 encoding.
       @param os OutputStream
       @return created PrintWriter or null
     */
    public static PrintWriter createPrintWriterUTF8(OutputStream os) {
	try {
	    PrintWriter pw = new PrintWriter(new OutputStreamWriter(new BufferedOutputStream(os), "UTF-8"));
	    return pw;
	} catch (IOException ex) {
	    return null;
	}
    }

    /**
       Create a new PrintWriter from a file name.
       Use UTF-8 encoding.
       @param fn file name
       @return created PrintWriter or null
     */
    public static PrintWriter createPrintWriterUTF8(String fn) {
	return createPrintWriterUTF8(fn, false);
    }

    /**
       Create a new PrintWriter from a file name.
       Use UTF-8 encoding.
       Append at end of file.
       @param fn file name
       @return created PrintWriter or null
     */
    public static PrintWriter createPrintWriterUTF8(String fn, boolean append) {
	try {
	    PrintWriter pw = new PrintWriter(new OutputStreamWriter(new BufferedOutputStream(new FileOutputStream(fn, append)), "UTF-8"));
	    return pw;
	} catch (IOException ex) {
	    return null;
	}
    }

// - - - - - - - - - - - - - - - - - - - - - - - - - - -

    /**
       Prefix a small number [0-9], with '0'.
     */
    public static String as02(int d) {
	if ( d < 10 )
	    return "0" + d;
	else
	    return "" + d;
    }

    /**
       Prefix a small number [0-9], with ' '.
     */
    public static String as_2(int d) {
	if ( d < 10 )
	    return " " + d;
	else
	    return "" + d;
    }

    /**
       Format a double as 2 decimal digits.
     */
    public static String as_02(double d) {
	if ( d < 0 )
	    return "-" + as_02(-d);
	int a = (int)d;
	double f = d - a;
	f *= 100;
	int b = (int)(f + 0.001);
	if ( b == 100 )
	    return "" + (a+1) + ".00";
	else if ( b < 10 )
	    return "" + a + ".0" + b;
	else
	    return "" + a + "." + b;
    }

    /**
       Format an integer with a specified width. Pad on right side
     */
    public static String pR(int a, int w) {
	return padRight("" + a, w, ' ');
    }

    /**
       Format an integer with a specified width. Pad on left side
     */
    public static String pL(int a, int w) {
	return padLeft("" + a, w, ' ');
    }

    /**
       Format an integer with a specified width. Pad on left side
     */
    public static String pL(int a, int w, char pad) {
	return padLeft("" + a, w, pad);
    }

    /**
       Format an integer with a specified width. Pad on right side
     */
    public static String pR(int a, int w, char pad) {
	return padRight("" + a, w, pad);
    }

    private static char[] pad_blank = new char[30];
    private static char[] pad_zero  = new char[30];

    static {
	Arrays.fill(pad_blank, ' ');
	Arrays.fill(pad_zero, '0');
    }

    /**
       Format an integer with a specified width. Pad on left side
     */
    public static String padLeft(String s, int len, char ch) {
	int l = len - s.length();
	if ( l <= 0 )
	    return s;

	char[] chA;
	if ( l <= 30 ) {
	    if ( ch == ' ' )
		chA = pad_blank;
	    else if ( ch == '0' )
		chA = pad_zero;
	    else {
		chA = new char[l];
		Arrays.fill(chA, ch);
	    }
	    return new String(chA, 0, l) + s;
	}
	if ( ch == ' ' )
	    chA = pad_blank;
	else if ( ch == '0' )
	    chA = pad_zero;
	else {
	    chA = new char[l];
	    Arrays.fill(chA, ch);
	}
	return padLeft(new String(chA) + s, len, ch);
    }

    /**
       Format an integer with a specified width. Pad on right side
     */
    public static String padRight(String s, int len, char ch) {
	int l = len - s.length();
	if ( l <= 0 )
	    return s;

	char[] chA;
	if ( l <= 30 ) {
	    if ( ch == ' ' )
		chA = pad_blank;
	    else if ( ch == '0' )
		chA = pad_zero;
	    else {
		chA = new char[l];
		Arrays.fill(chA, ch);
	    }
	    return s + new String(chA, 0, l);
	}
	if ( ch == ' ' )
	    chA = pad_blank;
	else if ( ch == '0' )
	    chA = pad_zero;
	else {
	    chA = new char[l];
	    Arrays.fill(chA, ch);
	}
	return padRight(s + new String(chA), len, ch);
    }

// - - - - - - - - - - - - - - - - - - - - - - - - - - -

    /**
       return time in milliseconds.
       Same as System.currentTimeMillis();
     */
    public static long ct() {
	return System.currentTimeMillis();
    }

// - - - - - - - - - - - - - - - - - - - - - - - - - - -

  

    /**
       Split a String into an Array of Strings.
       @param str String to split
       @param split which character to have as split point. Any character in split can be split charachter.
       @return Array of String
     */
    public static String[] split(String str, String split) {
	StringTokenizer t = new StringTokenizer(str, split);

	int n = t.countTokens();
	int i = 0;
	String[] arr = new String[n];

	for (; t.hasMoreTokens() ; ) {
	    String word = t.nextToken();
	    arr[i++] = word;
	}
	return arr;
    }

    /**
       Split a String into an Array of int. Convert string to int.
       @param str String to split
       @param split which character to have as split point. Any character in split can be split charachter.
       @return Array of int
     */
    public static int[] splitI(String str, String split) {
	String sa[] = split(str, split);
	int[] ia = (int[])castArray(sa, new int[0]);
	return ia;
    }

    /**
       Split a String into an Array of double. Convert string to double.
       @param str String to split
       @param split which character to have as split point. Any character in split can be split charachter.
       @return Array of double
     */
    public static double[] splitD(String str, String split) {
	String sa[] = split(str, split);
	double[] ia = (double[])castArray(sa, new double[0]);
	return ia;
    }

// - - - - - - - - - - - - - - - - - - - - - - - - - - -

    /**
       Convert an array of object into a String representation.
       Delimiter is ','.
       Format each member with width w and pad with ' '.
     */
    public static String a2s(Object o, int w) {
	return arrToString(o, ",", w, ' ');
    }

    /**
       Convert an array of object into a String representation.
       Delimiter is ','.
     */
    public static String a2s(Object o) {
	return arrToString(o, ",", 0, ' ');
    }

    /**
       Convert an array of object into a String representation.
       Delimiter is delim.
     */
    public static String a2s(Object o, String delim) {
	return arrToString(o, delim, 0, ' ');
    }

    /**
       Convert an array of object into a String representation.
       Delimiter is ','.
       Format each member with width 'w' and pad with 'pad'.
     */
    public static String a2s(Object o, String delim, int w, char pad) {
	return arrToString(o, delim, w, pad);
    }

    /**
       Convert an array of object into a String representation.
       Delimiter is ','.
       Format each member with width w and pad with ' '.
     */
    public static String arrToString(Object o, int w) {
	return arrToString(o, ",", w, ' ');
    }

    /**
       Convert an array of object into a String representation.
       Delimiter is ','.
     */
    public static String arrToString(Object o) {
	return arrToString(o, ",", 0, ' ');
    }

    /**
       Convert an array of object into a String representation.
       Delimiter is delim.
     */
    public static String arrToString(Object o, String delim) {
	return arrToString(o, delim, 0, ' ');
    }

    /**
       Convert an array of object into a String representation.
       Delimiter is ','.
       Format each member with width w and pad with ' '.
       Arrays of Array are handled recursivly.
       <p>
       I.e. the array of 3 int arrays are returned like this:<br>
       "[1,2,3],[4,5,6],[7,8,9]"
       <p>
       toString are used for printing Objects.
     */
    @SuppressWarnings("unchecked")
    public static String arrToString(Object o, String delim, int w, char pad) {
	if ( o == null )
	    return "null";
	StringBuffer s = new StringBuffer();
	Class cls = o.getClass();
	if ( cls.isArray() ) {
	    Class clsc = cls.getComponentType();
	    if ( !clsc.isPrimitive() ) {
		if ( clsc.isArray() ) {
		    Object[] oa = (Object[])o;
		    for(int i = 0; i < oa.length; i++)
			s.append((i == 0 ? "" : delim) +
			    "[" + arrToString(oa[i]) + "]");
		    return s.toString();
		} else {
		    Object[] oa = (Object[])o;
		    for(int i = 0; i < oa.length; i++)
			if ( w == 0 )
			    s.append((i == 0 ? "" : delim) + oa[i]);
			else
			    s.append((i == 0 ? "" : delim) + padLeft("" + oa[i], w, pad));
		    return s.toString();
		}
	    }
	    if ( clsc.getName().equals("int") ) {
		int[] ia = (int[])o;
		for(int i = 0; i < ia.length; i++)
		    if ( w == 0 )
			s.append((i == 0 ? "" : delim) + ia[i]);
		    else
			s.append((i == 0 ? "" : delim) + pL(ia[i], w, pad));
		return s.toString();
	    }
	    if ( clsc.getName().equals("char") ) {
		char[] ia = (char[])o;
		for(int i = 0; i < ia.length; i++)
		    if ( ia[i] == 0 )
			if ( w == 0 )
			    s.append((i == 0 ? "" : delim) + "^@");
			else
			    s.append((i == 0 ? "" : delim) + padLeft("^@", w, pad));
		    else
			if ( w == 0 )
			    s.append((i == 0 ? "" : delim) + ia[i]);
			else
			    s.append((i == 0 ? "" : delim) + padLeft("" + ia[i], w, pad));
		return s.toString();
	    }
	    if ( clsc.getName().equals("byte") ) {
		byte[] ia = (byte[])o;
		for(int i = 0; i < ia.length; i++)
		    if ( w == 0 )
			s.append((i == 0 ? "" : delim) + ia[i]);
		    else
			s.append((i == 0 ? "" : delim) + padLeft("" + ia[i], w, pad));
		return s.toString();
	    }
	    if ( clsc.getName().equals("short") ) {
		short[] ia = (short[])o;
		for(int i = 0; i < ia.length; i++)
		    if ( w == 0 )
			s.append((i == 0 ? "" : delim) + ia[i]);
		    else
			s.append((i == 0 ? "" : delim) + padLeft("" + ia[i], w, pad));
		return s.toString();
	    }
	    if ( clsc.getName().equals("boolean") ) {
		boolean[] ba = (boolean[])o;
		for(int i = 0; i < ba.length; i++)
		    if ( w == 0 )
			s.append((i == 0 ? "" : delim) + ba[i]);
		    else
			s.append((i == 0 ? "" : delim) + padLeft("" + ba[i], w, pad));
		return s.toString();
	    }
	}
	return null;
    }

// - - - - - - - - - - - - - - - - - - - - - - - - - - -

//      public static Object copyArr(Object o) {
//  	if ( o == null )
//  	    return null;
//  	Class cls = o.getClass();
//  	if ( cls.isArray() ) {
//  	    Class clsc = cls.getComponentType();
//  	    int len = java.lang.reflect.Array.getLength(o);
//  	    Object na = java.lang.reflect.Array.newInstance(clsc, len);
//  	    System.arraycopy(o, 0, na, 0, len);
//  	    return na;
//  	}
//  	return null;
//      }

	// use:        int[] ia = new int[10];
	//             int[] ia2 = (int[])ia.clone());
// - - - - - - - - - - - - - - - - - - - - - - - - - - -

    /**
       Convert an Array of one type to an Array of another type.
       Only a subset of cast is supported.
       @param o Array to convert
       @param to An object declared as the type of Array to convert to
       @return the casted array as same type as to
     */
    @SuppressWarnings("unchecked")
    public static Object castArray(Object o, Object to) {
	if ( o == null )
	    return null;
	Class cls = o.getClass();
	Class tcls = to.getClass();
	if ( cls.isArray() ) {
	    Class clsc = cls.getComponentType();
	    Class tclsc = tcls.getComponentType();
	    if ( !tclsc.isPrimitive() ) {
		throw new
		    RuntimeException("cast obj[] -> obj'[] Not supported yet");
	    }
	    if ( tclsc.getName().equals("int") ) {
		if ( clsc.getName().equals("java.lang.String") ) {
		    int l = java.lang.reflect.Array.getLength(o);
		    int[] ia = new int[l];
		    String[] sa = (String[])o;
		    try {
			for(int i = 0; i < ia.length; i++) {
			    ia[i] = Integer.parseInt(sa[i]);
			}
			return ia;
		    } catch (NumberFormatException ex) {
			return null;
		    }
		} else {
		    int l = java.lang.reflect.Array.getLength(o);
		    int[] ia = new int[l];
		    for(int i = 0; i < ia.length; i++) {
			ia[i] = java.lang.reflect.Array.getInt(o, i);
		    }
		    return ia;
		}
//  		if ( clsc.getName().equals("byte") ) {
//  			ia[i] = java.lang.reflect.Array.getByte(o, i);
//  		    } else if ( clsc.getName().equals("short") ) {
//  			ia[i] = java.lang.reflect.Array.getShort(o, i);
//  		    } else if ( clsc.getName().equals("int") ) {
//  			ia[i] = (int)java.lang.reflect.Array.getInt(o, i);
//  		    } else if ( clsc.getName().equals("long") ) {
//  			ia[i] = (int)java.lang.reflect.Array.getLong(o, i);
//  		    } else if ( clsc.getName().equals("char") ) {
//  			ia[i] = java.lang.reflect.Array.getChar(o, i);
//  		    } else if ( clsc.getName().equals("double") ) {
//  			ia[i] = (int)java.lang.reflect.Array.getDouble(o, i);
//  		    } else if ( clsc.getName().equals("float") ) {
//  			ia[i] = (int)java.lang.reflect.Array.getFloat(o, i);
//  		    }
//  		}
	    } else if ( tclsc.getName().equals("double") ) {
		if ( clsc.getName().equals("java.lang.String") ) {
		    int l = java.lang.reflect.Array.getLength(o);
		    double[] ia = new double[l];
		    String[] sa = (String[])o;
		    for(int i = 0; i < ia.length; i++) {
			ia[i] = tD(sa[i]);
		    }
		    return ia;
		} else {
		    int l = java.lang.reflect.Array.getLength(o);
		    double[] ia = new double[l];
		    for(int i = 0; i < ia.length; i++) {
			ia[i] = java.lang.reflect.Array.getDouble(o, i);
		    }
		    return ia;
		}
//  		if ( clsc.getName().equals("byte") ) {
//  			ia[i] = java.lang.reflect.Array.getByte(o, i);
//  		    } else if ( clsc.getName().equals("short") ) {
//  			ia[i] = java.lang.reflect.Array.getShort(o, i);
//  		    } else if ( clsc.getName().equals("int") ) {
//  			ia[i] = (int)java.lang.reflect.Array.getInt(o, i);
//  		    } else if ( clsc.getName().equals("long") ) {
//  			ia[i] = (int)java.lang.reflect.Array.getLong(o, i);
//  		    } else if ( clsc.getName().equals("char") ) {
//  			ia[i] = java.lang.reflect.Array.getChar(o, i);
//  		    } else if ( clsc.getName().equals("double") ) {
//  			ia[i] = (int)java.lang.reflect.Array.getDouble(o, i);
//  		    } else if ( clsc.getName().equals("float") ) {
//  			ia[i] = (int)java.lang.reflect.Array.getFloat(o, i);
//  		    }
//  		}
	    } else if ( tclsc.getName().equals("char") ) {
		int l = java.lang.reflect.Array.getLength(o);
		char[] ia = new char[l];
		for(int i = 0; i < ia.length; i++) {
		    if ( clsc.getName().equals("byte") ) {
			ia[i] = (char)java.lang.reflect.Array.getByte(o, i);
		    } else if ( clsc.getName().equals("short") ) {
			ia[i] = (char)java.lang.reflect.Array.getShort(o, i);
		    } else if ( clsc.getName().equals("int") ) {
			ia[i] = (char)java.lang.reflect.Array.getInt(o, i);
		    } else if ( clsc.getName().equals("long") ) {
			ia[i] = (char)java.lang.reflect.Array.getLong(o, i);
		    } else if ( clsc.getName().equals("char") ) {
			ia[i] = java.lang.reflect.Array.getChar(o, i);
		    } else if ( clsc.getName().equals("double") ) {
			ia[i] = (char)java.lang.reflect.Array.getDouble(o, i);
		    } else if ( clsc.getName().equals("float") ) {
			ia[i] = (char)java.lang.reflect.Array.getFloat(o, i);
		    }
		}
		return ia;
	    }
	}
	return null;
    }

// - - - - - - - - - - - - - - - - - - - - - - - - - - -

    static int scrN = 5;

    /**
       Set a value for how many times a array is mixed.
       @param n how many time to mix the whole array
       @see S#scrambleArr scrambleArr
     */
    public static void setScrambleN(int n) {
	scrN = n;
    }

    /**
       Randomly mix content in an Array.
       Note! Scramble in place.
     */
    public static void scrambleArr(Object a) {
	if ( a == null )
	    return;
	Class cls = a.getClass();
	if ( cls.isArray() ) {
	    @SuppressWarnings("unchecked") Class clsc = cls.getComponentType();
	    if ( !clsc.isPrimitive() ) {
		Object[] arr = (Object[])a;
		for(int j = 0; j < scrN; j++)
		    for(int i = 0; i < arr.length; i++) {
			int b = S.rand(arr.length);
			Object t = arr[i];
			arr[i] = arr[b];
			arr[b] = t;
		    }
	    }
	    if ( clsc.getName().equals("int") ) {
		int[] arr = (int[])a;
		for(int j = 0; j < scrN; j++)
		    for(int i = 0; i < arr.length; i++) {
			int b = S.rand(arr.length);
			int t = arr[i];
			arr[i] = arr[b];
			arr[b] = t;
		    }
	    }
	    if ( clsc.getName().equals("long") ) {
		long[] arr = (long[])a;
		for(int j = 0; j < scrN; j++)
		    for(int i = 0; i < arr.length; i++) {
			int b = S.rand(arr.length);
			long t = arr[i];
			arr[i] = arr[b];
			arr[b] = t;
		    }
	    }
	    if ( clsc.getName().equals("char") ) {
		char[] arr = (char[])a;
		for(int j = 0; j < scrN; j++)
		    for(int i = 0; i < arr.length; i++) {
			int b = S.rand(arr.length);
			char t = arr[i];
			arr[i] = arr[b];
			arr[b] = t;
		    }
	    }
	    if ( clsc.getName().equals("byte") ) {
		byte[] arr = (byte[])a;
		for(int j = 0; j < scrN; j++)
		    for(int i = 0; i < arr.length; i++) {
			int b = S.rand(arr.length);
			byte t = arr[i];
			arr[i] = arr[b];
			arr[b] = t;
		    }
	    }
	    if ( clsc.getName().equals("short") ) {
		short[] arr = (short[])a;
		for(int j = 0; j < scrN; j++)
		    for(int i = 0; i < arr.length; i++) {
			int b = S.rand(arr.length);
			short t = arr[i];
			arr[i] = arr[b];
			arr[b] = t;
		    }
	    }
	    if ( clsc.getName().equals("double") ) {
		double[] arr = (double[])a;
		for(int j = 0; j < scrN; j++)
		    for(int i = 0; i < arr.length; i++) {
			int b = S.rand(arr.length);
			double t = arr[i];
			arr[i] = arr[b];
			arr[b] = t;
		    }
	    }
	    if ( clsc.getName().equals("float") ) {
		float[] arr = (float[])a;
		for(int j = 0; j < scrN; j++)
		    for(int i = 0; i < arr.length; i++) {
			int b = S.rand(arr.length);
			float t = arr[i];
			arr[i] = arr[b];
			arr[b] = t;
		    }
	    }
	}
	return;
    }

    /**
       Randomly mix content in an Array.
       Return a new String.
     */
    public static String scrambleStr(String s) {
	char[] arr = s.toCharArray();
	scrambleArr(arr);
	return new String(arr);
    }


    /*
	try {
	    Class type = Class.forName("int");
	    int size = 10;
	    Object o = java.lang.reflect.Array.newInstance(type, size);

	    for(int i = 0; i < size; i++) {
		java.lang.reflect.Array.setInt(o, i, i);
	    }
	    ff(o);
	} catch (Exception ex) {
	    S.p("ex " + ex);
	}
    */

// - - - - - - - - - - - - - - - - - - - - - - - - - - -

    /**
       Convert the String to double value.
       Return 0 if exception occur.
     */
    public static double tD(String s) {
	try {
	    Double dval = Double.valueOf(s);
	    double d = dval.doubleValue();
	    return d;
	} catch (Exception ex) {
	    return 0.0;
	}
    }

// - - - - - - - - - - - - - - - - - - - - - - - - - - -

    /**
       Return a array with number [0, 1, .. , a-1, a)
     */
    public static int[] upTo(int a) {
	return fromTo(0, a);
    }

    /**
       Return a array with number [a, a+1, .. , b-1, b)
     */
    public static int[] fromTo(int a, int b) {
	int ia[] = new int[b-a];
	int ii = 0;
	for(int i = a; i < b; i++)
	    ia[ii++] = i;
	return ia;
    }

// - - - - - - - - - - - - - - - - - - - - - - - - - - -

    /**
       Swap integers at place 'a' and 'b'
     */
    public static void swapia(int[] ia, int a, int b) {
	int c = ia[a];
	ia[a] = ia[b];
	ia[b] = c;
    }

// - - - - - - - - - - - - - - - - - - - - - - - - - - -

    /**
       Create uniq number from 0 to N (not included)
     */
    static public class Uniq {
	int max;
	int[] picked;
	int taken = 0;

	public Uniq(int max) {
	    this(max, false);
	}

	public Uniq(int max, boolean scramble) {
	    this.max = max;
	    picked = S.upTo(max);
	    if ( scramble )
		scrambleArr(picked);
	    taken = 0;
	}

	/**
	   Get next uniq integer.
	 */
	public synchronized int getNext() {
	    int c = 0;
	    for(;;) {
		int ix = S.rand(max);
		if ( picked[ix] != -1 ) {
		    int r = picked[ix];
		    picked[ix] = -1;
		    taken++;
		    return r;
		} else {
		    if ( ++c > 5 ) {
			int[] ia = new int[max-taken];
			int ix2 = 0;
			for(int i = 0; i < picked.length; i++)
			    if ( picked[i] != -1 )
				ia[ix2++] = picked[i];
			picked = ia;
			max = picked.length;
			taken = 0;
//			S.p("<" + max + ">");
			return getNext();
		    }
		}
	    }
	}

	/**
	   Return all uniq integers in an array. Or null if not permitted.
	 */
	public synchronized int[] asIntArray() {
	    if ( taken != 0 ) {
		return null;
	    }
	    int[] ia = new int[max];
	    for(int i = 0; i < ia.length; i++)
		ia[i] = getNext();
	    return ia;
	}

	/**
	   Return 'n' uniq integers in an array. Or null if not permitted.
	 */
	public synchronized int[] asIntArray(int n) {
	    if ( taken != 0 ) {
		return null;
	    }
	    int[] ia = new int[n];
	    for(int i = 0; i < n; i++)
		ia[i] = getNext();
	    return ia;
	}
    };

    /**
       Create an Uniq object
       Same as: new S.Uniq(max);
     */
    public static Uniq createUniq(int max) {
	return new S.Uniq(max);
    }


    /**
       Create an Uniq object
       Same as: new S.Uniq(max, scramble);
     */
    public static Uniq createUniq(int max, boolean scramble) {
	return new S.Uniq(max, scramble);
    }

// - - - - - - - - - - - - - - - - - - - - - - - - - - -

    /**
       Return a secure random integer between [0 - a).
     */
    public static int rand(int a) {
	return secRand(a);
    }

// - - - - - - - - - - - - - - - - - - - - - - - - - - -

    private static SecureRandom sec_rand;

    static {
	try {
	    sec_rand = SecureRandom.getInstance("SHA1PRNG", "SUN");
	} catch (Exception ex) {
	    S.pL("" + ex);
	}
    }

    /**
       Return a secure random integer between [0 - a).
     */
    public static int secRand(int n) {
	return sec_rand.nextInt(n);
    }

// - - - - - - - - - - - - - - - - - - - - - - - - - - -

    /**
       Scan the argument for flags. A flag is prefixed with '-'.
       <p>
       Flag can have arguments. Save them all in a HashMap.
       <p>
       I.e.
       <p>
       -flag=value
     */
    public static HashMap<String, String> flagAsMap(String[] argv) {
	final List argl = Arrays.asList(argv);
	HashMap<String, String> flag = new HashMap<String, String>();

	for(int i = 0; i < argl.size(); i++) {
	    @SuppressWarnings("unchecked") String s = (String)argl.get(i);
	    if ( s.startsWith("-") && ! s.equals("-") ) {
		String ss = s.substring(1);
		int ix = ss.indexOf('=');
		if ( ix != -1 ) {
		    String sk = ss.substring(0, ix);
		    String sv = ss.substring(ix+1);
		    if ( sv.indexOf(',') == -1 )
			flag.put(sk, sv);
		    else {
			flag.put(sk, sv);
			//flag.put("[S;" + sk, sa);
		    }
		} else {
		    flag.put(ss, "");
		}
	    }
	}
	return flag;
    }

    /**
       Scan argument and put in List those that are not flags.
     */
    public static List<String> argAsList(String[] argv) {
	List<String> argl = new LinkedList<String>(Arrays.asList(argv));

	for (int i = 0; i < argl.size(); i++) {
             String s = argl.get(i);

             if (s.startsWith("-") && !s.equals("-")) {
                 argl.remove(i);
                 i--;
             }
         }
	return argl;
    }

    public static void main(String[] args) {
    }
}

