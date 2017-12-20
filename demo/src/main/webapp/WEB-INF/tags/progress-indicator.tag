<%@ tag description="Progress Indicator" %>
<%@ attribute name="title" required="false" %>
<%@ attribute name="status" required="false" %>

<div class="indicator -${status}" data-step-title="${title}">
    <span class="indicator--icon"></span>
</div>
