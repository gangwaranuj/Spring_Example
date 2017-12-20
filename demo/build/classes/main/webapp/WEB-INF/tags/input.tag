<%@ tag description="Text Input" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<%@ attribute name="id" required="true" %>
<%@ attribute name="label" required="true" %>
<%@ attribute name="pattern" required="false" %>
<%@ attribute name="error" required="false" %>
<%@ attribute name="hasFloatingLabel" required="false" %>
<%@ attribute name="textarea" required="false" %>
<%@ attribute name="rows" required="false" %>

<div class="mdl-textfield mdl-js-textfield ${not empty hasFloatingLabel and hasFloatingLabel ? 'mdl-textfield--floating-label' : ''}">
	<${not empty textarea and textarea ? 'textarea' : 'input'}
		class="mdl-textfield__input"
		type="text"
		id="${id}"
		<c:if test="${not empty rows}">rows="${rows}"</c:if>
		<c:if test="${not empty pattern}">pattern="${pattern}"</c:if>
	><c:if test="${not empty textarea and textarea}"></textarea></c:if>
	<label
		class="mdl-textfield__label"
		for="${id}"
	>${label}</label>
	<c:if test="${not empty error}">
		<span class="mdl-textfield__error">${error}</span>
	</c:if>
</div>
