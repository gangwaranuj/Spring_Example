<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="wmfmt" uri="http://www.workmarket.com/taglib/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<c:import url="/WEB-INF/views/web/partials/general/notices_js.jsp"/>

<c:choose>
	<c:when test="${fn:length(notes) > 0}">
		<c:forEach var="n" items="${notes}">
			<h5><c:out value="${n.creator.name.firstName}" /> <c:out value="${n.creator.name.lastName}" /> added a ${n.isPrivate ? 'private' : 'shared'}
				note</h5>
			<p>
				<small>${wmfmt:formatMillisWithTimeZone("MMM d, YYYY h:mma", n.createdOn, workTimeZone)}</small>
			</p>
			<p>${wmfmt:escapeHtmlAndnl2br(n.text)}</p>
		</c:forEach>
	</c:when>
	<c:otherwise>
		<p>There are no notes.</p>
	</c:otherwise>
</c:choose>