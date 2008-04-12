package org.openxdm.xcap.client;

import java.io.ByteArrayOutputStream;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Callable;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;

import org.openxdm.xcap.client.key.XcapUriKey;

public class MarshallingTask implements Callable<byte[]>{

	ArrayBlockingQueue<Map<String,Marshaller>> marshallersQueue;
	Object content;
	XcapUriKey key;
	boolean fragment;
	
	public MarshallingTask(XcapUriKey key,Object content,boolean fragment,ArrayBlockingQueue<Map<String,Marshaller>> marshallersQueue) {
		this.marshallersQueue = marshallersQueue;
		this.content = content;
		this.key = key;
		this.fragment = fragment;
	}
	
	public byte[] call() throws Exception {
		
		ByteArrayOutputStream baos = new ByteArrayOutputStream(8192);
		
		// get a map of marshallers
		Map<String,Marshaller> marshallers = marshallersQueue.take();
		try {
			// get marshaller for keys's package
			String keyPackageName = content.getClass().getPackage().getName();
			Marshaller marshaller = marshallers.get(keyPackageName);
			if (marshaller == null) {
				// create marshaller
				JAXBContext context = JAXBContext.newInstance(keyPackageName); 
				marshaller = context.createMarshaller();
				// add it to the cache map
				marshallers.put(keyPackageName,marshaller);
			}
			
			marshaller.setProperty(Marshaller.JAXB_FRAGMENT,fragment);						
			marshaller.marshal(content,baos);
		}
		catch (Exception e) {
			throw e;
		}
		finally {
			// return marshallers map to que
			marshallersQueue.put(marshallers);
		}
		
		// return content marshalled
		return baos.toByteArray();
	}

}
