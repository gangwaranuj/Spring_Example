package com.workmarket.service.business.integration.hooks.webhook;

import com.google.common.collect.Sets;
import com.workmarket.dao.integration.webhook.WebHookDAO;
import com.workmarket.domains.work.dao.state.WorkSubStatusTypeAssociationDAO;
import com.workmarket.domains.model.MboProfile;
import com.workmarket.domains.model.integration.IntegrationEventType;
import com.workmarket.domains.model.integration.IntegrationType;
import com.workmarket.domains.model.integration.webhook.WebHook;
import com.workmarket.domains.model.option.WorkOption;
import com.workmarket.domains.work.model.AbstractWork;
import com.workmarket.domains.work.model.negotiation.AbstractWorkNegotiation;
import com.workmarket.domains.work.model.negotiation.WorkBonusNegotiation;
import com.workmarket.domains.work.model.negotiation.WorkBudgetNegotiation;
import com.workmarket.domains.work.model.negotiation.WorkExpenseNegotiation;
import com.workmarket.domains.work.model.negotiation.WorkNegotiation;
import com.workmarket.domains.work.model.negotiation.WorkRescheduleNegotiation;
import com.workmarket.domains.work.model.state.WorkSubStatusType;
import com.workmarket.domains.work.model.state.WorkSubStatusTypeAssociation;
import com.workmarket.service.business.ProfileService;
import com.workmarket.service.business.integration.event.IntegrationEvent;
import com.workmarket.service.business.integration.hooks.autotask.AutotaskIntegrationService;
import com.workmarket.service.business.queue.integration.IntegrationEventService;
import com.workmarket.domains.work.service.WorkService;
import com.workmarket.service.infra.business.AuthenticationService;
import com.workmarket.service.option.OptionsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.math.BigDecimal;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;

import static com.google.common.base.Preconditions.checkNotNull;

@Service
public class WebHookEventServiceImpl implements WebHookEventService {

	@Autowired AutotaskIntegrationService autotaskIntegrationService;
	@Autowired WebHookIntegrationService webHookIntegrationService;
	@Autowired IntegrationEventService integrationEventService;
	@Autowired WorkSubStatusTypeAssociationDAO workSubStatusTypeAssociationDAO;
	@Autowired WebHookDAO webHookDAO;
	@Autowired WorkService workService;
	@Autowired ProfileService profileService;
	@Autowired AuthenticationService authenticationService;
	@Qualifier("workOptionsService") @Autowired private OptionsService<AbstractWork> workOptionsService;


	@Override
	public void onWorkCreated(String workNumber, String autotaskId) {

		Assert.notNull(workNumber);
		AbstractWork work = workService.findWorkByWorkNumber(workNumber);

		if (autotaskId != null) {
			onWorkCreated(work.getId(), work.getCompany().getId(), Long.valueOf(autotaskId));
		} else {
			onWorkCreated(work.getId(), work.getCompany().getId(), null);
		}
	}

	@Override
	public void onWorkCreated(final Long workId, final String autotaskId) {
		Assert.notNull(workId);
		AbstractWork work = workService.findWork(workId);

		if (autotaskId != null) {
			onWorkCreated(work.getId(), work.getCompany().getId(), Long.valueOf(autotaskId));
		} else {
			onWorkCreated(work.getId(), work.getCompany().getId(), null);
		}
	}

	@Override
	public void onWorkCreated(Long workId, Long companyId, Long autotaskId) {
		Assert.notNull(workId);
		Assert.notNull(companyId);

		if (autotaskId != null) {
			AbstractWork work = workService.findWork(workId);
			autotaskIntegrationService.createExternalWorkNumberForWork(workId, autotaskId);
			integrationEventService.sendEvent(
					IntegrationEvent.newWorkCreateEventAutotask(work.getId(), work.getBuyer().getId(), autotaskId));

		} else {
			List<WebHook> webHooks = webHookDAO.findAllEnabledWebHooksByCompanyAndType(companyId, IntegrationEventType.WORK_CREATE);
			for (WebHook webHook : webHooks) {
				integrationEventService.sendEvent(IntegrationEvent.newWorkCreateEvent(workId, webHook.getId()));
			}
		}

	}

