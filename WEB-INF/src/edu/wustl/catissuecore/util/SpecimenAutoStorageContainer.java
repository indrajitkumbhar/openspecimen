package edu.wustl.catissuecore.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import net.sf.ehcache.CacheException;

import edu.wustl.catissuecore.bean.GenericSpecimen;
import edu.wustl.catissuecore.bizlogic.BizLogicFactory;
import edu.wustl.catissuecore.bizlogic.StorageContainerBizLogic;
import edu.wustl.catissuecore.domain.StorageContainer;
import edu.wustl.catissuecore.util.global.Constants;
import edu.wustl.common.beans.NameValueBean;
import edu.wustl.common.beans.SessionDataBean;
import edu.wustl.common.security.exceptions.SMException;
import edu.wustl.common.util.dbManager.DAOException;

/**
 * This class exposes the functionality to set storage containers
 * automatically for multiple specimens.
 * @author abhijit_naik
 * 
 */
public class SpecimenAutoStorageContainer {

	private HashMap<String, ArrayList<GenericSpecimen>> specimenMap = new HashMap<String, ArrayList<GenericSpecimen>> ();
	private Long cpId = null;
	private HashMap<Long, HashMap<String, ArrayList<GenericSpecimen>>> collectionProtocolSpecimenMap = 
						new HashMap<Long, HashMap<String,ArrayList<GenericSpecimen>>> ();
	private ArrayList<String> storageContainerIds = new ArrayList<String>();
	
	public void setCollectionProtocol(Long cpId)
	{
		
		this.cpId = cpId;
	}
	
	public void addSpecimen(GenericSpecimen specimen, String className)
	{
		addToMap(specimen, className, specimenMap);
	}
	 
	public void addSpecimen(GenericSpecimen specimen, String className, Long collectionProtocolId)
	{ 
		if (collectionProtocolSpecimenMap.get(collectionProtocolId) == null)
		{ 
			collectionProtocolSpecimenMap.put(collectionProtocolId, new HashMap<String, ArrayList<GenericSpecimen>> ());
		}
		HashMap<String, ArrayList<GenericSpecimen>> targetMap = collectionProtocolSpecimenMap.get(collectionProtocolId);
		addToMap(specimen, className, targetMap);
	}
	
	private void addToMap (GenericSpecimen specimen, String className, HashMap<String, ArrayList<GenericSpecimen>> targetMap)
	{
		if( targetMap.get(className) == null)
		{
			targetMap.put(className, new ArrayList<GenericSpecimen>());
		}
		ArrayList<GenericSpecimen> specimenList = targetMap.get(className);
		specimenList.add(specimen);		
	}

	public void setSpecimenStoragePositions(SessionDataBean sessionDataBean) throws DAOException
	{

		storageContainerIds.clear();
		setAutoStoragePositions(specimenMap, sessionDataBean, cpId);
		
	}
	public void setCollectionProtocolSpecimenStoragePositions(
			SessionDataBean sessionDataBean) throws DAOException
	{
 
		storageContainerIds.clear();
		Set<Long> keySet = collectionProtocolSpecimenMap.keySet();
		Iterator<Long> keySetIterator = keySet.iterator();
		
		while(keySetIterator.hasNext())
		{
			Long collectionProtocolId = keySetIterator.next();
			
			HashMap<String, ArrayList<GenericSpecimen>> autoSpecimenMap =
				collectionProtocolSpecimenMap.get(collectionProtocolId);
			
			setAutoStoragePositions(autoSpecimenMap, sessionDataBean,
					collectionProtocolId );
		}	
	}

	/**
	 * @param sessionDataBean
	 * @throws DAOException
	 */
	private void setAutoStoragePositions(
			HashMap<String, ArrayList<GenericSpecimen>> autoSpecimenMap, 
			SessionDataBean sessionDataBean, Long collectionProtocolId)
			throws DAOException {
		
		Set<String> keySet = autoSpecimenMap.keySet();
		if (!keySet.isEmpty())
		{
			Iterator<String> keySetIterator = keySet.iterator();

			while(keySetIterator.hasNext())
			{
				String key = keySetIterator.next();
				ArrayList<GenericSpecimen> specimenList =
					autoSpecimenMap.get(key);
				setSpecimenStorageDetails(specimenList,key, sessionDataBean, collectionProtocolId);
			}
		}
	}
	
