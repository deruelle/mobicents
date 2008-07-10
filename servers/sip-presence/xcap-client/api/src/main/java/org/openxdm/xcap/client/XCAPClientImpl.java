package org.openxdm.xcap.client;

import java.io.IOException;

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
import org.openxdm.xcap.common.http.HttpConstant;
import org.openxdm.xcap.common.key.XcapUriKey;

public class XCAPClientImpl implements XCAPClient {
	
	private static final Log log = LogFactory.getLog(XCAPClientImpl.class);
	
	private HttpClient client;
	private String xcapRoot;
	
	public XCAPClientImpl(String host,int port,String xcapRoot) throws InterruptedException {		
		this.xcapRoot = xcapRoot;
		// create http client       
		client = new HttpClient(new MultiThreadedHttpConnectionManager());
      	// configure for relative path requests
      	client.getHostConfiguration().setHost(host, port, "http");
      	// set params
      	client.getParams().setParameter("http.protocol.version", HttpVersion.HTTP_1_1);
      	//client.getParams().setParameter("http.socket.timeout", new Integer(10000));
      	client.getParams().setParameter("http.protocol.content-charset", "UTF-8");      	
	}
		
	// CLIENT MANAGEMENT
	
	public void shutdown() {		
		log.info("shutdown()");
		if(client != null) {
			((MultiThreadedHttpConnectionManager)client.getHttpConnectionManager()).shutdown();
		}
	}
	
	// CLIENT OPERATIONS 
	
	private String addXcapRoot(String path) {
		return xcapRoot + path;
	}
		
	private Response getResponse(XcapUriKey key, HttpMethod method) throws IOException {		
		
		// get status code
		int statusCode = method.getStatusCode();
		// get headers
		Header[] headers = method.getResponseHeaders();
		String content = null;
		String eTag = null;

		if(statusCode == 200 || statusCode == 201) {
			// sucessful response
			// get content as string
			content = method.getResponseBodyAsString();
			// get etag			
			Header eTagHeader = method.getResponseHeader(HttpConstant.HEADER_ETAG);
			if(eTagHeader  != null) {
				eTag = eTagHeader.getValue();
			}			
		}
		else if (statusCode == 409) {			
			content = method.getResponseBodyAsString();
		}

		Response response = new Response(statusCode,eTag,headers,content); 
		if (log.isDebugEnabled()) {
			log.debug("Received:\n--BEGIN--\n"+response.toString()+"\n--END--");
		}
		return response;
		
	}

	public Response get(XcapUriKey key) throws HttpException, IOException {		
		if (log.isDebugEnabled()) {
			log.debug("get(key="+key+")");
		}
		
		Response response = null;
		
		// create method with key uri
		GetMethod get = new GetMethod(addXcapRoot(key.toString()));
		
		try {
        	// execute method
            client.executeMethod(get);
            // get response
            response = getResponse(key,get);            
        }                
        finally {
            // be sure the connection is released back to the connection manager
            get.releaseConnection();
        }		
				
		return response;
	}
	
	public Response put(XcapUriKey key, String mimetype, String content) throws HttpException, IOException {
		
		if (log.isDebugEnabled()) {
			log.debug("put(key="+key+", mimetype="+mimetype+", content="+content+")");
		}
		
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
            response = getResponse(key,put);            
        }               
        finally {
            // be sure the connection is released back to the connection manager
            put.releaseConnection();
        }		
				
