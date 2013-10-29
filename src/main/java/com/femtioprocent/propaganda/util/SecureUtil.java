/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.femtioprocent.propaganda.util;

import java.util.HashMap;
import org.apache.commons.codec.digest.DigestUtils;

/**
 *
 * @author lars
 */
public class SecureUtil {

    private static HashMap<String, String> m = new HashMap<String, String>();

    private static String salt = "" + System.getProperty("os.name") + System.nanoTime() + System.getProperty("user.name");

    public static String getSecureId(String s, String seasalt) {
        String us = s + (seasalt == null || seasalt.length() == 0 ? "" : ":" + seasalt) + ":" + salt;
        String shaX = DigestUtils.shaHex(us);
//        MessageDigest md = null;
//            md = MessageDigest.getInstance("SHA-1");
//            byte[] d = md.digest(us.getBytes("utf-8"));
//            String ss = Util.toAscii(d);
//            System.err.println("SHA1: 2 " + ss.length() + ' ' + ss + ' ' + shaX);
        m.put(shaX, s);
        return shaX;
    }

    public static String lookupUnsecureId(String id) {
        return m.get(id);
    }
}
