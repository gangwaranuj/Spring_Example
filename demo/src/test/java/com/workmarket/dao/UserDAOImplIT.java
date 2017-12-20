package com.workmarket.dao;

import com.google.common.collect.Lists;
import com.workmarket.configuration.Constants;
import com.workmarket.domains.model.ApprovalStatus;
import com.workmarket.domains.model.Pagination.SORT_DIRECTION;
import com.workmarket.domains.model.User;
import com.workmarket.domains.model.user.CompanyUserPagination;
import com.workmarket.service.business.BaseServiceIT;
import com.workmarket.service.business.RegistrationService;
import com.workmarket.service.business.dto.UserIdentityDTO;
import com.workmarket.service.infra.business.AuthenticationService;
import com.workmarket.test.IntegrationTest;
import com.workmarket.utility.DateUtilities;
import org.apache.commons.beanutils.BeanToPropertyValueTransformer;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.ListUtils;
import org.apache.commons.collections.MapUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import java.util.Calendar;
import java.util.List;
import java.util.Map;

import static com.workmarket.testutils.matchers.CommonMatchers.isInAscendingOrder;
import static com.workmarket.testutils.matchers.CommonMatchers.isInDescendingOrder;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

@RunWith(SpringJUnit4ClassRunner.class)
@Category(IntegrationTest.class)
public class UserDAOImplIT extends BaseServiceIT {

	@Autowired protected RegistrationService registrationService;
	@Autowired private AuthenticationService authnService;
	@Autowired UserDAO userDAO;

	private User user, user2;

	@Before
	public void setup() throws Exception {
		user = newRegisteredWorker();
		user2 = newRegisteredWorker();
	}

	@Test
	@Transactional
	public void userHasUuid() {
		assertNotNull(user.getUuid());
	}

	@Test
	@Transactional
	public void findAllCompanyUsers_twoConsecutivePages() throws Exception {
		Calendar to = DateUtilities.getMidnightTodayRelativeToTimezone(
				user.getProfile() != null ? user.getProfile().getTimeZone().getTimeZoneId() : Constants.WM_TIME_ZONE);
		Calendar from = (Calendar) to.clone();
		from.add(Calendar.DAY_OF_YEAR, -1);

		CompanyUserPagination pagination1 = new CompanyUserPagination();

		pagination1.setSortColumn(CompanyUserPagination.SORTS.USER_NUMBER);
		pagination1.setSortDirection(SORT_DIRECTION.ASC);
		pagination1.setResultsLimit(25);
		pagination1.setStartRow(0);

		CompanyUserPagination result1 = userDAO.findAllCompanyUsers(1L, pagination1, from, to);

		CompanyUserPagination pagination2 = (CompanyUserPagination) BeanUtils.cloneBean(pagination1);
		pagination2.setStartRow(25);
		CompanyUserPagination result2 = userDAO.findAllCompanyUsers(1L, pagination2, from, to);

		assertEquals(25, result1.getResults().size());
		assertEquals(25, result2.getResults().size());

		@SuppressWarnings("unchecked")
		List<String> userNumberList1 = (List<String>) CollectionUtils.collect(result1.getResults(),
				new BeanToPropertyValueTransformer("userNumber"));
		@SuppressWarnings("unchecked")
		List<String> userNumberList2 = (List<String>) CollectionUtils.collect(result2.getResults(),
				new BeanToPropertyValueTransformer("userNumber"));

		assertThat(userNumberList1, isInAscendingOrder());
		assertThat(userNumberList2, isInAscendingOrder());
		assertThat((List<String>) ListUtils.union(userNumberList1, userNumberList2), isInAscendingOrder());
	}

	@Test
	@Transactional
	public void findAllCompanyUsers_testSortedByUserRoles() {
		Calendar to = DateUtilities.getMidnightTodayRelativeToTimezone(
				user.getProfile() != null ? user.getProfile().getTimeZone().getTimeZoneId() : Constants.WM_TIME_ZONE);
		Calendar from = (Calendar) to.clone();
		from.add(Calendar.DAY_OF_YEAR, -1);

		CompanyUserPagination pagination = new CompanyUserPagination();

		pagination.setSortColumn(CompanyUserPagination.SORTS.ROLES_STRING);
		pagination.setSortDirection(SORT_DIRECTION.ASC);
		pagination.setResultsLimit(25);
		pagination.setStartRow(0);

		CompanyUserPagination result = userDAO.findAllCompanyUsers(1L, pagination, from, to);

		assertEquals(25, result.getResults().size());

		@SuppressWarnings("unchecked")
		List<String> userNumberList = (List<String>) CollectionUtils.collect(result.getResults(),
				new BeanToPropertyValueTransformer("rolesString"));

		assertThat(userNumberList, isInAscendingOrder());
	}

