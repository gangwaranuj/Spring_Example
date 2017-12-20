package com.workmarket.service.search.query;

import com.workmarket.reporting.util.ZipCSVSearchWriter;
import com.workmarket.search.request.user.Pagination;
import com.workmarket.search.request.user.PeopleSearchRequest;
import com.workmarket.search.response.user.PeopleSearchResponse;
import com.workmarket.service.business.BaseServiceIT;
import com.workmarket.configuration.Constants;
import com.workmarket.service.search.SearchService;
import com.workmarket.service.search.user.ZipCSVSearchExecutor;
import com.workmarket.test.IntegrationTest;
import com.workmarket.utility.DateUtilities;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.FileInputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import static org.junit.Assert.assertEquals;

@RunWith(SpringJUnit4ClassRunner.class)
@Category(IntegrationTest.class)
public class ZipCSVSearchExecutorIT extends BaseServiceIT {

	@Autowired SearchService peopleSearchService;

	@Test
	public void search_success() throws Exception {
		String filename = DateUtilities.getSearchCSVFilename();
		ZipCSVSearchWriter writer = new ZipCSVSearchWriter(DateUtilities.getSearchCSVFilename(), Constants.EXPORT_SEARCH_CSV_DIRECTORY);

		PeopleSearchRequest request = new PeopleSearchRequest();
		request.setUserId(1);
		request.setPaginationRequest(new Pagination());
		request.getPaginationRequest().setPageSize(1);

		PeopleSearchResponse response = peopleSearchService.searchPeople(request);
		ZipCSVSearchExecutor searchExecutor = new ZipCSVSearchExecutor(writer, response);

		// Creates zip file
		searchExecutor.search();

		ZipInputStream zis = new ZipInputStream(new FileInputStream(writer.getAbsolutePath()));
		ZipEntry entry = zis.getNextEntry();
		assertEquals(entry.getName(), filename + Constants.CSV_EXTENSION);
	}
}
