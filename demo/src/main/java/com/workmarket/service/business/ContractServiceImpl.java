package com.workmarket.service.business;

import com.google.common.base.Optional;
import com.google.common.collect.Sets;
import com.workmarket.dao.company.CompanyDAO;
import com.workmarket.dao.UserDAO;
import com.workmarket.dao.contract.ContractDAO;
import com.workmarket.dao.contract.ContractVersionDAO;
import com.workmarket.dao.contract.ContractVersionUserSignatureDAO;
import com.workmarket.domains.groups.dao.UserGroupDAO;
import com.workmarket.data.solr.indexer.user.UserIndexer;
import com.workmarket.domains.model.User;
import com.workmarket.domains.model.asset.Asset;
import com.workmarket.domains.model.contract.Contract;
import com.workmarket.domains.model.contract.ContractPagination;
import com.workmarket.domains.model.contract.ContractVersion;
import com.workmarket.domains.model.contract.ContractVersionPagination;
import com.workmarket.domains.model.contract.ContractVersionUserSignature;
import com.workmarket.domains.model.contract.ContractVersionUserSignaturePagination;
import com.workmarket.domains.groups.model.UserGroup;
import com.workmarket.domains.groups.model.UserGroupInvitationType;
import com.workmarket.service.business.dto.ContractDTO;
import com.workmarket.service.business.dto.ContractVersionDTO;
import com.workmarket.service.business.dto.ContractVersionUserSignatureDTO;
import com.workmarket.service.business.event.user.UserSearchIndexEvent;
import com.workmarket.service.infra.event.EventRouter;
import com.workmarket.utility.BeanUtilities;
import com.workmarket.utility.DateUtilities;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.List;
import java.util.Set;

@Service
public class ContractServiceImpl implements ContractService {
	private static final Log logger = LogFactory.getLog(ContractServiceImpl.class);

	@Autowired private UserGroupDAO userGroupDAO;
	@Autowired private UserDAO userDAO;
	@Autowired private CompanyDAO companyDAO;
	@Autowired private ContractDAO contractDAO;
	@Autowired private ContractVersionDAO contractVersionDAO;
	@Autowired private UserGroupService userGroupService;
	@Autowired private ContractVersionUserSignatureDAO contractVersionUserSignatureDAO;
	@Autowired private UserIndexer userIndexer;
	@Autowired private EventRouter eventRouter;

	@Override
	public Contract saveOrUpdateContract(ContractDTO contractDTO) throws Exception {
		Assert.notNull(contractDTO);

		Contract contract;
		if (contractDTO.getEntityId() == null) {
			contract = BeanUtilities.newBean(Contract.class, contractDTO);

			if (contractDTO.getCompanyId() != null) {
				contract.setCompany(companyDAO.get(contractDTO.getCompanyId()));
			}
		} else {
			contract = contractDAO.get(contractDTO.getEntityId());
			BeanUtilities.copyProperties(contract, contractDTO);

			if (contractDTO.getCompanyId().equals(BeanUtilities.getId(contract.getCompany()))) {
				contract.setCompany(companyDAO.get(contractDTO.getCompanyId()));
			}
		}

		contractDAO.saveOrUpdate(contract);

		return contract;
	}

	@Override
	public Contract saveOrUpdateContract(Contract contract) throws Exception {
		contractDAO.saveOrUpdate(contract);
		return contract;
	}

	@Override
	public ContractVersion saveOrUpdateContractVersion(ContractVersionDTO contractVersionDTO) throws Exception {
		Assert.notNull(contractVersionDTO);
		Long contractId = contractVersionDTO.getContractId();
		Assert.notNull(contractId);

		Contract contract = findContractById(contractId);
		Assert.notNull(contract, "Unable to find contract");

		ContractVersion contractVersion;
		if (contractVersionDTO.getContractVersionId() == null) {
			contractVersion = BeanUtilities.newBean(ContractVersion.class, contractVersionDTO);
			contractVersion.setContract(contract);
			contractVersion.setCreatedOn(DateUtilities.getCalendarNow());
		} else {
			contractVersion = contractVersionDAO.get(contractVersionDTO.getContractVersionId());
			BeanUtilities.copyProperties(contractVersion, contractVersionDTO);
		}

		contractVersionDAO.saveOrUpdate(contractVersion);

		// Update user group associations if any
		for (Long groupId : userGroupService.getUserGroupIdsWithAgreement(contract.getId())) {
			userGroupService.reinviteAllGroupMembers(groupId, UserGroupInvitationType.TERMS_AND_AGREEMENTS_MODIFICATION);
		}

		return contractVersion;
	}

