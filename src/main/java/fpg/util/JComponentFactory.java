package fpg.util;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.lang.reflect.Constructor;

import javax.swing.JComponent;
import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;
import javax.swing.JRootPane;
import javax.swing.JTabbedPane;

/**
   Create a JComponent of class JC
 */
public class JComponentFactory<JC extends JComponent>
{
    Class<JC> clazz;

    public JComponentFactory(Class<JC> clazz)
    {
	this.clazz = clazz;
    }

    public JComponentFactory()
    {
	this.clazz = null;
    }

    /**
       Return a component in the container with a name as the argument specifies.
       @param container where to look for components, look recursively if interior container is found
       @param name The name for the component to find
       @return The component or null
     */
    @SuppressWarnings("unchecked")
    public JC getJComponent(Container container, String name)
    {
	for(Component c : container.getComponents()) {
	    if ( c instanceof Container ||
		 c instanceof JPanel ||
		 c instanceof JFrame ||
		 c instanceof JRootPane ||
		 c instanceof JLayeredPane ||
		 c instanceof JTabbedPane ) {
		JC jc = getJComponent((Container) c, name);
		if ( jc != null )
		    return jc;
	    }
	    if ( clazz == null || c.getClass() == clazz ) {
		if ( name.equals(c.getName()) ) {
		    return (JC)c;
		}
	    }
	}
	return null;
    }

    /**
       Return a component in the container with a name as the argument specifies.
       @param container where to look for components, look recursively if interior container is found
       @param name The name for the component to find
       @param clazz The class type for the component to find or null to find any JComponent
       @return The component or null
     */
    @SuppressWarnings("unchecked")
    public static <JC extends JComponent> JC getJComponent(Container container, String name, Class<JC> clazz)
    {
	for(Component c : container.getComponents()) {
	    if ( c instanceof Container ||
		 c instanceof JPanel ||
		 c instanceof JFrame ||
		 c instanceof JRootPane ||
		 c instanceof JLayeredPane ||
		 c instanceof JTabbedPane ) {
		JC jc = getJComponent((Container) c, name, clazz);
		if ( jc != null )
		    return jc;
	    }
	    if ( clazz == null || c.getClass() == clazz ) {
		if ( name.equals(c.getName()) ) {
		    return (JC)c;
		}
	    }
	}
	return null;
    }

    /**
       Create a instance. The created object is of type clazz as specified in the JComponentFactory.
       @param name The name of the created component
       @param args if Dimension - the object size; if JFormattedTextField.AbstractFormatter - a formatter for input (JFormattedTextField)
     */
    @SuppressWarnings("unchecked")
    public JC create(String name, Object... args)
    {

	try {
	    JFormattedTextField.AbstractFormatter formatter = null;
	    Class<JC> clazz2 = clazz;

	    for(Object o : args) {
		if ( o instanceof JFormattedTextField.AbstractFormatter )
		    formatter = (JFormattedTextField.AbstractFormatter)o;
		if ( o instanceof Class )
		    clazz2 = (Class<JC>)o;
	    }
	    if ( clazz2 == null )
		return null;

	    JC jc;
	    if ( formatter == null )
		jc = clazz2.newInstance();
	    else {
		Constructor<JC> constr = clazz2.getConstructor(JFormattedTextField.AbstractFormatter.class);
		jc = constr.newInstance(formatter);
	    }

	    jc.setName(name);

	    Dimension dim = null;
	    for(Object o : args) {
		if ( o instanceof Dimension )
		    dim = (Dimension)o;
	    }
	    if ( dim != null )
		jc.setPreferredSize(dim);

	    return jc;
	}
	catch (Exception ex) {
	}
	return null;
    }
}
