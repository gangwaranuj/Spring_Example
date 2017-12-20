package com.workmarket.service.work.uploader;

import com.workmarket.test.IntegrationTest;
import com.workmarket.thrift.work.uploader.FieldMappingGroup;
import com.workmarket.thrift.work.uploader.SaveMappingRequest;
import com.workmarket.thrift.work.uploader.WorkUploadRequest;
import com.workmarket.thrift.work.uploader.WorkUploadResponse;
import net.jcip.annotations.NotThreadSafe;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static com.jayway.awaitility.Awaitility.await;
import static com.workmarket.service.work.uploader.CSVUploaderHelper.PASS;
import static com.workmarket.service.work.uploader.CSVUploaderHelper.fixtures;
import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

@RunWith(SpringJUnit4ClassRunner.class)
@Category(IntegrationTest.class)
@NotThreadSafe
public class WorkUploaderPricingCSVIT extends BaseWorkUploaderCSVIT {

	@Test
	public void flatRate_WithMapping_HappyPath() throws Exception {
		user = newFirstEmployeeWithCashBalance();
		FieldMappingGroup mappingGroup = setupFieldMappingGroup();

		SaveMappingRequest mappingRequest = new SaveMappingRequest()
			.setUserNumber(user.getUserNumber())
			.setMappingGroup(mappingGroup);

		FieldMappingGroup responseMappingGroup = workUploader.saveMapping(mappingRequest);

		String contrived = fixtures.get(PASS).get("contrived");

		WorkUploadRequest request = buildRequest(contrived, "contrived.csv")
				.setMappingGroupId(responseMappingGroup.getId());
		WorkUploadResponse response = workUploader.uploadWorkPreview(request);

		//if the upload preview works, then we *should* be good
		assertNull("Errors found: " + response.getErrorUploads(), response.getErrorUploads());

		//this response should work now..
		response = workUploader.uploadWork(request);

		await().atMost(JMS_DELAY, MILLISECONDS).until(uploadSaved());

		String key = (String)redisAdapter.getKeys("bulk_upload:user:" + user.getId() + ":timestamp:*").iterator().next();

		assertEquals(11, response.getMappingGroup().getMappingsSize());
		assertEquals(17, redisAdapter.getList(key).size());
	}

	@Test
	public void perHour_HappyPath() throws Exception {
		user = newFirstEmployeeWithCashBalance();
		String perHour = fixtures.get(PASS).get("per_hour");

		WorkUploadRequest request = buildRequest(perHour, "per_hour.csv");
		WorkUploadResponse response = workUploader.uploadWorkPreview(request);

		assertEquals(0, response.getErrorUploadsSize());
	}

	@Test
	public void perUnit_HappyPath() throws Exception {
		user = newFirstEmployeeWithCashBalance();
		String perUnit = fixtures.get(PASS).get("per_unit");

		WorkUploadRequest request = buildRequest(perUnit, "per_unit.csv");
		WorkUploadResponse response = workUploader.uploadWorkPreview(request);

		assertEquals(0, response.getErrorUploadsSize());
	}

	@Test
	public void blendedPerHour_HappyPath() throws Exception {
		user = newFirstEmployeeWithCashBalance();
		String blendedPerHour = fixtures.get(PASS).get("blended_per_hour");

		WorkUploadRequest request = buildRequest(blendedPerHour, "001-blended_per_hour.csv");
		WorkUploadResponse response = workUploader.uploadWorkPreview(request);

		//if the upload preview works, then we *should* be good
		assertEquals(0, response.getErrorUploadsSize());
	}
}
