package com.workmarket.domains.work.service;

import ch.lambdaj.function.matcher.Predicate;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.workmarket.common.service.helpers.ServiceMessageHelper;
import com.workmarket.dao.LocationDAO;
import com.workmarket.service.infra.business.UserRoleService;
import com.workmarket.data.solr.model.SolrWorkData;
import com.workmarket.data.solr.model.WorkSearchDataPagination;
import com.workmarket.domains.authentication.model.ExtendedUserDetails;
import com.workmarket.domains.authentication.services.ExtendedUserDetailsService;
import com.workmarket.domains.model.Address;
import com.workmarket.domains.model.AddressType;
import com.workmarket.domains.model.Company;
import com.workmarket.domains.model.DressCode;
import com.workmarket.domains.model.Location;
import com.workmarket.domains.model.LocationType;
import com.workmarket.domains.model.ManageMyWorkMarket;
import com.workmarket.domains.model.User;
import com.workmarket.domains.model.WorkStatusType;
import com.workmarket.domains.model.acl.Permission;
import com.workmarket.domains.model.crm.ClientLocation;
import com.workmarket.domains.model.validation.ConstraintViolation;
import com.workmarket.domains.model.validation.MessageKeys;
import com.workmarket.domains.payments.service.AccountRegisterAuthorizationService;
import com.workmarket.domains.reports.service.WorkReportService;
import com.workmarket.domains.work.dao.WorkBundleDAO;
import com.workmarket.domains.work.model.AbstractWork;
import com.workmarket.domains.work.model.Work;
import com.workmarket.domains.work.model.WorkBundle;
import com.workmarket.domains.work.model.negotiation.AbstractWorkNegotiation;
import com.workmarket.domains.work.model.negotiation.WorkNegotiation;
import com.workmarket.domains.work.service.resource.WorkAuthorizationResponse;
import com.workmarket.domains.work.service.route.WorkBundleRouting;
import com.workmarket.domains.work.service.validator.WorkSaveRequestValidator;
import com.workmarket.domains.work.service.workresource.WorkResourceDetailCache;
import com.workmarket.dto.AddressDTO;
import com.workmarket.helpers.GeoMathHelper;
import com.workmarket.service.business.AddressService;
import com.workmarket.service.business.LocationService;
import com.workmarket.service.business.UserService;
import com.workmarket.service.business.VendorService;
import com.workmarket.service.business.dto.CompleteWorkDTO;
import com.workmarket.service.business.dto.UnassignDTO;
import com.workmarket.service.business.dto.WorkBundleDTO;
import com.workmarket.service.business.dto.WorkCostDTO;
import com.workmarket.service.business.dto.WorkNegotiationDTO;
import com.workmarket.service.business.event.EventFactory;
import com.workmarket.service.business.event.work.WorkBundleAcceptEvent;
import com.workmarket.service.business.event.work.WorkBundleApplySubmitEvent;
import com.workmarket.service.business.event.work.WorkBundleCancelSubmitEvent;
import com.workmarket.service.business.event.work.WorkBundleDeclineOfferEvent;
import com.workmarket.service.business.status.ValidateWorkStatus;
import com.workmarket.service.business.wrapper.ValidateWorkResponse;
import com.workmarket.service.helpers.ServiceResponseBuilder;
import com.workmarket.service.infra.business.AuthenticationService;
import com.workmarket.service.infra.business.InvariantDataService;
import com.workmarket.service.infra.event.EventRouter;
import com.workmarket.service.infra.security.WorkContext;
import com.workmarket.service.search.work.WorkSearchService;
import com.workmarket.service.thrift.TWorkFacadeService;
import com.workmarket.service.thrift.transactional.work.WorkRequestInfo;
import com.workmarket.thrift.work.RequestContext;
import com.workmarket.thrift.work.WorkActionException;
import com.workmarket.thrift.work.WorkRequest;
import com.workmarket.thrift.work.WorkResponse;
import com.workmarket.thrift.work.WorkSaveRequest;
import com.workmarket.utility.CollectionUtilities;
import com.workmarket.utility.DateUtilities;
import com.workmarket.utility.StringUtilities;
import com.workmarket.web.forms.work.WorkBundleForm;
import com.workmarket.web.forms.work.WorkForm;
import com.workmarket.web.helpers.ThriftValidationMessageHelper;
import com.workmarket.web.models.DataTablesRequest;
import com.workmarket.web.models.DataTablesResponse;
import org.apache.commons.collections.CollectionUtils;
import org.hibernate.Hibernate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;

import javax.annotation.Nullable;
import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import static ch.lambdaj.Lambda.extract;
import static ch.lambdaj.Lambda.filter;
import static ch.lambdaj.Lambda.on;
import static org.apache.commons.collections.CollectionUtils.isNotEmpty;

@Service
public class WorkBundleServiceImpl implements WorkBundleService {
	@Autowired private WorkBundleDAO workBundleDAO;
	@Autowired private WorkService workService;
	@Autowired private UserService userService;
	@Autowired private AuthenticationService authenticationService;
	@Autowired private WorkReportService workReportService;
	@Autowired private TWorkFacadeService tWorkFacadeService;
	@Autowired private ServiceMessageHelper messageHelper;
	@Autowired private LocationService locationService;
	@Autowired private InvariantDataService invariantDataService;
	@Autowired private LocationDAO locationDAO;
	@Autowired private AddressService addressService;
	@Autowired private AccountRegisterAuthorizationService accountRegisterAuthorizationService;
	@Autowired private ExtendedUserDetailsService extendedUserDetailsService;
	@Autowired private WorkNegotiationService workNegotiationService;
	@Autowired private EventRouter eventRouter;
	@Autowired private EventFactory eventFactory;
	@Autowired private WorkSaveRequestValidator workSaveRequestValidator;
	@Autowired private WorkBundleRouting workBundleRouting;
	@Autowired private WorkSearchService workSearchService;
	@Autowired private WorkResourceDetailCache workResourceDetailCache;
	@Autowired private VendorService vendorService;
	@Autowired private UserRoleService userRoleService;

	private static final int TITLE_COLUMN = 0;
	private static final int DATE_COLUMN = 2;

	private static final int INDUSTRY_TECH = 1000;

