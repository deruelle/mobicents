package org.openxdm.xcap.client;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.HttpVersion;
import org.apache.commons.httpclient.MultiThreadedHttpConnectionManager;
import org.apache.commons.httpclient.methods.ByteArrayRequestEntity;
import org.apache.commons.httpclient.methods.DeleteMethod;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PutMethod;
import org.apache.commons.httpclient.methods.StringRequestEntity;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openxdm.xcap.client.error.XcapError;
import org.openxdm.xcap.client.key.AttributeUriKey;
import org.openxdm.xcap.client.key.DocumentUriKey;
import org.openxdm.xcap.client.key.ElementUriKey;
import org.openxdm.xcap.client.key.NamespaceBindingsUriKey;
import org.openxdm.xcap.client.key.XcapUriKey;
import org.openxdm.xcap.common.http.HttpConstant;
import org.openxdm.xcap.common.resource.AttributeResource;
import org.openxdm.xcap.common.resource.ElementResource;

public class XCAPClientImpl implements XCAPClient {
	
	private static final Log log = LogFactory.getLog(XCAPClientImpl.class);
	private int SIZE_OF_QUEUES = 10;
	
	private ArrayBlockingQueue<Map<String,Marshaller>> marshallersQueue;
	private ArrayBlockingQueue<Map<String,Unmarshaller>> unmarshallersQueue;
	private ArrayBlockingQueue<Unmarshaller> errorUnmarshallersQueue;
	private ExecutorService executorService; 
		
	private HttpClient client;
	private String xcapRoot;
	private boolean unmarshallingResponseError = false; 
	
	public XCAPClientImpl(String host,int port,String xcapRoot,int maxConcurrentThreads) throws InterruptedException {		
		this.xcapRoot = xcapRoot;
		SIZE_OF_QUEUES = maxConcurrentThreads;
		// create http client       
		client = new HttpClient(new MultiThreadedHttpConnectionManager());
      	// configure for relative path requests
      	client.getHostConfiguration().setHost(host, port, "http");
      	// set params
      	client.getParams().setParameter("http.protocol.version", HttpVersion.HTTP_1_1);
      	//client.getParams().setParameter("http.socket.timeout", new Integer(10000));
      	client.getParams().setParameter("http.protocol.content-charset", "UTF-8");      	
      	// init marshallers and unmarshallers queues and executor service      	
      	marshallersQueue = new ArrayBlockingQueue<Map<String,Marshaller>>(SIZE_OF_QUEUES);
      	for(int i=0;i<SIZE_OF_QUEUES;i++){
      		marshallersQueue.put(new HashMap<String,Marshaller>());
      	}
      	unmarshallersQueue = new ArrayBlockingQueue<Map<String,Unmarshaller>>(SIZE_OF_QUEUES);
      	for(int i=0;i<SIZE_OF_QUEUES;i++){
      		unmarshallersQueue.put(new HashMap<String,Unmarshaller>());
      	}      	      	
      	executorService = Executors.newCachedThreadPool();
	}
	
	
	// CLIENT MANAGEMENT
	
	public void shutdown() {		
		log.info("shutdown()");
		if(client != null) {
			((MultiThreadedHttpConnectionManager)client.getHttpConnectionManager()).shutdown();
		}
		if (executorService != null) {
			executorService.shutdown();
		}
		marshallersQueue = null;
		unmarshallersQueue = null;
		errorUnmarshallersQueue = null;
	}
	
	public void setUnmarshallingResponseError(boolean value) throws JAXBException {
		log.info("setUnmarshallingResponseError(value="+value+")");
		
		if(value) {
			try {
				// create error unmarshaller queue      	
				JAXBContext context = JAXBContext.newInstance(XcapError.class.getPackage().getName());
				errorUnmarshallersQueue = new ArrayBlockingQueue<Unmarshaller>(SIZE_OF_QUEUES);
				for(int i=0;i<SIZE_OF_QUEUES;i++){
					errorUnmarshallersQueue.put(context.createUnmarshaller());
				}
			}
			catch (Exception e) {
				errorUnmarshallersQueue = null;
				throw new JAXBException(e);
			}
		} else {
			errorUnmarshallersQueue = null;
		}
		this.unmarshallingResponseError = value;		
	}
	
	public boolean isUnmarshallingResponseError() {
		return unmarshallingResponseError;
	}
	
	// CLIENT OPERATIONS 
	
	private String addXcapRoot(String path) {
		return new StringBuilder(xcapRoot).append(path).toString();
	}
		
