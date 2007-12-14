package org.mobicents.slee.services.sip.registrar;

import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.sip.ServerTransaction;
import javax.sip.address.Address;
import javax.sip.address.URI;
import javax.sip.header.CSeqHeader;
import javax.sip.header.CallIdHeader;
import javax.sip.header.ContactHeader;
import javax.sip.header.DateHeader;
import javax.sip.header.ExpiresHeader;
import javax.sip.header.Header;
import javax.sip.header.HeaderAddress;
import javax.sip.header.ToHeader;
import javax.sip.message.Request;
import javax.sip.message.Response;
import javax.slee.ActivityContextInterface;
import javax.slee.facilities.TimerID;
import javax.slee.facilities.TimerOptions;
import javax.slee.nullactivity.NullActivity;

/**
 * 
 * This is working mule of registrar service, it does everything. It depends.
 * <br>
 * See methods desc to see on what they are dependant. Some just need sip stuff,
 * other are SLEE dependant, If You want to use this code You will have to
 * provide different impl.
 * 
 * @author F.Moggia
 * @author baranowb
 */
public class Registrar {
	private LocationService ls;

	private long minExpires = 10; // seconds

	private long maxExpires = 7200; // seconds

	// TODO: CONF NO HARDCODE
	private String[] localDomainNames = { "nist.gov", "antd.nist.gov",
			"nist.gov;transport=udp" };

	// Object that implements ResourcesProvider Interface
	protected Object resourcesProvider;

	private static Logger logger = Logger.getLogger(Registrar.class.getName());

	public Registrar(Object resourcesProvider) {
		this.resourcesProvider = resourcesProvider;
		ls = new LocationService();

	}

	public String getDomain(URI uri) {
		String address = uri.toString();

		// get rid of user part
		int index = address.indexOf('@');
		if (index != -1)
			address = address.substring(index + 1);

		// get rid of protocol part
		index = address.indexOf(':');
		if (index != -1)
			address = address.substring(index + 1);

		// get rid of port and all that comes after
		index = address.indexOf(':');
		if (index != -1)
			address = address.substring(0, index);

		return address;
	}

	private LocationService getLocationService() {
		return ls;
	}

	public boolean isLocalDomain(URI uri) {
		/*
		 * boolean match = false; String uriDomain = getDomain(uri); for (int i =
		 * 0; i < localDomainNames.length; i++ ) { match =
		 * localDomainNames[i].equalsIgnoreCase(uriDomain); if (match) break; }
		 * 
		 * return match;
		 */
		return true;
	}

	public static String getCanonicalAddress(HeaderAddress header) {
		Address na = header.getAddress();

		URI uri = na.getURI();

		String addr = uri.toString();

		int index = addr.indexOf(':');
		index = addr.indexOf(':', index + 1);
		if (index != -1) {
			// Get rid of the port
			addr = addr.substring(0, index);
		}

		return addr;
	}

