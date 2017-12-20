package com.workmarket.service.decisionflow;

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableList;
import com.workmarket.business.decision.DecisionFlowClient;
import com.workmarket.business.decision.gen.Messages.ActivateDecisionFlowTemplateReq;
import com.workmarket.business.decision.gen.Messages.CreateDecisionFlowTemplateReq;
import com.workmarket.business.decision.gen.Messages.CreateDecisionFlowTemplateResp;
import com.workmarket.business.decision.gen.Messages.DeactivateDecisionFlowTemplateReq;
import com.workmarket.business.decision.gen.Messages.DecideRequest;
import com.workmarket.business.decision.gen.Messages.Decider;
import com.workmarket.business.decision.gen.Messages.DeciderType;
import com.workmarket.business.decision.gen.Messages.Decision;
import com.workmarket.business.decision.gen.Messages.DecisionFlowTemplateResponse;
import com.workmarket.business.decision.gen.Messages.DecisionResponse;
import com.workmarket.business.decision.gen.Messages.DecisionResult;
import com.workmarket.business.decision.gen.Messages.GetDecisionFlowTemplateReq;
import com.workmarket.business.decision.gen.Messages.GetDoableDecisionsRequest;
import com.workmarket.business.decision.gen.Messages.QueryDecisionsRequest;
import com.workmarket.business.decision.gen.Messages.StartDecisionFlowRequest;
import com.workmarket.business.decision.gen.Messages.StartDecisionFlowResponse;
import com.workmarket.business.decision.gen.Messages.Status;
import com.workmarket.common.core.RequestContext;
import com.workmarket.dao.decisionflow.CompanyToDecisionFlowTemplateAssociationDAO;
import com.workmarket.dao.decisionflow.WorkToDecisionFlowAssociationDAO;
import com.workmarket.domains.model.Company;
import com.workmarket.service.web.WebRequestContextProvider;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import rx.functions.Action1;

import java.util.List;

@Service
public class DecisionFlowServiceImpl implements DecisionFlowService {

	@Autowired private CompanyToDecisionFlowTemplateAssociationDAO companyToDecisionFlowTemplateAssociationDAO;
	@Autowired private DecisionFlowClient decisionFlowClient;
	@Autowired private WebRequestContextProvider webRequestContextProvider;
	@Autowired private WorkToDecisionFlowAssociationDAO workToDecisionFlowAssociationDAO;

	private static final Logger logger = LoggerFactory.getLogger(DecisionFlowServiceImpl.class);

	@Override
	public DecisionFlowTemplateResponse getDecisionFlowTemplate(String uuid) {
		final RequestContext context = webRequestContextProvider.getRequestContext();
		final GetDecisionFlowTemplateReq request = GetDecisionFlowTemplateReq.newBuilder()
				.setDecisionFlowTemplateUuid(uuid)
				.build();
		final DecisionFlowTemplateResponse.Builder response = DecisionFlowTemplateResponse.newBuilder();

		decisionFlowClient.getDecisionFlowTemplate(request, context).subscribe(
			new Action1<DecisionFlowTemplateResponse>() {
				@Override
				public void call(final DecisionFlowTemplateResponse decisionFlowTemplateResponse) {
					response.mergeFrom(decisionFlowTemplateResponse);
				}
			},
			new Action1<Throwable>() {
				@Override
				public void call(Throwable throwable) {
					response.setStatus(Status.newBuilder()
						.setSuccess(false)
						.addMessage(throwable.getMessage()));
					logger.warn("Failed to get decision flow: {}", throwable.getMessage());
				}
			});
		return response.build();
	}

