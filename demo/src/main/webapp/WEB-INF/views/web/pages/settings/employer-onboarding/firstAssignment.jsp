<%@ page import="com.workmarket.configuration.Constants" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="wm" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<sec:authorize access="hasFeature('employerOnboarding')">
	<c:set var="employerOnboarding" value="true" />
</sec:authorize>

<fmt:message key="onboarding.first_assignment" var="onboarding_first_assignment"/>
<wm:app
	pagetitle="${onboarding_first_assignment}"
	bodyclass="page-settings"
	isBootstrapDisabled="true"
	webpackScript="newsettings"
>

	<script>
		var config = { name: 'settings', features: {} }
	</script>

	<div id="app">
		<c:if test="${employerOnboarding}">
			<div id="first-assignment"></div>
		</c:if>
	</div>

</wm:app>
