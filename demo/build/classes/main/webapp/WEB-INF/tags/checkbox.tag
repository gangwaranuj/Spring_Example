<%@ tag description="Checkbox Input" %>
<%@ attribute name="name" required="true" %>
<%@ attribute name="value" required="true" %>
<%@ attribute name="checked" required="false" %>
<%@ attribute name="isChecked" required="false" %>
<%@ attribute name="id" required="false" %>
<%@ attribute name="classList" required="false" %>
<%@ attribute name="badge" required="false" %>

<%-- Multiple escapes on the next line seem to be the only simple way to do concatenation. --%>
<label
	class="mdl-checkbox mdl-js-checkbox mdl-js-ripple-effect wm-checkbox ${classList}"
	id="${id}"
	${not empty badge ? "data-badge=" : ""}${not empty badge ? badge : ""}
>
  <input type="checkbox" class="mdl-checkbox__input" name="${name}" value="${value}" ${isChecked ? 'checked' : ''} ${checked} >
  <span class="mdl-checkbox__label" ${not empty badge ? "data-badge-content" : ""}><jsp:doBody /></span>
</label>
