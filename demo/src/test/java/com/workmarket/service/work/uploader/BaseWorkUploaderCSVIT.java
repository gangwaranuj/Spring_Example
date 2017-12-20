package com.workmarket.service.work.uploader;

import com.google.common.collect.Lists;
import com.workmarket.domains.model.MimeType;
import com.workmarket.domains.model.User;
import com.workmarket.domains.model.asset.Upload;
import com.workmarket.domains.model.crm.ClientCompany;
import com.workmarket.helpers.WMCallable;
import com.workmarket.redis.RedisAdapter;
import com.workmarket.redis.RedisFilters;
import com.workmarket.service.business.BaseServiceIT;
import com.workmarket.service.business.UserService;
import com.workmarket.domains.work.service.upload.WorkUploadColumn;
import com.workmarket.service.exception.HostServiceException;
import com.workmarket.service.infra.business.UploadService;
import com.workmarket.service.thrift.TWorkUploadService;
import com.workmarket.thrift.work.uploader.FieldMapping;
import com.workmarket.thrift.work.uploader.FieldMappingGroup;
import com.workmarket.thrift.work.uploader.FieldType;
import com.workmarket.thrift.work.uploader.WorkUploadRequest;
import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.ByteArrayInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Callable;

public class BaseWorkUploaderCSVIT extends BaseServiceIT {

	protected User user;
	protected ClientCompany clientCompany;

	protected final static String TMP_FILE = "/tmp/upload-test";

	@Autowired protected UserService userService;
	@Autowired protected UploadService uploadService;
	@Autowired protected TWorkUploadService workUploader;
	@Autowired protected RedisAdapter redisAdapter;

/*
	@Before
	public void before() {
		user = userService.findUserById(ANONYMOUS_USER_ID);
	}
*/

	protected Callable<Boolean> uploadSaved() {
		return new WMCallable<Boolean>(webRequestContextProvider) {
			public Boolean apply() throws Exception {
				String uploadProgressKey = RedisFilters.userBulkUploadProgressKey(user.getId());
				double progress = Double.valueOf((String)redisAdapter.get(uploadProgressKey).or("0"));

				return (progress == 1);
			}
		};
	}

	protected String findKey(Long userId) {
		String key = null;
		for (Object o : redisAdapter.getKeys("bulk_upload:user:" + userId + ":timestamp:*")) {
			String s = (String)o;
			// We don't want the bundle keys
			if (!s.contains("Bundle")) {
				key = s;
			}
		}
		return key;
	}

	protected WorkUploadRequest buildRequest(String data, String fileName) throws IOException, HostServiceException {
		Upload upload = getUpload(data, fileName);

		return new WorkUploadRequest()
			.setUserNumber(user.getUserNumber())
			.setHeadersProvided(true)
			.setUploadUuid(upload.getUUID());
	}

	protected Upload getUpload(String data, String fileName) throws IOException, HostServiceException {
		InputStream is = new ByteArrayInputStream(data.getBytes());
		IOUtils.copy(is, new FileOutputStream(TMP_FILE + fileName));
		return uploadService.storeUpload(TMP_FILE + fileName, fileName, MimeType.TEXT_CSV.getMimeType());
	}

	protected FieldMapping setupFieldMapping(int columnIndex, String name, String sampleValue, String code) {
		FieldMapping fm = new FieldMapping();
		fm.setColumnIndex(columnIndex);
		fm.setColumnName(name);
		fm.setSampleValue(sampleValue);
		FieldType ft = new FieldType();
		ft.setCode(code);
		fm.setType(ft);

		return fm;
	}

