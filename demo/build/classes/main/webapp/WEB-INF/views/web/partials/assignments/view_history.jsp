<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="wmfmt" uri="http://www.workmarket.com/taglib/fmt" %>

<c:import url="/WEB-INF/views/web/partials/general/notices.jsp">
	<c:param name="bundle" value="${bundle}"/>
</c:import>

<c:choose>
	<c:when test="${not empty log}">
		<table>
			<tbody>
				<c:forEach var="entry" items="${log}">
					<tr>
						<td class="nowrap"><small><c:out value="${wmfmt:formatMillisWithTimeZone('MM/dd/YY @ hh:mma z', entry.timestamp, currentUser.timeZoneId)}" /></small></td>
						<td><c:out escapeXml="false" value="${entry.text}"/></td>
					</tr>
				</c:forEach>
			</tbody>
		</table>
	</c:when>
	<c:otherwise>
		<p>No assignment history.</p>
	</c:otherwise>
</c:choose>
