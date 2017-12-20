package com.workmarket.service.business;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.workmarket.domains.model.Industry;
import com.workmarket.domains.model.User;
import com.workmarket.redis.RedisAdapter;
import com.workmarket.redis.RedisConfig;
import com.workmarket.test.IntegrationTest;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@RunWith(SpringJUnit4ClassRunner.class)
@Category(IntegrationTest.class)
public class IndustryServiceIT extends BaseServiceIT {

	@Autowired IndustryService industryService;
	@Autowired @Qualifier("redisCacheOnly") RedisAdapter redisAdapter;
	@Autowired ProfileService profileService;

	@Test
	public void getAllIndustries() throws Exception {
		assertTrue(industryService.getAllIndustries().size() > 0);
	}

	@Test
	public void getAllIndustryDTOs_testCache() {
		industryService.getAllIndustryDTOs();
		assertTrue(redisAdapter.get(RedisConfig.INDUSTRIES).isPresent());
	}

	@Test
	public void getIndustryDTOsForProfile_testCacheEviction() throws Exception {
		User worker = newContractor();
		Long profileId = profileService.findProfile(worker.getId()).getId();
		industryService.getIndustryDTOsForProfile(profileId);
		assertTrue(isCached(profileId));

		industryService.setIndustriesForProfile(profileId, Sets.newHashSet(Industry.TECHNOLOGY_AND_COMMUNICATIONS));
		assertFalse(isCached(profileId));

		industryService.getIndustryDTOsForProfile(profileId);
		assertTrue(isCached(profileId));
	}

	private boolean isCached(Long profileId) {
		return redisAdapter.get(RedisConfig.INDUSTRIES_FOR_PROFILE + profileId).isPresent();
	}

	@Test
	public void getDefaultIndustriesForUsers_withUsers_returnCorrectIndustries() throws Exception {
		User worker1 = newContractor();
		Long worker1ProfileId = profileService.findProfileId(worker1.getId());

		User worker2 = newContractor();
		Long worker2ProfileId = profileService.findProfileId(worker2.getId());

		User worker3 = newContractor(); // worker with no preference

		industryService.setIndustriesForProfile(worker2ProfileId, Sets.newHashSet(Industry.RETAIL));
		industryService.setIndustriesForProfile(profileService.findProfileId(worker3.getId()), Sets.<Industry>newHashSet());

		Map<Long, Long> industryMap = industryService.getDefaultIndustriesForUsers(Lists.newArrayList(worker1.getId(), worker2.getId(), worker3.getId()));

		assertEquals(3, industryMap.size());
		assertEquals(industryMap.get(worker1.getId()), industryService.getDefaultIndustryForProfile(worker1ProfileId).getId());
		assertEquals(industryMap.get(worker2.getId()), Industry.RETAIL.getId());
		assertEquals(industryMap.get(worker3.getId()), Industry.NONE.getId());
	}
}
