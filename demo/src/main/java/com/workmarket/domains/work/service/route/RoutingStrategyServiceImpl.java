package com.workmarket.domains.work.service.route;

import com.google.common.collect.Sets;
import com.workmarket.configuration.Constants;
import com.workmarket.service.business.dto.CompanyIdentityDTO;
import com.workmarket.domains.model.DeliveryStatusType;
import com.workmarket.domains.model.account.AccountRegister;
import com.workmarket.domains.work.dao.RoutingStrategyDAO;
import com.workmarket.domains.work.dao.RoutingStrategyGroupDAO;
import com.workmarket.domains.work.dao.WorkDAO;
import com.workmarket.domains.work.model.Work;
import com.workmarket.domains.work.model.route.AbstractRoutingStrategy;
import com.workmarket.domains.work.model.route.AutoRoutingStrategy;
import com.workmarket.domains.work.model.route.GroupRoutingStrategy;
import com.workmarket.domains.work.model.route.LikeGroupVendorRoutingStrategy;
import com.workmarket.domains.work.model.route.LikeGroupsAutoRoutingStrategy;
import com.workmarket.domains.work.model.route.LikeWorkAutoRoutingStrategy;
import com.workmarket.domains.work.model.route.LikeWorkVendorRoutingStrategy;
import com.workmarket.domains.work.model.route.PeopleSearchRoutingStrategy;
import com.workmarket.domains.work.model.route.PolymathAutoRoutingStrategy;
import com.workmarket.domains.work.model.route.PolymathVendorRoutingStrategy;
import com.workmarket.domains.work.model.route.RoutingVisitor;
import com.workmarket.domains.work.model.route.UserRoutingStrategy;
import com.workmarket.domains.work.model.route.VendorRoutingStrategy;
import com.workmarket.domains.work.model.route.VendorSearchRoutingStrategy;
import com.workmarket.search.request.user.PeopleSearchRequest;
import com.workmarket.service.business.CompanyService;
import com.workmarket.service.business.PricingService;
import com.workmarket.service.business.UserService;
import com.workmarket.service.business.event.EventFactory;
import com.workmarket.service.business.event.work.ExecuteRoutingStrategyGroupEvent;
import com.workmarket.service.business.event.work.RoutingStrategyScheduledEvent;
import com.workmarket.service.business.event.work.WorkUpdateSearchIndexEvent;
import com.workmarket.service.infra.event.EventRouter;
import com.workmarket.utility.DateUtilities;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.List;
import java.util.Set;

import static org.apache.commons.collections.CollectionUtils.isNotEmpty;

@Service
public class RoutingStrategyServiceImpl implements RoutingStrategyService {

	private static final int MAX_SEND_TO_RESOURCES_SYNC = 50;
	private static final Log logger = LogFactory.getLog(RoutingStrategyServiceImpl.class);

	@Autowired private WorkDAO workDAO;
	@Autowired private RoutingStrategyDAO routingStrategyDAO;
	@Autowired private RoutingStrategyGroupDAO routingStrategyGroupDAO;
	@Autowired private EventFactory eventFactory;
	@Autowired private EventRouter eventRouter;
	@Autowired private RoutingVisitor workRoutingVisitor;
	@Autowired private PricingService pricingService;
	@Autowired private CompanyService companyService;
	@Autowired private UserService userService;

	@Override
	public AbstractRoutingStrategy addPeopleSearchRequestRoutingStrategy(long workId, long routingStrategyGroupId, PeopleSearchRequest searchRequest, int delayMinutes, boolean assignToFirstToAccept) {
		Assert.notNull(searchRequest);
		if (isNotEmpty(searchRequest.getGroupFilter())) {
			return addGroupIdsRoutingStrategy(workId, routingStrategyGroupId, Sets.newHashSet(searchRequest.getGroupFilter()), delayMinutes, assignToFirstToAccept);
		}
		return null;
	}

	@Override
	public AbstractRoutingStrategy addPeopleSearchRequestRoutingStrategy(long workId, PeopleSearchRequest searchRequest, int delayMinutes, boolean assignToFirstToAccept) {
		Assert.notNull(searchRequest);
		if (isNotEmpty(searchRequest.getGroupFilter())) {
			return addGroupIdsRoutingStrategy(workId, Sets.newHashSet(searchRequest.getGroupFilter()), delayMinutes, assignToFirstToAccept);
		}
		return null;
	}

