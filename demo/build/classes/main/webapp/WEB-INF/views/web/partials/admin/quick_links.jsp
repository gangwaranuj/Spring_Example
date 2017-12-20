<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>

<div class="well">
	<div class="page-header">
		<h5>WM Network</h5>
	</div>
	<ul class="unstyled">
		<sec:authorize access="hasAnyRole('ROLE_MASQUERADE,ROLE_WM_ADMIN')">
			<li><a href="/admin/usermanagement/masquerade">Masquerade</a></li>
		</sec:authorize>
		<sec:authorize access="hasAnyRole('ROLE_WM_USERCO_SEARCH,ROLE_WM_ADMIN')">
			<li><a href="/admin/manage/company/search">Company Search</a></li>
			<li><a href="/admin/manage/users/recent">User Search</a></li>
		</sec:authorize>
	</ul>

	<div class="page-header">
		<h5>Software Support</h5>
	</div>
	<ul class="unstyled">
		<sec:authorize access="hasAnyRole('ROLE_WM_QUEUES,ROLE_WM_ADMIN')">
			<li><a href="/admin/concerns">Reported Concerns</a></li>
			<li><a href="/admin/locks">Locked Companies</a></li>
			<li><a href="/admin/manage/users/suspended">Suspended Users</a></li>
			<li><a href="/admin/beta_features/beta_features">Beta features</a></li>
			<li><a href="/admin/features/feature_toggles">Developer feature toggles</a></li>
			<li><a href="/admin/plans">Plans</a></li>
			<hr/>
			<li><a href="/admin/manage/users/pending">WM-DB Requests</a></li>
			<li><a href="/admin/licenses/review">Licenses</a></li>
			<li><a href="/admin/certifications/review">Certifications</a></li>
			<li><a href="/admin/insurance/review">Insurance</a></li>
			<li><a href="/admin/tags/review_tools">Tools</a></li>
			<li><a href="/admin/tags/review_skills">Skills</a></li>
			<li><a href="/admin/tags/review_specialties">Products</a></li>

			<sec:authorize access="hasAnyRole('ROLE_WM_ACCOUNTING,ROLE_WM_ADMIN')">
				<li><a href="/admin/screening">Screened Users</a></li>
				<li><a href="/admin/manage/screenings/bkgrnd/queue">Background Checks</a></li>
			</sec:authorize>

			<%--Will place in same Account circle once Drug Test Results are processed automagically--%>
			<li><a href="/admin/manage/screenings/drug/queue">Drug Tests</a></li>
			<hr/>
		</sec:authorize>
		<li><a href="http://help.workmarket.com/admin/index.php" target="_blank">Manage Help</a></li>
		<li><a href="https://secure.snapengage.com/widget" target="_blank">Chat Admin</a></li>
		<li><a href="https://docs.google.com/a/workmarket.com/spreadsheet/viewform?formkey=dFVzWjJSLUxkYmgyQnBvSDlZWGJ5TWc6MQ#gid=0" target="_blank">Recruiting Request</a></li>

	</ul>
	<sec:authorize access="hasAnyRole('ROLE_WM_ACCOUNTING')">
		<hr/>
		<div class="page-header">
			<h5><a href="/admin/accounting">Accounting</a></h5>
		</div>

		<div class="page-header">
			<h5>Employee Tools and Documents</h5>
			<ul class="unstyled">
				<li><a href="https://spreadsheets.google.com/a/workmarket.com/ccc?key=0Ap-IUzrJ9b9RdEhnS1RYWjJxb01TRnpKQUV1cEdZcFE&hl=en" target="_blank">Global Phone List</a></li>
				<li><a href="https://www.hrpassport.com/Link2HR.eng?/Saf/Entry/Signon-form.htm" target="_blank">HR - Trinet</a></li>
				<li><a href="https://docs.google.com/a/workmarket.com/document/d/14RspcEZQvAMMLiloodzzr2fmsWle5-YZz-8P2tM6SBA/edit" target="_blank">Acceptable Use Policies</a></li>
				<li><a href="https://docs.google.com/a/workmarket.com/document/d/1_FNZD4RL7Xy7l0OKX2jGmvckBpfKmGUCDnXRZDESFtc/edit" target="_blank">Customer Privacy and Information Security Policy</a></li>
				<li><a href="https://docs.google.com/a/workmarket.com/document/d/1X_0zvxXc0RQYJvXFtm6CdNYMXqYO0eNvCFyOOp07Ef8/edit" target="_blank">Accounting and HR Process Doc</a></li>
				<li><a href="https://docs.google.com/document/d/1sDegDrierYD3nkGaXVqtd5O22fFkHl2hA9Xs1Q2NUd8/edit" target="_blank">WM New Hire Onboarding Doc</a></li>
				<li><a href="https://docs.google.com/a/workmarket.com/folder/d/0B73HL6G4Ej0nb0pYSFRaNEJDR0U/edit" target="_blank">401k Documents</a></li>
				<li><a href="https://docs.google.com/a/workmarket.com/document/d/1VmuGD6heI-yfvaRzmKXkNy0WsqqTRHwpuTCu7EkrJ2A/edit" target="_blank">Long Island Office Information</a></li>
				<li><a href="https://docs.google.com/document/d/14KKCjyvZhSy9xGAORAiUwi55iyl9abyOsojB-P3UkqI/edit" target="_blank">NYC Office Information</a> </li>
			</ul>
		</div>
	</sec:authorize>

	<sec:authorize access="hasAnyRole('ROLE_WM_SUBS_REPORTS','ROLE_WM_SUBS_APPROVE')">
		<div class="page-header">
			<h5>Subscription</h5>
		</div>
		<ul class="unstyled">
			<sec:authorize access="hasRole('ROLE_WM_SUBS_APPROVE')">
				<li><a href="/admin/manage/subscriptions/approval">Approval Queue</a></li>
			</sec:authorize>
			<li><a href="/admin/manage/subscriptions/reporting/standard">Subscription Reporting</a></li>
		</ul>
	</sec:authorize>

	<div class="page-header">
		<h5>Marketing</h5>
	</div>
	<ul class="unstyled">
		<li><a href="/admin/marketing">Marketing Home</a></li>
		<sec:authorize access="hasFeature('marketingAdmin')">
			<li><a href="/admin/bullhorn">Bullhorn Message</a></li>
		</sec:authorize>
		<li><a href="//blog.workmarket.com">Work Market Blog</a></li>
		<li><a href="/admin/marketing/indeed">Indeed</a></li>
	</ul>

	<sec:authorize access="hasAnyRole('ROLE_WM_GENERAL,ROLE_WM_ADMIN')">
		<div class="page-header">
			<h5>General</h5>
		</div>
		<ul class="unstyled">
			<li><a href="/admin/reporting">KPIs</a></li>
			<li><a href="/realtime/admin">WorkMarket Realtime</a></li>
			<li><a href="/admin/styleguide">Style Guide</a></li>
			<li><a href="https://workmarket.zendesk.com/hc/en-us" target="_blank">WM Help</a></li>
			<li><a href="http://www.google.com/analytics/" target="_blank">Google Analytics</a></li>
			<li><a href="/admin/background_image">Background Image</a></li>
		</ul>
	</sec:authorize>

	<div class="page-header">
		<h5>Forums</h5>
	</div>
	<ul class="unstyled">
		<li><a href="/admin/forums/posts">Posts</a></li>
		<li><a href="/admin/forums/flagged">Flagged Posts</a></li>
		<li><a href="/admin/forums/banned">Banned Users</a></li>
	</ul>

	<div class="page-header">
		<h5>Technical</h5>
	</div>
	<ul class="unstyled">
		<li><a href="/admin/buildinfo">Build Info</a></li>
		<li><a href="/admin/endpoints">Endpoints</a></li>

	</ul>

</div>
