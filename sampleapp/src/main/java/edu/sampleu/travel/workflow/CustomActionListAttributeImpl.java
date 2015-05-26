/*
 * Copyright 2007-2008 The Kuali Foundation
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
package edu.sampleu.travel.workflow;

import org.kuali.rice.kew.actionitem.ActionItem;
import org.kuali.rice.kew.actionlist.CustomActionListAttribute;
import org.kuali.rice.kew.actionlist.DisplayParameters;
import org.kuali.rice.kew.actions.ActionSet;
import org.kuali.rice.kew.web.session.UserSession;


public class CustomActionListAttributeImpl implements CustomActionListAttribute {

	private static final long serialVersionUID = 6129615406164385616L;

	public DisplayParameters getDocHandlerDisplayParameters(UserSession userSession, ActionItem actionItem) throws Exception {
		return new DisplayParameters(new Integer(400));
	}

	public ActionSet getLegalActions(UserSession userSession, ActionItem actionItem) throws Exception {
		ActionSet actionSet = new ActionSet();
		actionSet.addAcknowledge();
		actionSet.addApprove();
		actionSet.addFyi();
		actionSet.addComplete();
		return actionSet;
	}

}