	private static final String WORK_COST_KEY = "workCost";
	private static final String OVERALL_BUDGET_KEY = "overallBudget";

	private static final Logger logger = LoggerFactory.getLogger(WorkBundleServiceImpl.class);

	private static final NumberFormat usdCostFormat = NumberFormat.getCurrencyInstance(Locale.US);

	static {
		usdCostFormat.setMinimumFractionDigits(2);
		usdCostFormat.setMaximumFractionDigits(2);
	}

	@Override
	public WorkBundle findById(Long id) {
		return findById(id, false);
	}

	@Override
	public WorkBundle findById(Long id, boolean initialize) {
		WorkBundle workBundle = workBundleDAO.get(id);

		if (workBundle != null && initialize) {
			Hibernate.initialize(workBundle.getBundle());
		}

		return workBundle;
	}

	@Override
	public WorkBundleDTO findByChild(Long childId) {
		Assert.notNull(childId);
		return workBundleDAO.findByChildId(childId);
	}

	@Override
	public List<WorkBundle> findAllBundlesByStatus(String status) {
		return workBundleDAO.findAllBy("workStatusType.code", status);
	}

	@Override
	public List<WorkBundle> findAllDraftBundles(Long userId) {
		User u = userService.findUserById(userId);
		Assert.notNull(u);
		return findAllDraftBundles(u.getCompany());
	}

	@Override
	public List<WorkBundle> findAllDraftBundles(Company c) {
		return workBundleDAO.findAllBy("workStatusType.code", WorkStatusType.DRAFT, "company.id", c.getId());
	}

	@Override
	public boolean isAssignmentBundle(AbstractWork work) {
		return work != null && (work instanceof WorkBundle);
	}

	@Override
	public boolean isAssignmentBundle(Long workId) {
		Assert.notNull(workId);
		return isAssignmentBundle(workService.findWork(workId, false));
	}

	@Override
	public boolean isAssignmentBundle(String workNumber) {
		Assert.notNull(workNumber);
		return isAssignmentBundle(workService.findWorkByWorkNumber(workNumber, false));
	}

	@Override
	public boolean isAssignmentBundleLight(String workNumber) {
		Assert.notNull(workNumber);
		return workBundleDAO.isAssignmentBundle(workNumber);
	}

	@Override
	public void addToBundle(WorkBundle parent, Work child) {
		Assert.notNull(parent);
		Assert.notNull(child);
		child.setParent(parent);
		child.getManageMyWorkMarket().setShowInFeed(false);
		if (parent.getBundle() == null) {
			Set<Work> bundle = new HashSet<>();
			parent.setBundle(bundle);
		}
		parent.getBundle().add(child);
	}

	@Override
	public void addToBundle(Long parentId, Long childId) {
		Assert.notNull(parentId);
		Assert.notNull(childId);
		WorkBundle parent = workService.findWork(parentId);
		Work child = workService.findWork(childId);
		addToBundle(parent, child);
	}

	@Override
	public void addToBundle(String parentWorkNumber, String childWorkNumber) {
		Assert.notNull(parentWorkNumber);
		Assert.notNull(childWorkNumber);
		WorkBundle parent = workService.findWorkByWorkNumber(parentWorkNumber);
		Work child = workService.findWorkByWorkNumber(childWorkNumber);
		addToBundle(parent, child);
	}

	@Override
	public void addToBundle(Long parentId, String childWorkNumber) {
		Assert.notNull(parentId);
		Assert.notNull(childWorkNumber);
		WorkBundle parent = workService.findWork(parentId);
		Work child = workService.findWorkByWorkNumber(childWorkNumber);
		addToBundle(parent, child);
	}

	@Override
	public void addToBundle(String parentWorkNumber, Long childId) {
		Assert.notNull(parentWorkNumber);
		Assert.notNull(childId);
		WorkBundle parent = workService.findWorkByWorkNumber(parentWorkNumber);
		Work child = workService.findWork(childId);
		addToBundle(parent, child);
	}

	@Override
	public List<Address> getBundleAddresses(Long parentId) {
		Assert.notNull(parentId);
		WorkBundle parent = workService.findWork(parentId);

		return getBundleAddresses(parent);
	}

	@Override
	public List<Address> getBundleAddresses(WorkBundle parent) {
		List<Address> addresses = null;
		Set<Work> assignments = parent.getBundle();
		if (isNotEmpty(assignments)) {
			addresses = extract(assignments, on(Work.class).getAddress());
		}
		return addresses;
	}

	@Override
	public boolean unassignBundle(UnassignDTO unassignDTO) {
		Assert.notNull(unassignDTO);
		Long parentId = unassignDTO.getWorkId();
		WorkBundle parent = workService.findWork(parentId);
		Set <Work> bundledWork = parent.getBundle();

		// ensure that all assignments are in a valid state for unassignment
		if(!WorkStatusType.UNASSIGN_STATUS_TYPES.contains(parent.getWorkStatusType().getCode())) {
			return false;
		}

		for (Work work : bundledWork) {
			if(!WorkStatusType.UNASSIGN_STATUS_TYPES.contains(work.getWorkStatusType().getCode()) &&
				!WorkStatusType.SENT.equals(work.getWorkStatusType().getCode())) {
				return false;
			}
		}

		workService.unassignWork(unassignDTO);
		for (Work work : bundledWork) {
			if(!WorkStatusType.SENT.equals(work.getWorkStatusType().getCode())) {
				unassignDTO.setWorkId(work.getId());
				workService.unassignWork(unassignDTO);
			}
		}
		return true;
	}

