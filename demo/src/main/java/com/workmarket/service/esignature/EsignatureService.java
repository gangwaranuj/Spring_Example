package com.workmarket.service.esignature;

import com.google.common.base.Optional;
import com.workmarket.api.v2.model.SignableEsignatureResponseDTO;
import com.workmarket.biz.esignature.gen.Messages.GetDocumentByStatusResp;
import com.workmarket.biz.esignature.gen.Messages.GetRequestByTemplateIdAndUserUuidResp;
import com.workmarket.biz.esignature.gen.Messages.GetTemplatesResp;
import com.workmarket.biz.esignature.gen.Messages.Template;

import java.util.List;

public interface EsignatureService {

	Optional<String> getTemplateName(String templateUuid, String companyUuid);

	GetRequestByTemplateIdAndUserUuidResp getStatus(String templateUuid, String userUuid);

	SignableEsignatureResponseDTO getOrCreateSignable(
			String companyUuid,
			String templateUuid,
			String userUuid,
			String email,
			String fullName);

	GetDocumentByStatusResp getSignableByUserNumber(String templateUuid, String userNumber);

	GetTemplatesResp getTemplates(String companyUuid);

	List<Template> getSignedTemplates(String companyUuid, String userUuid);

}
