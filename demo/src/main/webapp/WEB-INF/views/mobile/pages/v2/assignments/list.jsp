<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="wmfmt" uri="http://www.workmarket.com/taglib/fmt" %>

<c:set var="pageScript" value="wm.pages.mobile.assignments.list" scope="request"/>
<c:set var="pageScriptParams" value="'${status}', 1" scope="request"/>
<c:set var="backUrl" value="/mobile" scope="request"/>

<div class="list-page">
	<jsp:include page="/WEB-INF/views/mobile/partials/nav.jsp">
		<jsp:param name="title" value="${status eq WorkStatusType.COMPLETE ? 'Pending' : title}" />
	</jsp:include>

	<c:import url="/WEB-INF/views/mobile/partials/general-panel.jsp"/>

	<div class="content">
		<div class="unit whole" id="public-message">
			<c:import url="/WEB-INF/views/mobile/partials/notices.jsp" />
		</div>

		<div class="assignment-container">
			<div class="empty-list-message">
				Sorry, looks like you don't have any.
			</div>
		</div>

		<c:import url="/WEB-INF/views/mobile/partials/assignments/assignment-template.jsp"/>

		<input type="hidden" id="home-lat">
		<input type="hidden" id="home-lon">

	</div>
	<span class="up"><c:import url="/WEB-INF/views/mobile/partials/svg-icons/icon-uparrow-white.jsp"/></span>
</div>
