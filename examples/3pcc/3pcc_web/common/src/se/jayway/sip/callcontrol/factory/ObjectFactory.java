package se.jayway.sip.callcontrol.factory;

import java.util.ResourceBundle;

import javax.xml.rpc.ServiceException;

import se.jayway.sip.callcontrol.client.service.impl.ParlayXServiceImplWrapper;
import se.jayway.sip.callcontrol.exception.ConfigurationException;
import se.jayway.sip.callcontrol.exception.GeneralException;
import se.jayway.sip.callcontrol.exception.slee.SleeConnectionException;
import se.jayway.sip.callcontrol.service.CallControlService;
import se.jayway.sip.callcontrol.service.ExceptionErrorDefinitions;
import se.jayway.sip.callcontrol.service.impl.SleeConnectionServiceImpl;

/**
 * Factory for retrieving SIP session related objects
 * 
 * @author Niklas Uhrberg, Jacob Mattsson, Johan Haleby
 * 
 */
public class ObjectFactory implements ExceptionErrorDefinitions {
	
	/**
	 * The path where to find the config bundle
	 */
	private static final String CONFIG_PATH = "META-INF.callservice";
	
	/**
	 * The singleton instance of this class.
	 */
	private static ObjectFactory instance;
	
	/**
	 * A string representing the call service to use, loaded from bundle.
	 */
	private String callService; 
	
	/**
	 * The service impl to use.
	 */
	private CallControlService sleeConnectionServiceImpl;


	/**
	 * Make the constructor private since instance is retrieved using
	 * #getIntance().
	 */
	private ObjectFactory() {
		loadConfig();
	}
	
	/**
	 * Returns the singleton instance of this class.
	 * 
	 * @return The singleton instance of this class
	 */
	public static synchronized ObjectFactory getInstance() {
		if (instance == null)
			instance = new ObjectFactory();
		return instance;
	}
	
	/**
	 * Return the service impl. to use.
	 * 
	 * @return the service impl. to use.
	 * @throws SleeConnectionException 
	 * @throws ConfigurationException
	 * @throws GeneralException 
	 */
	public CallControlService getCallControlService() throws SleeConnectionException, ConfigurationException, GeneralException {
		return getSleeConnectionServiceImpl();
	}
	
	/**
	 * @return a textual representation of the service impl. to use.
	 */
	public String getCallControlServiceAsString() {
		return callService;
	}
	
	/**
	 * Load call service string from bundle.
	 */
	private void loadConfig() {
		ResourceBundle rb = ResourceBundle.getBundle(CONFIG_PATH);		
		callService = rb.getString("callService");
	}
	
	/**
	 * Return the service impl. to use.
	 * 
	 * @return the service impl. to use.
	 * @throws SleeConnectionException
	 * @throws ConfigurationException
	 */
	private CallControlService getSleeConnectionServiceImpl() throws SleeConnectionException, ConfigurationException, GeneralException {
		if(sleeConnectionServiceImpl == null) {
			if("WebService".equals(callService)) {
				try {
					sleeConnectionServiceImpl = new ParlayXServiceImplWrapper();
				} catch (ServiceException e) {
					throw new GeneralException("Failed to access web service "+e);
				}
			} else if("Servlet".equals(callService)) {
				sleeConnectionServiceImpl = new SleeConnectionServiceImpl();
			} else
				throw new ConfigurationException(ERROR_CONFIGURATION_INVALID_SERVICE + callService);
		}
		return sleeConnectionServiceImpl;
	}
	
}
