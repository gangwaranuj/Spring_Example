package com.workmarket.dao.assessment;

import com.workmarket.dao.DAOInterface;
import com.workmarket.data.report.assessment.AttemptReportPagination;
import com.workmarket.data.report.assessment.AttemptReportRow;

public interface AttemptReportDAO extends DAOInterface<AttemptReportRow> {
	AttemptReportPagination generateReportForAssessment(Long assessmentId, AttemptReportPagination pagination);
}