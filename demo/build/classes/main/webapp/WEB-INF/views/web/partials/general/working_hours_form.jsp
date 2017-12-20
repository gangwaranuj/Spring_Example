<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<div class="clearfix">
	<label><fmt:message key="global.working_hours" /></label>
	<div class="input">
		<ul class="inputs-list">
			<c:forEach var="i" begin="0" end="6" step="1">
				<jsp:include page="/WEB-INF/views/web/partials/general/working_hours_form_row.jsp">
					<jsp:param name="row_i" value="${i}" />
				</jsp:include>
			</c:forEach>
		</ul>
	</div>
</div>
