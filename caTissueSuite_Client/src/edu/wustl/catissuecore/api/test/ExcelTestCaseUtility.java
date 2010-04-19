package edu.wustl.catissuecore.api.test;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import edu.wustl.catissuecore.bean.CollectionProtocolEventBean;
import edu.wustl.catissuecore.bean.SpecimenRequirementBean;
import edu.wustl.catissuecore.domain.CollectionProtocol;
import edu.wustl.catissuecore.domain.CollectionProtocolEvent;
import edu.wustl.catissuecore.domain.CollectionProtocolRegistration;
import edu.wustl.catissuecore.domain.ConsentTier;
import edu.wustl.catissuecore.domain.Participant;
import edu.wustl.catissuecore.domain.Site;
import edu.wustl.catissuecore.domain.User;
import edu.wustl.catissuecore.util.global.Constants;
import edu.wustl.common.util.Utility;
import edu.wustl.common.util.global.Status;
import edu.wustl.common.util.logger.Logger;

public class ExcelTestCaseUtility extends CaTissueBaseTestCase {
	private static String shortTitle = "test_cp_for_edit";

	public static void createCP() throws Exception {
		System.out
				.println("---------IN ExcelTestCaseUtility.createCP-----------");
		CollectionProtocol collectionProtocol = initCollectionProtocol();
		collectionProtocol.setShortTitle(shortTitle);
		collectionProtocol.setTitle(shortTitle);
		Iterator<CollectionProtocolEvent> collEveItr = collectionProtocol
				.getCollectionProtocolEventCollection().iterator();
		int eveNo = 0;
		while (collEveItr.hasNext()) {
			((CollectionProtocolEvent) collEveItr.next())
					.setCollectionPointLabel("Event_" + eveNo);
			eveNo++;
		}
		collectionProtocol = (CollectionProtocol) appService
				.createObject(collectionProtocol);
	}

	public static void registerParticipant() throws Exception {
		Participant participant = BaseTestCaseUtility.initParticipantWithCPR();
		participant.setFirstName("fname");
		participant.setLastName("lname");

		CollectionProtocol collectionProtocol = new CollectionProtocol();
		collectionProtocol.setShortTitle(shortTitle);
		List<?> cPResultList = null;

		cPResultList = CollectionProtocolTestCases.appService.search(
				CollectionProtocol.class, collectionProtocol);
		if (cPResultList.size() != 0) {
			collectionProtocol = (CollectionProtocol) cPResultList.get(0);
		}

		Iterator<CollectionProtocolRegistration> collProtRegItr = participant
				.getCollectionProtocolRegistrationCollection().iterator();
		int eveNo = 0;
		while (collProtRegItr.hasNext()) {
			CollectionProtocolRegistration cpr = (CollectionProtocolRegistration) collProtRegItr
					.next();
			cpr.setProtocolParticipantIdentifier("1111");
			cpr.setCollectionProtocol(collectionProtocol);
			eveNo++;
		}
		participant = (Participant) appService.createObject(participant);
		System.out.println("Participant Object created successfully");
	}

	public static void shiftSpecimenInSCG() throws Exception { // SpecimenTestCases
		// .java
		try {
			System.out
					.println("---------IN ExcelTestCaseUtility.shiftSpecimenInSCG-----------");
			//createCP();
			registerParticipant();
			String excelFilePath = System.getProperty("user.dir")
					+ "/excelFiles/SCG.xls";
			ExcelFileReader EX_CP = new ExcelFileReader();
			String allexcel[][] = EX_CP.setInfo(excelFilePath);
			new ShiftSpecimenInSCG().shift(allexcel);
			System.out
					.println("---------END ExcelTestCaseUtility.shiftSpecimenInSCG-----------");
		} catch (Exception e) {
			System.out.println("Exception in shiftSpecimenInSCG ");
			System.err.println("Exception in shiftSpecimenInSCG ");
			e.printStackTrace();
		}
	}

