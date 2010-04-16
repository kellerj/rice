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
package org.kuali.rice.kim.service.impl;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.xml.namespace.QName;

import org.apache.commons.collections.ListUtils;
import org.kuali.rice.kew.exception.WorkflowRuntimeException;
import org.kuali.rice.kew.messaging.MessageServiceNames;
import org.kuali.rice.kew.workgroup.WorkgroupMembershipChangeProcessor;
import org.kuali.rice.kim.bo.impl.GroupImpl;
import org.kuali.rice.kim.service.GroupInternalService;
import org.kuali.rice.kim.service.GroupService;
import org.kuali.rice.kim.service.KIMServiceLocator;
import org.kuali.rice.kns.service.BusinessObjectService;
import org.kuali.rice.kns.service.KNSServiceLocator;
import org.kuali.rice.ksb.messaging.service.KSBXMLService;
import org.kuali.rice.ksb.service.KSBServiceLocator;

/**
 * Concrete Implementation of {@link GroupInternalService}
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 *
 */
public class GroupInternalServiceImpl implements GroupInternalService {
    protected BusinessObjectService getBusinessObjectService() {
    	return KNSServiceLocator.getBusinessObjectService();
    }


    public GroupService getGroupService(){
    	return KIMServiceLocator.getGroupService();
    }

    public void saveWorkgroup(GroupImpl group) {
    	GroupService ims = getGroupService();
    	List<String> oldIds = ims.getMemberPrincipalIds(group.getGroupId());
        group = (GroupImpl)getBusinessObjectService().save( group );
        List<String> newIds = ims.getMemberPrincipalIds(group.getGroupId());
        updateForWorkgroupChange(group.getGroupId(), oldIds, newIds);
    }

    public void updateForWorkgroupChange(String groupId,
    		List<String> oldPrincipalIds, List<String> newPrincipalIds) {
        MembersDiff membersDiff = getMembersDiff(oldPrincipalIds, newPrincipalIds);
        for (String removedPrincipalId : membersDiff.getRemovedPrincipalIds()) {
        	updateForUserRemovedFromGroup(removedPrincipalId, groupId);
        }
        for (String addedPrincipalId : membersDiff.getAddedPrincipalIds()) {
        	updateForUserAddedToGroup(addedPrincipalId, groupId);
        }
    }

    public void updateForUserAddedToGroup(String principalId, String groupId) {
        // first verify that the user is still a member of the workgroup
    	if(getGroupService().isMemberOfGroup(principalId, groupId))
    	{
            KSBXMLService workgroupMembershipChangeProcessor = (KSBXMLService) KSBServiceLocator.getMessageHelper()
            .getServiceAsynchronously(new QName(MessageServiceNames.WORKGROUP_MEMBERSHIP_CHANGE_SERVICE));
            try {
                workgroupMembershipChangeProcessor.invoke(WorkgroupMembershipChangeProcessor
                        .getMemberAddedMessageContents(principalId, groupId));
            } catch (Exception e) {
                throw new WorkflowRuntimeException(e);
            }
    	}
    }

    public void updateForUserRemovedFromGroup(String principalId, String groupId) {
        // first verify that the user is no longer a member of the workgroup
    	if(!getGroupService().isMemberOfGroup(principalId, groupId))
    	{
            KSBXMLService workgroupMembershipChangeProcessor = (KSBXMLService) KSBServiceLocator.getMessageHelper()
            .getServiceAsynchronously(new QName(MessageServiceNames.WORKGROUP_MEMBERSHIP_CHANGE_SERVICE));
            try {
                workgroupMembershipChangeProcessor.invoke(WorkgroupMembershipChangeProcessor
                        .getMemberRemovedMessageContents(principalId, groupId));
            } catch (Exception e) {
                throw new WorkflowRuntimeException(e);
            }
    	}

    }

    private MembersDiff getMembersDiff(List<String> oldMemberPrincipalIds, List<String> newMemberPrincipalIds) {

    	// ListUtils does not check the null case.  Which can happen when adding a new group
    	// so, if they're null make them empty lists.
    	if(oldMemberPrincipalIds == null) oldMemberPrincipalIds = new ArrayList<String>();
    	if(newMemberPrincipalIds == null) newMemberPrincipalIds = new ArrayList<String>();

        Set<String> addedPrincipalIds = new HashSet<String>(ListUtils.subtract(newMemberPrincipalIds, oldMemberPrincipalIds));
        Set<String> removedPrincipalIds = new HashSet<String>(ListUtils.subtract(oldMemberPrincipalIds, newMemberPrincipalIds));
        return new MembersDiff(addedPrincipalIds, removedPrincipalIds);
    }

    private class MembersDiff {
        private final Set<String> addedPrincipalIds;

        private final Set<String> removedPrincipalIds;

        public MembersDiff(Set<String> addedPrincipalIds, Set<String>removedPrincipalIds) {
            this.addedPrincipalIds = addedPrincipalIds;
            this.removedPrincipalIds = removedPrincipalIds;
        }

        public Set<String> getAddedPrincipalIds() {
            return addedPrincipalIds;
        }

        public Set<String> getRemovedPrincipalIds() {
            return removedPrincipalIds;
        }
    }

}