	private void setBundleAddress(WorkBundle parent) {
		Assert.notNull(parent);

		List<BigDecimal[]> locations = Lists.newArrayList();
		for (Work work : parent.getBundle()) {
			if (work.isSetOnsiteAddress() && work.getAddress() != null && work.getAddress().getLatitude() != null && work.getAddress().getLongitude() != null) {
				BigDecimal[] location = new BigDecimal[2];
				location[GeoMathHelper.LAT] = work.getAddress().getLatitude();
				location[GeoMathHelper.LON] = work.getAddress().getLongitude();
				locations.add(location);
			}
		}

		// no locations? then bundle address is virtual
		if (locations.size() == 0) {
			parent.setAddress(null);
			parent.setLocation(null);
			parent.setIsOnsiteAddress(false);
			workBundleDAO.saveOrUpdate(parent);
			return;
		}
		double[] midpoint = GeoMathHelper.findGeoMidpoint(locations.toArray(new BigDecimal[locations.size()][2]));
		Address address = null;
		try {
			final AddressDTO addressDTO = locationService.reverseLookup(midpoint[GeoMathHelper.LAT], midpoint[GeoMathHelper.LON]);
			addressDTO.setAddressTypeCode(AddressType.ASSIGNMENT);
			addressDTO.setDressCodeId(DressCode.BUSINESS_CASUAL);
			addressDTO.setLocationTypeId(LocationType.COMMERCIAL_CODE);

			if (addressDTO.getPostalCode() != null) {
				address = addressService.saveOrUpdate(addressDTO);
			} else {
				logger.warn(String.format("No valid mailing address for: (%f, %f)", midpoint[GeoMathHelper.LAT], midpoint[GeoMathHelper.LON]));
			}
		} catch (final Exception ex) {
			logger.error("Unable to get address: ", ex);
		}
		final ClientLocation location = new ClientLocation();

		// If no address from midpoint found, set address to first assignment address
		boolean badAddress = (
			address == null ||
			(address.getAddress1() == null || address.getCity() == null || address.getState() == null || address.getPostalCode() == null)
		);
		if (parent.getBundle().iterator().hasNext() && badAddress) {
			address = parent.getBundle().iterator().next().getAddress();
		}

		if (address != null && address.getAddress1() != null && address.getCity() != null && address.getState() != null && address.getPostalCode() != null) {
			location.setName(parent.getTitle());
			location.setAddress(address);
			location.setCompany(parent.getCompany());
			locationDAO.saveOrUpdate(location);
			parent.setAddress(address);
			parent.setLocation(location);
			parent.setIsOnsiteAddress(true);
		}
	}

	private ValidateWorkResponse validateBundledWorkForAdd(String workNumber, Long parentId) {
		Work work = workService.findWorkByWorkNumber(workNumber);

		return validateBundledWorkForAdd(work, parentId);
	}

	private ValidateWorkResponse validateBundledWorkForAdd(Work work, @Nullable Long parentId) {
		Assert.notNull(work);

		ValidateWorkResponse validateWorkResponse = new ValidateWorkResponse(ValidateWorkStatus.SUCCESS);
		BindingResult bindingResult = ThriftValidationMessageHelper.newBindingResult();

		if (!work.isDraft()) {
			ThriftValidationMessageHelper.rejectViolation(
				newBundleConstraintViolation("state", MessageKeys.Work.INVALID_BUNDLE_WORK_STATE, work.getWorkStatusType().toString()),
				bindingResult
			);
		}
		if (work.isInBundle() && (parentId == null || work.getParent().getId().longValue() != parentId.longValue())) {
			ThriftValidationMessageHelper.rejectViolation(
				newBundleConstraintViolation("state", MessageKeys.Work.INVALID_BUNDLE_WORK_INBUNDLE, work.getParent().getTitle()),
				bindingResult
			);
		}
		List<String> errors = messageHelper.getAllErrors(bindingResult);
		if (CollectionUtils.isNotEmpty(errors)) {
			validateWorkResponse.setStatus(ValidateWorkStatus.FAILURE);
			validateWorkResponse.addMessage(work.getTitle() + ": " + org.apache.commons.lang.StringUtils.join(errors, ", "));
		}
		return validateWorkResponse;
	}

	private ValidateWorkResponse validateBundledWorkForSend(String workNumber, Long userId) {
		Work work = workService.findWorkByWorkNumber(workNumber);

		return validateBundledWorkForSend(work, userId);
	}

	private ValidateWorkResponse validateBundledWorkForSend(Work work, Long userId) {
		Assert.notNull(work);

		WorkRequest request = new WorkRequest()
				.setUserId(userId)
				.setWorkId(work.getId())
				.setIncludes(Sets.newHashSet(
						WorkRequestInfo.CONTEXT_INFO,
						WorkRequestInfo.STATUS_INFO,
						WorkRequestInfo.INDUSTRY_INFO,
						WorkRequestInfo.PROJECT_INFO,
						WorkRequestInfo.BUYER_INFO,
						WorkRequestInfo.LOCATION_INFO,
						WorkRequestInfo.SCHEDULE_INFO,
						WorkRequestInfo.PRICING_INFO,
						WorkRequestInfo.CUSTOM_FIELDS_VALUES_AND_DATA_INFO
				));
		WorkResponse response;
		ValidateWorkResponse validateWorkResponse = new ValidateWorkResponse(ValidateWorkStatus.FAILURE);
		try {
			response = tWorkFacadeService.findWork(request);
			WorkSaveRequest saveRequest = new WorkSaveRequest()
					.setUserId(userId)
					.setWork(response.getWork());
			workSaveRequestValidator.validateWork(saveRequest);
			validateWorkResponse.setStatus(ValidateWorkStatus.SUCCESS);
		} catch (com.workmarket.thrift.core.ValidationException e) {
			BindingResult bindingResult = ThriftValidationMessageHelper.buildBindingResult(e);
			List<String> errors = messageHelper.getAllErrors(bindingResult);
			validateWorkResponse.addMessage(work.getTitle() + ": " + org.apache.commons.lang.StringUtils.join(errors, ", "));
		} catch (WorkActionException e) {
			validateWorkResponse.addMessage(work.getTitle());
		}
		return validateWorkResponse;
	}

	@Override
	public List<ValidateWorkResponse> validateAllBundledWorkForSend(List<String> workNumbers, Long userId) {
		Assert.notNull(workNumbers);
		Assert.notNull(userId);

		List<ValidateWorkResponse> response = Lists.newArrayList();
		for (String workNumber : workNumbers) {
			response.add(validateBundledWorkForSend(workNumber, userId));
		}
		return response;
	}

	@Override
	public List<ValidateWorkResponse> validateAllBundledWorkForAdd(List<String> workNumbers, Long parentId) {
		Assert.notNull(workNumbers);

		List<ValidateWorkResponse> response = Lists.newArrayList();
		for (String workNumber : workNumbers) {
			response.add(validateBundledWorkForAdd(workNumber, parentId));
		}
		return response;
	}

