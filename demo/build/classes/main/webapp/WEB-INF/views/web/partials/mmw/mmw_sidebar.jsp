<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="vr" uri="http://www.workmarket.com/taglib/velvet-rope" %>

<c:if test="${isLocaleEnabled}">
	<div class="sidebar-card">
		<h3 class="sidebar-card--title"><a href="/settings/manage/language">My Preferences</a></h3>
		<div>
			<ul>
				<li>Language</li>
				<li>Formats</li>
			</ul>
		</div>
	</div>
</c:if>

<c:if test="${currentUser.buyer}">
	<div class="sidebar-card">
		<h3 class="sidebar-card--title">Assignments</h3>
		<div>
			<ul>
				<sec:authorize access="hasAnyRole('ACL_ADMIN')">
					<li><a href="/settings/manage">Assignment Settings</a></li>
				</sec:authorize>
				<sec:authorize access="hasAnyRole('ACL_ADMIN','ACL_MANAGER')">
					<li><a href="/settings/manage/customfields">Custom Fields</a></li>
					<li><a href="/settings/manage/templates">Templates</a></li>
					<li><a href="/settings/manage/labels">Labels</a></li>
					<li><a href="/settings/manage/requirement_sets">Requirement Sets</a></li>
				<sec:authorize access="hasFeature('MultipleApprovals')">
					<li><a href="/settings/manage/approvals">Approvals</a></li>
				</sec:authorize>
					<%--Potentially could come back in Q3 2017--%>
					<%--<li><a href="/settings/manage/feed">Company Feed</a></li>--%>
				</sec:authorize>
			</ul>
		</div>
	</div>
</c:if>

<sec:authorize access="hasAnyRole('ACL_ADMIN')">
	<div class="sidebar-card">
		<h3 class="sidebar-card--title">Company</h3>
		<div>
			<ul>
				<li><a href="/account">Company Overview</a></li>
				<c:if test="${currentUser.buyer}">
					<li><a href="/mmw/subscription">Subscription Info</a></li>
				</c:if>
				<li><a href="/users">Employees</a></li>
				<li><a href="/account/tax">Tax Info</a></li>
				<li><a href="/mmw/taxes">Tax Documentation</a></li>
				<c:if test="${currentUser.buyer}">
					<li><a href="/filemanager">File Manager</a></li>
					<li><a href="/agreements">Agreements</a></li>
					<li><a href="/mmw/security">Security Access</a></li>
					<li><a href="/relationships/blocked_resources">Blocked Workers</a></li>
					<li><a href="/settings/manage/compliance_rule_sets">Compliance Rules</a></li>
				</c:if>
				<c:if test="${currentUser.seller || currentUser.dispatcher || currentUser.buyer}">
					<li><a href="/relationships/blocked_clients">Blocked Companies</a></li>
				</c:if>
			</ul>
		</div>
	</div>
</sec:authorize>

<sec:authorize access="hasAnyRole('ACL_ADMIN')">
	<c:if test="${currentUser.buyer}">
		<div class="sidebar-card">
			<h3 class="sidebar-card--title">Payments</h3>
			<div>
				<ul>
					<li><a href="/settings/manage/paymenterms">Payment Settings</a></li>
					<li><a href="/funds/invoice">Invoice Generator</a></li>
					<li><a href="/settings/alerts">Low Balance Alerts</a></li>
				</ul>
			</div>
		</div>
		<div class="sidebar-card">
			<h3 class="sidebar-card--title">Platform Integration</h3>
			<div>
				<ul>
					<li><a href="/mmw/api">API Access</a></li>
					<vr:rope>
						<vr:venue name="WEBHOOKS">
							<li><a href="/mmw/integration/webhooks">Webhooks <span class="label warning label-warning">BETA</span></a></li>
						</vr:venue>
					</vr:rope>
					<vr:rope>
						<vr:venue name="SALESFORCE_WEBHOOKS">
							<li><a href="/mmw/integration/salesforce">Salesforce <span class="label warning label-warning">BETA</span></a></li>
						</vr:venue>
					</vr:rope>
					<li><a href="/mmw/integration/autotask">Autotask</a></li>
					<vr:rope>
						<vr:venue name="SINGLE_SIGN_ON">
							<li><a href="/mmw/sso">Single Sign On</a></li>
						</vr:venue>
					</vr:rope>
				</ul>
			</div>
		</div>
	</c:if>
	<div class="sidebar-card">
		<h3 class="sidebar-card--title">Work Market Information</h3>
		<div>
			<ul>
				<li><a href="/download/WM-insurance-certification-v8.pdf" target="_blank">Certificate of Insurance</a></li>
				<li><a href="/download/workmarket-w9-2016.pdf" target="_blank">Work Market W9</a></li>
			</ul>
		</div>
	</div>
</sec:authorize>