	private Response getResponse(XcapUriKey key, HttpMethod method, boolean unmarshall) throws JAXBException, IOException {		
		
		try {
			// get status code
			int statusCode = method.getStatusCode();
			// get headers
			Header[] headers = method.getResponseHeaders();
			Object content = null;
			String eTag = null;
			
			if(statusCode == 200 || statusCode == 201) {
				// sucessful response
				if(unmarshall) {				
					// create task to unmarshall
					UnmarshallingTask task = new UnmarshallingTask(key,method.getResponseBodyAsStream(),unmarshallersQueue);
					// and execute it
					content = executorService.submit(task).get();
					
				} else {
					// get content as string
					content = method.getResponseBodyAsString();
				}
				// get etag			
				Header eTagHeader = method.getResponseHeader(HttpConstant.HEADER_ETAG);
				if(eTagHeader  != null) {
					eTag = eTagHeader.getValue();
				}			
			}
			
			else if (statusCode == 409) {			
				// conflict error, unmarshal error content if required and content exists
				if (unmarshallingResponseError) {
					InputStream in = method.getResponseBodyAsStream();
					if (in != null) {
						
						Unmarshaller errorUnmarshaller = null;					
						// get a error unmarshaller from the queue					
						errorUnmarshallersQueue.take();
						try {	
							// unmarshall error
							content = errorUnmarshaller.unmarshal(in);						
						}
						catch (RuntimeException e) {
							throw e;
						}
						catch (JAXBException e) {
							throw e;
						}
						finally {
							// put it back to the queue
							errorUnmarshallersQueue.put(errorUnmarshaller);
						}
					}			
				}
				else {
					content = method.getResponseBodyAsString();
				}
			}
			
			Response response = new Response(statusCode,eTag,headers,content,unmarshall); 
			log.info("Received:\n--BEGIN--\n"+response.toString()+"\n--END--");		
			return response;
		}
		catch (InterruptedException ie) {
			// something bad on the queues
			throw new JAXBException(ie);
		}
		catch (ExecutionException ee) {
			// something bad on the queues
			throw new JAXBException(ee);
		}
	}
	
	private Response get(XcapUriKey key,boolean unmarshall) throws HttpException, IOException, JAXBException {
		
		Response response = null;
		
		// create method with key uri
		GetMethod get = new GetMethod(addXcapRoot(key.toString()));
		
		try {
        	// execute method
            client.executeMethod(get);
            // get response
            response = getResponse(key,get,unmarshall);            
        }                
        finally {
            // be sure the connection is released back to the connection manager
            get.releaseConnection();
        }		
				
		return response;
	}

	public Response get(XcapUriKey key) throws HttpException, IOException, JAXBException {		
		log.info("get(key="+key+")");
		return get(key,false);
	}
	
	public Response getAndUnmarshallDocument(DocumentUriKey key) throws HttpException, IOException, JAXBException {		
		log.info("getAndUnmarshallDocument(key="+key+")");
		return get(key,true);				
	}
	
	public Response getAndUnmarshallElement(ElementUriKey key) throws HttpException, IOException, JAXBException {		
		log.info("getAndUnmarshallElement(key="+key+")");
		return get(key,true);				
	}
	
	public Response getAndUnmarshallNamespaceBindings(NamespaceBindingsUriKey key) throws HttpException, IOException, JAXBException {
		log.info("getAndUnmarshallNamespaceBindings(key="+key+")");
		return get(key,true);				
	}
	
	private byte[] marshall(XcapUriKey key,Object content,Boolean fragment) throws JAXBException {
		
		if(log.isDebugEnabled()) {
			log.info("marshall(key="+key+",fragment="+fragment+",content="+content+")");
		}
		
		// create task to marshall
		MarshallingTask task = new MarshallingTask(key,content,fragment,marshallersQueue);
		
		try {
			// execute and return result
			return executorService.submit(task).get();
		} catch (InterruptedException e) {
			throw new JAXBException(e);
		} catch (ExecutionException e) {
			throw new JAXBException(e);
		}
		
	}
	
	public Response marshallAndPutDocument(DocumentUriKey key, String mimetype, Object content) throws JAXBException, HttpException, IOException {		
		return put(key,mimetype,marshall(key,content,Boolean.FALSE));
	}
	
	public Response marshallAndPutDocumentIfMatch(DocumentUriKey key, String eTag, String mimetype, Object content) throws JAXBException, HttpException, IOException {
		return putIfMatch(key,eTag,mimetype,marshall(key,content,Boolean.FALSE));
	}
	
