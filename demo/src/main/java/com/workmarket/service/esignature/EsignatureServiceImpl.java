package com.workmarket.service.esignature;

import com.google.common.base.Optional;
import com.workmarket.api.v2.model.SignableEsignatureResponseDTO;
import com.workmarket.biz.esignature.EsignatureClient;
import com.workmarket.biz.esignature.gen.Messages.GetDocumentByStatusReq;
import com.workmarket.biz.esignature.gen.Messages.GetDocumentByStatusResp;
import com.workmarket.biz.esignature.gen.Messages.GetRequestByTemplateIdAndUserUuidReq;
import com.workmarket.biz.esignature.gen.Messages.GetRequestByTemplateIdAndUserUuidResp;
import com.workmarket.biz.esignature.gen.Messages.GetRequestsByUserUuidReq;
import com.workmarket.biz.esignature.gen.Messages.GetRequestsByUserUuidResp;
import com.workmarket.biz.esignature.gen.Messages.GetTemplatesReq;
import com.workmarket.biz.esignature.gen.Messages.GetTemplatesResp;
import com.workmarket.biz.esignature.gen.Messages.Request;
import com.workmarket.biz.esignature.gen.Messages.RequestState;
import com.workmarket.biz.esignature.gen.Messages.SignatureReq;
import com.workmarket.biz.esignature.gen.Messages.SignatureResp;
import com.workmarket.biz.esignature.gen.Messages.Template;
import com.workmarket.domains.model.User;
import com.workmarket.service.business.UserService;
import com.workmarket.service.web.WebRequestContextProvider;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;


@Component
public class EsignatureServiceImpl implements EsignatureService {

	private static final Logger logger = LoggerFactory.getLogger(EsignatureServiceImpl.class);

	@Autowired private EsignatureClient esignatureClient;
	@Autowired private UserService userService;
	@Autowired private WebRequestContextProvider webRequestContextProvider;

	@Override
	public Optional<String> getTemplateName(final String templateUuid, final String companyUuid) {
		GetTemplatesResp templatesResponse = getTemplates(companyUuid);
		for (final Template template : templatesResponse.getTemplateList()) {
			if (StringUtils.equals(template.getId(), templateUuid)) {
				return Optional.of(template.getTitle());
			}
		}
		return Optional.absent();
	}

	@Override
	public GetRequestByTemplateIdAndUserUuidResp getStatus(final String templateUuid, final String userUuid) {
		final GetRequestByTemplateIdAndUserUuidReq request = GetRequestByTemplateIdAndUserUuidReq.newBuilder()
				.setUserUuid(userUuid)
				.setTemplateUuid(templateUuid)
				.build();

		return esignatureClient.getSignatureRequestByTemplateAndUser(request, webRequestContextProvider.getRequestContext())
			.toBlocking()
			.singleOrDefault(GetRequestByTemplateIdAndUserUuidResp.getDefaultInstance());
	}

	@Override
	public SignableEsignatureResponseDTO getOrCreateSignable(
			final String companyUuid,
			final String templateUuid,
			final String userUuid,
			final String email,
			final String fullName) {
		final GetRequestByTemplateIdAndUserUuidResp statusResponse = getStatus(templateUuid, userUuid);
		switch (statusResponse.getRequest().getStatus()) {
			case NO_REQUEST_STATE:
			case DRAFT: {
				final SignatureResp signable = createSignable(companyUuid, templateUuid, userUuid, email, fullName);
				return SignableEsignatureResponseDTO.newBuilder()
					.withClientId(signable.getClientId())
					.withSigningUrl(signable.getSigningUrl())
					.build();
			}
			default: {
				final GetDocumentByStatusResp signable = getSignable(templateUuid, userUuid);
				return SignableEsignatureResponseDTO.newBuilder()
					.withClientId(signable.getClientId())
					.withSigningUrl(signable.getSigningUrl())
					.build();
			}
		}
	}

	private SignatureResp createSignable(
			final String companyUuid,
			final String templateUuid,
			final String userUuid,
			final String email,
			final String fullName) {

		final SignatureReq request = SignatureReq.newBuilder()
				.setCompanyUuid(companyUuid)
				.setTemplateUuid(templateUuid)
				.setUserUuid(userUuid)
				.setEmail(email)
				.setFullName(fullName)
				.setRole("Worker")
				.build();

		return esignatureClient.createSignatureRequest(request, webRequestContextProvider.getRequestContext())
			.toBlocking().singleOrDefault(SignatureResp.getDefaultInstance());
	}

	@Override
	public GetDocumentByStatusResp getSignableByUserNumber(final String templateUuid, final String userNumber) {
		final User user = userService.findUserByUserNumber(userNumber);
		if (user == null) {
			return GetDocumentByStatusResp.getDefaultInstance();
		}
		return getSignable(templateUuid, user.getUuid());
	}

	private GetDocumentByStatusResp getSignable(final String templateUuid, final String userUuid) {
		final GetDocumentByStatusReq request = GetDocumentByStatusReq.newBuilder()
				.setRequest(
						GetRequestByTemplateIdAndUserUuidReq.newBuilder()
								.setUserUuid(userUuid)
								.setTemplateUuid(templateUuid))
				.build();

		return esignatureClient.getDocumentByTemplateAndUser(request, webRequestContextProvider.getRequestContext())
			.toBlocking().singleOrDefault(GetDocumentByStatusResp.getDefaultInstance());
	}

	@Override
	public GetTemplatesResp getTemplates(final String companyUuid) {
		final GetTemplatesReq getTemplatesReq = GetTemplatesReq.newBuilder()
				.setCompanyUuid(companyUuid)
				.build();

		//using toBlocking because subscribe doesn't wait for the http call in another thread.
		return esignatureClient.listTemplates(getTemplatesReq, webRequestContextProvider.getRequestContext())
			.toBlocking().singleOrDefault(GetTemplatesResp.getDefaultInstance());
	}

	@Override
	public List<Template> getSignedTemplates(final String companyUuid, final String userUuid) {
		final GetRequestsByUserUuidReq request = GetRequestsByUserUuidReq.newBuilder()
				.setCompanyUuid(companyUuid)
				.setUserUuid(userUuid)
				.build();

		final GetRequestsByUserUuidResp esignatures =
				esignatureClient.getRequestsByUser(request, webRequestContextProvider.getRequestContext())
						.toBlocking()
						.singleOrDefault(GetRequestsByUserUuidResp.getDefaultInstance());

		final List<Request> signedRequests = getSignedRequests(esignatures.getRequestList());
		final List<String> signedRequestUuids = extractRequestUuid(signedRequests);
		final GetTemplatesResp companyTemplates = getTemplates(companyUuid);
		return filterTemplatesByUuid(companyTemplates, signedRequestUuids);
	}

	private List<Request> getSignedRequests(final List<Request> requests) {
		final List<Request> signed = new ArrayList<>();
		for (final Request request : requests) {
			if (RequestState.SIGNED == request.getStatus()) {
				signed.add(request);
			}
		}
		return signed;
	}

	private List<String> extractRequestUuid(final List<Request> requests) {
		final List<String> requestUuids = new ArrayList<>();
		for (final Request request : requests) {
			requestUuids.add(request.getTemplateUuid());
		}
		return requestUuids;
	}

	private List<Template> filterTemplatesByUuid(final GetTemplatesResp templates, final List<String> uuids) {
		final List<Template> matchingTemplates = new ArrayList<>();
		for (final Template template : templates.getTemplateList()) {
			if (uuids.contains(template.getId())) {
				matchingTemplates.add(template);
			}
		}
		return matchingTemplates;
	}

}
