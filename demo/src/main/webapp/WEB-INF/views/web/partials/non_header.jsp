<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<jsp:useBean id="date" class="java.util.Date"/>
<c:set var="lang" value="${currentLocale}" />

<!DOCTYPE html>
<html lang="${lang}">
<head>
	<meta charset="utf-8" />
	<meta http-equiv="Expires" content="Mon, 26 Jul 1997 05:00:00 GMT" />
	<meta http-equiv="Cache-Control" content="no-store, no-cache, must-revalidate" />
	<meta http-equiv="Pragma" content="no-cache" />
	<meta http-equiv="content-type" content="text/html; charset=utf-8" />
	<meta http-equiv="content-language" content="${lang}" />
	<meta name="author" lang="${lang}" content="Work Market, Inc" />
	<meta name="copyright" lang="${lang}" content="<fmt:formatDate value="${date}" pattern="yyyy" />" />
	<meta name="description" content="<fmt:message key="meta_header.description" />" />
	<meta name="keywords" content="<fmt:message key="meta_header.keywords" />" />
	<meta name="robots" content="all,follow" />
	<meta property="og:title" content="Work Market"/>
	<meta property="og:site_name" content="workmarket.com"/>
	<meta name="google-site-verification" content="RkyCyPVvl9K4ZeOG_f21u9htXBl6BbY8vOHeJjPRIfo" />

	<jsp:include page="/WEB-INF/views/web/partials/general/analytics/segment.jsp"/>

	<title><c:if test="${not empty pageTitle}"><c:out value="${pageTitle}"/> - </c:if>Work Market</title>
</head>