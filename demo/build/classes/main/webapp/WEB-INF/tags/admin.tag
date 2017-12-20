<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="wm-csrf" uri="http://www.workmarket.com/taglib/csrf" %>

<jsp:useBean id="date" class="java.util.Date"/>
<%@ attribute name="bodyclass" required="false" %>
<%@ attribute name="pagetitle" required="false" %>
<%@ attribute name="webpackScript" required="false" %>

<c:set var="pageTitle" value="${pagetitle}" scope="request"/>

<c:set var="uri" value="${requestScope['javax.servlet.forward.request_uri']}" />
<c:set var="lang" value="${currentLocale}" />

<!DOCTYPE html>
<html lang="${lang}">
	<head>
		<meta charset="utf-8"/>
		<meta http-equiv="Expires" content="Mon, 26 Jul 1997 05:00:00 GMT"/>
		<meta http-equiv="Cache-Control" content="no-store, no-cache, must-revalidate"/>
		<meta http-equiv="Pragma" content="no-cache"/>
		<meta http-equiv="Content-Type" content="text/html;charset=utf-8" />
		<meta http-equiv="content-language" content="${lang}"/>
		<meta name="author" lang="${lang}" content="Work Market, Inc"/>
		<meta name="copyright" lang="${lang}" content="<fmt:formatDate value="${date}" pattern="yyyy" />"/>
		<meta name="description" content="Work Market is a new venture, backed by Spark Capital and Union Square Ventures.  We are building a modular web platform, designed to power anyone or any business to efficiently deliver and manage labor and services"/>
		<meta name="keywords" content="workmarket, work market, work, labor resource platform, Independent contractor, staffing, IT pro, Service provider, consultant, freelancer, temporary, temp, part time, helper, labor, remote freelancer, outsource, outsource to freelancers, hire temps, VMS,  managed services, rollout, install, printers, contract worker, W9, 1099, temporary staff, roll out, point of sale, pos, voip, voice over ip, flat panel hang, consumer electronics, computer,  networking, wireless router install, server setup, software, computer technician, technical support, tech support, it service, it outsource, it outsourcing, network support, network support technician, network technician, network repair, comptia, ccna, msce, it pro, it service pro, it service professional, it staffing"/>
		<meta name="robots" content="all,follow"/>
		<meta property="og:title" content="Work Market"/>
		<meta property="og:site_name" content="workmarket.com"/>
		<meta name="google-site-verification" content="RkyCyPVvl9K4ZeOG_f21u9htXBl6BbY8vOHeJjPRIfo" />
		<meta name="csrf-token" content="<wm-csrf:csrfToken plainToken='true'/>"/>
		<meta name="viewport" content="width=device-width, initial-scale=1">
		<meta name="isDesktop" content="false"/>

		<link href="//fonts.googleapis.com/css?family=Open+Sans:300italic,400italic,600italic,700italic,800italic,400,300,600,700,800" rel="stylesheet" type="text/css">
		<link href="https://fonts.googleapis.com/icon?family=Material+Icons" rel="stylesheet">

		<jsp:include page="/WEB-INF/views/web/partials/general/analytics/trackjs.jsp"/>
		<jsp:include page="/WEB-INF/views/web/partials/context.jsp"/>

		<link href="${mediaPrefix}/core.css" rel="stylesheet" type="text/css"/>
		<link href="${mediaPrefix}/bootstrap2.css" rel="stylesheet" type="text/css"/>
		<link href="${mediaPrefix}/admin.css" rel="stylesheet" type="text/css"/>
		<script>
			var mediaPrefix = "${mediaPrefix}";
		</script>
		<c:if test="${empty webpackScript}">
			<script src="${mediaPrefix}/admin.js" type="text/javascript"></script>
			<jsp:include page="/WEB-INF/views/web/partials/general/temp-templates.jsp"/>
		</c:if>

		<title><c:if test="${not empty pageTitle}">${pageTitle} - </c:if>Work Market</title>
	</head>
	<body class="${bodyclass} admin" id="${pageName}">
		<div class="topbar admin" data-dropdown="dropdown">
			<c:import url="/WEB-INF/views/web/partials/navigation/admin-upgrade.jsp"/>
		</div>

		<div id="outer-container">
			<div class="main container-fluid">
				<c:import url="/WEB-INF/views/web/partials/general/maintenance-messaging.jsp"/>

				<sec:authorize access="hasRole('ROLE_PREVIOUS_ADMINISTRATOR')">
					<jsp:include page="/WEB-INF/views/web/partials/general/masquerade.jsp"/>
				</sec:authorize>

				<jsp:doBody />

				<c:set var="email" value=""/>
				<c:set var="fullName" value=""/>
				<c:set var="userNumber" value=""/>
				<sec:authorize access="isAuthenticated()">
					<sec:authentication property="principal.email" var="email"/>
					<sec:authentication property="principal.fullName" var="fullName"/>
					<sec:authentication property="principal.userNumber" var="userNumber"/>
				</sec:authorize>

			</div> <%-- container --%>
			<div class="push"></div>
		</div> <%-- outer container --%>

		<div class="site-footer">
			<div class="container-fluid">
				<ul class="site-footer--links">
					<li><wm:logo /></li>
					<li><a href="/about">About</a></li>
					<li><a href="https://workmarket.zendesk.com/hc/en-us" target="_blank">Help Center</a></li>
					<li><a href="/tos">Policies</a></li>
					<li><a href="/privacy">Privacy</a></li>
					<li><a href="mailto:support@workmarket.com">Feedback</a></li>
					<c:if test="${requestScope.currentDevice.mobile}">
						<li><a href="/mobile?site_preference=mobile">Mobile Site</a></li>
					</c:if>
					<sec:authorize access="hasRole('ROLE_INTERNAL')">
						<li><a href="/admin">Admin Site</a></li>
						<li><a href="${sugarUrl}">SugarCRM</a></li>
					</sec:authorize>
				</ul>
				<div class="copyright">
					<small class="meta">
						Work Market Logos, Site Design, and Content &copy; <fmt:formatDate value="${date}" pattern="yyyy" /> Work Market Inc. All rights reserved. Several aspects of the Work Market site are patent pending.
					</small>
				</div>
			</div>
		</div>

		<jsp:include page="/WEB-INF/views/web/partials/general/analytics/segment.jsp"/>

		<script>
			var UserNumber = "${currentUser.id}";
			var UserName = "${currentUser.fullName}";
			var UserProfileURL = "${baseurl}/profile/${currentUser.userNumber}";
		</script>

		<c:if test="${not empty pageScript}">
			<script>
				$(${pageScript}(${pageScriptParams}));
			</script>
		</c:if>

		<c:if test="${not empty manifestMap.get('manifest.js')}">
			<script src="${mediaPrefix}/builds/${manifestMap.get('manifest.js')}"></script>
		</c:if>
		<c:if test="${not empty webpackScript}">
			<jsp:include page="/WEB-INF/views/web/partials/config/webpack.jsp?script=${webpackScript}" />
		</c:if>

		<jsp:include page="/WEB-INF/views/web/partials/general/material-design-lite.jsp"/>
	</body>
</html>
