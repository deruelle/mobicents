package org.openxdm.xcap.common.appusage;

import java.io.IOException;

import javax.xml.transform.dom.DOMSource;
import javax.xml.validation.Validator;


import org.openxdm.xcap.common.datasource.DataSource;
import org.openxdm.xcap.common.error.ConstraintFailureConflictException;
import org.openxdm.xcap.common.error.InternalServerErrorException;
import org.openxdm.xcap.common.error.SchemaValidationErrorConflictException;
import org.openxdm.xcap.common.error.UniquenessFailureConflictException;
import org.openxdm.xcap.common.uri.DocumentSelector;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

/**
 * Each XCAP resource on a server is associated with an application.  In
 * order for an application to use those resources, application specific
 * conventions must be specified.  Those conventions include the XML
 * schema that defines the structure and constraints of the data, well
 * known URIs to bootstrap access to the data, and so on.  All of those
 * application specific conventions are defined by the application
 * usage.
 * 
 * @author Eduardo Martins
 *
 */
public abstract class AppUsage {
	
	/**
	 * Each application usage is associated with a name, called an
	 * Application Unique ID (AUID).  This name uniquely identifies the
	 * application usage within the namespace of application usages, and is
	 * different from AUIDs used by other applications.
	 */
	private String auid = null;
	
	/**
	 * All application usages MUST define a
	 * namespace URI that represents the default document namespace to be
	 * used when evaluating URIs.  The default document namespace does not
	 * apply to elements or attributes within the documents themselves - it
	 * applies only to the evaluation of URIs within that application usage.
	 * Indeed, the term 'default document namespace' is distinct from the
	 * term 'default namespace'.  The latter has the standard meaning within
	 * XML documents, and the former refers to the default used in
	 * evaluation of XCAP URIs.  XCAP does not change in any way the
	 * mechanisms for determining the default namespace within XML
	 * documents.  However, if a document contains a URI representing an
	 * XCAP resource, the default document namespace defined by the
	 * application usage applies to that URI as well.
	 */
	private String defaultDocumentNamespace = null;
	
	/**
	 * The application usage MUST also identify the MIME type for documents
	 * compliant to that schema.
	 */
	private String mimetype = null;
	
	private Validator uniquenessSchemaValidator = null;
	
	/**
	 * All application usages MUST describe their document contents using XML
	 * schema. Here we have an appropriate validator.
	 */
	private Validator schemaValidator = null;
	
	/**
	 * By default, each user is able to access (read, modify, and delete)
	 * all of the documents below their home directory, and any user is able
	 * to read documents within the global directory.  However, only trusted
	 * users, explicitly provisioned into the server, can modify global
	 * documents.
	 * The application usage can specify a different authorization policy
	 * that applies to all documents associated with that application usage.
	 */
	private AuthorizationPolicy authorizationPolicy;

	public AppUsage(String auid,String defaultDocumentNamespace,String mimetype,Validator schemaValidator) {
		this.auid = auid;
		this.defaultDocumentNamespace = defaultDocumentNamespace;
		this.mimetype = mimetype;
		this.schemaValidator = schemaValidator;		
		authorizationPolicy = new DefaultAuthorizationPolicy();
	}
	
	public AppUsage(String auid,String defaultDocumentNamespace,String mimetype,Validator schemaValidator, AuthorizationPolicy authorizationPolicy) {
		this.auid = auid;
		this.defaultDocumentNamespace = defaultDocumentNamespace;
		this.mimetype = mimetype;
		this.schemaValidator = schemaValidator;		
		this.authorizationPolicy = authorizationPolicy;
	}

	public AppUsage(String auid,String defaultDocumentNamespace,String mimetype,Validator schemaValidator,Validator uniquenessSchemaValidator) {
		this.auid = auid;
		this.defaultDocumentNamespace = defaultDocumentNamespace;
		this.mimetype = mimetype;
		this.schemaValidator = schemaValidator;		
		this.uniquenessSchemaValidator = uniquenessSchemaValidator;
		authorizationPolicy = new DefaultAuthorizationPolicy();
	}
	
	public AppUsage(String auid,String defaultDocumentNamespace,String mimetype,Validator schemaValidator, Validator uniquenessSchemaValidator, AuthorizationPolicy authorizationPolicy) {
		this.auid = auid;
		this.defaultDocumentNamespace = defaultDocumentNamespace;
		this.mimetype = mimetype;
		this.schemaValidator = schemaValidator;
		this.uniquenessSchemaValidator = uniquenessSchemaValidator;
		this.authorizationPolicy = authorizationPolicy;
	}
	
	public String getDefaultDocumentNamespace() {
		return defaultDocumentNamespace;
	}	

	public String getAUID() {
		return auid;
	}	

	public String getMimetype() {
		return mimetype;
	}
	
	public void validateSchema(Document document) throws SchemaValidationErrorConflictException, InternalServerErrorException {
		// check arg
		if (document == null) {
			throw new IllegalArgumentException("document can't be null");
		}
		// validate
        try {
        	if (schemaValidator != null) {
        		schemaValidator.validate(new DOMSource(document));
        	}
        }
        catch (SAXException e) {
            throw new SchemaValidationErrorConflictException();
        } 
        catch (IOException e) {
			throw new InternalServerErrorException(e.getMessage());
		}  
	}	
	
