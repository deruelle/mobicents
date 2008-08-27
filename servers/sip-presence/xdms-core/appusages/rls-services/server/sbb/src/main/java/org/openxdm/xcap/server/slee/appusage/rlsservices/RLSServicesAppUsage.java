package org.openxdm.xcap.server.slee.appusage.rlsservices;

import java.net.URI;
import java.util.HashSet;
import java.util.Set;

import javax.xml.validation.Validator;


import org.openxdm.xcap.common.appusage.AppUsage;
import org.openxdm.xcap.common.datasource.DataSource;
import org.openxdm.xcap.common.error.ConstraintFailureConflictException;
import org.openxdm.xcap.common.error.InternalServerErrorException;
import org.openxdm.xcap.common.error.SchemaValidationErrorConflictException;
import org.openxdm.xcap.common.error.UniquenessFailureConflictException;
import org.openxdm.xcap.common.uri.DocumentSelector;
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
	
	public RLSServicesAppUsage(Validator schemaValidator) {
		super(ID,DEFAULT_DOC_NAMESPACE,MIMETYPE,schemaValidator,new RLSServicesAuthorizationPolicy());
	}

	// TODO or redo, since the datasource interface changed
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
		 
		 As an example, consider the RLS services document for user joe:
		 
		 <?xml version="1.0" encoding="UTF-8"?>
		 <rls-services>
		 <service uri="sip:mybuddies@example.com">
		 <resource-list>http://xcap.example.com/resource-lists/users/joe/index/~~/
		 resource-lists/list%5b@name=%22l1%22%5d</resource-list>
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
		 http://xcap.example.com/root/rls-services/global/index would look
		 like:
		 
		 <?xml version="1.0" encoding="UTF-8"?>
		 <rls-services xmlns="urn:ietf:params:xml:ns:rls-services"
		 xmlns:rl="urn:ietf:params:xml:ns:resource-lists"
		 xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">
		 <service uri="sip:mybuddies@example.com">
		 <resource-list>http://xcap.example.com/resource-lists/users/joe/index/~~/
		 resource-lists/list%5b@name=%22l1%22%5d</resource-list>
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
		 
		 Requests made against the global document MUST generate responses
		 that reflect the most recent state of all the relevant user
		 documents.  This requirement does not imply that the server must
		 actually store this global document.  It is anticipated that most
		 systems will dynamically construct the responses to any particular
		 request against the document resource.
		 
		 The uniqueness constraint on the "uri" attribute of <service> will
		 ensure that no two <service> elements in the global document have the
		 same value of that attribute.		 
		 */
		
		/*
		// TODO more efficient cache document, wihtout creating the whole doc on each update, use getDocumentParent
		// split document parent
		String[] documentParentParts = documentSelector.getCompleteDocumentParent().split("/");
		// part 0 is "" and part 1 is the auid
		// so the auid child directory is part 2
		// is this a put of /rls-services/users/x/index document?
		if (documentParentParts[2].equals("users") && documentSelector.getDocumentName().equals("index")) {
			// declare global doc
			Document globalDocument = null;
			Element globalDocumentRoot = null;
			// declare uri set, to keep track of uniqueness
			// initialize service uri attrib set
			Set<String> serviceUriSet = new HashSet<String>();
			// get collection of rls-services/users
			DocumentCollection usersCollection = dataSource.getDocumentCollection("/rls-services/users");
			String[] usersChildCollectionNames = usersCollection.listChildCollections();
			for(int i=0;i<usersChildCollectionNames.length;i++) {
				// get child collection
				DocumentCollection childCollection = dataSource.getDocumentCollection(usersChildCollectionNames[i]);
				// get document index
				Document usersIndexChildDocument = childCollection.getDocument("index").getAsDOMDocument();				
				if (usersIndexChildDocument != null) {
					// get services from document and add it to the global one
					if (globalDocument == null) {
						// global doc is null, set it as the doc being processed
						globalDocument = usersIndexChildDocument;
						globalDocumentRoot = globalDocument.getDocumentElement();
					}
					else {						
						NodeList serviceNodes = usersIndexChildDocument.getDocumentElement().getChildNodes();
						for(int j=0;j<serviceNodes.getLength();j++) {
							if (serviceNodes.item(j).getNodeType() == Node.ELEMENT_NODE && serviceNodes.item(j).getLocalName().equals("service")) {
								// service element
								Attr serviceUriAttr = ((Element)serviceNodes.item(j)).getAttributeNode("uri");
								// attr must be unique
								if (serviceUriSet.contains(serviceUriAttr.getNodeValue())) {
									// not unique, raise exception
									throw new UniquenessFailureConflictException();
								}
								else {						
									// unique so far					
									// add it to the service uri set
									serviceUriSet.add(serviceUriAttr.getNodeValue());
									// add service to the global one
									globalDocument.importNode(serviceNodes.item(j),true);
									globalDocumentRoot.appendChild(serviceNodes.item(j));
									// FIXME need to import namespaces manually????
								}
							}
						}				
					}
				}				
			}			
			// get global collection
			DocumentCollection globalCollection = dataSource.getDocumentCollection("/rls-services/global");
			// put global doc
			globalCollection.putDocument("index",globalDocument);
			// put new document etag			
			dataSource.putDocumentEtag(globalCollection,"index",ETagGenerator.generate("/rls-services/global/index"));		
						
		}		
		*/			
	}
			
	public void checkConstraintsOnPut(Document document, String xcapRoot, DocumentSelector documentSelector, DataSource dataSource) throws UniquenessFailureConflictException, InternalServerErrorException, ConstraintFailureConflictException {
		
		super.checkConstraintsOnPut(document, xcapRoot, documentSelector, dataSource);
					
		/*		 
		 o  The URI in the "uri" attribute of the <service> element MUST be
		 unique amongst all other URIs in "uri" elements in any <service>
		 element in any document on a particular server.  This uniqueness
		 constraint spans across XCAP roots.
		 */
		
		/*
		 TODO undertstand this
		 Furthermore, the URI MUST NOT
		 correspond to an existing resource within the domain of the URI.
		 If a server is asked to set the URI to something that already
		 exists, the server MUST reject the request with a 409, and use the
		 mechanisms defined in [10] to suggest alternate URIs that have not
		 yet been allocated.		
		 */
		
		/*
		// initialize service uri attrib set
		Set<String> serviceUriSet = new HashSet<String>();
		*/
		
		/*
		 o  In addition, an RLS services document can contain a <list>
		 element, which in turn can contain <entry>, <entry-ref> and
		 <external> elements.  The constraints defined for these elements
		 in Section 3.4.7 MUST be enforced.				 
		 */		
		
		/*
		// initialize name uri attrib set
		Set<String> listNameUriSet = new HashSet<String>();
			
		// get document's element childs
		NodeList childNodes = document.getDocumentElement().getChildNodes();
		// process each one
		for(int i=0;i<childNodes.getLength();i++) {
			Node childNode = childNodes.item(i);			
			if (childNode.getNodeType() == Node.ELEMENT_NODE && childNode.getLocalName().equals("service")) {
				// service element
				Attr serviceUriAttr = ((Element)childNode).getAttributeNode("uri");
				// attr must be unique
				if (serviceUriSet.contains(serviceUriAttr.getNodeValue())) {
					// not unique, raise exception
					throw new UniquenessFailureConflictException();
				}
				else {						
					// unique so far					
					// add it to the service uri set
					serviceUriSet.add(serviceUriAttr.getNodeValue());
					// get childs
					NodeList serviceChildNodes = childNode.getChildNodes();
					// process each one
					for(int j=0;j<serviceChildNodes.getLength();j++) {
						Node serviceChildNode = serviceChildNodes.item(j);
						if (serviceChildNode.getNodeType() == Node.ELEMENT_NODE && serviceChildNode.getLocalName().equals("list")) {
							// list element
							Attr nameAttr = ((Element)serviceChildNode).getAttributeNode("name");
							if (nameAttr != null) {
								// name attr exists, it must be unique
								if (listNameUriSet.contains(nameAttr.getNodeValue())) {
									// not unique, raise exception
									throw new UniquenessFailureConflictException();
								}
								else {
									// unique so far, add it to the name set
									listNameUriSet.add(nameAttr.getNodeValue());
									// and process this list
									ResourceListsAppUsage.checkNodeResourceListConstraints(serviceChildNode);
								}
							}				
						}			 
						else if (serviceChildNode.getNodeType() == Node.ELEMENT_NODE && serviceChildNode.getLocalName().equals("resource-list")) {
							// resource-list element
				*/
							/*
							 o  The URI in a <resource-list> element MUST be an absolute URI.  The
							 server MUST verify that the URI path contains "resource-lists" in
							 the path segment corresponding to the AUID.  If the RLS services
							 document is within the XCAP user tree (as opposed to the global
							 tree), the server MUST verify that the XUI in the path is the same
							 as the XUI in the URI of to the RLS services document.  These
							 checks are made by examining the URI value, as opposed to
							 de-referencing the URI.  The server is not responsible for
							 verifying that the URI actually points to a <list> element within
							 a valid resource lists document.
							 */
				/*			
							// flag setup
							boolean throwException = true;
							// node value is the uri to evaluate
							String resourceListUri = serviceChildNode.getNodeValue();
							
							// split document parent
							String[] documentParentParts = documentSelector.getCompleteDocumentParent().split("/");
		
							try {																	
								// build uri
								URI uri = new URI(resourceListUri);
								if(uri.getScheme().equalsIgnoreCase("http")) {
									// split string after "http://" to find path segments
									String[] resourceListUriPaths = resourceListUri.substring(7).split("/");									
									for(int k=0;k<resourceListUriPaths.length;k++) {
										if (resourceListUriPaths[k].equals(documentSelector.getAUID())) {
											// found auid
											if (!resourceListUriPaths[k+1].equals("global")) {
												// not global
												if (resourceListUriPaths[k+2].equals(documentParentParts[3])) {
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
								// do nothing
							}							
							// throw exception if needed
							if (throwException) {
								throw new ConstraintFailureConflictException("URI: "+resourceListUri);
							}
						}												
					}
				}
			}						
		}	
		*/	
	}
		
}
	
