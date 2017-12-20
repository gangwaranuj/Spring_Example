<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="wm" tagdir="/WEB-INF/tags" %>

<sec:authorize access="hasFeature('employerOnboarding')">
	<c:set var="employerOnboarding" value="true" />
</sec:authorize>

<%-- Velvet Rope for Worker Role --%>
<c:set var="workerRole" value="false" />
<vr:rope>
	<vr:venue name="EMPLOYEE_WORKER_ROLE">
		<c:set var="workerRole" value="true" />
	</vr:venue>
</vr:rope>

<fmt:message key="onboarding.employees" var="onboarding_employees"/>
<wm:app
	pagetitle="${onboarding_employees}"
	bodyclass="page-settings"
	isBootstrapDisabled="true"
	webpackScript="newsettings"
>

	<script>
		var config = {name: 'settings', workerRole: ${workerRole}, features: {}}
	</script>

	<div id="app">
		<c:if test="${employerOnboarding}">
			<div id="employees"></div>
		</c:if>
	</div>

</wm:app>
