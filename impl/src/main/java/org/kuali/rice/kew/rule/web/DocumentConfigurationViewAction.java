/*
 * Copyright 2008-2009 The Kuali Foundation
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
package org.kuali.rice.kew.rule.web;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.kuali.rice.kew.doctype.bo.DocumentType;
import org.kuali.rice.kew.doctype.service.DocumentTypeService;
import org.kuali.rice.kew.engine.node.RouteNode;
import org.kuali.rice.kew.engine.node.service.RouteNodeService;
import org.kuali.rice.kew.service.KEWServiceLocator;
import org.kuali.rice.kew.util.KEWConstants;
import org.kuali.rice.kew.web.KewKualiAction;
import org.kuali.rice.kim.bo.impl.KimAttributes;
import org.kuali.rice.kim.bo.role.dto.KimPermissionInfo;
import org.kuali.rice.kim.bo.role.dto.KimPermissionTemplateInfo;
import org.kuali.rice.kim.bo.role.dto.KimResponsibilityInfo;
import org.kuali.rice.kim.bo.role.dto.KimRoleInfo;
import org.kuali.rice.kim.bo.role.impl.KimPermissionImpl;
import org.kuali.rice.kim.bo.role.impl.KimResponsibilityImpl;
import org.kuali.rice.kim.bo.types.dto.AttributeSet;
import org.kuali.rice.kim.service.KIMServiceLocator;
import org.kuali.rice.kim.service.PermissionService;
import org.kuali.rice.kim.service.ResponsibilityService;
import org.kuali.rice.kim.service.RoleManagementService;
import org.kuali.rice.kns.service.DataDictionaryService;
import org.kuali.rice.kns.service.DocumentHelperService;
import org.kuali.rice.kns.service.KNSServiceLocator;
import org.kuali.rice.kns.service.MaintenanceDocumentDictionaryService;
import org.kuali.rice.kns.util.GlobalVariables;
import org.kuali.rice.kns.util.KNSConstants;

/**
 * This is a description of what this class does - kellerj don't forget to fill this in. 
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 *
 */
public class DocumentConfigurationViewAction extends KewKualiAction {

	private static final Logger LOG = Logger.getLogger(DocumentConfigurationViewAction.class);
	
	private PermissionService permissionService;
	private RoleManagementService roleService;
	private ResponsibilityService responsibilityService;
	private DocumentTypeService documentTypeService;
	private DataDictionaryService dataDictionaryService;
	private RouteNodeService routeNodeService;
	private MaintenanceDocumentDictionaryService maintenanceDocumentDictionaryService;
	private DocumentHelperService documentHelperService;
	
    public ActionForward start(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
    	populateForm( (DocumentConfigurationViewForm)form );
        return mapping.findForward("basic");
    }
    
    protected void populateForm( DocumentConfigurationViewForm form ) {
    	if ( StringUtils.isNotEmpty( form.getDocumentTypeName() ) ) {
    		form.setDocumentType( getDocumentTypeService().findByName( form.getDocumentTypeName() ) ); 
        	if ( form.getDocumentType() != null ) {
	    		form.getDocumentType().getChildrenDocTypes();
	    		form.setAttributeLabels( new HashMap<String, String>() );
	    		populateRelatedDocuments( form );
	    		populatePermissions( form );
	    		populateRoutingResponsibilities( form );
	    		populateRoutingExceptionResponsibility( form );
	    		checkPermissions( form );
        	}
    	}
    }

