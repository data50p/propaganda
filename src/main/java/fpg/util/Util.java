//
//	$Id: S.java,v 1.3 2000/02/22 09:48:40 lars Exp $
//

package fpg.util;

import fpg.sundry.S;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

/**
   Convenient functions for the effective programmer.
   <p>
   Many tedius task are handled here. This class is expected to be used everywhere.
 */
@SuppressWarnings("unchecked")
final public class Util {

    public static <T> Class<T> classForName(String name)
    {
	try {
	    Class clazz = Class.forName(name);
	    return clazz;
	}
	catch (ClassNotFoundException ex) {
	    S.pL("" + ex);
	}
	return null;
    }

    public static <T> Class<T> classForName(String name, ClassLoader cl)
    {
	try {
	    Class clazz = Class.forName(name, true, cl);
	    return clazz;
	}
	catch (ClassNotFoundException ex) {
	    S.pL("" + ex);
	}
	return null;
    }

    public static <T> T createForName(String name, ClassLoader cl)
    {
	try {
	    Class<T> clazz = classForName(name, cl);
	    if ( clazz == null )
		return null;

	    T t = clazz.newInstance();
	    return t;
	}
	catch (IllegalAccessException ex) {
	    S.pL("" + ex);
	}
	catch (InstantiationException ex) {
	    S.pL("" + ex);
	}
	return null;
    }
    
    public static <T> T createForName(String name)
    {
	try {
	    Class<T> clazz = classForName(name);
	    if ( clazz == null )
		return null;

	    T t = clazz.newInstance();
	    return t;
	}
	catch (IllegalAccessException ex) {
	    S.pL("" + ex);
	}
	catch (InstantiationException ex) {
	    S.pL("" + ex);
	}
	return null;
    }

    public static <T> T createForName(String name, Class[] clazzA, Object... arg)
    {
	try {
	    Class<T> clazz = classForName(name);
	    if ( clazz == null )
		return null;

	    Constructor<T> constructor = clazz.getConstructor(clazzA);
	    T t = constructor.newInstance(arg);
	    return t;
	}
	catch (NoSuchMethodException ex) {
	    S.pL("" + ex);
	}
	catch (InvocationTargetException ex) {
	    S.pL("" + ex);
	}
	catch (IllegalAccessException ex) {
	    S.pL("" + ex);
	}
	catch (InstantiationException ex) {
	    S.pL("" + ex);
	}
	return null;
    }


    public static void main(String args[]) {
	Class<String> cc = classForName("java.lang.String");
	S.pL("" + cc);

	String s = createForName("java.lang.String");
	S.pL("" + s);

	Object oo = createForName("javax.swing.JLabel", new Class[] {String.class}, "my text");
	S.pL("" + oo);
    }
}
