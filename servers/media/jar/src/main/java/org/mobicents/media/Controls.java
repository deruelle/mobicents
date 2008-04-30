package org.mobicents.media;

/**
 * 
 * Standard JMF class -- see <a href="http://java.sun.com/products/java-media/jmf/2.1.1/apidocs/javax/media/Controls.html" target="_blank">this class in the JMF Javadoc</a>.
 * Complete.
 * @author Ken Larson
 *
 */
public interface Controls {
	Object getControl(String controlType);

	Object[] getControls();
}
