<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="wmfmt" uri="http://www.workmarket.com/taglib/fmt" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="wmfn" uri="http://www.workmarket.com/taglib/functions" %>
<%@ taglib prefix="wm" tagdir="/WEB-INF/tags" %>

<wm:admin pagetitle="Sales" bodyclass="manage-company">

	<c:set var="companyId" value="${company.id}"/>
	<c:set var="pageScript" value="wm.pages.admin.manage.company.sales" scope="request" />
	<c:set var="pageScriptParams" value="${companyId}" scope="request" />

	<div class="row_sidebar_left">
		<div class="sidebar admin">
			<jsp:include page="/WEB-INF/views/web/partials/admin/quick_links.jsp"/>
		</div>

		<div class="content">
			<c:import url="/WEB-INF/views/web/partials/general/notices.jsp">
				<c:param name="bundle" value="${bundle}" />
			</c:import>

			<c:if test="${company.locked}">
				<c:import url="/WEB-INF/views/web/partials/admin/manage/company/unlock_header.jsp"/>
			</c:if>
			<c:if test="${company.suspended}">
				<c:import url="/WEB-INF/views/web/partials/admin/manage/company/suspend_header.jsp"/>
			</c:if>

			<h1 class="name"><c:out value="${requestScope.company.name}"/></h1>

			<jsp:include page="/WEB-INF/views/web/partials/admin/manage/company/tabs.jsp" />

			<h3>Account Owner</h3>

			<div id="account-owner">
				<wm:spinner />
			</div>
		</div>
	</div>

</wm:admin>