    protected void checkPermissions( DocumentConfigurationViewForm form ) {
    	String docTypeDocumentType = getMaintenanceDocumentDictionaryService().getDocumentTypeName(DocumentType.class);
        try {
            if ((docTypeDocumentType != null) && getDocumentHelperService().getDocumentAuthorizer(docTypeDocumentType).canInitiate(docTypeDocumentType, GlobalVariables.getUserSession().getPerson())) {
                form.setCanInitiateDocumentTypeDocument( true );
            }
        } catch (Exception ex) {
			// just skip - and don't display links
        	LOG.error( "Unable to check DocumentType initiation permission for "+ docTypeDocumentType, ex );
		}
    	String permissionDocumentType = getMaintenanceDocumentDictionaryService().getDocumentTypeName(KimPermissionImpl.class);
        try {
            if ((permissionDocumentType != null) && getDocumentHelperService().getDocumentAuthorizer(permissionDocumentType).canInitiate(permissionDocumentType, GlobalVariables.getUserSession().getPerson())) {
                form.setCanInitiatePermissionDocument( true );
            }
        } catch (Exception ex) {
			// just skip - and don't display links
        	LOG.error( "Unable to check Permission initiation permission for "+ permissionDocumentType, ex );
		}
    	String responsibilityDocumentType = getMaintenanceDocumentDictionaryService().getDocumentTypeName(KimResponsibilityImpl.class);
        try {
            if ((responsibilityDocumentType != null) && getDocumentHelperService().getDocumentAuthorizer(responsibilityDocumentType).canInitiate(responsibilityDocumentType, GlobalVariables.getUserSession().getPerson())) {
                form.setCanInitiateResponsibilityDocument( true );
            }
        } catch (Exception ex) {
			// just skip - and don't display links
        	LOG.error( "Unable to check Responsibility initiation permission for "+ responsibilityDocumentType, ex );
		}
    }
    
    @SuppressWarnings("unchecked")
	public void populateRelatedDocuments( DocumentConfigurationViewForm form ) {
    	form.setParentDocumentType( form.getDocumentType().getParentDocType() );
    	form.setChildDocumentTypes( new ArrayList<DocumentType>( form.getDocumentType().getChildrenDocTypes() ) );
    }
    
	public void populatePermissions( DocumentConfigurationViewForm form ) {
		
		DocumentType docType = form.getDocumentType();
		Map<String,List<KimRoleInfo>> permRoles = new HashMap<String, List<KimRoleInfo>>(); 
		Map<String,String> searchCriteria = new HashMap<String,String>();
		searchCriteria.put("attributeName", "documentTypeName" );
		searchCriteria.put("active", "Y");
		// loop over the document hierarchy
		Set<String> seenDocumentPermissions = new HashSet<String>();
		while ( docType != null) {
			String documentTypeName = docType.getName();
			searchCriteria.put("detailCriteria",
					KimAttributes.DOCUMENT_TYPE_NAME+"="+docType.getName()
					);
			List<KimPermissionInfo> perms = getPermissionService().lookupPermissions( searchCriteria, true );
			for ( KimPermissionInfo perm : perms ) {
				List<String> roleIds = getPermissionService().getRoleIdsForPermissions(Collections.singletonList(perm));
				permRoles.put( perm.getPermissionId(), getRoleService().getRoles(roleIds) );
				for ( String attributeName : perm.getDetails().keySet() ) {
					addAttributeLabel(form, attributeName);
				}
			}
			// show the section if the current document or permissions exist
			if ( perms.size() > 0 || documentTypeName.equals( form.getDocumentTypeName() ) ) {
				ArrayList<PermissionForDisplay> dispPerms = new ArrayList<PermissionForDisplay>( perms.size() );
				for ( KimPermissionInfo perm : perms ) {
					if ( perm.getDetails().size() == 1  ) { // only the document type
						// this is a document type-specific permission, check if seen earlier
						if ( seenDocumentPermissions.contains(perm.getTemplate().getNamespaceCode()+"|"+perm.getTemplate().getName()) ) {
							dispPerms.add( new PermissionForDisplay( perm, true ) );
						} else {
							dispPerms.add( new PermissionForDisplay( perm, false ) );
							seenDocumentPermissions.add(perm.getTemplate().getNamespaceCode()+"|"+perm.getTemplate().getName());
						}
					} else {
						// other attributes, can't determine whether this is overridden at another level
						dispPerms.add( new PermissionForDisplay( perm, false ) );						
					}
				}
				form.setPermissionsForDocumentType(documentTypeName, dispPerms );
				form.addDocumentType(documentTypeName);
			}
			docType = docType.getParentDocType();			
		}
		
		form.setPermissionRoles( permRoles );
	}
	
