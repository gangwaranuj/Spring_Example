<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="wm-csrf" uri="http://www.workmarket.com/taglib/csrf" %>
<%@ taglib prefix="wmfmt" uri="http://www.workmarket.com/taglib/fmt" %>
<%@ taglib prefix="wm" tagdir="/WEB-INF/tags" %>

<wm:app pagetitle="Learning Center" bodyclass="lms" breadcrumbSection="Market" breadcrumbSectionURI="/surveys" breadcrumbPage="Take Surveys" webpackScript="lms">

	<script>
		var config = {
			mode: 'manageSurveys'
		};
	</script>


	<div class="page-header clear">
		<h2 class="pull-left">Surveys</h2>
		<sec:authorize access="hasRole('PERMISSION_ASSESSMENTS')" var="hasAssessmentPermission">
			<strong><a class="pull-right button -small" href="<c:url value="/lms/manage/surveys"/>">Manage Surveys</a></strong>
		</sec:authorize>
	</div>

	<div class="row_wide_sidebar_left">
		<div class="sidebar">
			<form action="/" method="post" id="assessments_filter_form" accept-charset="utf-8">
				<wm-csrf:csrfToken />
				<div class="well-b2">
					<h3>Survey Filters</h3>
					<div class="well-content">
						<ul class="unstyled">
							<li><input type="checkbox" name="filters[status_taken]" value="1" /> Complete</li>
							<li><input type="checkbox" name="filters[status_invited]" value="1" checked="checked" /> Invited to take</li>
							<li><input type="checkbox" name="filters[status_not_taken]" value="1" checked="checked" /> Not taken</li>
							<li><input type="checkbox" name="filters[status_inprogress]" value="1" checked="checked" /> In-progress</li>
						</ul>
					</div>
				</div>
			</form>
		</div>
		<div class="content">
			<jsp:include page="/WEB-INF/views/web/partials/message.jsp" />

			<table id="surveys_worker_list" class="group-list">
				<thead>
					<tr>
						<th width="40%">Name</th>
						<th width="30%">Company</th>
						<th>Status</th>
					</tr>
				</thead>
				<tbody></tbody>
			</table>
		</div>
	</div>

	<script id="cell-name-tmpl" type="text/x-jquery-tmpl">
		<div>
			<a href="/lms/view/details/\${meta.id}">\${data}</a>
		</div>
	</script>

	<script id="cell-status-tmpl" type="text/x-jquery-tmpl">
		<div>
			{{if meta.attempt_status == 'complete'}}
				<span class="tooltipped tooltipped-n" aria-label="Completed on \${meta.completed_on}">\${meta.is_passed ? 'Passed' : 'Failed'}</span>
			{{else meta.attempt_status == 'inprogress'}}
				<a href="/lms/view/take/\${meta.id}">Continue</a>
			{{else}}
				<a href="/lms/view/take/\${meta.id}">Take</a>
			{{/if}}
		</div>
	</script>

</wm:app>
