/*
 * Copyright 2007-2009 The Kuali Foundation
 *
 * Licensed under the Educational Community License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.opensource.org/licenses/ecl2.php
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kuali.rice.kns.service;

import org.kuali.rice.kim.bo.Person;
import org.kuali.rice.kim.bo.types.dto.AttributeSet;
import org.kuali.rice.kns.authorization.BusinessObjectRestrictions;
import org.kuali.rice.kns.bo.BusinessObject;
import org.kuali.rice.kns.document.Document;
import org.kuali.rice.kns.document.MaintenanceDocument;
import org.kuali.rice.kns.document.authorization.MaintenanceDocumentRestrictions;
import org.kuali.rice.kns.inquiry.InquiryRestrictions;

/**
 * This class is responsible for using AttributeSecurity on
 * AttributeDefinitions, InquirableField the data dictionary business object and
 * maintenance document entries
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 * 
 */
public interface BusinessObjectAuthorizationService {
	public BusinessObjectRestrictions getLookupResultRestrictions(
			BusinessObject businessObject, Person user);

	public InquiryRestrictions getInquiryRestrictions(
			BusinessObject businessObject, Person user);

	public MaintenanceDocumentRestrictions getMaintenanceDocumentRestrictions(
			MaintenanceDocument maintenanceDocument, Person user);

	public <T extends BusinessObject> boolean canFullyUnmaskField(Person user,
			Class<T> businessObjectClass, String fieldName, Document document);

	public <T extends BusinessObject> boolean canPartiallyUnmaskField(
			Person user, Class<T> businessObjectClass, String fieldName, Document document);
	
	public <T extends BusinessObject> boolean canCreate(Class<T> boClass, Person user, String docTypeName);
	
	public boolean canMaintain(BusinessObject businessObject, Person user, String docTypeName);
	
	public boolean attributeValueNeedsToBeEncryptedOnFormsAndLinks(Class<? extends BusinessObject> businessObjectClass, String attributeName);
}