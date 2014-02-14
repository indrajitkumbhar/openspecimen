
package com.krishagni.catissueplus.core.services.impl;

import java.util.ArrayList;
import java.util.List;

import com.krishagni.catissueplus.core.events.participants.CreateParticipantEvent;
import com.krishagni.catissueplus.core.events.participants.ParticipantCreatedEvent;
import com.krishagni.catissueplus.core.events.participants.ParticipantDetails;
import com.krishagni.catissueplus.core.events.participants.ParticipantDetailsEvent;
import com.krishagni.catissueplus.core.events.participants.ParticipantSummary;
import com.krishagni.catissueplus.core.events.participants.ParticipantUpdatedEvent;
import com.krishagni.catissueplus.core.events.participants.ReqParticipantDetailEvent;
import com.krishagni.catissueplus.core.events.participants.ReqParticipantsSummaryEvent;
import com.krishagni.catissueplus.core.events.participants.UpdateParticipantEvent;
import com.krishagni.catissueplus.core.events.registration.AllParticipantsSummaryEvent;
import com.krishagni.catissueplus.core.repository.ParticipantDao;
import com.krishagni.catissueplus.core.services.ParticipantService;

import edu.wustl.catissuecore.domain.Participant;

public class ParticipantServiceImpl implements ParticipantService {

	private ParticipantDao participantDao;

	@Override
	public AllParticipantsSummaryEvent getallParticipants(ReqParticipantsSummaryEvent event) {
		List<Participant> participants = participantDao.getAllParticipants();
		List<ParticipantSummary> participantsSummary = new ArrayList<ParticipantSummary>();
		for (Participant participant : participants) {
			participantsSummary.add(ParticipantSummary.fromDomain(participant));
		}
		return AllParticipantsSummaryEvent.ok(participantsSummary);
	}

	@Override
	public ParticipantDetailsEvent getParticipant(ReqParticipantDetailEvent event) {
		Participant participant = participantDao.getParticipant(event.getParticipantId());
		return ParticipantDetailsEvent.ok(ParticipantDetails.fromDomain(participant));
	}

	@Override
	public ParticipantCreatedEvent createParticipant(CreateParticipantEvent event) {
		// TODO Auto-generated method stub
		return null;
	}

	/* 
	 * This will update the participant details.
	 * @see com.krishagni.catissueplus.core.services.ParticipantService#updateParticipant(com.krishagni.catissueplus.core.events.participants.UpdateParticipantEvent)
	 */
	@Override
	public ParticipantUpdatedEvent updateParticipant(UpdateParticipantEvent event) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see com.krishagni.catissueplus.core.services.ParticipantService#listPedigree(com.krishagni.catissueplus.core.events.participants.ReqParticipantDetailEvent)
	 */
	@Override
	public Object listPedigree(ReqParticipantDetailEvent event) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * Updates the given relation
	 */
	@Override
	public Object updateRelation() {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * this will create the new relations for the given patients
	 */
	@Override
	public Object createRelation() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setParticipantDao(ParticipantDao participantDao) {
		this.participantDao = participantDao;
	}

}
