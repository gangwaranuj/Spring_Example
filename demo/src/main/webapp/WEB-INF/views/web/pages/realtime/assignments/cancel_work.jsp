<%@ taglib prefix="wm-csrf" uri="http://www.workmarket.com/taglib/csrf" %>

<form action="/realtime/cancel_work" id='form_cancel_work' method="post">
	<wm-csrf:csrfToken />
	<input type="hidden" name="id" value="${work.workNumber}"/>

	<div class="messages"></div>

	<p>Are you sure you want to void this assignment? No charges will be incurred.</p>

	<div class="control-group">
		<label name="void_note" class="control-label required">Note</label>

		<div class="controls">
			<textarea name="note" id="cancel_note" class="span8" rows="4"></textarea>
		</div>
	</div>

	<div class="wm-action-container">
		<button type="submit" class="button">Void Assignment</button>
	</div>
</form>
