/*
 * Copyright 2007 The Kuali Foundation
 *
 * Licensed under the Educational Community License, Version 1.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.opensource.org/licenses/ecl1.php
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package edu.iu.uis.eden.messaging;

import org.kuali.rice.core.Core;
import org.kuali.rice.ksb.util.KSBConstants;

import edu.iu.uis.eden.messaging.resourceloading.KSBResourceLoaderFactory;

/**
 * Holder for common testing code. 
 * 
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 *
 */
public class KSBTestUtils {

    public static void setMessagingToAsync() {
        setupMessaging("async");
    }

    public static void setMessagingToSync() {
        setupMessaging(KSBConstants.MESSAGING_SYNCHRONOUS);
    }

    private static void setupMessaging(String value) {
        Core.getCurrentContextConfig().overrideProperty(KSBConstants.MESSAGE_DELIVERY, value);
        ((Runnable) KSBResourceLoaderFactory.getRemoteResourceLocator()).run();
    }

}
