<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="wmfmt" uri="http://www.workmarket.com/taglib/fmt" %>
<%@ taglib prefix="wm" tagdir="/WEB-INF/tags" %>

<wm:app pagetitle="Learning Center" bodyclass="lms" webpackScript="lms">

	<script>
		var config = {
			mode: 'assets',
			assessmentId: ${wmfmt:escapeJavaScript(assessment.id)}
		};
	</script>

	<c:set var="isSurvey" value="${assessment.type.value eq AssessmentType.SURVEY}" />

	<p>
		<c:choose>
			<c:when test="${not empty work}">
				<a href="<c:url value="/assignments/details/${work.workNumber}"/>">&laquo; Back to Assignment</a>
			</c:when>
			<c:otherwise>
				<a href="<c:url value="/lms/view/details/${assessment.id}"/>">&laquo; Back to Survey</a>
			</c:otherwise>
		</c:choose>
	</p>

	<form:form modelAttribute="filterForm" id="filter_options">
		<c:choose>
			<c:when test="${not empty attemptId}">
				<form:hidden path="attemptId" />
			</c:when>
			<c:otherwise>
				<fieldset class="filters form-stacked">
					<div class="row">
						<div class="clearfix span3">
							<label for="filter_client">Client</label>
							<div class="input">
								<form:select path="client" id="filter_client" cssClass="span3">
									<form:option value="">Client</form:option>
									<form:options items="${clients}" />
								</form:select>
							</div>
						</div>
						<div class="clearfix span3">
							<label for="filter_project">Project</label>
							<div class="input">
								<form:select path="project" id="filter_project" cssClass="span3">
									<form:option value="">Project</form:option>
									<form:options items="${projects}" />
								</form:select>
							</div>
						</div>
						<div class="clearfix span3">
							<label for="filter_resources">Workers</label>
							<div class="input">
								<form:select path="userNumber" id="filter_resources" cssClass="span3">
									<form:option value="">Workers</form:option>
									<form:options items="${resources}" />
								</form:select>
							</div>
						</div>
						<div class="clearfix span3">
							<label for="created_on_from">Date uploaded</label>
							<div class="input">
								<form:input path="createdOnFrom" id="created_on_from" maxlength="10" placeholder="mm/dd/yyyy" class="datepicker input-small" />
								<form:input path="createdOnThrough" id="created_on_through" maxlength="10" placeholder="mm/dd/yyyy" class="datepicker input-small" />
							</div>
						</div>
						<div class="clearfix span3">
							<label>&nbsp;</label>
							<div class="input">
								<button id="filters_apply" class="button">Apply</button>
								<button id="filters_clear" class="button">Clear</button>
							</div>
						</div>
					</div>
				</fieldset>
			</c:otherwise>
		</c:choose>
	</form:form>

	<table class="group-list" id="assets_list">
		<thead>
			<tr>
				<th width="15"><input type="checkbox" name="select_all" value="1" id="select_all" /></th>
				<th width="48">&nbsp;</th>
				<th width="115">&nbsp;</th>
				<th width="150">Assignment</th>
				<th width="125">Worker</th>
				<th width="100">Uploaded On</th>
				<th>Caption</th>
			</tr>
		</thead>
		<tbody></tbody>
	</table>

	<p>
		<a href="/lms/manage/download_assets" id="download_selected">Download Selected (0 items, 0 B)</a> |
		<a href="/lms/manage/download_assessment_assets/${assessment.id}" id="download_all">Download All (0 items, 0 B)</a>
	</p>

	<script id="select-cell-tmpl" type="text/x-jquery-tmpl">
		<div>
			<input type="checkbox" name="selected[]" value="\${meta.uuid}" id="select_\${meta.uuid}" />
		</div>
	</script>

	<script id="thumbnail-cell-tmpl" type="text/x-jquery-tmpl">
		<div>
			<a href="/lms/manage/asset/${assessment.id}/\${meta.id}" class="view_asset"><img src="\${meta.smallThumbnailUri}" alt="Photo" /></a>
		</div>
	</script>

	<script id="meta-cell-tmpl" type="text/x-jquery-tmpl">
		<div>
			<a href="/lms/manage/asset/${assessment.id}/\${meta.id}" class="view_asset">\${meta.name}</a>
			<div class="gray">
				\${bytesToSize(meta.fileByteSize, 1)}<br />
				Question \${meta.itemPosition}
			</div>
		</div>
	</script>

	<script id="assignment-cell-tmpl" type="text/x-jquery-tmpl">
		<div>
			{{if meta.work_number}}
				<div>
					<a href="/assignments/details/\${meta.workNumber}" target="_blank">\${meta.workNumber}</a>
					(\${dateFromMillis(meta.workScheduledFrom).format('m/dd/yyyy')})<br />
					\${meta.workLocationName}<br />
					\${meta.workLocationNumber}
				</div>
			{{/if}}
		</div>
	</script>

	<script id="resource-cell-tmpl" type="text/x-jquery-tmpl">
		<div>
			<a href="/profile/\${meta.creatorUserNnumber}" target="_blank">\${meta.creatorFirstName} \${meta.creatorLastName}</a><br />
			\${meta.creatorCompanyName}
		</div>
	</script>

	<script id="uploadedon-cell-tmpl" type="text/x-jquery-tmpl">
		<div>
			\${dateFromMillis(meta.createdOn).format('m/dd/yyyy')}
		</div>
	</script>

	<script id="caption-cell-tmpl" type="text/x-jquery-tmpl">
		<div>
			\${meta.description}
		</div>
	</script>

</wm:app>