	@Override
	public CreateDecisionFlowTemplateResp createDecisionFlowTemplate(
		CreateDecisionFlowTemplateReq request,
		Company company) {

		final RequestContext context = webRequestContextProvider.getRequestContext();
		final CreateDecisionFlowTemplateResp.Builder builder = CreateDecisionFlowTemplateResp.newBuilder();

		decisionFlowClient.createDecisionFlowTemplate(request, context).subscribe(
			new Action1<CreateDecisionFlowTemplateResp>() {
				@Override
				public void call(final CreateDecisionFlowTemplateResp response) {
					builder.mergeFrom(response);
				}
			},
			new Action1<Throwable>() {
				@Override
				public void call(Throwable throwable) {
					builder.setStatus(Status.newBuilder()
						.setSuccess(false)
						.addMessage(throwable.getMessage()));
					logger.warn("Failed to create decision flow: {}", throwable.getMessage());
				}
			});

		final CreateDecisionFlowTemplateResp response = builder.build();

		if (response.getStatus().getSuccess()) {
			companyToDecisionFlowTemplateAssociationDAO.addDecisionFlowTemplateAssociation(company, response.getUuid());
		}

		return response;
	}

	@Override
	public CreateDecisionFlowTemplateResp updateDecisionFlow(CreateDecisionFlowTemplateReq request, Company company, String uuid) {

		final RequestContext context = webRequestContextProvider.getRequestContext();
		final CreateDecisionFlowTemplateResp.Builder builder = CreateDecisionFlowTemplateResp.newBuilder();

		decisionFlowClient.updateDecisionFlowTemplate(request, context).subscribe(
			new Action1<CreateDecisionFlowTemplateResp>() {
				@Override
				public void call(final CreateDecisionFlowTemplateResp response) {
					builder.mergeFrom(response);
				}
			},
			new Action1<Throwable>() {
				@Override
				public void call(Throwable throwable) {
					builder.setStatus(Status.newBuilder()
						.setSuccess(false)
						.addMessage(throwable.getMessage()));
					logger.warn("Failed to update decision flow: {}", throwable.getMessage());
				}
			});

		final CreateDecisionFlowTemplateResp response = builder.build();

		if (response.getStatus().getSuccess()) {
			companyToDecisionFlowTemplateAssociationDAO.updateDecisionFlowTemplateAssociation(company, uuid, response.getUuid());
		}

		return response;
	}

	@Override
	public List<String> getDecisionFlowTemplateUuids(final long companyId) {
		return companyToDecisionFlowTemplateAssociationDAO.findDecisionFlowTemplateUuids(companyId);
	}

	@Override
	public Optional<String> getDecisionFlowUuid(final long workId) {
		String flowUuid = workToDecisionFlowAssociationDAO.findDecisionFlowUuid(workId);
		if (StringUtils.isBlank(flowUuid)) {
			return Optional.absent();
		}
		return Optional.of(flowUuid);
	}

	@Override
	public List<Decision> getDoableDecisions(final GetDoableDecisionsRequest doableDecisionsRequest) {
		final ImmutableList.Builder<Decision> decisions = ImmutableList.builder();

		decisionFlowClient
				.getDoableDecisions(doableDecisionsRequest, webRequestContextProvider.getRequestContext())
				.subscribe(
						new Action1<DecisionResponse>() {
							@Override
							public void call(final DecisionResponse response) {
								if (response.getDescisionCount() > 0) {
									decisions.addAll(response.getDescisionList());
								}
							}
						},
						new Action1<Throwable>() {
							@Override
							public void call(Throwable throwable) {
								logger.error("Failed to get decisions: {}", throwable.getMessage());
							}
						});
		return decisions.build();
	}

	@Override
	public List<Decision> queryDecisions(final QueryDecisionsRequest queryDecisionsRequest) {
		final ImmutableList.Builder<Decision> decisions = ImmutableList.builder();
		decisionFlowClient.queryDecisions(queryDecisionsRequest, webRequestContextProvider.getRequestContext()).subscribe(
				new Action1<DecisionResponse>() {
					@Override
					public void call(final DecisionResponse response) {
						if (response.getDescisionCount() > 0) {
							decisions.addAll(response.getDescisionList());
						}
					}
				},
				new Action1<Throwable>() {
					@Override
					public void call(Throwable throwable) {
						logger.error("Failed to get decisions: {}", throwable.getMessage());
					}
				});
		return decisions.build();
	}