		return response;
								
	}
	
	public Response putIfMatch(XcapUriKey key, String eTag, String mimetype, String content)  throws HttpException, IOException {
		
		if (log.isDebugEnabled()) {
			log.debug("putIfMatch(key="+key+", eTag="+eTag+", mimetype="+mimetype+", content="+content+")");
		}
		
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
            response = getResponse(key,put);            
        }               
        finally {
            // be sure the connection is released back to the connection manager
            put.releaseConnection();
        }		
				
		return response;
	}
	
	public Response putIfNoneMatch(XcapUriKey key, String eTag, String mimetype, String content) throws HttpException, IOException {
		
		if (log.isDebugEnabled()) {
			log.debug("putIfNoneMatch(key="+key+", eTag="+eTag+", mimetype="+mimetype+", content="+content+")");
		}
		
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
            response = getResponse(key,put);            
        }
        finally {
            // be sure the connection is released back to the connection manager
            put.releaseConnection();
        }		
				
		return response;
	}		
	
	public Response put(XcapUriKey key, String mimetype, byte[] content) throws HttpException, IOException {
		
		if (log.isDebugEnabled()) {
			log.debug("put(key="+key+", mimetype="+mimetype+", content="+content+")");
		}
		
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
            response = getResponse(key,put);            
        }
        finally {
            // be sure the connection is released back to the connection manager
            put.releaseConnection();
        }
		
		return response;
		
	}
	
	public Response putIfMatch(XcapUriKey key, String eTag, String mimetype, byte[] content) throws HttpException, IOException {
		
		if (log.isDebugEnabled()) {
			log.debug("putIfMatch(key="+key+", eTag="+eTag+", mimetype="+mimetype+", content="+content+")");			
		}
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
            response = getResponse(key,put);            
        }
        finally {
            // be sure the connection is released back to the connection manager
            put.releaseConnection();
        }
		
		return response;
	}
	
	public Response putIfNoneMatch(XcapUriKey key, String eTag, String mimetype, byte[] content) throws HttpException, IOException {
		
		if (log.isDebugEnabled()) {
			log.debug("putIfNoneMatch(key="+key+", eTag="+eTag+", mimetype="+mimetype+", content="+content+")");
		}
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
            response = getResponse(key,put);            
        }
        finally {
            // be sure the connection is released back to the connection manager
            put.releaseConnection();
        }
		
		return response;
	}
	
	public Response delete(XcapUriKey key) throws HttpException, IOException {
		
		if (log.isDebugEnabled()) {
			log.debug("delete(key="+key+")");
		}
		Response response = null;
		
		// create method with key uri
		DeleteMethod delete = new DeleteMethod(addXcapRoot(key.toString()));
		
        try {        	
        	// execute method
            client.executeMethod(delete);
            // get response
            response = getResponse(key,delete);            
        }               
        finally {
            // be sure the connection is released back to the connection manager
            delete.releaseConnection();
        }		
				
		return response;
		
	}
	
	public Response deleteIfMatch(XcapUriKey key, String eTag) throws HttpException, IOException {

		if (log.isDebugEnabled()) {
			log.debug("deleteIfMatch(key="+key+", eTag="+eTag+")");
		}
		Response response = null;
		
		// create method with key uri
		DeleteMethod delete = new DeleteMethod(addXcapRoot(key.toString()));
		
        try {
        	// set if match
        	delete.setRequestHeader(HttpConstant.HEADER_IF_MATCH,eTag);
        	// execute method
            client.executeMethod(delete);
            // get response
            response = getResponse(key,delete);            
        }               
        finally {
            // be sure the connection is released back to the connection manager
            delete.releaseConnection();
        }		
				
		return response;
	}
	
	public Response deleteIfNoneMatch(XcapUriKey key, String eTag) throws HttpException, IOException {
		
		if (log.isDebugEnabled()) {
			log.debug("deleteIfNoneMatch(key="+key+", eTag="+eTag+")");
		}
		Response response = null;
		
		// create method with key uri
		DeleteMethod delete = new DeleteMethod(addXcapRoot(key.toString()));
		
        try {
        	// set if match
        	delete.setRequestHeader(HttpConstant.HEADER_IF_NONE_MATCH,eTag);
        	// execute method
            client.executeMethod(delete);
            // get response
            response = getResponse(key,delete);            
        }               
        finally {
            // be sure the connection is released back to the connection manager
            delete.releaseConnection();
        }		
				
		return response;
	}
	
}
