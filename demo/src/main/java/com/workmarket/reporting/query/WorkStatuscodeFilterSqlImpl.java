package com.workmarket.reporting.query;

import com.workmarket.domains.model.WorkStatusType;
import com.workmarket.domains.model.reporting.FieldValueFilter;
import com.workmarket.domains.model.reporting.Filter;
import com.workmarket.utility.DateUtilities;
import org.apache.commons.lang.StringUtils;

public class WorkStatuscodeFilterSqlImpl extends AbstractFilter {

	public <T extends Filter> void filter(T filter) throws Exception {
		FieldValueFilter fieldValueFilter = (FieldValueFilter) filter;
		if(StringUtils.isEmpty(fieldValueFilter.getFieldValue())) return;
		String[] workStatusTypes = fieldValueFilter.getFieldValue().split(",");
		StringBuffer inClause = new StringBuffer("");
		StringBuffer activeClause = new StringBuffer("");

		for(int i =0;i<workStatusTypes.length;i++){
			if(StringUtils.isEmpty(workStatusTypes[i])) continue;
			String workStatusType = workStatusTypes[i];
			if( i > 0 && !(WorkStatusType.ACTIVE_REPORTING_WORK_STATUS_TYPES.contains(workStatusType))) inClause.append(",");
			if (WorkStatusType.SIMPLE_REPORTING_WORK_STATUS_TYPES.contains(workStatusType)) {
				inClause.append("'" + workStatusType + "'");
			}else if (workStatusType.equals(WorkStatusType.PAYMENT_PENDING)){
				inClause.append("'paymentPending', 'invoiced', 'cancelledPayPending'");
			}else if (workStatusType.equals(WorkStatusType.CANCELLED)){
				inClause.append("'cancelled','cancelledPayPending','cancelledWithPay'");
			}else if (WorkStatusType.ACTIVE_REPORTING_WORK_STATUS_TYPES.contains(workStatusType)) {
				fieldValueFilter.getSqlBuilder().addParam("today", DateUtilities.formatTodayForSQL());
				if(activeClause.length() == 0){
					activeClause.append("work.work_status_type_code = 'active' AND (");
				}  else {
					activeClause.append(" OR ");
				}

				if (workStatusType.equals(WorkStatusType.ACTIVE)) {
					activeClause.append(" (IFNULL(assignedResource.checkedin_flag, false) = false");
					activeClause.append(" AND ( work.schedule_from > :today OR ( work.schedule_from <= :today AND (work.checkin_required_flag = true OR work.checkin_call_required = true) )))");
				} else {
					activeClause.append(" (assignedResource.checkedin_flag = true OR (work.checkin_required_flag = false AND work.checkin_call_required = false " +
							" AND IFNULL(assignedResource.checkedin_flag, false) = false AND work.schedule_from <= :today))");
				}
			}
		}
		String workStatusClause = null;
		if(StringUtils.isNotEmpty(inClause.toString())){
			workStatusClause = new String("work.work_status_type_code IN (" + inClause.toString() + ")");
		}
		if(StringUtils.isNotEmpty(activeClause.toString())){
			activeClause.append(")");
			if(StringUtils.isNotEmpty(workStatusClause)){
				workStatusClause = new String("((" + activeClause.toString() + ") OR (" + workStatusClause + "))");

			}else{
				workStatusClause = new String("(" + activeClause.toString() + ")");
			}
		}
		if(StringUtils.isNotEmpty(workStatusClause)){
			fieldValueFilter.getSqlBuilder().addWhereClause(workStatusClause);
		}
	}
}