	@Override
	public void onWorkAccepted(Long workId, Long companyId, Long resourceId) {
		Assert.notNull(workId);
		Assert.notNull(companyId);
		Assert.notNull(resourceId);

		// we notify MBO if buyer or seller are using MBO services
		MboProfile mboProfile = profileService.findMboProfile(resourceId);
		boolean notifyMbo = ((mboProfile != null && MboProfile.MBO.equals(mboProfile.getPaymentPreference()))
				|| workOptionsService.hasOptionByEntityId(workId, WorkOption.MBO_ENABLED, "true"));

		if (hasAutotaskIntegration(workId) || notifyMbo) {
			integrationEventService.sendEvent(
					IntegrationEvent.newWorkAcceptEvent(workId, resourceId, null, hasAutotaskIntegration(workId), notifyMbo)
			);
		}

		List<WebHook> webHooks = webHookDAO.findAllEnabledWebHooksByCompanyAndType(companyId, IntegrationEventType.WORK_ACCEPT);
		for (WebHook webHook : webHooks) {
			integrationEventService.sendEvent(
					IntegrationEvent.newWorkAcceptEvent(workId, resourceId, webHook.getId(), false, notifyMbo)
			);
		}

	}

	@Override
	public void onNoteAdded(Long workId, Long companyId, Long noteId) {
		Assert.notNull(workId);
		Assert.notNull(companyId);
		Assert.notNull(noteId);

		if (hasAutotaskIntegration(workId)) {
			integrationEventService.sendEvent(
					IntegrationEvent.newWorkNoteAddEvent(workId, noteId, null, true));
		}

		List<WebHook> webHooks = webHookDAO.findAllEnabledWebHooksByCompanyAndType(companyId, IntegrationEventType.WORK_NOTE_ADD);
		for (WebHook webHook : webHooks) {
			integrationEventService.sendEvent(
					IntegrationEvent.newWorkNoteAddEvent(workId, noteId, webHook.getId(), false));
		}

	}

	@Override
	public void onAssetAdded(Long workId, Long companyId, Long assetId) {
		Assert.notNull(workId);
		Assert.notNull(companyId);
		Assert.notNull(assetId);

		if (hasAutotaskIntegration(workId)) {
			integrationEventService.sendEvent(
					IntegrationEvent.newWorkAssetAddEvent(workId, assetId, null, true));
		}

		List<WebHook> webHooks = webHookDAO.findAllEnabledWebHooksByCompanyAndType(companyId, IntegrationEventType.WORK_ASSET_ADD);
		for (WebHook webHook : webHooks) {
			integrationEventService.sendEvent(IntegrationEvent.newWorkAssetAddEvent(workId, assetId, webHook.getId(), false));
		}

	}

	@Override
	public void onCheckInActiveResource(Long workId, Long companyId, Long timeTrackingId) {
		Assert.notNull(workId);
		Assert.notNull(companyId);

		if (hasAutotaskIntegration(workId)) {
			integrationEventService.sendEvent(IntegrationEvent.newResourceCheckInEvent(workId, null, timeTrackingId, true));
		}

		List<WebHook> webHooks = webHookDAO.findAllEnabledWebHooksByCompanyAndType(companyId, IntegrationEventType.WORK_CHECK_IN);
		for (WebHook webHook : webHooks) {
			integrationEventService.sendEvent(
					IntegrationEvent.newResourceCheckInEvent(workId, webHook.getId(), timeTrackingId, false)
			);
		}

	}

	@Override
	public void onCheckOutActiveResource(Long workId, Long companyId, Long timeTrackingId) {
		Assert.notNull(workId);
		Assert.notNull(companyId);

		if (hasAutotaskIntegration(workId)) {
			integrationEventService.sendEvent(IntegrationEvent.newResourceCheckOutEvent(workId, null, timeTrackingId, true));
		}

		List<WebHook> webHooks = webHookDAO.findAllEnabledWebHooksByCompanyAndType(companyId, IntegrationEventType.WORK_CHECK_OUT);
		for (WebHook webHook : webHooks) {
			integrationEventService.sendEvent(
					IntegrationEvent.newResourceCheckOutEvent(workId, webHook.getId(), timeTrackingId, false)
			);
		}

	}