	protected void populateRoutingExceptionResponsibility( DocumentConfigurationViewForm form ) {	
		Map<String,String> searchCriteria = new HashMap<String,String>();
		searchCriteria.put("template.namespaceCode", KNSConstants.KUALI_RICE_WORKFLOW_NAMESPACE);
		searchCriteria.put("template.name", KEWConstants.EXCEPTION_ROUTING_RESPONSIBILITY_TEMPLATE_NAME);
		searchCriteria.put("active", "Y");
		DocumentType docType = form.getDocumentType();
		List<ResponsibilityForDisplay> responsibilities = new ArrayList<ResponsibilityForDisplay>();
		while ( docType != null) {
			// pull the responsibilities for this node
			searchCriteria.put("detailCriteria",
					KimAttributes.DOCUMENT_TYPE_NAME+"="+docType.getName()
					);		
			List<? extends KimResponsibilityInfo> resps = getResponsibilityService().lookupResponsibilityInfo( searchCriteria, true );
			
			for ( KimResponsibilityInfo r : resps ) {
				if ( responsibilities.isEmpty() ) {
					responsibilities.add( new ResponsibilityForDisplay( r, false ) );
				} else {
					responsibilities.add( new ResponsibilityForDisplay( r, true ) );
				}
			}
			docType = docType.getParentDocType();			
		}
		form.setExceptionResponsibilities( responsibilities );
		for ( ResponsibilityForDisplay responsibility : responsibilities ) {
			List<String> roleIds = getResponsibilityService().getRoleIdsForResponsibility(responsibility.getResp(), null);
			form.getResponsibilityRoles().put( responsibility.getResponsibilityId(), getRoleService().getRoles(roleIds) );
		}
	}

	protected void addAttributeLabel( DocumentConfigurationViewForm form, String attributeName ) {
		if ( !form.getAttributeLabels().containsKey(attributeName) ) {
			form.getAttributeLabels().put(attributeName, 
					getDataDictionaryService().getAttributeLabel(KimAttributes.class, attributeName) );
		}
	}
	
	// loop over nodes
	// if split node, push onto stack
		// note the number of children, this is the number of times the join node needs to be found
	// when join node found, return to last split on stack
		// move to next child of the split
	
	protected RouteNode flattenSplitNode( RouteNode splitNode, Map<String,RouteNode> nodes ) {
		nodes.put( splitNode.getRouteNodeName(), splitNode );
		RouteNode joinNode = null;
		
		for ( RouteNode nextNode : splitNode.getNextNodes() ) {
			joinNode = flattenRouteNodes(nextNode, nodes);
		}
		
		if ( joinNode != null ) {
			nodes.put( joinNode.getRouteNodeName(), joinNode );
		}
		return joinNode;
	}
	
	/**
	 * @param node
	 * @param nodes
	 * @return The last node processed by this method.
	 */
	protected RouteNode flattenRouteNodes( RouteNode node, Map<String,RouteNode> nodes ) {
		RouteNode lastProcessedNode = null;
		// if we've seen the node before - skip, avoids infinite loop
		if ( nodes.containsKey(node.getRouteNodeName()) ) {
			return node;
		}
		
		if ( node.getNodeType().contains( "SplitNode" ) ) { // Hacky - but only way when the class may not be present in the KEW JVM
			lastProcessedNode = flattenSplitNode(node, nodes); // special handling to process all split children before continuing
			// now, process the join node's children
			for ( RouteNode nextNode : lastProcessedNode.getNextNodes() ) {
				lastProcessedNode = flattenRouteNodes(nextNode, nodes);
			}
		} else if ( node.getNodeType().contains( "JoinNode" ) ) {
			lastProcessedNode = node; // skip, handled by the split node
		} else {
			// normal node, add to list and process all children
			nodes.put(node.getRouteNodeName(), node);
			for ( RouteNode nextNode : node.getNextNodes() ) {
				lastProcessedNode = flattenRouteNodes(nextNode, nodes);
			}
		}
		return lastProcessedNode;
	}
	
