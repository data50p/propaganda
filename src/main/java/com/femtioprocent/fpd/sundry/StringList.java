package com.femtioprocent.fpd.sundry;

import java.util.*;

@SuppressWarnings("unchecked")
public class StringList {
	LinkedList li;

	public StringList() {
		li = new LinkedList();
	}

	public void append(String s1) {
		li.addLast(s1);
	}

	public void append(String s1, String s2) {
		li.addFirst(s1);
		li.addLast(s2);
	}

	public void append(String s1, String s2, String s3) {
		li.addFirst(s1);
		li.addLast(s2);
		li.addLast(s3);
	}

	@Override
         public String toString() {
		StringBuffer sb = new StringBuffer();

		Iterator it = li.iterator();
		while (it.hasNext()) {
			String s = (String)it.next();
			sb.append(s);
		}
		return sb.toString();
	}

	public static void main(String[] args) {
		StringList sl = new StringList();

		sl.append("ett");
		sl.append("noll", "två");
		sl.append("-1", "tre", "fyra");

		S.pL("" + sl.toString());
	}
}
