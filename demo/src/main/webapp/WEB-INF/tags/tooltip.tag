<%@ tag description="Tooltip" %>
<%@ attribute name="forHtml" required="true" %>
<%@ attribute name="direction" required="false" %>

<span
	class="mdl-tooltip ${not empty direction ? 'mdl-tooltip--' : ''}${not empty direction ? direction : ''}"
	for="${forHtml}"
><jsp:doBody /></span>