	public static CollectionProtocol initCollectionProtocol() {
		CollectionProtocol collectionProtocol = new CollectionProtocol();

		Collection consentTierColl = new LinkedHashSet();
		ConsentTier c1 = new ConsentTier();
		c1.setStatement("Consent for aids research");
		consentTierColl.add(c1);
		ConsentTier c2 = new ConsentTier();
		c2.setStatement("Consent for cancer research");
		consentTierColl.add(c2);
		ConsentTier c3 = new ConsentTier();
		c3.setStatement("Consent for Tb research");
		consentTierColl.add(c3);

		collectionProtocol.setConsentTierCollection(consentTierColl);
		collectionProtocol.setAliquotInSameContainer(new Boolean(true));
		collectionProtocol.setDescriptionURL("");
		collectionProtocol.setActivityStatus("Active");

		collectionProtocol.setEndDate(null);
		collectionProtocol.setEnrollment(null);
		collectionProtocol.setIrbIdentifier("77777");
		collectionProtocol.setTitle(shortTitle);
		collectionProtocol.setShortTitle(shortTitle);
		collectionProtocol.setEnrollment(2);

		System.out.println("reached setUp");

		try {
			collectionProtocol.setStartDate(Utility.parseDate("08/15/2003",
					Utility.datePattern("08/15/1975")));

		} catch (Exception e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
		}

		Collection collectionProtocolEventList = new LinkedHashSet();
		CollectionProtocolEvent collectionProtocolEvent = null;
		for (int specimenEventCount = 0; specimenEventCount < 2; specimenEventCount++) {
			collectionProtocolEvent = new CollectionProtocolEvent();
			collectionProtocolEvent.setCollectionPointLabel("Event_"
					+ specimenEventCount);
			setCollectionProtocolEvent(collectionProtocolEvent);
			collectionProtocolEvent.setCollectionProtocol(collectionProtocol);
			collectionProtocolEventList.add(collectionProtocolEvent);
		}
		collectionProtocol
				.setCollectionProtocolEventCollection(collectionProtocolEventList);

		User principalInvestigator = new User();
		principalInvestigator.setId(new Long("1"));
		collectionProtocol.setPrincipalInvestigator(principalInvestigator);

		Collection protocolCordinatorCollection = new HashSet();
		collectionProtocol
				.setCoordinatorCollection(protocolCordinatorCollection);

		return collectionProtocol;
	}

	public static void setCollectionProtocolEvent(
			CollectionProtocolEvent collectionProtocolEvent) {
		collectionProtocolEvent.setStudyCalendarEventPoint(new Double(1.0));
		collectionProtocolEvent.setClinicalStatus("Operative");
		collectionProtocolEvent.setActivityStatus(Status.ACTIVITY_STATUS_ACTIVE
				.toString());
		collectionProtocolEvent
				.setClinicalDiagnosis("Abdominal fibromatosis (disorder)");
		Collection specimenCollection = null;
		CollectionProtocolEventBean cpEventBean = new CollectionProtocolEventBean();
		SpecimenRequirementBean specimenRequirementBean = createSpecimenBean();
		cpEventBean.addSpecimenRequirementBean(specimenRequirementBean);
		Map specimenMap = (Map) cpEventBean.getSpecimenRequirementbeanMap();
		if (specimenMap != null && !specimenMap.isEmpty()) {
			specimenCollection = edu.wustl.catissuecore.util.CollectionProtocolUtil
					.getReqSpecimens(specimenMap.values(), null,
							collectionProtocolEvent);
		}
		collectionProtocolEvent
				.setSpecimenRequirementCollection(specimenCollection);
	}

	private static SpecimenRequirementBean createSpecimenBean() {
		SpecimenRequirementBean specimenRequirementBean = createSpecimen();
		specimenRequirementBean.setAliquotSpecimenCollection(null);
		specimenRequirementBean.setDeriveSpecimenCollection(null);
		return specimenRequirementBean;
	}

