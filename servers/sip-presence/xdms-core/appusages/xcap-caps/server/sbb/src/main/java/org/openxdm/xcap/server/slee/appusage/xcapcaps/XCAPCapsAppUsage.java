package org.openxdm.xcap.server.slee.appusage.xcapcaps;

import javax.xml.validation.Validator;

import org.openxdm.xcap.common.appusage.AppUsage;


/**
 * 
 XCAP Server Capabilities App Usage
 
 (from Specs)
 
 XCAP can be extended through the addition of new application usages
 and extensions to the core protocol.  Application usages may define
 MIME types with XML schemas that allow new extension nodes from new
 namespaces.  It will often be necessary for a client to determine
 what extensions, application usages or namespaces a server supports
 before making a request.  To enable that, this specification defines
 an application usage with the AUID "xcap-caps".  All XCAP servers
 MUST support this application usage.  This usage defines a single
 document within the global tree which lists the capabilities of the
 server.  Clients can read this well-known document, and therefore
 learn the capabilities of the server.
 
 The structure of the document is simple.  The root element is <xcap-
 caps>.  Its children are <auids>, <extensions>, and <namespaces>.
 Each of these contain a list of AUIDs, extensions and namespaces
 supported by the server.  Extensions are named by tokens defined by
 the extension, and typically define new selectors.  Namespaces are
 identified by their namespace URI.  To 'support' a namespace, the
 server must have the schemas for all elements within that namespace,
 and be able to validate them if they appear within documents.  Since
 all XCAP servers support the "xcap-caps" AUID, it MUST be listed in
 the <auids> element and the "urn:ietf:params:xml:ns:xcap-caps"
 namespace MUST be listed in the <namespaces> element.
 
 The following sections provide the information needed to define this
 application usage.
 
 Application Unique ID (AUID):
 
 This specification defines the "xcap-caps" AUID within the IETF tree,
 via the IANA registration in Section 15.
 
 Default Document Namespace:
 
 The default document namespace used in evaluating a URI is
 urn:ietf:params:xml:ns:xcap-caps.
 
 MIME Type:
 
 Documents conformant to this schema are known by the MIME type
 "application/xcap-caps+xml", registered in Section 15.2.5.
 
 Validation Constraints:
 
 There are no additional validation constraints associated with this
 application usage.
 
 Data Semantics:
 
 Data semantics are defined above.
 
 Naming Conventions:
 
 A server MUST maintain a single instance of the document in the
 global tree, using the filename "index".  There MUST NOT be an
 instance of this document in the users tree.
 
 Resource Interdependencies:
 
 There are no resource interdependencies in this application usage
 beyond those defined by the schema.
 
 Authorization Policies:
 
 This application usage does not change the default authorization
 policy defined by XCAP.
 
 * @author Eduardo Martins
 *
 */
public class XCAPCapsAppUsage extends AppUsage {

	public static final String ID = "xcap-caps";
	public static final String DEFAULT_DOC_NAMESPACE = "urn:ietf:params:xml:ns:xcap-caps";
	public static final String MIMETYPE = "application/xcap-caps+xml";
	
	public XCAPCapsAppUsage(Validator schemaValidator) {
		super(ID,DEFAULT_DOC_NAMESPACE,MIMETYPE,schemaValidator);
	}
	
}
