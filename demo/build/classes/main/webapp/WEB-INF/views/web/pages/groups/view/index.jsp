<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="wm" tagdir="/WEB-INF/tags" %>

<c:set var="companyIsLocked" value="${false}" />
<c:set var="companyHasLockWarning" value="${false}" />
<c:set var="companyHasOverdueWarning" value="${false}" />
<c:set var="companyOverdueWarningDaysBetweenFromNow" value="-1" />
<sec:authorize access="(!principal.userPaymentAccessBlocked) AND (principal.hasCustomAccessSettingsSet OR hasAnyRole('PERMISSION_INVOICES', 'PERMISSION_PAY_INVOICE', 'PERMISSION_PAY_ASSIGNMENT', 'PERMISSION_PAYABLES'))">
	<sec:authentication property="principal.companyIsLocked" var="companyIsLocked"/>
	<sec:authentication property="principal.companyHasLockWarning" var="companyHasLockWarning"/>
	<sec:authentication property="principal.companyHasOverdueWarning" var="companyHasOverdueWarning"/>
	<sec:authentication property="principal.companyOverdueWarningDaysBetweenFromNow" var="companyOverdueWarningDaysBetweenFromNow"/>
</sec:authorize>

<wm:app
	pagetitle="${currentUser.companyName} Talent Pools"
	bodyclass="groups"
	isBootstrapDisabled="true"
	isOldNotificationDisabled="true"
	webpackScript="talentpools"
>
	<script>
		var config = ${contextJson};
		var notificationConfig = {
			companyIsLocked: ${companyIsLocked},
			companyHasLockWarning: ${companyHasLockWarning},
			companyHasOverdueWarning: ${companyHasOverdueWarning},
			companyOverdueWarningDaysBetweenFromNow: ${companyOverdueWarningDaysBetweenFromNow}
		};
	</script>
	<div id="talent-pool-bucket"></div>
</wm:app>