	private static SpecimenRequirementBean createSpecimen() {
		SpecimenRequirementBean specimenRequirementBean = new SpecimenRequirementBean();
		specimenRequirementBean.setParentName("Specimen_E1");
		specimenRequirementBean.setUniqueIdentifier("E1_S0");
		specimenRequirementBean.setDisplayName("Specimen_E1_S0");
		specimenRequirementBean.setLineage("New");
		specimenRequirementBean.setClassName("Tissue");
		specimenRequirementBean.setType("Fixed Tissue");
		specimenRequirementBean.setTissueSite("Accessory sinus, NOS");
		specimenRequirementBean.setTissueSide("Left");
		specimenRequirementBean.setPathologicalStatus("Malignant, Invasive");
		specimenRequirementBean.setConcentration("0");
		specimenRequirementBean.setQuantity("10");
		specimenRequirementBean.setStorageContainerForSpecimen("Auto");

		// Collected and received events
		specimenRequirementBean.setCollectionEventUserId(1);
		specimenRequirementBean.setReceivedEventUserId(1);
		specimenRequirementBean
				.setCollectionEventContainer("Heparin Vacutainer");
		specimenRequirementBean.setReceivedEventReceivedQuality("Cauterized");
		specimenRequirementBean.setCollectionEventCollectionProcedure("Lavage");

		// Aliquot
		specimenRequirementBean.setNoOfAliquots("2");
		specimenRequirementBean.setQuantityPerAliquot("1");
		specimenRequirementBean.setStorageContainerForAliquotSpecimem("Auto");

		specimenRequirementBean.setNoOfDeriveSpecimen(1);
		specimenRequirementBean.setDeriveSpecimen(null);
		return specimenRequirementBean;
	}

	public static void cpEditMigration() throws Exception { // CollectionProtocolTestcase
		// .java
		try {
			// createCP();
			System.out
					.println("---------IN ExcelTestCaseUtility.cpEditMigration-----------");
			System.out.println("user.dir  " + System.getProperty("user.dir"));
			String excelFilePath = System.getProperty("user.dir")
					+ "/excelFiles/CPExpansion.xls";
			ExcelFileReader EX_CP = new ExcelFileReader();
			String allCP[][] = EX_CP.setInfo(excelFilePath);

			new CollectionProtocolEdit().setCP(allCP);
			System.out
					.println("---------END ExcelTestCaseUtility.cpEditMigration-----------");
		} catch (Exception e) {
			System.out.println("Exception in cpEditMigration");
			System.err.println("Exception in cpEditMigration");
			e.printStackTrace();
			throw e;
		}
	}

	public static void createUser() throws Exception {
		try {
			User user = BaseTestCaseUtility.initUser();
			user.setLoginName("test_user@test.com");
			user.setPageOf(Constants.PAGE_OF_USER_ADMIN);
			user = (User) appService.createObject(user);
			Logger.out.info("User added successfully");
			System.out.println("User added successfully");
		} catch (Exception e) {
			System.out.println("Exception while creating User "
					+ e.getMessage());
			throw e;
		}
	}

	public static void createSite() throws Exception {
		try {
			Site site = BaseTestCaseUtility.initSite();
			site.setName("testSite");
			site = (Site) appService.createObject(site);
			System.out.println("Site created successfully");
		} catch (Exception e) {
			System.out.println("Exception while creating Site "
					+ e.getMessage());
			throw e;
		}
	}

	public static void siteAssociation() throws Exception { //SiteTestCases.java

		try {
			System.out
					.println("---------IN ExcelTestCaseUtility.siteAssociation-----------");
			createCP();
			createUser();
			createSite();
			System.out.println("user.dir  " + System.getProperty("user.dir"));
			String excelFilePath = System.getProperty("user.dir")
					+ "/excelFiles/UsersSiteCp.xls";
			ExcelFileReader EX_CP = new ExcelFileReader();
			String allexcel[][] = EX_CP.setInfo(excelFilePath);
			new SiteAssociation().setUserSite(allexcel);
			// new SiteAssociation().setCPSite(allexcel);
			// new SiteAssociation().setUserSiteCP(allexcel);
			System.out
					.println("---------END ExcelTestCaseUtility.siteAssociation-----------");
		} catch (Exception e) {
			System.out.println("Exception in siteAssociation ");
			System.err.println("Exception in siteAssociation ");
			e.printStackTrace();
			throw e;
		}
	}

	/*
	 * public static void debounDataMigration() throws Exception { //
	 * CollectionProtocolTestcase // .java
	 *
	 * try { System.out
	 * .println("---------IN ExcelTestCaseUtility.debounDataMigration-----------"
	 * ); System.out.println("user.dir  " + System.getProperty("user.dir"));
	 * String excelFilePath = System.getProperty("user.dir") +
	 * "/excelFiles/DeBaunMasterList.xls"; ExcelFileReader EX_CP = new
	 * ExcelFileReader(); String allexcel[][] = EX_CP.setInfo(excelFilePath);
	 * new DebounDataMigration().setCPRAndSCG(allexcel); System.out
	 * .println("---------END ExcelTestCaseUtility.debounDataMigration-----------"
	 * ); } catch (Exception e) {
	 * System.out.println("Exception in debounDataMigration ");
	 * System.err.println("Exception in debounDataMigration ");
	 * e.printStackTrace(); throw e; } }
	 */