	@Override
	public void onWorkCustomFieldsUpdated(Long workId, Long companyId) {
		Assert.notNull(workId);
		Assert.notNull(companyId);

		if (hasAutotaskIntegration(workId)) {
			integrationEventService.sendEvent(IntegrationEvent.newWorkCustomFieldsUpdatedEvent(workId, null, true, isApiTriggered()));
		}

		List<WebHook> webHooks = webHookDAO.findAllEnabledWebHooksByCompanyAndType(companyId, IntegrationEventType.WORK_CUSTOM_FIELDS_UPDATE);
		for (WebHook webHook : webHooks) {
			integrationEventService.sendEvent(
					IntegrationEvent.newWorkCustomFieldsUpdatedEvent(workId, webHook.getId(), false, isApiTriggered())
			);
		}

	}

	@Override
	public void onWorkCompleted(Long workId, Long companyId) {
		Assert.notNull(workId);
		Assert.notNull(companyId);

		if (hasAutotaskIntegration(workId)) {
			integrationEventService.sendEvent(IntegrationEvent.newWorkCompleteEvent(workId, null, true));
		}

		List<WebHook> webHooks = webHookDAO.findAllEnabledWebHooksByCompanyAndType(companyId, IntegrationEventType.WORK_COMPLETE);
		for (WebHook webHook : webHooks) {
			integrationEventService.sendEvent(
					IntegrationEvent.newWorkCompleteEvent(workId, webHook.getId(), false)
			);
		}

	}

	@Override
	public void onWorkApproved(Long workId, Long companyId) {
		Assert.notNull(workId);
		Assert.notNull(companyId);

		if (hasAutotaskIntegration(workId)) {
			integrationEventService.sendEvent(IntegrationEvent.newWorkApproveEvent(workId, null, true));
		}

		List<WebHook> webHooks = webHookDAO.findAllEnabledWebHooksByCompanyAndType(companyId, IntegrationEventType.WORK_APPROVE);
		for (WebHook webHook : webHooks) {
			integrationEventService.sendEvent(
					IntegrationEvent.newWorkApproveEvent(workId, webHook.getId(), false)
			);
		}

	}

	@Override
	public void onWorkSent(Long workId, Long companyId) {
		Assert.notNull(workId);
		Assert.notNull(companyId);


		List<WebHook> webHooks = webHookDAO.findAllEnabledWebHooksByCompanyAndType(companyId, IntegrationEventType.WORK_SEND);
		for (WebHook webHook : webHooks) {
			integrationEventService.sendEvent(
					IntegrationEvent.newWorkSentEvent(workId, webHook.getId())
			);
		}

	}

	@Override
	public void onWorkVoided(Long workId, Long companyId) {
		Assert.notNull(workId);
		Assert.notNull(companyId);

		List<WebHook> webHooks = webHookDAO.findAllEnabledWebHooksByCompanyAndType(companyId, IntegrationEventType.WORK_VOID);
		for (WebHook webHook : webHooks) {
			integrationEventService.sendEvent(
					IntegrationEvent.newWorkVoidedEvent(workId, webHook.getId())
			);
		}
	}

	@Override
	public void onWorkCancelled(Long workId, Long companyId) {
		Assert.notNull(workId);
		Assert.notNull(companyId);

		List<WebHook> webHooks = webHookDAO.findAllEnabledWebHooksByCompanyAndType(companyId, IntegrationEventType.WORK_CANCEL);
		for (WebHook webHook : webHooks) {
			integrationEventService.sendEvent(
					IntegrationEvent.newWorkCancelEvent(workId, webHook.getId())
			);
		}
	}

	@Override
	public void onWorkPaid(Long workId, Long companyId) {
		Assert.notNull(workId);
		Assert.notNull(companyId);

		List<WebHook> webHooks = webHookDAO.findAllEnabledWebHooksByCompanyAndType(companyId, IntegrationEventType.WORK_PAY);
		for (WebHook webHook : webHooks) {
			integrationEventService.sendEvent(
					IntegrationEvent.newWorkPaidEvent(workId, webHook.getId())
			);
		}
	}

	@Override
	public void onWorkConfirmed(Long workId, Long companyId) {
		Assert.notNull(workId);
		Assert.notNull(companyId);

		List<WebHook> webHooks = webHookDAO.findAllEnabledWebHooksByCompanyAndType(companyId, IntegrationEventType.WORK_CONFIRM);
		for (WebHook webHook : webHooks) {
			integrationEventService.sendEvent(
					IntegrationEvent.newWorkConfirmedEvent(workId, webHook.getId())
			);
		}
	}

