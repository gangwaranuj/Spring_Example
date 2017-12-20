package com.workmarket.dao.screening;

import com.workmarket.dao.PaginatableDAOInterface;
import com.workmarket.domains.model.screening.BackgroundCheck;
import com.workmarket.domains.model.screening.DrugTest;
import com.workmarket.domains.model.screening.ScreenedUserPagination;
import com.workmarket.domains.model.screening.Screening;
import com.workmarket.domains.model.screening.ScreeningPagination;
import com.workmarket.reporting.model.EvidenceReport;

import java.util.List;

public interface ScreeningDAO extends PaginatableDAOInterface<Screening> {
	public Screening findByScreeningId(String screeningId);

	public BackgroundCheck findMostRecentBackgroundCheck(Long userId);
	public BackgroundCheck findPreviousPassedBackgroundCheck(Long userId);
	public List<BackgroundCheck> findBackgroundChecksByUser(Long userId);
	public List<BackgroundCheck> findBackgroundChecksByUserAndStatus(Long userId, String status);
	public ScreeningPagination findBackgroundChecksByStatus(String status, ScreeningPagination pagination);
	
	public DrugTest findMostRecentDrugTest(Long userId);
	public DrugTest findPreviousPassedDrugTest(Long userId);
	public List<DrugTest> findDrugTestsByUser(Long userId);
	public List<DrugTest> findDrugTestsByUserAndStatus(Long userId, String status);
	public ScreeningPagination findDrugTestsByStatus(String status, ScreeningPagination pagination);
	
	public ScreenedUserPagination findAllScreenedUsers(ScreenedUserPagination pagination);

	public List<EvidenceReport> findBulkEvidenceReportForUsers(List<Long> userIds, String screeningType);

	List<String> findScreeningUuids(List<Long> userIds);
	ScreenedUserPagination findAllScreenedUsersOnly(final ScreenedUserPagination pagination);
}