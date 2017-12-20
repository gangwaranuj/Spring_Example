<%@ tag description="App Page template" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="wmfmt" uri="http://www.workmarket.com/taglib/fmt" %>
<%@ taglib prefix="wm-csrf" uri="http://www.workmarket.com/taglib/csrf" %>
<%@ taglib prefix="wm" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="vr" uri="http://www.workmarket.com/taglib/velvet-rope" %>

<jsp:useBean id="date" class="java.util.Date"/>
<%@ attribute name="pagetitle" required="false" %>
<%@ attribute name="bodyclass" required="false" %>
<%@ attribute name="fluid" required="false" %>
<%@ attribute name="breadcrumbSection" required="false" %>
<%@ attribute name="breadcrumbSectionURI" required="false" %>
<%@ attribute name="breadcrumbPage" required="false" %>
<%@ attribute name="isOnboarding" required="false" %>
<%@ attribute name="isDashboard" required="false" %>
<%@ attribute name="webpackScript" required="false" %>
<%@ attribute name="isBootstrapDisabled" required="false" %>
<%@ attribute name="isOldNotificationDisabled" required="false" %>

<c:set var="pageTitle" value="${pagetitle}" scope="request"/>
<c:set var="bodyClass" value="${bodyclass}" scope="request"/>
<c:set var="fluid" value="${fluid}" scope="request"/>
<c:set var="breadcrumbSection" value="${breadcrumbSection}" scope="request"/>
<c:set var="breadcrumbSectionURI" value="${breadcrumbSectionURI}" scope="request"/>
<c:set var="breadcrumbPage" value="${breadcrumbPage}" scope="request"/>
<c:set var="email" value=""/>
<c:set var="fullName" value=""/>
<c:set var="userNumber" value=""/>
<c:set var="lang" value="${currentLocale}" />

<sec:authorize access="isAuthenticated()">
	<sec:authentication property="principal.email" var="email"/>
	<sec:authentication property="principal.fullName" var="fullName"/>
	<sec:authentication property="principal.userNumber" var="userNumber"/>
</sec:authorize>

<c:set var="shouldShowMDL" value="false"/>