	@SuppressWarnings("unchecked")
	public void populateRoutingResponsibilities( DocumentConfigurationViewForm form ) {
		// pull all the responsibilities
		// merge the data and attach to route levels
		// pull the route levels and store on form
		//List<RouteNode> routeNodes = getRouteNodeService().getFlattenedNodes(form.getDocumentType(), true);
		RouteNode rootNode = ((List<org.kuali.rice.kew.engine.node.Process>)form.getDocumentType().getProcesses()).get(0).getInitialRouteNode();
		LinkedHashMap<String, RouteNode> routeNodeMap = new LinkedHashMap<String, RouteNode>();
		flattenRouteNodes(rootNode, routeNodeMap);
		
		form.setRouteNodes( new ArrayList<RouteNode>( routeNodeMap.values() ) );
		// pull all the responsibilities and store into a map for use by the JSP
		
		// FILTER TO THE "Review" template only
		// pull responsibility roles
		Map<String,String> searchCriteria = new HashMap<String,String>();
		searchCriteria.put("template.namespaceCode", KNSConstants.KUALI_RICE_WORKFLOW_NAMESPACE);
		searchCriteria.put("template.name", KEWConstants.DEFAULT_RESPONSIBILITY_TEMPLATE_NAME);
		searchCriteria.put("active", "Y");
		DocumentType docType = form.getDocumentType();
		Set<KimResponsibilityInfo> responsibilities = new HashSet<KimResponsibilityInfo>();
		Map<String,List<ResponsibilityForDisplay>> nodeToRespMap = new LinkedHashMap<String, List<ResponsibilityForDisplay>>();
		while ( docType != null) {
			// pull the responsibilities for this node
			searchCriteria.put("detailCriteria",
					KimAttributes.DOCUMENT_TYPE_NAME+"="+docType.getName()
					);		
			List<? extends KimResponsibilityInfo> resps = getResponsibilityService().lookupResponsibilityInfo( searchCriteria, true );
			
			for ( KimResponsibilityInfo r : resps ) {
				String routeNodeName = r.getDetails().get(KimAttributes.ROUTE_NODE_NAME); 
				if ( StringUtils.isNotBlank(routeNodeName) ) {
					if ( !nodeToRespMap.containsKey( routeNodeName ) ) {
						nodeToRespMap.put(routeNodeName, new ArrayList<ResponsibilityForDisplay>() );
						nodeToRespMap.get(routeNodeName).add( new ResponsibilityForDisplay( r, false ) );
					} else {
						// check if the responsibility in the existing list is for the current document
						// if so, OK to add.  Otherwise, a lower level document has overridden this
						// responsibility (since we are walking up the hierarchy
						if ( nodeToRespMap.get(routeNodeName).get(0).getDetails().get( KimAttributes.DOCUMENT_TYPE_NAME ).equals(docType.getName() ) ) {
							nodeToRespMap.get(routeNodeName).add( new ResponsibilityForDisplay( r, false ) );
						} else { // doc type name did not match, mark as overridden
							nodeToRespMap.get(routeNodeName).add( new ResponsibilityForDisplay( r, true ) );
						}
					}
					responsibilities.add(r);
				}
			}
			docType = docType.getParentDocType();			
		}
		form.setResponsibilityMap( nodeToRespMap );
		
		Map<String,List<KimRoleInfo>> respToRoleMap = new HashMap<String, List<KimRoleInfo>>();
		for ( KimResponsibilityInfo responsibility : responsibilities ) {
			List<String> roleIds = getResponsibilityService().getRoleIdsForResponsibility(responsibility, null);
			respToRoleMap.put( responsibility.getResponsibilityId(), getRoleService().getRoles(roleIds) );
		}
		form.setResponsibilityRoles( respToRoleMap );
	}
	
