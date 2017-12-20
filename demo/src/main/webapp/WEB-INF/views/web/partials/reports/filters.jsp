<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<form:form commandName="filterForm" id="report_filters" method="get">
	<form:hidden path="buyerReport" />

	<fieldset class="form-stacked" id="advanced-filters">
		<div class="row">
			<c:if test="${filterForm.buyerReport}">
				<form:hidden path="budgetReport" />
				<div class="span4">
					<div class="control-group">
						<label for="status_filter" class="control-label">Status</label>
						<div class="controls">
							<form:select path="filters.status" items="${statuses}" id="status_filter" class="wm-select" multiple="multiple" />
						</div>
					</div>

					<div class="control-group">
						<label for="substatus_filter" class="control-label">Substatus</label>
						<div class="controls">
							<form:select path="filters.subStatus" items="${subStatuses}" id="substatus_filter" class="wm-select" multiple="multiple" />
						</div>
					</div>


					<c:if test="${!filterForm.budgetReport && canViewAllData}">
						<div class="control-group">
							<label for="owner_filter" class="control-label">Internal Owner</label>
							<div class="controls">
								<form:select path="filters.owner" id="owner_filter" cssClass="w150 gap">
									<form:option value="">All</form:option>
									<form:options items="${users}" />
								</form:select>
							</div>
						</div>
					</c:if>
				</div>
			</c:if>
			<c:if test="${!filterForm.assignmentFeedbackReport}">
				<div class="span4">
					<div class="control-group">
						<label for="client_filter" class="control-label">Client</label>
						<div class="controls">
							<form:select path="filters.client" id="client_filter" cssClass="w150 gap">
								<form:option value="">All</form:option>
								<form:options items="${clients}" />
							</form:select>
						</div>
					</div>

					<div class="control-group">
						<label for="project_filter" class="control-label">Project</label>
						<div class="controls">
							<form:select path="filters.project" id="project_filter" cssClass="w150 gap">
								<form:option value="">All</form:option>
								<form:options items="${projects}" />
							</form:select>
						</div>
					</div>

					<div class="control-group">
						<label for="resource_type_filter" class="control-label">Worker Type</label>
						<div class="controls">
							<form:select path="filters.resourceType" id="resource_type_filter" cssClass="w150 gap">
								<form:option value="">All</form:option>
								<form:options items="${lanes}" />
							</form:select>
						</div>
					</div>
				</div>
			</c:if>
			<div class="span5">
				<c:choose>
					<c:when test="${filterForm.transactionsReport}">
						<div class="control-group">
							<label for="from_date_filter" class="control-label">Transaction Date Range <a href="javascript:void(0);" class="tooltip" title="Transaction date is paid date for assignments; for other transactions, it is the date the transaction posted to your account."><i class="wm-icon-question-filled"></i></a></label>
							<div class="controls">
								<form:input path="filters.transaction_date_from" cssClass="small datepicker" cssStyle="width:80px;" />
								to
								<form:input path="filters.transaction_date_to" cssClass="small datepicker mr" cssStyle="width:80px;" />
							</div>
						</div>

						<div class="control-group">
							<label for="from_date_filter" class="control-label">Assignment Scheduled Range</label>
							<div class="controls">
								<form:input path="filters.assignment_scheduled_date_from" cssClass="small datepicker" cssStyle="width:80px;" />
								to
								<form:input path="filters.assignment_scheduled_date_to" cssClass="small datepicker mr" cssStyle="width:80px;" />
							</div>
						</div>

						<div class="control-group">
							<label for="from_date_filter" class="control-label">Assignment Approved Range</label>
							<div class="controls">
								<form:input path="filters.assignment_approved_date_from" cssClass="small datepicker" cssStyle="width:80px;" />
								to
								<form:input path="filters.assignment_approved_date_to" cssClass="small datepicker mr" cssStyle="width:80px;" />
							</div>
						</div>
					</c:when>
					<c:otherwise>
						<div class="control-group">
							<c:choose>
								<c:when test="${filterForm.assignmentFeedbackReport}">
									<label for="from_date_filter" class="control-label">Assignment Paid Date Range</label>
								</c:when>
								<c:otherwise>
									<label for="from_date_filter" class="control-label">Assignment Scheduled Range</label>
								</c:otherwise>
							</c:choose>
							<div class="controls">
								<form:input path="filters.from_date" id="from_date_filter" cssClass="small datepicker" cssStyle="width:80px;" />
								to
								<form:input path="filters.to_date" id="to_date_filter" cssClass="small datepicker mr" cssStyle="width:80px;" />
							</div>
						</div>

						<c:if test="${!filterForm.assignmentFeedbackReport}">
							<div class="control-group">
								<label for="from_date_filter">Assignment Approved Range</label>
								<div class="controls">
									<form:input path="filters.assignment_approved_date_from" cssClass="small datepicker" cssStyle="width:80px;" />
									to
									<form:input path="filters.assignment_approved_date_to" cssClass="small datepicker mr" cssStyle="width:80px;" />
								</div>
							</div>

							<div class="control-group">
								<label for="from_date_filter" class="control-label">Assignment Paid Range</label>
								<div class="controls">
									<form:input path="filters.assignment_paid_date_from" cssClass="small datepicker" cssStyle="width:80px;" />
									to
									<form:input path="filters.assignment_paid_date_to" cssClass="small datepicker mr" cssStyle="width:80px;" />
								</div>
							</div>
						</c:if>
					</c:otherwise>
				</c:choose>
			</div>

			<div class="span4">
				<c:if test="${!filterForm.assignmentFeedbackReport}">
					<c:if test="${filterForm.standardReport && !filterForm.budgetReport}">
						<div class="control-group">
							<label for="from_price_filter" class="control-label">Budgeted Spend</label>
							<div class="controls">
								<form:input path="filters.from_price" id="from_price_filter" cssClass="small" cssStyle="width:60px;" placeholder="0.00" />
								to
								<form:input path="filters.to_price" id="to_price_filter" cssClass="small mr" cssStyle="width:60px;" placeholder="0.00" />
							</div>
						</div>
					</c:if>

					<div class="control-group">
						<label class="control-label">Custom Fields</label>
						<div class="controls">
							<form:checkbox path="includeCustomFields" value="1" id="include_custom_fields" />
							<span>Load custom fields</span>
						</div>
					</div>
				</c:if>
			</div>
		</div>
	</fieldset>
</form:form>