	@Override
	public List<ValidateWorkResponse> addAllToBundleByWork(WorkBundle parent, List<Work> workList) {
		Assert.notNull(parent);
		Assert.notNull(workList);
		List<ValidateWorkResponse> errorResponses = new ArrayList<>();
		for (Work work : workList) {
			ValidateWorkResponse validateWorkResponse = validateBundledWorkForAdd(work, parent.getId());

			if (validateWorkResponse.isSuccessful()) {
				addToBundle(parent, work);
			} else {
				errorResponses.add(validateWorkResponse);
			}
		}
		updateBundleCalculatedValues(parent);
		return errorResponses;
	}

	@Override
	public List<ValidateWorkResponse> addAllToBundleByWorkNumbers(WorkBundle parent, List<String> workNumbers) {
		Assert.notNull(parent);
		Assert.notNull(workNumbers);
		List<Work> workList = workBundleDAO.findAllInByWorkNumbers(workNumbers);
		return addAllToBundleByWork(parent, workList);
	}

	@Override
	public List<ValidateWorkResponse> addAllToBundleByWorkNumbers(String parentWorkNumber, List<String> workNumbers) {
		Assert.notNull(parentWorkNumber);

		WorkBundle parent = workService.findWorkByWorkNumber(parentWorkNumber);
		return addAllToBundleByWorkNumbers(parent, workNumbers);
	}

	@Override
	public List<ValidateWorkResponse> addAllToBundleByWorkNumbers(Long parentId, List<String> workNumbers) {
		Assert.notNull(parentId);

		WorkBundle parent = workService.findWork(parentId);
		return addAllToBundleByWorkNumbers(parent, workNumbers);
	}

	@Override
	public List<ValidateWorkResponse> addAllToBundleByIds(WorkBundle parent, List<Long> ids) {
		Assert.notNull(parent);
		Assert.notNull(ids);

		List<Work> workList = workBundleDAO.findAllInByIds(ids);
		return addAllToBundleByWork(parent, workList);
	}

	@Override
	public List<ValidateWorkResponse> addAllToBundleByIds(String parentWorkNumber, List<Long> ids) {
		Assert.notNull(parentWorkNumber);

		WorkBundle parent = workService.findWorkByWorkNumber(parentWorkNumber);
		return addAllToBundleByIds(parent, ids);
	}

	@Override
	public List<ValidateWorkResponse> addAllToBundleByIds(Long parentId, List<Long> ids) {
		Assert.notNull(parentId);

		WorkBundle parent = workService.findWork(parentId);
		return addAllToBundleByIds(parent, ids);
	}

	@Override
	public void removeFromBundle(Long childId) {
		Assert.notNull(childId);
		Work child = workService.findWork(childId);
		if (child.isInBundle()) {
			WorkBundle parent = child.getParent();
			removeFromBundle(parent, child);
		}
	}

	@Override
	public void removeFromBundle(Long parentId, Long childId) {
		Assert.notNull(parentId);
		Assert.notNull(childId);
		WorkBundle parent = workService.findWork(parentId);
		Work child = workService.findWork(childId);
		removeFromBundle(parent, child);
	}

	@Override
	public void removeFromBundle(String parentWorkNumber, String childWorkNumber) {
		Assert.notNull(parentWorkNumber);
		Assert.notNull(childWorkNumber);
		WorkBundle parent = workService.findWorkByWorkNumber(parentWorkNumber);
		Work child = workService.findWorkByWorkNumber(childWorkNumber);
		removeFromBundle(parent, child);
	}

	@Override
	public void removeFromBundle(Long parentId, String childWorkNumber) {
		Assert.notNull(parentId);
		Assert.notNull(childWorkNumber);
		WorkBundle parent = workService.findWork(parentId);
		Work child = workService.findWorkByWorkNumber(childWorkNumber);
		removeFromBundle(parent, child);
	}

	@Override
	public void removeFromBundle(String parentWorkNumber, Long childId) {
		Assert.notNull(parentWorkNumber);
		Assert.notNull(childId);
		WorkBundle parent = workService.findWorkByWorkNumber(parentWorkNumber);
		Work child = workService.findWork(childId);
		removeFromBundle(parent, child);
	}

	@Override
	public void removeFromBundle(WorkBundle parent, Work child) {
		Assert.notNull(parent);
		Assert.notNull(child);
		child.setParent(null);
		if (workBundleRouting.isWorkBundlePendingRouting(parent.getId())) {
			return;
		}
		if (parent.getBundle() != null) {
			parent.getBundle().remove(child);
		}
		updateBundleCalculatedValues(parent);
	}

	private Map<String, BigDecimal> addToBudget(boolean isBuyer, Work work, BigDecimal budget) {
		BigDecimal cost = null;
		WorkCostDTO workCostDTO = accountRegisterAuthorizationService.calculateCostOnSentWork(work);
		if (workCostDTO != null) {
			cost = isBuyer ? workCostDTO.getTotalBuyerCost() : workCostDTO.getTotalResourceCost();
		}

		return ImmutableMap.of(WORK_COST_KEY, cost, OVERALL_BUDGET_KEY, budget.add(cost));
	}

	@Override
	public WorkAuthorizationResponse verifyBundleFunds(Long userId, Long parentId) {
		Assert.notNull(userId);
		Assert.notNull(parentId);

		User user = userService.findUserById(userId);
		WorkBundle bundle = findById(parentId);

		BigDecimal bundleTotalCost = getBundleBudget(user, bundle);
		try {
			return accountRegisterAuthorizationService.verifyFundsForAuthorization(user, bundle, bundleTotalCost);
		} catch (Exception e) {
			logger.error("Error verifying bundle funds " + bundle.getId(), e);
			return WorkAuthorizationResponse.UNKNOWN;
		}
	}