	@Override
	public ContractVersionUserSignature saveOrUpdateContractVersionUserSignature(ContractVersionUserSignatureDTO contractVersionUserSignatureDTO) throws Exception {
		Assert.notNull(contractVersionUserSignatureDTO);

		ContractVersionUserSignature contractVersionUserSignature;
		if (contractVersionUserSignatureDTO.getContractVersionUserSignatureId() == null) {
			contractVersionUserSignature = BeanUtilities.newBean(ContractVersionUserSignature.class, contractVersionUserSignatureDTO);

			if (contractVersionUserSignatureDTO.getUserId() != null)
				contractVersionUserSignature.setUser(userDAO.get(contractVersionUserSignatureDTO.getUserId()));

			if (contractVersionUserSignatureDTO.getGroupId() != null)
				contractVersionUserSignature.setUserGroup(userGroupDAO.get(contractVersionUserSignatureDTO.getGroupId()));

			if (contractVersionUserSignatureDTO.getContractVersionId() != null)
				contractVersionUserSignature.setContractVersion(contractVersionDAO.get(contractVersionUserSignatureDTO.getContractVersionId()));

			contractVersionUserSignature.setSignature(contractVersionUserSignatureDTO.getSignature());
			contractVersionUserSignature.setCreatedOn(DateUtilities.getCalendarNow());
		} else {
			contractVersionUserSignature = contractVersionUserSignatureDAO.get(contractVersionUserSignatureDTO.getContractVersionUserSignatureId());
			BeanUtilities.copyProperties(contractVersionUserSignature, contractVersionUserSignature);

			if (contractVersionUserSignatureDTO.getUserId().equals(BeanUtilities.getId(contractVersionUserSignature.getUser())))
				contractVersionUserSignature.setUser(userDAO.get(contractVersionUserSignatureDTO.getUserId()));

			if (contractVersionUserSignatureDTO.getGroupId().equals(BeanUtilities.getId(contractVersionUserSignature.getUserGroup())))
				contractVersionUserSignature.setUserGroup(userGroupDAO.get(contractVersionUserSignatureDTO.getGroupId()));

			if (contractVersionUserSignatureDTO.getContractVersionId().equals(BeanUtilities.getId(contractVersionUserSignature.getCreatedOn())))
				contractVersionUserSignature.setContractVersion(contractVersionDAO.get(contractVersionUserSignatureDTO.getContractVersionId()));
		}

		contractVersionUserSignatureDAO.saveOrUpdate(contractVersionUserSignature);

		if (contractVersionUserSignatureDTO.getUserId() == null) {
			logger.debug("Trying to reindex null user for signed contract");
		}

		eventRouter.sendEvent(new UserSearchIndexEvent(contractVersionUserSignatureDTO.getUserId()));

		return contractVersionUserSignature;
	}

	public Contract findContractById(Long contractId) {
		Assert.notNull(contractId);
		Contract contract = contractDAO.findContractById(contractId);
		if (contract != null) {
			contractVersionDAO.initialize(contract.getContractVersions());
		}
		return contract;
	}

	@Override
	public Contract findContractByIdAndCompany(Long contractId, Long companyId) {
		Assert.notNull(contractId);
		Assert.notNull(companyId);
		Contract contract = contractDAO.findContractByIdAndCompany(contractId, companyId);
		if (contract != null) {
			contractVersionDAO.initialize(contract.getContractVersions());
		}
		return contract;
	}

	@Override
	public ContractPagination findAllContractsByCompanyId(Long companyId, ContractPagination contractPagination) {
		return contractDAO.findAllContractsByCompanyId(companyId, contractPagination);
	}

