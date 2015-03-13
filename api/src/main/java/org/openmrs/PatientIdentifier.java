/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.util.OpenmrsUtil;

/**
 * A <code>Patient</code> can have zero to n identifying PatientIdentifier(s). PatientIdentifiers
 * are anything from medical record numbers, to social security numbers, to driver's licenses. The
 * type of identifier is defined by the PatientIdentifierType. A PatientIdentifier also contains a
 * Location.
 *
 * @see org.openmrs.PatientIdentifierType
 */
public class PatientIdentifier extends BaseOpenmrsData implements java.io.Serializable, Comparable<PatientIdentifier> {
	
	public static final long serialVersionUID = 1123121L;
	
	private static final Log log = LogFactory.getLog(PatientIdentifier.class);
	
	// Fields
	
	/**
	 * @since 1.5
	 */
	private Integer patientIdentifierId;
	
	private Patient patient;
	
	private String identifier;
	
	private PatientIdentifierType identifierType;
	
	private Location location;
	
	private Boolean preferred = false;
	
	/** default constructor */
	public PatientIdentifier() {
	}
	
	/**
	 * Convenience constructor for creating a basic identifier
	 *
	 * @param identifier String identifier
	 * @param type PatientIdentifierType
	 * @param location Location of the identifier
	 */
	public PatientIdentifier(String identifier, PatientIdentifierType type, Location location) {
		this.identifier = identifier;
		this.identifierType = type;
		this.location = location;
	}
	
	/**
	 * Compares this PatientIdentifier object to the given otherIdentifier. This method differs from
	 * {@link #equals(Object)} in that this method compares the inner fields of each identifier for
	 * equality. Note: Null/empty fields on <code>otherIdentifier</code> /will not/ cause a false
	 * value to be returned
	 *
	 * @param otherIdentifier PatientiIdentifier with which to compare
	 * @return boolean true/false whether or not they are the same names
	 */
	public boolean equalsContent(PatientIdentifier otherIdentifier) {
		boolean returnValue = true;
		
		// these are the methods to compare.
		String[] methods = { "getIdentifier", "getIdentifierType", "getLocation" };
		
		Class<? extends PatientIdentifier> identifierClass = this.getClass();
		
		// loop over all of the selected methods and compare this and other
		for (String methodName : methods) {
			try {
				Method method = identifierClass.getMethod(methodName, new Class[] {});
				
				Object thisValue = method.invoke(this);
				Object otherValue = method.invoke(otherIdentifier);
				
				if (otherValue != null) {
					returnValue &= otherValue.equals(thisValue);
				}
				
			}
			catch (NoSuchMethodException e) {
				log.warn("No such method for comparison " + methodName, e);
			}
			catch (IllegalAccessException e) {
				log.error("Error while comparing identifiers", e);
			}
			catch (InvocationTargetException e) {
				log.error("Error while comparing identifiers", e);
			}
			
		}
		
		return returnValue;
	}
	
	//property accessors
	
	/**
	 * @return Returns the identifier.
	 */
	public String getIdentifier() {
		return identifier;
	}
	
	/**
	 * @param identifier The identifier to set.
	 */
	public void setIdentifier(String identifier) {
		this.identifier = identifier;
	}
	
	/**
	 * @return Returns the identifierType.
	 */
	public PatientIdentifierType getIdentifierType() {
		return identifierType;
	}
	
	/**
	 * @param identifierType The identifierType to set.
	 */
	public void setIdentifierType(PatientIdentifierType identifierType) {
		this.identifierType = identifierType;
	}
	
	/**
	 * @return Returns the location.
	 */
	public Location getLocation() {
		return location;
	}
	
	/**
	 * @param location The location to set.
	 */
	public void setLocation(Location location) {
		this.location = location;
	}
	
	/**
	 * @return Returns the patient.
	 */
	public Patient getPatient() {
		return patient;
	}
	
	/**
	 * @param patient The patient to set.
	 */
	public void setPatient(Patient patient) {
		this.patient = patient;
	}
	