	@Override
	public BigDecimal getBundleBudget(User user, WorkBundle parent) {
		Assert.notNull(user);
		Assert.notNull(parent);
		BigDecimal budget = BigDecimal.ZERO;
		List<WorkContext> workContexts = workService.getWorkContext(parent, user);
		boolean isBuyer = workContexts.contains(WorkContext.OWNER) ||
				(workContexts.contains(WorkContext.COMPANY_OWNED) &&
						authenticationService.authorizeUserByAclPermission(user.getId(), Permission.VIEW_AND_MANAGE_MY_ASSIGNMENTS));

		for (Work work : parent.getBundle()) {
			budget = addToBudget(isBuyer, work, budget).get(OVERALL_BUDGET_KEY);
		}
		return budget;
	}

	@Override
	public ServiceResponseBuilder getBundleData(Long userId, Long parentId) {
		Assert.notNull(userId);
		Assert.notNull(parentId);
		User user = userService.findUserById(userId);
		WorkBundle parent = workService.findWork(parentId);
		return getBundleData(user, parent);
	}

	@Override
	public ServiceResponseBuilder getBundleData(User user, WorkBundle parent) {
		Assert.notNull(parent);
		ServiceResponseBuilder response = new ServiceResponseBuilder();

		List<WorkContext> workContexts = workService.getWorkContext(parent, user);
		if (
				!workContexts.contains(WorkContext.OWNER) &&
						!(
								workContexts.contains(WorkContext.COMPANY_OWNED) &&
										authenticationService.authorizeUserByAclPermission(user.getId(), Permission.VIEW_AND_MANAGE_MY_ASSIGNMENTS)
						) &&
						!workContexts.contains(WorkContext.INVITED) &&
						!workContexts.contains(WorkContext.DISPATCHER) &&
						!workContexts.contains(WorkContext.ACTIVE_RESOURCE) &&
						!userRoleService.isInternalUser(user)
				) {
			response.setSuccessful(false);
			response.addMessage(messageHelper.getMessage("assignment_bundle.get_json.fail"));
			return response;
		}

		Map<String, Object> overview = new HashMap<>();
		overview.put("assignments", parent.getBundle().size());
		overview.put("owner", parent.getBuyer().getFullName());
		overview.put("workNumber", parent.getWorkNumber());

		BigDecimal budget = BigDecimal.ZERO;
		Calendar from = null;
		Calendar to = null;
		String timeZoneId = parent.getTimeZone().getTimeZoneId();

		List<Map<String, String>> assignments = new ArrayList<>();
		for (Work work : parent.getBundle()) {
			Calendar curFrom = work.getScheduleFrom();
			Calendar curTo = (work.getScheduleThrough() != null) ? work.getScheduleThrough() : work.getScheduleFrom();

			Calendar[] range = DateUtilities.getWidestDateRange(curFrom, curTo, from, to);

			from = range[DateUtilities.FROM];
			to = range[DateUtilities.TO];

			Map<String, String> assignment = new HashMap<>();

			assignment.put("title", work.getTitle());

			Address address = (work.getLocation() != null) ? work.getLocation().getAddress() : new Address();
			String addrString = (address != null && StringUtils.hasText(address.getCity())) ?
					String.format("%s, %s, %s", address.getCity(), address.getState(), address.getPostalCode()) :
					"off-site";

			assignment.put("location", addrString);
			assignment.put("due", (work.getScheduleFrom().getTimeInMillis() > 0) ? DateUtilities.format("MM/dd/yyyy", work.getScheduleFrom(), timeZoneId) : "Not Set");

			boolean isBuyer = workContexts.contains(WorkContext.OWNER) ||
					(workContexts.contains(WorkContext.COMPANY_OWNED) &&
							authenticationService.authorizeUserByAclPermission(user.getId(), Permission.VIEW_AND_MANAGE_MY_ASSIGNMENTS));
			Map<String, BigDecimal> budgetData = addToBudget(isBuyer, work, budget);
			budget = budgetData.get(OVERALL_BUDGET_KEY);

			assignment.put("budget", usdCostFormat.format(budgetData.get(WORK_COST_KEY).doubleValue()));
			assignment.put("status", work.getWorkStatusType().toString());
			assignment.put("workNumber", work.getWorkNumber());

			assignments.add(assignment);
		}

		overview.put("budget", usdCostFormat.format(budget.doubleValue()));

		Map<String, String> dates = new HashMap<>();
		dates.put("from", (from != null && from.getTimeInMillis() > 0) ? DateUtilities.format("MM/dd/yyyy", from, timeZoneId) : "Not Set");
		dates.put("to", (to != null && to.getTimeInMillis() > 0) ? DateUtilities.format("MM/dd/yyyy", to, timeZoneId) : "Not Set");

		overview.put("dates", dates);

		ImmutableMap<String, Object> retMap = ImmutableMap.of("overview", overview, "assignments", assignments);
		response.setData(retMap);
		response.setSuccessful(true);
		response.addMessage(messageHelper.getMessage("assignment_bundle.get_json.success"));

		return response;
	}

	@Override
	public DataTablesResponse<List<String>, Map<String, Object>> getDataTablesResponse(ExtendedUserDetails user, Long id, HttpServletRequest httpRequest) {
		DataTablesRequest request = DataTablesRequest.newInstance(httpRequest);
		request.setSortableColumnMapping(ImmutableMap.of(
			TITLE_COLUMN, WorkSearchDataPagination.SORTS.TITLE.toString(),
			DATE_COLUMN, WorkSearchDataPagination.SORTS.SCHEDULE_FROM.toString()
		));

		WorkSearchDataPagination pagination = new WorkSearchDataPagination();
		pagination.setStartRow(request.getStart());
		pagination.setResultsLimit(request.getLimit());
		pagination.setSortColumn(request.getSortColumn());
		pagination.setSortDirection(request.getSortColumnDirection());
		pagination.addFilter(WorkSearchDataPagination.FILTER_KEYS.PARENT_ID, id);
		pagination.setShowAllCompanyAssignments(true);

		pagination = workReportService.generateWorkDashboardReportBuyer(user.getCompanyId(), user.getId(), pagination);

		List<SolrWorkData> results = pagination.getResults();
		DataTablesResponse<List<String>, Map<String, Object>> response = DataTablesResponse.newInstance(request, pagination);

		Calendar from = null;
		Calendar to = null;
		Double totalMoney = 0D;

		for (SolrWorkData item : results) {
			String address = (StringUtils.hasText(item.getCity())) ? String.format("%s, %s, %s", item.getCity(), item.getState(), item.getPostalCode()) : "off-site";

			Calendar curFrom = item.getScheduleFrom();
			Calendar curTo = (item.getScheduleThrough() != null) ? item.getScheduleThrough() : curFrom;

			from = (from != null && curFrom.compareTo(from) > 0) ? from : curFrom;
			to = (to != null && curTo.compareTo(to) < 0) ? to : curTo;
			totalMoney += item.getSpendLimit();

			String money = (item.getSpendLimit() > 0) ? StringUtilities.formatMoneyForDisplay(new BigDecimal(item.getSpendLimit())) + " " : "";

			List<String> row = Lists.newArrayList(
				item.getTitle(),
				address,
				DateUtilities.format("MMM d, yyyy", item.getScheduleFrom(), user.getTimeZoneId()),
				"" + money + item.getPricingType().toLowerCase(),
				item.getWorkStatusTypeCode()
			);

			Map<String, Object> meta = CollectionUtilities.newObjectMap(
				"id", item.getWorkNumber()
			);

			response.addRow(row, meta);
		}

		Map<String, Object> meta = new HashMap<>();
		meta.put("fromDate", DateUtilities.format("MMM d, yyyy", from, user.getTimeZoneId()));
		meta.put("toDate", DateUtilities.format("MMM d, yyyy", to, user.getTimeZoneId()));
		meta.put("totalBudget", StringUtilities.formatMoneyForDisplay(new BigDecimal(totalMoney)));

		response.setResponseMeta(meta);

		return response;
	}