	@Override
	public Status approve(final String decisionUuid, final String deciderUuid) {
		final DecideRequest decideRequest = DecideRequest.newBuilder()
				.setFlowNodeUuid(decisionUuid)
				.setDecider(
						Decider.newBuilder()
								.setDeciderType(DeciderType.INDIVIDUAL_DECIDER)
								.setUuid(deciderUuid))
				.setDecisionResult(DecisionResult.DECISION_TRUE)
				.build();

		final Status.Builder status = Status.newBuilder();
		decisionFlowClient.decide(decideRequest, webRequestContextProvider.getRequestContext()).subscribe(
				new Action1<Status>() {
					@Override
					public void call(final Status response) {
						status.mergeFrom(response);
					}
				},
				new Action1<Throwable>() {
					@Override
					public void call(Throwable throwable) {
						status.setSuccess(false).addMessage(throwable.getMessage());
						logger.warn("Failed to get decision flow: {}", throwable.getMessage());
					}
				});
		return status.build();
	}

	@Override
	public StartDecisionFlowResponse start(final String templateUuid) {
		final StartDecisionFlowRequest startDecisionFlowRequest = StartDecisionFlowRequest.newBuilder()
				.setTemplateUuid(templateUuid)
				.build();
		final StartDecisionFlowResponse.Builder startDecisionFlowResponse = StartDecisionFlowResponse.newBuilder();
		decisionFlowClient.startDecisionFlow(startDecisionFlowRequest, webRequestContextProvider.getRequestContext()).subscribe(
				new Action1<StartDecisionFlowResponse>() {
					@Override
					public void call(final StartDecisionFlowResponse response) {
						startDecisionFlowResponse.mergeFrom(response);
					}
				},
				new Action1<Throwable>() {
					@Override
					public void call(Throwable throwable) {
						startDecisionFlowResponse.setStatus(
								Status.newBuilder()
										.setSuccess(false)
										.addMessage(throwable.getMessage()));
						logger.warn("Failed to get decision flow: {}", throwable.getMessage());
					}
				});
		return startDecisionFlowResponse.build();
	}

	@Override
	public Status activate(final String uuid) {
		final ActivateDecisionFlowTemplateReq activateRequest = ActivateDecisionFlowTemplateReq.newBuilder()
				.setDecisionFlowTemplateUuid(uuid)
				.build();
		final Status.Builder status = Status.newBuilder();
		decisionFlowClient.activateDecisionFlowTemplate(activateRequest, webRequestContextProvider.getRequestContext()).subscribe(
				new Action1<Status>() {
					@Override
					public void call(final Status response) {
						status.mergeFrom(response);
					}
				},
				new Action1<Throwable>() {
					@Override
					public void call(Throwable throwable) {
						status.setSuccess(false).addMessage(throwable.getMessage());
						logger.warn("Failed to activate decision flow: {}", throwable.getMessage());
					}
				});
		return status.build();
	}

	@Override
	public Status deactivate(final String uuid) {
		final DeactivateDecisionFlowTemplateReq deactivateRequest = DeactivateDecisionFlowTemplateReq.newBuilder()
				.setDecisionFlowTemplateUuid(uuid)
				.build();
		final Status.Builder status = Status.newBuilder();
		decisionFlowClient.deactivateDecisionFlowTemplate(deactivateRequest, webRequestContextProvider.getRequestContext()).subscribe(
				new Action1<Status>() {
					@Override
					public void call(final Status response) {
						status.mergeFrom(response);
					}
				},
				new Action1<Throwable>() {
					@Override
					public void call(Throwable throwable) {
						status.setSuccess(false).addMessage(throwable.getMessage());
						logger.warn("Failed to deactivate decision flow: {}", throwable.getMessage());
					}
				});
		return status.build();
	}
}
