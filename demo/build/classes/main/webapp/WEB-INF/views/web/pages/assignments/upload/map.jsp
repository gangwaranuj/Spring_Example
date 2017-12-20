<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="wmfmt" uri="http://www.workmarket.com/taglib/fmt" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="wm" tagdir="/WEB-INF/tags" %>

<wm:app pagetitle="WorkUpload" bodyclass="accountSettings" webpackScript="uploader">

	<script>
		var config = {
			mode: 'mapping',
			response: ${responseJson},
			initialFieldTypes: ${fieldTypesJson},
			initialFieldCategories: ${fieldTypeCategoryNamesJson}
		};
	</script>


	<div class="inner-container">
		<div class="page-header">
			<h3>Assignment Upload</h3>
		</div>

		<div class="alert alert-info">
			Map the columns of your uploaded file to the assignment fields.
			Be sure to save your mapping if you plan to upload assignments in the same
			format again.
			<a href="https://workmarket.zendesk.com/hc/en-us/articles/210052707-What-additional-tools-are-available-for-streamlining-assignment-creation" target="_blank">
				<strong>Learn more <i class="icon-info-sign"></i></strong>
			</a>
		</div>

		<form:form modelAttribute="uploadForm" id="map-form">
			<form:hidden path="headersProvided" value="1"/>
			<form:hidden path="uploadUuid"/>
			<form:hidden path="templateId"/>
			<form:hidden path="labelId"/>

			<div class="row">
				<div class="span7">
					<dl>
						<dt>Source File</dt>
						<dd><code><c:out value='${wmfmt:truncate(upload.filename, 55, 55, "...")}'/></code> <span class="label notice"><c:out value="${response.uploadCount}"/> <c:out
								value="${wmfmt:pluralize('record', response.uploadCount)}"/></span></dd>
						<dd><form:checkbox path="headersProvided"/> Source has column headers</dd>
					</dl>
				</div>
				<div class="span4">
					<dl>
						<dt>Template</dt>
						<dd><c:out value="${template.templateName}" default="<None>"/></dd>
					</dl>
				</div>
				<div class="span4">
					<dl>
						<dt>Mapping</dt>
						<dd>
				<span class="mapping-group-name-outlet">
				<c:choose>
					<c:when test="${response.mappingGroup.setId}">
						<c:out value="${response.mappingGroup.name}"/>
					</c:when>
					<c:otherwise>
						&lt;None&gt;
					</c:otherwise>
				</c:choose>
				</span>
						</dd>
					</dl>
				</div>
			</div>

			<jsp:include page="/WEB-INF/views/web/partials/general/notices.jsp"/>

			<div id="dynamic_messages"></div>
			<div id="warnings"></div>
			<div id="errors"></div>

			<table id="field-mappings">
				<thead>
					<tr>
						<th>Source File Column</th>
						<th>Assignment Field</th>
						<th>Sample Value</th>
					</tr>
				</thead>
				<tbody></tbody>
			</table>

			<div id="save-mapping-info" style="display: none">
				<div class="alert alert-info">
					<p>
						<strong>Tip:</strong>
						Be sure to save your mapping if you plan to upload assignments in the same
						format again.
					</p>
				</div>

				<c:if test="${template.manageMyWorkMarket.showInFeed}">
					<div class="alert">
						<strong>Warning:</strong>
						Your chosen template,
						<c:out value="${template.templateName}" default="<None>"/>,
						is set to send these assignments to the Work Feed, where they will be
						publicly viewable. If this is not what you want, you can go
						<a href="/assignments/template_edit/<c:out value="${template.workNumber}" />">here</a>
						to modify the template or choose another one.
					</div>
				</c:if>
			</div>

			<div class="wm-action-container" id="upload-actions">
				<a href="/assignments/upload" id="back-outlet" class="button">Back</a>
				<button type="button" id="preview-upload-outlet" class="button">Preview <i class="wm-icon-follow"></i></button>
				<a href="/assignments/upload/create_mapping" id="save-mapping-outlet" class="button">Save Mapping</a>
				<button type="button" id="save-upload-outlet-async" class="button">Create Drafts</button>
			</div>

		</form:form>
	</div>

	<script id="tmpl-field-mapping-row" type="text/x-jquery-tmpl" charset="utf-8">
		<tr>
			<td class="nowrap">\${column_index + 1}. \${column_name}</td>
			<td>
				<input type="hidden" name="mapping.mappings[\${column_index}].columnIndex" value="\${column_index}"/>
				<input type="hidden" name="mapping.mappings[\${column_index}].columnName" value="\${column_name}"/>
				<input type="hidden" name="mapping.mappings[\${column_index}].sampleValue" value="\${value}"/>
				<select name="mapping.mappings[\${column_index}].type">
					<option value="ignore">----- (ignore) -----</option>
					<c:forEach var="c" items="${fieldTypeCategories}">
		<optgroup label="<c:out value="${c.key}" />">
		<c:forEach var="item" items="${c.value}">
			<option value="${item.key}"><c:out value="${item.value}"/></option>
		</c:forEach>
		</optgroup>
	</c:forEach>
				</select>
			</td>
			<td>{{html value}}</td>
		</tr>
	</script>

	<script id="tmpl-preview" type="text/x-jquery-tmpl" charset="utf-8">
		<div>
			<p>The first 400 characters of any fields are previewed below. Any HTML formatting is removed for the preview, but remains intact for your import.</p>
			<hr/>

			<div class="pagination">
				<ul>
					<li class="prev"><a>&laquo; Previous</a></li>
					<li class="status"><span>Assignment <span class="current_page">\${current_page}</span> of <span class="num_pages">\${num_pages}</span></span></li>
					<li class="next"><a>Next &raquo;</a></li>
				</ul>
				&nbsp;
				<span id="preview_limited" class="label notice dn">NOTE: Only previewing first 25 assignments.</span>
			</div>

			<table id="mapping-preview"></table>
		</div>
	</script>

	<script id="tmpl-preview-row" type="text/x-jquery-tmpl" charset="utf-8">
		<tr>
			<td class="tar">\${key}</td>
			<td><span {{if newValue}}class="red"{{/if}}>{{html value}}</span></td>
		</tr>
	</script>

	<script id="tmpl-errors" type="text/x-jquery-tmpl" charset="utf-8">
		<div class="alert alert-error">
			<p><strong>The following errors were found with the source file and mapping:</strong></p>
			{{each(i, row) errors}}
			<div class="row">
				<div class="span1 nowrap">
					<p>Line \${row.lineNumber}:</p>
				</div>
				<div class="span12">
					<p>
						{{each(j, e) row.errors}}
						\${e.violation.why}<br/>
						{{/each}}
					</p>
				</div>
			</div>
			{{/each}}
		</div>
	</script>

	<script id="tmpl-warnings" type="text/x-jquery-tmpl" charset="utf-8">
		<div class="alert alert-warning">
			<p><strong>The following warnings were found with the source file and mapping:</strong></p>
			<ul>
				{{each(i, e) errors}}
				<li>\${e.violation.why}</li>
				{{/each}}
			</ul>
		</div>
	</script>
</wm:app>
