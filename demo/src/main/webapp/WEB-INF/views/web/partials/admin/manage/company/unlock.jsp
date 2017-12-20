<%@ taglib prefix="wm-csrf" uri="http://www.workmarket.com/taglib/csrf" %>

<form action="/admin/manage/company/unlock/" enctype="multipart/form-data" id="form_unlock" method="post" class="form-horizontal">
	<wm-csrf:csrfToken />
	<div class="alert alert-error dn">
		<div></div>
	</div>

	<div class="control-group">
		<label class="required control-label">Unlock for:</label>

		<div class="controls">
			<select name="hours">
				<option>- Hours -</option>
				<option value="24">24 hours</option>
				<option value="48">48 hours</option>
				<option value="72">72 hours</option>
			</select>
		</div>
	</div>

	<div class="control-group">
		<label class="required control-label">Comment:</label>
		<div class="controls">
			<textarea class="small" name="comment"></textarea>
		</div>
	</div>

	<div class="control-group">
		<label class="control-label">Attachment:</label>
		<div class="controls">
			<input type="file" name="qqfile"/>
		</div>
	</div>

	<div class="control-group">
		<label class="control-label">Description of attachment:</label>
		<div class="controls">
			<textarea class="small" name="description"></textarea>
		</div>
	</div>

	<div class="form-actions">
		<button class="button">Unlock</button>
	</div>
</form>