	@Override
	public String toString() {
		return this.identifier;
	}
	
	/**
	 * @return Returns the preferred.
	 */
	public Boolean getPreferred() {
		return isPreferred();
	}
	
	/**
	 * @param preferred The preferred to set.
	 */
	public void setPreferred(Boolean preferred) {
		this.preferred = preferred;
	}
	
	/**
	 * @return the preferred status
	 */
	public Boolean isPreferred() {
		return preferred;
	}
	
	/**
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	public int compareTo(PatientIdentifier other) {
		int retValue = 0;
		if (other != null) {
			retValue = isVoided().compareTo(other.isVoided());
			if (retValue == 0) {
				retValue = other.isPreferred().compareTo(isPreferred());
			}
			if (retValue == 0) {
				retValue = OpenmrsUtil.compareWithNullAsLatest(getDateCreated(), other.getDateCreated());
			}
			if (getIdentifierType() == null && other.getIdentifierType() == null) {
				return 0;
			}
			if (getIdentifierType() == null && other.getIdentifierType() != null) {
				retValue = 1;
			}
			if (other.getIdentifierType() == null && getIdentifierType() != null) {
				retValue = -1;
			}
			if (retValue == 0) {
				retValue = OpenmrsUtil.compareWithNullAsGreatest(getIdentifierType().getPatientIdentifierTypeId(), other
				        .getIdentifierType().getPatientIdentifierTypeId());
			}
			if (retValue == 0) {
				retValue = OpenmrsUtil.compareWithNullAsGreatest(getIdentifier(), other.getIdentifier());
			}
			
			// if we've gotten this far, just check all identifier values.  If they are
			// equal, leave the objects at 0.  If not, arbitrarily pick retValue=1
			// and return that (they are not equal).
			if (retValue == 0 && !equalsContent(other)) {
				retValue = 1;
			}
		}
		
		return retValue;
	}
	
	/**
	 * Indicates whether some other object is "equal to" this one.
	 *
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	public boolean equals(PatientIdentifier other) {
		boolean result;
		
		if ((other == null) || (getClass() != other.getClass())) {
			result = false;
		} // end if
		else {
			if (patient == null) {
				if (other.getPatient() != null) {
					return false;
				}
			} else if (!patient.equals(other.getPatient())) {
				return false;
			}
			if (identifier == null) {
				if (other.getIdentifier() != null) {
					return false;
				}
			} else if (!identifier.equals(other.getIdentifier())) {
				return false;
			}
			if (identifierType == null) {
				if (other.getIdentifierType() != null) {
					return false;
				}
			} else if (!identifierType.equals(other.getIdentifierType())) {
				return false;
			}
			if (location == null) {
				if (other.getLocation() != null) {
					return false;
				}
			} else if (!location.equals(other.getLocation())) {
				return false;
			}
			result = (patientIdentifierId == other.getPatientIdentifierId() && preferred == other.getPreferred());
		} // end else
		if (getUuid() == null) {
			return false;
		} else if (!getUuid().equals(other.getUuid())) {
			return false;
		}
		return result;
	}
	
	/**
	 * @since 1.5
	 * @see org.openmrs.OpenmrsObject#getId()
	 */
	public Integer getId() {
		return getPatientIdentifierId();
	}
	
	/**
	 * @since 1.5
	 * @see org.openmrs.OpenmrsObject#setId(java.lang.Integer)
	 */
	public void setId(Integer id) {
		setPatientIdentifierId(id);
	}
	
	/**
	 * @since 1.5
	 * @return the patientIdentifierId
	 */
	public Integer getPatientIdentifierId() {
		return patientIdentifierId;
	}
	
	/**
	 * @since 1.5
	 * @param patientIdentifierId the patientIdentifierId to set
	 */
	public void setPatientIdentifierId(Integer patientIdentifierId) {
		this.patientIdentifierId = patientIdentifierId;
	}
}
