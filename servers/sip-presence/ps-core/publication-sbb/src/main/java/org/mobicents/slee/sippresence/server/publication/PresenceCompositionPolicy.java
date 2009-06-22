package org.mobicents.slee.sippresence.server.publication;

import gov.nist.javax.sip.Utils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.xml.bind.JAXBElement;
import javax.xml.namespace.QName;

import org.mobicents.slee.sippresence.pojo.commonschema.NoteT;
import org.mobicents.slee.sippresence.pojo.datamodel.Device;
import org.mobicents.slee.sippresence.pojo.datamodel.Person;
import org.mobicents.slee.sippresence.pojo.pidf.Contact;
import org.mobicents.slee.sippresence.pojo.pidf.Note;
import org.mobicents.slee.sippresence.pojo.pidf.Presence;
import org.mobicents.slee.sippresence.pojo.pidf.Status;
import org.mobicents.slee.sippresence.pojo.pidf.Tuple;
import org.mobicents.slee.sippresence.pojo.pidf.oma.ServiceDescription;

public class PresenceCompositionPolicy {

	/**
	 * 
	 * @param presence the new state to add to the composition
	 * @param otherPresence the current presence composition state 
	 * @return
	 */
	public Presence compose(Presence presence, Presence otherPresence) {
		
		Presence result = new Presence();
		result.setEntity(presence.getEntity());
		
		// process tuples
		result.getTuple().addAll(composeTuples(presence.getTuple(), otherPresence.getTuple()));
				
		// extract devices and persons from anys
		List<Object> presenceAny = presence.getAny();
		List<Device> presenceDevices = new ArrayList<Device>();
		List<Person> presencePersons = new ArrayList<Person>();
		for (Iterator<?> it = presenceAny.iterator(); it.hasNext();) {
			Object obj = it.next();
			if (obj instanceof Device) {
				presenceDevices.add((Device)obj);
				it.remove();
			}
			else if (obj instanceof Person) {
				presencePersons.add((Person)obj);
				it.remove();
			}
		}
		List<Object> otherPresenceAny = otherPresence.getAny();
		List<Device> otherPresenceDevices = new ArrayList<Device>();
		List<Person> otherPresencePersons = new ArrayList<Person>();
		for (Iterator<?> it = otherPresenceAny.iterator(); it.hasNext();) {
			Object obj = it.next();
			if (obj instanceof Device) {
				otherPresenceDevices.add((Device)obj);
				it.remove();
			}
			else if (obj instanceof Person) {
				otherPresencePersons.add((Person)obj);
				it.remove();
			}
		}
		// process devices
		result.getAny().addAll(composeDevices(presenceDevices, otherPresenceDevices));
		
		// process persons
		result.getAny().addAll(composePersons(presencePersons, otherPresencePersons));
		
		// process anys
		result.getAny().addAll(compose(presenceAny, otherPresenceAny,true,false,false));
		
		// process notes
		result.getNote().addAll(composeNotes(presence.getNote(), presence.getNote()));
		return result;
	}

	private List<Tuple> composeTuples(List<Tuple> tuples,List<Tuple> otherTuples) {
		
		ArrayList<Tuple> result = new ArrayList<Tuple>();
		// process all from tuples trying to match each with othertuples
		for (Iterator<Tuple> tuplesIt = tuples.iterator(); tuplesIt.hasNext(); ) {
			Tuple tuple = tuplesIt.next();
			for (Iterator<Tuple> otherTuplesIt = otherTuples.iterator(); otherTuplesIt.hasNext(); ) {
				Tuple otherTuple = otherTuplesIt.next();
				Tuple compositionTuple = compose(tuple, otherTuple);
				if (compositionTuple != null) {
					// the composition has a result
					result.add(compositionTuple);
					// remove the tuples to not be iterated again
					tuplesIt.remove();
					otherTuplesIt.remove();
					break;
				}
			}
		}
		// now add the ones left but replacing the ids
		for (Tuple tuple : tuples) {
			tuple.setId(Utils.getInstance().generateTag());
			result.add(tuple);
		}
		for (Tuple tuple : otherTuples) {
			tuple.setId(Utils.getInstance().generateTag());
			result.add(tuple);
		}
		return result;
	}

