/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.femtioprocent.propaganda.util;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author lars
 */
public class SecureUtil {

    private static HashMap<String, String> m = new HashMap<String, String>();
    
    private static String salt = "" + System.getProperty("os.name") + System.nanoTime() + System.getProperty("user.name");

    public static String getSecureId(String s) {
        MessageDigest md = null;
        try {
            String us = s + ":" + salt;
            md = MessageDigest.getInstance("SHA-1");
            String ss = new String(md.digest(us.getBytes("utf-8")));
            ss = Util.toAscii(ss);
            m.put(ss, s);
            return ss;
        } catch (NoSuchAlgorithmException e) {
            return null;
        } catch (UnsupportedEncodingException ex) {
            return null;
        }
    }

    public static String lookupUnsecureId(String id) {
        return m.get(id);
    }
}
