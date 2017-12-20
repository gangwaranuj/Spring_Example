<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<sec:authorize access="(!principal.userPaymentAccessBlocked) AND (principal.hasCustomAccessSettingsSet OR hasAnyRole('PERMISSION_INVOICES', 'PERMISSION_PAY_INVOICE', 'PERMISSION_PAY_ASSIGNMENT', 'PERMISSION_PAYABLES'))">
	<sec:authentication property="principal.companyIsLocked" var="companyIsLocked"/>
	<sec:authentication property="principal.companyHasLockWarning" var="companyHasLockWarning"/>
	<sec:authentication property="principal.companyHasOverdueWarning" var="companyHasOverdueWarning"/>
	<sec:authentication property="principal.companyOverdueWarningDaysBetweenFromNow" var="companyOverdueWarningDaysBetweenFromNow"/>
</sec:authorize>

<c:if test="${userHasLockedCompany || companyIsLocked || companyHasLockWarning || companyHasOverdueWarning}">
	<div class="container" id="locked-warnings">
		<div class="alert
					<c:if test="${companyIsLocked || userHasLockedCompany}">alert-error</c:if>
					alert-block">
			<a class="locked-warnings close">x</a>

			<c:choose>
				<c:when test="${companyIsLocked || userHasLockedCompany}">
					<h4><fmt:message key="global.important" />:</h4>
					<p><strong><fmt:message key="locked.account_overdue_message" /></strong>
						<fmt:message key="locked.account_locked_message" />
					</p>
					<p><strong><fmt:message key="global.note" />:</strong> <fmt:message key="global.payment_processing_message" /></p>
					<a class="button" href="/payments/invoices/payables/past-due"><fmt:message key="global.pay_invoices" /></a>
					<a class="ml" href="https://workmarket.zendesk.com/hc/en-us/articles/209338048-My-account-is-locked-due-to-past-due-invoices-how-can-I-unlock-it" target="_blank"><fmt:message key="locked.help_with_a_locked_account" /></a>
				</c:when>
				<c:when test="${companyHasLockWarning }">
					<p>
						<strong><fmt:message key="global.reminder" />:</strong><br/>
                            <fmt:message key="locked.incoming_invoices_due_message" />
					</p>
					<strong><a class="button" href="/payments/invoices/payables/upcoming-due"><fmt:message key="global.view_outstanding_invoices" /></a></strong>
				</c:when>
				<c:when test="${companyHasOverdueWarning}">
					<p>
						<strong><fmt:message key="global.notice" />:</strong><br/>
						<c:choose>
							<c:when test="${companyOverdueWarningDaysBetweenFromNow == 0}"><fmt:message key="locked.account_overdue_0_days" /></c:when>
							<c:when test="${companyOverdueWarningDaysBetweenFromNow == 1}"><fmt:message key="locked.account_overdue_1_days" /></c:when>
							<c:when test="${companyOverdueWarningDaysBetweenFromNow == 2}"><fmt:message key="locked.account_overdue_2_days" /></c:when>
						</c:choose>
					</p>
					<strong><a class="button" href="/payments/invoices/payables/past-due"><fmt:message key="global.view_outstanding_invoices" /></a></strong>
				</c:when>
			</c:choose>
		</div>
	</div>
</c:if>
