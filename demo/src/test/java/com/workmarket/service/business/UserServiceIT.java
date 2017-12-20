package com.workmarket.service.business;

import com.google.common.collect.Sets;
import com.workmarket.dao.BlockedAssociationDAO;
import com.workmarket.domains.model.User;
import com.workmarket.domains.work.model.Work;
import com.workmarket.redis.RedisAdapter;
import com.workmarket.redis.RedisConfig;
import com.workmarket.search.model.SearchUser;
import com.workmarket.service.business.dto.CompleteWorkDTO;
import com.workmarket.service.business.wrapper.AcceptWorkResponse;
import com.workmarket.test.IntegrationTest;
import com.workmarket.utility.CollectionUtilities;
import com.workmarket.utility.DateUtilities;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Calendar;
import java.util.List;
import java.util.UUID;

import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@Category(IntegrationTest.class)
public class UserServiceIT extends BaseServiceIT {

	@Autowired
	UserService userService;
	@Autowired
	BlockedAssociationDAO blockedAssociationDAO;
	@Autowired
	@Qualifier("redisCacheOnly")
	RedisAdapter redisAdapter;
	@Autowired private WebActivityAuditService webActivityAuditService;

	private User worker, buyer, buyer2;
	private Work work;

	@Before
	public void initialize() throws Exception {
		worker = newContractor();
		buyer = newFirstEmployeeWithCashBalance();
		buyer2 = newFirstEmployeeWithCashBalance();
		work = newWork(buyer.getId());

		laneService.addUserToCompanyLane2(worker.getId(), buyer.getCompany().getId());

		workRoutingService.openWork(work.getWorkNumber());
		routingStrategyService.addUserNumbersRoutingStrategy(work.getId(), Sets.newHashSet(worker.getUserNumber()), 0, false);

		AcceptWorkResponse response = workService.acceptWork(worker.getId(), work.getId());
		assertTrue(response.isSuccessful());
	}

	@Test(expected = IllegalArgumentException.class)
	public void isCompanyBlockedByUser_haveWorkInProgress_throwException() {
		userService.blockCompany(worker.getId(), buyer.getCompany().getId());

		assertFalse(userService.isCompanyBlockedByUser(worker.getId(), worker.getCompany().getId(), buyer.getCompany().getId()));
	}

	@Test
	public void isCompanyBlockedByUser_noWorkInProgress_returnBlockedTrue() {
		authenticationService.setCurrentUser(buyer);
		CompleteWorkDTO completeWorkDTO = new CompleteWorkDTO();
		completeWorkDTO.setResolution("Complete Work");
		workService.completeWork(work.getId(), completeWorkDTO);
		workService.closeWork(work.getId());
		assertFalse(userService.isCompanyBlockedByUser(worker.getId(), worker.getCompany().getId(), buyer.getCompany().getId()));

		userService.blockCompany(worker.getId(), buyer.getCompany().getId());

		assertTrue(userService.isCompanyBlockedByUser(worker.getId(), worker.getCompany().getId(), buyer.getCompany().getId()));
	}

	@Test
	public void isCompanyBlockedByUser_noBlock_returnBlockedFalse() {
		assertFalse(userService.isCompanyBlockedByUser(worker.getId(), worker.getCompany().getId(), buyer.getCompany().getId()));
	}

	@Test
	public void isUserBlockedByCompany_noBlock_returnBlockedFalse() {
		assertFalse(userService.isUserBlockedByCompany(worker.getId(), worker.getCompany().getId(), buyer.getCompany().getId()));
	}

	@Test
	public void isUserBlockedByCompany_buyerBlocksWorker_returnBlockedTrue() {
		userService.blockUser(buyer.getId(), worker.getId());

		assertTrue(userService.isUserBlockedByCompany(worker.getId(), worker.getCompany().getId(), buyer.getCompany().getId()));
	}

	@Test
	public void isUserBlockedForCompany_noBlock_returnBlockedFalse() {
		assertFalse(userService.isUserBlockedForCompany(worker.getId(), worker.getCompany().getId(), buyer.getCompany().getId()));
	}

	@Test
	public void isUserBlockedForCompany_buyer1BlocksWorker_workerBlocksBuyer2_returnTrueForBoth() throws Exception {
		userService.blockUser(buyer.getId(), worker.getId());
		userService.blockCompany(worker.getId(), buyer2.getCompany().getId());

		assertTrue(userService.isUserBlockedForCompany(worker.getId(), worker.getCompany().getId(), buyer.getCompany().getId()));
		assertTrue(userService.isUserBlockedForCompany(worker.getId(), worker.getCompany().getId(), buyer2.getCompany().getId()));
	}

	@Test
	public void findBlockedOrBlockedByCompanyIdsByUserId_noRecords_returnEmptyList() throws Exception {
		assertEquals(0, userService.findBlockedOrBlockedByCompanyIdsByUserId(worker.getId()).size());
	}

	@Test
	public void findBlockedOrBlockedByCompanyIdsByUserId_buyer1BlocksWorker_workerBlocksBuyer2_returnBothBuyerCompanyIds() throws Exception {
		userService.blockUser(buyer.getId(), worker.getId());
		userService.blockCompany(worker.getId(), buyer2.getCompany().getId());

		assertTrue(CollectionUtilities.containsAll(
			userService.findBlockedOrBlockedByCompanyIdsByUserId(worker.getId()),
			buyer.getCompany().getId(),
			buyer2.getCompany().getId()
		));
	}

