<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="wm-csrf" uri="http://www.workmarket.com/taglib/csrf" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="wmfmt" uri="http://www.workmarket.com/taglib/fmt" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="wm" tagdir="/WEB-INF/tags" %>

<wm:app pagetitle="WorkUpload" bodyclass="page-assignment-upload" webpackScript="uploader">
	<script>
		var config = {
			mode: 'setup'
		};
	</script>

	<div class="sidebar-card">
		<h3 class="sidebar-card--title">
			<jsp:include page="/WEB-INF/views/web/partials/wm-branding/work-upload.jsp"/>
		</h3>
		<p>
			The <jsp:include page="/WEB-INF/views/web/partials/wm-branding/work-upload.jsp"/> tool allows you to create multiple draft assignments by
			uploading a spreadsheet (in CSV format) containing assignment details. Draft assignments created using this method can be
			based on an existing template. Created drafts can be routed directly to workers or talent pools.
		</p>

		<jsp:include page="/WEB-INF/views/web/partials/message.jsp"/>

		<form:form action="/assignments/upload/map" enctype="multipart/form-data" modelAttribute="uploadForm" method="POST" class="form-horizontal">
			<div id="upload-form">
				<wm-csrf:csrfToken/>
				<input type="hidden" name="headersProvided" value="1"/>
				<input type="hidden" name="preview" value="1"/>

				<div>
					<c:import url="/WEB-INF/views/web/partials/general/notices_js.jsp">
						<c:param name="containerId" value="dynamic_message"/>
					</c:import>
					<label class="required control-label">File to Upload</label>
					<div class="control-group">
						<div class="controls">
							<input type="file" name="upload" id="assignment-upload" />
							<span class="help-block">Need a sample assignment upload template? <a href="https://workmarket.zendesk.com/hc/en-us/articles/210052707" target="_blank">
							<strong> Learn More <i class="icon-info-sign"></i></strong></a></span>
						</div>
					</div>
					<div id="mapping-select"></div>
					<div id="template-select"></div>
					<div id="label-select"></div>
				</div>
			</div>
			<div class="wm-action-container">
				<button type="submit" class="button -primary">Continue &raquo;</button>
			</div>
		</form:form>
	</div>

	<script id="tmpl-template-select" type="text/x-jquery-tmpl">
		<div class="control-group">
			<label for="templateId" class="control-label">Template</label>

			<div class="controls">
				<select id="templateId" name="templateId">
					<option value="">None</option>
					<optgroup label="Templates">
						{{each(row, item) templates}}
						<option value="\${item.id}">\${item.name}</option>
						{{/each}}
					</optgroup>
				</select>

				{{if templates.length > 0}}
					<span class="help-block">Alternatively, you can provide template IDs in your upload file. <a href="/settings/manage/templates.csv">Download your
						template IDs <i class="wm-icon-download"></i></a></span>
				{{/if}}
			</div>
		</div>
	</script>

	<script id="tmpl-label-select" type="text/x-jquery-tmpl">
		<div class="control-group">
			<label for="labelId" class="control-label">Upload Label</label>

			<div class="controls" id="labels_dashboard">
				<select id="labelId" name="labelId">
					<option value="">None</option>
					<optgroup label="Labels">
						{{each(row, item) labels}}
						<option value="\${item.id}">\${item.description}</option>
						{{/each}}
					</optgroup>
				</select>
				<span class="help-inline"><a href="/settings/manage/labels_manage" rel="add" class="cta-manage-label">Add New Label</a></span>
				<span class="help-block">You can select a label to be applied to all drafts in this upload.</span>
			</div>
		</div>
	</script>

	<script id="tmpl-mapping-select" type="text/x-jquery-tmpl">
		<div class="control-group">
			<label for="mapping.id" class="control-label">Mapping</label>

			<div class="controls">
				<select id="mapping.id" name="mapping.id">
					<option value="">None</option>
					{{if mappings.length > 0}}
					<optgroup label="Mappings">
						{{each(row, item) mappings}}
						<option value="\${item.id}">\${item.name}</option>
						{{/each}}
					</optgroup>
					{{/if}}
				</select>
				<span class="help-block"><a href="/assignments/upload/mappings" class="mappings-outlet">Manage mappings</a></span>
			</div>
		</div>
	</script>

</wm:app>
