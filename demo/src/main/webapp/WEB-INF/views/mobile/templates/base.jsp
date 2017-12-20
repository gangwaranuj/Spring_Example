<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles" %>
<%@ taglib prefix="tilesx" uri="http://tiles.apache.org/tags-tiles-extras" %>
<%@ taglib prefix="wm-csrf" uri="http://www.workmarket.com/taglib/csrf" %>

<!DOCTYPE html>
<html>
	<head>
		<title>Work Market Mobile</title>
		<meta charset="utf-8"/>
		<meta name="viewport" content="width=device-width,height=device-height,initial-scale=1,maximum-scale=1.0,minimum-scale=1,user-scalable=no" />
		<meta name="apple-mobile-web-app-capable" content="yes" />
		<meta name="apple-mobile-web-app-status-bar-style" content="black" />
		<meta http-equiv="content-type" content="text/html; charset=utf-8"/>
		<meta http-equiv="content-language" content="en"/>
		<meta name="csrf-token" content="<wm-csrf:csrfToken plainToken='true'/>"/>
		<meta name="format-detection" content="telephone=no">
		<meta name="description" content="Hire local, on-site freelancers for any job! Our workforce staffing solutions help you find, manage and pay the best talent for any contractor job with our simple online service."/>
		<meta property="og:title" content="Work Market | Freelance Workforce Solutions for Finding & Managing Contractors"/>
		<meta name="isDesktop" content="false"/>

		<link href="${mediaPrefix}/mobile.css" rel="stylesheet" type="text/css"/>
		<script>
			var mediaPrefix = "${mediaPrefix}";
		</script>
	</head>
	<body class="${bodyClass}">

	<tiles:insertAttribute name="body" ignore="true" />

	<jsp:include page="/WEB-INF/views/web/partials/general/analytics/segment.jsp"/>
	<jsp:include page="/WEB-INF/views/web/partials/context.jsp"/>
	
	<script type="text/javascript" src="${mediaPrefix}/mobile.js"></script>

	<c:if test="${not empty pageScript}">
		<script>
			$(${pageScript}(${pageScriptParams}));
		</script>
	</c:if>

	<%-- Backdrop for any popups --%>
	<div class="popup-background"></div>

	<script type="text/javascript">
		bullhorn_count();
	</script>

	<c:import url="/WEB-INF/views/mobile/partials/wm-loader.jsp" />

	<c:if test="${not empty manifestMap.get('manifest.js')}">
		<script src="${mediaPrefix}/builds/${manifestMap.get('manifest.js')}"></script>
	</c:if>
	<c:if test="${not empty webpackScript}">
		<jsp:include page="/WEB-INF/views/web/partials/config/webpack.jsp?script=${webpackScript}" />
	</c:if>
	</body>
</html>