package org.openxdm.xcap.common.xml;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.dom.DOMSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class SchemaContext {

	/**
	 * Map with schemas as DOM documents, indexed by schema's target namespace
	 */
	private Map<String,Document> documentMap = new HashMap<String,Document>();
	private SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
	
	/**
	 * Retreives an instance from all schema files in a dir. The schema files must have the xsd file extension 
	 * @return
	 * @throws IOException 
	 * @throws SAXException 
	 * @throws ParserConfigurationException 
	 */
	public static SchemaContext fromDir(URI dirURI) throws SAXException, IOException, ParserConfigurationException {
		// init dom resources
		List<Document> schemaDocuments = new ArrayList<Document>(); 
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance(); 
		dbf.setNamespaceAware(true); 				
		DocumentBuilder documentBuilder = dbf.newDocumentBuilder();
		// read files and parse to dom resources				
		File schemaDir = new File(dirURI);
		if(schemaDir.isDirectory()) {				
			// create filter to select only files with name that ends with .xsd 
			FileFilter fileFilter = new FileFilter() {					
				public boolean accept(File f) {
					if(f.isDirectory()) {
						return false;
					}
					else {
						if(f.getName().endsWith(".xsd")){
							return true;
						}
						else {
							return false;
						}
					}
				}									
			};
			// get schema files from schema dir 
			File[] schemaFiles = schemaDir.listFiles(fileFilter);
			for(File schemaFile : schemaFiles) {
				// parse each one to dom document and add to schema docs list
				schemaDocuments.add(documentBuilder.parse(schemaFile));				
			}
		}			
		// create and return new schema context
		return new SchemaContext(schemaDocuments);
	}
	
	/**
	 * Creates a new instance to provide schemas that can combine the ones specified as DOM documents.
	 * @param documents the list os schemas that can be combined by the provider
	 */
	public SchemaContext(List<Document> documents) {		
		for(Iterator<Document> i=documents.iterator();i.hasNext();) {
			Document document = i.next();			
			String targetNamespace = (document.getDocumentElement()).getAttribute("targetNamespace");
			if (targetNamespace != null) {
				documentMap.put(targetNamespace,document);
			}
		}		
	}
	
	public Schema getCombinedSchema(String rootTargetNamespace) {
		
		// create temp list that will hold all schema docs sources to combine
		LinkedList<DOMSource> sourcesToCombine = new LinkedList<DOMSource>();
		// create temp list that will hold all schema docs to process
		LinkedList<String> documentsToProcessByTargetNamepsace = new LinkedList<String>();
		// get root document and kick off the process to find other needed schemas		
		documentsToProcessByTargetNamepsace.addLast(rootTargetNamespace);
		// add others by looking at each document for others needed to import		
		while(!documentsToProcessByTargetNamepsace.isEmpty()) {
			// get head target namespace
			String targetNamespace = documentsToProcessByTargetNamepsace.removeFirst();
			// get the related schema document
			Document document = documentMap.get(targetNamespace);
			if(document != null) {
				// document exists, add source to combination list
				sourcesToCombine.addFirst(new DOMSource(document));
				// and find other docs to import
				NodeList nl = document.getElementsByTagNameNS("http://www.w3.org/2001/XMLSchema","import");
				for (int i=0;i<nl.getLength();i++){
					Element elem = (Element)nl.item(i);
					// found one, add target namespace to process list
					documentsToProcessByTargetNamepsace.addLast(elem.getAttribute("namespace"));
				}
			}
			else {
				// document needed is not here, abort
				return null;
			}
		}
		// create a schema by combining all selected
		try {
			return factory.newSchema(sourcesToCombine.toArray(new DOMSource[sourcesToCombine.size()]));
		}
		catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
}