	private Tuple compose(Tuple tuple, Tuple otherTuple) {
		
		Tuple result = new Tuple();
		result.setId(Utils.getInstance().generateTag());

		/*
		 * 
		 * Service elements (defined in section 10.1.2) If the following
		 * conditions all apply:
		 * 
		 * a. If one <tuple> element includes a <contact> element, as
		 * defined in [RFC3863], other <tuple> elements include an
		 * identical <contact> element; and
		 */
		if (tuple.getContact() == null) {
			if (otherTuple.getContact() != null) {
				// different contacts
				return null;
			}
			// else ignore no contacts
		}
		else {
			if (otherTuple.getContact() == null) {
				// different contacts
				return null;
			}
			else {
				Contact composedContact = this.compose(tuple.getContact(), otherTuple.getContact());
				if (composedContact != null) {
					result.setContact(composedContact);
				}
				else {
					return null;
				}
			}
		}
		
		/* b. If one <tuple> element includes a <service-description>
		 * element, as defined in section 10.5.1, other <tuple> elements
		 * include an identical <service-description> element. Two
		 * <service-description> elements are identical if they contain
		 * identical <service-id> and <version> elements; and
		 * 
		 * c. If one <tuple> element includes a <class> element, as
		 * defined in section 10.5.1, other <tuple> elements include an
		 * identical <class> element; and
		 * 
		 * d. If there are no conflicting elements (i.e. same elements
		 * with different values) or attributes under the <tuple>
		 * elements. Different <timestamp> values are not considered as
		 * a conflict.
		 *
		 * then the PS SHALL:
		 * 
		 * a. Aggregate elements within a <tuple> element that are
		 * published from different Presence Sources into one <tuple>
		 * element. Identical elements with the same value and
		 * attributes SHALL not be duplicated; and
		 * 
		 * b. Set the “priority” attribute of the <contact> element in
		 * the aggregated <tuple> element to the highest one among those
		 * in the input <tuple> elements, if any “priority” attribute is
		 * present; and
		 * 
		 * c. Set the <timestamp> of the aggregated <tuple> to the most
		 * recent one among the ones that contribute to the aggregation;
		 * and
		 * 
		 * d. Keep no more than one <description> element from the
		 * <service-description> elements of the aggregated <tuple>
		 * element when there are different values of the <description>
		 * elements. 
		 */

		// process status
		if (tuple.getStatus() == null) {
			if (otherTuple.getStatus() != null) {
				// different status
				return null;
			}
			// else ignore no status
		}
		else {
			if (otherTuple.getStatus() == null) {
				// different status
				return null;
			}
			else {
				Status status = this.compose(tuple.getStatus(), otherTuple.getStatus());
				if (status != null) {
					result.setStatus(status);
				}
				else {
					return null;
				}
			}
		}

		// process anys
		List<Object> anys = compose(tuple.getAny(), otherTuple.getAny(),false,false,false);
		if (anys != null) {
			result.getAny().addAll(anys);
		}
		else {
			return null;
		}

		// process notes
		result.getNote().addAll(composeNotes(tuple.getNote(), otherTuple.getNote()));
				
		// process timestamp
		if (tuple.getTimestamp() == null) {
			result.setTimestamp(otherTuple.getTimestamp());
		}
		else {
			if (otherTuple.getTimestamp() == null) {
				result.setTimestamp(tuple.getTimestamp());
			}
			else {
				if (tuple.getTimestamp().compare(otherTuple.getTimestamp()) > 0) {
					result.setTimestamp(tuple.getTimestamp());
				}
				else {
					result.setTimestamp(otherTuple.getTimestamp());
				}
			}
		}
 
		return result;
	}
	
	/**
	 * Compose a {@link Contact} object from two non null ones.
	 * @param contact
	 * @param otherContact
	 * @return
	 */
	private Contact compose(Contact contact, Contact otherContact) {
		Contact result = new Contact();
		// compare values
		if (contact.getValue().equals(otherContact.getValue())) {
			result.setValue(contact.getValue());
		}
		else {
			return null;
		}
		// process priority
		if (contact.getPriority() != null) {
			if (otherContact.getPriority() == null || otherContact.getPriority().compareTo(contact.getPriority()) < 0) {
				result.setPriority(contact.getPriority());				
			}			
		}
		if (result.getPriority() == null) {
			result.setPriority(otherContact.getPriority());
		}
		return result;
	}
	