	@Override
	public void onAssetRemoved(Long workId, Long assetId) {
		Assert.notNull(workId);
		Assert.notNull(assetId);

		AbstractWork work = workService.findWork(workId); // TODO: get rid of this
		Assert.notNull(work);
		Assert.notNull(work.getCompany());


		List<WebHook> webHooks = webHookDAO.findAllEnabledWebHooksByCompanyAndType(
				work.getCompany().getId(),
				IntegrationEventType.WORK_ASSET_REMOVE
		);

		for (WebHook webHook : webHooks) {
			integrationEventService.sendEvent(
					IntegrationEvent.newWorkAssetRemoveEvent(workId, webHook.getId(), assetId)
			);
		}
	}

	@Override
	public void onLabelAdded(Long workId, Long companyId, Long workSubStatusTypeAssociationId) {
		Assert.notNull(workId);
		Assert.notNull(companyId);
		Assert.notNull(workSubStatusTypeAssociationId); // TODO: rename

		WorkSubStatusTypeAssociation workLabelAssociation = checkNotNull(workSubStatusTypeAssociationDAO.get(workSubStatusTypeAssociationId));

		String labelCode = workLabelAssociation.getWorkSubStatusType().getCode();
		Set<String> negotiationLabelCodes = Sets.newHashSet(
				WorkSubStatusType.BONUS,
				WorkSubStatusType.BUDGET_INCREASE,
				WorkSubStatusType.EXPENSE_REIMBURSEMENT,
				WorkSubStatusType.RESCHEDULE_REQUEST
		);

		// We don't do "add label" hook for negotiation-related labels... they have their own hooks
		if (negotiationLabelCodes.contains(labelCode)) {
			return;
		}

		List<WebHook> webHooks = webHookDAO.findAllEnabledWebHooksByCompanyAndType(companyId, IntegrationEventType.WORK_LABEL_ADD);
		for (WebHook webHook : webHooks) {
			integrationEventService.sendEvent(
					IntegrationEvent.newWorkLabelAddEvent(workId, webHook.getId(), workSubStatusTypeAssociationId)
			);
		}
	}

	@Override
	public void onLabelRemoved(Long workId, Long companyId, Long workSubStatusTypeAssociationId) {
		Assert.notNull(workId);
		Assert.notNull(companyId);
		Assert.notNull(workSubStatusTypeAssociationId); // TODO: Rename

		List<WebHook> webHooks = webHookDAO.findAllEnabledWebHooksByCompanyAndType(companyId, IntegrationEventType.WORK_LABEL_REMOVE);
		for (WebHook webHook : webHooks) {
			integrationEventService.sendEvent(
					IntegrationEvent.newWorkLabelRemoveEvent(workId, webHook.getId(), workSubStatusTypeAssociationId)
			);
		}
	}

	@Override
	public void onNegotiationRequested(Long workId, Long companyId, AbstractWorkNegotiation negotiation) {
		Assert.notNull(workId);
		Assert.notNull(companyId);
		Assert.notNull(negotiation);

		if (negotiation instanceof WorkNegotiation) {
			List<WebHook> webHooks = webHookDAO.findAllEnabledWebHooksByCompanyAndType(companyId, IntegrationEventType.WORK_NEGOTIATION_REQUEST);

			for (WebHook webHook : webHooks) {
				integrationEventService.sendEvent(
						IntegrationEvent.newWorkNegotiationRequestEvent(workId, negotiation.getId(), webHook.getId())
				);
			}
		} else if (negotiation instanceof WorkRescheduleNegotiation) {
			List<WebHook> webHooks = webHookDAO.findAllEnabledWebHooksByCompanyAndType(companyId, IntegrationEventType.WORK_RESCHEDULE_REQUEST);

			for (WebHook webHook : webHooks) {
				integrationEventService.sendEvent(
						IntegrationEvent.newWorkRescheduleRequestEvent(workId, negotiation.getId(), webHook.getId())
				);
			}
		} else if (negotiation instanceof WorkBudgetNegotiation) {
			List<WebHook> webHooks = webHookDAO.findAllEnabledWebHooksByCompanyAndType(companyId, IntegrationEventType.WORK_BUDGET_INCREASE_REQUEST);

			for (WebHook webHook : webHooks) {
				integrationEventService.sendEvent(
						IntegrationEvent.newWorkBudgetIncreaseRequestEvent(workId, negotiation.getId(), webHook.getId())
				);
			}
		} else if (negotiation instanceof WorkExpenseNegotiation) {
			List<WebHook> webHooks = webHookDAO.findAllEnabledWebHooksByCompanyAndType(companyId, IntegrationEventType.WORK_EXPENSE_REIMBURSEMENT_REQUEST);

			for (WebHook webHook : webHooks) {
				integrationEventService.sendEvent(
						IntegrationEvent.newWorkExpenseReimbursementRequestEvent(workId, negotiation.getId(), webHook.getId())
				);
			}
		} else if (negotiation instanceof WorkBonusNegotiation) {
			List<WebHook> webHooks = webHookDAO.findAllEnabledWebHooksByCompanyAndType(companyId, IntegrationEventType.WORK_BONUS_REQUEST);

			for (WebHook webHook : webHooks) {
				integrationEventService.sendEvent(
						IntegrationEvent.newWorkBonusRequestEvent(workId, negotiation.getId(), webHook.getId())
				);
			}
		}
	}