	protected FieldMappingGroup setupFieldMappingGroup() {
		FieldMappingGroup mappingGroup = new FieldMappingGroup();
		mappingGroup.setName("Blarg " + new Date().getTime());

		List<FieldMapping> fml = Lists.newArrayList();

		fml.add(setupFieldMapping(0, "Name",       "", "title"));
		fml.add(setupFieldMapping(1, "Desc",       "", "description"));
		fml.add(setupFieldMapping(2, "Start Date", "", "start_date"));
		fml.add(setupFieldMapping(3, "End Date",   "", "end_date"));
		fml.add(setupFieldMapping(4, "Start Time", "", "start_time"));
		fml.add(setupFieldMapping(5, "End Time",   "", "end_time"));
		fml.add(setupFieldMapping(6, "Address",    "", "location_address_1"));
		fml.add(setupFieldMapping(7, "City",       "", "location_city"));
		fml.add(setupFieldMapping(8, "State",      "", "location_state"));
		fml.add(setupFieldMapping(9, "Zip",        "", "location_postal_code"));
		fml.add(setupFieldMapping(10, "Pay",       "", "flat_price_client_fee"));

		mappingGroup.setMappings(fml);

		return mappingGroup;
	}

	protected FieldMappingGroup createStevesFieldMappingGroup() {
		FieldMappingGroup groupToReturn = new FieldMappingGroup();
		groupToReturn.setMappings(createSteveMappings());
		groupToReturn.setName("Steve's mapping mock.");
		return groupToReturn;
	}

	protected List<FieldMapping> createSteveMappings() {
		List<FieldMapping> returnVal = Lists.newArrayListWithCapacity(21);
		returnVal.add(createFieldMapping("title", WorkUploadColumn.TITLE, 0));
		returnVal.add(createFieldMapping("description", WorkUploadColumn.DESCRIPTION, 1));
		returnVal.add(createFieldMapping("start_date", WorkUploadColumn.START_DATE, 2));
		returnVal.add(createFieldMapping("end_date", WorkUploadColumn.END_DATE, 3));
		returnVal.add(createFieldMapping("end_time", WorkUploadColumn.END_TIME, 4));
		returnVal.add(createFieldMapping("start_time", WorkUploadColumn.START_TIME, 5));
		returnVal.add(createFieldMapping("location_name", WorkUploadColumn.LOCATION_NAME, 6));
		returnVal.add(createFieldMapping("location_address_1", WorkUploadColumn.LOCATION_ADDRESS_1, 7));
		returnVal.add(createFieldMapping("location_city", WorkUploadColumn.LOCATION_CITY, 8));
		returnVal.add(createFieldMapping("location_state", WorkUploadColumn.LOCATION_STATE, 9));
		returnVal.add(createFieldMapping("location_postal_code", WorkUploadColumn.LOCATION_POSTAL_CODE, 10));
		returnVal.add(createFieldMapping("initial_per_hour_price_client_fee", WorkUploadColumn.INITIAL_PER_HOUR_PRICE_CLIENT_FEE, 11));
		returnVal.add(createFieldMapping("additional_per_hour_price_client_fee", WorkUploadColumn.ADDITIONAL_PER_HOUR_PRICE_CLIENT_FEE, 12));
		returnVal.add(createFieldMapping("max_number_of_hours_at_initial_price", WorkUploadColumn.MAX_NUMBER_OF_HOURS_AT_INITIAL_PRICE, 13));
		returnVal.add(createFieldMapping("max_number_of_hours_at_additional_price", WorkUploadColumn.MAX_NUMBER_OF_HOURS_AT_ADDITIONAL_PRICE, 14));
		returnVal.add(createFieldMapping("client_name", WorkUploadColumn.CLIENT_NAME, 15));
		returnVal.add(createFieldMapping("contact_first_name", WorkUploadColumn.CONTACT_FIRST_NAME, 16));
		returnVal.add(createFieldMapping("contact_last_name", WorkUploadColumn.CONTACT_LAST_NAME, 17));
		returnVal.add(createFieldMapping("contact_phone", WorkUploadColumn.CONTACT_PHONE, 18));
		returnVal.add(createFieldMapping("contact_email", WorkUploadColumn.CONTACT_EMAIL, 19));
		returnVal.add(createFieldMapping("location_dress_code", WorkUploadColumn.LOCATION_DRESS_CODE, 20));
		return returnVal;
	}

	protected FieldMapping createFieldMapping(String string, WorkUploadColumn column, int i) {
		FieldMapping mapping = new FieldMapping().setColumnIndex(i).setColumnName(string).setType(column.createFieldType());
		return mapping;
	}
}
