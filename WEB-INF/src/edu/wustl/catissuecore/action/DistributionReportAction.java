
package edu.wustl.catissuecore.action;

import java.util.HashMap;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import edu.wustl.catissuecore.actionForm.ConfigureResultViewForm;
import edu.wustl.catissuecore.actionForm.DistributionReportForm;
import edu.wustl.catissuecore.domain.Distribution;
import edu.wustl.catissuecore.util.global.Constants;
import edu.wustl.common.beans.SessionDataBean;

/**
 * This is the action class for displaying the Distribution report.
 *
 * @author Poornima Govindrao
 */

public class DistributionReportAction extends BaseDistributionReportAction
{

	/**
	 * Overrides the execute method of Action class. Sets the various fields in
	 * DistributionProtocol Add/Edit webpage.
	 *
	 * @param mapping
	 *            object of ActionMapping
	 * @param form
	 *            object of ActionForm
	 * @param request
	 *            object of HttpServletRequest
	 * @param response
	 *            object of HttpServletResponse
	 * @throws Exception
	 *             generic exception
	 * @return value for ActionForward object
	 */
	@Override
	protected ActionForward executeAction(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) throws Exception
	{
		final ConfigureResultViewForm configForm = (ConfigureResultViewForm) form;
		String forward = "Success";

		// Retrieve the distribution ID which is set in CommonAddEdit Action
		Long distributionId = (Long) request.getAttribute(Constants.DISTRIBUTION_ID);

		// retrieve from configuration form if it is null
		if (distributionId == null)
		{
			distributionId = configForm.getDistributionId();
		}

		// this condition executes when user distributes the order
		if (request.getAttribute("forwardToHashMap") != null)
		{
			final HashMap forwardToHashMap = (HashMap) request.getAttribute("forwardToHashMap");
			distributionId = (Long) forwardToHashMap.get("distributionId");
		}
		/*
		 * Retrieve from request attribute if it null.
		 */
		if (distributionId == null)
		{
			distributionId = (Long) request.getAttribute(Constants.SYSTEM_IDENTIFIER);
		}

		/*
		 * Retrieve from request parameter if it null. This request parameter is
		 * set in Distribution page incase the Report buttonis clicked from
		 * Distribution Edit page
		 */
		if (distributionId == null)
		{
			distributionId = new Long(request.getParameter(Constants.SYSTEM_IDENTIFIER));
		}

		// Set it in configuration form if it is not null
		if (distributionId != null)
		{
			configForm.setDistributionId(distributionId);
		}

		final Distribution dist = this.getDistribution(distributionId,
				this.getSessionData(request),
				edu.wustl.security.global.Constants.CLASS_LEVEL_SECURE_RETRIEVE);

		// Retrieve the distributed items data
		final DistributionReportForm distributionReportForm = this.getDistributionReportForm(dist);
		final SessionDataBean sessionData = this.getSessionData(request);
		final List listOfData = this.getListOfData(dist, configForm, sessionData);
		if (listOfData.isEmpty())
		{
			forward = Constants.PAGE_OF_DISTRIBUTION_ARRAY;
		}

		// Set the columns for Distribution report
		final String action = configForm.getNextAction();
		final String[] selectedColumns = this.getSelectedColumns(action, configForm, false);
		final String[] columnNames = this.getColumnNames(selectedColumns);

		// Set the request attributes for the Distribution report data
		request.setAttribute(Constants.DISTRIBUTION_REPORT_FORM, distributionReportForm);
		request.setAttribute(Constants.COLUMN_NAMES_LIST, columnNames);
		request.setAttribute(Constants.DISTRIBUTED_ITEMS_DATA, listOfData);
		this.setSelectedMenuRequestAttribute(request);

		final String pageOf = request.getParameter(Constants.PAGE_OF);
		request.setAttribute(Constants.PAGE_OF, pageOf);

		return (mapping.findForward(forward));
	}
}