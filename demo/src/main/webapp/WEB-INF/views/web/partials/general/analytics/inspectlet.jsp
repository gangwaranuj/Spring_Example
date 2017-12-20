<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="wmfmt" uri="http://www.workmarket.com/taglib/fmt" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>


<%-- Begin Inspectlet Embed Code
<script type="text/javascript" id="inspectletjs">
	window.__insp = window.__insp || [];
	__insp.push(['wid', 797135200]);

	<sec:authorize access="!hasRole('ROLE_ANONYMOUS')">
	__insp.push(['identify', '${currentUser.email}']);
	__insp.push(['tagSession', {
		name: '${wmfmt:escapeJavaScript(currentUser.fullName)}',
		company: '${wmfmt:escapeJavaScript(currentUser.companyName)}',
		userNumber: '${currentUser.userNumber}',
		<c:choose>
		<c:when test="${currentUser.buyer}">
		mode: 'Manage Work',
		</c:when>
		<c:when test="${currentUser.seller}">
		mode: 'Perform Work',
		</c:when>
		<c:when test="${currentUser.dispatcher}">
		mode: 'Dispatch Work',
		</c:when>
		</c:choose>
		sessionId: '${pageContext.session.id}'
	}]);
	</sec:authorize>

	(function() {
		function ldinsp(){if(typeof window.__inspld != "undefined") return; window.__inspld = 1; var insp = document.createElement('script'); insp.type = 'text/javascript'; insp.async = true; insp.id = "inspsync"; insp.src = ('https:' == document.location.protocol ? 'https' : 'http') + '://cdn.inspectlet.com/inspectlet.js'; var x = document.getElementsByTagName('script')[0]; x.parentNode.insertBefore(insp, x); };
		setTimeout(ldinsp, 500); document.readyState != "complete" ? (window.attachEvent ? window.attachEvent('onload', ldinsp) : window.addEventListener('load', ldinsp, false)) : ldinsp();
	})();
</script>
<%-- End Inspectlet Embed Code --%>
