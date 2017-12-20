<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="wm" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="wmfmt" uri="http://www.workmarket.com/taglib/fmt" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<fmt:message key="feed.company_work_feed" var="feed_company_work_feed">
	<fmt:param value="${requestScope.companyName}"/>
</fmt:message>

<wm:app
	pagetitle="${feed_company_work_feed}"
	bodyclass="accountSettings"
	webpackScript="settings"
>

	<script>
		var config = {
			mode: 'feed',
			companyId: '${wmfmt:escapeJavaScript(companyId)}'
		};
	</script>

	<div class="row_sidebar_left">
		<div class="sidebar">
			<c:set var="selected_navigation_link" value="/settings/manage/customfields" scope="request"/>
			<c:import url="/WEB-INF/views/web/partials/mmw/mmw_sidebar.jsp"/>
		</div>

		<div class="content">
			<div class="page-header">
				<h3>${feed_company_work_feed}</h3>
			</div>

			<c:import url="/WEB-INF/views/web/partials/feed/shared/builder.jsp" />
		</div>
	</div>

</wm:app>
