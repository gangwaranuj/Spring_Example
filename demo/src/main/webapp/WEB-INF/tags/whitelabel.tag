<%@ tag description="White Label Template" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<jsp:useBean id="date" class="java.util.Date"/>
<%@ attribute name="pageTitle" required="false" %>
<%@ attribute name="bodyClass" required="false" %>
<%@ attribute name="webpackScript" required="false" %>
<%@ attribute name="disableBootstrap" required="false" %>
<%@ attribute name="hideMDL" required="false" %>

<c:set var="lang" value="${currentLocale}" />

<!DOCTYPE html>
<html lang="${lang}">
<head>
	<meta charset="utf-8"/>
	<meta name="viewport" content="width=device-width, initial-scale=1.0"/>
	<meta http-equiv="Expires" content="Mon, 26 Jul 1997 05:00:00 GMT"/>
	<meta http-equiv="Cache-Control" content="no-store, no-cache, must-revalidate"/>
	<meta http-equiv="Pragma" content="no-cache"/>
	<meta http-equiv="content-type" content="text/html; charset=utf-8"/>
	<meta name="author" lang="${lang}" content="Work Market, Inc"/>
	<meta http-equiv="content-language" content="${lang}"/>
	<meta name="copyright" lang="${lang}" content="<fmt:formatDate value="${date}" pattern="yyyy" />"/>
	<meta name="description" content="Hire local, on-site freelancers for any job! Our workforce staffing solutions help you find, manage and pay the best talent for any contractor job with our simple online service."/>
	<meta name="keywords" content="workmarket, work market, work, labor resource platform, Independent contractor, staffing, Service provider, consultant, freelancer, temporary, temp, part time, helper, labor, remote freelancer, outsource, outsource to freelancers, hire temps, VMS,  managed services, contract worker, W9, 1099, temporary staff, it staffing, field marketing, mystery shopping, nurse temps, paralegal"/>
	<meta name="robots" content="all,follow"/>
	<meta property="og:title" content="Work Market | Freelance Workforce Solutions for Finding & Managing Contractors"/>
	<meta property="og:site_name" content="workmarket.com"/>
	<meta name="google-site-verification" content="RkyCyPVvl9K4ZeOG_f21u9htXBl6BbY8vOHeJjPRIfo" />
	<meta name="isDesktop" content="false"/>
	<link href="https://plus.google.com/+WorkMarketHQ" rel="publisher"/>

	<title>Work Market<c:if test="${not empty pageTitle}"> | ${pageTitle}</c:if></title>

	<jsp:include page="/WEB-INF/views/web/partials/context.jsp"/>

	<c:if test="${not disableBootstrap}">
		<link href="${mediaPrefix}/bootstrap2.css" rel="stylesheet" type="text/css"/>
		<link href="${mediaPrefix}/core.css" rel="stylesheet" type="text/css"/>
	</c:if>

	<link href="https://fonts.googleapis.com/icon?family=Material+Icons" rel="stylesheet"/>

	<script>
		var mediaPrefix = "${mediaPrefix}";
		var context = {
			name: "${not empty webpackScript ? webpackScript : "base"}",
			data: {},
			features: {}
		}
	</script>

</head>

	<body class="${bodyClass}">

		<!-- Begin: Main Template -->
		<sec:authorize access="hasRole('ROLE_PREVIOUS_ADMINISTRATOR')">
			<jsp:include page="/WEB-INF/views/web/partials/general/masquerade.jsp"/>
		</sec:authorize>

		<jsp:doBody />

		<footer class="footer">
			<div class="container">
				<c:if test="${not disableBootstrap}">
					<p class="fr">
						<a href="/" target="_blank" style="color:#ffffff;">Powered by Work Market</a>
					</p>
				</c:if>
			</div>
		</footer>

		<c:if test="${not empty manifestMap.get('manifest.js')}">
			<script src="${mediaPrefix}/builds/${manifestMap.get('manifest.js')}"></script>
		</c:if>

		<c:if test="${not empty webpackScript}">
			<jsp:include page="/WEB-INF/views/web/partials/config/webpack.jsp?script=${webpackScript}" />
		</c:if>

		<c:if test="${empty webpackScript}">
			<jsp:include page="/WEB-INF/views/web/partials/general/temp-templates.jsp"/>
		</c:if>

        <c:if test="${not hideMDL}">
            <jsp:include page="/WEB-INF/views/web/partials/general/material-design-lite.jsp"/>
        </c:if>

	</body>
</html>