	public AuthorizationPolicy getAuthorizationPolicy() {
		return authorizationPolicy;
	}
	
	/**
	 * The application usage can specify additional constraints that are not
	 * possible through XML Schema, e.g. a collection of documents need to have
	 * the root element with a unique value for attribute "id". In this method
	 * the app usage implements the checks on constraints for PUT operations. It
	 * is recommended that the main XML Schema of the app usage doesn't
	 * implement any of these checks, because in a error use case, the server
	 * will return a schema validation error, thus this API supports the usage
	 * of a additional XML Schema just for uniqueness contraints.
	 * 
	 * @param document
	 * @param xcapRoot
	 * @param documentSelector
	 * @param dataSource
	 * @throws UniquenessFailureConflictException
	 * @throws InternalServerErrorException
	 * @throws ConstraintFailureConflictException
	 */
	public void checkConstraintsOnPut(Document document, String xcapRoot, DocumentSelector documentSelector, DataSource dataSource) throws UniquenessFailureConflictException, InternalServerErrorException, ConstraintFailureConflictException {
		// check arg
		if (document == null) {
			throw new IllegalArgumentException("document can't be null");
		}
		else if(xcapRoot == null) {
			throw new IllegalArgumentException("xcap root can't be null");
		}
		else if(documentSelector == null) {
			throw new IllegalArgumentException("document selector can't be null");
		}
		else if(dataSource == null) {
			throw new IllegalArgumentException("data source can't be null");
		}
		
		// validate uniqueness schema if exists
		if (uniquenessSchemaValidator != null) {
			try {
				uniquenessSchemaValidator.validate(new DOMSource(document));            
			}
			catch (SAXException e) {
				throw new UniquenessFailureConflictException();
			} 
			catch (IOException e) {
				throw new InternalServerErrorException(e.getMessage());
			}
		}
		
		// default is nothing else to check
	}
	
	/**
	 * The application usage may specify resource interdependencies, like one
	 * global document being a composition of all user documents, this method is
	 * where the application usage defines this dependency logic on PUT
	 * requests.
	 * @param document
	 * @param documentSelector
	 * @param dataSource
	 * @throws SchemaValidationErrorConflictException
	 * @throws UniquenessFailureConflictException
	 * @throws InternalServerErrorException
	 * @throws ConstraintFailureConflictException
	 */
	public void processResourceInterdependenciesOnPut(Document document, DocumentSelector documentSelector, DataSource dataSource) throws SchemaValidationErrorConflictException, UniquenessFailureConflictException, InternalServerErrorException, ConstraintFailureConflictException {
		// check arg
		if (document == null) {
			throw new IllegalArgumentException("document can't be null");
		}
		else if(documentSelector == null) {
			throw new IllegalArgumentException("document selector can't be null");
		}
		else if(dataSource == null) {
			throw new IllegalArgumentException("data source can't be null");
		}
		
		//default is no resource interdependencies
	}	
	
	/**
	 * The application usage can specify additional constraints that are not
	 * possible through XML Schema, e.g. a collection of documents need to have
	 * the root element with a unique value for attribute "id". In this method
	 * the app usage implements the checks on constraints for DELETE operations. It
	 * is recommended that the main XML Schema of the app usage doesn't
	 * implement any of these checks, because in a error use case, the server
	 * will return a schema validation error, thus this API supports the usage
	 * of a additional XML Schema just for uniqueness contraints.
	 * 
	 * @param document
	 * @param xcapRoot
	 * @param documentSelector
	 * @param dataSource
	 * @throws UniquenessFailureConflictException
	 * @throws InternalServerErrorException
	 * @throws ConstraintFailureConflictException
	 */
	public void checkConstraintsOnDelete(Document document, String xcapRoot, DocumentSelector documentSelector, DataSource dataSource) throws UniquenessFailureConflictException, InternalServerErrorException, ConstraintFailureConflictException {
		// check arg
		 if(xcapRoot == null) {
			throw new IllegalArgumentException("xcap root can't be null");
		}
		else if(documentSelector == null) {
			throw new IllegalArgumentException("document selector can't be null");
		}
		else if(dataSource == null) {
			throw new IllegalArgumentException("data source can't be null");
		}
		 
		// default is nothing else to check
	}
	
	/**
	 * The application usage may specify resource interdependencies, like one
	 * global document being a composition of all user documents, this method is
	 * where the application usage defines this dependency logic on DELETE
	 * requests.
	 * 
	 * @param document
	 * @param documentSelector
	 * @param dataSource
	 * @throws SchemaValidationErrorConflictException
	 * @throws UniquenessFailureConflictException
	 * @throws InternalServerErrorException
	 * @throws ConstraintFailureConflictException
	 */
	public void processResourceInterdependenciesOnDelete(Document document, DocumentSelector documentSelector, DataSource dataSource) throws SchemaValidationErrorConflictException, UniquenessFailureConflictException, InternalServerErrorException, ConstraintFailureConflictException {
		// check arg
		if(documentSelector == null) {
			throw new IllegalArgumentException("document selector can't be null");
		}
		else if(dataSource == null) {
			throw new IllegalArgumentException("data source can't be null");
		}
		
		//default is no resource interdependencies
	}
}
