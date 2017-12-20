<%@ taglib prefix="wm-csrf" uri="http://www.workmarket.com/taglib/csrf" %>

<div class="dn">
	<div id="addto_filemanager_form_container">
		<form action="/filemanager/add_new_asset_external" method="post" id="addto_filemanager_form" accept-charset="utf-8" class="form-stacked">
			<wm-csrf:csrfToken />
			<input type="hidden" id="asset_id" name="asset_id" value="" />
			<input type="hidden" name="file_display_with_profile" value="1" />
			<div class="clearfix">
				<label for="file_description">Description</label>
				<div class="input">
					<textarea id="file_description" name="file_description" value="${file_description}"></textarea>
				</div>
			</div>
			<div class="wm-action-container">
				<button type="button" class="button" id="addto_filemanager_form_close">Cancel</button>
				<button type="button" class="button" id="addto_filemanager_form_submit">Submit</button>
			</div>
		</form>	
	</div>
</div>
