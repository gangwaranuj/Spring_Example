package com.workmarket.service.work.uploader;

import com.google.api.client.util.Lists;
import com.workmarket.domains.model.User;
import com.workmarket.domains.work.model.Work;
import com.workmarket.test.IntegrationTest;
import com.workmarket.thrift.work.uploader.WorkUploadRequest;
import net.jcip.annotations.NotThreadSafe;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

import static com.jayway.awaitility.Awaitility.await;
import static com.workmarket.service.work.uploader.CSVUploaderHelper.PASS;
import static com.workmarket.service.work.uploader.CSVUploaderHelper.completeDynamicFixture;
import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static org.junit.Assert.assertEquals;

@RunWith(SpringJUnit4ClassRunner.class)
@Category(IntegrationTest.class)
@NotThreadSafe
public class WorkUploaderCSVIT extends BaseWorkUploaderCSVIT {

	@Test
	public void uploadWork_With_Routing() throws Exception {
		user = newFirstEmployeeWithCashBalance();
		User worker1 = newContractorIndependentlane4Ready();
		laneService.addUserToCompanyLane2(worker1.getId(), user.getCompany().getId());

		String fourAssignmentsWithRouting = completeDynamicFixture(PASS, "four_assignments_with_routing", worker1.getUserNumber());

		WorkUploadRequest request = buildRequest(fourAssignmentsWithRouting, "four_assignments_with_routing.csv");
		workUploader.uploadWork(request);

		await().atMost(JMS_DELAY, MILLISECONDS).until(uploadSaved());

		String key = findKey(user.getId());
		List<String> workNumbers = redisAdapter.getList(key);

		List<Long> userIds = Lists.newArrayList();
		for (String workNumber : workNumbers) {
			Work work = workService.findWorkByWorkNumber(workNumber);
			userIds.add(workService.findWorkerIdsForWork(work.getId()).get(0));
		}

		for (Long userId : userIds) { assertEquals(worker1.getId(), userId); }
	}
}
