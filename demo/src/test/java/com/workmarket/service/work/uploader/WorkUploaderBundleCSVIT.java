package com.workmarket.service.work.uploader;

import com.workmarket.domains.model.User;
import com.workmarket.domains.work.model.Work;
import com.workmarket.domains.work.service.WorkBundleService;
import com.workmarket.domains.work.service.upload.WorkUploadColumn;
import com.workmarket.helpers.WMCallable;
import com.workmarket.test.IntegrationTest;
import com.workmarket.thrift.work.uploader.WorkUploadRequest;
import com.workmarket.thrift.work.uploader.WorkUploadResponse;
import net.jcip.annotations.NotThreadSafe;
import org.apache.commons.collections.CollectionUtils;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;
import java.util.concurrent.Callable;

import static com.jayway.awaitility.Awaitility.await;
import static com.workmarket.service.work.uploader.CSVUploaderHelper.FAIL;
import static com.workmarket.service.work.uploader.CSVUploaderHelper.PASS;
import static com.workmarket.service.work.uploader.CSVUploaderHelper.completeDynamicFixture;
import static com.workmarket.service.work.uploader.CSVUploaderHelper.fixtures;
import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

@RunWith(SpringJUnit4ClassRunner.class)
@Category(IntegrationTest.class)
@NotThreadSafe
public class WorkUploaderBundleCSVIT extends BaseWorkUploaderCSVIT {

	@Test
	public void flatRate_NewBundle_NoMapping_HappyPath() throws Exception {
		user = newFirstEmployeeWithCashBalance();
		String newBundle = fixtures.get(PASS).get("flat_rate_new_bundle");

		WorkUploadRequest request = buildRequest(newBundle, "flat_rate_new_bundle.csv");
		WorkUploadResponse response = workUploader.uploadWorkPreview(request);
		assertNull("Errors found: " + response.getErrorUploads(), response.getErrorUploads());
	}

	@Test
	public void badBundle_Mix_Error() throws Exception {
		user = newFirstEmployeeWithCashBalance();
		String badBundleMix = fixtures.get(FAIL).get("bad_bundle_mix");

		WorkUploadRequest request = buildRequest(badBundleMix, "bad_bundle_mix.csv");
		WorkUploadResponse response = workUploader.uploadWorkPreview(request);
		assertEquals(WorkUploadColumn.EXISTING_BUNDLE_ID.getUploadColumnName(), response.getErrorUploads().get(0).getErrors().get(0).getColumn());
	}

	@Test
	public void badBundle_MissingDesc_Error() throws Exception {
		user = newFirstEmployeeWithCashBalance();
		String badBundleMissingDesc = fixtures.get(FAIL).get("bad_bundle_missing_desc");

		WorkUploadRequest request = buildRequest(badBundleMissingDesc, "bad_bundle_missing_desc.csv");
		WorkUploadResponse response = workUploader.uploadWorkPreview(request);
		assertEquals(WorkUploadColumn.NEW_BUNDLE_DESCRIPTION.getUploadColumnName(), response.getErrorUploads().get(0).getErrors().get(0).getColumn());
	}

	@Test
	public void badBundle_MissingName_Error() throws Exception {
		user = newFirstEmployeeWithCashBalance();
		String badBundleMissingDesc = fixtures.get(FAIL).get("bad_bundle_missing_name");

		WorkUploadRequest request = buildRequest(badBundleMissingDesc, "bad_bundle_missing_name.csv");
		WorkUploadResponse response = workUploader.uploadWorkPreview(request);
		assertEquals(WorkUploadColumn.NEW_BUNDLE_NAME.getUploadColumnName(), response.getErrorUploads().get(0).getErrors().get(0).getColumn());
	}

	@Test
	public void flatRate_NewBundle_NoMapping_BadWork_Error() throws Exception {
		user = newFirstEmployeeWithCashBalance();
		String goodBundleBadWork = fixtures.get(FAIL).get("good_bundle_bad_work");

		WorkUploadRequest request = buildRequest(goodBundleBadWork, "good_bundle_bad_work.csv");
		WorkUploadResponse response = workUploader.uploadWork(request);
		assertEquals(WorkUploadColumn.TITLE.getUploadColumnName(), response.getErrorUploads().get(0).getErrors().get(0).getColumn());
	}

	@Test
	public void flatRate_TwoBundles_HappyPath() throws Exception {
		user = newFirstEmployeeWithCashBalance();
		String goodBundleBadWork = fixtures.get(PASS).get("two_good_bundles");

		WorkUploadRequest request = buildRequest(goodBundleBadWork, "two_good_bundles.csv");
		workUploader.uploadWork(request);

		await().atMost(JMS_DELAY, MILLISECONDS).until(uploadSaved());

		String key = findKey(user.getId());
		List<String> workNumbers = redisAdapter.getList(key);

		assertEquals(4, workNumbers.size());
		for (String workNumber : workNumbers) {
			Work work = workService.findWorkByWorkNumber(workNumber);
			assertTrue(work.isInBundle());
			assertTrue(workBundleService.findByChild(work.getId()).getTitle().contains("Bundle"));
		}
	}

	private Callable<Boolean> doneAttachingResources(final Work work) {
		return new WMCallable<Boolean>(webRequestContextProvider) {
			public Boolean apply() throws Exception {
				return CollectionUtils.isNotEmpty(workService.findWorkerIdsForWork(work.getId()));
			}
		};
	}

	@Test
	public void uploadWork_Bundles_Routing() throws Exception {
		user = newFirstEmployeeWithCashBalance();
		User worker1 = newContractorIndependentlane4Ready();
		laneService.addUserToCompanyLane2(worker1.getId(), user.getCompany().getId());

		String twoGoodBundlesWithRouting = completeDynamicFixture(PASS, "two_good_bundles_with_routing", worker1.getUserNumber());

		WorkUploadRequest request = buildRequest(twoGoodBundlesWithRouting, "two_good_bundles_with_routing.csv");
		workUploader.uploadWork(request);

		await().atMost(JMS_DELAY, MILLISECONDS).until(uploadSaved());

		String key = findKey(user.getId());
		List<String> workNumbers = redisAdapter.getList(key);

		for (String workNumber : workNumbers) {
			Work work = workService.findWorkByWorkNumber(workNumber);
			await().atMost(JMS_DELAY, MILLISECONDS).until(doneAttachingResources(work));
			assertTrue(work.isInBundle());
			assertEquals(1, workService.findWorkerIdsForWork(work.getId()).size());
		}
	}
}
