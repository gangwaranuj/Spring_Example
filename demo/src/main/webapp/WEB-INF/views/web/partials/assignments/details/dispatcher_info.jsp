<%@ taglib prefix="wmfmt" uri="http://www.workmarket.com/taglib/fmt" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<c:if test="${dispatcher != null}">
	<dl class="iconed-dl">
		<dt><jsp:include page="/WEB-INF/views/web/partials/svg-icons/icon-team-agent.jsp"/></dt>
		<dd>
			<strong><c:out value="${dispatcher.firstName}" /> <c:out value="${dispatcher.lastName}" /></strong><br/>
			${wmfmt:phone(dispatcher.workPhone)}<br/>
			${wmfmt:phone(dispatcher.mobilePhone)}<br/>
			<small><a href="mailto:${dispatcher.email}"><c:out value="${dispatcher.email}" /></a></small>
		</dd>
	</dl>
</c:if>