	@Override
	public Set<Long> getAllWorkIdsInBundle(WorkBundle parent) {
		Assert.notNull(parent);
		return parent.getBundleIds();
	}

	@Override
	public Set<Long> getAllWorkIdsInBundle(String parentWorkNumber) {
		Assert.notNull(parentWorkNumber);

		WorkBundle parent = workService.findWorkByWorkNumber(parentWorkNumber);
		if (parent != null) {
			Hibernate.initialize(parent.getBundle());
		}
		return getAllWorkIdsInBundle(parent);
	}

	@Override
	public Set<Long> getAllWorkIdsInBundle(Long parentId) {
		Assert.notNull(parentId);
		WorkBundle parent = workService.findWork(parentId);
		if (parent != null) {
			Hibernate.initialize(parent.getBundle());
		}
		return getAllWorkIdsInBundle(parent);
	}

	@Override
	public Set<Work> getAllWorkInBundle(WorkBundle parent) {
		Assert.notNull(parent);
		return parent.getBundle();
	}

	@Override
	public Set<Work> getAllWorkInBundle(String parentWorkNumber) {
		Assert.notNull(parentWorkNumber);

		WorkBundle parent = workService.findWorkByWorkNumber(parentWorkNumber);
		if (parent != null) {
			Hibernate.initialize(parent.getBundle());
		}
		return getAllWorkInBundle(parent);
	}

	@Override
	public Set<Work> getAllWorkInBundle(Long parentId) {
		Assert.notNull(parentId);

		WorkBundle parent = workService.findWork(parentId);
		if (parent != null) {
			Hibernate.initialize(parent.getBundle());
		}
		return getAllWorkInBundle(parent);
	}

	@Override
	public ServiceResponseBuilder getWorkWithLocations(Long parentId) {
		ServiceResponseBuilder response = new ServiceResponseBuilder();

		List<Work> workWithLocations = filter(new Predicate<Work>() {
			@Override public boolean apply(Work work) {
				return (work.getAddress() != null && work.getAddress().getLatitude() != null && work.getAddress().getLongitude() != null);
			}
		}, getAllWorkInBundle(parentId));

		List<Map<String, String>> data = Lists.newArrayList();
		for (Work work : workWithLocations) {
			data.add(ImmutableMap.of(
					"title", work.getTitle(),
					"latitude", "" + work.getAddress().getLatitude(),
					"longitude", "" + work.getAddress().getLongitude(),
					"address", work.getAddress().getFullAddress()
			));
		}

		response.addData("mapData", data);
		return response;
	}

	@Override
	public WorkBundle saveOrUpdateWorkBundle(Long userId, WorkBundleDTO workBundleDTO) {
		WorkBundle workBundle;

		boolean initialize = false;

		if (workBundleDTO.getId() != null) {
			workBundle = workBundleDAO.get(workBundleDTO.getId());
			workBundleDTO.setTitle(workBundle.getTitle());
			workBundleDTO.setDescription(workBundle.getDescription());
		} else {
			initialize = true;
			workBundle = new WorkBundle();
		}

		List<Work> workList = (isNotEmpty(workBundleDTO.getWorkNumbers())) ?
				workBundleDAO.findAllInByWorkNumbers(workBundleDTO.getWorkNumbers()) :
				new ArrayList<Work>();

		//date range
		setBundleDateRange(workBundleDTO, workList);

		//industry
		Long industryId = isNotEmpty(workList) ?
				workList.get(0).getIndustry().getId() :
				INDUSTRY_TECH;
		workBundleDTO.setIndustryId(industryId);

		workBundle = workService.buildWork(userId, workBundleDTO, workBundle, initialize);

		setBundleMMW(workBundle);

		workBundleDAO.saveOrUpdate(workBundle);

		return workBundle;
	}

	@Override
	public void acceptAllWorkInBundle(Long workId, Long userId) {
		eventRouter.sendEvent(new WorkBundleAcceptEvent(userId, workId));
	}

	@Override
	public void declineAllWorkInBundle(String workNumber, Long negotiationId, String note) {
		// get workIds before getting the negotiation, which establishes work as Work instead of WorkBundle
		Set<Long> workIds = getAllWorkIdsInBundle(workNumber);
		AbstractWorkNegotiation parentNegotiation = workNegotiationService.findById(negotiationId);
		Long userId = parentNegotiation.getRequestedBy().getId();
		for (Long workId : workIds) {
			WorkNegotiation negotiation = workNegotiationService.findLatestByUserForWork(userId, workId);
			if (negotiation == null) {
				continue;
			}
			try {
				workNegotiationService.declineNegotiation(negotiation.getId(), note, null);
			}
			catch (Exception e) {
				logger.error("declineAllWorkInBundle: Error declining negotiation " + negotiation.getId(), e);
			}
		}
	}

