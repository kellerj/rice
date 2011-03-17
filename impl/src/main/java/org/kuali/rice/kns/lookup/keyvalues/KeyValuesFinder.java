/*
 * Copyright 2005-2007 The Kuali Foundation
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

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * This interface defines basic methods value finders.
 *
 *
 */
public interface KeyValuesFinder extends Serializable {

    /**
     * Builds a list of key values representations for valid value selections.
     *
     * @return List of KeyValue objects.
     */
    public List getKeyValues();

    /**
     * Builds a list of key values representations for valid value selections.
     *
     * @param includeActiveOnly whether to only include active values in the list
     *  
     * @return List of KeyValue objects.
     */
    public List getKeyValues(boolean includeActiveOnly);

    /**
     * Returns a map with the key as the key of the map and the label as the value. Used to render the label instead of the code in
     * the jsp when the field is readonly.
     *
     * @return
     */
    public Map getKeyLabelMap();

    /**
     * Returns the label for the associated key.
     *
     * @param key
     * @return
     */
    public String getKeyLabel(Object key);
    
   
    /**
     * Clears any internal cache that is being maintained by the value finder
     */
    public void clearInternalCache();

}