	@Override
	public void onNegotiationAdded(Long workId, Long companyId, AbstractWorkNegotiation negotiation) {
		Assert.notNull(workId);
		Assert.notNull(companyId);
		Assert.notNull(negotiation);

		 if (negotiation instanceof WorkBudgetNegotiation) {
			List<WebHook> webHooks = webHookDAO.findAllEnabledWebHooksByCompanyAndType(companyId, IntegrationEventType.WORK_BUDGET_INCREASE_ADD);

			for (WebHook webHook : webHooks) {
				integrationEventService.sendEvent(
						IntegrationEvent.newWorkBudgetIncreaseAddEvent(workId, negotiation.getId(), webHook.getId())
				);
			}
		} else if (negotiation instanceof WorkExpenseNegotiation) {
			List<WebHook> webHooks = webHookDAO.findAllEnabledWebHooksByCompanyAndType(companyId, IntegrationEventType.WORK_EXPENSE_REIMBURSEMENT_ADD);

			for (WebHook webHook : webHooks) {
				integrationEventService.sendEvent(
						IntegrationEvent.newWorkExpenseReimbursementAddEvent(workId, negotiation.getId(), webHook.getId())
				);
			}
		} else if (negotiation instanceof WorkBonusNegotiation) {
			List<WebHook> webHooks = webHookDAO.findAllEnabledWebHooksByCompanyAndType(companyId, IntegrationEventType.WORK_BONUS_ADD);

			for (WebHook webHook : webHooks) {
				integrationEventService.sendEvent(
						IntegrationEvent.newWorkBonusAddEvent(workId, negotiation.getId(), webHook.getId())
				);
			}
		}
	}


	@Override
	public void onNegotiationApproved(Long workId, Long companyId, AbstractWorkNegotiation negotiation, BigDecimal amount) {
		Assert.notNull(workId);
		Assert.notNull(companyId);
		Assert.notNull(negotiation);

		if (negotiation instanceof WorkNegotiation) {
			List<WebHook> webHooks = webHookDAO.findAllEnabledWebHooksByCompanyAndType(companyId, IntegrationEventType.WORK_NEGOTIATION_APPROVE);

			for (WebHook webHook : webHooks) {
				integrationEventService.sendEvent(
						IntegrationEvent.newWorkNegotiationApproveEvent(workId, negotiation.getId(), webHook.getId())
				);
			}
		} else if (negotiation instanceof WorkRescheduleNegotiation) {
			List<WebHook> webHooks = webHookDAO.findAllEnabledWebHooksByCompanyAndType(
					companyId, IntegrationEventType.WORK_RESCHEDULE_APPROVE
			);

			for (WebHook webHook : webHooks) {
				integrationEventService.sendEvent(
						IntegrationEvent.newWorkRescheduleApproveEvent(workId, negotiation.getId(), webHook.getId())
				);
			}
		} else if (negotiation instanceof WorkBudgetNegotiation) {

			List<WebHook> webHooks = webHookDAO.findAllEnabledWebHooksByCompanyAndType(
					companyId, IntegrationEventType.WORK_BUDGET_INCREASE_APPROVE
			);
			for (WebHook webHook : webHooks) {
				integrationEventService.sendEvent(
						IntegrationEvent.newWorkBudgetIncreaseApproveEvent(workId, negotiation.getId(), webHook.getId())
				);
			}
		} else if (negotiation instanceof WorkExpenseNegotiation) {

			List<WebHook> webHooks = webHookDAO.findAllEnabledWebHooksByCompanyAndType(
					companyId, IntegrationEventType.WORK_EXPENSE_REIMBURSEMENT_APPROVE
			);
			for (WebHook webHook : webHooks) {
				integrationEventService.sendEvent(
						IntegrationEvent.newWorkExpenseReimbursementApproveEvent(workId, negotiation.getId(), webHook.getId(), amount)
				);
			}
		} else if (negotiation instanceof WorkBonusNegotiation) {

			List<WebHook> webHooks = webHookDAO.findAllEnabledWebHooksByCompanyAndType(
					companyId, IntegrationEventType.WORK_BONUS_APPROVE
			);
			for (WebHook webHook : webHooks) {
				integrationEventService.sendEvent(
						IntegrationEvent.newWorkBonusApproveEvent(workId, negotiation.getId(), webHook.getId(), amount)
				);
			}
		}
	}

