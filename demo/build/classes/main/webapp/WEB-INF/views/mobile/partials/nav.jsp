<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<div class="grid nav">
	<div class="unit whole">
		<jsp:include page="/WEB-INF/views/mobile/partials/svg-icons/icon-logo.jsp"/>
		<h2 class="title">
			<c:out value="${param.title}" default="Work Market"/>
		</h2>
		<%-- Left Side --%>
		<div class="left-side">
			<a class="spin back-button" href="${not empty backUrl ? backUrl : "javascript: history.go(-1);"}">
				<c:import url="/WEB-INF/views/mobile/partials/svg-icons/back.jsp" />
			</a>
			<a class="spin home-button" href="/mobile">
				<c:import url="/WEB-INF/views/mobile/partials/svg-icons/icon-home.jsp" />
			</a>
		</div>
		<div class="right-side">
			<a href="/mobile/notifications" class="bullhorn-container spin">
				<span class="push-bubble"></span>
				<jsp:include page="/WEB-INF/views/mobile/partials/svg-icons/icon-bullhorn.jsp"/>
			</a>
			<c:if test="${empty param.isNotPaid or param.isNotPaid}">
				<a class="wmpanel-button" href="javascript:void(0);">
					<c:import url="/WEB-INF/views/mobile/partials/svg-icons/icon-panel.jsp"/>
				</a>
			</c:if>
		</div>
		<span class="diamond"></span>
	</div><%--unit whole--%>
</div><%--grid--%>
