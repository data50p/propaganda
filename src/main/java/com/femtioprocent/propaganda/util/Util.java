package com.femtioprocent.propaganda.util;

import java.io.UnsupportedEncodingException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Util {
    public static String toAscii(String s) {
        try {
            StringBuilder sb = new StringBuilder();
            for(byte b : s.getBytes("utf-8")) {
                sb.append(Integer.toHexString(0xff & b));                
            }
            return sb.toString();
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(Util.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
}