	@Override
	public AbstractRoutingStrategy addGroupIdsRoutingStrategy(long workId, Set<Long> groupIds, int delayMinutes, boolean assignToFirstToAccept) {
		Assert.notEmpty(groupIds);
		GroupRoutingStrategy routing = new GroupRoutingStrategy();
		routing.setUserGroups(groupIds);
		routing.setAssignToFirstToAccept(assignToFirstToAccept);
		saveRoutingStrategy(workId, routing, delayMinutes);
		scheduleExecuteRoutingStrategy(workId, routing);
		return routing;
	}

	@Override
	public AbstractRoutingStrategy addGroupIdsRoutingStrategy(long workId, long routingStrategyGroupId, Set<Long> groupIds, int delayMinutes, boolean assignToFirstToAccept) {
		Assert.notEmpty(groupIds);
		GroupRoutingStrategy routing = new GroupRoutingStrategy();
		routing.setUserGroups(groupIds);
		routing.setAssignToFirstToAccept(assignToFirstToAccept);
		routing.setRoutingStrategyGroup(routingStrategyGroupDAO.findById(routingStrategyGroupId));
		saveRoutingStrategy(workId, routing, delayMinutes);
		return routing;
	}

	@Override
	public UserRoutingStrategy addUserIdsRoutingStrategy(long workId, Set<Long> userIds, int delayInMinutes, boolean assignToFirstToAcccept) {
		Assert.notEmpty(userIds);
		Set<String> userNumbers = userService.findAllUserNumbersByUserIds(userIds);
		UserRoutingStrategy routing = new UserRoutingStrategy();
		routing.setUserIds(userIds);
		routing.setUserNumbers(userNumbers);
		routing.setAssignToFirstToAccept(assignToFirstToAcccept);
		saveRoutingStrategy(workId, routing, delayInMinutes);

		if(userIds.size() <= MAX_SEND_TO_RESOURCES_SYNC) {
			executeRoutingStrategy(routing);
		} else {
			scheduleExecuteRoutingStrategy(workId, routing);
		}

		return routing;
	}

	@Override
	public UserRoutingStrategy addUserNumbersRoutingStrategy(long workId, Set<String> userNumbers, int delayInMinutes, boolean assignToFirstToAcccept) {
		Assert.notEmpty(userNumbers);
		UserRoutingStrategy routing = new UserRoutingStrategy();
		routing.setUserNumbers(userNumbers);
		routing.setAssignToFirstToAccept(assignToFirstToAcccept);
		saveRoutingStrategy(workId, routing, delayInMinutes);

		if (userNumbers.size() <= MAX_SEND_TO_RESOURCES_SYNC) {
			executeRoutingStrategy(routing);
		} else {
			scheduleExecuteRoutingStrategy(workId, routing);
		}
		return routing;
	}

	@Override
	public UserRoutingStrategy addUserNumbersRoutingStrategy(long workId, long routingStrategyGroupId, Set<String> userNumbers, int delayInMinutes, boolean assignToFirstToAccept) {
		Assert.notEmpty(userNumbers);
		UserRoutingStrategy routing = new UserRoutingStrategy();
		routing.setUserNumbers(userNumbers);
		routing.setAssignToFirstToAccept(assignToFirstToAccept);
		routing.setRoutingStrategyGroup(routingStrategyGroupDAO.findById(routingStrategyGroupId));
		saveRoutingStrategy(workId, routing, delayInMinutes);

		return routing;
	}

	@Override
	public AutoRoutingStrategy addAutoRoutingStrategy(long workId, boolean assignToFirstToAccept) {
		AutoRoutingStrategy routing = new AutoRoutingStrategy();
		routing.setAssignToFirstToAccept(assignToFirstToAccept);
		saveRoutingStrategy(workId, routing, 0);
		scheduleExecuteRoutingStrategy(workId, routing);
		return routing;
	}

	@Override
	public LikeGroupsAutoRoutingStrategy addLikeGroupsAutoRoutingStrategy(long workId) {
		LikeGroupsAutoRoutingStrategy routing = new LikeGroupsAutoRoutingStrategy();
		routing.setAssignToFirstToAccept(false);
		saveRoutingStrategy(workId, routing, 0);
		scheduleExecuteRoutingStrategy(workId, routing);
		return routing;
	}

	@Override
	public LikeWorkAutoRoutingStrategy addLikeWorkAutoRoutingStrategy(long workId) {
		LikeWorkAutoRoutingStrategy routing = new LikeWorkAutoRoutingStrategy();
		routing.setAssignToFirstToAccept(false);
		saveRoutingStrategy(workId, routing, 0);
		scheduleExecuteRoutingStrategy(workId, routing);
		return routing;
	}