	public Response marshallAndPutDocumentIfNoneMatch(DocumentUriKey key, String eTag, String mimetype, Object content) throws JAXBException, HttpException, IOException {
		return putIfNoneMatch(key,eTag,mimetype,marshall(key,content,Boolean.FALSE));
	}
	
	public Response marshallAndPutElement(ElementUriKey key, Object content) throws JAXBException, HttpException, IOException {		
		return put(key,ElementResource.MIMETYPE,marshall(key,content,Boolean.TRUE));
	}
	
	public Response marshallAndPutElementIfMatch(ElementUriKey key, String eTag, Object content) throws JAXBException, HttpException, IOException {
		return putIfMatch(key,eTag,ElementResource.MIMETYPE,marshall(key,content,Boolean.TRUE));
	}
	
	public Response marshallAndPutElementIfNoneMatch(ElementUriKey key, String eTag, Object content) throws JAXBException, HttpException, IOException {
		return putIfNoneMatch(key,eTag,ElementResource.MIMETYPE,marshall(key,content,Boolean.TRUE));
	}
	
	public Response putDocument(DocumentUriKey key, String mimetype, String content) throws HttpException, IOException, JAXBException {		
		return put(key,mimetype,content);
	}

	public Response putDocumentIfMatch(DocumentUriKey key, String eTag, String mimetype, String content) throws HttpException, IOException, JAXBException {
		return putIfMatch(key,eTag,mimetype,content);
	}

	public Response putDocumentIfNoneMatch(DocumentUriKey key, String eTag, String mimetype, String content) throws HttpException, IOException, JAXBException {
		return putIfNoneMatch(key,eTag,mimetype,content);
	}

	public Response putDocument(DocumentUriKey key, String mimetype, byte[] content) throws HttpException, IOException, JAXBException {
		return put(key,mimetype,content);
	}

	public Response putDocumentIfMatch(DocumentUriKey key, String eTag, String mimetype, byte[] content) throws HttpException, IOException, JAXBException {
		return putIfMatch(key,eTag,mimetype,content);
	}

	public Response putDocumentIfNoneMatch(DocumentUriKey key, String eTag, String mimetype, byte[] content) throws HttpException, IOException, JAXBException {
		return putIfNoneMatch(key,eTag,mimetype,content);
	}

	public Response putElement(ElementUriKey key, String content) throws HttpException, IOException, JAXBException {
		return put(key,ElementResource.MIMETYPE,content);
	}

	public Response putElementIfMatch(ElementUriKey key, String eTag, String content) throws HttpException, IOException, JAXBException {
		return putIfMatch(key,eTag,ElementResource.MIMETYPE,content);
	}

	public Response putElementIfNoneMatch(ElementUriKey key, String eTag, String content) throws HttpException, IOException, JAXBException {
		return putIfNoneMatch(key,eTag,ElementResource.MIMETYPE,content);
	}

	public Response putElement(ElementUriKey key, byte[] content) throws HttpException, IOException, JAXBException {
		return put(key,ElementResource.MIMETYPE,content);
	}

	public Response putElementIfMatch(ElementUriKey key, String eTag, byte[] content) throws HttpException, IOException, JAXBException {
		return putIfMatch(key,eTag,ElementResource.MIMETYPE,content);
	}

	public Response putElementIfNoneMatch(ElementUriKey key, String eTag, byte[] content) throws HttpException, IOException, JAXBException {
		return putIfNoneMatch(key,eTag,ElementResource.MIMETYPE,content);
	}

	public Response putAttribute(AttributeUriKey key, String content) throws HttpException, IOException, JAXBException {
		return put(key,AttributeResource.MIMETYPE,content);
	}

	public Response putAttributeIfMatch(AttributeUriKey key, String eTag, String content) throws HttpException, IOException, JAXBException {
		return putIfMatch(key,eTag,AttributeResource.MIMETYPE,content);
	}

	public Response putAttributeIfNoneMatch(AttributeUriKey key, String eTag, String content) throws HttpException, IOException, JAXBException {
		return putIfNoneMatch(key,eTag,AttributeResource.MIMETYPE,content);
	}

	public Response putAttribute(AttributeUriKey key, byte[] content) throws HttpException, IOException, JAXBException {
		return put(key,AttributeResource.MIMETYPE,content);
	}

	public Response putAttributeIfMatch(AttributeUriKey key, String eTag, byte[] content) throws HttpException, IOException, JAXBException {
		return putIfMatch(key,eTag,AttributeResource.MIMETYPE,content);
	}

