<%@ tag description="File Input" %>
<%@ attribute name="inline" required="false" %>
<%@ attribute name="classlist" required="false" %>
<%@ attribute name="name" required="false" %>

<label class="wm-file-input ${inline ? '-inline' : '' } ${classlist}">
	<input type="file" name="${name}" />
	<div class="wm-file-input--name"></div>
	<jsp:doBody />
</label>
