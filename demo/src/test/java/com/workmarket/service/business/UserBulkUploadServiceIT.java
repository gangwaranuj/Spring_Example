package com.workmarket.service.business;

import au.com.bytecode.opencsv.CSVReader;
import com.google.api.client.util.Lists;
import com.workmarket.domains.model.MimeType;
import com.workmarket.domains.model.User;
import com.workmarket.domains.model.asset.Upload;
import com.workmarket.helpers.WMCallable;
import com.workmarket.redis.RedisAdapter;
import com.workmarket.redis.RedisFilters;
import com.workmarket.service.business.upload.users.model.BulkUserUploadRequest;
import com.workmarket.service.business.upload.users.model.BulkUserUploadResponse;
import com.workmarket.service.infra.business.UploadService;
import com.workmarket.service.infra.file.AWSRemoteFileAdapterImpl;
import com.workmarket.service.infra.file.RemoteFileType;
import com.workmarket.test.IntegrationTest;
import com.workmarket.utility.RandomUtilities;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.InputStream;
import java.util.List;
import java.util.concurrent.Callable;

import static com.jayway.awaitility.Awaitility.await;
import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

@RunWith(SpringJUnit4ClassRunner.class)
@Category(IntegrationTest.class)
public class UserBulkUploadServiceIT extends BaseServiceIT {

	private final static String TMP_FILE = "/tmp/users-import";
	public static final String INVALID_COLUMN = "Title";
	public static final String[] USERS_HEADER = new String[]{"First Name", "Last Name", "Email", "Work Phone", "Work Phone Ext.", "Work Phone International Code", "Job Title", "Role"};
	public static final String[] USERS_HEADER_WITH_MISSING_COLUMN = new String[]{"First Name", "Last Name", "Email", "Work Phone", "Work Phone Ext.", "Work Phone International Code", "Role"};
	public static final String[] USERS_HEADER_WITH_INVALID_COLUMN = new String[]{"First Name", "Last Name", "Email", "Work Phone", "Work Phone Ext.", "Work Phone International Code", INVALID_COLUMN, "Role"};


	@Autowired private UserBulkUploadService userBulkUploadService;
	@Autowired private UploadService uploadService;
	@Autowired private AWSRemoteFileAdapterImpl remoteFileAdapter;
	@Autowired private RedisAdapter redisAdapter;

	private User buyer;
	private final boolean orgDisabledForUser = false;

	@Before
	public void initialize() throws Exception {
		buyer = newFirstEmployeeWithCashBalance();
	}

	@Test
	public void bulkUploadFileRead_users_withNoColumnHeaders_fail() throws Exception {
		String uuid = generateCSVFile(new String[]{}, getValidUserRow());
		BulkUserUploadRequest request = new BulkUserUploadRequest(uuid, buyer.getId());
		BulkUserUploadResponse response = new BulkUserUploadResponse();
		userBulkUploadService.start(request, response, orgDisabledForUser);
		assertTrue(response.hasErrors());
		assertEquals(response.getErrors().size(), 1);
		assertEquals(response.getErrors().get(0), "Your upload is missing required First Name, Last Name, Email, Work Phone, Work Phone Ext., Work Phone International Code, Job Title, Role column headers");
		assertEquals(uuid, response.getFileUUID());
	}

	@Test
	public void bulkUploadFileRead_users_withMissingColumn_fail() throws Exception {
		String uuid = generateCSVFile(USERS_HEADER_WITH_MISSING_COLUMN, getValidUserRow());
		BulkUserUploadRequest request = new BulkUserUploadRequest(uuid, buyer.getId());
		BulkUserUploadResponse response = new BulkUserUploadResponse();
		userBulkUploadService.start(request, response, orgDisabledForUser);
		assertTrue(response.hasErrors());
		assertEquals(response.getErrors().size(), 1);
		assertEquals(response.getErrors().get(0), "Your upload is missing required Job Title column header");
		assertEquals(uuid, response.getFileUUID());
	}

