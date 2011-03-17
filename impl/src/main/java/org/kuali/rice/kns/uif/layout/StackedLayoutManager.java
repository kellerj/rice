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
package org.kuali.rice.kns.uif.layout;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.kuali.rice.kns.uif.Component;
import org.kuali.rice.kns.uif.DataBinding;
import org.kuali.rice.kns.uif.UifConstants.Orientation;
import org.kuali.rice.kns.uif.container.CollectionGroup;
import org.kuali.rice.kns.uif.container.Container;
import org.kuali.rice.kns.uif.container.Group;
import org.kuali.rice.kns.uif.container.View;
import org.kuali.rice.kns.uif.field.ActionField;
import org.kuali.rice.kns.uif.field.Field;
import org.kuali.rice.kns.uif.util.ComponentUtils;
import org.kuali.rice.kns.uif.util.ObjectPropertyUtils;

/**
 * Layout manager that works with <code>CollectionGroup</code> containers and
 * renders the collection lines in a vertical row
 * 
 * <p>
 * For each line of the collection, a <code>Group</code> instance is created.
 * The group header contains a label for the line (summary information), the
 * group fields are the collection line fields, and the group footer contains
 * the line actions. All the groups are rendered using the
 * <code>BoxLayoutManager</code> with vertical orientation.
 * </p>
 * 
 * <p>
 * Modify the lineGroupPrototype to change header/footer styles or any other
 * customization for the line groups
 * </p>
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class StackedLayoutManager extends BoxLayoutManager implements CollectionLayoutManager {
	private static final long serialVersionUID = 4602368505430238846L;

	private String summaryTitle;
	private List<String> summaryFields;

	private Group addLineGroup;
	private Group lineGroupPrototype;

	private List<Group> stackedGroups;

	public StackedLayoutManager() {
		super();

		setOrientation(Orientation.VERTICAL);

		summaryFields = new ArrayList<String>();
		stackedGroups = new ArrayList<Group>();
	}

	/**
	 * The following initialization is performed:
	 * 
	 * <ul>
	 * <li>If add line group is not configured, set to new instance of line
	 * prototype</li>
	 * </ul>
	 * 
	 * @see org.kuali.rice.kns.uif.layout.BoxLayoutManager#performInitialization(org.kuali.rice.kns.uif.container.View,
	 *      org.kuali.rice.kns.uif.container.Container)
	 */
	@Override
	public void performInitialization(View view, Container container) {
		super.performInitialization(view, container);

		if (addLineGroup == null) {
			addLineGroup = ComponentUtils.copy(lineGroupPrototype);
		}
	}

	/**
	 * Builds a <code>Group</code> instance for a collection line. The group is
	 * built by first creating a copy of the configured prototype. Then the
	 * header for the group is created using the configured summary fields on
	 * the <code>CollectionGroup</code>. The line fields passed in are set as
	 * the items for the group, and finally the actions are placed into the
	 * group footer
	 * 
	 * @see org.kuali.rice.kns.uif.layout.CollectionLayoutManager#buildLine(org.kuali.rice.kns.uif.container.View,
	 *      java.lang.Object, org.kuali.rice.kns.uif.container.CollectionGroup,
	 *      java.util.List, java.lang.String, java.util.List, java.lang.String,
	 *      java.lang.Object, int)
	 */
	public void buildLine(View view, Object model, CollectionGroup collectionGroup, List<? extends Field> lineFields,
			String bindingPath, List<ActionField> actions, String idSuffix, Object currentLine, int lineIndex) {
		boolean isAddLine = lineIndex == -1;

		// construct new group
		Group lineGroup = null;
		if (isAddLine) {
			stackedGroups = new ArrayList<Group>();

			lineGroup = getAddLineGroup();
		}
		else {
			lineGroup = ComponentUtils.copy(lineGroupPrototype);
		}

		ComponentUtils.updateIdsAndContextForLine(lineGroup, currentLine, lineIndex);

		// build header text for group
		String headerText = "";
		if (isAddLine) {
			headerText = collectionGroup.getAddLineLabel();
		}
		else {
			// get the collection for this group from the model
			List<Object> modelCollection = ObjectPropertyUtils.getPropertyValue(model, ((DataBinding) collectionGroup)
					.getBindingInfo().getBindingPath());

			headerText = buildLineHeaderText(modelCollection.get(lineIndex));
		}
		lineGroup.getHeader().setHeaderText(headerText);

		lineGroup.setItems(lineFields);

		// set line actions on group footer
		if (collectionGroup.isRenderLineActions() && !collectionGroup.isReadOnly()) {
			lineGroup.getFooter().setItems(actions);
		}

		stackedGroups.add(lineGroup);
	}

	/**
	 * Builds the header text for the collection line
	 * 
	 * <p>
	 * Header text is built up by first the collection label, either specified
	 * on the collection definition or retrieved from the dictionary. Then for
	 * each summary field defined, the value from the model is retrieved and
	 * added to the header.
	 * </p>
	 * 
	 * @param line
	 *            - Collection line containing data
	 * @return String header text for line
	 */
	protected String buildLineHeaderText(Object line) {
		String summaryFieldString = "";
		for (String summaryField : summaryFields) {
			Object summaryFieldValue = ObjectPropertyUtils.getPropertyValue(line, summaryField);
			if (StringUtils.isNotBlank(summaryFieldString)) {
				summaryFieldString += " - ";
			}

			if (summaryFieldValue != null) {
				summaryFieldString += summaryFieldValue;
			}
			else {
				summaryFieldString += "Null";
			}
		}

		String headerText = summaryTitle;
		if (StringUtils.isNotBlank(summaryFieldString)) {
			headerText += " ( " + summaryFieldString + " )";
		}

		return headerText;
	}

	/**
	 * @see org.kuali.rice.kns.uif.layout.ContainerAware#getSupportedContainer()
	 */
	@Override
	public Class<? extends Container> getSupportedContainer() {
		return CollectionGroup.class;
	}

	/**
	 * @see org.kuali.rice.kns.uif.layout.LayoutManagerBase#getNestedComponents()
	 */
	@Override
	public List<Component> getNestedComponents() {
		List<Component> components = super.getNestedComponents();

		components.add(lineGroupPrototype);
		components.addAll(stackedGroups);

		return components;
	}

	/**
	 * Text to appears in the header for each collection lines Group. Used in
	 * conjunction with {@link #getSummaryFields()} to build up the final header
	 * text
	 * 
	 * @return String summary title text
	 */
	public String getSummaryTitle() {
		return this.summaryTitle;
	}

	/**
	 * Setter for the summary title text
	 * 
	 * @param summaryTitle
	 */
	public void setSummaryTitle(String summaryTitle) {
		this.summaryTitle = summaryTitle;
	}

	/**
	 * List of attribute names from the collection line class that should be
	 * used to build the line summary. To build the summary the value for each
	 * attribute is retrieved from the line instance. All the values are then
	 * placed together with a separator.
	 * 
	 * @return List<String> summary field names
	 * @see #buildLineHeaderText(java.lang.Object)
	 */
	public List<String> getSummaryFields() {
		return this.summaryFields;
	}

	/**
	 * Setter for the summary field name list
	 * 
	 * @param summaryFields
	 */
	public void setSummaryFields(List<String> summaryFields) {
		this.summaryFields = summaryFields;
	}

	/**
	 * Group instance that will be used for the add line
	 * 
	 * <p>
	 * Add line fields and actions configured on the
	 * <code>CollectionGroup</code> will be set onto the add line group (if add
	 * line is enabled). If the add line group is not configured, a new instance
	 * of the line group prototype will be used for the add line.
	 * </p>
	 * 
	 * @return Group add line group instance
	 * @see #getAddLineGroup()
	 */
	public Group getAddLineGroup() {
		return this.addLineGroup;
	}

	/**
	 * Setter for the add line group
	 * 
	 * @param addLineGroup
	 */
	public void setAddLineGroup(Group addLineGroup) {
		this.addLineGroup = addLineGroup;
	}

	/**
	 * Group instance that is used as a prototype for creating the collection
	 * line groups. For each line a copy of the prototype is made and then
	 * adjusted as necessary
	 * 
	 * @return Group instance to use as prototype
	 */
	public Group getLineGroupPrototype() {
		return this.lineGroupPrototype;
	}

	/**
	 * Setter for the line group prototype
	 * 
	 * @param lineGroupPrototype
	 */
	public void setLineGroupPrototype(Group lineGroupPrototype) {
		this.lineGroupPrototype = lineGroupPrototype;
	}

	/**
	 * Final <code>List</code> of Groups to render for the collection
	 * 
	 * @return List<Group> collection groups
	 */
	public List<Group> getStackedGroups() {
		return this.stackedGroups;
	}

	/**
	 * Setter for the collection groups
	 * 
	 * @param stackedGroups
	 */
	public void setStackedGroups(List<Group> stackedGroups) {
		this.stackedGroups = stackedGroups;
	}

}
