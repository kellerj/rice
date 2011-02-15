/*
 * Copyright 2011 The Kuali Foundation
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
package org.kuali.rice.kns.datadictionary.validation.processor;

import org.kuali.rice.kns.datadictionary.validation.capability.Validatable;
import org.kuali.rice.kns.datadictionary.validation.constraint.ConstraintProcessor;

/**
 * This abstract class can be extended by constraint processor classes that
 * must be processed on every validation.  
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org) 
 */
public abstract class MandatoryElementConstraintProcessor<D extends Validatable> implements ConstraintProcessor<Object, D> {

	/**
	 * @see org.kuali.rice.kns.datadictionary.validation.constraint.ConstraintProcessor#isOptional()
	 */
	@Override
	public boolean isOptional() {
		return false;
	}

}
