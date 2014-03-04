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
package org.kuali.rice.kim.document.rule;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.kuali.rice.kim.bo.types.dto.AttributeSet;
import org.kuali.rice.kim.bo.types.dto.KimTypeAttributeInfo;
import org.kuali.rice.kim.bo.types.impl.KimAttributeDataImpl;
import org.kuali.rice.kim.bo.types.impl.KimAttributeImpl;
import org.kuali.rice.kim.bo.ui.KimDocumentAttributeDataBusinessObjectBase;
import org.kuali.rice.kim.util.KimConstants;
import org.kuali.rice.kns.service.BusinessObjectService;
import org.kuali.rice.kns.service.KNSServiceLocator;
import org.kuali.rice.kns.util.GlobalVariables;
import org.kuali.rice.kns.util.KNSConstants;
import org.kuali.rice.kns.util.KNSPropertyConstants;

/**
 * This is a description of what this class does - wliang don't forget to fill this in. 
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 *
 */
public class AttributeValidationHelper {
	private static final Logger LOG = Logger.getLogger(AttributeValidationHelper.class);
	
	protected BusinessObjectService businessObjectService;
    protected Map<String,KimAttributeImpl> attributeDefinitionMap = new HashMap<String,KimAttributeImpl>();
    
    protected KimAttributeImpl getAttributeDefinition( String id ) {
    	KimAttributeImpl attributeImpl = attributeDefinitionMap.get( id );
    	
    	if ( attributeImpl == null ) {
			Map<String,String> criteria = new HashMap<String,String>();
			criteria.put( KimConstants.PrimaryKeyConstants.KIM_ATTRIBUTE_ID, id );
			attributeImpl = (KimAttributeImpl)getBusinessObjectService().findByPrimaryKey( KimAttributeImpl.class, criteria );
			attributeDefinitionMap.put( id, attributeImpl );
    	}
    	return attributeImpl;
    }
    
	public AttributeSet convertAttributesToMap(List<? extends KimAttributeDataImpl> attributes) {
		AttributeSet m = new AttributeSet();
		for(KimAttributeDataImpl data: attributes) {
			KimAttributeImpl attrib = getAttributeDefinition(data.getKimAttributeId());
			if(attrib != null){
				m.put(attrib.getAttributeName(), data.getAttributeValue());
			} else {
				LOG.error("Unable to get attribute name for ID:" + data.getKimAttributeId());
			}
		}
		return m;
	}
    
	public AttributeSet convertQualifiersToMap( List<? extends KimDocumentAttributeDataBusinessObjectBase> qualifiers ) {
		AttributeSet m = new AttributeSet();
		for ( KimDocumentAttributeDataBusinessObjectBase data : qualifiers ) {
			KimAttributeImpl attrib = getAttributeDefinition( data.getKimAttrDefnId() );
			if ( attrib != null ) {
				m.put( attrib.getAttributeName(), data.getAttrVal() );
			} else {
				LOG.error("Unable to get attribute name for ID:" + data.getKimAttrDefnId() );
			}
		}
		return m;
	}

	public AttributeSet getBlankValueQualifiersMap(List<KimTypeAttributeInfo> attributes) {
		AttributeSet m = new AttributeSet();
		for(KimTypeAttributeInfo attribute: attributes){
			KimAttributeImpl attrib = getAttributeDefinition(attribute.getKimAttributeId());
			if ( attrib != null ) {
				m.put( attrib.getAttributeName(), "" );
			} else {
				LOG.error("Unable to get attribute name for ID:" + attribute.getKimAttributeId());
			}
		}
		return m;
	}
	
	public AttributeSet convertQualifiersToAttrIdxMap( List<? extends KimDocumentAttributeDataBusinessObjectBase> qualifiers ) {
		AttributeSet m = new AttributeSet();
		int i = 0;
		for ( KimDocumentAttributeDataBusinessObjectBase data : qualifiers ) {
			KimAttributeImpl attrib = getAttributeDefinition( data.getKimAttrDefnId() );
			if ( attrib != null ) {
				m.put( attrib.getAttributeName(), Integer.toString(i) );
			} else {
				LOG.error("Unable to get attribute name for ID:" + data.getKimAttrDefnId() );
			}
			i++;
		}
		return m;
	}
	
	public BusinessObjectService getBusinessObjectService() {
		if(businessObjectService == null){
			businessObjectService = KNSServiceLocator.getBusinessObjectService();
		}
		return businessObjectService;
	}
	
    public void moveValidationErrorsToErrorMap(AttributeSet validationErrors) {
		// FIXME: This does not use the correct error path yet - may need to be moved up so that the error path is known
		// Also, the above code would overwrite messages on the same attributes (namespaceCode) but on different rows
		for ( String key : validationErrors.keySet() ) {
    		String[] errorMsg = StringUtils.split(validationErrors.get( key ), ":");
    		
			GlobalVariables.getMessageMap().putError( key, errorMsg[0], errorMsg.length > 1 ? StringUtils.split(errorMsg[1], ";") : new String[] {} );
		}
    }

	public AttributeSet convertErrorsForMappedFields(String errorPath, AttributeSet localErrors) {
		AttributeSet errors = new AttributeSet();
		if (errorPath == null) {
			errorPath = KNSConstants.EMPTY_STRING;
		}
		else if (StringUtils.isNotEmpty(errorPath)) {
			errorPath = errorPath + ".";
		}
		for ( String key : localErrors.keySet() ) {
			Map<String,String> criteria = new HashMap<String,String>();
			criteria.put(KNSPropertyConstants.ATTRIBUTE_NAME, key);
			KimAttributeImpl attribute = (KimAttributeImpl) getBusinessObjectService().findByPrimaryKey(KimAttributeImpl.class, criteria);
			String attributeDefnId = attribute==null?"":attribute.getKimAttributeId();
			errors.put(errorPath+"qualifier("+attributeDefnId+").attrVal", localErrors.get(key));
		}
		return errors;
	}

	public AttributeSet convertErrors(String errorPath, AttributeSet attrIdxMap, AttributeSet localErrors) {
		AttributeSet errors = new AttributeSet();
		if (errorPath == null) {
			errorPath = KNSConstants.EMPTY_STRING;
		}
		else if (StringUtils.isNotEmpty(errorPath)) {
			errorPath = errorPath + ".";
		}
		for ( String key : localErrors.keySet() ) {
			errors.put(errorPath+"qualifiers["+attrIdxMap.get(key)+"].attrVal", localErrors.get(key));
		}
		return errors;
	}
}