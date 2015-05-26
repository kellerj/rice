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

import javax.persistence.Transient;

import org.apache.commons.lang.StringUtils;
import org.kuali.rice.kew.user.UserUtils;
import org.kuali.rice.kew.web.session.UserSession;
import org.kuali.rice.kim.bo.Person;
import org.kuali.rice.kim.bo.entity.KimPrincipal;
import org.kuali.rice.kim.bo.entity.dto.KimEntityDefaultInfo;
import org.kuali.rice.kim.service.KIMServiceLocator;


/**
 * An extension of {@link DocumentRouteHeaderValue} which is mapped to OJB to help
 * with optimization of the loading of a user's Action List.
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
//@Entity
//@Table(name="KREW_DOC_HDR_T")
public class DocumentRouteHeaderValueActionListExtension extends DocumentRouteHeaderValue {

	private static final long serialVersionUID = 8458532812557846684L;

    @Transient
	private KimPrincipal actionListInitiatorPrincipal = null;

    public KimPrincipal getActionListInitiatorPrincipal() {
        return actionListInitiatorPrincipal;
    }

    public void setActionListInitiatorPrincipal(KimPrincipal actionListInitiatorPrincipal) {
        this.actionListInitiatorPrincipal = actionListInitiatorPrincipal;
    }

    /**
     * Gets the initiator name, masked appropriately if restricted.
     */
    public String getInitiatorName() {
        String initiatorName = null;
        Person initiator = KIMServiceLocator.getPersonService().getPerson(getActionListInitiatorPrincipal().getPrincipalId());
    	if (initiator != null) {
    	    initiatorName = initiator.getName();
    	}
    	return initiatorName;
    }

}
