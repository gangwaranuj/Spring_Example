<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="vr" uri="http://www.workmarket.com/taglib/velvet-rope" %>

<div class="well-b2">
	<h3>Profile Settings</h3>
	<div class="well-content">
		<ul class="stacked-nav">

			<li><a href="/profile-edit">Contact Information</a></li>
			<li><a href="/profile-edit/photo">Profile Photo</a></li>
			<li><a href="/profile-edit/qualifications">Job Title And Skills</a></li>

			<%--Steve ?--%>
			<c:if test="${currentUser.buyer and currentUser.seller}">
				<li><a href="/profile-edit/calendar_sync">Sync to Calendar</a></li>
			</c:if>
			<c:if test="${not currentUser.ssoUser}">
				<li><a href="/mysettings/password">Password Change</a></li>
			</c:if>
			<li><a href="/mysettings/notifications">Notifications Settings</a></li>
		</ul>
	</div>
</div>

<div class="well-b2">
	<h3>Additional Background</h3>
	<div class="well-content">
		<ul class="stacked-nav">
			<li><a href="/profile-edit/employment">Overview &amp; Employment</a></li>
			<li><a href="/profile-edit/rates_locations">Rates &amp; Travel</a></li>
			<li><a href="/mysettings/hours">Working Hours</a></li>
			<li><a href="/profile-edit/certifications">Certifications</a></li>
			<li><a href="/profile-edit/licenses">Licenses</a></li>

			<vr:rope>
				<vr:venue name="HIDE_PROF_INSURANCE" bypass="true">
					<sec:authorize access="hasFeature('screening-bg')">
						<li><a href="/profile-edit/insurance">Insurance</a></li>
					</sec:authorize>
				</vr:venue>
			</vr:rope>

			<li><a href="/profile-edit/languages">Languages</a></li>
			<li><a href="/profile-edit/education">Education</a></li>

			<vr:rope>
				<vr:venue name="HIDE_BG_CHECKS" bypass="true">
					<sec:authorize access="hasFeature('screening-bg')">
						<li><a href="/screening/bkgrnd">Background Check</a></li>
					</sec:authorize>
				</vr:venue>
			</vr:rope>

			<vr:rope>
				<vr:venue name="HIDE_DRUG_TESTS" bypass="true">
					<sec:authorize access="hasFeature('screening-drug')">
						<li><a href="/screening/drug">Drug Test</a></li>
					</sec:authorize>
				</vr:venue>
			</vr:rope>

			<li><a href="/profile-edit/industries">Industries</a></li>
			<li><a href="/profile-edit/specialties">Products</a></li>
			<li><a href="/profile-edit/tools">Tools</a></li>
		</ul>
	</div>
</div>

