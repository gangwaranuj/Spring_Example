<%@ tag description="Status Indicator" %>
<%@ attribute name="classlist" required="false" %>
<%@ attribute name="active" required="false" %>

<div class="status-indicator ${classlist} ${not empty active and active ? '-active' : ''}"></div>
