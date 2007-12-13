package org.mobicents.sleetests.resource.http;

import java.rmi.RemoteException;
import java.util.HashMap;

import javax.slee.management.DeployableUnitID;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.GetMethod;

import com.opencloud.sleetck.lib.SleeTCKTest;
import com.opencloud.sleetck.lib.SleeTCKTestUtils;
import com.opencloud.sleetck.lib.TCKTestResult;
import com.opencloud.sleetck.lib.resource.TCKActivityID;
import com.opencloud.sleetck.lib.resource.TCKSbbMessage;
import com.opencloud.sleetck.lib.resource.testapi.TCKResourceListener;
import com.opencloud.sleetck.lib.testutils.BaseTCKResourceListener;
import com.opencloud.sleetck.lib.testutils.FutureResult;


/**
 * Tests that the HTTP RA routes http requests to SBBs. 
 * @author Ivelin Ivanov
 * @author amit.bhayani
 *
 */

public class TestHttpRaTest implements SleeTCKTest {

    private static final String SERVICE_DU_PATH_PARAM = "serviceDUPath";
    //private static final String ACTIVITY_NAME_PARAM ="activityName";
    private static final String HTTP_RA_URL_PARAM = "http-ra-url";
    private static final String TEST_RESULT_BODY = "onGet OK!"; 
    private static final int TEST_ID = 1;

    public void init(SleeTCKTestUtils utils) {
    	this.utils = utils;
    }

    /**
     * Perform the actual test.
     */

    public TCKTestResult run() {
        HttpClient client = new HttpClient();
        client.getHttpConnectionManager().
            getParams().setConnectionTimeout(5000);
        
        String url = utils.getTestParams().getProperty(HTTP_RA_URL_PARAM);   	
        
        GetMethod get = new GetMethod(url);
        get.setFollowRedirects(false);
        
        try {
            int iGetResultCode = client.executeMethod(get);
            if (iGetResultCode != HttpStatus.SC_OK) {
            	return TCKTestResult.failed(1, "URL GET did not return OK status code. Instead it returned status code: " + iGetResultCode);
            };
            
            final String strGetResponseBody = get.getResponseBodyAsString();
            if (!strGetResponseBody.equals(TEST_RESULT_BODY)) {
            	return TCKTestResult.failed(2, "URL GET did not return 'OK!' in the response body. Instead it returned: '" + strGetResponseBody + "'");
            }            
        } catch (Exception ex) {
            ex.printStackTrace();
            return TCKTestResult.failed(3, ex.getMessage());
        } finally {
            get.releaseConnection();
        }
        
        return TCKTestResult.passed();
  
// -- the following lines are only needed when the TCK RA is used.
//    	result = new FutureResult(utils.getLog());
//    	TCKResourceTestInterface resource = utils.getResourceInterface();    	
//		String activityName = utils.getTestParams().getProperty(ACTIVITY_NAME_PARAM);   	
//    	TCKActivityID activityID1 = resource.createActivity(activityName);    	
//    	resource.fireEvent(TCKResourceEventX.X1, TCKResourceEventX.X1, activityID1, null);    	    	
//    	utils.getLog().fine("Event X1 fired, waiting for test pass indicator from SBB.");
//    	return result.waitForResultOrFail(utils.getTestTimeout(), "Timeout waiting for SBB to receive ActivityEndEvent", TEST_ID);    	
    }

    /**
     * Do all the pre-run configuration of the test.
     */

    public void setUp() throws Exception {
		utils.getLog().fine("Connecting to resource");
		resourceListener = new TCKResourceListenerImpl();
		utils.getResourceInterface().setResourceListener(resourceListener);
		utils.getLog().fine("Installing and activating service");
	
		// Install the Deployable Unit.
		String duPath = utils.getTestParams().getProperty(SERVICE_DU_PATH_PARAM);
		DeployableUnitID duID = utils.install(duPath);
		utils.activateServices(duID, true); // Activate the service
    }

    /**
     * Clean up after the test.
     */

    public void tearDown() throws Exception {
		utils.getLog().fine("Disconnecting from resource");
		utils.getResourceInterface().clearActivities();
		utils.getResourceInterface().removeResourceListener();
		utils.getLog().fine("Deactivating and uninstalling service");
		utils.deactivateAllServices();
		utils.uninstallAll();
    }

    private class TCKResourceListenerImpl extends BaseTCKResourceListener {
		public synchronized void onSbbMessage(TCKSbbMessage message, TCKActivityID calledActivity) throws RemoteException {
		    utils.getLog().info("Received message from SBB");
	
		    HashMap map = (HashMap) message.getMessage();
		    Boolean passed = (Boolean) map.get("Result");
		    String msgString = (String) map.get("Message");
	
		    if (passed.booleanValue() == true)
			result.setPassed();
		    else
			result.setFailed(TEST_ID, msgString);
		}

		public void onException(Exception e) throws RemoteException {
		    utils.getLog().warning("Received exception from SBB");
		    utils.getLog().warning(e);
		    result.setError(e);
		}
    }

    private SleeTCKTestUtils utils;
    private TCKResourceListener resourceListener;
    private FutureResult result;
}