	@Test
	public void bulkUploadFileRead_users_withInvalidColumn_fail() throws Exception {
		String uuid = generateCSVFile(USERS_HEADER_WITH_INVALID_COLUMN, getValidUserRow());
		BulkUserUploadRequest request = new BulkUserUploadRequest(uuid, buyer.getId());
		BulkUserUploadResponse response = new BulkUserUploadResponse();
		userBulkUploadService.start(request, response, orgDisabledForUser);
		assertTrue(response.hasErrors());
		assertEquals(response.getErrors().size(), 1);
		assertEquals(response.getErrors().get(0), "Your upload is missing required Job Title column header");
		assertEquals(uuid, response.getFileUUID());
	}

	@Test
	public void bulkUploadFileRead_users_noUserData_fail() throws Exception {
		List<String[]> row = Lists.newArrayList();
		row.add(new String[]{});
		String uuid = generateCSVFile(USERS_HEADER, row);
		BulkUserUploadRequest request = new BulkUserUploadRequest(uuid, buyer.getId());
		BulkUserUploadResponse response = new BulkUserUploadResponse();
		userBulkUploadService.start(request, response, orgDisabledForUser);
		assertTrue(response.hasErrors());
		assertEquals(response.getErrors().size(), 1);
		assertEquals(response.getErrors().get(0), "Looks like the file you are trying to upload does not have user data.");
		assertEquals(uuid, response.getFileUUID());
	}

	@Test
	public void bulkUploadFileRead_users_noEffectiveUserData_fail() throws Exception {
		List<String[]> row = Lists.newArrayList();
		row.add(new String[]{});
		row.add(new String[]{",,,,,,,"});
		String uuid = generateCSVFile(USERS_HEADER, row);
		BulkUserUploadRequest request = new BulkUserUploadRequest(uuid, buyer.getId());
		BulkUserUploadResponse response = new BulkUserUploadResponse();
		userBulkUploadService.start(request, response, orgDisabledForUser);
		assertTrue(response.hasErrors());
		assertEquals(response.getErrors().size(), 1);
		assertEquals(response.getErrors().get(0), "Looks like the file you are trying to upload does not have user data.");
		assertEquals(uuid, response.getFileUUID());
	}

	@Test
	public void bulkUploadFileRead_users_emptyFile_fail() throws Exception {
		String uuid = generateEmptyCSVFile();
		BulkUserUploadRequest request = new BulkUserUploadRequest(uuid, buyer.getId());
		BulkUserUploadResponse response = new BulkUserUploadResponse();
		userBulkUploadService.start(request, response, orgDisabledForUser);
		assertTrue(response.hasErrors());
		assertEquals(response.getErrors().size(), 1);
		assertEquals(response.getErrors().get(0), "Looks like the file you are trying to upload is empty.");
		assertEquals(uuid, response.getFileUUID());
	}

	@Test
	public void bulkUploadFileRead_users_withEmptyFirstName_fail() throws Exception {
		String uuid = generateCSVFile(USERS_HEADER, getUserRowWithEmptyFirstName());
		BulkUserUploadRequest request = new BulkUserUploadRequest(uuid, buyer.getId());
		BulkUserUploadResponse response = new BulkUserUploadResponse();
		userBulkUploadService.start(request, response, orgDisabledForUser);
		assertTrue(response.hasErrors());
		assertEquals(response.getErrors().size(), 1);
		assertNotEquals(uuid, response.getFileUUID());

		File file = remoteFileAdapter.getFile(RemoteFileType.TMP, response.getFileUUID());
		CSVReader reader = new CSVReader(new FileReader(file));
		reader.readNext();
		String[] row = reader.readNext();
		assertThat(row.length - USERS_HEADER.length, is(1));
		assertNotNull(row[row.length - 1]);
	}


	@Test
	public void bulkUploadFileRead_users_success() throws Exception {
		String uuid = generateCSVFile(USERS_HEADER, getValidUserRow());
		BulkUserUploadRequest request = new BulkUserUploadRequest(uuid, buyer.getId());
		BulkUserUploadResponse response = new BulkUserUploadResponse();
		userBulkUploadService.start(request, response, orgDisabledForUser);
		assertFalse(response.hasErrors());
		assertEquals(request.getUUID(), response.getFileUUID());
	}

