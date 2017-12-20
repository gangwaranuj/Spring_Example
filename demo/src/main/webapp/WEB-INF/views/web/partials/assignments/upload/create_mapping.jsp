<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<form:form modelAttribute="uploadForm.mapping" action="/assignments/upload/create_mapping" id="create-mapping-form">
	<div class="clearfix">
		<label for="name" class="required">Name</label>
		<div class="input">
			<form:input path="name" />
		</div>
	</div>

	<div class="wm-action-container">
		<button type="submit" class="button">Save</button>
	</div>
</form:form>