	public static void addAnticipatedSCGInParticipant() throws Exception {

		try {
			System.out
					.println("---------IN ExcelTestCaseUtility.addAnticipatedSCGInParticipant-----------");
			System.out.println("user.dir  " + System.getProperty("user.dir"));
			String csvFilePath = System.getProperty("user.dir")
					+ "/excelFiles/AntiScgForPart.csv";

			new AddAnticipatedSCGInParticipant().readCSV(csvFilePath);
			System.out
					.println("---------END ExcelTestCaseUtility.addAnticipatedSCGInParticipant-----------");
		} catch (Exception e) {
			System.out.println("Exception in addAnticipatedSCGInParticipant");
			System.err.println("Exception in addAnticipatedSCGInParticipant");
			e.printStackTrace();
			throw e;
		}

	}


//
//	public static void registerPart() throws Exception { // CollectionProtocolTestcase
//
//		//createCP();
//		//registerParticipant();
//		//createSite();
//		//createStorageContainer();
//		try {
//			System.out
//					.println("---------IN ExcelTestCaseUtility.registerPart-----------");
//			System.out.println("user.dir  " + System.getProperty("user.dir"));
//			String excelFilePath = System.getProperty("user.dir")
//			+ "/excelFiles/Deboun.xls";
//			ExcelFileReader EX_CP = new ExcelFileReader();
//			String allexcel[][] = EX_CP.setInfo(excelFilePath);
//			new DebounDataMigration().registerAndCollectSCG(allexcel);
//			System.out
//					.println("---------END ExcelTestCaseUtility.registerPart-----------");
//		} catch (Exception e) {
//			System.out.println("Exception in registerPart");
//			System.err.println("Exception in registerPart");
//			e.printStackTrace();
//			throw e;
//		}
//	}
//
//	public static void createStorageContainer()
//	{
//		try{
//			StorageContainer storageContainer= BaseTestCaseUtility.initStorageContainer();
//			storageContainer.setName("test_contaner");
//			System.out.println(storageContainer);
//
//			Collection holdsStorageTypeCollection = new HashSet();
//			StorageType sttype = new StorageType();
//			sttype.setId(3L);
//			holdsStorageTypeCollection.add(sttype);
//			//storageContainer.setHoldsStorageTypeCollection(holdsStorageTypeCollection);*/
//			storageContainer.setStorageType(sttype);
//			Site site = new Site();
//			site.setId(2L);
//			storageContainer.setSite(site);
//
//			CollectionProtocol collectionProtocol = new CollectionProtocol();
//			collectionProtocol.setId(3L);
//			Collection collectionProtocolCollection = new HashSet();
//			collectionProtocolCollection.add(collectionProtocol);
//			storageContainer.setCollectionProtocolCollection(collectionProtocolCollection);
//
//
//			storageContainer = (StorageContainer) appService.createObject(storageContainer);
//			System.out.println("container name "+ storageContainer.getName());
//			System.out.println("Object created successfully");
//			assertTrue("Object added successfully", true);
//		 }
//		 catch(Exception e){
//			 e.printStackTrace();
//			 assertFalse("could not add object", true);
//		 }
//	}
//
//	public static void participantVitalStatusUpdate() throws Exception {
//
//		try {
//			System.out
//					.println("---------IN ExcelTestCaseUtility.participantVitalStatusUpdate-----------");
//			System.out.println("user.dir  " + System.getProperty("user.dir"));
//			String excelFilePath = System.getProperty("user.dir")
//					+ "/excelFiles/participantVitalStatusUpdate.xls";
//			ExcelFileReader EX_CP = new ExcelFileReader();
//			String allexcel[][] = EX_CP.setInfo(excelFilePath);
//			new ParticipantEdit().parriciapantEdit(allexcel);
//			System.out
//					.println("---------END ExcelTestCaseUtility.participantVitalStatusUpdate-----------");
//		} catch (Exception e) {
//			System.out.println("Exception in participantVitalStatusUpdate");
//			System.err.println("Exception in participantVitalStatusUpdate");
//			e.printStackTrace();
//			throw e;
//		}
//	}
//
//

}
