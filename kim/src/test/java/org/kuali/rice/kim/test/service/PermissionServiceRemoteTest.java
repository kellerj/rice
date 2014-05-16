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
package org.kuali.rice.kim.test.service;

import java.util.List;

import javax.xml.namespace.QName;

import org.kuali.rice.core.config.ConfigContext;
import org.kuali.rice.core.lifecycle.BaseLifecycle;
import org.kuali.rice.core.lifecycle.Lifecycle;
import org.kuali.rice.ksb.messaging.RemoteResourceServiceLocator;
import org.kuali.rice.ksb.messaging.RemotedServiceHolder;
import org.kuali.rice.ksb.messaging.ServiceInfo;
import org.kuali.rice.ksb.messaging.resourceloader.KSBResourceLoaderFactory;
import org.kuali.rice.ksb.messaging.serviceconnectors.SOAPConnector;
import org.kuali.rice.test.lifecycles.JettyServerLifecycle;

/**
 * Test the PermissionService via remote calls
 * 
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 *
 */
public class PermissionServiceRemoteTest extends PermissionServiceTest {

	public void setUp() throws Exception {
		super.setUp();
	}

	@Override
	protected Lifecycle getLoadApplicationLifecycle() {
		return new BaseLifecycle() {
			public void start() throws Exception {
				new JettyServerLifecycle(getConfigIntProp("kim.test.port"), "/" + getConfigProp("app.context.name"), "/../kim/src/test/webapp").start();
				super.start();
			}
		};	
	}
	
	private int getConfigIntProp(String intPropKey) {
		return Integer.parseInt(getConfigProp(intPropKey));
	}

	private String getConfigProp(String propKey) {
		return ConfigContext.getCurrentContextConfig().getProperty(propKey);
	}
	
	/**
	 * This method tries to get a client proxy for the specified KIM service
	 * 
	 * @param  svcName - name of the KIM service desired
	 * @return the proxy object
	 * @throws Exception 
	 */
	protected Object getKimService(String svcName) throws Exception {
		RemoteResourceServiceLocator rrl = KSBResourceLoaderFactory.getRemoteResourceLocator();
		List<RemotedServiceHolder> svcHolders = rrl.getAllServices(new QName("KIM", svcName));
		if (svcHolders.size() > 1) {
			fail("Found more than one RemotedServiceHolder for " + svcName);
		}
		ServiceInfo svcInfo = svcHolders.get(0).getServiceInfo();
		SOAPConnector connector = new SOAPConnector(svcInfo);
		return connector.getService();
	}
}