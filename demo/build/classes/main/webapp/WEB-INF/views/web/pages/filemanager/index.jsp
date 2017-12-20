<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="wm-csrf" uri="http://www.workmarket.com/taglib/csrf" %>
<%@ taglib prefix="wmfmt" uri="http://www.workmarket.com/taglib/fmt" %>
<%@ taglib prefix="wm" tagdir="/WEB-INF/tags" %>

<wm:app pagetitle="File Manager" bodyclass="accountSettings" webpackScript="settings">

	<script>
		var config = {
			mode: 'filemanager'
		};
	</script>

	<sec:authorize access="hasRole('PERMISSION_ACCESSMMW')" var="hasMmwSidebar"/>

	<c:if test="${hasMmwSidebar}">
		<div class="row_sidebar_left">

		<div class="sidebar">
			<c:import url="/WEB-INF/views/web/partials/mmw/mmw_sidebar.jsp"/>
		</div>
	</c:if>

	<div class="content">
		<div id="custom_message">
			<div class="alert alert-error dn">
				<div></div>
			</div>
			<div class="alert alert-success dn">
				<div></div>
			</div>
		</div>

		<c:import url="/WEB-INF/views/web/partials/general/notices.jsp">
			<c:param name="bundle" value="${bundle}"/>
		</c:import>
		<c:import url="/WEB-INF/views/web/partials/general/notices_js.jsp">
			<c:param name="containerId" value="dynamic_messages"/>
		</c:import>
		<div class="inner-container">

			<div class="page-header">
				<a class="button pull-right" id="upload_file_button">Upload New File</a>
				<h3>File Manager</h3>
			</div>

			<form action="/filemanager/add" id="upload_form" method="POST" class="form-horizontal">
				<wm-csrf:csrfToken />
				<div id="upload_form_container" class="dn">
					<h4>Upload a File</h4>

					<div class="control-group">
						<label class="control-label required" id="file_label">File</label>

						<div class="controls" id="upload">
							<div id="file-uploader"></div>
							<input type="hidden" name="upload_uuid" value="<c:out value="${uploadForm.upload_uuid}" />" id="upload_uuid" />
						</div>
					</div>

					<div class="control-group">
						<label for="file_description" class="control-label">Description</label>

						<div class="controls">
							<textarea name="description" rows="3" class="span7" id="file_description"><c:out value="${uploadForm.description}" /></textarea>
						</div>

						<div class="wm-action-container">
							<a class="button" id="cancel_upload_file_form">Cancel</a>
							<a class="button" id="submit_upload_file_form">Complete Upload</a>
						</div>
					</div>
				</div>
			</form>

			<table id="documents_list">
				<thead>
				<tr>
					<th>File</th>
					<th>Description</th>
					<th>Added By</th>
					<th class="text-center">Download</th>
					<th class="text-center">Edit</th>
					<th class="text-center">Delete</th>
				</tr>
				</thead>
				<tbody></tbody>
			</table>
		</div>
	</div>
	<c:if test="${hasMmwSidebar}">
		</div>
	</c:if>


	<script type="text/x-jquery-tmpl" id="qq-uploader-tmpl">
		<div class="qq-uploader">
			<ul class="qq-upload-list unstyled"></ul>
			<div class="qq-upload-drop-area"><span>Drop photo here to upload</span></div>
			<a href="javascript:void(0);" class="qq-upload-button">Select a file</a>
		</div>
	</script>

	<script id="tmpl-qq-uploader-attachment" type="text/x-jquery-tmpl">
		<div class="qq-uploader">
			<div class="qq-upload-drop-area"><span>Drop attachment here to upload</span></div>
			<a href="javascript:void(0);" class="qq-upload-button button">Choose File</a>
			<ul class="qq-upload-list unstyled"></ul>
		</div>
	</script>

	<script id="tmpl-asset" type="text/x-jquery-tmpl">
		<a href="/asset/download/${uuid}" class="fixed">{{if
			file_name}}<c:out value="${file_name}" />{{else}}<c:out value="${name}" />{{/if}}</a>
		<p>
			<a href="asset/download/${uuid}">{{if file_name}}<c:out value="${file_name}" />{{else}}<c:out value="${name}" />{{/if}}</a><br/>
			<c:out value="${description}" />
		</p>
		<a class="db pa close">remove</a>
	</script>
</wm:app>