	@Test
	public void findBlockedOrBlockedByCompanyIdsByUserId_buyer2BlocksWorker_workerBlocksBuyer2_returnNoDupes() throws Exception {
		userService.blockUser(buyer2.getId(), worker.getId());
		userService.blockCompany(worker.getId(), buyer2.getCompany().getId());

		assertEquals(1, userService.findBlockedOrBlockedByCompanyIdsByUserId(worker.getId()).size());
		assertEquals(buyer2.getCompany().getId(), userService.findBlockedOrBlockedByCompanyIdsByUserId(worker.getId()).get(0));

	}

	@Test
	public void findAllBlockedUserIdsByBlockingUserId_noRecordExists_returnEmptyList() {
		assertEquals(0, userService.findAllBlockedUserIdsByBlockingUserId(0L).size());
	}

	@Test
	public void findAllBlockedUserIdsByBlockingUserId_userBlocksUser_returnBlockedUserId() {
		userService.blockUser(buyer.getId(), worker.getId());

		assertEquals(worker.getId(), userService.findAllBlockedUserIdsByBlockingUserId(buyer.getId()).get(0));
	}

	@Test
	public void findAllBlockedUserIdsByBlockingUserId_userInUsersCompanyBlocksUser_returnBlockedUserId() throws Exception {
		userService.blockUser(buyer.getId(), worker.getId());
		User newEmployee = newCompanyEmployee(buyer.getCompany().getId());

		assertEquals(worker.getId(), userService.findAllBlockedUserIdsByBlockingUserId(newEmployee.getId()).get(0));
	}

	@Test
	public void findAllBlockedUserIdsByBlockingUserId_testCacheEviction_blockUser() throws Exception {
		userService.findAllBlockedUserIdsByBlockingUserId(buyer.getId());
		assertTrue(redisAdapter.get(RedisConfig.BLOCKED_USER_IDS + buyer.getId()).isPresent());

		userService.blockUser(buyer.getId(), 1L);

		assertFalse(redisAdapter.get(RedisConfig.BLOCKED_USER_IDS + buyer.getId()).isPresent());
	}

	@Test
	public void findAllBlockedUserIdsByBlockingUserId_testCacheEviction_blockUserFromCompany() throws Exception {
		userService.findAllBlockedUserIdsByBlockingUserId(buyer.getId());
		assertTrue(redisAdapter.get(RedisConfig.BLOCKED_USER_IDS + buyer.getId()).isPresent());

		userService.blockUserFromCompany(buyer.getId(), 1L, buyer.getCompany().getId());

		assertFalse(redisAdapter.get(RedisConfig.BLOCKED_USER_IDS + buyer.getId()).isPresent());
	}

	@Test
	public void findAllBlockedUserIdsByBlockingUserId_testCacheEviction_unblockUser() throws Exception {
		userService.findAllBlockedUserIdsByBlockingUserId(buyer.getId());
		assertTrue(redisAdapter.get(RedisConfig.BLOCKED_USER_IDS + buyer.getId()).isPresent());

		userService.unblockUser(buyer.getId(), 1L);

		assertFalse(redisAdapter.get(RedisConfig.BLOCKED_USER_IDS + buyer.getId()).isPresent());
	}

	@Test
	public void getSearchUser_propertiesAreEqual() throws Exception {
		User user = newFirstEmployeeWithCashBalance();
		SearchUser searchUser = userService.getSearchUser(user.getId());

		assertEquals(searchUser.getId(), user.getId());
		assertEquals(searchUser.getProfileId(), user.getProfile().getId());
		assertEquals(searchUser.getCompanyId(), user.getCompany().getId());
	}

	@Test
	public void getSearchUser_methodCallDoesCachePut() throws Exception {
		userService.getSearchUser(buyer.getId());
		assertTrue(redisAdapter.get(RedisConfig.SEARCH_USER + buyer.getId()).isPresent());
	}

	@Test
	public void existsBy_withValidUserNumber_returnTrue() {
		assertTrue(userService.existsBy("userNumber", worker.getUserNumber()));
	}

	@Test
	public void existsBy_withValidUserNumberAndValidId_returnTrue() {
		assertTrue(userService.existsBy("userNumber", worker.getUserNumber(), "id", worker.getId()));
	}

	@Test
	public void existsBy_withInvalidUserNumber_returnFalse() {
		assertFalse(userService.existsBy("userNumber", UUID.randomUUID().toString()));
	}

	@Test
	public void existsBy_withInvalidUserNumberAndValidId_returnFalse() {
		assertFalse(userService.existsBy("userNumber", UUID.randomUUID().toString(), "id", worker.getId()));
	}

	@Test
	public void findAllBlockedUserNumbersByBlockingUserId_noRecordExists_returnEmptyList() {
		assertEquals(0, userService.findAllBlockedUserNumbersByBlockingUserId(0L).size());
	}

	@Test
	public void findAllBlockedUserNumbersByBlockingUserId_userBlocksUser_returnBlockedUserId() {
		userService.blockUser(buyer.getId(), worker.getId());

		assertEquals(worker.getUserNumber(), userService.findAllBlockedUserNumbersByBlockingUserId(buyer.getId()).get(0));
	}

	@Test
	public void findAllBlockedUserNumbersByBlockingUserId_userInUsersCompanyBlocksUser_returnBlockedUserId() throws Exception {
		userService.blockUser(buyer.getId(), worker.getId());
		User newEmployee = newCompanyEmployee(buyer.getCompany().getId());

		assertEquals(worker.getUserNumber(), userService.findAllBlockedUserNumbersByBlockingUserId(newEmployee.getId()).get(0));
	}
}
