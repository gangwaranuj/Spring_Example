<%@ tag description="Switch" %>
<%@ attribute name="classlist" required="false" %>
<%@ attribute name="id" required="false" %>
<%@ attribute name="text" required="true" %>
<%@ attribute name="checked" required="false" %>
<%@ attribute name="isUnique" required="false" %>
<%@ attribute name="name" required="true" %>
<%@ attribute name="value" required="false" %>
<%@ attribute name="attributes" required="false" %>

<label class="switch ${classlist}" id="${id}" ${attributes}>
	<input type="${isUnique ? 'radio' : 'checkbox'}" name="${name}" value="${value}" class="switch--checkbox" ${checked ? 'checked' : ''} />
	<div class="switch--skin">${text}</div>
</label>
