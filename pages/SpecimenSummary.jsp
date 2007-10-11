<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html"%>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic"%>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean"%>
<%@ taglib uri="/WEB-INF/nlevelcombo.tld" prefix="ncombo" %>
<%@ page import="edu.wustl.catissuecore.util.global.Constants"%>
<link rel="stylesheet" type="text/css" href="css/styleSheet.css" />
<html>
<head>
<script language="JavaScript" type="text/javascript" src="jss/javaScript.js"></script>
<%
String formAction = "SubmitSpecimenCollectionProtocol.do";
if(request.getAttribute(Constants.PAGEOF) != null)
{
	formAction = formAction + "?pageOf="+request.getAttribute(Constants.PAGEOF);
}

%>
	<script language="JavaScript">
		window.parent.frames['SpecimenEvents'].location="ShowCollectionProtocol.do?pageOf=specimenEventsPage&operation=ViewSummary";
		function saveCollectionProtocol()
		{
				var action ="SubmitSpecimenCollectionProtocol.do?action=collectionprotocol";
				document.forms[0].action = action;
				document.forms[0].submit();
		}
		
		function confirmDisable()
		{		
			var go = confirm("Disabling any data will disable ALL its associated data also. Once disabled you will not be able to recover any of the data back from the system. Please refer to the user manual for more details. \n Do you really want to disable?");
			if (go==true)
			{
				document.forms[0].target = "_top";
				var action ="SubmitSpecimenCollectionProtocol.do";
				document.forms[0].action = action;
				document.forms[0].submit();
			}
							
		}
		
	</script>