	public void processRequest(ServerTransaction txn, Request request) {

		// RFC3261 ch10.3
		try {

			// Is this request for this domain?
			if (!isLocalDomain(request.getRequestURI())) {
				// If we are a proxy then forward to correct domain
				// For now just return error code
				Response response = ((ResourcesProvider) resourcesProvider)
						.getMessageFactory().createResponse(Response.FORBIDDEN,
								request);
				txn.sendResponse(response);
				return;
			}

			// Process require header

			// Authenticate
			// Authorize
			// OK we're authorized now ;-)

			// extract address-of-record
			String sipAddressOfRecord = getCanonicalAddress((HeaderAddress) request
					.getHeader(ToHeader.NAME));

			logger.finer("address-of-record = " + sipAddressOfRecord);

			// map will be empty if user not in LS...
			// Note we don't care if the user has a valid account in the LS, we
			// just
			// add them anyway.
			Map bindings = getLocationService().getBindings(sipAddressOfRecord);

			// Do we have any contact header(s)?
			if (request.getHeader(ContactHeader.NAME) == null) {
				// Just send OK with current bindings - this request was a
				// query.
				sendRegistrationOKResponse(txn, request, bindings);
				return;
			}

			// Check contact, callid, cseq

			ArrayList newContacts = getContactHeaderList(request
					.getHeaders(ContactHeader.NAME));
			ExpiresHeader expiresHeader = null;
			boolean removeAll = false;
			CallIdHeader cidh = (CallIdHeader) request
					.getHeader(CallIdHeader.NAME);
			String callId = cidh.getCallId();

			CSeqHeader cseqh = (CSeqHeader) request.getHeader(CSeqHeader.NAME);
			long seq = cseqh.getSequenceNumber();

			expiresHeader = request.getExpires();

			if (hasWildCard(newContacts)) { // This is a "Contact: *" "remove
											// all bindings" request
				if ((expiresHeader == null)
						|| (expiresHeader.getExpires() != 0)
						|| (newContacts.size() > 1)) {
					// malformed request in RFC3261 ch10.3 step 6
					txn.sendResponse(((ResourcesProvider) resourcesProvider)
							.getMessageFactory().createResponse(
									Response.BAD_REQUEST, request));
					return;
				}

				removeAll = true;
			}

			if (removeAll) {
				logger.fine("Removing bindings");
				// Go through list of current bindings
				// if callid doesn't match - remove binding
				// if callid matches and seq greater, remove binding.
				Iterator it = bindings.values().iterator();

				try {
					while (it.hasNext()) {
						RegistrationBinding binding = (RegistrationBinding) it
								.next();
						if (callId.equals(binding.getCallId())) {

							if (seq > binding.getCSeq()) {
								it.remove();
								getLocationService().removeBinding(
										sipAddressOfRecord,
										binding.getContactAddress());
							} else {
								txn
										.sendResponse(((ResourcesProvider) resourcesProvider)
												.getMessageFactory()
												.createResponse(
														Response.BAD_REQUEST,
														request));
								return;
							}
						} else {
							it.remove();
							getLocationService().removeBinding(
									sipAddressOfRecord,
									binding.getContactAddress());
						}
					}

				} catch (LocationServiceException lse) {
					lse.printStackTrace();
					txn.sendResponse(((ResourcesProvider) resourcesProvider)
							.getMessageFactory().createResponse(
									Response.SERVER_INTERNAL_ERROR, request));
					return;
				}
				// try {
				// getLocationService().setBindings(sipAddressOfRecord,
				// bindings);
				// } catch (LocationServiceException lse) {
				// txn.sendResponse(((ResourcesProvider)resourcesProvider).getMessageFactory().createResponse(Response.SERVER_INTERNAL_ERROR,
				// request));
				// return;
				// }
				sendRegistrationOKResponse(txn, request, bindings);
			} else {
				// Update bindings
				logger.fine("Updating bindings");
				ListIterator li = newContacts.listIterator();

				while (li.hasNext()) {
					ContactHeader contact = (ContactHeader) li.next();

					// get expires value, either in header or default
					// do min-expires etc
					long requestedExpires = 0;

					if (contact.getExpires() >= 0) {
						requestedExpires = contact.getExpires();
					} else if ((expiresHeader != null)
							&& (expiresHeader.getExpires() >= 0)) {
						requestedExpires = expiresHeader.getExpires();
					} else {
						requestedExpires = 3600; // default
					}

					// If expires too large, reset to our local max
					if (requestedExpires > maxExpires) {
						requestedExpires = maxExpires;
					} else if ((requestedExpires > 0)
							&& (requestedExpires < minExpires)) {
						// requested expiry too short, send response with
						// min-expires
						// 
						sendIntervalTooBriefResponse(txn, request);
						return;
					}

					// Get the q-value (preference) for this binding - default
					// to 0.0 (min)
					float q = 0;
					if (contact.getQValue() != -1)
						q = contact.getQValue();
					if ((q > 1) || (q < 0)) {
						txn
								.sendResponse(((ResourcesProvider) resourcesProvider)
										.getMessageFactory().createResponse(
												Response.BAD_REQUEST, request));
						return;
					}

					// Find existing binding
					URI uri = contact.getAddress().getURI();
					String contactAddress = uri.toString();

					RegistrationBinding binding = (RegistrationBinding) bindings
							.get(contactAddress);

					if (binding != null) { // Update this binding
						if (callId.equals(binding.getCallId())) {
							if (seq <= binding.getCSeq()) {
								// Invalid request
								txn
										.sendResponse(((ResourcesProvider) resourcesProvider)
												.getMessageFactory()
												.createResponse(
														Response.BAD_REQUEST,
														request));
								return;
							}
						}

						if (requestedExpires == 0) {
							logger.fine("Removing binding: "
									+ sipAddressOfRecord + " -> "
									+ contactAddress);
							bindings.remove(contactAddress);

							try {
								getLocationService().removeBinding(
										sipAddressOfRecord,
										binding.getContactAddress());
							} catch (LocationServiceException lse) {
								lse.printStackTrace();
								txn
										.sendResponse(((ResourcesProvider) resourcesProvider)
												.getMessageFactory()
												.createResponse(
														Response.SERVER_INTERNAL_ERROR,
														request));
								return;
							}

						} else {

							logger.fine("Updating binding: "
									+ sipAddressOfRecord + " -> "
									+ contactAddress);
							logger.fine("contact: " + contact.toString());
							binding.setCallId(callId);
							binding.setCSeq(seq);
							binding.setExpiryDelta(requestedExpires);
							binding.setQValue(q);
							// set timer for registration expiry
							setRegistrationTimer(sipAddressOfRecord,
									contactAddress, requestedExpires, callId,
									seq);
							// Lets push it into location service, this will
							// update version of binding
							try {
								getLocationService().addBinding(
										sipAddressOfRecord, binding);
							} catch (LocationServiceException lse) {
								lse.printStackTrace();
								txn
										.sendResponse(((ResourcesProvider) resourcesProvider)
												.getMessageFactory()
												.createResponse(
														Response.SERVER_INTERNAL_ERROR,
														request));
								return;
							}
						}

					} else {
						// Create new binding, add to list...
						if (requestedExpires != 0) {
							logger.fine("Adding new binding: "
									+ sipAddressOfRecord + " -> "
									+ contactAddress);
							logger.fine(contact.toString());
							RegistrationBinding newBinding = new RegistrationBinding(
									contactAddress, "", requestedExpires, q,
									callId, seq);
							// removed comment parameter to registration binding
							// - Address and Contact headers don't have comments
							// in 1.1

							bindings.put(contactAddress, newBinding);

							try {
								getLocationService().addBinding(
										sipAddressOfRecord, newBinding);
							} catch (LocationServiceException lse) {
								lse.printStackTrace();
								txn
										.sendResponse(((ResourcesProvider) resourcesProvider)
												.getMessageFactory()
												.createResponse(
														Response.SERVER_INTERNAL_ERROR,
														request));
								return;
							}

							// set timer for registration expiry
							setRegistrationTimer(sipAddressOfRecord,
									contactAddress, requestedExpires, callId,
									seq);
						}
					}
				}
				// Update bindings, return 200 if successful, 500 on error

				sendRegistrationOKResponse(txn, request, bindings);

			}

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private boolean hasWildCard(ArrayList contactHeaders) {
		Iterator it = contactHeaders.iterator();
		while (it.hasNext()) {
			ContactHeader header = (ContactHeader) it.next();
			if (header.toString().indexOf('*') > 0)
				return true;
		}
		return false;
	}

	private ArrayList getContactHeaderList(ListIterator it) {
		ArrayList l = new ArrayList();
		while (it.hasNext()) {
			l.add(it.next());
		}
		return l;
	}

	/**
	 * Set a timer on a registration entry. If a timer is already set for this
	 * registration, reset it to the new timeout value. <br>
	 * <b>THIS IS SLEE DEPENDANT METHOD</b>
	 * 
	 * @param sipAddress
	 *            the public SIP address-of-record for the user
	 * @param sipContactAddress
	 *            the physical contact address for this registration
	 * @param timeout
	 *            expiry time (in seconds) of the registration
	 * @param callId
	 *            the SIP callid of the REGISTER request
	 * @param cseq
	 *            the SIP sequence number of the REGISTER request
	 */
	void setRegistrationTimer(String sipAddress, String sipContactAddress,
			long timeout, String callId, long cseq) {
		// first find out if we already have a timer set for this registration,
		// and if so, cancel it.
		logger.fine("setRegistrationTimer(" + sipAddress + ", "
				+ sipContactAddress + ", " + timeout + ", " + callId + ", "
				+ cseq + ")");
		try {
			// set a one-shot timer. when it fires we expire the registration
			long expireTime = System.currentTimeMillis() + (timeout * 1000);

			// Create new ACI for this timer
			NullActivity nullAC = ((RegistrarSbb) resourcesProvider)
					.getNullActivityFactory().createNullActivity();
			ActivityContextInterface nullACI = ((RegistrarSbb) resourcesProvider)
					.getNullACIFactory().getActivityContextInterface(nullAC);
			RegistrarActivityContextInterface regACI = ((RegistrarSbb) resourcesProvider)
					.asSbbActivityContextInterface(nullACI);
			regACI.setSipAddress(sipAddress);
			regACI.setSipContactAddress(sipContactAddress);
			// callId and cseq used to identify a particular registration
			regACI.setCallId(callId);
			regACI.setCSeq(cseq);

			// attach so we will receive the timer event...
			regACI.attach(((RegistrarSbb) resourcesProvider)
					.getSbbLocalObject());

			TimerOptions timerOpts = new TimerOptions();
			timerOpts.setPersistent(true);

			TimerID newTimer = ((RegistrarSbb) resourcesProvider)
					.getTimerFacility().setTimer(regACI, null, expireTime,
							timerOpts);

			logger.fine("set new timer for registration: " + sipAddress
					+ " -> " + sipContactAddress + ", expires in " + timeout
					+ "s");

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	/**
	 * Expire registration entry, remove it from location service. This would be
	 * a callback from whatever timer is set in setRegistrationExpiry() above.
	 * Only remove a registration if the callId and cseq values match those of
	 * the original registration. If the values don't match, this means the
	 * registration has been updated by a more recent REGISTER request, so we
	 * should not change anything. The timer for the most recent REGISTER
	 * request will expire the entry.
	 * 
	 * @param sipAddress
	 *            the public SIP address-of-record for the user
	 * @param sipContactAddress
	 *            the physical contact address for this registration
	 * @param callId
	 *            the SIP callid of the REGISTER request
	 * @param cseq
	 *            the SIP sequence number of the REGISTER request
	 */
	void expireRegistration(String sipAddress, String sipContactAddress,
			String callId, long cseq) {
		// find user in location service
		try {
			Map bindings = getLocationService().getBindings(sipAddress);
			if (bindings == null) {
				logger.fine("expireRegistration: user " + sipAddress
						+ " not found.");
				return;
			}

			// remove binding for sipContactAddress, if callId and cseq match.
			RegistrationBinding binding = (RegistrationBinding) bindings
					.get(sipContactAddress);
			if (binding == null) {
				logger.fine("expireRegistration: registration for "
						+ sipAddress + " -> " + sipContactAddress
						+ " not found.");
				return;
			}

			if (callId.equals(binding.getCallId())
					&& (cseq == binding.getCSeq())) {
				// this is my registration, so I am allowed to remove it
				RegistrationBinding removedBinding = (RegistrationBinding) bindings
						.remove(sipContactAddress);

				// set bindings
				getLocationService().setBindings(sipAddress, bindings);
				logger.fine("expireRegistration: removed binding: "
						+ sipAddress + " -> " + sipContactAddress);
			} else { // not my registration, do nothing
				logger.fine("expireRegistration: callId/cseq for binding ("
						+ sipAddress + " -> " + sipContactAddress
						+ ") has been updated, not removing");
			}

		} catch (LocationServiceException lse) {
			lse.printStackTrace();
		}

	}

	private List getContactHeaders(Map bindings) {
		if (bindings == null)
			return null;
		ArrayList contactHeaders = new ArrayList();
		Iterator it = bindings.values().iterator();
		ResourcesProvider provider = ((ResourcesProvider) resourcesProvider);
		while (it.hasNext()) {
			RegistrationBinding binding = (RegistrationBinding) it.next();
			ContactHeader header = binding.getContactHeader(provider
					.getAddressFactory(), provider.getHeaderFactory());
			if (header != null) {
				contactHeaders.add(header);
			}
		}
		return contactHeaders;

	}

	private void sendIntervalTooBriefResponse(ServerTransaction txn,
			Request request) {
		try {
			Response res = ((ResourcesProvider) resourcesProvider)
					.getMessageFactory().createResponse(423, request); // In
																		// RFC3261
																		// but
																		// not
																		// JAIN
																		// SIP -
																		// coming
																		// in
																		// JAIN
																		// SIP
																		// 1.1??
			res.setReasonPhrase("Interval Too Brief");
			DateHeader dateHeader = ((ResourcesProvider) resourcesProvider)
					.getHeaderFactory().createDateHeader(
							new GregorianCalendar());
			res.setHeader(dateHeader);
			Header minExpiresHeader = ((ResourcesProvider) resourcesProvider)
					.getHeaderFactory().createHeader("Min-Expires",
							String.valueOf(minExpires));
			res.addHeader(minExpiresHeader);
			logger.fine("Sending Response:\n" + res.toString());
			txn.sendResponse(res);
		} catch (Exception e) {
			logger.log(Level.WARNING, "Failed to sendInternalTooBriefResponse",
					e);
		}

	}

	private void sendRegistrationOKResponse(ServerTransaction txn,
			Request request, Map bindings) {
		List contactHeaders = getContactHeaders(bindings);
		try {
			Response res = ((ResourcesProvider) resourcesProvider)
					.getMessageFactory().createResponse(Response.OK, request);

			if ((contactHeaders != null) && (!contactHeaders.isEmpty())) {
				logger.fine("Adding " + contactHeaders.size() + " headers");
				for (int i = 0; i < contactHeaders.size(); i++) {
					ContactHeader hdr = (ContactHeader) contactHeaders.get(i);
					res.addHeader(hdr);
				}
				// ((SIPResponse) res).setHeaders(contactHeaders);
			}
			DateHeader dateHeader = ((ResourcesProvider) resourcesProvider)
					.getHeaderFactory().createDateHeader(
							new GregorianCalendar());
			res.setHeader(dateHeader);
			logger.fine("Sending Response:\n" + res.toString());
			txn.sendResponse(res);
		} catch (Exception e) {
			logger
					.log(Level.WARNING, "Failed to sendRegistrationOKResponse",
							e);
		}
	}

	public void processResponse(ServerTransaction txn, Response response) {
	}
}
