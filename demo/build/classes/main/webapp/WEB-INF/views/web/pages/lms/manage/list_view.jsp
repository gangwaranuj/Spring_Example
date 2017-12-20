<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="wmfn" uri="http://www.workmarket.com/taglib/functions" %>
<%@ taglib prefix="wm" tagdir="/WEB-INF/tags" %>

<wm:app pagetitle="Training" bodyclass="lms" breadcrumbSection="Market" breadcrumbSectionURI="/search" breadcrumbPage="Training" webpackScript="lms">

	<script>
		var config = {
			mode: 'manageList',
			type: 'test'
		};
	</script>

	<sec:authorize access="hasRole('PERMISSION_ASSESSMENTS')" var="hasAssessmentPermission" />

	<c:import url="/WEB-INF/views/web/partials/general/notices.jsp">
		<c:param name="bundle" value="${bundle}" />
	</c:import>

	<div class="inner-container dn" id="tests_container">
		<div class="page-header">
			<strong><a class="button pull-right" href="/lms/manage/step1">New Test</a></strong>
			<h3>Tests
				<small class="meta">
					<span class="selected-view">List View</span> |
					<a href="<c:url value="/lms/manage?ref=gridview"/>">Grid View</a>
				</small>
			</h3>
		</div>

		<div class="alert alert-info">
			Tests can provide you a means to qualify workers for talent pools or assignments.
			You can customize tests to your specific needs and include as a requirement to join talent pools or take on specific assignments.
			<strong><a href="https://workmarket.zendesk.com/hc/en-us/articles/209336528" target="_blank">Learn More <i class="icon-info-sign"></i></a></strong>
		</div>

		<div class="row">
			<div class="table-center">
				<table id="assessments_list" class="group-list" style="width: 100%;">
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
			</div>
		</div>
	</div>

	<div id="tests_hero" class="hero-unit well-b2 dn">
		<h2>Tests</h2>
		<p>are useful in a few different ways. Primarily tests are used as a screening tool and as an educational tool.
			As a screening tool, you can build a test  that can be placed as a  requirement to join a Talent Pool or you can use Requirement Sets and place a test as a prerequisite to accept an assignment.
			We believe that confirming skills and knowledge is an important process that drives high quality work engagements.  As an educational tool tests can be very effective as well.  As our world evolves new skills are required to successfully complete work.  You can embed videos and documents into a test and then ask questions related to its content.
			The power to educate your curated Talent Pools is a very powerful way to drive quality and engagement into your workflows.
			<br/>
			<br/>
			Tests can be set as private or public.  Public tests will be viewable by registered Work Market users.  You can Invite people to private tests.

			<a style="text-decoration: none;" href="http://help.workmarket.com/customer/portal/articles/888606-make-tests-video-" target="_blank"> Learn more <i class="icon-info-sign"></i></a>
		</p>
		<p>
			<a class="button" href="/lms/manage/step1">Create a Test</a>
		</p>
	</div>

	<script id="cell-name-tmpl" type="text/x-jquery-tmpl">
		<div>
			<strong><a href="/lms/view/details/\${meta.id}">\${data}</a></strong>
		</div>
	</script>

	<script id="cell-user-tmpl" type="text/x-jquery-tmpl">
		<div>
			<a href="/profile/\${meta.user_number}">\${data}</a>
		</div>
	</script>

	<script id="cell-activate-tmpl" type="text/x-jquery-tmpl">
		<div>
			{{if meta.status == 'active'}}
			<a data-action="deactivate" class="tooltipped tooltipped-n" aria-label="Currently Active">
				<i class="icon-off muted icon-large icon-green" data-id="\${meta.id}"></i>
			</a>
			{{else}}
			<a data-action="activate" class="tooltipped tooltipped-n" aria-label="Currently Inactive">
				<i class="icon-off icon-red icon-large power-off" data-id="\${meta.id}"></i>
			</a>
			{{/if}}
		</div>
	</script>

	<script id="cell-edit-tmpl" type="text/x-jquery-tmpl">
		<div>
			<a href="/lms/manage/step1/\${meta.id}" class="tooltipped tooltipped-n" aria-label="Edit">
				<i class="wm-icon-edit icon-large muted"></i>
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