	public Response putAttributeIfNoneMatch(AttributeUriKey key, String eTag, byte[] content) throws HttpException, IOException, JAXBException {
		return putIfNoneMatch(key,eTag,AttributeResource.MIMETYPE,content);
	}	
	
	private Response put(XcapUriKey key, String mimetype, String content) throws HttpException, IOException, JAXBException {
		
		log.info("put(key="+key+", mimetype="+mimetype+", content="+content+")");
		
		Response response = null;
		
		// create method with key uri
		PutMethod put = new PutMethod(addXcapRoot(key.toString()));
		// set mimetype
		put.setRequestHeader(HttpConstant.HEADER_CONTENT_TYPE,mimetype);
		// set content
		put.setRequestEntity(new StringRequestEntity(content));
        try {
        	// execute method
            client.executeMethod(put);
            // get response
            response = getResponse(key,put,false);            
        }               
        finally {
            // be sure the connection is released back to the connection manager
            put.releaseConnection();
        }		
				
		return response;
								
	}
	
	private Response putIfMatch(XcapUriKey key, String eTag, String mimetype, String content)  throws HttpException, IOException, JAXBException {
		
		log.info("putIfMatch(key="+key+", eTag="+eTag+", mimetype="+mimetype+", content="+content+")");
		
		Response response = null;
		
		// create method with key uri
		PutMethod put = new PutMethod(addXcapRoot(key.toString()));
		// set mimetype
		put.setRequestHeader(HttpConstant.HEADER_CONTENT_TYPE,mimetype);
		// set if match
		put.setRequestHeader(HttpConstant.HEADER_IF_MATCH,eTag);
		// set content
		put.setRequestEntity(new StringRequestEntity(content));
        try {
        	// execute method
            client.executeMethod(put);
            // get response
            response = getResponse(key,put,false);            
        }               
        finally {
            // be sure the connection is released back to the connection manager
            put.releaseConnection();
        }		
				
		return response;
	}
	
	private Response putIfNoneMatch(XcapUriKey key, String eTag, String mimetype, String content) throws HttpException, IOException, JAXBException {
		
		log.info("putIfNoneMatch(key="+key+", eTag="+eTag+", mimetype="+mimetype+", content="+content+")");
		
		Response response = null;
		
		// create method with key uri
		PutMethod put = new PutMethod(addXcapRoot(key.toString()));
		// set mimetype
		put.setRequestHeader(HttpConstant.HEADER_CONTENT_TYPE,mimetype);
		// set if match
		put.setRequestHeader(HttpConstant.HEADER_IF_NONE_MATCH,eTag);
		// set content
		put.setRequestEntity(new StringRequestEntity(content));
        try {
        	// execute method
            client.executeMethod(put);
            // get response
            response = getResponse(key,put,false);            
        }
        finally {
            // be sure the connection is released back to the connection manager
            put.releaseConnection();
        }		
				
		return response;
	}		
	
	private Response put(XcapUriKey key, String mimetype, byte[] content) throws HttpException, IOException, JAXBException {
		
		log.info("put(key="+key+", mimetype="+mimetype+", content="+content+")");
		
		Response response = null;
		
		// create method with key uri
		PutMethod put = new PutMethod(addXcapRoot(key.toString()));
		// set mimetype
		put.setRequestHeader(HttpConstant.HEADER_CONTENT_TYPE,mimetype);		
		// set content
		put.setRequestEntity(new ByteArrayRequestEntity(content));
        try {
        	// execute method
            client.executeMethod(put);
            // get response
            response = getResponse(key,put,false);            
        }
        finally {
            // be sure the connection is released back to the connection manager
            put.releaseConnection();
        }
		
		return response;
		
	}
	
	private Response putIfMatch(XcapUriKey key, String eTag, String mimetype, byte[] content) throws HttpException, IOException, JAXBException {
		
		log.info("putIfMatch(key="+key+", eTag="+eTag+", mimetype="+mimetype+", content="+content+")");			
		
		Response response = null;
		
		// create method with key uri
		PutMethod put = new PutMethod(addXcapRoot(key.toString()));
		// set mimetype
		put.setRequestHeader(HttpConstant.HEADER_CONTENT_TYPE,mimetype);
		// set if match
		put.setRequestHeader(HttpConstant.HEADER_IF_MATCH,eTag);
		// set content
		put.setRequestEntity(new ByteArrayRequestEntity(content));
        try {
        	// execute method
            client.executeMethod(put);
            // get response
            response = getResponse(key,put,false);            
        }
        finally {
            // be sure the connection is released back to the connection manager
            put.releaseConnection();
        }
		
		return response;
	}
	