	@Override
	public void processWorkBundleForm(WorkForm aForm) {
		if (!(aForm instanceof WorkBundleForm) || aForm.getId() == null) {
			return;
		}
		WorkBundleForm form = (WorkBundleForm) aForm;
		WorkBundle work = findById(form.getId());
		if (work == null) {
			return;
		}

		form.setTitle(work.getTitle());
		form.setDescription(work.getDescription());
		form.setIndustry(work.getIndustry().getId());

		form.setScheduling(work.getIsScheduleRange());
		if (work.getIsScheduleRange()) {
			form.setVariable_from(work.getScheduleFrom().getTime());
			form.setVariable_fromtime(work.getScheduleFrom().getTime());
			form.setTo(work.getScheduleThrough().getTime());
			form.setTotime(work.getScheduleThrough().getTime());
		} else {
			form.setFrom(work.getScheduleFrom().getTime());
			form.setFromtime(work.getScheduleFrom().getTime());
		}

		form.setInternal_owner(work.getBuyer().getId());

		if (work.getIsOnsiteAddress() && work.getLocation() != null) {
			Location location = work.getLocation();
			form.setClientlocation_id(location.getId());
			form.setClientlocations(WorkForm.CLIENT_LOCATION_CLIENT_COMPANY);
		}

		if (work.getProject() != null) {
			form.setProject(work.getProject().getId());
		}
		if (work.getClientCompany() != null) {
			form.setClientcompany(work.getClientCompany().getId());
		}

		if (work.getManageMyWorkMarket().getPaymentTermsEnabled()) {
			form.setPayment_terms_days(work.getPaymentTermsDays());
		}
		form.setShow_in_feed(false);
	}

	@Override
	public void updateBundleCalculatedValues(Long parentId) {
		Assert.notNull(parentId);
		WorkBundle parent = workService.findWork(parentId);
		updateBundleCalculatedValues(parent);
	}

	@Override
	public void updateBundleCalculatedValues(WorkBundle parent) {
		Assert.notNull(parent);

		setBundleDateRange(parent);

		// set industry
		if (parent.getBundle().iterator().hasNext()) {
			parent.setIndustry(parent.getBundle().iterator().next().getIndustry());
		}

		setBundleMMW(parent);

		setBundleAddress(parent);

		setBundleProject(parent);

		workBundleDAO.saveOrUpdate(parent);
	}

	// called from the controller, so current user is relevant
	@Override
	public void applySubmitBundle(Long workId, User worker) {
		WorkBundle bundle = findById(workId);
		Assert.notNull(bundle);

		if (bundle.getBundle() == null) {
			return;
		}

		for (Work work : bundle.getBundle()) {
			WorkBundleApplySubmitEvent event = eventFactory.buildWorkBundleApplySubmitEvent(work.getId());
			event.setUser(worker);
			eventRouter.sendEvent(event);
		}
	}

	// called from EventRouter, so need the user from the event
	@Override
	public void applySubmitBundleHandler(WorkBundleApplySubmitEvent event) {
		Assert.notNull(event);
		Assert.notNull(event.getWorkId());
		Assert.notNull(event.getUser());

		Set<WorkRequestInfo> includes = ImmutableSet.of(
				WorkRequestInfo.CONTEXT_INFO,
				WorkRequestInfo.PRICING_INFO,
				WorkRequestInfo.SCHEDULE_INFO,
				WorkRequestInfo.MANAGE_MY_WORK_MARKET_INFO
		);

		WorkRequest workRequest = new WorkRequest()
				.setUserId(event.getUser().getId())
				.setWorkId(event.getWorkId())
				.setIncludes(includes);
		WorkResponse workResponse;

		try {
			workResponse = tWorkFacadeService.findWork(workRequest);
		} catch (Exception e) {
			logger.error("Error finding work: ", e);
			return;
		}

		ExtendedUserDetails userDetails = (ExtendedUserDetails) extendedUserDetailsService.loadUserByEmail(event.getUser().getEmail(), null);

		if (!userDetails.isInternal() &&
			(workResponse.getAuthorizationContexts().isEmpty() || workResponse.getRequestContexts().contains(RequestContext.UNRELATED)) &&
			!vendorService.isVendorInvitedToWork(event.getUser().getCompany().getId(), workResponse.getWork().getId())) {
			logger.error("Unauthorized negotiation for work id: " + event.getWorkId() + ", user id: " + event.getUser().getId());
			return;
		}

		try {
			workNegotiationService.createApplyNegotiation(workResponse.getWork().getId(), userDetails.getId(), new WorkNegotiationDTO());
			workSearchService.reindexWorkAsynchronous(workResponse.getWork().getId());
			workResourceDetailCache.evict(workResponse.getWork().getId());
		} catch (Exception e) {
			logger.error("Apply negotiation error", e);
		}
	}

	// called from EventRouter, so need the user from the event
	@Override
	public void cancelSubmitBundleHandler(WorkBundleCancelSubmitEvent event) {
		Assert.notNull(event);
		Assert.notNull(event.getWorkId());
		Assert.notNull(event.getUser());
		Set<Long> workIds = getAllWorkIdsInBundle(event.getWorkId());

		authenticationService.setCurrentUser(event.getUser());
		for (Long workId : workIds) {
			workNegotiationService.cancelAllNegotiationsByCompanyForWork(event.getUser().getCompany().getId(), workId);
			workResourceDetailCache.evict(workId);
		}
		workSearchService.reindexWorkAsynchronous(workIds);
	}

	@Override
	public boolean isBundleComplete(Long parentId) {
		Assert.notNull(parentId);
		WorkBundle parent = findById(parentId);
		Assert.notNull(parent);

		return (WorkStatusType.COMPLETE.equals(parent.getWorkStatusType().getCode()));
	}

	@Override
	public boolean isBundleVoid(Long parentId) {
		Assert.notNull(parentId);
		WorkBundle parent = findById(parentId);
		Assert.notNull(parent);

		return (WorkStatusType.VOID.equals(parent.getWorkStatusType().getCode()));
	}