	private Status compose(Status status, Status otherStatus) {
		final Status result = new Status();
		// check basic
		if (status.getBasic() != null && status.getBasic().equals(otherStatus.getBasic())) {
			result.setBasic(status.getBasic());
		}
		else {
			if (otherStatus.getBasic() != null) {
				return null;
			}
			// else ignore
		}
		// lets process the anys
		List<Object> anys = compose(status.getAny(), otherStatus.getAny(),false,false,false);
		if (anys != null) {
			result.getAny().addAll(anys);
			return result; 
		}
		else {
			return null;
		}
	}
	
	private List<Object> compose(List<Object> anys, List<Object> otherAnys, boolean allowConflicts, boolean keepRecentInConflict, boolean anysIsOlder) {
		
		ArrayList<Object> result = new ArrayList<Object>();
		for (Iterator<?> anysIt = anys.iterator(); anysIt.hasNext(); ) {
			Object anysObject = anysIt.next();
			for (Iterator<?> otherAnysIt = otherAnys.iterator(); otherAnysIt.hasNext(); ) {
				Object otherAnysObject = otherAnysIt.next();
				if (anysObject instanceof JAXBElement) {
					JAXBElement<?> anysElement = (JAXBElement<?>) anysObject;
					if (otherAnysObject instanceof JAXBElement) {
						JAXBElement<?> otherAnysElement = (JAXBElement<?>) otherAnysObject;
						if (anysElement.getName().equals(otherAnysElement.getName())) {
							// same element type, check for conflict
							if (isConflict(anysElement.getValue(), otherAnysElement.getValue())) {
								if (allowConflicts) {
									if (keepRecentInConflict) {
										if (anysIsOlder) {
											result.add(otherAnysElement);											
										}
										else {
											result.add(anysElement);											
										}
										anysIt.remove();
										otherAnysIt.remove();
										break;
									}									
								}
								else {
									return null;
								}
							}							
						}
					}
				}
				else {
					if (anysObject.getClass() == otherAnysObject.getClass()) {
						// same element type, check for conflict
						if (isConflict(anysObject, otherAnysObject)) {
							if (allowConflicts) {
								if (keepRecentInConflict) {
									if (anysIsOlder) {
										result.add(otherAnysObject);										
									}
									else {
										result.add(anysObject);										
									}
									anysIt.remove();
									otherAnysIt.remove();
									break;
								}
							}
							else {
								return null;
							}
						}
						else {
							Object composedObject = compose(anysObject, otherAnysObject);
							if (composedObject != null) {
								result.add(composedObject);
								anysIt.remove();
								otherAnysIt.remove();
								break;
							}							
						}
					}
				}
			}
		}
		// now add the ones left 
		result.addAll(anys);
		result.addAll(otherAnys);
		return result;
		
	}

	/**
	 * Here we compose 2 objects of same class
	 * @param anysObject
	 * @param otherAnysObject
	 * @return
	 */
	private Object compose(Object anysObject, Object otherAnysObject) {
		if (anysObject instanceof ServiceDescription) {
			ServiceDescription anysServiceDescription = (ServiceDescription) anysObject;
			ServiceDescription otherAnysServiceDescription = (ServiceDescription) otherAnysObject;
			ServiceDescription result = new ServiceDescription();
			result.setServiceId(anysServiceDescription.getServiceId());
			result.setVersion(anysServiceDescription.getVersion());
			result.setDescription(anysServiceDescription.getDescription());
			result.getAny().addAll(compose(anysServiceDescription.getAny(), otherAnysServiceDescription.getAny(), true, false, false));
			// merge other attributes, leaving 
			for (QName attributeName : anysServiceDescription.getOtherAttributes().keySet()) {
				result.getOtherAttributes().put(attributeName, anysServiceDescription.getOtherAttributes().get(attributeName));
			}
			for (QName attributeName : otherAnysServiceDescription.getOtherAttributes().keySet()) {
				String attributeValue = result.getOtherAttributes().get(attributeName);
				if (attributeValue != null) {
					if (!attributeValue.equals(otherAnysServiceDescription.getOtherAttributes().get(attributeName))) {
						// attribute conflict
						result.getOtherAttributes().remove(attributeName);
					}
				}
				else {
					result.getOtherAttributes().put(attributeName, otherAnysServiceDescription.getOtherAttributes().get(attributeName));
				}
			}
			return result;
		}
		else {
			return null;
		}
	}

