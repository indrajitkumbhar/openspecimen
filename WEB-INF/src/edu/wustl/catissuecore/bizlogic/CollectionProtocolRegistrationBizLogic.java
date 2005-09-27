/**
 * <p>Title: UserHDAO Class>
 * <p>Description:	UserHDAO is used to add user information into the database using Hibernate.</p>
 * Copyright:    Copyright (c) year
 * Company: Washington University, School of Medicine, St. Louis.
 * @author Ajay Sharma
 * @version 1.00
 * Created on Apr 13, 2005
 */

package edu.wustl.catissuecore.bizlogic;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import edu.wustl.catissuecore.dao.DAO;
import edu.wustl.catissuecore.domain.AbstractDomainObject;
import edu.wustl.catissuecore.domain.CollectionProtocol;
import edu.wustl.catissuecore.domain.CollectionProtocolRegistration;
import edu.wustl.catissuecore.domain.Participant;
import edu.wustl.catissuecore.util.global.Constants;
import edu.wustl.catissuecore.util.global.Utility;
import edu.wustl.common.beans.SessionDataBean;
import edu.wustl.common.security.SecurityManager;
import edu.wustl.common.security.exceptions.SMException;
import edu.wustl.common.security.exceptions.UserNotAuthorizedException;
import edu.wustl.common.util.dbManager.DAOException;
import edu.wustl.common.util.logger.Logger;

/**
 * UserHDAO is used to add user information into the database using Hibernate.
 * @author kapil_kaveeshwar
 */
public class CollectionProtocolRegistrationBizLogic extends DefaultBizLogic
{
	/**
	 * Saves the user object in the database.
	 * @param obj The user object to be saved.
	 * @param session The session in which the object is saved.
	 * @throws DAOException 
	 */
	protected void insert(Object obj, DAO dao, SessionDataBean sessionDataBean) throws DAOException, UserNotAuthorizedException
	{
		CollectionProtocolRegistration collectionProtocolRegistration = (CollectionProtocolRegistration) obj;
		
		registerParticipantAndProtocol(dao,collectionProtocolRegistration, sessionDataBean);
		
		dao.insert(collectionProtocolRegistration, sessionDataBean, true, true);
		
		try
        {
            SecurityManager.getInstance(this.getClass()).insertAuthorizationData(null,getProtectionObjects(collectionProtocolRegistration),getDynamicGroups(collectionProtocolRegistration));
        }
        catch (SMException e)
        {
            Logger.out.error("Exception in Authorization: "+e.getMessage(),e);
        }
	}

	/**
	 * Updates the persistent object in the database.
	 * @param obj The object to be updated.
	 * @param session The session in which the object is saved.
	 * @throws DAOException 
	 */
	protected void update(DAO dao, Object obj, SessionDataBean sessionDataBean) throws DAOException, UserNotAuthorizedException
	{
		CollectionProtocolRegistration collectionProtocolRegistration = (CollectionProtocolRegistration) obj;
		
		dao.update(collectionProtocolRegistration, sessionDataBean, true, true, false);
		
		Logger.out.debug("collectionProtocolRegistration.getActivityStatus() "+collectionProtocolRegistration.getActivityStatus());
		if(collectionProtocolRegistration.getActivityStatus().equals(Constants.ACTIVITY_STATUS_DISABLED))
		{
			Logger.out.debug("collectionProtocolRegistration.getActivityStatus() "+collectionProtocolRegistration.getActivityStatus());
			Long collectionProtocolRegistrationIDArr[] = {collectionProtocolRegistration.getSystemIdentifier()};
			
			SpecimenCollectionGroupBizLogic bizLogic = (SpecimenCollectionGroupBizLogic)BizLogicFactory.getBizLogic(Constants.SPECIMEN_COLLECTION_GROUP_FORM_ID);
			bizLogic.disableRelatedObjects(dao,collectionProtocolRegistrationIDArr);
		}
	}

	public Set getProtectionObjects(AbstractDomainObject obj)
    {
        Set protectionObjects = new HashSet();
        
        CollectionProtocolRegistration collectionProtocolRegistration = (CollectionProtocolRegistration) obj;
        protectionObjects.add(collectionProtocolRegistration);
        
		Participant participant = null;
		//Case of registering Participant on its participant ID
		if(collectionProtocolRegistration.getParticipant()!=null)
		{
		    protectionObjects.add(collectionProtocolRegistration.getParticipant());
		}
		
        Logger.out.debug(protectionObjects.toString());
        return protectionObjects;
    }

    public String[] getDynamicGroups(AbstractDomainObject obj)
    {
        String[] dynamicGroups=null;
        CollectionProtocolRegistration collectionProtocolRegistration = (CollectionProtocolRegistration) obj;
        dynamicGroups = new String[1];
        dynamicGroups[0] = Constants.getCollectionProtocolPGName(collectionProtocolRegistration.getCollectionProtocol().getSystemIdentifier());
        return dynamicGroups;
        
    }
    
