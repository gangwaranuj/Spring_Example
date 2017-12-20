<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="wmfmt" uri="http://www.workmarket.com/taglib/fmt" %>
<%@ taglib prefix="wm-csrf" uri="http://www.workmarket.com/taglib/csrf" %>

<form action="/assignments/drop_add_label/${work.workNumber}/${label.id}" id='drop_add_label_form' class='form-stacked'
	  method="POST">
	<wm-csrf:csrfToken />

	<div class="messages"></div>

	<label>Label: <c:out value="${label.description}"/></label>

	<div class="clearfix">
		<label for="label_note">Note:</label>

		<p class="dn" id="label_note_instructions"></p>

		<div class="input">
			<textarea name='note' id='label_note' class='span8'></textarea>
			<input type="hidden" id="millisOffset" value="${wmfmt:escapeJavaScript(assignment_tz_millis_offset)}" />
		</div>
	</div>

	<c:if test="${work.status.code != workStatusTypes['DRAFT']}">
		<div class="dn" id="label_reschedule">
			<h4>
				Reschedule
				<small>(<a href="javascript:void(0);" class="tooltip" title="Per this client's preferences, a reschedule is required when setting this label.">Why?</a>)</small>
			</h4>

			<c:import url="/WEB-INF/views/web/partials/assignments/reschedule_form.jsp"/>
		</div>
	</c:if>

	<div class="wm-action-container">
		<button type="submit" class="button">Submit</button>
	</div>

</form>

<script type="application/json" id="json_labels"><c:out value="${label_json}" /></script>
