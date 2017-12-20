package com.workmarket.service.infra.business;

import com.google.common.collect.Lists;
import com.workmarket.dao.UserDAO;
import com.workmarket.dao.banking.BankRoutingDAO;
import com.workmarket.dao.company.CompanyDAO;
import com.workmarket.dao.crm.ClientCompanyDAO;
import com.workmarket.dao.skill.SkillDAO;
import com.workmarket.dao.specialty.SpecialtyDAO;
import com.workmarket.dao.tool.ToolDAO;
import com.workmarket.domains.groups.dao.UserGroupDAO;
import com.workmarket.domains.model.Address;
import com.workmarket.domains.model.Company;
import com.workmarket.domains.model.banking.BankRouting;
import com.workmarket.domains.velvetrope.guest.UserGuest;
import com.workmarket.domains.velvetrope.rope.MarketplaceRope;
import com.workmarket.domains.work.dao.WorkBundleDAO;
import com.workmarket.domains.work.dao.project.ProjectDAO;
import com.workmarket.dto.SuggestionDTO;
import com.workmarket.dto.UserSuggestionDTO;
import com.workmarket.dto.VendorSuggestionDTO;
import com.workmarket.service.business.AddressService;
import com.workmarket.service.infra.dto.WorkBundleSuggestionDTO;
import com.workmarket.velvetrope.Doorman;
import org.apache.commons.lang3.mutable.MutableBoolean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SuggestionServiceImpl implements SuggestionService {

	@Autowired private CompanyDAO companyDAO;
	@Autowired private UserDAO userDAO;
	@Autowired private UserGroupDAO userGroupDAO;
	@Autowired private SpecialtyDAO specialtyDAO;
	@Autowired private ToolDAO toolDAO;
	@Autowired private SkillDAO skillDAO;
	@Autowired private ClientCompanyDAO clientCompanyDAO;
	@Autowired private BankRoutingDAO bankRoutingDAO;
	@Autowired private WorkBundleDAO workBundleDAO;
	@Autowired private AddressService addressService;
	@Autowired private ProjectDAO projectDAO;
	@Autowired private AuthenticationService authenticationService;
	@Autowired @Qualifier("marketplaceDoorman") private Doorman<MarketplaceRope> marketplaceDoorman;

	public List<SuggestionDTO> suggestCompany(String prefix) {
		return companyDAO.suggest(prefix, "effectiveName");
	}

	public List<SuggestionDTO> suggestGroup(String prefix) {
		return userGroupDAO.suggest(prefix, "name",
				authenticationService.getCurrentUser().getCompany().getId());
	}

	@Override public List<VendorSuggestionDTO> suggestVendor(String prefix) {
		return transformToVendorSuggestionDTO(companyDAO.suggest(prefix, true));
	}

	public List<UserSuggestionDTO> suggestUser(String prefix) {
		return userDAO.suggest(prefix);
	}

	@Override
	public List<UserSuggestionDTO> suggestUser(String prefix, Long companyId){
		MutableBoolean hasMarketplace = new MutableBoolean(false);
		marketplaceDoorman.welcome(new UserGuest(authenticationService.getCurrentUser()), new MarketplaceRope(hasMarketplace));
		return userDAO.suggestWorkers(prefix, companyId, false, false, hasMarketplace.isTrue());
	}

	@Override
	public List<UserSuggestionDTO> suggestInternalUser(String prefix, Long companyId) {
		MutableBoolean hasMarketplace = new MutableBoolean(false);
		marketplaceDoorman.welcome(new UserGuest(authenticationService.getCurrentUser()), new MarketplaceRope(hasMarketplace));
		return userDAO.suggestWorkers(prefix, companyId, true, false, hasMarketplace.isTrue());
	}

	@Override
	public List<UserSuggestionDTO> suggestWorkers(String prefix, Long companyId, boolean internalOnly, boolean externalOnly) {
		MutableBoolean hasMarketplace = new MutableBoolean(false);
		marketplaceDoorman.welcome(new UserGuest(authenticationService.getCurrentUser()), new MarketplaceRope(hasMarketplace));
		return userDAO.suggestWorkers(prefix, companyId, internalOnly, externalOnly, hasMarketplace.isTrue());
	}

	@Override
	public List<WorkBundleSuggestionDTO> suggestWorkBundle(String prefix, Long userId) {
		return workBundleDAO.suggest(prefix, userId);
	}

	@Override
	public List<SuggestionDTO> suggestProject(String prefix, Long userId) {
		return projectDAO.suggest(prefix, userId);
	}

	public List<SuggestionDTO> suggestSkills(String prefix) {

		return skillDAO.suggest(prefix, "name");
	}

	@Override
	public List<SuggestionDTO> suggestSpecialties(String prefix) {

		return specialtyDAO.suggest(prefix, "name");
	}

	@Override
	public List<SuggestionDTO> suggestTools(String prefix) {

		return toolDAO.suggest(prefix, "name");
	}

	@Override
	public List<SuggestionDTO> suggestClientCompany(String prefix, Integer companyId) {
		if (companyId == null)
			return Lists.newArrayList();

		return clientCompanyDAO.suggest(prefix, "name", companyId.longValue());
	}

	@Override
	@Deprecated
	public List<BankRouting> suggestBankRouting(String prefix) {
		return bankRoutingDAO.suggest(prefix);
	}

	@Override
	public List<BankRouting> suggestBankRouting(String text, String countryId) {
		return bankRoutingDAO.suggestInCountry(text, countryId);
	}

	private List<VendorSuggestionDTO> transformToVendorSuggestionDTO(final List<Company> vendors) {
		List<VendorSuggestionDTO> vendorSuggestions = Lists.newArrayList();

		for (final Company vendor : vendors) {
			VendorSuggestionDTO vendorSuggestionDTO = new VendorSuggestionDTO();
			// NOTE: vendors/companies don't require address. Suggest them anyway.
			final Address address = vendor.getAddress();
			vendorSuggestionDTO.setId(vendor.getId());
			vendorSuggestionDTO.setCompanyNumber(vendor.getCompanyNumber());
			vendorSuggestionDTO.setName(vendor.getName());
			vendorSuggestionDTO.setEffectiveName(vendor.getEffectiveName());
			vendorSuggestionDTO.setCityStateCountry(getCityStateCountry(address));
			vendorSuggestions.add(vendorSuggestionDTO);
		}
		return vendorSuggestions;
	}

	private String getCityStateCountry(final Address address) {
		return address == null ? "" :
			address.getCity() + ", " + address.getState().getShortName() + ", " + address.getState().getCountry();
	}
}
