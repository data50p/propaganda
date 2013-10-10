package com.femtioprocent.propaganda.util;

import java.io.UnsupportedEncodingException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Util {

    public static String toAscii(byte[] ba) {
        StringBuilder sb = new StringBuilder();
        for (byte b : ba) {
            sb.append(Integer.toHexString(0xff & b));
        }
        return sb.toString();
    }
}