	@Test
	@Transactional
	public void findAllCompanyUsers_testSortedByLatestActivity() {
		Calendar to = DateUtilities.getMidnightTodayRelativeToTimezone(
				user.getProfile() != null ? user.getProfile().getTimeZone().getTimeZoneId() : Constants.WM_TIME_ZONE);
		Calendar from = (Calendar) to.clone();
		from.add(Calendar.DAY_OF_YEAR, -1);

		CompanyUserPagination pagination = new CompanyUserPagination();

		pagination.setSortColumn(CompanyUserPagination.SORTS.LATEST_ACTIVITY);
		pagination.setSortDirection(SORT_DIRECTION.DESC);
		pagination.setResultsLimit(25);
		pagination.setStartRow(0);

		CompanyUserPagination result = userDAO.findAllCompanyUsers(1L, pagination, from, to);

		assertEquals(25, result.getResults().size());

		@SuppressWarnings("unchecked")
		List<Calendar> activityList = (List<Calendar>) CollectionUtils.collect(result.getResults(),
				new BeanToPropertyValueTransformer("latestActivityOn"));

		assertThat(activityList, isInDescendingOrder());
	}


	@Test
	@Transactional
	public void suggest_NotConfirmed_Lane3Pending_NoResults() throws Exception {
		user.setLane3ApprovalStatus(ApprovalStatus.PENDING);
		assertEquals(Boolean.FALSE, authenticationService.getEmailConfirmed(user));
		assertEquals(ApprovalStatus.PENDING, user.getLane3ApprovalStatus());

		List<User> results =
			userDAO.suggest(user.getFirstName()+ " " + user.getLastName(), user.getCompany().getId(), false, false);

		assertEquals(0, results.size());
	}

	@Test
	@Transactional
	public void suggest_NotConfirmed_Approved_NoResults() throws Exception {
		authnService.approveUser(user.getId());

		assertEquals(Boolean.FALSE, authenticationService.getEmailConfirmed(user));
		assertEquals(ApprovalStatus.APPROVED, user.getLane3ApprovalStatus());

		List<User> results =
				userDAO.suggest(
						user.getFirstName()+ " " + user.getLastName(), user.getCompany().getId(), false, false
				);

		assertEquals(0, results.size());
	}

	@Test
	@Transactional
	public void suggest_Confirmed_Approved_OneResult() throws Exception {
		registrationService.confirmAccount(user.getId());
		authnService.approveUser(user.getId());

		assertEquals(Boolean.TRUE, authenticationService.getEmailConfirmed(user));
		assertEquals(ApprovalStatus.APPROVED, user.getLane3ApprovalStatus());

		List<User> results =
			userDAO.suggest(
					user.getFirstName()+ " " + user.getLastName(), user.getCompany().getId(), false, false
			);

		assertEquals(1, results.size());
	}

	@Test
	@Transactional
	public void getProjectionMapById_fetchLastName_emptyFirstName() {
		Map<String, Object> map = userDAO.getProjectionMapById(user.getId(), "lastName");
		assertNull(map.get("firstName"));
	}

	@Test
	@Transactional
	public void getProjectionMapById_fetchLastNameAndEmail_emptyFirstName() {
		Map<String, Object> map = userDAO.getProjectionMapById(user.getId(), "id", "lastName", "email");
		assertNull(map.get("firstName"));
	}

	@Test
	@Transactional
	public void getProjectionMapById_fetchLastNameAndEmail_correctSize() {
		Map<String, Object> map = userDAO.getProjectionMapById(user.getId(), "id", "lastName", "email");
		assertTrue(map.keySet().size() == 3);
	}

	@Test
	@Transactional
	public void getProjectionMapById_fetchFirstName_hasFirstName() {
		Map<String, Object> map = userDAO.getProjectionMapById(user.getId(), "firstName");
		assertEquals(map.get("firstName"), user.getFirstName());
	}

	@Test
	@Transactional
	public void getProjectionMapById_fetchId_hasId() {
		Map<String, Object> map = userDAO.getProjectionMapById(user.getId(), "id");
		assertEquals(map.get("id"), user.getId());
	}

