<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<c:if test="${not empty work.subStatuses}">
	<c:forEach items="${work.subStatuses}" var="s" varStatus="status">
		<span class="label" <c:if test="${not empty s.colorRgb}">style="background-color: #${s.colorRgb}; border-color: transparent #${s.colorRgb} transparent transparent;"</c:if>>
			<c:out value="${s.description}" />
			<%--TODO -- allow removing labels --%>
		</span><%--label--%>
	</c:forEach>
</c:if>
