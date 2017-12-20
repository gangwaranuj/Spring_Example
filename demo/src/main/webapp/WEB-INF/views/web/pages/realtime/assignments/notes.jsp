<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<div class="row">
	<div class="span10" id="notes">
		<c:import url="/WEB-INF/views/web/partials/assignments/details/notes.jsp">
			<c:param name="notes" value="${work.notes}"/>
			<c:param name="disablePrivacy" value="true" />
		</c:import>
	</div>
</div>
