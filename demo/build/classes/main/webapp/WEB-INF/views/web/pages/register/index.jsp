<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="wmfmt" uri="http://www.workmarket.com/taglib/fmt" %>
<%@ taglib prefix="wmfn" uri="http://www.workmarket.com/taglib/functions" %>
<%@ taglib prefix="wm-csrf" uri="http://www.workmarket.com/taglib/csrf" %>
<%@ taglib prefix="wm" tagdir="/WEB-INF/tags" %>

<wm:whitelabel
	disableBootstrap="true"
	hideMDL="true"
	pageTitle="${pageTitle}"
	bodyClass="registration-landing"
	webpackScript="invitationlandingpage"
>
	<meta name="csrf-token" content="<wm-csrf:csrfToken plainToken='true'/>"/>
	<script>
		var config = {
			csrf: "<wm-csrf:csrfToken plainToken='true'/>",
			companyNumber: '<c:out value="${company.companyNumber}" />',
			campaignText: "<c:out value="${wmfmt:escapeHtmlAndnl2br(campaign.description)}" escapeXml="false" />",
			encryptedId: '<c:out value="${encryptedId}" />',
			isInvitation: <c:out value="${not empty invitation}" />
		};
	</script>
	<div id="landing-page-bucket"></div>
</wm:whitelabel>
