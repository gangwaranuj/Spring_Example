<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="wm-csrf" uri="http://www.workmarket.com/taglib/csrf" %>
<%@ taglib prefix="wm" tagdir="/WEB-INF/tags" %>

<wm:app pagetitle="Edit Agreement" bodyclass="accountSettings" webpackScript="settings">

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
					<h3>Edit Agreement</h3>
				</div>

				<form action="/agreements/edit" id='editagreement_form' method="POST" class="form-horizontal">
					<wm-csrf:csrfToken />
					<input type="hidden" name="id" value="${contract.id}"/>

					<div class="control-group">
						<label for="name" class="control-label">Name</label>

						<div class="controls">
							<input type="text" name="name" value="<c:out value="${contract_version_asset.name}" escapeXml="false" />" id="name" maxlength="200" class="input-block-level">
							<span class="help-block">The agreement name is displayed to anyone presented with this document. Title your agreement in a way that is clear about the content of the agreement.</span>
						</div>
					</div>
					<div class="control-group">
						<label for="description" class="control-label">Description</label>

						<div class="controls">
							<textarea rows="5" name="description" cols="30" id='description' class="input-block-level"><c:out
									value="${contract_version_asset.description}"/></textarea>
							<span class="help-block">Provide a brief description of the agreement. This is for you and your company only and is not displayed to others.</span>
						</div>
					</div>
					<div class="control-group">
						<label for="content_editor" class="control-label">Agreement</label>

						<div class="controls">
							<textarea rows="15" name="content" cols="30" id='content_editor' data-richtext='wysiwyg' class="input-block-level"><c:out
									value="${contract_version_asset.content}"/></textarea>
							<span class="help-block">Either copy and paste or simply type your agreement here. This could be an NDA, contractor terms, or other document you need digitally signed before someone can engage in work with your company.</span>
						</div>
					</div>
					<div class="wm-action-container">
						<a href="/agreements" class="button">Cancel</a>
						<button id="edit_agreement" class="button">Save Changes</button>
					</div>
				</form>
			</div>
		</div>
	</div>

</wm:app>
