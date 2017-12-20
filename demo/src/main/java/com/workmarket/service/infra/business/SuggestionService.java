package com.workmarket.service.infra.business;

import com.workmarket.domains.model.banking.BankRouting;
import com.workmarket.dto.SuggestionDTO;
import com.workmarket.dto.UserSuggestionDTO;
import com.workmarket.dto.VendorSuggestionDTO;
import com.workmarket.service.infra.dto.WorkBundleSuggestionDTO;

import java.util.List;

public interface SuggestionService {

	List<SuggestionDTO> suggestCompany(String prefix);
	List<SuggestionDTO> suggestGroup(String prefix);

	List<VendorSuggestionDTO> suggestVendor(String prefix);

	List<UserSuggestionDTO> suggestUser(String prefix);
	List<UserSuggestionDTO> suggestUser(String prefix, Long companyId);
	List<UserSuggestionDTO> suggestInternalUser(String prefix, Long companyId);
	List<UserSuggestionDTO> suggestWorkers(String prefix, Long companyId, boolean internalOnly, boolean externalOnly);

	List<SuggestionDTO> suggestProject(String prefix, Long userId);
	List<SuggestionDTO> suggestSkills(String prefix);
	List<SuggestionDTO> suggestSpecialties(String prefix);
	List<SuggestionDTO> suggestTools(String prefix);

	List<SuggestionDTO> suggestClientCompany(String prefix, Integer companyId);
	List<BankRouting> suggestBankRouting(String prefix);
	List<BankRouting> suggestBankRouting(String text, String countryId);

	List<WorkBundleSuggestionDTO> suggestWorkBundle(String prefix, Long userId);
}
