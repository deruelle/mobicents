package org.openxdm.xcap.server.slee.resource.appusagecache;

import java.util.concurrent.ArrayBlockingQueue;

import org.openxdm.xcap.common.appusage.AppUsage;
import org.openxdm.xcap.common.appusage.AppUsageFactory;

public class AppUsagePool {

	private ArrayBlockingQueue<AppUsage> queue = null;
		
	public AppUsagePool(AppUsageFactory factory, int queueSize) {
		queue = new ArrayBlockingQueue<AppUsage>(queueSize);
		for(int i=0;i<queueSize;i++) {
			try {
				queue.put(factory.getAppUsageInstance());
			}
			catch (InterruptedException e) {
				// shouldn't happen
				e.printStackTrace();
			}
		}
	}
	
	public AppUsage borrow() throws InterruptedException {
		return queue.take();		
	}
	
	public void release(AppUsage appUsage) {
		queue.offer(appUsage);		
	}
}
