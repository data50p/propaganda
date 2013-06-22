package com.femtioprocent.fpd.sundry;

import java.io.*;
import java.util.*;

class crlf {
    public static void main(String[] args) {
	HashMap<String, String> flags = S.flagAsMap(args);

	try {
	    BufferedReader br = null;
	    String fn = flags.get("in");
	    if ( fn == null )
		br = new BufferedReader(new FileReader(args[0]));
	    else if ( fn.equals("-") )
		br = new BufferedReader(new InputStreamReader(System.in));
	    else
		br = new BufferedReader(new FileReader(fn));
	    for(;;) {
		String s = br.readLine();
		if ( s == null )
		    break;
		S.p_(s + "\r\n");
	    }
	} catch (IOException ex) {
	    S.pL("! " + ex);
	}

	S.flush();
    }
}