    private void registerParticipantAndProtocol(DAO dao, CollectionProtocolRegistration collectionProtocolRegistration, SessionDataBean sessionDataBean) throws DAOException, UserNotAuthorizedException
	{
    	//Case of registering Participant on its participant ID
    	Participant participant = null;
    	
		if(collectionProtocolRegistration.getParticipant()!=null)
		{
			List list = dao.retrieve(Participant.class.getName(),
                    Constants.SYSTEM_IDENTIFIER, collectionProtocolRegistration.getParticipant().getSystemIdentifier());
			
			if (list != null && list.size() != 0)
			{
				participant = (Participant)list.get(0);
			}
		}
		else
		{
			participant = new Participant();
			
			participant.setLastName("");
			participant.setFirstName("");
			participant.setMiddleName("");
			participant.setSocialSecurityNumber(null);
			
			dao.insert(participant, sessionDataBean, true, true);
		}
		
		collectionProtocolRegistration.setParticipant(participant);

		List list = dao.retrieve(CollectionProtocol.class.getName(), Constants.SYSTEM_IDENTIFIER,
				collectionProtocolRegistration.getCollectionProtocol().getSystemIdentifier());
		if (list != null && list.size() != 0)
		{
			CollectionProtocol collectionProtocol = (CollectionProtocol)list.get(0);
			collectionProtocolRegistration.setCollectionProtocol(collectionProtocol);
		}
	}
    
    public void disableRelatedObjectsForParticipant(DAO dao, Long participantIDArr[])throws DAOException 
    {
    	List listOfSubElement = super.disableObjects(dao, CollectionProtocolRegistration.class, "participant", 
    			"CATISSUE_COLLECTION_PROTOCOL_REGISTRATION", "PARTICIPANT_ID", participantIDArr);
    	if(!listOfSubElement.isEmpty())
    	{
    		SpecimenCollectionGroupBizLogic bizLogic = (SpecimenCollectionGroupBizLogic)BizLogicFactory.getBizLogic(Constants.SPECIMEN_COLLECTION_GROUP_FORM_ID);
    		bizLogic.disableRelatedObjects(dao,Utility.toLongArray(listOfSubElement));
    	}
    }
    
    public void disableRelatedObjectsForCollectionProtocol(DAO dao, Long collectionProtocolIDArr[])throws DAOException 
    {
    	List listOfSubElement = super.disableObjects(dao, CollectionProtocolRegistration.class, "collectionProtocol", 
    			"CATISSUE_COLLECTION_PROTOCOL_REGISTRATION", "COLLECTION_PROTOCOL_ID", collectionProtocolIDArr);
    	if(!listOfSubElement.isEmpty())
    	{
			SpecimenCollectionGroupBizLogic bizLogic = (SpecimenCollectionGroupBizLogic)BizLogicFactory.getBizLogic(Constants.SPECIMEN_COLLECTION_GROUP_FORM_ID);
			bizLogic.disableRelatedObjects(dao,Utility.toLongArray(listOfSubElement));
    	}
    }

    /**
     * @param dao
     * @param objectIds
     * @throws DAOException
     * @throws SMException
     */
    public void assignPrivilegeToRelatedObjectsForParticipant(DAO dao, String privilegeName, Long[] objectIds, Long userId) throws SMException, DAOException
    {
        List listOfSubElement = super.getRelatedObjects(dao, CollectionProtocolRegistration.class, "participant", 
    			 objectIds);
        
    	if(!listOfSubElement.isEmpty())
    	{
    	    super.setPrivilege(dao,privilegeName,CollectionProtocolRegistration.class,Utility.toLongArray(listOfSubElement),userId, null, true);
    		SpecimenCollectionGroupBizLogic bizLogic = (SpecimenCollectionGroupBizLogic)BizLogicFactory.getBizLogic(Constants.SPECIMEN_COLLECTION_GROUP_FORM_ID);
    		bizLogic.assignPrivilegeToRelatedObjects(dao,privilegeName,Utility.toLongArray(listOfSubElement),userId);
    	}
    }

    /**
     * @param dao
     * @param privilegeName
     * @param objectIds
     * @param userId
     */
    public void assignPrivilegeToRelatedObjectsForCP(DAO dao, String privilegeName, Long[] objectIds, Long userId)throws SMException, DAOException
    {
        List listOfSubElement = super.getRelatedObjects(dao, CollectionProtocolRegistration.class, "collectionProtocol",objectIds);
    	if(!listOfSubElement.isEmpty())
    	{
    	    super.setPrivilege(dao,privilegeName,CollectionProtocolRegistration.class,Utility.toLongArray(listOfSubElement),userId, null, true);
			SpecimenCollectionGroupBizLogic bizLogic = (SpecimenCollectionGroupBizLogic)BizLogicFactory.getBizLogic(Constants.SPECIMEN_COLLECTION_GROUP_FORM_ID);
			bizLogic.assignPrivilegeToRelatedObjects(dao,privilegeName,Utility.toLongArray(listOfSubElement),userId);
    	}
    }
    
    public void assignPrivilegeToUser(DAO dao, String privilegeName, Class objectType, Long[] objectIds, Long userId, String roleId, boolean assignToUser) throws SMException, DAOException
    {
	    super.setPrivilege(dao,privilegeName,objectType,objectIds,userId, roleId, assignToUser);
	    
	    SpecimenCollectionGroupBizLogic bizLogic = (SpecimenCollectionGroupBizLogic)BizLogicFactory.getBizLogic(Constants.SPECIMEN_COLLECTION_GROUP_FORM_ID);
		bizLogic.assignPrivilegeToRelatedObjects(dao,privilegeName,objectIds,userId);
	}
}