	@Override
	public ContractVersionUserSignaturePagination findAllContractVersionsUserSignaturesByUserId(long userId, ContractVersionUserSignaturePagination contractVersionUserSignaturePagination) {
		return contractVersionUserSignatureDAO.findAllContractVersionsUserSignaturesByUserId(userId, contractVersionUserSignaturePagination);
	}

	@Override
	public ContractVersion findMostRecentContractVersionByContractId(Long contractId) {
		Assert.notNull(contractId);
		return contractVersionDAO.findMostRecentContractVersionByContractId(contractId);
	}

	@Override
	public Optional<Long> findMostRecentContractVersionIdByContractId(final long contractId) {
		return contractVersionDAO.findMostRecentContractVersionIdByContractId(contractId);
	}

	@Override
	public ContractVersionUserSignature findContractVersionUserSignatureByContractVersionIdAndUserId(Long contractVersionId, Long userId) {
		Assert.notNull(contractVersionId);
		Assert.notNull(userId);

		return contractVersionUserSignatureDAO.findBy(
			"contractVersion.id", contractVersionId,
			"user.id", userId
		);
	}

	@Override
	public ContractVersion findContractVersionByIdAndCompany(Long contractVersionId, Long companyId) {
		Assert.notNull(contractVersionId);
		Assert.notNull(companyId);
		return contractVersionDAO.findContractVersionByIdAndCompany(contractVersionId, companyId);
	}

	@Override
	public Contract updateContractActiveFlag(Long contractId, boolean active) {
		Assert.notNull(contractId);
		Contract contract = contractDAO.findContractById(contractId);
		Assert.notNull(contract);
		contract.setActive(active);
		return contract;
	}

	@Override
	public boolean activeContractExists(String name, Long companyId) {
		Assert.hasText(name);
		Contract contract = contractDAO.findActiveContractByNameAndCompanyId(name, companyId);
		return contract != null;
	}

	@Override
	public ContractVersionPagination findAllContractVersionsByContractId(Long contractId, ContractVersionPagination pagination) {
		return contractVersionDAO.findAllContractVersionsByContractId(contractId, pagination);
	}

	@Override
	public List<Contract> findAllByCompanyId(Long companyId) {
		return contractDAO.findAllBy(
			"company.id", companyId,
			"active", true
		);
	}

	@Override
	public Set<Asset> getMostRecentAssetsForContractId(Long id) {
		ContractVersion version = findMostRecentContractVersionByContractId(id);
		return version.getContractVersionAssets();
	}

	@Override
	public ContractVersionUserSignature findOrCreateContractVersionUserSignature(ContractVersionUserSignatureDTO dto) throws Exception {
		ContractVersionUserSignature signature =
			findContractVersionUserSignatureByContractVersionIdAndUserId(
				dto.getContractVersionId(),
				dto.getUserId()
			);

		if (signature == null) {
			return saveOrUpdateContractVersionUserSignature(dto);
		}
		return signature;
	}

	@Override
	public Set<ContractVersion> findUnsignedContractsByUser(User user,List<Contract> contracts){
		Set<ContractVersion> requiredContractVersions = Sets.newLinkedHashSet();
		for (Contract c : contracts){
			requiredContractVersions.add(findMostRecentContractVersionByContractId(c.getId()));
		}

		Set<ContractVersion> signedContractVersions = Sets.newLinkedHashSet();
		for (ContractVersionUserSignature signature : findAllContractVersionsUserSignaturesByUserId(user.getId(), new ContractVersionUserSignaturePagination(true)).getResults()){
			signedContractVersions.add(signature.getContractVersion());
		}

		Set<ContractVersion> matchingContractVersions = Sets.newHashSet(signedContractVersions);
		matchingContractVersions.retainAll(signedContractVersions);
		requiredContractVersions.removeAll(signedContractVersions);
		return requiredContractVersions;
	}

	public UserDAO getUserDAO() {
		return userDAO;
	}

	public void setUserDAO(UserDAO userDAO) {
		this.userDAO = userDAO;
	}

	public UserGroupService getUserGroupService() {
		return userGroupService;
	}

	public void setUserGroupService(UserGroupService userGroupService) {
		this.userGroupService = userGroupService;
	}
}