	/**
	 * @see org.kuali.rice.kns.web.struts.action.KualiAction#toggleTab(org.apache.struts.action.ActionMapping, org.apache.struts.action.ActionForm, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	@Override
	public ActionForward toggleTab(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
		// Repopulating the form is necessary when toggling tab states on the server side.
		ActionForward actionForward = super.toggleTab(mapping, form, request, response);
		populateForm( (DocumentConfigurationViewForm)form );
		return actionForward;
	}

	/**
	 * Internal delegate class to wrap a responsibility and add an overridden flag.
	 */
	public static class ResponsibilityForDisplay {

		private KimResponsibilityInfo resp;
		private boolean overridden = false;
		
		public ResponsibilityForDisplay( KimResponsibilityInfo resp, boolean overridden ) {
			this.resp = resp;
			this.overridden = overridden;
		}
		
		/**
		 * @return the resp
		 */
		KimResponsibilityInfo getResp() {
			return this.resp;
		}
		
		public boolean isOverridden() {
			return this.overridden;
		}

		public void setOverridden(boolean overridden) {
			this.overridden = overridden;
		}

		public AttributeSet getDetails() {
			return this.resp.getDetails();
		}

		public String getName() {
			return this.resp.getName();
		}

		public String getNamespaceCode() {
			return this.resp.getNamespaceCode();
		}

		public String getResponsibilityId() {
			return this.resp.getResponsibilityId();
		}
	}

	public static class PermissionForDisplay {
		private KimPermissionInfo perm;
		private boolean overridden = false;
		
		public PermissionForDisplay( KimPermissionInfo perm, boolean overridden ) {
			this.perm = perm;
			this.overridden = overridden;
		}
		public boolean isOverridden() {
			return this.overridden;
		}

		public void setOverridden(boolean overridden) {
			this.overridden = overridden;
		}
		public AttributeSet getDetails() {
			return this.perm.getDetails();
		}
		public String getName() {
			return this.perm.getName();
		}
		public String getNamespaceCode() {
			return this.perm.getNamespaceCode();
		}
		public String getPermissionId() {
			return this.perm.getPermissionId();
		}
		public KimPermissionTemplateInfo getTemplate() {
			return this.perm.getTemplate();
		}
		
	}
	
	/**
	 * @return the permissionService
	 */
	public PermissionService getPermissionService() {
		if ( permissionService == null ) {
			permissionService = KIMServiceLocator.getPermissionService();
		}
		return permissionService;
	}

	/**
	 * @return the roleService
	 */
	public RoleManagementService getRoleService() {
		if ( roleService == null ) {
			roleService = KIMServiceLocator.getRoleManagementService();
		}
		return roleService;
	}

	/**
	 * @return the responsibilityService
	 */
	public ResponsibilityService getResponsibilityService() {
		if ( responsibilityService == null ) {
			responsibilityService = KIMServiceLocator.getResponsibilityService();
		}
		return responsibilityService;
	}

	/**
	 * @return the documentTypeService
	 */
	public DocumentTypeService getDocumentTypeService() {
		if ( documentTypeService == null ) {
			documentTypeService = KEWServiceLocator.getDocumentTypeService();
		}
		return documentTypeService;
	}

	public DataDictionaryService getDataDictionaryService() {
		if(dataDictionaryService == null){
			dataDictionaryService = KNSServiceLocator.getDataDictionaryService();
		}
		return dataDictionaryService;
	}

	public RouteNodeService getRouteNodeService() {
		if ( routeNodeService == null ) {
			routeNodeService = KEWServiceLocator.getRouteNodeService();
		}
		return routeNodeService;
	}

	public DocumentHelperService getDocumentHelperService() {
		if(documentHelperService == null){
			documentHelperService = KNSServiceLocator.getDocumentHelperService();
		}
		return documentHelperService;
	}

	public MaintenanceDocumentDictionaryService getMaintenanceDocumentDictionaryService() {
		if(maintenanceDocumentDictionaryService == null){
			maintenanceDocumentDictionaryService = KNSServiceLocator.getMaintenanceDocumentDictionaryService();
		}
		return maintenanceDocumentDictionaryService;
	}
	
}