package com.femtioprocent.fpd.sundry;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Base class for applications. Decode the argument and put all flags in a Map and the rest arguments in a list.
 */
public class Appl {

    public static HashMap<String, String> flags = new HashMap<String, String>();
    public static List argl = new ArrayList();
    public static String[][] help;

    public static void decodeArgs(String[] args, boolean dump, String[][] help) {
        flags = S.flagAsMap(args);
        argl = S.argAsList(args);
        if (dump) {
            S.pL("" + flags + ' ' + argl);
        }

        if (flags.get("help") != null && help != null) {
            int max = 0;
            for (int i = 0; i < help.length; i++) {
                if (help[i][0].length() > max) {
                    max = help[i][0].length() + help[i][1].length() + 1;
                }
            }

            for (int i = 0; i < help.length; i++) {
                String h = help[i][0];
                String a = help[i][1];
                String hs;
                if (a == null || a.length() == 0) {
                    hs = S.padRight(h, max, ' ');
                } else {
                    hs = S.padRight(h + '=' + a, max, ' ');
                }
                S.pL(hs + ' ' + help[i][2]);
            }
            System.exit(0);
        }
    }

    public static void decodeArgs(String[] args) {
        decodeArgs(args, false, null);
    }

    public static void decodeArgs(String[] args, boolean dump) {
        decodeArgs(args, dump, null);
    }

    public void main() {
    }

    public static void main(Appl appl) {
        appl.main();
    }

    public static void main(String[] args) {
        decodeArgs(args, true, new String[][]{
            new String[]{"help", "", "Show this help text"}
        });
        main(new Appl());
    }
}
