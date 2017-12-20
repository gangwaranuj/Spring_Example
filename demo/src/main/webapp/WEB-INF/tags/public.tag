<%@ tag description="Public Page template" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles" %>
<%@ taglib prefix="tilesx" uri="http://tiles.apache.org/tags-tiles-extras" %>
<%@ taglib prefix="wmfn" uri="http://www.workmarket.com/taglib/functions" %>
<%@ taglib prefix="wmfmt" uri="http://www.workmarket.com/taglib/fmt" %>
<%@ taglib prefix="wm-csrf" uri="http://www.workmarket.com/taglib/csrf" %>
<%@ taglib prefix="wm" tagdir="/WEB-INF/tags" %>

<jsp:useBean id="date" class="java.util.Date"/>
<%@ attribute name="hideHeader" required="false" %>
<%@ attribute name="hideFooter" required="false" %>
<%@ attribute name="pagetitle" required="false" %>
<%@ attribute name="bodyclass" required="false" %>
<%@ attribute name="webpackScript" required="false" %>

<c:set var="hideHeader" value="${hideHeader}" scope="request"/>
<c:set var="hideFooter" value="${hideFooter}" scope="request"/>
<c:set var="pageTitle" value="${pagetitle}" scope="request"/>
<c:set var="bodyClass" value="${bodyclass}" scope="request"/>
<c:set var="lang" value="${currentLocale}" />

<!DOCTYPE html>
<html class="no-js" lang="${lang}">
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

	<meta name="description" content="${wmfn:defaultString(pageDescription,'Hire local, on-site freelancers for any job! Our workforce staffing solutions help you find, manage and pay the best talent for any contractor job with our simple online service.' )}"/>
	<meta name="keywords" content="${pageKeywords}, workmarket, work market, work, labor resource platform, Independent contractor, staffing, Service provider, consultant, freelancer, temporary, temp, part time, helper, labor, remote freelancer, outsource, outsource to freelancers, hire temps, VMS,  managed services, contract worker, W9, 1099, temporary staff, it staffing, field marketing, mystery shopping, nurse temps, paralegal"/>
	<meta name="robots" content="all,follow"/>
	<meta property="og:title" content="${wmfn:defaultString(pageTitle,'Work Market | Freelance Workforce Solutions for Finding & Managing Contractors')}"/>
	<meta property="og:site_name" content="workmarket.com"/>
	<meta name="google-site-verification" content="RkyCyPVvl9K4ZeOG_f21u9htXBl6BbY8vOHeJjPRIfo" />
	<meta name="csrf-token" content="<wm-csrf:csrfToken plainToken='true'/>"/>
	<meta name="isDesktop" content="false"/>
	<link href="https://plus.google.com/+WorkMarketHQ" rel="publisher"/>

	<title>Work Market<c:if test="${not empty pageTitle}"> | <c:out value="${pageTitle}"/></c:if></title>

	<jsp:include page="/WEB-INF/views/web/partials/general/analytics/trackjs.jsp"/>
	<jsp:include page="/WEB-INF/views/web/partials/context.jsp"/>

	<script>
		var mediaPrefix = "${mediaPrefix}";
	</script>

	<link href="//fonts.googleapis.com/css?family=Open+Sans:300italic,400italic,600italic,700italic,800italic,400,300,600,700,800|Roboto+Slab:300,400,700" rel="stylesheet" type="text/css">
	<link href="https://fonts.googleapis.com/icon?family=Material+Icons" rel="stylesheet"/>
	<link href="${mediaPrefix}/core.css" rel="stylesheet" type="text/css"/>
	<script src='https://www.google.com/recaptcha/api.js'></script>
</head>

<body class="<c:out value="${bodyClass}" /> bod">
<c:if test="${not hideHeader}">
	<header id="header" class="site-header">
		<a href="/home" class="site-header--home wm-icon-wm-filled"></a>
		<div class="site-header--contact active">
			<wm:icon name="phone"/>
			<span class="number highlight">212-229-WORK (9675)</span>
		</div>
	</header>
</c:if>
<div id="outer-container">
	<div class="main container-fluid">
		<jsp:doBody />
	</div>
</div>

<c:if test="${not hideFooter}">
	<div class="site-footer">
		<div class="container-fluid">
			<ul class="site-footer--links">
				<li><wm:logo /></li>
				<li><a href="/about" target="_blank">About</a></li>
				<li><a href="https://blog.workmarket.com" target="_blank">Blog</a></li>
				<li><a href="/tos">Policies</a></li>
				<li><a href="/privacy">Privacy</a></li>
				<li><a href="mailto:support@workmarket.com">Feedback</a></li>
				<li><a id="reset-password" href="/login#reset">Forgot Password</a></li>
				<li><a href="https://workmarket.zendesk.com/hc/en-us" target="_blank">Help Center</a></li>
				<c:if test="${requestScope.currentDevice.mobile}">
					<li><a href="/mobile?site_preference=mobile">Mobile Site</a></li>
				</c:if>
			</ul>
			<div class="copyright">
				Work Market Logos, Site Design, and Content &copy; <fmt:formatDate value="${date}" pattern="yyyy" /> Work Market Inc. All rights reserved. Several aspects of the Work Market site are patent pending.
			</div>
		</div>
	</div></c:if>

<script>
	(function(i,s,o,g,r,a,m){i['GoogleAnalyticsObject']=r;i[r]=i[r]||function(){
		(i[r].q=i[r].q||[]).push(arguments)},i[r].l=1*new Date();a=s.createElement(o),
		m=s.getElementsByTagName(o)[0];a.async=1;a.src=g;m.parentNode.insertBefore(a,m)
	})(window,document,'script','//www.google-analytics.com/analytics.js','ga');

	ga('create', 'UA-16961266-1', 'auto');
	ga('require', 'displayfeatures');
	ga('send', 'pageview');
</script>

<c:if test="${not empty manifestMap.get('manifest.js')}">
	<script src="${mediaPrefix}/builds/${manifestMap.get('manifest.js')}"></script>
</c:if>
<c:if test="${not empty webpackScript}">
	<jsp:include page="/WEB-INF/views/web/partials/config/webpack.jsp?script=${webpackScript}" />
</c:if>

<jsp:include page="/WEB-INF/views/web/partials/general/material-design-lite.jsp"/>
</body>
</html>