	private boolean isConflict(Object object, Object otherObject) {
		// well known objects
		if (object.getClass() == ServiceDescription.class) {
			ServiceDescription serviceDescription = (ServiceDescription) object;
			ServiceDescription otherServiceDescription = (ServiceDescription) object;
			if (serviceDescription.getServiceId() == null) {
				if (otherServiceDescription.getServiceId() != null)
					return true;
			} else if (!serviceDescription.getServiceId().equals(otherServiceDescription.getServiceId()))
				return true;
			if (serviceDescription.getVersion() == null) {
				if (otherServiceDescription.getVersion() != null)
					return true;
			} else if (!serviceDescription.getVersion().equals(otherServiceDescription.getVersion()))
				return true;
			return false;
		}
		// unknown
		else if (object.equals(otherObject)) {
			return false;
		}
		else {
			return true;
		}
	}
	
	private List<Note> composeNotes(List<Note> notes,List<Note> otherNotes) {
		
		ArrayList<Note> result = new ArrayList<Note>();
		// process all from notes trying to match each with otherNote
		for (Iterator<Note> notesIt = notes.iterator(); notesIt.hasNext(); ) {
			Note note = notesIt.next();
			for (Iterator<Note> otherNotesIt = otherNotes.iterator(); otherNotesIt.hasNext(); ) {
				Note otherNote = otherNotesIt.next();
				if (note.getLang() == null) {
					if (otherNote.getLang() != null) {
						continue;
					}
				}
				else {
					if (!note.getLang().equals(otherNote.getLang())) {
						continue;
					}
				}
				if (note.getValue() == null) {
					if (otherNote.getValue() != null) {
						continue;
					}
				}
				else {
					if (!note.getValue().equals(otherNote.getValue())) {
						continue;
					}
				}
				result.add(note);
				notesIt.remove();
				otherNotesIt.remove();
				break;
			}
		}
		// now add the ones left 
		result.addAll(notes);
		result.addAll(otherNotes);
		return result;
	}
	
	private List<Device> composeDevices(
			List<Device> devices,
			List<Device> otherDevices) {
				
		ArrayList<Device> result = new ArrayList<Device>();

		for (Iterator<Device> it = devices.iterator(); it.hasNext(); ) {
			Device device = it.next();
			for (Iterator<Device> otherIt = otherDevices.iterator(); otherIt.hasNext(); ) {
				Device otherDevice = otherIt.next();
				Device compositionDevice = composeDevice(device, otherDevice);
				if (compositionDevice != null) {
					// the composition has a result
					result.add(compositionDevice);
					// remove both to not be iterated again
					it.remove();
					otherIt.remove();
					break;
				}
			}
		}
		// now add the ones left but replacing the ids
		for (Device device : devices) {
			device.setId(Utils.getInstance().generateTag());
			result.add(device);
		}
		for (Device device : otherDevices) {
			device.setId(Utils.getInstance().generateTag());
			result.add(device);
		}
		return result;
		
	}

	private Device composeDevice(Device device,
			Device otherDevice) {
		
		/*
		 * If the <deviceID> of the <device> elements that are published from
		 * different Presence Sources match
		 * 
		 * then the PS SHALL
		 * 
		 * a. Aggregate the non-conflicting elements within one <device>
		 * element; and
		 * 
		 * b. Set the <timestamp> of the aggregated <device> element to the most
		 * recent one among the ones that contribute to the aggregation; and
		 * 
		 * c. Use the element from the most recent publication for conflicting
		 * elements.
		 * 
		 */
		
		if (device.getDeviceID().equals(otherDevice.getDeviceID())) {
			Device result = new Device();
			
			// process timestamp
			boolean deviceIsOlder = false;
			if (device.getTimestamp() == null) {
				result.setTimestamp(otherDevice.getTimestamp());
				deviceIsOlder = true;
			}
			else {
				if (otherDevice.getTimestamp() == null) {
					result.setTimestamp(device.getTimestamp());
				}
				else {
					if (device.getTimestamp().compare(otherDevice.getTimestamp()) > 0) {
						result.setTimestamp(device.getTimestamp());
					}
					else {
						result.setTimestamp(otherDevice.getTimestamp());
						deviceIsOlder = true;
					}
				}
			}
			
			// process anys
			result.getAny().addAll(compose(device.getAny(), otherDevice.getAny(),true,true,deviceIsOlder));
						
			// process notes
			result.getNote().addAll(composeNotesT(device.getNote(), otherDevice.getNote()));
			result.setDeviceID(device.getDeviceID());
			result.setId(Utils.getInstance().generateTag());
			return result;
		}
		else {
			return null;
		}
	}
	
