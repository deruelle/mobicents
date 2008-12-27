package org.openxdm.xcap.server.slee.appusage.rlsservices;

import java.net.URI;
import java.util.HashSet;
import java.util.Set;

import javax.xml.transform.TransformerException;
import javax.xml.validation.Validator;

import org.apache.log4j.Logger;
import org.openxdm.xcap.common.appusage.AppUsage;
import org.openxdm.xcap.common.datasource.DataSource;
import org.openxdm.xcap.common.error.ConstraintFailureConflictException;
import org.openxdm.xcap.common.error.InternalServerErrorException;
import org.openxdm.xcap.common.error.SchemaValidationErrorConflictException;
import org.openxdm.xcap.common.error.UniquenessFailureConflictException;
import org.openxdm.xcap.common.uri.DocumentSelector;
import org.openxdm.xcap.common.xml.TextWriter;
import org.openxdm.xcap.server.etag.ETagGenerator;
import org.openxdm.xcap.server.slee.appusage.resourcelists.ResourceListsAppUsage;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class RLSServicesAppUsage extends AppUsage {

	public static final String ID = "rls-services";
	public static final String DEFAULT_DOC_NAMESPACE = "urn:ietf:params:xml:ns:rls-services";
	public static final String MIMETYPE = "application/rls-services+xml";
	
	private static Logger logger = Logger.getLogger(RLSServicesAppUsage.class);
	
	public RLSServicesAppUsage(Validator schemaValidator) {
		super(ID,DEFAULT_DOC_NAMESPACE,MIMETYPE,schemaValidator,new RLSServicesAuthorizationPolicy());
	}

	/**
	 * (re)builds the global document on delete
	 */
	public void processResourceInterdependenciesOnDelete(Document document,
			DocumentSelector documentSelector, DataSource dataSource)
			throws SchemaValidationErrorConflictException,
			UniquenessFailureConflictException, InternalServerErrorException,
			ConstraintFailureConflictException {
		
		super.processResourceInterdependenciesOnDelete(document, documentSelector,
				dataSource);
		
		// declare uri set, to keep track of uniqueness
		// initialize service uri attrib set
		Set<String> serviceUriSet = new HashSet<String>();
		
		// is this a put of an user document?
		int separator = documentSelector.getDocumentParent().indexOf('/');
		if (separator > 0 && documentSelector.getDocumentParent().substring(0,separator).equals("users")) {
		
			// get global index doc
			DocumentSelector globalDocumentSelector = new DocumentSelector(ID,"global","index");
			org.openxdm.xcap.common.datasource.Document dataSourceGlobalDocument = dataSource.getDocument(globalDocumentSelector);

			if (dataSourceGlobalDocument != null) {
				
				// let's gather all uris of service elements to delete from the global doc
				NodeList documentChildNodes = document.getDocumentElement().getChildNodes();
				for(int i=0;i<documentChildNodes.getLength();i++) {
					Node documentChildNode = documentChildNodes.item(i);			
					if (documentChildNode.getNodeType() == Node.ELEMENT_NODE && documentChildNode.getLocalName().equals("service")) {
						// assume doc is in good shape
						serviceUriSet.add(((Element) documentChildNode).getAttributeNode("uri").getNodeValue());																		
					}
				}
			
				Document globalDocument = dataSourceGlobalDocument.getAsDOMDocument();									
				if (logger.isDebugEnabled()) {
					try {
						logger.debug("Global doc before deleting:\n "+TextWriter.toString(globalDocument));
					} catch (TransformerException e1) {
						logger.error(e1);
					}
				}
				Node rlsServices = globalDocument.getDocumentElement();
				Node rlsServicesChild = rlsServices.getFirstChild();
				Node nextChild = null;
				do {
					nextChild = rlsServicesChild.getNextSibling();
					if (rlsServicesChild.getNodeType() == Node.ELEMENT_NODE && rlsServicesChild.getLocalName().equals("service")) {	
						if (logger.isDebugEnabled()) {
							logger.debug("Deleting service with uri "+((Element) rlsServicesChild).getAttributeNode("uri").getNodeValue());
						}
						if (serviceUriSet.contains(((Element) rlsServicesChild).getAttributeNode("uri").getNodeValue())) {
							// remove the node
							rlsServices.removeChild(rlsServicesChild);
						}						
					}
					rlsServicesChild = nextChild;
				}
				while(nextChild != null);
				
				try {
					dataSource.updateDocument(globalDocumentSelector, dataSourceGlobalDocument.getETag(),ETagGenerator.generate("/rls-services/global/index"), TextWriter.toString(globalDocument),globalDocument);
				} catch (TransformerException e) {
					// ignore, won't happen
					e.printStackTrace();
				}
				if (logger.isDebugEnabled()) {
					try {
						logger.debug("Global doc after deleting:\n "+TextWriter.toString(globalDocument));
					} catch (TransformerException e1) {						
						logger.error(e1);
					}
				}
			}						
		}							
	}
	
	/**
	 * (re)builds the global document on put
	 */
	public void processResourceInterdependenciesOnPut(Document document, DocumentSelector documentSelector, DataSource dataSource) throws SchemaValidationErrorConflictException, UniquenessFailureConflictException, InternalServerErrorException, ConstraintFailureConflictException {
		
		super.processResourceInterdependenciesOnPut(document, documentSelector, dataSource);
		
		/*		
		   This application usage defines an additional resource interdependence
		   between a single document in the global tree and all documents in the
		   user tree with the name "index".  This global document is formed as
		   the union of all of the index documents for all users within the same
		   XCAP root.  In this case, the union operation implies that each
		   <service> element in a user document will also be present as a
		   <service> element in the global document.  The inverse is true as
		   well.  Every <service> element in the global document exists within a
		   user document within the same XCAP root.
		
		   As an example, consider the RLS services document for user
		   sip:joe@example.com:
		
		   <?xml version="1.0" encoding="UTF-8"?>
		   <rls-services>
		    <service uri="sip:mybuddies@example.com">
		     <resource-list>http://xcap.example.com/resource-lists/users/si
		      p:joe@example.com/index/~~/resource-lists/list%5b@name=%22l1%
		      22%5d</resource-list>
		     <packages>
		      <package>presence</package>
		     </packages>
		    </service>
		   </rls-services>
		
		   And consider the RLS services document for user bob:
		
		   <?xml version="1.0" encoding="UTF-8"?>
		   <rls-services>
		    <service uri="sip:marketing@example.com">
		      <list name="marketing">
		        <rl:entry uri="sip:joe@example.com"/>
		        <rl:entry uri="sip:sudhir@example.com"/>
		      </list>
		      <packages>
		        <package>presence</package>
		      </packages>
		    </service>
		   </rls-services>
		
		   The global document at
		   http://xcap.example.com/rls-services/global/index would look like
		   this:
		
		   <?xml version="1.0" encoding="UTF-8"?>
		   <rls-services xmlns="urn:ietf:params:xml:ns:rls-services"
		      xmlns:rl="urn:ietf:params:xml:ns:resource-lists"
		      xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">
		    <service uri="sip:mybuddies@example.com">
		     <resource-list>http://xcap.example.com/resource-lists/user
		      s/sip:joe@example.com/index/~~/resource-lists/list%5b@nam
		      e=%22l1%22%5d</resource-list>
		     <packages>
		      <package>presence</package>
		     </packages>
		    </service>
		    <service uri="sip:marketing@example.com">
		      <list name="marketing">
		        <rl:entry uri="sip:joe@example.com"/>
		        <rl:entry uri="sip:sudhir@example.com"/>
		      </list>
		      <packages>
		        <package>presence</package>
		      </packages>
		    </service>
		   </rls-services>
		 */
				
		// declare uri set, to keep track of uniqueness
		// initialize service uri attrib set
		Set<String> serviceUriSet = new HashSet<String>();
		
		// split document parent
		String[] documentParentParts = documentSelector.getDocumentParent().split("/");
		
		// is this a put of an user document?
		if (documentParentParts[0].equals("users")) {
			
			// get document's element childs
			NodeList documentChildNodes = document.getDocumentElement().getChildNodes();
			// get global index doc
			DocumentSelector globalDocumentSelector = new DocumentSelector(ID,"global","index");
			org.openxdm.xcap.common.datasource.Document dataSourceGlobalDocument = dataSource.getDocument(globalDocumentSelector);
			
			if (dataSourceGlobalDocument == null) {
				// this is the first document being inserted, just verify service elements uris are unique
				for(int d=0;d<documentChildNodes.getLength();d++) {
					Node documentChildNode = documentChildNodes.item(d);			
					if (documentChildNode.getNodeType() == Node.ELEMENT_NODE && documentChildNode.getLocalName().equals("service")) {
						Element element = (Element) documentChildNode;
						// service element
						Attr serviceUriAttr = element.getAttributeNode("uri");
						// attr must be unique
						if (serviceUriSet.contains(serviceUriAttr.getNodeValue())) {
							// not unique, raise exception
							throw new UniquenessFailureConflictException();
						}
						else {						
							// unique so far					
							// add it to the service uri set
							serviceUriSet.add(serviceUriAttr.getNodeValue());							
						}					
					}
				}
				// document verified, let's define this one as the global one
				try {
					dataSource.addCollection(ID, globalDocumentSelector.getDocumentParent());
					dataSource.createDocument(globalDocumentSelector, ETagGenerator.generate("/rls-services/global/index"), TextWriter.toString(document), document);
				} catch (TransformerException e) {
					// ignore, won't happen, otherwise xdm had returned error to client
					e.printStackTrace();
				}
			}
			
			else {
				Document globalDocument = dataSourceGlobalDocument.getAsDOMDocument();
				if (logger.isDebugEnabled()) {
					try {
						logger.debug("Global doc before inserting:\n "+TextWriter.toString(globalDocument));
					} catch (TransformerException e1) {						
						logger.error(e1);
					}
				}
				// lets process global doc and gather all service uris there									
				NodeList globalChildNodes = globalDocument.getDocumentElement().getChildNodes();				
				for(int i=0;i<globalChildNodes.getLength();i++) {
					// assume global doc is in good shape for faster processing
					Node globalChildNode = globalChildNodes.item(i);
					if (globalChildNode.getNodeType() == Node.ELEMENT_NODE && globalChildNode.getLocalName().equals("service")) {
						serviceUriSet.add(((Element)globalChildNodes.item(i)).getAttributeNode("uri").getNodeValue());
					}
				}
				// this may be an update, if so we need to remove the old services before checking for uniqueness
				org.openxdm.xcap.common.datasource.Document oldDatasourceDocument = dataSource.getDocument(documentSelector);
				if (oldDatasourceDocument != null) {
					NodeList oldDocumentChildNodes = oldDatasourceDocument.getAsDOMDocument().getDocumentElement().getChildNodes();				
					for(int i=0;i<oldDocumentChildNodes.getLength();i++) {
						Node oldDocumentChildNode = oldDocumentChildNodes.item(i);
						if (oldDocumentChildNode.getNodeType() == Node.ELEMENT_NODE && oldDocumentChildNode.getLocalName().equals("service")) {
							serviceUriSet.remove(((Element)oldDocumentChildNodes.item(i)).getAttributeNode("uri").getNodeValue());
						}
					}
				}
				
				// now we process the document being inserted
				for(int d=0;d<documentChildNodes.getLength();d++) {
					Node documentChildNode = documentChildNodes.item(d);	
					if (documentChildNode.getNodeType() == Node.ELEMENT_NODE && documentChildNode.getLocalName().equals("service")) {
						// service element
						Element documentElement = (Element) documentChildNode;
						Attr serviceUriAttr = documentElement.getAttributeNode("uri");
						// attr must be unique
						if (serviceUriSet.contains(serviceUriAttr.getNodeValue())) {
							// not unique, raise exception
							throw new UniquenessFailureConflictException();
						}
						else {
							// add it to the service uri set
							serviceUriSet.add(serviceUriAttr.getNodeValue());
							// add service to the global one
							Node importedNode = globalDocument.importNode(documentElement,true);
							Node insertedNode = globalDocument.getDocumentElement().insertBefore(importedNode,null);							
						}						
					}
				}
				try {
					String xml = TextWriter.toString(globalDocument);
					if (logger.isDebugEnabled()) {
						logger.debug("Global doc being inserting:\n "+xml);						
					}
					dataSource.updateDocument(globalDocumentSelector, dataSourceGlobalDocument.getETag(), ETagGenerator.generate("/rls-services/global/index"), xml,globalDocument);
				} catch (TransformerException e) {
					// ignore, won't happen, otherwise xdm had returned error to client
					e.printStackTrace();
				}
			}						
		}							
	}
			
	public void checkConstraintsOnPut(Document document, String xcapRoot, DocumentSelector documentSelector, DataSource dataSource) throws UniquenessFailureConflictException, InternalServerErrorException, ConstraintFailureConflictException {
		
		super.checkConstraintsOnPut(document, xcapRoot, documentSelector, dataSource);	
		
		/*
		 	NOTE: the contraint below is ensured when (re)building the global doc
		 	
		    "The URI in the "uri" attribute of the <service> element MUST be
      		unique amongst all other URIs in "uri" elements in any <service>
      		element in any document on a particular server.  This uniqueness
      		constraint spans across XCAP roots."
      	*/	
		
        /*  
            TODO ensure the uri is not a network resource, such as the uri of a sip user
            
            "Furthermore, the URI MUST NOT correspond to an existing resource
            within the domain of the URI.
      		If a server is asked to set the URI to something that already
      		exists, the server MUST reject the request with a 409, and use the
      		mechanisms defined in [10] to suggest alternate URIs that have not
      		yet been allocated."		
		 */
					
		// get document's element childs
		NodeList childNodes = document.getDocumentElement().getChildNodes();
		// process each one
		for(int i=0;i<childNodes.getLength();i++) {
			Node childNode = childNodes.item(i);			
			if (childNode.getNodeType() == Node.ELEMENT_NODE && childNode.getLocalName().equals("service")) {
				// service element
				// get childs
				NodeList serviceChildNodes = childNode.getChildNodes();
				// process each one
				for(int j=0;j<serviceChildNodes.getLength();j++) {
					Node serviceChildNode = serviceChildNodes.item(j);
					if (serviceChildNode.getNodeType() == Node.ELEMENT_NODE && serviceChildNode.getLocalName().equals("list")) {
						// list element
						/*
							 o  In addition, an RLS services document can contain a <list>
							 element, which in turn can contain <entry>, <entry-ref> and
							 <external> elements.  The constraints defined for these elements
							 in Section 3.4.7 MUST be enforced.				 
						 */		
						ResourceListsAppUsage.checkNodeResourceListConstraints(serviceChildNode);
					}			 
					else if (serviceChildNode.getNodeType() == Node.ELEMENT_NODE && serviceChildNode.getLocalName().equals("resource-list")) {
						// resource-list element

						// flag setup
						boolean throwException = true;
						// node value is the uri to evaluate
						String resourceListUri = serviceChildNode.getTextContent();

						try {																	
							// build uri
							URI uri = new URI(resourceListUri);
							String uriScheme = uri.getScheme();
							/*
								 The URI in a <resource-list> element MUST be an absolute URI.
							 */
							if(uriScheme != null && (uriScheme.equalsIgnoreCase("http") || uriScheme.equalsIgnoreCase("https"))) {
								// split string after "scheme://" to find path segments
								String[] resourceListUriPaths = resourceListUri.substring(uriScheme.length()+3).split("/");									
								for(int k=0;k<resourceListUriPaths.length;k++) {
									/*
										  The server MUST verify that the URI
										  path contains "resource-lists" in the
										  path segment corresponding to the
										  AUID.
									 */
									if (resourceListUriPaths[k].equals(ResourceListsAppUsage.ID)) {
										// found auid
										if (!resourceListUriPaths[k+1].equals("global")) {
											// not global
											/*
  													If the RLS services
					      							document is within the XCAP user tree (as opposed to the global
					      							tree), the server MUST verify that the XUI in the path is the same
					      							as the XUI in the URI of to the resource-list document.
											 */
											if (resourceListUriPaths[k+2].equals(documentSelector.getDocumentParent().split("/")[1])) {
												throwException = false;
												break;
											}
										}
										else {
											throwException = false;
											break;
										}
									}
								}									
							}								
						}
						catch (Exception e) {
							// ignore
							e.printStackTrace();
						}							
						// throw exception if needed
						if (throwException) {
							throw new ConstraintFailureConflictException("Bad URI in resource-list element >> "+resourceListUri);
						}
					}												
				}
			}
		}						
	}
}
	
