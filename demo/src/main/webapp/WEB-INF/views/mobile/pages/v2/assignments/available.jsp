<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="wmfmt" uri="http://www.workmarket.com/taglib/fmt" %>

<c:set var="pageScript" value="wm.pages.mobile.assignments.available" scope="request"/>
<c:set var="pageScriptParams" value="'invited', 1" scope="request"/>
<c:set var="backUrl" value="/mobile" scope="request"/>

<div class="available-page">
	<jsp:include page="/WEB-INF/views/mobile/partials/nav.jsp">
		<jsp:param name="title" value="Available" />
	</jsp:include>

	<div class="grid filters">
		<div class="unit whole">
			<div class="half invited">Invited</div>
			<div class="half feed"><span class="orange-text">Work</span>Feed&#8482;</div>
		</div>
	</div>

	<c:import url="/WEB-INF/views/mobile/partials/general-panel.jsp"/>

	<div class="content">
		<div class="unit whole" id="public-message">
			<c:import url="/WEB-INF/views/mobile/partials/notices.jsp" />
		</div>

		<div class="invited-container"></div>
		<div class="feed-container"></div>

		<c:import url="/WEB-INF/views/mobile/partials/assignments/assignment-template.jsp"/>

		<input type="hidden" id="home-lat">
		<input type="hidden" id="home-lon">

	</div>
	<span class="up"><c:import url="/WEB-INF/views/mobile/partials/svg-icons/icon-uparrow-white.jsp"/></span>
</div>
