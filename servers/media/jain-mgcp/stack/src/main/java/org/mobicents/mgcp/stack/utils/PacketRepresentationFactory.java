package org.mobicents.mgcp.stack.utils;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

public class PacketRepresentationFactory {

	private static final Logger logger = Logger.getLogger(PacketRepresentationFactory.class);

	private List<PacketRepresentation> list = new ArrayList<PacketRepresentation>();
	private int size = 0;
	private int dataArrSize = 0;
	private int count = 0;

	public PacketRepresentationFactory(int size, int dataArrSize) {
		this.size = size;
		this.dataArrSize = dataArrSize;
		for (int i = 0; i < size; i++) {
			PacketRepresentation pr = new PacketRepresentation(dataArrSize, this); 
			list.add(pr);
		}
	}

	public PacketRepresentation allocate() {
		PacketRepresentation pr = null;

		if (!list.isEmpty()) {
			pr = list.remove(0);
			
		}
		
		if(pr!=null){
			pr.setLength(0);
			return pr;
		}

		pr = new PacketRepresentation(this.dataArrSize, this);
		count++;

		if (logger.isDebugEnabled()) {
			logger.debug("UtilsFactory underflow. Count = " + count);			
		}
		
		logger.error("UtilsFactory underflow. Count = " + count);
		
		return pr;
	}

	public void deallocate(PacketRepresentation pr) {
		if (list.size() < size && pr != null) {
			list.add(pr);
		} else{
			System.out.println("Discarding the PR "+pr);
		}
	}

	public int getSize() {
		return this.size;
	}

	public int getDataArrSize() {
		return this.dataArrSize;
	}

	public int getCount() {
		return this.count;
	}

}