<!DOCTYPE html>
<html lang="${lang}">
	<head>
		<meta charset="utf-8"/>
		<meta http-equiv="Expires" content="Mon, 26 Jul 1997 05:00:00 GMT"/>
		<meta http-equiv="Cache-Control" content="no-store, no-cache, must-revalidate"/>
		<meta http-equiv="Pragma" content="no-cache"/>
		<meta http-equiv="content-type" content="text/html; charset=utf-8"/>
		<meta http-equiv="content-language" content="${lang}"/>
		<meta name="author" lang="${lang}" content="Work Market, Inc"/>
		<meta name="copyright" lang="${lang}" content="<fmt:formatDate value="${date}" pattern="yyyy" />"/>
		<meta name="description" content="Hire local, on-site freelancers for any job! Our workforce staffing solutions help you find, manage and pay the best talent for any contractor job with our simple online service."/>
		<meta name="keywords" content="workmarket, work market, work, labor resource platform, Independent contractor, staffing, IT pro, Service provider, consultant, freelancer, temporary, temp, part time, helper, labor, remote freelancer, outsource, outsource to freelancers, hire temps, VMS,  managed services, rollout, install, printers, contract worker, W9, 1099, temporary staff, roll out, point of sale, pos, voip, voice over ip, flat panel hang, consumer electronics, computer,  networking, wireless router install, server setup, software, computer technician, technical support, tech support, it service, it outsource, it outsourcing, network support, network support technician, network technician, network repair, comptia, ccna, msce, it pro, it service pro, it service professional, it staffing"/>
		<meta name="robots" content="all,follow"/>
		<meta property="og:title" content="Work Market | Freelance Workforce Solutions for Finding & Managing Contractors"/>
		<meta property="og:site_name" content="workmarket.com"/>
		<meta name="google-site-verification" content="RkyCyPVvl9K4ZeOG_f21u9htXBl6BbY8vOHeJjPRIfo" />
		<meta name="csrf-token" content="<wm-csrf:csrfToken plainToken='true'/>"/>
		<meta name="userName" content="${currentUser.fullName}"/>
		<meta name="firstName" content="${currentUser.firstName}"/>
		<meta name="userEmail" content="${currentUser.email}"/>
		<meta name="userNumber" content="${currentUser.userNumber}"/>
		<meta name="companyNumber" content="${currentUser.companyNumber}"/>
		<meta name="companyUuid" content="${currentUser.companyUuid}"/>
		<meta name="companyName" content="${currentUser.companyName}"/>
		<meta name="companyId" content="${currentUser.companyId}"/>
		<meta name="isBuyer" content="${currentUser.isBuyer()}"/>
		<%-- Check for admin permission level (not internal) --%>
		<c:set var="isAdmin" value="${false}" />
		<c:set var="isManager" value="${false}" />
		<sec:authorize access="hasAnyRole('ACL_ADMIN')">
		  <c:set var="isAdmin" value="${true}" />
		</sec:authorize>
		<sec:authorize access="hasAnyRole('ACL_MANAGER')">
		  <c:set var="isManager" value="${true}" />
		</sec:authorize>
		<meta name="isAdmin" content="${isAdmin}"/>
		<meta name="isManager" content="${isManager}"/>
		<meta name="isDesktop" content="true"/>
		<meta name="viewport" content="width=device-width, initial-scale=1">

		<jsp:include page="/WEB-INF/views/web/partials/general/analytics/optimizely.jsp"/>
		<jsp:include page="/WEB-INF/views/web/partials/general/analytics/inspectlet.jsp"/>
		<jsp:include page="/WEB-INF/views/web/partials/general/analytics/segment.jsp"/>
		<jsp:include page="/WEB-INF/views/web/partials/general/analytics/trackjs.jsp"/>
		<jsp:include page="/WEB-INF/views/web/partials/context.jsp"/>

		<link href="//fonts.googleapis.com/css?family=Open+Sans:300italic,400italic,600italic,700italic,800italic,400,300,600,700,800|Roboto+Slab:300,400,700" rel="stylesheet" type="text/css">
		<link href="https://fonts.googleapis.com/icon?family=Material+Icons" rel="stylesheet"/>

		<link href="${mediaPrefix}/core.css" rel="stylesheet" type="text/css"/>

		<c:if test="${not isBootstrapDisabled}">
			<link href="${mediaPrefix}/bootstrap2.css" rel="stylesheet" type="text/css"/>
		</c:if>

		<script>
			var mediaPrefix = "${mediaPrefix}";
			var context = {
				name: "${not empty webpackScript ? webpackScript : "base"}",
				data: {
					shouldShowMDL: ${shouldShowMDL}
				},
				features: {}
			}
		</script>

		<title><c:if test="${not empty pageTitle}"><c:out value="${pageTitle}" escapeXml="false"/> - </c:if>Work Market</title>
	</head>

	<sec:authorize access="!hasRole('ROLE_ANONYMOUS')">
		<sec:authentication property="principal.companyIsLocked" var="companyIsLocked"/>
		<sec:authentication property="principal.companyHasLockWarning" var="companyHasLockWarning"/>
	</sec:authorize>

	<body class="${bodyClass} " id="${pageName}">
		<div class="left-nav app-nav <c:if test="${isNavPinnedOpen}"> open</c:if>" data-dropdown="dropdown">
			<jsp:include page="/WEB-INF/views/web/partials/navigation/app.jsp"/>
		</div>
		<div id="outer-container">
			<div class="main container<c:if test="${not empty fluid}">-fluid</c:if>">
			<c:if test="${not isOldNotificationDisabled}">
				<div class="wm-alert-center">
					<c:import url="/WEB-INF/views/web/partials/general/locked-messaging.jsp"/>
					<c:import url="/WEB-INF/views/web/partials/general/maintenance-messaging.jsp"/>
					<sec:authorize access="hasRole('ROLE_PREVIOUS_ADMINISTRATOR')">
						<jsp:include page="/WEB-INF/views/web/partials/general/masquerade.jsp"/>
					</sec:authorize>
				</div>
			</c:if>
				<jsp:doBody />
			</div>
		</div>

		<div class="site-footer">
			<div class="container-fluid">
				<ul class="site-footer--links">
					<li><wm:logo /></li>
					<li><a href="/about" target="_blank">About</a></li>
					<li><a href="https://blog.workmarket.com" target="_blank">Blog</a></li>
					<li><a href="/tos">Policies</a></li>
					<li><a href="/privacy">Privacy</a></li>
					<li><a href="mailto:support@workmarket.com">Feedback</a></li>
					<li><a href="/forums">Forums</a></li>
					<c:if test="${currentUser.isBuyer()}">
						<li><a href="https://community.workmarket.com" target="_blank">Community</a></li>
					</c:if>
					<li><a href="https://workmarket.zendesk.com/hc/en-us" target="_blank">Help Center</a></li>
					<li><a href="javascript:void(0);" class="chat-action">Chat with us</a></li>
					<c:if test="${requestScope.currentDevice.mobile}">
						<li><a href="/mobile?site_preference=mobile">Mobile Site</a></li>
					</c:if>
					<sec:authorize access="hasRole('ROLE_INTERNAL')">
						<li><a href="/admin">Admin Site</a></li>
						<li><a href="${sugarUrl}">SugarCRM</a></li>
					</sec:authorize>
				</ul>
				<div class="copyright">
					Work Market Logos, Site Design, and Content &copy; <fmt:formatDate value="${date}" pattern="yyyy" /> Work Market Inc. All rights reserved. Several aspects of the Work Market site are patent pending.
				</div>
			</div>
		</div>

		<script type="text/javascript">
			// Should be used exclusively for SnapEngage
			var UserNumber = "${currentUser.userNumber}";
			var UserName = "${currentUser.fullName}";

			(function() {
				var se = document.createElement('script'); se.type = 'text/javascript'; se.async = true;
				se.src = '//storage.googleapis.com/code.snapengage.com/js/5267e49b-5b37-4550-b9a5-4130fcff45be.js';
				var done = false;
				se.onload = se.onreadystatechange = function() {
					if (!done&&(!this.readyState||this.readyState==='loaded'||this.readyState==='complete')) {
						done = true;
						SnapEngage.hideButton();
						SnapEngage.setUserEmail("${wmfmt:escapeJavaScript(currentUser.email)}", true);
					}
				};
				var s = document.getElementsByTagName('script')[0]; s.parentNode.insertBefore(se, s);
			})();

			var chatActions = document.querySelectorAll('.chat-action');
			for (var i = 0; i < chatActions.length; i++) {
				chatActions[i].addEventListener('click', function () {
					SnapEngage.startChat('How can I help you today?');
				});
			}
		</script>

		<jsp:include page="/WEB-INF/views/web/partials/general/material-design-lite.jsp"/>

		<c:if test="${not empty manifestMap.get('manifest.js')}">
			<script src="${mediaPrefix}/builds/${manifestMap.get('manifest.js')}"></script>
		</c:if>
		<c:if test="${empty webpackScript}">
			<script src="${mediaPrefix}/builds/noJsEntryPoint.js"></script>
		</c:if>

		<c:if test="${not empty webpackScript}">
			<jsp:include page="/WEB-INF/views/web/partials/config/webpack.jsp?script=${webpackScript}" />
		</c:if>

		<c:if test="${empty webpackScript}">
			<jsp:include page="/WEB-INF/views/web/partials/general/temp-templates.jsp"/>
		</c:if>

		<jsp:include page="/WEB-INF/views/web/partials/general/analytics/uservoice.jsp"/>

	</body>
</html>
