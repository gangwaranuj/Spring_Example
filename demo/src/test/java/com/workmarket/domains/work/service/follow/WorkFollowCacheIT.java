package com.workmarket.domains.work.service.follow;

import com.google.common.base.Optional;
import com.google.common.collect.Lists;
import com.workmarket.service.business.BaseServiceIT;
import com.workmarket.service.business.dto.WorkFollowDTO;
import com.workmarket.test.IntegrationTest;
import com.workmarket.utility.RandomUtilities;
import org.apache.commons.collections.CollectionUtils;
import org.hamcrest.collection.IsIterableContainingInOrder;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@RunWith(SpringJUnit4ClassRunner.class)
@Category(IntegrationTest.class)
public class WorkFollowCacheIT extends BaseServiceIT {

	@Autowired WorkFollowCache workFollowCache;

	@Test
	public void set_get_evict() {
		List<WorkFollowDTO> followDTOs = Lists.newArrayList(buildWorkFollowDTO(), buildWorkFollowDTO());
		final long workId = RandomUtilities.nextLong();

		workFollowCache.set(workId, followDTOs);

		Optional<List<WorkFollowDTO>> followersOptional = workFollowCache.get(workId);
		assertTrue(followersOptional.isPresent());

		List<WorkFollowDTO> followDTOsFromCache = followersOptional.get();
		assertTrue(CollectionUtils.isEqualCollection(followDTOs, followDTOsFromCache));

		workFollowCache.evict(workId);
		assertFalse(workFollowCache.get(workId).isPresent());
	}

	private WorkFollowDTO buildWorkFollowDTO() {
		WorkFollowDTO workFollowDTO = new WorkFollowDTO();
		workFollowDTO.setId(RandomUtilities.nextLong());
		workFollowDTO.setFollowerFirstName(RandomUtilities.generateAlphaString(4));
		workFollowDTO.setFollowerLastName(RandomUtilities.generateAlphaString(4));
		return workFollowDTO;
	}

}