	@Test
	@Transactional
	public void getProjectionMapByIds_fetchLastName_emptyFirstName() {
		Map<Long, Map<String, Object>> map = userDAO.getProjectionMapByIds(Lists.newArrayList(user.getId()), "lastName");

		assertNull(map.get(user.getId()).get("firstName"));
	}

	@Test
	@Transactional
	public void getProjectionMapByIds_multipleFetchLastName_emptyFirstName() {
		Map<Long, Map<String, Object>> map = userDAO.getProjectionMapByIds(Lists.newArrayList(user.getId(), user2.getId()), "lastName");

		assertNull(map.get(user.getId()).get("firstName"));
	}

	@Test
	@Transactional
	public void getProjectionMapByIds_multipleFetchLastName_correctSize() {
		Map<Long, Map<String, Object>> map = userDAO.getProjectionMapByIds(Lists.newArrayList(user.getId(), user2.getId()), "lastName");

		assertTrue(map.keySet().size() == 2);
	}

	@Test
	@Transactional
	public void getProjectionMapByIds_fetchLastNameAndEmailAndId_emptyFirstName() {
		Map<Long, Map<String, Object>> map = userDAO.getProjectionMapByIds(Lists.newArrayList(user.getId()), "lastName", "email", "id");

		assertNull(map.get(user.getId()).get("firstName"));
	}

	@Test
	@Transactional
	public void getProjectionMapByIds_multpleFetchLastNameAndEmailAndId_emptyFirstName() {
		Map<Long, Map<String, Object>> map = userDAO.getProjectionMapByIds(Lists.newArrayList(user.getId(), user2.getId()), "lastName", "email", "id");

		assertNull(map.get(user.getId()).get("firstName"));
	}

	@Test
	@Transactional
	public void getProjectionMapByIds_fetchFirstName_hasFirstName() {
		Map<Long, Map<String, Object>> map = userDAO.getProjectionMapByIds(Lists.newArrayList(user.getId()), "firstName");
		assertEquals(map.get(user.getId()).get("firstName"), user.getFirstName());
	}

	@Test
	@Transactional
	public void getProjectionMapByIds_multipleFetchFirstName_hasFirstName() {
		Map<Long, Map<String, Object>> map = userDAO.getProjectionMapByIds(Lists.newArrayList(user.getId(), user2.getId()), "firstName");
		assertEquals(map.get(user2.getId()).get("firstName"), user2.getFirstName());
	}

	@Test
	@Transactional
	public void getProjectionMapByIds_fetchFirstNameAndId_hasId() {
		Map<Long, Map<String, Object>> map = userDAO.getProjectionMapByIds(Lists.newArrayList(user.getId()), "firstName", "id");
		assertEquals(map.get(user.getId()).get("id"), user.getId());
	}

	@Test
	@Transactional
	public void getProjectionMapByIds_multipleFetchFirstNameAndId_hasId() {
		Map<Long, Map<String, Object>> map = userDAO.getProjectionMapByIds(Lists.newArrayList(user.getId(), user2.getId()), "firstName", "id");
		assertEquals(map.get(user2.getId()).get("id"), user2.getId());
	}

	@Test
	@Transactional
	public void getProjectionMapByIds_emptyIds_returnsEmptyMap() {
		List<Long> ids = Lists.newArrayList();
		Map<Long, Map<String, Object>> map = userDAO.getProjectionMapByIds(ids, "firstName", "id");
		assertTrue(MapUtils.isEmpty(map));
	}

	@Test
	@Transactional
	public void getProjectionMapByIds_nullIds_returnsEmptyMap() {
		List<Long> ids = Lists.newArrayList();
		ids.add(null);
		Map<Long, Map<String, Object>> map = userDAO.getProjectionMapByIds(ids, "firstName", "id");
		assertTrue(MapUtils.isEmpty(map));
	}

	@Test
	public void getUserIdentitiesByUuids_returnsExistingUsers() throws Exception {
		final User user = newRegisteredWorker();
		List<String> uuids = Lists.newArrayList("non-existing-uuid", user.getUuid());
		List<UserIdentityDTO> userIdentities = userDAO.findUserIdentitiesByUuids(uuids);
		assertEquals(1, userIdentities.size());
		assertEquals(user.getUserNumber(), userIdentities.get(0).getUserNumber());
		assertEquals(user.getUuid(), userIdentities.get(0).getUuid());
		assertEquals(user.getId(), userIdentities.get(0).getUserId());
	}
}