	private List<Person> composePersons(
			List<Person> persons,
			List<Person> otherPersons) {
		
		ArrayList<Person> result = new ArrayList<Person>();

		for (Iterator<Person> it = persons.iterator(); it.hasNext(); ) {
			Person person = it.next();
			for (Iterator<Person> otherIt = otherPersons.iterator(); otherIt.hasNext(); ) {
				Person otherPerson = otherIt.next();
				Person compositionPerson = composePerson(person, otherPerson);
				if (compositionPerson != null) {
					// the composition has a result
					result.add(compositionPerson);
					// remove both to not be iterated again
					it.remove();
					otherIt.remove();
					break;
				}
			}
		}
		// now add the ones left but replacing the ids
		for (Person person : persons) {
			person.setId(Utils.getInstance().generateTag());
			result.add(person);
		}
		for (Person person : otherPersons) {
			person.setId(Utils.getInstance().generateTag());
			result.add(person);
		}
		return result;
	}
	
	private Person composePerson(Person person,
			Person otherPerson) {
		
		/*
		 * If the following conditions all apply:
		 * 
		 * a. If one <person> element includes a <class> element, as defined in
		 * section 10.5.1, other <person> elements include an identical <class>
		 * element; and
		 * 
		 * b. If there are no conflicting elements (same elements with different
		 * values or attributes) under the <person> elements. Different
		 * <timestamp> values are not considered as a conflict.
		 * 
		 * then the PS SHALL:
		 * 
		 * a. Aggregate elements within a <person> element that are published
		 * from different Presence Sources into one <person> element. Identical
		 * elements with the same value SHALL not be duplicated.
		 * 
		 * b. Set the <timestamp> of the aggregated <person> element to the most
		 * recent one among the ones that contribute to the aggregation. In any
		 * other case, the PS SHALL keep <person> elements from different
		 * Presence Sources separate.
		 */
		
		Person result = new Person();
		
		// process anys
		List<Object> anys = compose(person.getAny(), otherPerson.getAny(),false,false,false);
		if (anys != null) {
			result.getAny().addAll(anys);
		}
		else {
			return null;
		}
		
		// process timestamp
		if (person.getTimestamp() == null) {
			result.setTimestamp(otherPerson.getTimestamp());
		}
		else {
			if (otherPerson.getTimestamp() == null) {
				result.setTimestamp(person.getTimestamp());
			}
			else {
				if (person.getTimestamp().compare(otherPerson.getTimestamp()) > 0) {
					result.setTimestamp(person.getTimestamp());
				}
				else {
					result.setTimestamp(otherPerson.getTimestamp());
				}
			}
		}
		
		// process notes
		result.getNote().addAll(composeNotesT(person.getNote(), otherPerson.getNote()));
		
		result.setId(Utils.getInstance().generateTag());
		return result;
	}
	
	/**
	 * same thing as composeNotes, just the object type differs
	 * @param notes
	 * @param otherNotes
	 * @return
	 */
	private List<NoteT> composeNotesT(List<NoteT> notes,List<NoteT> otherNotes) {
		
		ArrayList<NoteT> result = new ArrayList<NoteT>();
		// process all from notes trying to match each with otherNote
		for (Iterator<NoteT> notesIt = notes.iterator(); notesIt.hasNext(); ) {
			NoteT note = notesIt.next();
			for (Iterator<NoteT> otherNotesIt = otherNotes.iterator(); otherNotesIt.hasNext(); ) {
				NoteT otherNote = otherNotesIt.next();
				if (note.getLang() == null) {
					if (otherNote.getLang() != null) {
						continue;
					}
				}
				else {
					if (!note.getLang().equals(otherNote.getLang())) {
						continue;
					}
				}
				if (note.getValue() == null) {
					if (otherNote.getValue() != null) {
						continue;
					}
				}
				else {
					if (!note.getValue().equals(otherNote.getValue())) {
						continue;
					}
				}
				result.add(note);
				notesIt.remove();
				otherNotesIt.remove();
				break;
			}
		}
		// now add the ones left 
		result.addAll(notes);
		result.addAll(otherNotes);
		return result;
	}
}
