//
//	$Id: S.java,v 1.3 2000/02/22 09:48:40 lars Exp $
//
package com.femtioprocent.fpd.sundry;

import java.io.*;
import java.util.*;

import java.security.*;

/**
 * Convenient functions for the effective programmer.
 * <p>
 * Many tedius task are handled here. This class is expected to be used everywhere.
 */
final public class S {

    /**
     * Sleep for some milliseconds. Might return bofore timeout if it is interrupted.
     *
     * @param a how many millisecunds to sleep.
     */
    public static void m_sleep(int a) {
        try {
            Thread.sleep(a);
        } catch (InterruptedException e) {
        }
    }

    /**
     * Change System.out encoding to UTF-8.
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
     * Same as: System.err.println(s);
     */
    public static final void pL(String s) {
        System.err.println("" + s);
    }

// - - - - - - - - - - - - - - - - - - - - - - - - - - -
    /**
     * Open a new BufferedReader.
     *
     * @param fn the file name of opened file
     * @return the opened BufferedReader or null
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
     * Create a new PrintWriter from a file name.
     *
     * @param fn file name
     * @return created PrintWriter or null
     */
    public static PrintWriter createPrintWriter(String fn) {
        return createPrintWriter(fn, false);
    }

    /**
     * Create a new PrintWriter from a file name. Append at end of file.
     *
     * @param fn file name
     * @return created PrintWriter or null
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
     * Create a new PrintWriter from a OutputStream.
     *
     * @param os OutputStream
     * @return created PrintWriter or null
     */
    public static PrintWriter createPrintWriter(OutputStream os) {
        PrintWriter pw = new PrintWriter(new OutputStreamWriter(new BufferedOutputStream(os)));
        return pw;
    }

    /**
     * Create a new PrintWriter from a OutputStream. Use UTF-8 encoding.
     *
     * @param os OutputStream
     * @return created PrintWriter or null
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
     * Create a new PrintWriter from a file name. Use UTF-8 encoding.
     *
     * @param fn file name
     * @return created PrintWriter or null
     */
    public static PrintWriter createPrintWriterUTF8(String fn) {
        return createPrintWriterUTF8(fn, false);
    }

    /**
     * Create a new PrintWriter from a file name. Use UTF-8 encoding. Append at end of file.
     *
     * @param fn file name
     * @return created PrintWriter or null
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
     * Format an integer with a specified width. Pad on right side
     */
    public static String pR(int a, int w) {
        return padRight("" + a, w, ' ');
    }

    /**
     * Format an integer with a specified width. Pad on left side
     */
    public static String pL(int a, int w) {
        return padLeft("" + a, w, ' ');
    }

    /**
     * Format an integer with a specified width. Pad on left side
     */
    public static String pL(int a, int w, char pad) {
        return padLeft("" + a, w, pad);
    }

    /**
     * Format an integer with a specified width. Pad on right side
     */
    public static String pR(int a, int w, char pad) {
        return padRight("" + a, w, pad);
    }

    private static char[] pad_blank = new char[30];
    private static char[] pad_zero = new char[30];

    static {
        Arrays.fill(pad_blank, ' ');
        Arrays.fill(pad_zero, '0');
    }

    /**
     * Format an integer with a specified width. Pad on left side
     */
    public static String padLeft(String s, int len, char ch) {
        int l = len - s.length();
        if (l <= 0) {
            return s;
        }

        char[] chA;
        if (l <= 30) {
            if (ch == ' ') {
                chA = pad_blank;
            } else if (ch == '0') {
                chA = pad_zero;
            } else {
                chA = new char[l];
                Arrays.fill(chA, ch);
            }
            return new String(chA, 0, l) + s;
        }
        if (ch == ' ') {
            chA = pad_blank;
        } else if (ch == '0') {
            chA = pad_zero;
        } else {
            chA = new char[l];
            Arrays.fill(chA, ch);
        }
        return padLeft(new String(chA) + s, len, ch);
    }

    /**
     * Format an integer with a specified width. Pad on right side
     */
    public static String padRight(String s, int len, char ch) {
        int l = len - s.length();
        if (l <= 0) {
            return s;
        }

        char[] chA;
        if (l <= 30) {
            if (ch == ' ') {
                chA = pad_blank;
            } else if (ch == '0') {
                chA = pad_zero;
            } else {
                chA = new char[l];
                Arrays.fill(chA, ch);
            }
            return s + new String(chA, 0, l);
        }
        if (ch == ' ') {
            chA = pad_blank;
        } else if (ch == '0') {
            chA = pad_zero;
        } else {
            chA = new char[l];
            Arrays.fill(chA, ch);
        }
        return padRight(s + new String(chA), len, ch);
    }

// - - - - - - - - - - - - - - - - - - - - - - - - - - -
    /**
     * return time in milliseconds. Same as System.currentTimeMillis();
     */
    public static long ct() {
        return System.currentTimeMillis();
    }

// - - - - - - - - - - - - - - - - - - - - - - - - - - -
    /**
     * Split a String into an Array of Strings.
     *
     * @param str String to split
     * @param split which character to have as split point. Any character in split can be split charachter.
     * @return Array of String
     */
    public static String[] split(String str, String split) {
        StringTokenizer t = new StringTokenizer(str, split);

        int n = t.countTokens();
        int i = 0;
        String[] arr = new String[n];

        for (; t.hasMoreTokens();) {
            String word = t.nextToken();
            arr[i++] = word;
        }
        return arr;
    }

// - - - - - - - - - - - - - - - - - - - - - - - - - - -
    /**
     * Convert an array of object into a String representation. Delimiter is ','. Format each member with width w and pad with ' '.
     */
    public static String a2s(Object o, int w) {
        return arrToString(o, ",", w, ' ');
    }

