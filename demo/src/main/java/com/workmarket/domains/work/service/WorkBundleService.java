package com.workmarket.domains.work.service;

import com.workmarket.domains.model.Address;
import com.workmarket.domains.model.Company;
import com.workmarket.domains.model.User;
import com.workmarket.domains.work.model.AbstractWork;
import com.workmarket.domains.work.model.Work;
import com.workmarket.domains.work.model.WorkBundle;
import com.workmarket.service.business.dto.UnassignDTO;
import com.workmarket.service.business.dto.WorkBundleDTO;
import com.workmarket.service.business.event.work.WorkBundleApplySubmitEvent;
import com.workmarket.domains.work.service.resource.WorkAuthorizationResponse;
import com.workmarket.service.business.event.work.WorkBundleCancelSubmitEvent;
import com.workmarket.service.business.wrapper.ValidateWorkResponse;
import com.workmarket.service.helpers.ServiceResponseBuilder;
import com.workmarket.domains.authentication.model.ExtendedUserDetails;
import com.workmarket.web.forms.work.WorkForm;
import com.workmarket.web.models.DataTablesResponse;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Set;

public interface WorkBundleService {
	WorkBundle findById(Long id);
	WorkBundleDTO findByChild(Long childId);

	boolean isAssignmentBundle(AbstractWork work);
	boolean isAssignmentBundle(Long workId);
	boolean isAssignmentBundleLight(String workNumber);
	boolean isAssignmentBundle(String workNumber);

	void addToBundle(Long parentId, Long childId);
	void addToBundle(String parentWorkNumber, String childWorkNumber);
	void addToBundle(String parentWorkNumber, Long childId);
	void addToBundle(Long parentId, String childWorkNumber);
	void addToBundle(WorkBundle parent, Work child);

	List<ValidateWorkResponse> addAllToBundleByWork(WorkBundle parent, List<Work> workList);

	List<ValidateWorkResponse> addAllToBundleByWorkNumbers(WorkBundle parent, List<String> workNumbers);
	List<ValidateWorkResponse> addAllToBundleByWorkNumbers(String parentWorkNumber, List<String> workNumbers);
	List<ValidateWorkResponse> addAllToBundleByWorkNumbers(Long parentId, List<String> workNumbers);

	List<ValidateWorkResponse> addAllToBundleByIds(WorkBundle parent, List<Long> ids);
	List<ValidateWorkResponse> addAllToBundleByIds(String parentWorkNumber, List<Long> ids);
	List<ValidateWorkResponse> addAllToBundleByIds(Long parentId, List<Long> ids);

	void removeFromBundle(Long childId);
	void removeFromBundle(Long parentId, Long childId);
	void removeFromBundle(String parentWorkNumber, String childWorkNumber);
	void removeFromBundle(String parentWorkNumber, Long childId);
	void removeFromBundle(Long parentId, String childWorkNumber);
	void removeFromBundle(WorkBundle parent, Work child);

	ServiceResponseBuilder getBundleData(Long userId, Long parentId);
	ServiceResponseBuilder getBundleData(User user, WorkBundle parent);

	DataTablesResponse<List<String>, Map<String, Object>> getDataTablesResponse(ExtendedUserDetails user, Long id, HttpServletRequest httpRequest);

	Set<Work> getAllWorkInBundle(Long parentId);
	Set<Work> getAllWorkInBundle(String parentWorkNumber);
	Set<Work> getAllWorkInBundle(WorkBundle parent);

	Set<Long> getAllWorkIdsInBundle(String parentWorkNumber);
	Set<Long> getAllWorkIdsInBundle(WorkBundle parent);
	Set<Long> getAllWorkIdsInBundle(Long parentId);

	WorkBundle saveOrUpdateWorkBundle(Long userId, WorkBundleDTO workBundleDTO);

	void acceptAllWorkInBundle(Long workId, Long userId);

	BigDecimal getBundleBudget(User user, WorkBundle parent);

	WorkAuthorizationResponse verifyBundleFunds(Long userId, Long parentId);

	void declineAllWorkInBundle(String workNumber, Long negotiationId, String note);

	void processWorkBundleForm(WorkForm form);

	void updateBundleCalculatedValues(WorkBundle parent);

	void updateBundleCalculatedValues(Long parentId);

	List<WorkBundle> findAllBundlesByStatus(String status);

	List<WorkBundle> findAllDraftBundles(Long userId);
	List<WorkBundle> findAllDraftBundles(Company c);

	List<Address> getBundleAddresses(Long parentId);

	List<Address> getBundleAddresses(WorkBundle parent);

	ServiceResponseBuilder getWorkWithLocations(Long parentId);

	WorkBundle findById(Long id, boolean initialize);

	void applySubmitBundleHandler(WorkBundleApplySubmitEvent event);
	void applySubmitBundle(Long workId, User worker);

	void cancelSubmitBundleHandler(WorkBundleCancelSubmitEvent event);

	List<ValidateWorkResponse> validateAllBundledWorkForSend(List<String> workNumbers, Long userId);
	List<ValidateWorkResponse> validateAllBundledWorkForAdd(List<String> workNumbers, Long parentId);

	boolean authorizeBundleView(long bundleId, ExtendedUserDetails user);
	boolean authorizeBundlePendingRouting(long bundleId, long userId);

	boolean isBundleComplete(Long parentId);
	boolean updateBundleComplete(Long parentId);

	boolean isBundleVoid(Long parentId);
	boolean updateBundleVoid(Long parentId);
	boolean unassignBundle(UnassignDTO unassignDTO);

}
