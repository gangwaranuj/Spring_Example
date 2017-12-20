<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="wmfmt" uri="http://www.workmarket.com/taglib/fmt" %>
<%@ taglib prefix="wm-csrf" uri="http://www.workmarket.com/taglib/csrf" %>

<c:if test="${success}">
	<form action="javascript:void(0);" id="add_label_form" class="form-stacked" method="POST">
		<wm-csrf:csrfToken />

		<div class="clearfix">
			<label class="required">Label:</label>

			<div class="input">
				<select name="bulk_edit_label" id="bulk_edit_label" data-placeholder="Select Label">
					<option value="">- Select -</option>
					<c:forEach items="${labels}" var="label">
						<option value="${label.key}"><c:out value="${label.value.description}" /></option>
					</c:forEach>
				</select>
			</div>
		</div>

		<div class="clearfix">
			<label for="label_note">Note:</label>

			<p class="dn" id="label_note_instructions"></p>

			<div class="input">
				<textarea name="note" id="label_note"></textarea>
			</div>
		</div>

		<c:if test="${not isThereDraft}">
			<div class="dn" id="label_reschedule">
				<h4>
					Reschedule
					<small><span class="tooltipped tooltipped-n" aria-label="Per the company settings set by your administrator, a reschedule is required when setting this label."><i class="wm-icon-question-filled"></i></span></small>
				</h4>

				<c:import url="/WEB-INF/views/web/partials/assignments/reschedule_bulk_form.jsp" />
			</div>
		</c:if>

		<div class="wm-action-container">
			<button data-modal-close class="button">Close</button>
			<button type="submit" class="button" id="add_mult_label">Add Label</button>
		</div>
	</form>

	<script type="application/json" id="json_labels"><c:out value="${label_json}" /></script>
</c:if>