	@Override
	public void onNegotiationDeclined(Long workId, Long companyId, AbstractWorkNegotiation negotiation) {
		Assert.notNull(workId);
		Assert.notNull(companyId);
		Assert.notNull(negotiation);

		if (negotiation instanceof WorkNegotiation) {
			List<WebHook> webHooks = webHookDAO.findAllEnabledWebHooksByCompanyAndType(companyId, IntegrationEventType.WORK_NEGOTIATION_DECLINE);

			for (WebHook webHook : webHooks) {
				integrationEventService.sendEvent(
						IntegrationEvent.newWorkNegotiationDeclineEvent(workId, negotiation.getId(), webHook.getId())
				);
			}
		} else if (negotiation instanceof WorkRescheduleNegotiation) {
			List<WebHook> webHooks = webHookDAO.findAllEnabledWebHooksByCompanyAndType(
					companyId, IntegrationEventType.WORK_RESCHEDULE_DECLINE
			);
			for (WebHook webHook : webHooks) {
				integrationEventService.sendEvent(
						IntegrationEvent.newWorkRescheduleDeclineEvent(workId, negotiation.getId(), webHook.getId())
				);
			}
		} else if (negotiation instanceof WorkBudgetNegotiation) {
			List<WebHook> webHooks = webHookDAO.findAllEnabledWebHooksByCompanyAndType(
					companyId, IntegrationEventType.WORK_BUDGET_INCREASE_DECLINE
			);
			for (WebHook webHook : webHooks) {
				integrationEventService.sendEvent(
						IntegrationEvent.newWorkBudgetIncreaseDeclineEvent(workId, negotiation.getId(), webHook.getId())
				);
			}
		} else if (negotiation instanceof WorkExpenseNegotiation) {
			List<WebHook> webHooks = webHookDAO.findAllEnabledWebHooksByCompanyAndType(
					companyId, IntegrationEventType.WORK_EXPENSE_REIMBURSEMENT_DECLINE
			);
			for (WebHook webHook : webHooks) {
				integrationEventService.sendEvent(
						IntegrationEvent.newWorkExpenseReimbursementDeclineEvent(workId, negotiation.getId(), webHook.getId())
				);
			}
		} else if (negotiation instanceof WorkBonusNegotiation) {
			List<WebHook> webHooks = webHookDAO.findAllEnabledWebHooksByCompanyAndType(
					companyId, IntegrationEventType.WORK_BONUS_DECLINE
			);
			for (WebHook webHook : webHooks) {
				integrationEventService.sendEvent(
						IntegrationEvent.newWorkBonusDeclineEvent(workId, negotiation.getId(), webHook.getId())
				);
			}
		}
	}

	private Set<IntegrationType> getIntegrationTypesForWorkId(Long workId) {
		Set<IntegrationType> result = EnumSet.noneOf(IntegrationType.class);
		if (autotaskIntegrationService.isCreatedByAutotask(workId))
			result.add(IntegrationType.AUTOTASK);
		return result;
	}

	private boolean hasAutotaskIntegration(Long workId) {
		return getIntegrationTypesForWorkId(workId).contains(IntegrationType.AUTOTASK);
	}

	// Right now, this is how we determine "API triggered".. but this will likely change
	private boolean isApiTriggered() {
		return authenticationService.getCurrentUser().isApiEnabled();
	}
}