	private Response putIfNoneMatch(XcapUriKey key, String eTag, String mimetype, byte[] content) throws HttpException, IOException, JAXBException {
		
		log.info("putIfNoneMatch(key="+key+", eTag="+eTag+", mimetype="+mimetype+", content="+content+")");
		
		Response response = null;
		
		// create method with key uri
		PutMethod put = new PutMethod(addXcapRoot(key.toString()));
		// set mimetype
		put.setRequestHeader(HttpConstant.HEADER_CONTENT_TYPE,mimetype);
		// set if match
		put.setRequestHeader(HttpConstant.HEADER_IF_NONE_MATCH,eTag);
		// set content
		put.setRequestEntity(new ByteArrayRequestEntity(content));
        try {
        	// execute method
            client.executeMethod(put);
            // get response
            response = getResponse(key,put,false);            
        }
        finally {
            // be sure the connection is released back to the connection manager
            put.releaseConnection();
        }
		
		return response;
	}
	
	private Response delete(XcapUriKey key) throws HttpException, IOException, JAXBException {
		
		log.info("delete(key="+key+")");
		
		Response response = null;
		
		// create method with key uri
		DeleteMethod delete = new DeleteMethod(addXcapRoot(key.toString()));
		
        try {        	
        	// execute method
            client.executeMethod(delete);
            // get response
            response = getResponse(key,delete,false);            
        }               
        finally {
            // be sure the connection is released back to the connection manager
            delete.releaseConnection();
        }		
				
		return response;
		
	}
	
	private Response deleteIfMatch(XcapUriKey key, String eTag) throws HttpException, IOException, JAXBException {

		log.info("deleteIfMatch(key="+key+", eTag="+eTag+")");
		
		Response response = null;
		
		// create method with key uri
		DeleteMethod delete = new DeleteMethod(addXcapRoot(key.toString()));
		
        try {
        	// set if match
        	delete.setRequestHeader(HttpConstant.HEADER_IF_MATCH,eTag);
        	// execute method
            client.executeMethod(delete);
            // get response
            response = getResponse(key,delete,false);            
        }               
        finally {
            // be sure the connection is released back to the connection manager
            delete.releaseConnection();
        }		
				
		return response;
	}
	
	private Response deleteIfNoneMatch(XcapUriKey key, String eTag) throws HttpException, IOException, JAXBException {
		
		log.info("deleteIfNoneMatch(key="+key+", eTag="+eTag+")");
		
		Response response = null;
		
		// create method with key uri
		DeleteMethod delete = new DeleteMethod(addXcapRoot(key.toString()));
		
        try {
        	// set if match
        	delete.setRequestHeader(HttpConstant.HEADER_IF_NONE_MATCH,eTag);
        	// execute method
            client.executeMethod(delete);
            // get response
            response = getResponse(key,delete,false);            
        }               
        finally {
            // be sure the connection is released back to the connection manager
            delete.releaseConnection();
        }		
				
		return response;
	}


	public Response deleteDocument(DocumentUriKey key) throws HttpException, IOException, JAXBException {
		return delete(key);
	}


	public Response deleteDocumentIfMatch(DocumentUriKey key, String eTag) throws HttpException, IOException, JAXBException {
		return deleteIfMatch(key,eTag);
	}


	public Response deleteDocumentIfNoneMatch(DocumentUriKey key, String eTag) throws HttpException, IOException, JAXBException {
		return deleteIfNoneMatch(key,eTag);
	}


	public Response deleteElement(ElementUriKey key) throws HttpException, IOException, JAXBException {
		return delete(key);
	}


	public Response deleteElementIfMatch(ElementUriKey key, String eTag) throws HttpException, IOException, JAXBException {
		return deleteIfMatch(key,eTag);
	}


	public Response deleteElementIfNoneMatch(ElementUriKey key, String eTag) throws HttpException, IOException, JAXBException {
		return deleteIfNoneMatch(key,eTag);
	}


	public Response deleteAttribute(AttributeUriKey key) throws HttpException, IOException, JAXBException {
		return delete(key);
	}


	public Response deleteAttributeIfMatch(AttributeUriKey key, String eTag) throws HttpException, IOException, JAXBException {
		return deleteIfMatch(key,eTag);
	}


	public Response deleteAttributeIfNoneMatch(AttributeUriKey key, String eTag) throws HttpException, IOException, JAXBException {
		return deleteIfNoneMatch(key,eTag);
	}
	
}
