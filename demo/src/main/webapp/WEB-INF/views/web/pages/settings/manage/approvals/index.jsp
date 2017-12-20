<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="wm-csrf" uri="http://www.workmarket.com/taglib/csrf" %>
<%@ taglib prefix="wm" tagdir="/WEB-INF/tags" %>

<fmt:message key="global.approvals" var="global_approvals"/>
<wm:app
	pagetitle="${global_approvals}"
	bodyclass="accountSettings"
	webpackScript="approvals"
>
	<div class="row_wide_sidebar_left">

		<div class="sidebar">
			<c:set var="selected_navigation_link" value="/settings/manage/approvals" scope="request"/>
			<c:import url="/WEB-INF/views/web/partials/mmw/mmw_sidebar.jsp"/>
		</div>

		<div class="content">
			<div style="padding:20px;margin-bottom:20px;">
				<c:import url="/WEB-INF/views/web/partials/general/notices_js.jsp">
					<c:param name="containerId" value="dynamic_messages"/>
				</c:import>
				<div id="approvals_container"></div>
			</div>
		</div>
	</div>
</wm:app>
