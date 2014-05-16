/*
 * Copyright 2005-2008 The Kuali Foundation
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
package org.kuali.rice.kns.lookup.keyvalues;

import java.util.ArrayList;
import java.util.List;

import org.kuali.rice.core.util.KeyLabelPair;
import org.kuali.rice.kns.util.KNSConstants;

/**
 * This class returns list of boolean key value pairs.
 * 
 * 
 */
public class IndicatorValuesFinder extends KeyValuesBase {

	public static final IndicatorValuesFinder INSTANCE = new IndicatorValuesFinder();
	
	protected static final List<KeyLabelPair> activeLabels = new ArrayList<KeyLabelPair>(3);
	static {
        activeLabels.add(new KeyLabelPair(KNSConstants.YES_INDICATOR_VALUE, "Yes"));
        activeLabels.add(new KeyLabelPair(KNSConstants.NO_INDICATOR_VALUE, "No"));
        activeLabels.add(new KeyLabelPair("", "Both"));
	}
	
    public List<KeyLabelPair> getKeyValues() {
        return activeLabels;
    }
}