<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="wmfmt" uri="http://www.workmarket.com/taglib/fmt" %>
<%@ taglib prefix="wmfn" uri="http://www.workmarket.com/taglib/functions" %>

<c:set var="pageScript" value="wm.pages.mobile.notifications" scope="request"/>
<c:set var="pageScriptParams" value="1" scope="request"/>
<c:set var="backUrl" value="/mobile" scope="request"/>

<div class="wrap notifications-page">
	<jsp:include page="/WEB-INF/views/mobile/partials/nav.jsp">
		<jsp:param name="title" value="Notifications" />
	</jsp:include>
	<div class="content">
		<div id="public-message">
			<c:import url="/WEB-INF/views/mobile/partials/notices.jsp" />
		</div>

		<ul class="notifications"></ul>
		<div class="zero-notifications">You have no notifications.</div>

	</div><%--content--%>
</div><%--wrap--%>
<span class="up"><c:import url="/WEB-INF/views/mobile/partials/svg-icons/icon-uparrow-white.jsp"/></span>

<script id="notification-template" type="text/template">
	{{ _.each( notifications, function ( notification ) { }}
		<li {{ notification.viewed ? 'viewed' : ''; }}">
			{{= notification.displayMessage }}
			${notification.displayMessage}
			<small class="notification-meta">
				{{= $.ago(new Date(notification.createdOn).valueOf()) }}
			</small>
		</li>
	{{ });  }}
</script>
