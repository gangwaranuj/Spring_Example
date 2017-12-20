<%@ tag description="Radio Input" %>
<%@ attribute name="name" required="true" %>
<%@ attribute name="value" required="true" %>
<%@ attribute name="attributes" required="false" %>
<%@ attribute name="isDisabled" required="false" %>
<%@ attribute name="isChecked" required="false" %>
<%@ attribute name="id" required="false" %>

<label class="wm-radio mdl-radio mdl-js-radio mdl-js-ripple-effect" id="${id}">
	<input type="radio" name="${name}" class="mdl-radio__button" value="${value}" ${isChecked ? 'checked' : ''} ${isDisabled ? 'disabled' : ''} ${attributes} />
	<span class="mdl-radio__label"><jsp:doBody /></span>
</label>
