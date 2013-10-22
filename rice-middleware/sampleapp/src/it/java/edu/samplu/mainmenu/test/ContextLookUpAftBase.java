/**
 * Copyright 2005-2013 The Kuali Foundation
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
package edu.samplu.mainmenu.test;

import org.kuali.rice.testtools.common.Failable;
import org.kuali.rice.testtools.selenium.ITUtil;
import org.kuali.rice.testtools.selenium.WebDriverUtil;

/**
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class ContextLookUpAftBase extends MainTmplMthdSTNavBase{

    /**
     * ITUtil.PORTAL + "?channelTitle=Context%20Lookup&channelUrl="
     *   + WebDriverUtil.getBaseUrlString() + ITUtil.KRAD_LOOKUP_METHOD
     *   + "org.kuali.rice.krms.impl.repository.ContextBo"
     *   + ITUtil.SHOW_MAINTENANCE_LINKS
     *   + "&returnLocation=" + ITUtil.PORTAL_URL + ITUtil.HIDE_RETURN_LINK;
     */
    public static final String BOOKMARK_URL = ITUtil.PORTAL + "?channelTitle=Context%20Lookup&channelUrl="
            + WebDriverUtil.getBaseUrlString() + ITUtil.KRAD_LOOKUP_METHOD
            + "org.kuali.rice.krms.impl.repository.ContextBo"
            + ITUtil.SHOW_MAINTENANCE_LINKS
            + "&returnLocation=" + ITUtil.PORTAL_URL + ITUtil.HIDE_RETURN_LINK;
    /**
     * {@inheritDoc}
     * Context Lookup
     * @return
     */
    @Override
    protected String getLinkLocator() {
        return "Context Lookup";
    }

    public void testContextLookUpBookmark(Failable failable) throws Exception {
        contextLookupAssertions();
        passed();
    }
    public void testContextLookUpNav(Failable failable) throws Exception {
        contextLookupAssertions();
        passed();
    }
}