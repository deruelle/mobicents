package org.openxdm.xcap.client;

import java.io.InputStream;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Callable;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;

import org.openxdm.xcap.client.key.XcapUriKey;

public class UnmarshallingTask implements Callable<Object>{

	ArrayBlockingQueue<Map<String,Unmarshaller>> unmarshallersQueue;
	InputStream in;
	XcapUriKey key;
	
	public UnmarshallingTask(XcapUriKey key,InputStream in,ArrayBlockingQueue<Map<String,Unmarshaller>> unmarshallersQueue) {
		this.unmarshallersQueue = unmarshallersQueue;
		this.in = in;
		this.key = key;
	}
	
	public Object call() throws Exception {
		
		Object contentUnmarshalled = null;
		
		// get a map of unmarshallers
		Map<String,Unmarshaller> unmarshallers = unmarshallersQueue.take();
		try {
		// get unmarshaller for key's package
		String keyPackageName = key.getClass().getPackage().getName();
		Unmarshaller unmarshaller = unmarshallers.get(keyPackageName);
		if (unmarshaller == null) {
			// create unmarshaller
			JAXBContext context = JAXBContext.newInstance(keyPackageName); 
			unmarshaller = context.createUnmarshaller();
			// add it to the cache map
			unmarshallers.put(keyPackageName,unmarshaller);
		}
		// unmarshall content
		contentUnmarshalled = unmarshaller.unmarshal(in);
		}
		catch (Exception e) {
			throw e;
		}
		finally {
			// return unmarshallers map to que
			unmarshallersQueue.put(unmarshallers);
		}
		
		// return content unmarshalled
		return contentUnmarshalled;
	}

}
