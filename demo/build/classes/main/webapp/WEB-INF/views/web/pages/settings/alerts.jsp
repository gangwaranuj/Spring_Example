<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="sf" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="s" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="wm-csrf" uri="http://www.workmarket.com/taglib/csrf" %>
<%@ taglib prefix="wm" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<fmt:message key="alerts.settings_alerts" var="alerts_settings_alerts"/>
<wm:app
	pagetitle="${alerts_settings_alerts}"
	bodyclass="accountSettings"
>

	<div class="row_wide_sidebar_left">

		<div class="sidebar">
			<c:import url='/WEB-INF/views/web/partials/mmw/mmw_sidebar.jsp' />
		</div>

		<div class="content">
			<div class="inner-container">
				<div class="page-header">
					<h3><fmt:message key="alerts.low_balance"/></h3>
				</div>

				<c:import url="/WEB-INF/views/web/partials/general/notices.jsp">
					<c:param name="bundle" value="${bundle}" />
				</c:import>


				<sf:form action="/settings/alerts" method="POST" modelAttribute="lowBalanceAlertsForm" id="fund-alerts">
					<wm-csrf:csrfToken />
					<div>
						<s:bind path="custom_low_balance_flag">
							<input type="checkbox" name="custom_low_balance_flag" id="custom_low_balance_flag" <c:if test="${company.customLowBalanceFlag}">checked</c:if>/> Send me an email alert when my balance goes below $
							<sf:input path="low_balance_amount" value="${company.lowBalanceAmount}" size='10' type="text" class="input-small text-right"/>
						</s:bind>
					</div>
					<div class="wm-action-container">
						<a href="/funds" class="button"><fmt:message key="global.cancel"/></a>
						<button type="submit" class="button"><fmt:message key="global.save_changes"/></button>
					</div>
				</sf:form>
			</div>
		</div>
	</div>

</wm:app>
