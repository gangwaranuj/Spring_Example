<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="wm-csrf" uri="http://www.workmarket.com/taglib/csrf" %>
<%@ taglib prefix="wm" tagdir="/WEB-INF/tags" %>

<wm:app pagetitle="Edit File" bodyclass="accountSettings">

	<div class="row_wide_sidebar_left">
		<div class="sidebar">
			<c:import url="/WEB-INF/views/web/partials/mmw/mmw_sidebar.jsp"/>
		</div>

		<div class="content">
			<div class="inner-container">

				<div class="page-header"><h3>Edit File</h3></div>

				<c:import url="/WEB-INF/views/web/partials/general/notices.jsp">
					<c:param name="bundle" value="${bundle}"/>
				</c:import>

				<form action="#" method="POST" class="form-horizontal">
					<wm-csrf:csrfToken />
					<input type="hidden" name="id" value="<c:out value="${param.id}" />"/>

					<fieldset>
						<div class="control-group">
							<label for="file_name" class='control-label required'>Name</label>

							<div class="controls">
								<input name="name" type="text" value="<c:out value="${asset.name}" />" id="file_name">
							</div>
						</div>

						<div class="control-group">
							<label for="file_description" class='control-label required'>Description</label>

							<div class="controls">
							<textarea name="description" rows="3" cols="20" id='file_description' class="input-block-level"><c:out
									value="${asset.description}"/></textarea>
							</div>
						</div>

						<div class="control-group">
							<label name="file" class="control-label">File</label>

							<div class="controls">
								<a class="text" href="<c:out value="/asset/download/${asset.UUID}"/>">Download</a></span>
							</div>
						</div>

						<div class="control-group">
							<label name="type" class="control-label">Type</label>

							<div class="controls">
								<div class="text"><c:out value="${asset.mimeType}"/></div>
							</div>
						</div>

						<div class="wm-action-container">
							<c:choose>
								<c:when test="${param.return_to == 'images'}">
									<a href="/filemanager?section=${param.return_to}" class="button">Cancel</a>
								</c:when>
								<c:otherwise>
									<a href="/filemanager" class="button">Cancel</a>
								</c:otherwise>
							</c:choose>
							<button type="submit" class="button">Save Changes</button>
						</div>
					</fieldset>
				</form>
			</div>
		</div>
	</div>

</wm:app>