    /**
     * Convert an array of object into a String representation. Delimiter is ','.
     */
    public static String a2s(Object o) {
        return arrToString(o, ",", 0, ' ');
    }

    /**
     * Convert an array of object into a String representation. Delimiter is delim.
     */
    public static String a2s(Object o, String delim) {
        return arrToString(o, delim, 0, ' ');
    }

    /**
     * Convert an array of object into a String representation. Delimiter is ','. Format each member with width 'w' and pad with 'pad'.
     */
    public static String a2s(Object o, String delim, int w, char pad) {
        return arrToString(o, delim, w, pad);
    }

    /**
     * Convert an array of object into a String representation. Delimiter is ','. Format each member with width w and pad with ' '.
     */
    public static String arrToString(Object o, int w) {
        return arrToString(o, ",", w, ' ');
    }

    /**
     * Convert an array of object into a String representation. Delimiter is ','.
     */
    public static String arrToString(Object o) {
        return arrToString(o, ",", 0, ' ');
    }

    /**
     * Convert an array of object into a String representation. Delimiter is delim.
     */
    public static String arrToString(Object o, String delim) {
        return arrToString(o, delim, 0, ' ');
    }

    /**
     * Convert an array of object into a String representation. Delimiter is ','. Format each member with width w and pad with ' '. Arrays of Array are handled
     * recursivly.
     * <p>
     * I.e. the array of 3 int arrays are returned like this:<br>
     * "[1,2,3],[4,5,6],[7,8,9]"
     * <p>
     * toString are used for printing Objects.
     */
    @SuppressWarnings("unchecked")
    public static String arrToString(Object o, String delim, int w, char pad) {
        if (o == null) {
            return "null";
        }
        StringBuffer s = new StringBuffer();
        Class cls = o.getClass();
        if (cls.isArray()) {
            Class clsc = cls.getComponentType();
            if (!clsc.isPrimitive()) {
                if (clsc.isArray()) {
                    Object[] oa = (Object[]) o;
                    for (int i = 0; i < oa.length; i++) {
                        s.append((i == 0 ? "" : delim)
                                + "[" + arrToString(oa[i]) + "]");
                    }
                    return s.toString();
                } else {
                    Object[] oa = (Object[]) o;
                    for (int i = 0; i < oa.length; i++) {
                        if (w == 0) {
                            s.append((i == 0 ? "" : delim) + oa[i]);
                        } else {
                            s.append((i == 0 ? "" : delim) + padLeft("" + oa[i], w, pad));
                        }
                    }
                    return s.toString();
                }
            }
            if (clsc.getName().equals("int")) {
                int[] ia = (int[]) o;
                for (int i = 0; i < ia.length; i++) {
                    if (w == 0) {
                        s.append((i == 0 ? "" : delim) + ia[i]);
                    } else {
                        s.append((i == 0 ? "" : delim) + pL(ia[i], w, pad));
                    }
                }
                return s.toString();
            }
            if (clsc.getName().equals("char")) {
                char[] ia = (char[]) o;
                for (int i = 0; i < ia.length; i++) {
                    if (ia[i] == 0) {
                        if (w == 0) {
                            s.append((i == 0 ? "" : delim) + "^@");
                        } else {
                            s.append((i == 0 ? "" : delim) + padLeft("^@", w, pad));
                        }
                    } else if (w == 0) {
                        s.append((i == 0 ? "" : delim) + ia[i]);
                    } else {
                        s.append((i == 0 ? "" : delim) + padLeft("" + ia[i], w, pad));
                    }
                }
                return s.toString();
            }
            if (clsc.getName().equals("byte")) {
                byte[] ia = (byte[]) o;
                for (int i = 0; i < ia.length; i++) {
                    if (w == 0) {
                        s.append((i == 0 ? "" : delim) + ia[i]);
                    } else {
                        s.append((i == 0 ? "" : delim) + padLeft("" + ia[i], w, pad));
                    }
                }
                return s.toString();
            }
            if (clsc.getName().equals("short")) {
                short[] ia = (short[]) o;
                for (int i = 0; i < ia.length; i++) {
                    if (w == 0) {
                        s.append((i == 0 ? "" : delim) + ia[i]);
                    } else {
                        s.append((i == 0 ? "" : delim) + padLeft("" + ia[i], w, pad));
                    }
                }
                return s.toString();
            }
            if (clsc.getName().equals("boolean")) {
                boolean[] ba = (boolean[]) o;
                for (int i = 0; i < ba.length; i++) {
                    if (w == 0) {
                        s.append((i == 0 ? "" : delim) + ba[i]);
                    } else {
                        s.append((i == 0 ? "" : delim) + padLeft("" + ba[i], w, pad));
                    }
                }
                return s.toString();
            }
        }
        return null;
    }

// - - - - - - - - - - - - - - - - - - - - - - - - - - -
    /**
     * Scan the argument for flags. A flag is prefixed with '-'.
     * <p>
     * Flag can have arguments. Save them all in a HashMap.
     * <p>
     * I.e.
     * <p>
     * -flag=value
     */
    public static HashMap<String, String> flagAsMap(String[] argv) {
        final List argl = Arrays.asList(argv);
        HashMap<String, String> flag = new HashMap<String, String>();

        for (int i = 0; i < argl.size(); i++) {
            @SuppressWarnings("unchecked")
            String s = (String) argl.get(i);
            if (s.startsWith("-") && !s.equals("-")) {
                String ss = s.substring(1);
                int ix = ss.indexOf('=');
                if (ix != -1) {
                    String sk = ss.substring(0, ix);
                    String sv = ss.substring(ix + 1);
                    if (sv.indexOf(',') == -1) {
                        flag.put(sk, sv);
                    } else {
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
     * Scan argument and put in List those that are not flags.
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
}
