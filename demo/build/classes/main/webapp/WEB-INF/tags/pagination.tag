<%@ tag description="Pagination" %>
<%@ taglib prefix="wm" tagdir="/WEB-INF/tags" %>

<%@ attribute name="previous" required="false" %>
<%@ attribute name="next" required="false" %>
<%@ attribute name="min" required="false" %>
<%@ attribute name="max" required="true" %>

<div class="wm-pagination pagination" data-min="${min ? min : 1}" data-max="${max}">
	<a class="wm-pagination--back wm-icon-left-arrow"></a>
	<span class="wm-pagination--label">of</span>
	<a class="wm-pagination--next wm-icon-right-arrow"></a>
</div>
