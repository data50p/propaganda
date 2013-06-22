package com.femtioprocent.fpd.util;

import java.beans.XMLDecoder;
import java.beans.XMLEncoder;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import com.femtioprocent.fpd.sundry.S;

/**
   Save an instance of an object and restore it later in another java VM invokation.
   Each object is saved in its own file.
 */
public class ObjectHibernator<T>
{
    String base_name = ".ObjectHibernator";
    /**
       The id for this saved object. Used to create part in a filename.
     */
    public String id;

    /**
       Create an hibernator with a specific id.
     */
    public ObjectHibernator(String id)
    {
	this.id = id;
    }

    /**
       Save supplied object in the file.
     */
    public void saveObject(T t)
    {
	try {
	    XMLEncoder e = new XMLEncoder(new BufferedOutputStream(new FileOutputStream(base_name + '-' + id + ".xml")));
	    e.writeObject(t);
	    e.close();
	} catch (IOException ex) {
	    S.pL("Can't XMLEncode " + ex);
	}
    }

    /**
       Restore an aobject from the file.
       @return the restored object or null
     */
    @SuppressWarnings("unchecked")
    public T restoreObject()
    {
	try {
	    XMLDecoder d = new XMLDecoder(new BufferedInputStream(new FileInputStream(base_name + '-' + id + ".xml")));
	    T readObject = (T)d.readObject();
	    T t = readObject;
	    d.close();
	    return t;
	} catch (IOException ex) {
	    S.pL("Can't XMLDecode " + ex);
	} catch (Exception ex) {
	    S.pL("Can't XMLDecode " + ex);
	}
	return null;
    }

}
