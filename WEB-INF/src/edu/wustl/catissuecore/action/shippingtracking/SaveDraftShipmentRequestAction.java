/**
 * <p>Title: SaveDraftShipmentRequestAction Class>
 * <p>Description:	Request Save Draft action.</p>
 * Copyright:    Copyright (c) year
 * Company: Washington University, School of Medicine, St. Louis.
 * @author Nilesh_Ghone
 * @version 1.00
 * Created on September 26, 2008
 */

package edu.wustl.catissuecore.action.shippingtracking;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionError;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import edu.wustl.catissuecore.actionForm.shippingtracking.ShipmentRequestForm;
import edu.wustl.catissuecore.bizlogic.BizLogicFactory;
import edu.wustl.catissuecore.bizlogic.shippingtracking.ShipmentRequestBizLogic;
import edu.wustl.catissuecore.domain.Site;
import edu.wustl.catissuecore.domain.shippingtracking.ShipmentRequest;
import edu.wustl.catissuecore.util.shippingtracking.Constants;
import edu.wustl.catissuecore.util.shippingtracking.ShippingTrackingUtility;
import edu.wustl.common.action.SecureAction;
import edu.wustl.common.actionForm.AbstractActionForm;
import edu.wustl.common.beans.SessionDataBean;
import edu.wustl.common.exception.AssignDataException;
import edu.wustl.common.factory.AbstractFactoryConfig;
import edu.wustl.common.factory.IFactory;
import edu.wustl.dao.daofactory.DAOConfigFactory;
import edu.wustl.dao.daofactory.DAOFactory;
import edu.wustl.common.util.Utility;

import edu.wustl.common.util.logger.Logger;
import edu.wustl.dao.DAO;

