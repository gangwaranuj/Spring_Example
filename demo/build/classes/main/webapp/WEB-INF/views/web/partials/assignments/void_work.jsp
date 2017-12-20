<%@ taglib prefix="wm-csrf" uri="http://www.workmarket.com/taglib/csrf" %>

<style type="text/css">
	.ui-datepicker {
		z-index:10000 !important;
	}
</style>

<form action="/assignments/void_work/${work.workNumber}" id="void_work_form" class='form-stacked'  method="POST">
	<wm-csrf:csrfToken />

	<input type="hidden" name="id" value="${work.workNumber}"/>

	<div class="messages"></div>

	<p>Are you sure you want to void this assignment? No charges will be incurred.</p>

	<div class="control-group">
		<label name="void_note" class="control-label required">Note</label>

		<div class="controls">
			<textarea name="void_note" id="void_note" class="span8" rows="4"></textarea>
		</div>
	</div>

	<div class="wm-action-container">
		<button type="submit" class="button">Void Assignment</button>
	</div>
</form>
