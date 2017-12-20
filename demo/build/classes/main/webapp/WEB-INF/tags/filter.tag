<%@ tag description="Filter" %>
<%@ attribute name="name" required="true" %>
<%@ attribute name="value" required="true" %>
<%@ attribute name="text" required="true" %>
<%@ attribute name="checked" required="false" %>
<%@ attribute name="isChecked" required="false" %>
<%@ attribute name="id" required="false" %>
<%@ attribute name="isUnique" required="false" %>
<%@ attribute name="classlist" required="false" %>

<label class="filter" id="${id}">
	<input type="${isUnique ? 'radio' : 'checkbox'}" name="${name}" value="${value}" ${isChecked ? 'checked' : ''} ${checked} />
	<div class="filter--skin ${classlist}">${text}</div>
</label>
