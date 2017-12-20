<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<c:import url="/WEB-INF/views/web/partials/general/notices.jsp" />

<form:form modelAttribute="mappingForm" action="/assignments/upload/rename_mapping/${mappingId}" id="rename-mapping-form">
	<div>
		<label for="name">Name</label>
		<div class="input">
			<form:input path="name" />
		</div>
	</div>

	<div class="wm-action-container">
		<button type="submit" class="button">Save Changes</button>
	</div>
</form:form>