	@Test
	public void bulkUploadFileRead_users_withInlineErrors_success() throws Exception {
		String uuid = generateCSVFile(USERS_HEADER, getValidUserRowWithError());
		BulkUserUploadRequest request = new BulkUserUploadRequest(uuid, buyer.getId());
		BulkUserUploadResponse response = new BulkUserUploadResponse();
		userBulkUploadService.start(request, response, orgDisabledForUser);
		assertFalse(response.hasErrors());
		assertEquals(request.getUUID(), response.getFileUUID());
	}

	@Test
	public void bulkUpload_users_success() throws Exception {

		int uploadSize = getValidUserRows().size();
		String uuid = generateCSVFile(USERS_HEADER, getValidUserRows());
		BulkUserUploadRequest request = new BulkUserUploadRequest(uuid, buyer.getId());
		BulkUserUploadResponse response = new BulkUserUploadResponse();
		userBulkUploadService.start(request, response, orgDisabledForUser);
		assertFalse(response.hasErrors());
		assertEquals(request.getUUID(), response.getFileUUID());
		await().atMost(JMS_DELAY, MILLISECONDS).until(uploadSaved(uuid, uploadSize));
	}

	private Callable<Boolean> uploadSaved(final String uuid, final Integer totalToUpload) {
		return new WMCallable<Boolean>(webRequestContextProvider) {
			public Boolean apply() throws Exception {
				Integer successCount = Integer.valueOf((String) redisAdapter.get(RedisFilters.userBulkUserUploadSuccessCounterKey(buyer.getId(), uuid)).or("0"));
				String failedUploadKey = RedisFilters.userBulkUserFailedUploadKey(buyer.getId(), uuid);
				List<String> failedUpload = redisAdapter.getList(failedUploadKey);
				return successCount.equals(totalToUpload) && failedUpload.isEmpty();
			}
		};
	}

	private String generateCSVFile(String[] csvHeader, List<String[]> csvRows) throws Exception {
		// Prepare data
		List<String> csv = Lists.newArrayList();
		csv.add(StringUtils.join(csvHeader, ","));
		for (String[] csvRow : csvRows) {
			csv.add(StringUtils.join(csvRow, ","));
		}

		String data = StringUtils.join(csv, "\n");
		InputStream is = new ByteArrayInputStream(data.getBytes());
		IOUtils.copy(is, new FileOutputStream(TMP_FILE));
		Upload upload = uploadService.storeUpload(TMP_FILE, "test", MimeType.TEXT_CSV.getMimeType());

		return upload.getUUID();
	}

	private String generateEmptyCSVFile() throws Exception {

		String data = "";
		InputStream is = new ByteArrayInputStream(data.getBytes());
		IOUtils.copy(is, new FileOutputStream(TMP_FILE));
		Upload upload = uploadService.storeUpload(TMP_FILE, "test", MimeType.TEXT_CSV.getMimeType());

		return upload.getUUID();
	}

	private static List<String[]> getValidUserRow() {
		List<String[]> row = Lists.newArrayList();
		row.add(new String[]{RandomUtilities.generateAlphaString(10), RandomUtilities.generateAlphaString(10), getRandomEmail(), "(666)666-6666", "1234", "1", "Engineer", "User"});
		return row;
	}

	private static List<String[]> getValidUserRowWithError() {
		List<String[]> row = Lists.newArrayList();
		row.add(new String[]{RandomUtilities.generateAlphaString(10), RandomUtilities.generateAlphaString(10), getRandomEmail(), "(666)666-6666", "1234", "1", "Engineer", "User", RandomUtilities.generateAlphaString(20)});
		return row;
	}

	private static List<String[]> getUserRowWithEmptyFirstName() {
		List<String[]> row = Lists.newArrayList();
		row.add(new String[]{"", RandomUtilities.generateAlphaString(10), getRandomEmail(), "(666)666-6666", "1234", "1", "Engineer", "User"});
		return row;
	}

	private static List<String[]> getValidUserRows() {
		List<String[]> rows = Lists.newArrayList();
		for (int i = 0; i < 2; i++) {
			rows.add(new String[]{RandomUtilities.generateAlphaString(10), RandomUtilities.generateAlphaString(10), getRandomEmail(), "(666)666-6666", "", "1", "", "User"});
		}
		return rows;
	}

	private static String getRandomEmail() {
		return RandomStringUtils.randomAlphanumeric(20).concat("@xyz.com");
	}


}
