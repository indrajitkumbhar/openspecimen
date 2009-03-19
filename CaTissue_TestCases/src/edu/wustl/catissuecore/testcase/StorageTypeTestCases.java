package edu.wustl.catissuecore.testcase;

import java.util.List;

import org.junit.Test;

import edu.wustl.catissuecore.actionForm.StorageTypeForm;
import edu.wustl.catissuecore.domain.Capacity;
import edu.wustl.catissuecore.domain.StorageType;
import edu.wustl.common.bizlogic.DefaultBizLogic;
import edu.wustl.common.dao.AbstractDAO;
import edu.wustl.common.util.dbManager.DAOException;

/**
 * This class contains test cases for Storage Type add/edit
 * @author Himanshu Aseeja
 */
public class StorageTypeTestCases extends CaTissueSuiteBaseTest
{
	/**
	 * Test Storage Type Add.
	 */
	@Test
	public void testStorageTypeAdd()
	{
		String name = new String("stype_" + UniqueKeyGeneratorUtil.getUniqueKey());
		addRequestParameter("name", name);
		addRequestParameter("oneDimensionCapacity", "3");
		addRequestParameter("twoDimensionCapacity", "2");
		addRequestParameter("oneDimensionLabel", "row");
		addRequestParameter("twoDimensionLabel", "col");
		addRequestParameter("defaultTemperature", "29");
		addRequestParameter("type","type_" + UniqueKeyGeneratorUtil.getUniqueKey());
		addRequestParameter("operation", "add");
		setRequestPathInfo("/StorageTypeAdd");
		actionPerform();
		verifyForward("success");
		verifyNoActionErrors();
		
		StorageTypeForm form = (StorageTypeForm) getActionForm();
		StorageType storageType = new StorageType();
		
		storageType.setName(name);
		storageType.setId(form.getId());
		storageType.setOneDimensionLabel(form.getOneDimensionLabel());
		storageType.setTwoDimensionLabel(form.getTwoDimensionLabel());
		storageType.setDefaultTempratureInCentigrade(Double.parseDouble(form.getDefaultTemperature()));
		
		Capacity capacity = new Capacity();
		capacity.setOneDimensionCapacity(form.getOneDimensionCapacity());
		capacity.setTwoDimensionCapacity(form.getTwoDimensionCapacity());
		storageType.setCapacity(capacity);
		
		TestCaseUtility.setNameObjectMap("StorageType",storageType);
	}
	
	/**
	 * Test Storage Type edit.
	 */
	@Test
	public void testStorageTypeEdit()
	{

			/*Simple Search Action*/
		    setRequestPathInfo("/SimpleSearch");
			addRequestParameter("aliasName", "StorageType");
			addRequestParameter("value(SimpleConditionsNode:1_Condition_DataElement_table)", "StorageType");
			addRequestParameter("value(SimpleConditionsNode:1_Condition_DataElement_field)","ContainerType.NAME.varchar");
			addRequestParameter("value(SimpleConditionsNode:1_Condition_Operator_operator)","Starts With");
			addRequestParameter("value(SimpleConditionsNode:1_Condition_value)","");
			addRequestParameter("pageOf","pageOfStorageType");
			addRequestParameter("operation","search");
			actionPerform();
			
			StorageType storageType = (StorageType) TestCaseUtility.getNameObjectMap("StorageType");
			DefaultBizLogic bizLogic = new DefaultBizLogic();
			List<StorageType> storageTypeList = null;
			try 
			{
				storageTypeList = bizLogic.retrieve("StorageType");
			}
			catch (DAOException e) 
			{
				e.printStackTrace();
				System.out.println("StorageTypeTestCases.testStorageTypeEdit(): "+e.getMessage());
				fail(e.getMessage());
			}
			
			if(storageTypeList.size() > 1)
			{
			    verifyForward("success");
			    verifyNoActionErrors();
			}
			else if(storageTypeList.size() == 1)
			{
				verifyForwardPath("/SearchObject.do?pageOf=pageOfStorageType&operation=search&id=" + storageType.getId());
				verifyNoActionErrors();
			}
			else
			{
				verifyForward("failure");
				//verify action errors
				String errorNames[] = new String[]{"simpleQuery.noRecordsFound"};
				verifyActionErrors(errorNames);
			}
						
			/*common search action.Generates StorageTypeForm*/
			setRequestPathInfo("/StorageTypeSearch");
			addRequestParameter("id", "" + storageType.getId());
			actionPerform();
			verifyForward("pageOfStorageType");
			
			
			/*edit action*/
			storageType.setName("stype1_" + UniqueKeyGeneratorUtil.getUniqueKey());
			addRequestParameter("name",storageType.getName());
			setRequestPathInfo("/StorageTypeEdit");
			addRequestParameter("operation", "edit");
			actionPerform();
			verifyForward("success");
			
			TestCaseUtility.setNameObjectMap("StorageType",storageType);
	}
}