	@Override
	public PolymathAutoRoutingStrategy addPolymathAutoRoutingStrategy(long workId) {
		PolymathAutoRoutingStrategy routing = new PolymathAutoRoutingStrategy();
		routing.setAssignToFirstToAccept(false);
		saveRoutingStrategy(workId, routing, 0);
		scheduleExecuteRoutingStrategy(workId, routing);
		return routing;
	}

	@Override
	public LikeGroupVendorRoutingStrategy addLikeGroupVendorRoutingStrategy(long workId) {
		LikeGroupVendorRoutingStrategy routing = new LikeGroupVendorRoutingStrategy();
		routing.setAssignToFirstToAccept(false);
		saveRoutingStrategy(workId, routing, 0);
		return routing;
	}

	@Override
	public LikeWorkVendorRoutingStrategy addLikeWorkVendorRoutingStrategy(long workId) {
		LikeWorkVendorRoutingStrategy routing = new LikeWorkVendorRoutingStrategy();
		routing.setAssignToFirstToAccept(false);
		saveRoutingStrategy(workId, routing, 0);
		return routing;
	}

	@Override
	public PolymathVendorRoutingStrategy addPolymathVendorRoutingStrategy(long workId) {
		PolymathVendorRoutingStrategy routing = new PolymathVendorRoutingStrategy();
		routing.setAssignToFirstToAccept(false);
		saveRoutingStrategy(workId, routing, 0);
		return routing;
	}

	@Override
	public PeopleSearchRoutingStrategy addPeopleSearchRoutingStrategy(long workId, Set<String> userNumbers, Long dispatcherId, boolean assignToFirstToAccept) {
		PeopleSearchRoutingStrategy routing = new PeopleSearchRoutingStrategy();
		routing.setAssignToFirstToAccept(assignToFirstToAccept);
		routing.setUserNumbers(userNumbers);
		routing.setDispatcherId(dispatcherId);
		saveRoutingStrategy(workId, routing, 0);
		executeRoutingStrategy(routing);
		return routing;
	}

	@Override
	public PeopleSearchRoutingStrategy addPeopleSearchRoutingStrategy(String workNumber, Set<String> userNumbers, Long dispatcherId, boolean assignToFirstToAccept) {
		PeopleSearchRoutingStrategy routing = new PeopleSearchRoutingStrategy();
		routing.setAssignToFirstToAccept(assignToFirstToAccept);
		routing.setUserNumbers(userNumbers);
		routing.setDispatcherId(dispatcherId);
		saveRoutingStrategy(workNumber, routing, 0);
		executeRoutingStrategy(routing);
		return routing;
	}

	@Override
	public VendorSearchRoutingStrategy addVendorSearchRoutingStrategy(long workId, Set<String> companyNumbers, Long dispatcherId, boolean assignToFirstToAccept) {
		VendorSearchRoutingStrategy routing = new VendorSearchRoutingStrategy();
		routing.setAssignToFirstToAccept(assignToFirstToAccept);
		routing.setCompanyNumbers(companyNumbers);
		routing.setDispatcherId(dispatcherId);
		saveRoutingStrategy(workId, routing, 0);
		executeRoutingStrategy(routing);
		return  routing;
	}

	@Override
	public VendorRoutingStrategy addVendorRoutingStrategyByCompanyNumbers(
		final long workId,
		final long routingStrategyGroupId,
		final Set<String> companyNumbers,
		final int delayInMinutes,
		final boolean assignToFirstToAccept) {

		Assert.notEmpty(companyNumbers);
		List<CompanyIdentityDTO> vendorIdentities = companyService.findCompanyIdentitiesByCompanyNumbers(companyNumbers);
		Set<Long> companyIds = Sets.newHashSet();
		for (CompanyIdentityDTO vendor : vendorIdentities) {
			companyIds.add(vendor.getCompanyId());
		}
		VendorRoutingStrategy routing = new VendorRoutingStrategy();
		routing.setAssignToFirstToAccept(assignToFirstToAccept);
		routing.setCompanyNumbers(companyNumbers);
		routing.setCompanyIds(companyIds);
		routing.setRoutingStrategyGroup(routingStrategyGroupDAO.findById(routingStrategyGroupId));
		saveRoutingStrategy(workId, routing, delayInMinutes);
		return routing;
	}

	private <T extends AbstractRoutingStrategy> void saveRoutingStrategy(String workNumber, T routingStrategy, int delayMinutes) {
		Work work = workDAO.findWorkByWorkNumber(workNumber);
		Assert.notNull(work);
		saveRoutingStrategy(work, routingStrategy, delayMinutes);
	}

	private <T extends AbstractRoutingStrategy> void saveRoutingStrategy(long workId, T routingStrategy, int delayMinutes) {
		Work work = workDAO.get(workId);
		Assert.notNull(work);
		saveRoutingStrategy(work, routingStrategy, delayMinutes);
	}

