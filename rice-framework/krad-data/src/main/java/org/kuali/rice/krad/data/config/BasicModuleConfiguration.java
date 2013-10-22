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
package org.kuali.rice.krad.data.config;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.kuali.rice.krad.data.provider.Provider;

/**
 * Entry point for application configuration of KRAD/KNS providers
 */
public class BasicModuleConfiguration {
	@SuppressWarnings("unchecked")
	protected List<Provider> providers = Collections.unmodifiableList(Collections.EMPTY_LIST);

    /**
     * Sets the list of providers for this module
     * @param providers list of providers
     */
    public void setProviders(List<Provider> providers) {
        this.providers = Collections.unmodifiableList(new ArrayList<Provider>(providers));
    }

    public List<Provider> getProviders() {
        return providers;
    }
}