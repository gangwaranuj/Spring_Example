<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="wmfn" uri="http://www.workmarket.com/taglib/functions" %>
<%@ taglib prefix="wm" tagdir="/WEB-INF/tags" %>

<wm:app pagetitle="Surveys" bodyclass="lms" breadcrumbSection="Market" breadcrumbSectionURI="/search" breadcrumbPage="Surveys" webpackScript="lms">

	<script>
		var config = {
			mode: 'manageList',
			type: 'survey'
		};
	</script>

	<sec:authorize access="hasRole('PERMISSION_ASSESSMENTS')" var="hasAssessmentPermission" />

	<div class="content">
		<div class="hero-unit well-b2 dn" id="survey_hero">
			<h2>Surveys</h2>
			<p>Surveys can be created and attached to assignments as a mandatory or optional requirement.
				If mandatory, the worker will need to complete the survey before they're given the option to submit the assignment for approval.
				Surveys are attached during the assignment creation process.<a class="learn-more" href="https://workmarket.zendesk.com/hc/en-us/articles/214799528" target="_blank"> Learn more <i class="icon-info-sign"></i></a>
			</p>
			<p>
				<a class="button" href="/lms/manage/step1/survey">Create a Survey</a>
			</p>
		</div>

		<div class="inner-container dn" id="survey_container">
			<c:import url="/WEB-INF/views/web/partials/general/notices.jsp">
				<c:param name="bundle" value="${bundle}" />
			</c:import>

			<div class="page-header">
				<strong><a class="button pull-right" href="/lms/manage/step1/survey">New Survey</a></strong>
				<h3>Surveys</h3>
			</div>

			<div class="alert alert-info">
				Surveys can be created and attached to assignments as a mandatory or optional requirement. If mandatory, the
				worker will need to complete the survey before they're given the option to submit the assignment for approval.
				Surveys are attached during the assignment creation process.
				<strong><a href="https://workmarket.zendesk.com/hc/en-us/articles/214799528" target="_blank">Learn More <i class="icon-info-sign"></i></a></strong>
			</div>

			<table id="surveys_list" class="group-list">
				<thead>
					<tr>
						<th>Name</th>
						<th>Owner</th>
						<th>Created Date</th>
						<th class="text-center">Active</th>
						<th class="text-center">Edit</th>
						<th class="text-center">Copy</th>
						<th class="text-center">Delete</th>
					</tr>
				</thead>
				<tbody id="lms_actions"></tbody>
			</table>

			<div class="table_templates_msg"></div>
		</div>
	</div>

	<script id="cell-name-tmpl" type="text/x-jquery-tmpl">
		<div>
			<strong><a href="/lms/view/details/\${meta.id}">\${data}</a></strong>
		</div>
	</script>

	<script id="cell-user-tmpl" type="text/x-jquery-tmpl">
		<div>
			<a target="_blank" href="/profile/\${meta.user_number}">\${data}</a>
		</div>
	</script>

	<script id="cell-activate-tmpl" type="text/x-jquery-tmpl">
		<div>
			{{if meta.status == 'active'}}
			<a data-action="deactivate" class="tooltipped tooltipped-n" aria-label="Currently Active">
				<i class="icon-off muted icon-large icon-green" data-id="\${meta.id}"></i>
			</a>
			{{else}}
			<a data-action="activate" class=" tooltipped tooltipped-n" aria-label="Currently Inactive">
				<i class="icon-off icon-red icon-large power-off" data-id="\${meta.id}"></i>
			</a>
			{{/if}}
		</div>
	</script>

	<script id="cell-edit-tmpl" type="text/x-jquery-tmpl">
		<div>
			<a href="/lms/manage/step1/\${meta.id}" class="tooltipped tooltipped-n" aria-label="Edit">
				<i class="wm-icon-edit icon-large muted" aria-label="Edit"></i>
			</a>
		</div>
	</script>

	<script id="cell-copy-tmpl" type="text/x-jquery-tmpl">
		<div>
			<a data-action="copy" class="tooltipped tooltipped-n" aria-label="Copy">
				<i class="wm-icon-copy icon-large muted" data-id="\${meta.id}"></i>
			</a>
		</div>
	</script>

	<script id="cell-delete-tmpl" type="text/x-jquery-tmpl">
		<div>
			<a data-action="delete" class="tooltipped tooltipped-n" aria-label="Delete">
				<i class="wm-icon-trash icon-large muted" data-id="\${meta.id}"></i>
			</a>
		</div>
	</script>

</wm:app>