public class SaveDraftShipmentRequestAction extends SecureAction 
{
	protected ActionForward executeSecureAction(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception
	{
		String target = edu.wustl.catissuecore.util.global.Constants.SUCCESS;
		String operation=request.getParameter(edu.wustl.catissuecore.util.global.Constants.OPERATION);
		if(operation==null || operation.equals(""))
		{
			operation=(String)request.getAttribute(edu.wustl.catissuecore.util.global.Constants.OPERATION);
		}
		if(operation==null || operation.equals(""))
		{
			operation=((AbstractActionForm)form).getOperation();
		}
		request.setAttribute(edu.wustl.catissuecore.util.global.Constants.OPERATION, operation);
		ActionErrors actionErrors=new ActionErrors();
		// Create DAO for passing as an argument to bizlogic's validate
		DAO dao = DAOConfigFactory.getInstance().getDAOFactory(edu.wustl.catissuecore.util.global.Constants.APPLICATION_NAME).getDAO();
		//Create ShipmentRequest Object explicitly
		ShipmentRequest shipmentRequest=new ShipmentRequest();
		try
		{
			dao.openSession(getSessionData(request));
			ShipmentRequestForm shipmentRequestForm=(ShipmentRequestForm)form;
			request.setAttribute("shipmentRequestForm", shipmentRequestForm);
			// Call ShipmentRequestBizlogic's method to validate the contents of the shipment request
			IFactory factory = AbstractFactoryConfig.getInstance().getBizLogicFactory();
			ShipmentRequestBizLogic bizLogic = (ShipmentRequestBizLogic)factory.getBizLogic(Constants.SHIPMENT_REQUEST_FORM_ID);
			shipmentRequest.setAllValues(shipmentRequestForm);
			// Check whether user have privilege to create request.
			//If not throws UserNotAuthorizedException.
			bizLogic.isAuthorized(dao, shipmentRequest, getSessionData(request));
			// Validations.
			boolean isValid=false;
			isValid = bizLogic.validate(shipmentRequest, dao,operation);
			if(isValid)
			{
				Collection<ShipmentRequest> shipmentRequestCollection = new HashSet<ShipmentRequest>();
				shipmentRequestCollection.add(shipmentRequest);
				Integer[] specimenCountArr=new Integer[shipmentRequestCollection.size()];
				Integer[] containerCountArr=new Integer[shipmentRequestCollection.size()];
				String[] specimenLabelArr=new String[shipmentRequestForm.getSpecimenCounter()];
				String[] containerLabelArr=new String[shipmentRequestForm.getContainerCounter()];
				// for holding receiver site's names
				String[] recieverSiteNameArr=new String[shipmentRequestCollection.size()];
				int specimenCount=0;
				int containerCount=0;
				int count=0;
				if(shipmentRequestCollection!=null)
				{
					// Set specimens label to specimenLabelArr[]
					if(shipmentRequestForm.getSpecimenCounter() > 0)
					{
						for(int specimenCounter=0; specimenCounter < shipmentRequestForm.getSpecimenCounter(); specimenCounter++)
						{
							specimenLabelArr[specimenCount++] = (String) shipmentRequestForm.getSpecimenDetails("specimenLabel_"+(specimenCounter+1));
						}
					}
					// Set containers label to containerLabelArr[]
					if(shipmentRequestForm.getContainerCounter() > 0)
					{
						for(int containerCounter=0; containerCounter < shipmentRequestForm.getContainerCounter(); containerCounter++)
						{
							containerLabelArr[containerCount++] = (String) shipmentRequestForm.getContainerDetails("containerLabel_"+(containerCounter+1));
						}
					}
					specimenCountArr[count] = shipmentRequestForm.getSpecimenCounter();
					containerCountArr[count] = shipmentRequestForm.getContainerCounter();

					// adding receiver site's names to the array -
					//No receiver site for saving draft.
					recieverSiteNameArr[count]= "";
				}
				request.getSession().setAttribute("shipmentRequestCollection",
						shipmentRequestCollection);
				request.setAttribute("siteCount", shipmentRequestCollection.size());
				request.setAttribute("specimenCountArr", specimenCountArr);
				request.setAttribute("containerCountArr", containerCountArr);
				request.setAttribute("specimenLabelArr", specimenLabelArr);
				request.setAttribute("containerLabelArr", containerLabelArr);
				request.setAttribute("recieverSiteNameArr", recieverSiteNameArr);
			}
			// Sets the sender and receiver site list attribute
	        String sourceObjectName = Site.class.getName();
	        String[] displayNameFields = {edu.wustl.catissuecore.util.global.Constants.NAME};
	        String valueField = edu.wustl.catissuecore.util.global.Constants.SYSTEM_IDENTIFIER;
	        List siteList = bizLogic.getList(sourceObjectName, displayNameFields, valueField, false);
	        request.setAttribute(Constants.REQUESTERS_SITE_LIST, siteList);
	        request.setAttribute("senderSiteName", ShippingTrackingUtility.getDisplayName(siteList,""+shipmentRequestForm.getSenderSiteId()));
		}
		/*catch (UserNotAuthorizedException excp)
		{
	        //ActionErrors errors = new ActionErrors();
	        SessionDataBean sessionDataBean = getSessionData(request);
	        String userName = "";
	        if(sessionDataBean != null)
	    	{
	    	    userName = sessionDataBean.getUserName();
	    	}
	        String className = getActualClassName(shipmentRequest.getClass().getName());
	        String decoratedPrivilegeName = Utility.getDisplayLabelForUnderscore(excp.getPrivilegeName());
	        String baseObject = "";
	        if (excp.getBaseObject() != null && excp.getBaseObject().trim().length() != 0)
	        {
	            baseObject = excp.getBaseObject();
	        }
	        else
	        {
	            baseObject = className;
	        }
	        ActionError error = new ActionError("access.addedit.object.denied", userName, className,decoratedPrivilegeName,baseObject);
	        actionErrors.add(ActionErrors.GLOBAL_ERROR, error);
	    	//saveErrors(request, errors);
	    	target = edu.wustl.catissuecore.util.global.Constants.FAILURE;
	        Logger.out.error(excp.getMessage(), excp);

		}*/
		catch (AssignDataException assignDataException)
		{
			target = edu.wustl.catissuecore.util.global.Constants.FAILURE;
			actionErrors.add(ActionErrors.GLOBAL_ERROR, new ActionError("errors.item",
					assignDataException.getMessage()));
		}
		finally
		{
			dao.closeSession();
		}
	    saveErrors(request,actionErrors);
		return mapping.findForward(target);
	}
	/**
	 * checks the authorization for execution.
	 * @param arg0 HttpServletRequest object.
	 * @return boolean result of check.
	 * @throws Exception if some problem occurs.
	 */
	protected boolean isAuthorizedToExecute(HttpServletRequest arg0) throws Exception
	{
		return true;
	}

	/**
	 * parses the string to get the class name.
     * @param name string to be parsed.
     * @return actual class name.
     */
    public String getActualClassName(String name)
    {
        if (name != null && name.trim().length()!=0)
        {
            String splitter = "\\.";
            String [] arr = name.split(splitter);
            if (arr != null && arr.length != 0)
            {
                return arr[arr.length-1];
            }
        }
        return name;
    }
}