package com.workmarket.service.business.upload;

import com.workmarket.domains.model.User;
import com.workmarket.redis.RedisAdapter;
import com.workmarket.redis.RedisConfig;
import com.workmarket.service.business.BaseServiceIT;
import com.workmarket.service.thrift.transactional.TWorkService;
import com.workmarket.test.IntegrationTest;
import com.workmarket.thrift.work.Template;
import com.workmarket.thrift.work.WorkSaveRequest;
import groovy.lang.Category;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;

@RunWith(SpringJUnit4ClassRunner.class)
@Category(IntegrationTest.class)
public class WorkUploadTemplateFactoryIT extends BaseServiceIT {

	@Autowired TWorkService tWorkService;
	@Autowired @Qualifier("redisCacheOnly") RedisAdapter redisAdapter;
	@Autowired WorkUploadTemplateFactory workUploadTemplateFactory;

	@Test
	public void getTemplate_methodCallDoesCachePut() throws Exception {
		User user = newFirstEmployee();
		WorkSaveRequest workSaveRequest = newWorkSaveRequest(user);
		workSaveRequest.getWork().setTemplate(new Template(0, "sadsfdgfh", "sadsfd"));
		Long workId = tWorkService.saveOrUpdateWorkTemplate(workSaveRequest).getWork().getId();

		workUploadTemplateFactory.getTemplate(workId, user.getId());
		assertTrue(redisAdapter.get(RedisConfig.WORK_TEMPLATE + workId + ":" + user.getId()).isPresent());

		workSaveRequest.getWork().getTemplate().setId(workId);
		tWorkService.saveOrUpdateWorkTemplate(workSaveRequest);
		assertFalse(redisAdapter.get(RedisConfig.WORK_TEMPLATE + workId + ":" + user.getId()).isPresent());
	}
}
