<%@ tag description="Toggle" %>
<%@ attribute name="name" required="true" %>
<%@ attribute name="value" required="true" %>
<%@ attribute name="text" required="true" %>
<%@ attribute name="checked" required="false" %>
<%@ attribute name="isChecked" required="false" %>
<%@ attribute name="id" required="false" %>
<%@ attribute name="classlist" required="false" %>

<label class="toggle" id="${id}">
	<input type="radio" name="${name}" value="${value}" ${isChecked ? 'checked' : ''} ${checked} />
	<div class="toggle--skin ${classlist}">${text}</div>
</label>
