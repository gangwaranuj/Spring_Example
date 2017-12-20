<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="wmfmt" uri="http://www.workmarket.com/taglib/fmt" %>
<%@ taglib prefix="wm-csrf" uri="http://www.workmarket.com/taglib/csrf" %>

<form action="/assignments/add_label/${work.workNumber}" id='add_label_form' class='form-stacked' method="POST">
	<wm-csrf:csrfToken />
	<div class="messages"></div>

	<div class="control-group">
		<label for="select_label" class="control-label required">Label</label>
		<div class="controls">
			<select name="label_id" id='select_label' class='input-block-level'>
				<option value="">- Select -</option>
				<c:forEach items="${labels}" var="label">
					<option value="${label.key}"><c:out value="${label.value.description}" /></option>
				</c:forEach>
			</select>
		</div>
	</div>

	<div class="control-group">
		<label for="label_note" class="control-label">Message</label>
		<div class="controls">
			<textarea name='note' id='label_note' class='input-block-level'></textarea>
		</div>
		<span class="help-block dn" id="label_note_instructions"></span>
	</div>

		<div class="dn" id="label_reschedule">
			<h4>
				Reschedule
				<small>(<a href="javascript:void(0);" class="tooltip" title="Per this company's preferences, a reschedule is required when setting this label.">Why?</a>)</small>
			</h4>

			<c:import url="/WEB-INF/views/web/partials/assignments/reschedule_form.jsp" />
		</div>

	<div class="wm-action-container">
		<button type="submit" class="button">Submit</button>
	</div>
</form>

<script type="application/json" id="json_labels"><c:out value="${label_json}" /></script>