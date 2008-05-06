package org.openxdm.xcap.common.xml;

import java.io.StringWriter;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class TextWriter {

	public static String toString(Node node) throws TransformerException {
		
		if (node == null) {
			throw new IllegalArgumentException("node can't be null.");
		}
		
		Source source = new DOMSource(node);			
		StringWriter stringWriter = new StringWriter();
		Result result = new StreamResult(stringWriter);
		TransformerFactory factory = TransformerFactory.newInstance();
		Transformer transformer = factory.newTransformer();
		if (node instanceof Element) {
			transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION,"yes");						
		}
		transformer.transform(source, result);
		return stringWriter.getBuffer().toString();        
    }
}
