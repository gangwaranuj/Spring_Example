package com.workmarket.service.business;

import com.google.common.collect.Sets;
import com.workmarket.data.solr.indexer.work.WorkIndexer;
import com.workmarket.domains.model.DateRange;
import com.workmarket.domains.model.User;
import com.workmarket.domains.work.model.Work;
import com.workmarket.search.request.work.WorkSearchRequest;
import com.workmarket.search.response.work.WorkSearchResponse;
import com.workmarket.service.business.dto.WorkDTO;
import com.workmarket.test.BrokenTest;
import com.workmarket.utility.DateUtilities;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.util.StopWatch;

import java.util.Date;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * Created with IntelliJ IDEA.
 * User: KhalidRich
 */

@RunWith(SpringJUnit4ClassRunner.class)
@Category(BrokenTest.class)
@Ignore
public class WorkSearchServiceIT extends BaseServiceIT {

	private @Autowired WorkIndexer workIndexer;
	private StopWatch stopWatch = new StopWatch();
	private final long TIMEOUT_TIME = 30000L;

	@Test
	public void testWorkSearchService_NotNull() throws Exception {
		User user = newFirstEmployeeWithCashBalance();
		authenticationService.setCurrentUser(user);

		WorkDTO workDTO = new WorkDTO();
		workDTO.setTitle("Dat Test Work");
		workDTO.setDescription("This is dat test work");
		addWorkDTODetails(workDTO);

		Work work = workFacadeService.saveOrUpdateWork(user.getId(), workDTO);
		workIndexer.reindexById(Sets.newHashSet(work.getId()));

		WorkSearchRequest workSearchRequest = new WorkSearchRequest();
		WorkSearchResponse workSearchResponse = null;
		stopWatch.start();

		while(workSearchResponse == null) {
			if(stopWatch.getTotalTimeMillis() > TIMEOUT_TIME) {
				stopWatch.stop();
				assertNotNull("Timeout error", workSearchResponse);
			}
			workSearchResponse = workSearchService.searchAllWorkByUserId(user.getId(), workSearchRequest);
		}
		stopWatch.stop();
		assertNotNull(workSearchResponse);
		assertNotNull(workSearchResponse.getResults());

	}

	@Test
	public void testWorkSearchServiceUsingKeywordFilter() throws Exception {
		User user = newFirstEmployeeWithCashBalance();
		authenticationService.setCurrentUser(user);

		String keyword = "Dhalik";

		//Three assignments that will contain the keyword "Dhalik"

		WorkDTO workDTO = new WorkDTO();
		workDTO.setTitle("Dhalik");
		workDTO.setDescription("Dhalik.");
		addWorkDTODetails(workDTO);

		Work work = workFacadeService.saveOrUpdateWork(user.getId(), workDTO);

		workDTO.setTitle("Dhalik Rising");
		workDTO.setDescription("Dhalik Rising");
		Work work2 = workFacadeService.saveOrUpdateWork(user.getId(), workDTO);

		workDTO.setTitle("Dhalik Reborn");
		workDTO.setDescription("Dhalik Reborn");
		Work work3 = workFacadeService.saveOrUpdateWork(user.getId(), workDTO);

		workIndexer.reindexById(Sets.newHashSet(work.getId(), work2.getId(), work3.getId()));

		WorkSearchRequest request = new WorkSearchRequest();
		request.setKeyword(keyword);

		WorkSearchResponse workSearchResponse = null;

		stopWatch.start();
		while(workSearchResponse == null) {
			if(stopWatch.getTotalTimeMillis() > TIMEOUT_TIME) {
				assertNotNull("Timeout error", workSearchResponse);
				stopWatch.stop();
			}
			workSearchResponse = workSearchService.searchAllWorkByUserId(user.getId(), request);
		}
		stopWatch.stop();
		assertTrue(3 <= workSearchResponse.getResults().size()); //Should be 3

		WorkSearchRequest failedRequest = new WorkSearchRequest();
		failedRequest.setKeyword("Khalid");

		WorkSearchResponse failedSearchResponse = workSearchService.searchAllWorkByUserId(user.getId(), failedRequest);
		assertFalse(failedSearchResponse.getResults().contains(work));
		assertFalse(failedSearchResponse.getResults().contains(work2));
		assertFalse(failedSearchResponse.getResults().contains(work3));

	}

	@Test
	public void testWorkSearchServiceUsingTimeFilter() throws Exception {
		User user = newFirstEmployeeWithCashBalance();
		authenticationService.setCurrentUser(user);

		//Set up a date range
		DateRange targetRange = new DateRange();
		targetRange.setFrom(DateUtilities.getCalendarFromDate(DateUtilities.getMidnight(new Date(1441962000000L))));
		targetRange.setThrough(DateUtilities.getCalendarFromDate(DateUtilities.getMidnightNextDay(new Date(1441962000000L))));

		//Create the work

		WorkDTO workDTO = new WorkDTO();
		workDTO.setTitle("Dhalik");
		workDTO.setDescription("Dhalik.");
		addWorkDTODetails(workDTO);

		Work work = workFacadeService.saveOrUpdateWork(user.getId(), workDTO);

		workDTO.setScheduleFromString("2015-09-11T12:00:00Z");
		Work work2 = workFacadeService.saveOrUpdateWork(user.getId(), workDTO);

		workDTO.setScheduleFromString("2015-09-11T06:00:00Z");
		Work work3 = workFacadeService.saveOrUpdateWork(user.getId(), workDTO);

		workIndexer.reindexById(Sets.newHashSet(work.getId(), work2.getId(), work3.getId()));

		//Successful Request
		WorkSearchRequest request = new WorkSearchRequest();
		request.setDateRange(targetRange);

		WorkSearchResponse workSearchResponse = null;

		stopWatch.start();
		while(workSearchResponse == null) {
			if(stopWatch.getTotalTimeMillis() > TIMEOUT_TIME) {
				stopWatch.stop();
				assertNotNull("Timeout error", workSearchResponse);
			}
			workSearchResponse = workSearchService.searchAllWorkByUserId(user.getId(), request);
		}
		stopWatch.stop();
		assertTrue(3 <= workSearchResponse.getResults().size());

	}

	private void addWorkDTODetails(WorkDTO workDTO) {
		workDTO.setIsOnsiteAddress(false);
		workDTO.setPricingStrategyId(FLAT_PRICING_STRATEGY);
		workDTO.setFlatPrice(100.00);
		workDTO.setIsScheduleRange(false);
		workDTO.setScheduleFromString("2010-09-02T09:00:00Z");
		workDTO.setIvrActive(true);
		workDTO.setCheckinCallRequired(true);
	}
}
