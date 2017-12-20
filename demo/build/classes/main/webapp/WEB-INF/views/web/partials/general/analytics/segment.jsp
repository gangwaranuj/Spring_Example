<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="wmfmt" uri="http://www.workmarket.com/taglib/fmt" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<script type="text/javascript">
	!function(){var analytics=window.analytics=window.analytics||[];if(!analytics.initialize)if(analytics.invoked)window.console&&console.error&&console.error("Segment snippet included twice.");else{analytics.invoked=!0;analytics.methods=["trackSubmit","trackClick","trackLink","trackForm","pageview","identify","reset","group","track","ready","alias","debug","page","once","off","on"];analytics.factory=function(t){return function(){var e=Array.prototype.slice.call(arguments);e.unshift(t);analytics.push(e);return analytics}};for(var t=0;t<analytics.methods.length;t++){var e=analytics.methods[t];analytics[e]=analytics.factory(e)}analytics.load=function(t){var e=document.createElement("script");e.type="text/javascript";e.async=!0;e.src=("https:"===document.location.protocol?"https://":"http://")+"cdn.segment.com/analytics.js/v1/"+t+"/analytics.min.js";var n=document.getElementsByTagName("script")[0];n.parentNode.insertBefore(e,n)};analytics.SNIPPET_VERSION="4.0.0";
	}}();

	/* Begin Segment
	analytics.load("${segmentWriteKey}");
	analytics.identify('${currentUser.userNumber}', {
		LoggedIn: 'Yes', <%-- Dimension 1: User is logged in --%>
		firstName: '${wmfmt:escapeJavaScript(currentUser.firstName)}',
		companyNumber: '${currentUser.companyNumber}',
		companyId: '${currentUser.companyId}',
		Company: '${wmfmt:escapeJavaScript(currentUser.companyName)}', <%--Dimension 4: Company name--%>
		subscriptionEnabled: '${currentUser.subscriptionEnabled}',
		companyIsIndividual: '${currentUser.companyIsIndividual}',
		sessionId: '${pageContext.session.id}',
		<%-- Dimension 2: Check if this user is a buyer, seller, or internal user --%>
		<c:choose>
			<c:when test="${currentUser.manageWork}">
				UserType: '<fmt:message key="global.buyer" />',
			</c:when>
			<c:when test="${currentUser.findWork}">
				UserType: '<fmt:message key="global.seller" />',
			</c:when>
		</c:choose>
		<sec:authorize access="hasRole('ROLE_INTERNAL')">
			UserType: '<fmt:message key="global.internal" />',
		</sec:authorize>
		<%-- What "mode" is the user currently in? --%>
		<c:choose>
			<c:when test="${currentUser.buyer}">
				mode: '<fmt:message key="global.manage_work" />',
			</c:when>
			<c:when test="${currentUser.seller}">
				mode: '<fmt:message key="global.perform_work" />',
			</c:when>
			<c:when test="${currentUser.dispatcher}">
				mode: '<fmt:message key="global.dispatch_work" />',
			</c:when>
		</c:choose>
		<%-- Dimension 6: WM native app version --%>
		<c:if test="${not empty cookie['wm-app-version'].value}">
			AppVersion: '${cookie['wm-app-version'].value}'
		</c:if>
	});
	analytics.page();
	window.addEventListener('beforeunload', function (event) {
		if (analytics) {
			analytics.reset();
		}
	});
	/* end Segment */
</script>
