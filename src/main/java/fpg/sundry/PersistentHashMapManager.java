package fpg.sundry;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.prefs.Preferences;


public class PersistentHashMapManager {
    HashMap store;
    Class owner;
    Preferences prefs;

    public PersistentHashMapManager() {
	this(false);
    }

    public PersistentHashMapManager(boolean system) {
	this(PersistentHashMapManager.class, system);
    }

    public PersistentHashMapManager(Class owner) {
	this(owner, false);
    }

    public PersistentHashMapManager(Class owner, boolean system) {
	this.owner = owner;
	if ( system )
	    prefs = Preferences.systemNodeForPackage(owner.getClass());
	else
	    prefs = Preferences.userNodeForPackage(owner.getClass());
	load();
    }

    @SuppressWarnings("unchecked") void save() {
	try {
	    store.put("save-time", new Long(S.ct()));
	    ByteArrayOutputStream bo = new ByteArrayOutputStream();
	    ObjectOutputStream oo = new ObjectOutputStream(bo);
	    oo.writeObject(store);
	    oo.close();
	    byte[] ba = bo.toByteArray();
	    prefs.putByteArray("PersistentHashMapManager.store", ba);
	    //	    S.pe("saved " + S.a2s(ba));
	} catch (IOException ex) {
	    S.pL("Can't save " + ex);
	}
    }

    @SuppressWarnings("unchecked") void load() {
	//	S.pe("load");
	try {
	    byte[] ba = prefs.getByteArray("PersistentHashMapManager.store", null);
	    if ( ba != null ) {
		ByteArrayInputStream is = new ByteArrayInputStream(ba);
		ObjectInputStream oi = new ObjectInputStream(is);
		HashMap hm1 = (HashMap)oi.readObject();
		if ( hm1 != null )
		    store = hm1;
		store.put("load-time", new Long(S.ct()));
	    } else {
	    }
	} catch (ClassNotFoundException ex) {
	    S.pL("Can't load " + ex);
	} catch (IOException ex) {
	    S.pL("Can't load " + ex);
	}
	if ( store == null ) {
	    store = getDefaultHashMap();
	    save();
	}

	//	S.pe("loaded " + store);
    }

    public Class getOwner() {
	return owner;
    }

    @SuppressWarnings("unchecked")
    public HashMap getDefaultHashMap() {
	HashMap hm = new HashMap();
	hm.put("created-time", new Long(S.ct()));
	return hm;
    }

    @SuppressWarnings("unchecked")
    public void putHashMap(HashMap hm) {
	store.putAll(hm);
	save();
	//	S.pe("WMS new settings " + hm);
    }

    /**
       Get all settings value in a new HashMap.
       Also set the entry "get-time" to reflect current time.
     */
    @SuppressWarnings("unchecked")
    public HashMap getHashMap() {
	HashMap hm = new HashMap();
	hm.putAll(store);
	hm.put("get-time", new Long(S.ct()));
	return hm;
    }

    /**
       Get settings value, but only for those key found in argument HashMap hm.
       Also set the entry "get-time" to reflect current time.
     */
    @SuppressWarnings("unchecked")
    public HashMap getHashMap(HashMap hm) {
	Iterator it = hm.keySet().iterator();
	while ( it.hasNext()) {
	    String key = (String)it.next();
	    Object o = store.get(key);
	    hm.put(key, o);
	}
	hm.put("get-time", new Long(S.ct()));
	return hm;
    }

    @SuppressWarnings("unchecked")
    public static void main(String[] args) {
	PersistentHashMapManager phm = new PersistentHashMapManager();
	HashMap hm = phm.getHashMap();
	S.pL("" + hm);
	hm.put("test1", "value1_" + new Date());
	hm.put("test3", "value2_" + new Date());
	phm.putHashMap(hm);
	hm = phm.getHashMap();
	S.pL("" + hm);
    }
}
