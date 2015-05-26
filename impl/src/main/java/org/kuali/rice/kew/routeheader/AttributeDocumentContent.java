/*
 * Copyright 2005-2008 The Kuali Foundation
 * 
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
package org.kuali.rice.kew.routeheader;

import java.util.Iterator;
import java.util.List;

import org.kuali.rice.kew.exception.InvalidXmlException;
import org.kuali.rice.kew.rule.WorkflowAttribute;
import org.kuali.rice.kew.rule.web.RoutingReportAction;
import org.kuali.rice.kew.util.KEWConstants;


/**
 * {@link DocumentContent} which is generated from a List of attributes.
 * Used by the {@link RoutingReportAction} to aid in generation of 
 * document content when running routing reports.
 * 
 * @see WorkflowAttribute
 * @see RoutingReportAction
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class AttributeDocumentContent extends StandardDocumentContent {
    
	private static final long serialVersionUID = 6789132279492877000L;

	public AttributeDocumentContent(List attributes) throws InvalidXmlException {
        super(generateDocContent(attributes));
    }
    
    private static String generateDocContent(List attributes) {
        StringBuffer buffer = new StringBuffer();
        buffer.append("<").append(KEWConstants.DOCUMENT_CONTENT_ELEMENT).append(">");
        buffer.append("<").append(KEWConstants.ATTRIBUTE_CONTENT_ELEMENT).append(">");
        for (Iterator iterator = attributes.iterator(); iterator.hasNext();) {
            WorkflowAttribute attribute = (WorkflowAttribute) iterator.next();
            buffer.append(attribute.getDocContent());
        }
        buffer.append("</").append(KEWConstants.ATTRIBUTE_CONTENT_ELEMENT).append(">");
        buffer.append("</").append(KEWConstants.DOCUMENT_CONTENT_ELEMENT).append(">");
        return buffer.toString();
    }

}