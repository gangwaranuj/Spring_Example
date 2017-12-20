<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="wm-csrf" uri="http://www.workmarket.com/taglib/csrf" %>
<%@ taglib prefix="wm" tagdir="/WEB-INF/tags" %>

<wm:app pagetitle="Add Agreement" bodyclass="accountSettings" webpackScript="settings">

	<script>
		var config = {
			mode: 'agreementsView'
		};
	</script>

	<div class="row_wide_sidebar_left">
		<div class="sidebar">
			<c:import url="/WEB-INF/views/web/partials/mmw/mmw_sidebar.jsp"/>
		</div>

		<div class="content">
			<div class="inner-container">
				<div class="page-header">
					<h3>New Agreement</h3>
				</div>

				<form action='/agreements/add' id='addagreement_form' method="POST" class="form-horizontal">
					<wm-csrf:csrfToken />

					<fieldset>
						<div class="control-group">
							<label for="name" class="control-label required">Name</label>

							<div class="controls">
								<input name="name" type="text" value="<c:out value="${addagreement_form.name}" />" id="name" maxlength="200" class="input-block-level">
								<span class="help-block"><i class="icon-info-sign"></i> The agreement name is displayed to anyone presented with this document. Title your agreement in a way that is clear about the content of the agreement.</span>
							</div>
						</div>

						<div class="control-group">
							<label for="description" class="control-label">Description</label>

							<div class="controls">
								<textarea rows="5" name="description" cols="30" id='description'
										  class='input-block-level'><c:out value="${addagreement_form.description}" /></textarea>
								<span class="help-block"><i class="icon-info-sign"></i> Provide a brief description of the agreement. This is for you and your company only and is not displayed to others.</span>
							</div>
						</div>

						<div class="control-group">
							<label for="content_editor" class="control-label required">Agreement</label>

							<div class="controls">
								<textarea rows="15" name="agreement" cols="30" id='content_editor' data-richtext='wysiwyg'
										  class='input-block-level'><c:out value="${addagreement_form.agreement}" /></textarea>
								<span class="help-block"><i class="icon-info-sign"></i> Either copy and paste or simply type your agreement here. This could be an NDA, contractor terms, or other document you need digitally signed before a worker can engage in work with your company.</span>
							</div>
						</div>
					</fieldset>
					<div class="wm-action-container">
						<a href="/agreements" class="button">Cancel</a>
						<button type="submit" id="add_agreement" class="button">Save Changes</button>
					</div>
				</form>
			</div>
		</div>
	</div>

</wm:app>
