<%@ include file="/kr/WEB-INF/jsp/tldHeader.jsp"%>
<%@ attribute name="mbrIdx" required="true" %>
<c:set var="roleMember" value="${KualiForm.document.members[mbrIdx]}"/>
<c:set var="docRoleRspActionAttributes" value="${DataDictionary.KimDocumentRoleResponsibilityAction.attributes}" />
<c:set var="readOnly" value="${!KualiForm.documentActions[Constants.KUALI_ACTION_CAN_EDIT]}" />

<c:if test="${readOnly}">
	<c:set var="inquiry" value="${readOnly}"/>
</c:if>

<kul:subtab lookedUpCollectionName="roleRspActions" noShowHideButton="true" width="${tableWidth}" subTabTitle="Responsibility Actions">      
    <table cellpadding=0 cellspacing=0 summary="">
      	<tr>
            <th width="5%" rowspan=20 style=border-style:none>&nbsp;</th>
			<kul:htmlAttributeHeaderCell literalLabel="Name"  align="center"/>
			<kul:htmlAttributeHeaderCell attributeEntry="${docRoleRspActionAttributes.actionTypeCode}"  align="center"/>
         	<kul:htmlAttributeHeaderCell attributeEntry="${docRoleRspActionAttributes.priorityNumber}"  align="center" />
         	<kul:htmlAttributeHeaderCell attributeEntry="${docRoleRspActionAttributes.actionPolicyCode}"  align="center" />
         	<kul:htmlAttributeHeaderCell attributeEntry="${docRoleRspActionAttributes.ignorePrevious}"  align="center" />
       	</tr>
		<c:forEach var="roleRspAction" items="${roleMember.roleRspActions}" varStatus="actionStatus">
           	<tr>	
				<td>
					<div align="center">
					${roleRspAction.kimResponsibility.namespaceCode}
					${roleRspAction.kimResponsibility.nameToDisplay}		            
					</div>
        		</td>
				<td>
					<div align="center">
		            	<kul:htmlControlAttribute property="document.members[${mbrIdx}].roleRspActions[${actionStatus.index}].actionTypeCode"  attributeEntry="${docRoleRspActionAttributes.actionTypeCode}" readOnlyAlternateDisplay="${roleRspAction.actionTypeDescription}" readOnly="${readOnly}" />
		            </div>
        		</td>
        		<td>
	        		<div align="center">
		            	<kul:htmlControlAttribute property="document.members[${mbrIdx}].roleRspActions[${actionStatus.index}].priorityNumber"  attributeEntry="${docRoleRspActionAttributes.priorityNumber}" readOnly="${readOnly}" />
	        		</div>
        		</td>
        		<td>
	        		<div align="center">
		            	<kul:htmlControlAttribute property="document.members[${mbrIdx}].roleRspActions[${actionStatus.index}].actionPolicyCode"  attributeEntry="${docRoleRspActionAttributes.actionPolicyCode}" readOnlyAlternateDisplay="${roleRspAction.actionPolicyDescription}" readOnly="${readOnly}" />
	        		</div>
        		</td>
	       		<td>
	        		<div align="center">
		            	<kul:htmlControlAttribute property="document.members[${mbrIdx}].roleRspActions[${actionStatus.index}].ignorePrevious"  attributeEntry="${docRoleRspActionAttributes.ignorePrevious}" readOnly="${readOnly}" />
	        		</div>
	       		</td>
		    </tr>
		</c:forEach>
		<tr>
            <td colspan=7 style="padding:0px; border-style:none; height:22px; background-color:#F6F6F6">&nbsp;</td>
        </tr>		        	
	</table>       
</kul:subtab>