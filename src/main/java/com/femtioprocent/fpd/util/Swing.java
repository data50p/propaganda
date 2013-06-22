package com.femtioprocent.fpd.util;

import java.awt.Color;
import java.lang.reflect.Method;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JFormattedTextField;
import javax.swing.text.DefaultFormatter;

/**
   Convenient functions for the effective programmer.
   <p>
   Many tedius task are handled here. This class is expected to be used everywhere.
 */
final public class Swing {

    /**
       Create a component.
     */
    @SuppressWarnings("unchecked")
    public static <JC extends javax.swing.JComponent> JC getJComponent(java.awt.Container container, Class<JC> clazz, String id)
    {
	for(java.awt.Component c : container.getComponents()) {
	    if ( c instanceof java.awt.Container ||
		 c instanceof javax.swing.JPanel ||
		 c instanceof javax.swing.JFrame ||
		 c instanceof javax.swing.JRootPane ||
		 c instanceof javax.swing.JLayeredPane ||
		 c instanceof javax.swing.JTabbedPane ) {
		JC jc = getJComponent((java.awt.Container)c, clazz, id);
		if ( jc != null )
		    return jc;
	    }
	    if ( c.getClass() == clazz ) {
		if ( id.equals(c.getName()) )
		    return (JC)c;
	    }
	}
	return null;
    }


    public static <T extends RegexPatternObject> DefaultFormatter createRegexpFormatter(final Class<T> clazz)
    {
	DefaultFormatter formatter = new DefaultFormatter() {
             private static final long serialVersionUID = 5867304093377030035L;

             @Override
             public String valueToString(Object obj) {
                 if (obj == null)
                     return "null";
                 return obj.toString();
             }

             @Override
             public Object stringToValue(String val) {
                 val = val.trim();
                 try {
                     JFormattedTextField ftf = getFormattedTextField();
                     Method m = clazz.getDeclaredMethod("getRegexPatternString");
                     String regexp = (String) m.invoke(null);
                     Pattern pattern = Pattern.compile(regexp);
                     Matcher matcher = pattern.matcher(val);

                     if (matcher.matches()) {
                         try {
                             String[] args = new String[matcher.groupCount()];

                             for (int i = 0; i < args.length; i++)
                                 args[i] = matcher.group(i + 1);
                             ftf.setForeground(Color.black);
                             try {
                                 T t = clazz.newInstance();

                                 // t.setValue(args);
                                 if (args.length == 0)
                                     t.setValue();
                                 if (args.length == 1)
                                     t.setValue(args[0]);
                                 if (args.length == 2)
                                     t.setValue(args[0], args[1]);
                                 if (args.length == 3)
                                     t.setValue(args[0], args[1], args[2]);
                                 if (args.length > 3)
                                     throw new Exception("To many arg " +
                                                         args.length);
                                 return t;
                             }
                             catch (Exception ex) {
                                 return val;
                             }
                         }
                         catch (NumberFormatException ex) {
                         }
                     }
                     ftf.setForeground(Color.red);
                     return val;
                 }
                 catch (Exception ex) {
                 }
                 return val;
             }
         };
	formatter.setValueClass(clazz);
	return formatter;
    }
}