</head>

		<html:errors />
		<html:messages id="messageKey" message="true" header="messages.header" footer="messages.footer">
			<%=messageKey%>
		</html:messages>
		
		<html:form action="<%=formAction%>">		
		<table summary="" cellpadding="0" cellspacing="0" border="0">
		<tr>
				<td>			
					&nbsp;&nbsp;
				</td>
			</tr>
			<tr>
				<td class="dataTablePrimaryLabel" height="20">
					<bean:write name="viewSpecimenSummaryForm" property="title" />
				</td>				
				<logic:equal name="viewSpecimenSummaryForm" property="requestType" value="Multiple Specimen">
					<script language="javascript">
							refreshTree('<%=Constants.CP_AND_PARTICIPANT_VIEW%>','<%=Constants.CP_TREE_VIEW%>','<%=Constants.CP_SEARCH_CP_ID%>','<%=Constants.CP_SEARCH_PARTICIPANT_ID%>','1');	
					</script>
				</logic:equal>
			</tr>
			<logic:empty name="viewSpecimenSummaryForm" property="specimenList" >
				<tr>
					<td class="dataTableWhiteCenterHeader" colspan="9">  
							No specimens to display for current action!!
					</td>
				</tr>		
			</logic:empty>

			<logic:notEmpty name="viewSpecimenSummaryForm" property="specimenList" >	
			
				<tr>	
				<td>
					<table summary="" cellpadding="3" cellspacing="0" border="0"  class="dataTable" width="100%">
						<tr>
								<th class="formSerialNumberLabelForTable" scope="col" > &nbsp </th>
								<logic:equal name="viewSpecimenSummaryForm" property="requestType" value="anticipatory specimens">
								<th class="formSerialNumberLabelForTable" scope="col">Received</th>
								</logic:equal>
									<th class="formSerialNumberLabelForTable" scope="col">Label</th>
									<th class="formSerialNumberLabelForTable" scope="col"> Class</th>
									<th class="formSerialNumberLabelForTable" scope="col"> Type</th>
									<th class="formSerialNumberLabelForTable" scope="col"> Tissue Site</th>
									<th class="formSerialNumberLabelForTable" scope="col"> Tissue Side</th>
									<th class="formSerialNumberLabelForTable" scope="col"> Pathological Status</th>
									<th class="formSerialNumberLabelForTable" scope="col"> Storage</th>
									<th class="formSerialNumberLabelForTable" scope="col"> Qty</th>
					</tr>
				  <logic:iterate name="viewSpecimenSummaryForm" property="specimenList" id="specimen" indexId="counter">
					<tr>
						<td class="dataCellText">
						<html:radio property="selectedSpecimenId" value="uniqueIdentifier" idName="specimen" 
						onclick=" form.action='GenericSpecimenSummary.do'; submit()"/> 
						<html:hidden name="specimen" indexed="true" property="uniqueIdentifier" /></td>
						
						<logic:equal name="viewSpecimenSummaryForm" property="requestType" value="anticipatory specimens">
							<logic:equal name="specimen" property="readOnly" value="false">
								<td>
									<html:checkbox name="specimen" indexed="true" property="checkedSpecimen" />					
								</td>
							</logic:equal>
							<logic:equal name="specimen" property="readOnly" value="true">
								<td>
									<html:checkbox name="specimen" indexed="true" property="checkedSpecimen" disabled="true" value="true"/>
									<html:hidden name="specimen" indexed="true" property="checkedSpecimen" />					
								</td>
							</logic:equal>
							<html:hidden name="specimen" indexed="true" property="readOnly"/>
						</logic:equal>
						<td class="dataCellText" > <bean:write name="specimen" property="displayName" />
						<html:hidden name="specimen" indexed="true" property="displayName" /></td>
						<td class="dataCellText"> <bean:write name="specimen" property="className" />
						<html:hidden name="specimen" indexed="true" property="className" /></td>
						<td class="dataCellText"> <bean:write name="specimen" property="type" />
						<html:hidden name="specimen" indexed="true" property="type" /></td>
						<td class="dataCellText"> <bean:write name="specimen" property="tissueSite" />
						<html:hidden name="specimen" indexed="true" property="tissueSite" /></td>
						<td class="dataCellText"> <bean:write name="specimen" property="tissueSide" />
						 <html:hidden name="specimen" indexed="true" property="tissueSide" /></td>
						<td class="dataCellText"> <bean:write name="specimen" property="pathologicalStatus" />
						<html:hidden name="specimen" indexed="true" property="pathologicalStatus" /></td>

						<logic:empty name="viewSpecimenSummaryForm" property="eventId">
							<logic:equal name="specimen" property="storageContainerForSpecimen" value="Virtual">
							
								<td class="dataCellText"> <bean:write name="specimen" property="storageContainerForSpecimen" />
								<html:hidden name="specimen" indexed="true" property="storageContainerForSpecimen" /></td>
							</logic:equal>
							
							<logic:notEqual name="specimen" property="storageContainerForSpecimen" value="Virtual">
								<td class="dataCellText"> <html:text size="10" name="specimen" indexed="true" property="storageContainerForSpecimen" /></td>
							</logic:notEqual>
						</logic:empty>
						<logic:notEmpty name="viewSpecimenSummaryForm" property="eventId">
								<td class="dataCellText"> <bean:write name="specimen" property="storageContainerForSpecimen" />
								<html:hidden name="specimen" indexed="true" property="storageContainerForSpecimen" /></td>
						</logic:notEmpty>
												
						<td class="dataCellText"> <bean:write name="specimen" property="quantity" />
						<html:hidden name="specimen" indexed="true" property="quantity" />
						</td>
						
					</tr>
				  </logic:iterate>	
				</table>
				</td>
			</tr>
		</logic:notEmpty>
		
		
			<logic:notEmpty name="viewSpecimenSummaryForm" property="eventId">
				<html:hidden property="eventId"  />
			</logic:notEmpty>
				<html:hidden property="userAction" />
				<html:hidden property="requestType" />
			<logic:notEmpty name="viewSpecimenSummaryForm" property="aliquotList" >
				
				<tr>
					<td>			
					&nbsp;&nbsp;
					</td>
				</tr>				

				<tr>
					<td class="dataTablePrimaryLabel" height="20">			
						<p>Aliquot details
					</td>
				</tr>
				
				<tr>
					<td>
						<table summary="" cellpadding="3" cellspacing="0" border="0" class="dataTable" width="100%">
							<tr>	
								<th class="formSerialNumberLabelForTable" scope="col">Parent</th>
								<th class="formSerialNumberLabelForTable" scope="col">Label</th>
								<th class="formSerialNumberLabelForTable" scope="col">Storage</th>
								<th class="formSerialNumberLabelForTable" scope="col">Qty</th>
							</tr>
							<logic:iterate name="viewSpecimenSummaryForm" property="aliquotList" id="aliquot">
							<tr>
								<td class="dataCellText"> <bean:write name="aliquot" property="parentName" /></td>		      		
								<td class="dataCellText"> <bean:write name="aliquot" property="displayName" /></td>
								<td class="dataCellText"> <bean:write name="aliquot" property="storageContainerForSpecimen" /></td>
								<td class="dataCellText">&nbsp; <bean:write name="aliquot" property="quantity" /></td>
							</tr>
						  </logic:iterate>	
					 </table>			
				 </td>
			 </tr>
		</logic:notEmpty>		
		
		
		 
		<logic:notEmpty name="viewSpecimenSummaryForm" property="derivedList" >		
		<tr>
			<td>			
				&nbsp;&nbsp;
			</td>
		</tr>				

		<tr>
			<td class="dataTablePrimaryLabel" height="20">
				<p>Derived details
			 </td>
		</tr>
			 <td>
			    <table summary="" cellpadding="3" cellspacing="0" border="0" class="dataTable" width="100%">
					<tr>
						<th class="formSerialNumberLabelForTable" scope="col">Parent</th>
						<th class="formSerialNumberLabelForTable" scope="col">Label</th>
						<th class="formSerialNumberLabelForTable" scope="col"> Class</th>
						<th class="formSerialNumberLabelForTable" scope="col"> Type</th>
						<th class="formSerialNumberLabelForTable" scope="col"> Qty</th>
						<th class="formSerialNumberLabelForTable" scope="col"> Storage</th>
						<th class="formSerialNumberLabelForTable" scope="col"> concentration</th>
					</tr>
					  <logic:iterate name="viewSpecimenSummaryForm" property="derivedList" id="derived">
		      		<tr>
						<td class="dataCellText"> <bean:write name="derived" property="parentName" /></td>
						<td class="dataCellText" > <bean:write name="derived" property="displayName" /></td>
						<td class="dataCellText"> <bean:write name="derived" property="className" /></td>
						<td class="dataCellText"> <bean:write name="derived" property="type" /></td>
						<td class="dataCellText"> <bean:write name="derived" property="quantity" /></td>
						<td class="dataCellText"> <bean:write name="derived" property="storageContainerForSpecimen" /></td>
						<td class="dataCellText"> <bean:write name="derived" property="concentration" /></td>
			      	</tr>
					  </logic:iterate>	
				</table>
			</td>
		</tr>
	</logic:notEmpty>		
	</table>

		<table align="bottom">
		

			<logic:equal name="viewSpecimenSummaryForm" property="requestType" value="Collection Protocol">
				<logic:equal name="viewSpecimenSummaryForm" property="collectionProtocolStatus" value="Disabled">
					<tr>
						<td>			
							&nbsp;&nbsp;
						</td>
					</tr>
					<tr>
						<td>
							<html:button  styleClass="actionButton" 
								property="submitPage" 
								title=""
								value="Save Collection Protocol" 
								onclick="confirmDisable()">
							</html:button>
						</td>
					</tr>
				</logic:equal>
				
				<logic:notEqual name="viewSpecimenSummaryForm" property="collectionProtocolStatus" value="Disabled">
					<tr>
						<td>			
							&nbsp;&nbsp;
						</td>
					</tr>
					<tr>
						<td>
							<html:submit  value="Save Collection Protocol" />
						</td>
					</tr>
				</logic:notEqual>
					
			</logic:equal>
			
			<logic:equal name="viewSpecimenSummaryForm" property="requestType" value="Multiple Specimen">		
			<tr>
				<td>
				<html:submit value="Save Specimens" />
				</td>
			 </tr>
			</logic:equal>
		
		<logic:equal name="viewSpecimenSummaryForm" property="requestType" value="anticipatory specimens">
		<tr>
			<td>
			<html:submit value="Update Specimen status" onclick="form.action='GenericSpecimenSummary.do?save=SCGSpecimens'; submit()" />
			</td>
		 </tr>
		</logic:equal>
		
		</table>
		</html:form>		
</html>