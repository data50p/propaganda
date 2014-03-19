package com.femtioprocent.fpd.sundry;

import java.io.*;
import java.util.*;
import java.util.prefs.*;

public class PreferenceUtil {

    Class owner;
    String ownername;

    public PreferenceUtil(Class owner) {
        this.owner = owner;
        String s = owner.getName();
        this.ownername = s.substring(s.lastIndexOf('.') + 1);
    }

    /**
     * Save an Object to the user preference area. Use the supplied id.
     */
    synchronized public void save(String subid, Object object) {
        Preferences prefs = Preferences.userNodeForPackage(owner);
        try {
            ByteArrayOutputStream bo = new ByteArrayOutputStream();
            ObjectOutputStream oo = new ObjectOutputStream(bo);
            oo.writeObject(object);
            oo.close();
            byte[] ba = bo.toByteArray();
            byte[][] baa = split(ba, 1000);
            for (int i = 0; i < baa.length; i++) {
                prefs.putByteArray(ownername + ":" + subid + '-' + i, baa[i]);
            }
            prefs.putInt(ownername + ":" + subid + "-size", baa.length);
        } catch (IOException ex) {
            S.pL("Can't write object: " + object + ' ' + ex);
        }
    }

    private byte[][] split(byte[] ba, int size) {
        int l = ba.length / size + (ba.length % size == 0 ? 0 : 1);
        byte baa[][] = new byte[l][];
        return split(baa, ba, size, 0);
    }

    private byte[][] split(byte[][] baa, byte[] ba, int size, int n) {
        int offs = n * size;
        int rest = ba.length - offs;
        int l = rest > size ? size : rest;
        byte[] nba = new byte[l];
        System.arraycopy(ba, offs, nba, 0, l);
        baa[n] = nba;
        if (rest > size) {
            split(baa, ba, size, n + 1);
        }
        return baa;
    }

    /**
     * Load a saved Object from the user preference area. Use the id to get it. Return the default value if nothing found.
     */
    synchronized public Object load(String subid, Object def) {
        Preferences prefs = Preferences.userNodeForPackage(owner);
        try {
            int size = prefs.getInt(ownername + ":" + subid + "-size", 100);
            byte[] ba = load(prefs, ownername + ":" + subid, new ByteArrayOutputStream(), 0, size);
            if (ba != null) {
                ByteArrayInputStream is = new ByteArrayInputStream(ba);
                ObjectInputStream oi = new ObjectInputStream(is);
                Object object = oi.readObject();
                return object;
            } else {
            }
        } catch (ClassNotFoundException ex) {
        } catch (IOException ex) {
        }
        return def;
    }

    synchronized private byte[] load(Preferences prefs, String key, ByteArrayOutputStream bao, int n, int max) {
        byte[] ba = prefs.getByteArray(key + '-' + n, null);
        if (ba == null) {
            return bao.toByteArray();
        }
        bao.write(ba, 0, ba.length);
        n++;
        if (n == max) {
            return bao.toByteArray();
        }
        return load(prefs, key, bao, n, max);
    }

    synchronized public Object getObject(String sub_id, Object def) {
        Object ret = load(sub_id, null);
        if (ret == null) {
            save(sub_id, def);
            return def;
        }
        return ret;
    }

    @SuppressWarnings("unchecked")
    static public void main(String[] args) {
        PreferenceUtil pu = new PreferenceUtil(PreferenceUtil.class);
        HashMap hm = (HashMap) pu.getObject("test_obj", new HashMap());
        S.pL("get " + hm);
        hm.put(args[0], args[1]);
        pu.save("test_obj", hm);
        S.pL("saved " + hm);
    }
}
