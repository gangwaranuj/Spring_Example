<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="wmfn" uri="http://www.workmarket.com/taglib/functions" %>
<%@ taglib prefix="wmfmt" uri="http://www.workmarket.com/taglib/fmt" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<div class="fill">
	<div id="navbar" class="navbar navbar-inverse navbar-fixed-top">
		<div class="navbar-inner">
			<div class="container-fluid" style="<c:if test="${minwidth}">min-width:<c:out value="${minwidth}"/>;</c:if>">
				<a class="brand" href="/admin"><fmt:message key="admin_upgrade.internal" /></a>

				<ul class="nav" role="navigation">
					<sec:authorize access="hasAnyRole('ROLE_WM_ATS,ROLE_WM_ADMIN')">
					<li class="dropdown">
						<a href="#" class="dropdown-toggle" data-toggle="dropdown"><fmt:message key="global.recruiting" /> <b class="caret"></b></a>
						<ul class="dropdown-menu" role="menu">
							<li><a href="https://app.greenhouse.io/alljobs"><fmt:message key="admin_upgrade.greenhouse_dashboard" /></a></li>
							<li><a href="https://app.greenhouse.io/people?sort=last_activity+desc&stage_status_id=2&type=all"><fmt:message key="admin_upgrade.active_candidates" /></a></li>
							<li><a href="https://www.workmarket.com/jobs/#job-table-header"><fmt:message key="admin_upgrade.public_job_board" /></a></li>
						</ul>
					</li>
					</sec:authorize>
					<sec:authorize access="hasAnyRole('ROLE_WM_CRM,ROLE_WM_ADMIN')">
						<li><a href="${sugarUrl}"><fmt:message key="admin_upgrade.sugarCRM" /></a></li>
					</sec:authorize>

					<sec:authorize access="hasAnyRole('ROLE_WM_EMPLOYEE_MGMT,ROLE_WM_ADMIN')">
					<li><a href="/admin/usermanagement/index"><fmt:message key="global.employees" /></a></li>
					</sec:authorize>
					<li><a href="/home"><fmt:message key="admin_upgrade.internal" /></a></li>
				</ul>

				<sec:authorize access="!hasRole('ROLE_ANONYMOUS')">
					<ul class="nav pull-right">
						<li class="dropdown">
							<a id="profile-btn" class="dropdown-toggle dropdown-toggle-user" data-toggle="dropdown" href="/profile?ref=navTop" role="button">
								<i class="wm-icon-gear" style="display: inline-flex;"></i>
								<b class="caret"></b>
							</a>
							<ul class="dropdown-menu">
								<li style="padding: 0px 15px 4px 15px;">
									<span class="profile-email overflow">${currentUser.email}</span>
								</li>
								<li class="divider"></li>
								<li><a href="/profile"><fmt:message key="global.profile" /></a></li>
								<li class="divider"></li>
								<li><a href="/logout"><fmt:message key="global.sign_out" /></a></li>
							</ul>
						</li>
					</ul>
				</sec:authorize>
			</div>
		</div>
	</div>
</div>