	protected void setSpecimenStorageDetails(List<GenericSpecimen> specimenDataBeanList, 
			String className, SessionDataBean bean, Long collectionProtocolId ) throws DAOException
	{
 
		StorageContainerBizLogic bizLogic = (StorageContainerBizLogic) BizLogicFactory.getInstance()
		.getBizLogic(Constants.STORAGE_CONTAINER_FORM_ID);
		
		Map containerMap;
		try {
			containerMap = StorageContainerUtil.getContainerMapFromCache();
			populateStorageLocations(specimenDataBeanList,
					collectionProtocolId.longValue(), containerMap, bean, className);

		} catch (Exception exception) {
			// TODO Auto-generated catch block
			throw new DAOException(exception.getMessage(),exception);
		}
		
			/*bizLogic.getAllocatedContaienrMapForSpecimen(
				collectionProtocolId.longValue(), className, 0, "false", bean, true);
	*/
	}
	
	protected void populateStorageLocations(List specimenDataBeanList, Long cpId, 
			Map containerMap, SessionDataBean bean, String classType)throws SMException,DAOException
	{
		
		int counter = 0;

		StorageContainerBizLogic bizLogic = (StorageContainerBizLogic) BizLogicFactory.getInstance()
		.getBizLogic(Constants.STORAGE_CONTAINER_FORM_ID);
		if (containerMap.isEmpty())
		{
			return;
		}
		Object[] containerId = containerMap.keySet().toArray();

		for (int i = 0; i < containerId.length; i++)
		{
			if(counter >= specimenDataBeanList.size())
			{
				break;
			}

			String storageId = ((NameValueBean) containerId[i]).getValue();
			StorageContainer sc = new StorageContainer();
			sc.setId( new Long(storageId));
			sc.setName(((NameValueBean) containerId[i]).getName());

			if (!checkStorageContainerRules(bizLogic, sc, bean, classType))
			{
				continue;
			}
			

			Map xDimMap = (Map) containerMap.get(containerId[i]);
			if (!xDimMap.isEmpty())
			{
				counter = populateStoragePositions(specimenDataBeanList,  counter,
						 sc, xDimMap);
			}
		}


	}

	private boolean checkStorageContainerRules(StorageContainerBizLogic bizLogic
			, StorageContainer sc, SessionDataBean bean, String classType) 
				throws DAOException,SMException
	{
		if (!bizLogic.canHoldCollectionProtocol(cpId, sc))
		{
			return false;
		}

		if (!bizLogic.canHoldSpecimenClass(classType, sc))
		{
			return false;
		}
		if (!bizLogic.validateContainerAccess(sc , bean))
		{
			return false;
		}
		return true;
	}
	/**
	 * @param specimenDataBeanList
	 * @param counter
	 * @param sc
	 * @param xDimMap
	 * @return
	 */
	private int populateStoragePositions(List specimenDataBeanList, int counter,
			StorageContainer sc, Map xDimMap)
	{
		
		Object[] xDim = xDimMap.keySet().toArray();
		
		for (int j = 0; j < xDim.length; j++)
		{
			List yDimList = (List) xDimMap.get(xDim[j]);
			if(counter >= specimenDataBeanList.size())
			{
				break;
			}

			for (int k = 0; k < yDimList.size(); k++)
			{
				if(counter >= specimenDataBeanList.size())
				{
					break;
				}
				GenericSpecimen specimenDataBean = 
					(GenericSpecimen)specimenDataBeanList.get(counter);
				String stName = sc.getName();
				String posOne = ((NameValueBean) xDim[j]).getValue();
				String posTwo = ((NameValueBean) yDimList.get(k)).getValue();
				String storageValue = stName+":"+posOne+" ,"+posTwo; 

				if(!storageContainerIds.contains(storageValue))
				{													
					specimenDataBean.setContainerId(String.valueOf(sc.getId()));
					specimenDataBean.setSelectedContainerName(stName);
					specimenDataBean.setPositionDimensionOne(posOne);
					specimenDataBean.setPositionDimensionTwo(posTwo);
					storageContainerIds.add(storageValue);
					counter++;									
				}
			}
		}
		return counter;
	}

}
