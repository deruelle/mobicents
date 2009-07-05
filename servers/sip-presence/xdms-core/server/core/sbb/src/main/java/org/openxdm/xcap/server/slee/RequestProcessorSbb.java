package org.openxdm.xcap.server.slee;

import java.io.InputStream;
import java.io.Reader;
import java.io.StringReader;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.slee.ActivityContextInterface;
import javax.slee.RolledBackContext;
import javax.slee.SbbContext;
import javax.xml.XMLConstants;
import javax.xml.transform.TransformerException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.apache.log4j.Logger;
import org.openxdm.xcap.common.appusage.AppUsage;
import org.openxdm.xcap.common.appusage.AuthorizationPolicy;
import org.openxdm.xcap.common.datasource.DataSource;
import org.openxdm.xcap.common.error.BadRequestException;
import org.openxdm.xcap.common.error.CannotDeleteConflictException;
import org.openxdm.xcap.common.error.CannotInsertConflictException;
import org.openxdm.xcap.common.error.ConflictException;
import org.openxdm.xcap.common.error.ConstraintFailureConflictException;
import org.openxdm.xcap.common.error.InternalServerErrorException;
import org.openxdm.xcap.common.error.MethodNotAllowedException;
import org.openxdm.xcap.common.error.NoParentConflictException;
import org.openxdm.xcap.common.error.NotAuthorizedRequestException;
import org.openxdm.xcap.common.error.NotFoundException;
import org.openxdm.xcap.common.error.NotUTF8ConflictException;
import org.openxdm.xcap.common.error.NotValidXMLFragmentConflictException;
import org.openxdm.xcap.common.error.PreconditionFailedException;
import org.openxdm.xcap.common.error.SchemaValidationErrorConflictException;
import org.openxdm.xcap.common.error.UniquenessFailureConflictException;
import org.openxdm.xcap.common.error.UnsupportedMediaTypeException;
import org.openxdm.xcap.common.resource.AttributeResource;
import org.openxdm.xcap.common.resource.DocumentResource;
import org.openxdm.xcap.common.resource.ElementResource;
import org.openxdm.xcap.common.resource.NamespaceBindings;
import org.openxdm.xcap.common.uri.AttributeSelector;
import org.openxdm.xcap.common.uri.DocumentSelector;
import org.openxdm.xcap.common.uri.ElementSelector;
import org.openxdm.xcap.common.uri.ElementSelectorStep;
import org.openxdm.xcap.common.uri.ElementSelectorStepByAttr;
import org.openxdm.xcap.common.uri.ElementSelectorStepByPos;
import org.openxdm.xcap.common.uri.ElementSelectorStepByPosAttr;
import org.openxdm.xcap.common.uri.NamespaceSelector;
import org.openxdm.xcap.common.uri.NodeSelector;
import org.openxdm.xcap.common.uri.ParseException;
import org.openxdm.xcap.common.uri.Parser;
import org.openxdm.xcap.common.uri.ResourceSelector;
import org.openxdm.xcap.common.uri.TerminalSelector;
import org.openxdm.xcap.common.xml.NamespaceContext;
import org.openxdm.xcap.common.xml.TextWriter;
import org.openxdm.xcap.common.xml.XMLValidator;
import org.openxdm.xcap.server.etag.ETagGenerator;
import org.openxdm.xcap.server.etag.ETagValidator;
import org.openxdm.xcap.server.result.CreatedWriteResult;
import org.openxdm.xcap.server.result.OKWriteResult;
import org.openxdm.xcap.server.result.ReadResult;
import org.openxdm.xcap.server.result.WriteResult;
import org.openxdm.xcap.server.slee.resource.appusagecache.AppUsageCacheResourceAdaptorSbbInterface;
import org.openxdm.xcap.server.slee.resource.datasource.DataSourceSbbInterface;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public abstract class RequestProcessorSbb implements
		RequestProcessorSbbLocalObject, javax.slee.Sbb {

	private SbbContext sbbContext = null; // This SBB's context

	private Context myEnv = null; // This SBB's environment

	private static Logger logger = Logger.getLogger(RequestProcessorSbb.class);

	private AppUsageCacheResourceAdaptorSbbInterface appUsageCache;
	private DataSourceSbbInterface dataSourceSbbInterface = null;

	/**
	 * if true a put request for a non existent user will insert user before
	 * request is processed
	 */
	private boolean dynamicUserProvisioning = false;

	/**
	 * Called when an sbb object is instantied and enters the pooled state.
	 */
	public void setSbbContext(SbbContext context) {
		if (logger.isDebugEnabled())
			logger.debug("setSbbContext(context=" + context.toString() + ")");
		this.sbbContext = context;
		try {
			myEnv = (Context) new InitialContext().lookup("java:comp/env");
			appUsageCache = (AppUsageCacheResourceAdaptorSbbInterface) myEnv
					.lookup("slee/resources/openxdm/appusagecache/sbbrainterface");
			dataSourceSbbInterface = (DataSourceSbbInterface) myEnv
					.lookup("slee/resources/openxdm/datasource/sbbrainterface");
			dynamicUserProvisioning = ((Boolean) myEnv
					.lookup("dynamicUserProvisioning")).booleanValue();
		} catch (NamingException e) {
			logger.error("Can't set sbb context.", e);
		}
	}

	public void unsetSbbContext() {
		if (logger.isDebugEnabled())
			logger.debug("unsetSbbContext()");
		this.sbbContext = null;
	}

	public void sbbCreate() throws javax.slee.CreateException {
	}

	public void sbbPostCreate() throws javax.slee.CreateException {
	}

	public void sbbActivate() {
	}

	public void sbbPassivate() {
	}

	public void sbbRemove() {
	}

	public void sbbLoad() {
	}

	public void sbbStore() {
	}

	public void sbbExceptionThrown(Exception exception, Object event,
			ActivityContextInterface activity) {
		if (logger.isDebugEnabled())
			logger.debug("sbbExceptionThrown(exception=" + exception.toString()
					+ ",event=" + event.toString() + ",activity="
					+ activity.toString() + ")");
	}

	public void sbbRolledBack(RolledBackContext sbbRolledBack) {
		if (logger.isDebugEnabled())
			logger.debug("sbbRolledBack(sbbRolledBack="
					+ sbbRolledBack.toString() + ")");
	}

	protected SbbContext getSbbContext() {
		return sbbContext;
	}

	// APPUSAGE CACHE ITERACTION

	// SERVICE LOGIC
	// #############################################################

	public WriteResult delete(ResourceSelector resourceSelector,
			ETagValidator eTagValidator, String xcapRoot, String authenticatedUser)
			throws NotFoundException, InternalServerErrorException,
			BadRequestException, CannotDeleteConflictException,
			PreconditionFailedException, MethodNotAllowedException,
			SchemaValidationErrorConflictException,
			UniquenessFailureConflictException,
			ConstraintFailureConflictException, NotAuthorizedRequestException {

		WriteResult result = new OKWriteResult();
		DocumentSelector documentSelector = null;
		NodeSelector nodeSelector = null;
		AttributeSelector attributeSelector = null;
		Map<String, String> namespaces = null;
		AppUsage appUsage = null;

		try {
			// parse document parent String
			documentSelector = Parser.parseDocumentSelector(resourceSelector
					.getDocumentSelector());
			// get app usage from cache
			appUsage = appUsageCache.borrow(documentSelector.getAUID());
			if (appUsage == null) {
				// throw exception
				if (logger.isDebugEnabled())
					logger.debug("appusage not found");
				throw new NotFoundException();
			}
			// authorize user
			if (authenticatedUser != null && !appUsage.getAuthorizationPolicy().isAuthorized(authenticatedUser, AuthorizationPolicy.Operation.DELETE, documentSelector)) {
				throw new NotAuthorizedRequestException();
			}
			// get document
			org.openxdm.xcap.common.datasource.Document document = dataSourceSbbInterface
					.getDocument(documentSelector);
			if (document == null) {
				// throw exception
				if (logger.isDebugEnabled())
					logger.debug("document not found");
				throw new NotFoundException();
			} else {
				// document exists
				if (logger.isDebugEnabled())
					logger.debug("document found");

				// get as dom
				Document domDocument = document.getAsDOMDocument();

				// check document etag
				if (eTagValidator != null) {
					eTagValidator.validate(document.getETag());
					if (logger.isDebugEnabled())
						logger.debug("document etag found and validated");
				} else {
					if (logger.isDebugEnabled())
						logger.debug("document etag not found");
				}

				// check node selector string from resource selector
				if (resourceSelector.getNodeSelector() != null) {
					// elem, attrib or namespace bind
					// parse node selector
					nodeSelector = Parser.parseNodeSelector(resourceSelector
							.getNodeSelector());
					if (logger.isDebugEnabled())
						logger.debug("node selector found and parsed");

					// create xpath
					XPath xpath = XPathFactory.newInstance().newXPath();
					// get namespaces bindings from resource selector
					namespaces = resourceSelector.getNamespaces();
					// get default doc namespace binding for app usage and add
					// it to namespace bindings for empty prefix
					namespaces.put(XMLConstants.DEFAULT_NS_PREFIX, appUsage
							.getDefaultDocumentNamespace());
					// add a namespace context to xpath to resolve bindings
					NamespaceContext nsContext = new NamespaceContext(
							namespaces);
					xpath.setNamespaceContext(nsContext);
					if (logger.isDebugEnabled())
						logger.debug("xpath initiated with namespace context");

					try {
						// exec query to get element
						NodeList elementNodeList = (NodeList) xpath.evaluate(
								nodeSelector
										.getElementSelectorWithEmptyPrefix(),
								domDocument, XPathConstants.NODESET);

						if (elementNodeList.getLength() > 1) { // MULTIPLE
							// ELEMENTS
							if (logger.isDebugEnabled())
								logger
										.debug("xpath query returned more than one element, returning not found");
							throw new NotFoundException();
						}

						else if (elementNodeList.getLength() == 1) {
							if (logger.isDebugEnabled())
								logger
										.debug("xpath query returned one element as expected");

							Element element = (Element) elementNodeList.item(0);
							if (nodeSelector.getTerminalSelector() != null) {
								// parse terminal selector
								TerminalSelector terminalSelector = Parser
										.parseTerminalSelector(nodeSelector
												.getTerminalSelector());
								if (logger.isDebugEnabled())
									logger
											.debug("terminal selector found and parsed");

								if (terminalSelector instanceof AttributeSelector) {
									if (logger.isDebugEnabled())
										logger
												.debug("terminal selector is an attribute selector");
									attributeSelector = (AttributeSelector) terminalSelector;
									// attribute selector, get attribute
									String attrName = attributeSelector
											.getAttName();
									if (element.hasAttribute(attrName)) {
										// exists, delete it
										element.removeAttribute(attrName);
										if (logger.isDebugEnabled())
											logger
													.debug("attribute found and deleted");
									} else {
										// does not exists
										if (logger.isDebugEnabled())
											logger
													.debug("attribute to delete not found");
										throw new NotFoundException();
									}
								} else if (terminalSelector instanceof NamespaceSelector) {
									// onle GET method is allowed for a
									// namespace selector
									if (logger.isDebugEnabled())
										logger
												.debug("terminal selector is a namespace selector, not allowed on delete");
									Map<String, String> map = new HashMap<String, String>();
									map.put("Allow", "GET");
									throw new MethodNotAllowedException(map);
								}

								else {
									// unknown terminal selector
									if (logger.isDebugEnabled())
										logger
												.debug("unknow terminal selector");
									throw new InternalServerErrorException(
											"unknown terminal selector");
								}

							} else { // DELETE ELEMENT
								if (logger.isDebugEnabled())
									logger
											.debug("terminal selector not found, delete of an element");
								// check cannot delete
								ElementSelectorStep lastElementSelectorStep = Parser
										.parseLastElementSelectorStep(nodeSelector
												.getElementSelector());
								if (lastElementSelectorStep instanceof ElementSelectorStepByPosAttr) {
									// need to check if it's the last sibring
									// with the same name and attr value
									ElementSelectorStepByPosAttr elementSelectorStepByPosAttr = (ElementSelectorStepByPosAttr) lastElementSelectorStep;
									if (elementSelectorStepByPosAttr.getName()
											.equals("*")) {
										if (logger.isDebugEnabled())
											logger
													.debug("element selector by attr and pos with wildcard name");
										// all elements wildcard
										Element siblingElement = element;
										while ((siblingElement = (Element) siblingElement
												.getNextSibling()) != null) {
											// get attribute with same name
											Attr siblingElementAttr = siblingElement
													.getAttributeNode(elementSelectorStepByPosAttr
															.getAttrName());
											// check if it has the same value
											if (siblingElementAttr != null
													&& siblingElementAttr
															.getValue()
															.equals(
																	elementSelectorStepByPosAttr
																			.getAttrValue())) {
												// we have a sibling with the
												// same attribute with the same
												// value, so when we delete the
												// element the uri points to
												// this one
												if (logger.isDebugEnabled())
													logger
															.debug("sibling element with same attr name and value, cannot delete");
												throw new CannotDeleteConflictException();
											}
										}
									} else {
										if (logger.isDebugEnabled())
											logger
													.debug("element selector by attr and pos without wildcard name");
										Element siblingElement = element;
										while ((siblingElement = (Element) siblingElement
												.getNextSibling()) != null) {
											if (element
													.getNodeName()
													.compareTo(
															siblingElement
																	.getNodeName()) == 0
													&& element
															.getNamespaceURI()
															.compareTo(
																	siblingElement
																			.getNamespaceURI()) == 0) {
												// sibling with the same name
												// get attribute with same name
												Attr siblingElementAttr = siblingElement
														.getAttributeNode(elementSelectorStepByPosAttr
																.getAttrName());
												// check if it has the same
												// value
												if (siblingElementAttr != null
														&& siblingElementAttr
																.getValue()
																.equals(
																		elementSelectorStepByPosAttr
																				.getAttrValue())) {
													// we have a sibling with
													// the same attribute with
													// the same value, so when
													// we delete the element the
													// uri points to this one
													if (logger.isDebugEnabled())
														logger
																.debug("sibling element with same attr name and value, cannot delete");
													throw new CannotDeleteConflictException();
												}
											}
										}
									}
								} else if (lastElementSelectorStep instanceof ElementSelectorStepByPos) {

									ElementSelectorStepByPos elementSelectorStepByPos = (ElementSelectorStepByPos) lastElementSelectorStep;
									/*
									 * In particular, if a DELETE operation
									 * refers to an element by name and position
									 * alone (parent/elname[n]), this is
									 * permitted only when the element to be
									 * deleted is the last element amongst all
									 * its siblings with that name. Similarly,
									 * if a DELETE operation refers to an
									 * element by position alone (parent/*[n]),
									 * this is permitted only when the elemented
									 * to be deleted is the last amongst all
									 * sibling elements, regardless of name.
									 */
									// find out if it's the last sibling
									if (elementSelectorStepByPos.getName()
											.equals("*")) {
										if (logger.isDebugEnabled())
											logger
													.debug("element selector by pos with wildcard name");
										if (element.getNextSibling() != null) {
											// not the last * sibling
											if (logger.isDebugEnabled())
												logger
														.debug("not the last * sibling, cannot delete");
											throw new CannotDeleteConflictException();
										}
									} else {
										if (logger.isDebugEnabled())
											logger
													.debug("element selector by pos without wildcard name");
										// search a next sibling with the same
										// name
										Element siblingElement = element;
										while ((siblingElement = (Element) siblingElement
												.getNextSibling()) != null) {
											if (element
													.getNodeName()
													.compareTo(
															siblingElement
																	.getNodeName()) == 0
													&& element
															.getNamespaceURI()
															.compareTo(
																	siblingElement
																			.getNamespaceURI()) == 0) {
												if (logger.isDebugEnabled())
													logger
															.debug("sibling element with same name and ns after the selected element,cannot delete");
												throw new CannotDeleteConflictException();
											}
										}
									}
								}
								if (logger.isDebugEnabled())
									logger.debug("element deleted");
								// the element can be deleted
								element.getParentNode().removeChild(element);
							}

						} else { // ELEMENT DOES NOT EXIST
							if (logger.isDebugEnabled())
								logger.debug("element not found");
							throw new NotFoundException();
						}

						if (logger.isDebugEnabled())
							logger.debug("validating document after delete");
						// validate the updated document against it's schema
						appUsage.validateSchema(domDocument);

					} catch (XPathExpressionException e) {
						// error in xpath expression
						if (logger.isDebugEnabled())
							logger.debug("error in xpath expression.");
						if (nodeSelector
								.elementSelectorHasUnbindedPrefixes(namespaces)) {
							// element selector has unbinded prefixe(s)
							if (logger.isDebugEnabled())
								logger
										.debug("element selector doesn't have prefixe(s) bound, bad request");
							throw new BadRequestException();
						} else {
							// nothing wrong with prefixes, return not found
							// exception
							if (logger.isDebugEnabled())
								logger.debug("element not found");
							throw new NotFoundException();
						}
					}
				}

				if (logger.isDebugEnabled())
					logger
							.debug("checking app usage constraints and resource interdependencies...");
				// verify app usage constraints
				appUsage.checkConstraintsOnDelete(domDocument, xcapRoot,
						documentSelector, dataSourceSbbInterface);
				// process resource interdependencies
				appUsage.processResourceInterdependenciesOnDelete(domDocument,
						documentSelector, dataSourceSbbInterface);
				// last delete or update the doc
				if (resourceSelector.getNodeSelector() == null) {
					// delete document
					if (logger.isDebugEnabled())
						logger
								.debug("node selector not found, delete of a document");
					dataSourceSbbInterface.deleteDocument(documentSelector,
							document.getETag());
				} else {
					// update document
					// create new etag
					String newETag = ETagGenerator.generate(resourceSelector
							.getDocumentSelector());

					// update data source with document
					try {
						String xml = TextWriter.toString(domDocument);
						if (attributeSelector == null) {
							dataSourceSbbInterface.updateElement(
									documentSelector, nodeSelector, namespaces,
									document.getETag(), newETag, xml,
									domDocument, null, null);
						} else {
							dataSourceSbbInterface.updateAttribute(
									documentSelector, nodeSelector,
									attributeSelector, namespaces, document
											.getETag(), newETag, xml,
									domDocument, null);
						}
						if (logger.isDebugEnabled())
							logger.debug("document updated in data source");
					} catch (Exception e) {
						throw new InternalServerErrorException(
								"Failed to serialize resulting dom document to string");
					}
					// add it to the result
					result.setResponseEntityTag(newETag);
				}
				if (logger.isDebugEnabled())
					logger.debug("delete request processed with sucess");
				// return result
				return result;
			}
		} catch (ParseException e) {
			if (logger.isDebugEnabled())
				logger.debug("error parsing uri, returning not found");
			throw new NotFoundException();
		} catch (InterruptedException e) {
			String msg = "failed to borrow app usage object from cache";
			logger.error(msg, e);
			throw new InternalServerErrorException(msg);
		} finally {
			if (appUsage != null) {
				appUsageCache.release(appUsage);
			}
		}

	}

	public ReadResult get(ResourceSelector resourceSelector, String authenticatedUser)
			throws NotFoundException, InternalServerErrorException,
			BadRequestException, NotAuthorizedRequestException {

		AppUsage appUsage = null;
		try {
			// parse document parent String
			DocumentSelector documentSelector = Parser
					.parseDocumentSelector(resourceSelector
							.getDocumentSelector());
			// get app usage from cache
			appUsage = appUsageCache.borrow(documentSelector.getAUID());
			if (appUsage == null) {
				// throw exception
				if (logger.isDebugEnabled())
					logger.debug("appusage not found");
				throw new NotFoundException();
			}
			// authorize user
			if (authenticatedUser != null && !appUsage.getAuthorizationPolicy().isAuthorized(authenticatedUser, AuthorizationPolicy.Operation.GET, documentSelector)) {
				throw new NotAuthorizedRequestException();
			}
			// get document
			org.openxdm.xcap.common.datasource.Document document = dataSourceSbbInterface
					.getDocument(documentSelector);
			if (document == null) {
				// throw exception
				if (logger.isDebugEnabled())
					logger.debug("document not found");
				throw new NotFoundException();
			}
			if (logger.isDebugEnabled())
				logger.debug("document found");
			// get document's etag
			String eTag = document.getETag();
			// check node selector string from resource selector
			if (resourceSelector.getNodeSelector() != null) {
				// elem, attrib or namespace bind
				// parse node selector
				NodeSelector nodeSelector = Parser
						.parseNodeSelector(resourceSelector.getNodeSelector());
				if (logger.isDebugEnabled())
					logger.debug("node selector found and parsed");
				// create xpath
				XPath xpath = XPathFactory.newInstance().newXPath();
				// get namespaces bindings from resource selector
				Map<String, String> namespaces = resourceSelector
						.getNamespaces();
				// get default doc namespace binding for app usage and add it to
				// namespace bindings for empty prefix
				namespaces.put(XMLConstants.DEFAULT_NS_PREFIX, appUsage
						.getDefaultDocumentNamespace());
				// add a namespace context to xpath to resolve bindings
				NamespaceContext nsContext = new NamespaceContext(namespaces);
				xpath.setNamespaceContext(nsContext);
				if (logger.isDebugEnabled())
					logger.debug("xpath initiated with namespace context");
				// get document as dom
				org.w3c.dom.Document domDocument = document.getAsDOMDocument();
				try {
					// exec query for element
					NodeList elementNodeList = (NodeList) xpath.evaluate(
							nodeSelector.getElementSelectorWithEmptyPrefix(),
							domDocument, XPathConstants.NODESET);

					if (elementNodeList.getLength() > 1) { // MULTIPLE ELEMENTS
						if (logger.isDebugEnabled())
							logger
									.debug("xpath query returned more than one element, returning not found");
						throw new NotFoundException();
					}

					else if (elementNodeList.getLength() == 1) {
						if (logger.isDebugEnabled())
							logger
									.debug("xpath query returned one element as expected");
						Element element = (Element) elementNodeList.item(0);

						if (nodeSelector.getTerminalSelector() != null) {
							// parse terminal selector
							TerminalSelector terminalSelector = Parser
									.parseTerminalSelector(nodeSelector
											.getTerminalSelector());
							if (logger.isDebugEnabled())
								logger
										.debug("terminal selector found and parsed");
							if (terminalSelector instanceof AttributeSelector) {
								// attribute selector, get attribute
								if (logger.isDebugEnabled())
									logger
											.debug("terminal selector is an attribute selector");
								Attr attr = element
										.getAttributeNode(((AttributeSelector) terminalSelector)
												.getAttName());
								if (attr != null) {
									// exists, return its value
									if (logger.isDebugEnabled())
										logger
												.debug("attribute found, returning result");
									return new ReadResult(eTag,
											new AttributeResource(attr
													.getNodeValue()));
								} else {
									// does not exists
									if (logger.isDebugEnabled())
										logger
												.debug("attribute to retreive not found");
									throw new NotFoundException();
								}
							} else if (terminalSelector instanceof NamespaceSelector) {
								// namespace selector, get namespace bindings
								if (logger.isDebugEnabled())
									logger
											.debug("terminal selector is a namespace selector");
								return new ReadResult(eTag,
										getNamespaceBindings(element, element
												.getLocalName(),
												resourceSelector
														.getNamespaces()));
							} else {
								// invalid terminal selector
								if (logger.isDebugEnabled())
									logger.debug("unknow terminal selector");
								throw new NotFoundException();
							}
						} else {
							// element
							if (logger.isDebugEnabled())
								logger
										.debug("terminal selector not found, returining result with the element found");
							return new ReadResult(eTag, new ElementResource(
									TextWriter.toString(element)));
						}
					} else { // ELEMENT DOES NOT EXIST
						if (logger.isDebugEnabled())
							logger.debug("element not found");
						throw new NotFoundException();
					}
				} catch (XPathExpressionException e) {
					// error in xpath expression
					if (logger.isDebugEnabled())
						logger.debug("error in xpath expression.");
					if (nodeSelector
							.elementSelectorHasUnbindedPrefixes(namespaces)) {
						// element selector has unbinded prefixe(s)
						if (logger.isDebugEnabled())
							logger
									.debug("element selector doesn't have prefixe(s) bound, bad request");
						throw new BadRequestException();
					} else {
						// nothing wrong with prefixes, return not found
						// exception
						if (logger.isDebugEnabled())
							logger.debug("element not found");
						throw new NotFoundException();
					}
				}
			} else {
				// no node selector, just get the document
				if (logger.isDebugEnabled())
					logger
							.debug("node selector not found, returning the document");
				return new ReadResult(eTag, new DocumentResource(document
						.getAsString(), appUsage.getMimetype()));
			}
		} catch (ParseException e) {
			if (logger.isDebugEnabled())
				logger.debug("error in parsing uri.");
			throw new NotFoundException();
		} catch (TransformerException e) {
			logger.error("unable to transform dom element to text.", e);
			throw new InternalServerErrorException(e.getMessage());
		} catch (InterruptedException e) {
			String msg = "failed to borrow app usage object from cache";
			logger.error(msg, e);
			throw new InternalServerErrorException(msg);
		} finally {
			if (appUsage != null) {
				appUsageCache.release(appUsage);
			}
		}
	}

	private NamespaceBindings getNamespaceBindings(Node element,
			String elementName, Map<String, String> namespacesToGet)
			throws NotFoundException {

		boolean done = false;
		// init result namespaces map
		Map<String, String> result = new HashMap<String, String>();
		// remove empty prefix from "namespaces to get" map
		namespacesToGet.remove("");
		// create set of namespaces uri to get
		Collection<String> namespacesUris = namespacesToGet.values();

		while (done == false && element.getNodeType() == Node.ELEMENT_NODE) {
			// get element attributes
			NamedNodeMap elementAttributes = element.getAttributes();
			// process each one
			for (int i = 0; i < elementAttributes.getLength(); i++) {
				Node attributeNode = elementAttributes.item(i);
				if (attributeNode.getNodeName().compareTo("xmlns") == 0
						|| attributeNode.getPrefix().compareTo("xmlns") == 0) {
					// its a namespace
					if (namespacesUris.contains(attributeNode.getNodeValue())) {
						// it was requested, add it to the result map
						result.put(attributeNode.getNodeName(), attributeNode
								.getNodeValue());
						if (result.size() == namespacesUris.size()) {
							done = true;
							break;
						}
					}
				}
			}
			// move to parent
			element = element.getParentNode();
		}

		if (!done) {
			// at least one was not found
			if (logger.isDebugEnabled())
				logger
						.debug("didn't found any namespace binding, returning not found");
			throw new NotFoundException();
		} else {
			// return namespace bindings
			if (logger.isDebugEnabled())
				logger.debug("found namespace binding(s)");
			return new NamespaceBindings(elementName, result);
		}
	}

	public WriteResult put(ResourceSelector resourceSelector, String mimetype,
			InputStream contentStream, ETagValidator eTagValidator,
			String xcapRoot, String authenticatedUser) throws ConflictException,
			MethodNotAllowedException, UnsupportedMediaTypeException,
			InternalServerErrorException, PreconditionFailedException,
			BadRequestException, NotAuthorizedRequestException {

		WriteResult result = null;
		DocumentSelector documentSelector = null;
		NodeSelector nodeSelector = null;
		AttributeSelector attributeSelector = null;
		Map<String, String> namespaces = null;
		Document domDocument = null;
		String attributeValue = null;
		String newElementAsString = null;
		Element newElement = null;
		AppUsage appUsage = null;

		try {

			// parse document selector
			documentSelector = Parser.parseDocumentSelector(resourceSelector
					.getDocumentSelector());

			// get app usage from cache
			appUsage = appUsageCache.borrow(documentSelector.getAUID());
			if (appUsage == null) {
				// throw exception
				if (logger.isDebugEnabled())
					logger.debug("appusage not found");
				throw new NoParentConflictException(xcapRoot);
			}
			// authorize user
			if (authenticatedUser != null && !appUsage.getAuthorizationPolicy().isAuthorized(authenticatedUser, AuthorizationPolicy.Operation.PUT, documentSelector)) {
				throw new NotAuthorizedRequestException();
			}
			if (dynamicUserProvisioning) {
				// creates user if does not exist
				String[] appUsageCollections = dataSourceSbbInterface
						.getCollections(appUsage.getAUID());
				boolean found = false;
				for (String collection : appUsageCollections) {
					if (collection.equals(documentSelector.getDocumentParent())) {
						found = true;
						break;
					}
				}
				if (!found) {
					dataSourceSbbInterface.addCollection(appUsage.getAUID(),
							documentSelector.getDocumentParent());
				}
			}

			// try to get document's resource
			org.openxdm.xcap.common.datasource.Document document = dataSourceSbbInterface
					.getDocument(documentSelector);

			if (document == null) { // DOCUMENTS DOES NOT EXIST
				if (logger.isDebugEnabled())
					logger.debug("document not found");

				// check parent exists
				String existingParent = dataSourceSbbInterface
						.getExistingCollection(documentSelector.getAUID(),
								documentSelector.getDocumentParent());
				if (!existingParent
						.equals(documentSelector.getDocumentParent())) {
					throw new NoParentConflictException(xcapRoot + "/"
							+ documentSelector.getAUID() + "/" + existingParent);
				}

				if (resourceSelector.getNodeSelector() != null) {
					// we have a node selector, throw exception with ancestor,
					// which we already know it's the document parent
					if (logger.isDebugEnabled())
						logger.debug("node selector found, no parent conflict");
					throw new NoParentConflictException(xcapRoot
							+ documentSelector.getCompleteDocumentParent());
				}

				else { // PUT NEW DOCUMENT
					if (logger.isDebugEnabled())
						logger
								.debug("node selector not found, put of a document");
					if (mimetype == null
							|| !mimetype.equals(appUsage.getMimetype())) {
						// mimetype is not valid
						if (logger.isDebugEnabled())
							logger
									.debug("invalid mimetype, does not matches the app usage");
						throw new UnsupportedMediaTypeException();
					}
					// verify if content is utf-8
					Reader utf8reader = XMLValidator
							.getUTF8Reader(contentStream);
					if (logger.isDebugEnabled())
						logger.debug("document content is utf-8");
					// create document
					domDocument = XMLValidator
							.getWellFormedDocument(utf8reader);
					if (logger.isDebugEnabled())
						logger.debug("document content parsed with sucess");
					// create result
					result = new CreatedWriteResult();
				}

			} else { // DOCUMENT EXISTS
				if (logger.isDebugEnabled())
					logger.debug("document found");

				// get as dom
				domDocument = document.getAsDOMDocument();

				// check document etag
				if (eTagValidator != null) {
					eTagValidator.validate(document.getETag());
					if (logger.isDebugEnabled())
						logger.debug("document etag found and validated");
				} else {
					if (logger.isDebugEnabled())
						logger.debug("document etag not found");
				}

				if (resourceSelector.getNodeSelector() != null) { // PUT
					// ELEMENT
					// OR ATTR

					// create xpath
					XPath xpath = XPathFactory.newInstance().newXPath();
					// get namespaces bindings from resource selector
					namespaces = resourceSelector.getNamespaces();
					// get default doc namespace binding for app usage and add
					// it to namespace bindings for empty prefix
					namespaces.put(XMLConstants.DEFAULT_NS_PREFIX, appUsage
							.getDefaultDocumentNamespace());
					// add a namespace context to xpath to resolve bindings
					NamespaceContext nsContext = new NamespaceContext(
							namespaces);
					xpath.setNamespaceContext(nsContext);
					if (logger.isDebugEnabled())
						logger.debug("xpath initiated with namespace context");

					try {

						// parse node selector
						nodeSelector = Parser
								.parseNodeSelector(resourceSelector
										.getNodeSelector());
						if (logger.isDebugEnabled())
							logger.debug("node selector found and parsed");
						String elementSelectorWithEmptyPrefix = nodeSelector
								.getElementSelectorWithEmptyPrefix();

						try {
							// exec query for element

							NodeList elementNodeList = (NodeList) xpath
									.evaluate(elementSelectorWithEmptyPrefix,
											domDocument, XPathConstants.NODESET);

							if (elementNodeList.getLength() > 1) { // MULTIPLE
								// ELEMENTS
								if (logger.isDebugEnabled())
									logger
											.debug("xpath query returned more than one element, no parent conflict");
								throw new NoParentConflictException(
										getElementExistentAncestor(xcapRoot,
												resourceSelector
														.getDocumentSelector(),
												elementSelectorWithEmptyPrefix,
												domDocument, xpath));
							}

							else if (elementNodeList.getLength() == 1) { // ELEMENT
								// EXISTS
								if (logger.isDebugEnabled())
									logger
											.debug("xpath query returned one element as expected");
								Element element = (Element) elementNodeList
										.item(0);

								if (nodeSelector.getTerminalSelector() != null) { // PUT
									// ATTR
									// ?
									try {
										// parse terminal selector
										TerminalSelector terminalSelector = Parser
												.parseTerminalSelector(nodeSelector
														.getTerminalSelector());
										if (logger.isDebugEnabled())
											logger
													.debug("terminal selector found and parsed");

										if (terminalSelector instanceof AttributeSelector) { // PUT
											// ATTR
											// CONFIRMED
											if (logger.isDebugEnabled())
												logger
														.debug("terminal selector is an attribute selector");
											// verify mimetype
											if (mimetype == null
													|| !mimetype
															.equals(AttributeResource.MIMETYPE)) {
												// mimetype is not correct
												throw new UnsupportedMediaTypeException();
											}
											// read and verify if attribute
											// value is utf-8
											attributeValue = XMLValidator
													.getUTF8String(contentStream);
											if (logger.isDebugEnabled())
												logger
														.debug("attr content is utf-8");
											// verify if attribute value is
											// valid AttValue
											XMLValidator
													.checkAttValue(attributeValue);
											if (logger.isDebugEnabled())
												logger
														.debug("attr value is valid AttValue");
											// get attribute name
											attributeSelector = (AttributeSelector) terminalSelector;
											String attributeName = attributeSelector
													.getAttName();
											// get attribute
											Attr attribute = element
													.getAttributeNode(attributeName);
											if (attribute != null) { // ATTR
												// EXISTS
												if (logger.isDebugEnabled())
													logger
															.debug("attr found in document");
												try {
													// verify if cannot insert,
													// e.g .../x[id1="1"]/@id1
													// and attValue = 2
													ElementSelectorStep lastElementSelectorStep = Parser
															.parseLastElementSelectorStep(nodeSelector
																	.getElementSelector());
													if (lastElementSelectorStep instanceof ElementSelectorStepByAttr) {
														ElementSelectorStepByAttr elementSelectorByAttr = (ElementSelectorStepByAttr) lastElementSelectorStep;
														// if this step attr
														// name is the specified
														// attrName and this
														// step attrValue is not
														// the same as the
														// specified attr value,
														// it cannot insert
														if (elementSelectorByAttr
																.getAttrName()
																.equals(
																		attributeName)
																&& !elementSelectorByAttr
																		.getAttrValue()
																		.equals(
																				attributeValue)) {
															if (logger
																	.isDebugEnabled())
																logger
																		.debug("element selector's last step attr name is the specified attrName and this step attrValue is not the same as the specified attr value, cannot insert");
															throw new CannotInsertConflictException();
														}
													}
												} catch (ParseException e) {
													// this shouldn't happen
													logger
															.error(
																	"error parsing last element selector step",
																	e);
													throw new InternalServerErrorException(
															"error parsing last element selector step");
												}
												// create result
												result = new OKWriteResult();
											} else { // ATTR DOES NOT EXISTS
												if (logger.isDebugEnabled())
													logger
															.debug("attr not found in document");
												result = new CreatedWriteResult();
											}
											// set attribute
											element.setAttribute(attributeName,
													attributeValue);
											// element.setAttributeNS(namespace,
											// attributeName,attributeValue);
											if (logger.isDebugEnabled())
												logger.debug("attr set");
										}

										else if (terminalSelector instanceof NamespaceSelector) { // PUT
											// NAMESPACE
											// BINDINGS
											// onle GET method is allowed for a
											// namespace selector
											if (logger.isDebugEnabled())
												logger
														.debug("terminal selector is a namespace selector, not allowed on put");
											Map<String, String> map = new HashMap<String, String>();
											map.put("Allow", "GET");
											throw new MethodNotAllowedException(
													map);
										}

										else {
											// unknown terminal selector
											if (logger.isDebugEnabled())
												logger
														.debug("unknown terminal selector");
											throw new InternalServerErrorException(
													"unknown terminal selector");
										}
									} catch (ParseException e) {
										// invalid terminal selector, existing
										// ancestor is the element
										if (logger.isDebugEnabled())
											logger
													.debug("failed to parse terminal selector, returning no parent conflict with element as ancestor");
										throw new NoParentConflictException(
												xcapRoot
														+ resourceSelector
																.getDocumentSelector()
														+ "/~~"
														+ nodeSelector
																.getElementSelector());
									}

								}

								else { // REPLACE ELEMENT
									if (logger.isDebugEnabled())
										logger.debug("element found");
									if (mimetype == null
											|| !mimetype
													.equals(ElementResource.MIMETYPE)) {
										// mimetype is not correct
										throw new UnsupportedMediaTypeException();
									}
									// read and verify if content value is utf-8
									newElementAsString = XMLValidator
											.getUTF8String(contentStream);
									if (logger.isDebugEnabled())
										logger.debug("content is utf-8");
									// create XML fragment node
									newElement = XMLValidator
											.getWellFormedDocumentFragment(new StringReader(
													newElementAsString));
									if (logger.isDebugEnabled())
										logger
												.debug("content is well formed document fragment");
									try {
										// verify if cannot insert
										ElementSelectorStep lastElementSelectorStep = Parser
												.parseLastElementSelectorStep(nodeSelector
														.getElementSelector());
										// if element's tag name is not equal to
										// this step's name then cannot insert
										if (!newElement.getTagName().equals(
												lastElementSelectorStep
														.getName())) {
											if (logger.isDebugEnabled())
												logger
														.debug("element's tag name is not equal to this step's name, cannot insert");
											throw new CannotInsertConflictException();
										}
										if (lastElementSelectorStep instanceof ElementSelectorStepByAttr) {
											ElementSelectorStepByAttr elementSelectorStepByAttr = (ElementSelectorStepByAttr) lastElementSelectorStep;
											// check attr value
											String elementAttrValue = newElement
													.getAttribute(elementSelectorStepByAttr
															.getAttrName());
											if (elementAttrValue == null
													|| !elementAttrValue
															.equals(elementSelectorStepByAttr
																	.getAttrValue())) {
												if (logger.isDebugEnabled())
													logger
															.debug("element selector's last step has an attr and it's new value changes this attr value, cannot insert");
												throw new CannotInsertConflictException();
											}
										}
										// import the element node
										newElement = (Element) domDocument
												.importNode(newElement, true);
										// replace node
										element.getParentNode().replaceChild(
												newElement, element);
										// create result
										result = new OKWriteResult();
										if (logger.isDebugEnabled())
											logger.debug("element replaced");
									} catch (ParseException e) {
										// MUST not come here, the element was
										// found
										logger
												.error(
														"the element was found but the parsing of the element selector's last step thrown an error",
														e);
										throw new InternalServerErrorException(
												"the element was found but the parsing of the element selector's last step thrown an error");
									}
								}

							}

							else { // ELEMENT NOT FOUND
								if (logger.isDebugEnabled())
									logger.debug("element not found");

								if (nodeSelector.getTerminalSelector() != null) {
									// throw no parent exception since there is
									// no element but we have a terminal
									// selector
									if (logger.isDebugEnabled())
										logger
												.debug("element not found but terminal selector exists, returning no parent conflict with document as ancestor");
									throw new NoParentConflictException(
											getElementExistentAncestor(
													xcapRoot,
													resourceSelector
															.getDocumentSelector(),
													elementSelectorWithEmptyPrefix,
													domDocument, xpath));
								} else { // NEW ELEMENT
									if (mimetype == null
											|| !mimetype
													.equals(ElementResource.MIMETYPE)) {
										// mimetype is not correct
										throw new UnsupportedMediaTypeException();
									}
									try {
										// parse the element selector
										ElementSelector elementSelector = Parser
												.parseElementSelector(nodeSelector
														.getElementSelector());
										if (logger.isDebugEnabled())
											logger
													.debug("element selector parsed with sucess");
										// get element parent selector with
										// empty prefix
										String elementParentSelectorWithEmptyPrefix = nodeSelector
												.getElementParentSelectorWithEmptyPrefix();
										boolean elementParentExists = true;
										try {
											// get element parent
											NodeList parentElementNodeList = (NodeList) xpath
													.evaluate(
															elementParentSelectorWithEmptyPrefix,
															domDocument,
															XPathConstants.NODESET);
											if (parentElementNodeList
													.getLength() == 1
													&& parentElementNodeList
															.item(0) instanceof Element) { // ELEMENT
												// PARENT
												// EXISTS
												Element elementParent = (Element) parentElementNodeList
														.item(0);
												// read and verify if content
												// value is utf-8
												newElementAsString = XMLValidator
														.getUTF8String(contentStream);
												if (logger.isDebugEnabled())
													logger
															.debug("element content is utf-8");
												// create XML fragment node
												newElement = XMLValidator
														.getWellFormedDocumentFragment(new StringReader(
																newElementAsString));
												if (logger.isDebugEnabled())
													logger
															.debug("element content is well formed document fragment");
												newElement = (Element) domDocument
														.importNode(newElement,
																true);
												// put new element
												domDocument = putNewElementInDocument(
														newElement,
														elementSelector,
														elementParent,
														domDocument, nsContext);
												if (logger.isDebugEnabled())
													logger
															.debug("element parent found, new element added");
											} else {
												elementParentExists = false;
											}
										} catch (XPathExpressionException e) {
											// invalid xpath expression
											elementParentExists = false;
										}

										if (!elementParentExists) { // ELEMENT
											// PARENT
											// DOES NOT
											// EXIST
											// find it's existing ancestor &
											// throw exception
											if (logger.isDebugEnabled())
												logger
														.debug("element parent not found, returning no parent conflict");
											throw new NoParentConflictException(
													getElementExistentAncestor(
															xcapRoot,
															resourceSelector
																	.getDocumentSelector(),
															elementParentSelectorWithEmptyPrefix,
															domDocument, xpath));
										}
									} catch (ParseException e) {
										// failed to parse the element selector,
										// throw no parent exception
										if (logger.isDebugEnabled())
											logger
													.debug("failed to parse element selector, returning no parent conflict");
										throw new NoParentConflictException(
												getElementExistentAncestor(
														xcapRoot,
														resourceSelector
																.getDocumentSelector(),
														elementSelectorWithEmptyPrefix,
														domDocument, xpath));
									}

									// create result
									result = new CreatedWriteResult();
								}
							}
						} catch (XPathExpressionException e) {
							// invalid xpath expression
							if (logger.isInfoEnabled()) {
								logger.info("error in xpath expression");
							}
							// check element selector for unbinded prefixes
							if (nodeSelector
									.elementSelectorHasUnbindedPrefixes(namespaces)) {
								if (logger.isDebugEnabled())
									logger
											.debug("element selector doesn't have prefixe(s) bound, bad request");
								throw new BadRequestException();
							} else {
								// find existing ancestor
								if (logger.isDebugEnabled())
									logger
											.debug("element not found, returning no parent conflict");
								throw new NoParentConflictException(
										getElementExistentAncestor(xcapRoot,
												resourceSelector
														.getDocumentSelector(),
												elementSelectorWithEmptyPrefix,
												domDocument, xpath));
							}
						}
					} catch (ParseException e) {
						// unable to parse the node selector, throw no parent
						// exception with the document as the existent ancestor
						if (logger.isDebugEnabled())
							logger
									.debug("unable to parse the node selector, returning no parent conflict with the document as the existent ancestor");
						throw new NoParentConflictException(xcapRoot
								+ resourceSelector.getDocumentSelector());
					}

				} else { // DOCUMENT EXISTS, REPLACE IT
					if (logger.isDebugEnabled())
						logger.debug("document found");
					if (mimetype == null
							|| !mimetype.equals(appUsage.getMimetype())) {
						// mimetype is not valid
						if (logger.isDebugEnabled())
							logger
									.debug("invalid mimetype, does not matches the app usage");
						throw new UnsupportedMediaTypeException();
					}
					// verify if content is utf-8
					Reader utf8reader = XMLValidator
							.getUTF8Reader(contentStream);
					if (logger.isDebugEnabled())
						logger.debug("document content is utf-8");
					// get document
					domDocument = XMLValidator
							.getWellFormedDocument(utf8reader);
					if (logger.isDebugEnabled())
						logger.debug("document content is well formed");
					// create result
					result = new OKWriteResult();
				}

			}

			// validate the updated document against it's schema
			appUsage.validateSchema(domDocument);
			if (logger.isDebugEnabled())
				logger.debug("document validated by schema");
			// verify app usage constraints
			appUsage.checkConstraintsOnPut(domDocument, xcapRoot,
					documentSelector, dataSourceSbbInterface);
			if (logger.isDebugEnabled())
				logger.debug("app usage constraints checked");
			// process resource interdependencies
			appUsage.processResourceInterdependenciesOnPut(domDocument,
					documentSelector, dataSourceSbbInterface);
			if (logger.isDebugEnabled())
				logger.debug("app usage resource interdependencies processed");
			// create new document etag
			String newETag = ETagGenerator.generate(resourceSelector
					.getDocumentSelector());
			if (logger.isDebugEnabled())
				logger
						.debug("new document etag generated and stored in data source");
			// update data source with document
			try {
				String xml = TextWriter.toString(domDocument);
				if (document == null) {
					dataSourceSbbInterface.createDocument(documentSelector,
							newETag, xml, domDocument);
					if (logger.isDebugEnabled())
						logger.debug("document created in data source");
				} else {
					if (attributeSelector != null) {
						// attribute update
						dataSourceSbbInterface.updateAttribute(
								documentSelector, nodeSelector,
								attributeSelector, namespaces, document
										.getETag(), newETag, xml, domDocument,
								attributeValue);
					} else if (nodeSelector != null) {
						// element update
						dataSourceSbbInterface.updateElement(documentSelector,
								nodeSelector, namespaces, document.getETag(),
								newETag, xml, domDocument, newElementAsString,
								newElement);
					} else {
						// whole doc
						dataSourceSbbInterface.updateDocument(documentSelector,
								document.getETag(), newETag, xml, domDocument);
					}
					if (logger.isDebugEnabled())
						logger.debug("document updated in data source");
				}
			} catch (Exception e) {
				logger.error(e);
				throw new InternalServerErrorException(
						"Failed to serialize resulting dom document to string");
			}
			// add it to the result
			result.setResponseEntityTag(newETag);
			// and return that result
			return result;

		} catch (ParseException e) {
			// invalid document selector, throw no parent exception
			if (logger.isDebugEnabled())
				logger
						.debug("failed to parse document selector, returning no parent conflict");
			throw new NoParentConflictException(getDocumentExistingAncestor(
					xcapRoot, documentSelector != null ? documentSelector.getAUID() : null,
					documentSelector != null ? documentSelector
							.getDocumentParent() : e.getValidParent(),
					dataSourceSbbInterface));
		} catch (InterruptedException e) {
			String msg = "failed to borrow app usage object from cache";
			logger.error(msg, e);
			throw new InternalServerErrorException(msg);
		} finally {
			if (appUsage != null) {
				appUsageCache.release(appUsage);
			}
		}
	}

	private String getDocumentExistingAncestor(String xcapRoot, String auid,
			String documentParent, DataSource dataSource)
			throws InternalServerErrorException {
		StringBuilder sb = new StringBuilder(xcapRoot).append('/').append(auid);
		String existingDocumentParent = null;
		if (documentParent != null) {
			if (documentParent.startsWith("/" + auid + "/")) {
				existingDocumentParent = dataSource.getExistingCollection(auid,
						documentParent.substring(3 + auid.length()));
			} else {
				existingDocumentParent = dataSource.getExistingCollection(auid,
						documentParent);
			}
		}
		return existingDocumentParent != null ? sb.append('/').append(
				existingDocumentParent).toString() : sb.toString();
	}

	private String getElementExistentAncestor(String xcapRoot,
			String documentSelector, String elementSelectorWithEmptyPrefix,
			Document document, XPath xpath) {

		// first part is the xcap uri that points to the document
		StringBuilder sb = new StringBuilder(xcapRoot).append(documentSelector);

		// loop till we find an existing ancestor
		String elementAncestor = null;
		int index = -1;
		while ((index = elementSelectorWithEmptyPrefix.lastIndexOf('/')) > 0) {
			elementSelectorWithEmptyPrefix = elementSelectorWithEmptyPrefix
					.substring(0, index);
			try {
				Element element = (Element) xpath.evaluate(
						elementSelectorWithEmptyPrefix, document,
						XPathConstants.NODE);
				if (element != null) {
					elementAncestor = elementSelectorWithEmptyPrefix;
					break;
				}
			} catch (XPathExpressionException e) {
				// silently ignore an invalid xpath expression, specs requires
				// it
			}
		}

		if (elementAncestor != null) {
			// existing element ancestor found
			// remove empty prefixes if those exist
			elementAncestor = elementAncestor.replaceAll("/:", "/");
			// and add it to the ancestor
			sb.append("/~~").append(elementAncestor);
		}

		String ancestor = sb.toString();
		if (logger.isDebugEnabled())
			logger.debug("existing ancestor is " + ancestor);
		return ancestor;
	}

	private Document putNewElementInDocument(Element newElement,
			ElementSelector elementSelector, Element elementParent,
			Document domDocument, NamespaceContext namespaceContext)
			throws InternalServerErrorException, CannotInsertConflictException,
			NotValidXMLFragmentConflictException, NotUTF8ConflictException {

		// get element step
		ElementSelectorStep elementLastStep = elementSelector.getLastStep();

		// get element name & namespace
		String elementNamespace = null;
		String elementName = null;
		String elementNamePrefix = elementLastStep.getPrefix();
		if (elementNamePrefix != null) {
			// get element name without prefix
			elementName = elementLastStep.getNameWithoutPrefix();
			// and get namespace
			elementNamespace = namespaceContext
					.getNamespaceURI(elementNamePrefix);
		} else {
			// get element name without prefix
			elementName = elementLastStep.getName();
			// and get namespace
			elementNamespace = namespaceContext.getNamespaceURI("");
		}

		// if new element node name is not the same as in the uri then cannot
		// insert
		if (!newElement.getNodeName().equals(elementName)) {
			if (logger.isDebugEnabled())
				logger
						.debug("element node name is not the same as in the uri, cannot insert");
			throw new CannotInsertConflictException();
		}

		if (elementLastStep instanceof ElementSelectorStepByPos) {
			// position defined
			if (logger.isDebugEnabled())
				logger
						.debug("element selector's last step with position defined");
			ElementSelectorStepByPos elementSelectorStepByPos = (ElementSelectorStepByPos) elementLastStep;
			if (elementSelectorStepByPos.getPos() == 1) {
				// POS = 1
				if (!(elementLastStep instanceof ElementSelectorStepByPosAttr)) {
					// NO ATTR TEST, *[1] e name[1], either way, just append to
					// the parent
					if (logger.isDebugEnabled())
						logger
								.debug("element selector's last step without attr test defined");
					elementParent.appendChild(newElement);
					if (logger.isDebugEnabled())
						logger.debug("element appended to parent");
				} else {
					// ATTR TEST
					if (logger.isDebugEnabled())
						logger
								.debug("element selector's last step with attr test defined");
					// verify that the element has this step atribute with this
					// step attribute value, if not it cannot insert
					ElementSelectorStepByPosAttr elementSelectorStepByPosAttr = (ElementSelectorStepByPosAttr) elementLastStep;
					String elementAttrName = elementSelectorStepByPosAttr
							.getAttrName();
					String elementAttrValue = newElement
							.getAttribute(elementAttrName);
					if (elementAttrValue == null
							|| !elementAttrValue
									.equals(elementSelectorStepByPosAttr
											.getAttrValue())) {
						if (logger.isDebugEnabled())
							logger
									.debug("element selector's last step has an atribute and the attribute value does not matches, cannot insert");
						throw new CannotInsertConflictException();
					}
					// *[1][attr-test], insert before the first element
					// name[1][attr-test], insert before the first element with
					// same name
					NodeList elementParentChilds = elementParent
							.getChildNodes();
					boolean inserted = false;
					for (int i = 0; i < elementParentChilds.getLength(); i++) {
						if (elementParentChilds.item(i) instanceof Element
								&& ((elementName.equals(elementParentChilds
										.item(i).getNodeName()) && elementParentChilds
										.item(i).getNamespaceURI().equals(
												elementNamespace)) || (elementName
										.equals("*")))) {
							elementParent.insertBefore(newElement,
									elementParentChilds.item(i));
							if (logger.isDebugEnabled())
								logger.debug("element inserted at pos " + i);
							inserted = true;
							break;
						}
					}
					if (!inserted) {
						// didn't found an element just append to parent
						elementParent.appendChild(newElement);
						if (logger.isDebugEnabled())
							logger.debug("element appended to parent");
					}
				}
			}

			else {
				// POS > 1, must find the pos-1 element and insert after
				if (elementLastStep instanceof ElementSelectorStepByPosAttr) {
					// ATTR TEST
					if (logger.isDebugEnabled())
						logger
								.debug("element selector's last step with attr test defined");
					// verify that the element has this step atribute with this
					// step attribute value, if not it cannot insert
					ElementSelectorStepByPosAttr elementSelectorStepByPosAttr = (ElementSelectorStepByPosAttr) elementLastStep;
					String elementAttrName = elementSelectorStepByPosAttr
							.getAttrName();
					String elementAttrValue = newElement
							.getAttribute(elementAttrName);
					if (elementAttrValue == null
							|| !elementAttrValue
									.equals(elementSelectorStepByPosAttr
											.getAttrValue())) {
						if (logger.isDebugEnabled())
							logger
									.debug("element selector's last step has an atribute and the attribute value does not matches, cannot insert");
						throw new CannotInsertConflictException();
					}
				}
				// *[pos>1], name[pos>1], *[pos>1][attr-test],
				// name[pos>1][attr-test], insert in the parent after the pos-1
				// element
				NodeList elementParentChilds = elementParent.getChildNodes();
				boolean inserted = false;
				int elementsFound = 0;
				for (int i = 0; i < elementParentChilds.getLength(); i++) {
					if (elementParentChilds.item(i) instanceof Element
							&& ((elementName.equals(elementParentChilds.item(i)
									.getNodeName()) && elementParentChilds
									.item(i).getNamespaceURI().equals(
											elementNamespace)) || (elementName
									.equals("*")))) {
						elementsFound++;
						if (elementsFound == elementSelectorStepByPos.getPos() - 1) {
							// insert after
							if (i == elementParentChilds.getLength() - 1) {
								// no node after, use append
								elementParent.appendChild(newElement);
								if (logger.isDebugEnabled())
									logger.debug("element appended to parent");
							} else {
								// node after exists, insert before
								elementParent.insertBefore(newElement,
										elementParentChilds.item(i + 1));
								if (logger.isDebugEnabled())
									logger.debug("element inserted at pos " + i
											+ 1);
							}
							inserted = true;
							break;
						}
					}
				}
				if (!inserted) {
					// didn't found pos-1 element, cannot insert
					if (logger.isDebugEnabled())
						logger.debug("didn't found "
								+ (elementSelectorStepByPos.getPos() - 1)
								+ " element, cannot insert");
					throw new CannotInsertConflictException();
				}
			}
		}

		else if (elementLastStep instanceof ElementSelectorStepByAttr) {
			// no position defined
			if (logger.isDebugEnabled())
				logger
						.debug("element selector's last step with attr test defined only");
			// first verify element has this step atribute with this step
			// attribute value, if not it cannot insert
			ElementSelectorStepByAttr elementSelectorStepByAttr = (ElementSelectorStepByAttr) elementLastStep;
			String elementAttrValue = newElement
					.getAttribute(elementSelectorStepByAttr.getAttrName());
			if (elementAttrValue == null
					|| !elementAttrValue.equals(elementSelectorStepByAttr
							.getAttrValue())) {
				if (logger.isDebugEnabled())
					logger
							.debug("element selector's last step has an atribute and the attribute value does not matches, cannot insert");
				throw new CannotInsertConflictException();
			}
			// insert after the last with same name
			NodeList elementParentChilds = elementParent.getChildNodes();
			boolean inserted = false;
			for (int i = elementParentChilds.getLength() - 1; i > -1; i--) {
				if (elementParentChilds.item(i) instanceof Element) {
					if (elementParentChilds.item(i) instanceof Element
							&& ((elementName.equals(elementParentChilds.item(i)
									.getNodeName()) && elementParentChilds
									.item(i).getNamespaceURI().equals(
											elementNamespace)) || (elementName
									.equals("*")))) {
						// insert after this element
						if (i == elementParentChilds.getLength() - 1) {
							elementParent.appendChild(newElement);
							if (logger.isDebugEnabled())
								logger.debug("element appended to parent");
						} else {
							elementParent.insertBefore(newElement,
									elementParentChilds.item(i + 1));
							if (logger.isDebugEnabled())
								logger
										.debug("element inserted at pos " + i
												+ 1);
						}
						inserted = true;
						break;
					}
				}
			}
			if (!inserted) {
				// didn't found an element with same name and namespace, just
				// append to parent
				elementParent.appendChild(newElement);
				if (logger.isDebugEnabled())
					logger.debug("element appended to parent");
			}
		}

		else {
			// no position and attr defined, it's the first child or the first
			// with this name so just append new element
			elementParent.appendChild(newElement);
			if (logger.isDebugEnabled())
				logger
						.debug("element selector's last step without attr test or position defined, element appended to parent");
		}

		return domDocument;

	}

}
