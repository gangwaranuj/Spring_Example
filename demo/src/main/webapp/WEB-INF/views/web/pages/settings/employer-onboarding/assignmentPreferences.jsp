<%@ page import="com.workmarket.configuration.Constants" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="wm" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<sec:authorize access="hasFeature('employerOnboarding')">
	<c:set var="employerOnboarding" value="true" />
</sec:authorize>

<fmt:message key="onboarding.assignment_preferences" var="onboarding_assignment_preferences"/>
<wm:app
	pagetitle="${onboarding_assignment_preferences}"
	bodyclass="page-settings"
	isBootstrapDisabled="true"
	webpackScript="newsettings"
>

	<script>
		var config = {
			name: 'settings',
			features: {
				companyLogoUri: '${companyLogoUri}',
			}
		}
	</script>

	<div id="app">
		<c:if test="${employerOnboarding}">
			<div id="assignment-preferences"></div>
		</c:if>
	</div>

</wm:app>