	private <T extends AbstractRoutingStrategy> void saveRoutingStrategy(Work work, T routingStrategy, int delayMinutes) {
		routingStrategy.setWork(work);
		routingStrategy.setDelayMinutes(delayMinutes);
		routingStrategy.setInitializedOn(DateUtilities.getCalendarNow());
		routingStrategy.setDeliveryStatus(new DeliveryStatusType(DeliveryStatusType.SCHEDULED));

		routingStrategyDAO.saveOrUpdate(routingStrategy);
	}

	@Override
	public void scheduleExecuteRoutingStrategy(long workId, AbstractRoutingStrategy routingStrategy) {
		Assert.notNull(routingStrategy);
		RoutingStrategyScheduledEvent event = eventFactory.buildRoutingStrategyScheduledEvent(routingStrategy);
		Work work = workDAO.findWorkById(workId);
		Assert.notNull(work);
		AccountRegister accountRegister = pricingService.findDefaultRegisterForCompany(work.getCompany().getId());
		event.setMessageGroupId(String.format(Constants.ACCOUNT_REGISTER_MESSAGE_GROUP_ID, accountRegister.getId()));
		eventRouter.sendEvent(event);
	}

	@Override
	public void scheduleExecuteRoutingStrategyGroup(long workId, long routingStrategyGroupId, int delayMinutes) {
		ExecuteRoutingStrategyGroupEvent event = eventFactory.buildExecuteRoutingStrategyGroupEvent(routingStrategyGroupId, delayMinutes);
		Work work = workDAO.findWorkById(workId);
		AccountRegister accountRegister = pricingService.findDefaultRegisterForCompany(work.getCompany().getId());
		event.setMessageGroupId(String.format(Constants.ACCOUNT_REGISTER_MESSAGE_GROUP_ID, accountRegister.getId()));
		eventRouter.sendEvent(event);
	}

	@Override
	public void executeRoutingStrategy(long routingStrategyId) {
		AbstractRoutingStrategy strategy = routingStrategyDAO.get(routingStrategyId);
		Assert.notNull(strategy);
		Assert.notNull(strategy.getWork());
		executeRoutingStrategy(strategy);
	}

	private void executeRoutingStrategy(AbstractRoutingStrategy strategy) {
		logger.info(String.format("[routing] Execute routing strategy [%d] for work [%d]", strategy.getId(), strategy.getWork().getId()));

		if (strategy.getDeliveryStatus().isSent()) {
			logger.info(String.format("[routing] Routing strategy [%d] already sent for work [%d]. Aborting.", strategy.getId(), strategy.getWork().getId()));
			return;
		}

		strategy.execute(workRoutingVisitor);
		eventRouter.sendEvent(new WorkUpdateSearchIndexEvent(strategy.getWork().getId()));
	}

	@Override
	public List<Long> findScheduledRoutingStrategies() {
		return routingStrategyDAO.findAllScheduled();
	}

	@Override
	public List<Long> findScheduledRoutingStrategiesWithoutGroup() {
		return routingStrategyDAO.findAllScheduledWithoutGroup();
	}

	@Override
	public List<Long> findScheduledRoutingStrategiesByGroupId(long routingStrategyGroupId) {
		return routingStrategyDAO.findAllScheduledByGroup(routingStrategyGroupId);
	}

	@Override
	public List<UserRoutingStrategy> findUserRoutingStrategiesByGroup(long routingGroupId) {
		return routingStrategyDAO.findUserRoutingStrategiesByGroup(routingGroupId);
	}

	@Override
	public List<GroupRoutingStrategy> findGroupRoutingStrategiesByGroup(long routingGroupId) {
		return routingStrategyDAO.findGroupRoutingStrategiesByGroup(routingGroupId);
	}

	@Override
	public List<VendorRoutingStrategy> findVendorRoutingStrategiesByGroup(long routingGroupId) {
		return routingStrategyDAO.findVendorRoutingStrategiesByGroup(routingGroupId);
	}

	@Override
	public List<AbstractRoutingStrategy> findAllRoutingStrategiesByWork(long workId) {
		return routingStrategyDAO.findAllRoutingStrategiesByWork(workId);
	}

	@Override
	public void saveOrUpdateRoutingStrategy(AbstractRoutingStrategy routingStrategy) {
		routingStrategyDAO.saveOrUpdate(routingStrategy);
	}

	@Override
	public <T extends AbstractRoutingStrategy> T findRoutingStrategy(long id) {
		return (T)routingStrategyDAO.get(id);
	}

}
