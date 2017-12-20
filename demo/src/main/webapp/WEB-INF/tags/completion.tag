<%@ tag description="Completion Bar" %>
<%@ attribute name="name" required="true" %>
<%@ attribute name="min" required="true" %>
<%@ attribute name="max" required="true" %>
<%@ attribute name="value" required="true" %>
<%@ attribute name="unit" required="false" %>
<%@ attribute name="status" required="false" %>
<%@ attribute name="classlist" required="false" %>

<div class="completion-bar ${not empty status ? '-'.concat(status) : ''} ${classlist}" data-completion-min="${min}" data-completion-max="${max}" data-completion-value="${value}">
	<span class="completion-bar--name">${name}</span>
	<span class="completion-bar--value">${value}${not empty unit ? unit : ''}</span>
	<div class="completion-bar--bar"></div>
</div>
