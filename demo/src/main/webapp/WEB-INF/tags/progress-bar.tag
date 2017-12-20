<%@ tag description="Progress Bar" %>
<%@ attribute name="id" required="false" %>
<%@ attribute name="size" required="false" %>
<%@ attribute name="width" required="false" %>
<%@ attribute name="classlist" required="false" %>

<div id="${id}" class="mdl-progress mdl-js-progress ${classlist} progress-bar" style="width: 100%;">
	<div class="progressbar bar bar1" style="width: ${width}%;"></div>
	<div class="bufferbar bar bar2" style="width: 100%;"></div>
	<div class="auxbar bar bar3" style="width: 0%;"></div>
</div>
