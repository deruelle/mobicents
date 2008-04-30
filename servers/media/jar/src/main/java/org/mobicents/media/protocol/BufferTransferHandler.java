package org.mobicents.media.protocol;

/**
 * Standard JMF class -- see <a
 * href="http://java.sun.com/products/java-media/jmf/2.1.1/apidocs/javax/media/protocol/BufferTransferHandler.html"
 * target="_blank">this class in the JMF Javadoc</a>. Complete.
 * 
 * @author Ken Larson
 * 
 */
public interface BufferTransferHandler {
	public void transferData(PushBufferStream stream);
}