	@Override
	public boolean updateBundleComplete(Long parentId) {
		Assert.notNull(parentId);
		WorkBundle parent = findById(parentId);
		Assert.notNull(parent);

		for (Work w : parent.getBundle()) {
			switch(w.getWorkStatusType().getCode()) {
				case WorkStatusType.CANCELLED:
				case WorkStatusType.CANCELLED_WITH_PAY:
				case WorkStatusType.DELETED:
				case WorkStatusType.PAID:
				case WorkStatusType.VOID: { break; }
				default: { return false; }
			}
		}

		CompleteWorkDTO dto = new CompleteWorkDTO();
		dto.setResolution("Bundle Complete.");
		List<ConstraintViolation> violations = workService.completeWork(parentId, dto);
		if (CollectionUtils.isNotEmpty(violations)) {
			for (ConstraintViolation violation : violations) { logger.info("Can't complete bundle[" + parentId + "]: " + violation); }
			return false;
		}

		return true;
	}

	@Override
	public boolean updateBundleVoid(Long parentId) {
		Assert.notNull(parentId);
		WorkBundle parent = findById(parentId);
		Assert.notNull(parent);

		if (CollectionUtils.isNotEmpty(parent.getBundle())) { return false; }

		if (WorkStatusType.VOID.equals(parent.getWorkStatusType().getCode())) { return true; }

		if (
			!WorkStatusType.DRAFT.equals(parent.getWorkStatusType().getCode()) &&
			!WorkStatusType.SENT.equals(parent.getWorkStatusType().getCode())
		) { return false; }

		List<ConstraintViolation> violations = workService.voidWork(parentId, "Empty Bundle");
		if (CollectionUtils.isNotEmpty(violations)) {
			for (ConstraintViolation violation : violations) { logger.info("Can't void bundle[" + parentId + "]: " + violation); }
			return false;
		}

		return true;
	}

	@Override
	public boolean authorizeBundleView(long bundleId, ExtendedUserDetails user) {
		List<WorkContext> workContexts = workService.getWorkContext(bundleId, user.getId());

		return workContexts.contains(WorkContext.OWNER) ||
				(workContexts.contains(WorkContext.COMPANY_OWNED) &&
				 authenticationService.authorizeUserByAclPermission(user.getId(), Permission.VIEW_AND_MANAGE_MY_ASSIGNMENTS)) ||
				workContexts.contains(WorkContext.INVITED) ||
				workContexts.contains(WorkContext.DISPATCHER) ||
				workContexts.contains(WorkContext.ACTIVE_RESOURCE) ||
				user.hasAnyRoles("ROLE_INTERNAL");
	}

	@Override
	public boolean authorizeBundlePendingRouting(long bundleId, long userId) {
		List<WorkContext> workContexts = workService.getWorkContext(bundleId, userId);
		return workContexts.contains(WorkContext.OWNER) ||
				(workContexts.contains(WorkContext.COMPANY_OWNED) &&
				 authenticationService.authorizeUserByAclPermission(userId, Permission.VIEW_AND_MANAGE_MY_ASSIGNMENTS)) ||
				!workBundleRouting.isWorkBundlePendingRouting(bundleId);
	}

	private void setBundleProject(WorkBundle parent) {
		if (parent.getBundle().iterator().hasNext()) {
			Work w = parent.getBundle().iterator().next();
			parent.setProject(w.getProject());
			parent.setClientCompany(w.getClientCompany());
		}
	}

	private void setBundleMMW(WorkBundle parent) {
		if (parent.getBundle() == null || !parent.getBundle().iterator().hasNext()) { return; }

		Work w = parent.getBundle().iterator().next();
		ManageMyWorkMarket childMMW = w.getManageMyWorkMarket();
		ManageMyWorkMarket parentMMW = parent.getManageMyWorkMarket();

		parentMMW.setPaymentTermsEnabled(childMMW.getPaymentTermsEnabled());
		parentMMW.setPaymentTermsOverride(childMMW.getPaymentTermsOverride());
		parentMMW.setPaymentTermsDays(childMMW.getPaymentTermsDays());
		parentMMW.setAutoPayEnabled(childMMW.getAutoPayEnabled());
	}

	// TODO - micah - some repition here. Refactor next iteration.
	private void setBundleDateRange(WorkBundle parent) {
		Calendar from = null;
		Calendar through = null;

		for (Work work : parent.getBundle()) {
			Calendar curFrom = work.getScheduleFrom();
			Calendar curThrough = work.getScheduleFrom();
			if (work.getIsScheduleRange()) {
				curThrough = work.getScheduleThrough();
			}
			if (from == null || curFrom.before(from)) {
				from = curFrom;
			}
			if (through == null || curThrough.after(through)) {
				through = curThrough;
			}
		}
		from = (from == null) ? Calendar.getInstance() : from;
		parent.setScheduleFrom(from);
		if (through == null || from.equals(through)) {
			parent.setIsScheduleRange(false);
		} else {
			parent.setIsScheduleRange(true);
			parent.setScheduleThrough(through);
		}
	}

	private void setBundleDateRange(WorkBundleDTO workBundleDTO, List<Work> workList) {
		Calendar from = null;
		Calendar through = null;

		for (Work work : workList) {
			Calendar curFrom = work.getScheduleFrom();
			Calendar curThrough = work.getScheduleFrom();
			if (work.getIsScheduleRange()) {
				curThrough = work.getScheduleThrough();
			}
			if (from == null || curFrom.before(from)) {
				from = curFrom;
			}
			if (through == null || curThrough.after(through)) {
				through = curThrough;
			}
		}

		from = (from == null) ? Calendar.getInstance() : from;

		SimpleDateFormat universal = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");
		workBundleDTO.setScheduleFromString(universal.format(from.getTime()));

		if (through == null || from.equals(through)) {
			workBundleDTO.setIsScheduleRange(false);
		} else {
			workBundleDTO.setIsScheduleRange(true);
			workBundleDTO.setScheduleThroughString(universal.format(through.getTime()));
		}
	}

	protected com.workmarket.thrift.core.ConstraintViolation newBundleConstraintViolation(String property, String error, String... params) {
		com.workmarket.thrift.core.ConstraintViolation v =
				new com.workmarket.thrift.core.ConstraintViolation().setProperty(property).setError(error);
		v.addToParams(Lists.newArrayList(params));
		return v;
	}
}
