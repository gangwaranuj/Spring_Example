<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="wmfmt" uri="http://www.workmarket.com/taglib/fmt" %>
<%@ taglib prefix="wm-csrf" uri="http://www.workmarket.com/taglib/csrf" %>

<c:if test="${not success}">
	<div id="assets"><div class="alert alert-error" id="labelAssignmentsfailed"><ul class="unstyled"><li>There was an error please try again.</li></ul></div>
</c:if>

<c:if test="${success}">
	<form onsubmit="return false;" id='remove_label_form' class='form-stacked' method="POST">
		<wm-csrf:csrfToken />

		<div class="clearfix">
			<label for="bulk_edit_label" class="required">Label:</label>
			<select name="bulk_edit_label" id="bulk_edit_label">
				<option value="">- Select -</option>
				<option id='remove_all_labels' value='-1'>REMOVE ALL LABELS</option>
				<c:forEach items="${labels}" var="label">
					<option value="${label.key}"><c:out value="${label.value.description}" /></option>
				</c:forEach>
			</select>
		</div>

		<div class="clearfix">
			<label for="label_note">Note:</label>
			<p class="dn" id="label_note_instructions"></p>
			<div class="input">
				<textarea name="note" id="label_note"></textarea>
			</div>
		</div>


		<div class="wm-action-container">
			<button data-modal-close class="button">Close</button>
			<button type="submit" class="button" id="remove_mult_label">Submit</button>
		</div>

	</form>

	<script type="application/json" id="json_labels"><c:out value="${label_json}" /></script>
</c:if>


