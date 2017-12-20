<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>

<a name="assignmentdetails"></a>
<div class="inner-container">
	<div class="page-header">
		<c:choose>
			<c:when test="${not empty form.workNumber}">
				<h2>Edit Assignment</h2>
			</c:when>
			<c:otherwise>
				<h2>Post a New Assignment</h2>
			</c:otherwise>
		</c:choose>
	</div>

	<fieldset>
		<div class="control-group">
			<label for="title-input" class="control-label <c:if test='${not isTemplate}'>required</c:if>">Title</label>
			<div class="controls">
				<span id="remaining"></span>
				<input id="title-input" type="text" style="width: 97%;" name="title" value="<c:out value="${form.title}" /> "/>
				<span class="help-block">
					Please use descriptive titles. The first 70 characters of the title
					are highlighted to identify the portion that will be publicly viewable
					by anyone considering this assignment.
				</span>
			</div>
		</div>

		<div class="control-group">
			<label for="desc-text" class="control-label <c:if test='${not isTemplate}'>required</c:if>">Description</label>
			<div class="controls">
				<c:choose>
					<c:when test="${empty form.autotaskId && not is_autotask}">
						<form:textarea data-richtext="wysiwyg" path="description" id="desc-text" class="input-block-level" />
					</c:when>
					<c:otherwise>
						<form:textarea path="description" id="desc-text" class="input-block-level" rows="10"/>
					</c:otherwise>
				</c:choose>
				<span class="help-block">
					The description field is publicly viewable. Please use this field for
					high level overview of the assignment details.
				</span>
			</div>
		</div>

		<div class="control-group">
			<label for="instructions-text" class="control-label">Special Instructions
				<span style="margin-top:6px;" class="tooltipped tooltipped-n" aria-label="Special instructions are optional and allow you to provide specific
 details regarding tasks and what is required to successfully complete
 the assignment. Instructions are not on printouts for the end client.">
					<i class="wm-icon-question-filled"></i>
				</span>
			</label>
			<div class="controls">
				<c:choose>
					<c:when test="${empty form.autotaskId && not is_autotask}">
						<form:textarea data-richtext="wysiwyg" path="instructions" id="instructions-text" class="input-block-level" />
					</c:when>
					<c:when test="${not empty template_id}">
						<form:textarea data-richtext="wysiwyg" path="instructions" id="instructions-text" class="input-block-level" />
					</c:when>
					<c:otherwise>
						<form:textarea path="instructions" id="instructions-text" class="input-block-level" rows="10"/>
					</c:otherwise>
				</c:choose>
				<label class="help-block">
					<form:checkbox path="privateInstructions" value="1" id="intructions-private"/>
					<i class="wm-icon-lock-circle icon-gray icon-large"></i>
					Only show to assigned worker. Instructions will not be displayed to invited workers.
				</label>
			</div>
		</div>

		<div class="control-group">
			<label for="job-title-autocomplete" class="control-label">Job Function</label>
			<div class="controls">
				<input id="job-title-autocomplete" type="text" style="width: 97%;" value=""/>

				<span class="help-block">
					e.g. Database Administrator, Desktop Technician
				</span>
			</div>
		</div>

		<div class="control-group">
			<label for="skills-autocomplete" class="control-label">Skills &amp; Specialties</label>
			<input type="hidden" id="desired_skills" name="desired_skills" value="<c:out value="${form.desired_skills}" escapeXml="false" /> " />
			<div class="controls" style="max-width: 500px;">
				<div>
					<ul class="your-skills your-skills-wrapper" name="your-skills">
						<span class="skills-list"></span>

						<div class="skills-select">
							<input id="skills-autocomplete" class="skills-autocomplete" type="text" name="skill" placeholder="Search or Select..." value="" />
						</div>
					</ul>

				</div>

				<span class="help-block">
					Provide a list of skills and specialties needed to perform the work.
				</span>

				<div class="recommended-skills">
					<p>Suggested Skills</p>
					<ul class="skills" name="skills"></ul>
				</div>
			</div>
		</div>

		<c:if test="${not empty form.autotaskId}">
			<div class="control-group">
				<label for="desc-text" class="control-label">Autotask ID</label>
				<div class="controls">
					<form:input path="autotaskId" id="autotaskId" class="input-block-level" maxlength="50" />
				</div>
			</div>
		</c:if>

		<c:choose>
			<c:when test="${is_copy}">
				<c:if test="${form.requiresUniqueExternalId}">
					<div class="control-group">
						<label for="uniqueExternalId" class="control-label required"><c:out value="${form.uniqueExternalIdDisplayName}" /></label>
						<div class="controls">
							<form:input path="uniqueExternalId" id="uniqueExternalId" class="input-block-level" maxlength="50" />
							<span class="help-block">
								Unique value is required on all assignments
							</span>
						</div>
					</div>
				</c:if>
			</c:when>
			<c:otherwise>
				<c:if test="${form.requiresUniqueExternalId || not empty form.uniqueExternalIdDisplayName}">
					<div class="control-group">
						<label for="uniqueExternalId" class="control-label required"><c:out value="${form.uniqueExternalIdDisplayName}" /></label>
						<div class="controls">
							<form:input path="uniqueExternalId" id="uniqueExternalId" class="input-block-level" maxlength="50" />
							<span class="help-block">
								Unique value is required on all assignments
							</span>
						</div>
					</div>
				</c:if>
			</c:otherwise>
		</c:choose>



	</fieldset>
</div>

<script type="text/x-jquery-tmpl" id="qq-uploader-attachment-tmpl">
	<div class="qq-uploader">
		<div class="qq-upload-drop-area"><span>Drop attachment here to upload</span></div>
		<a href="javascript:void(0);" class="qq-upload-button button">Upload File</a>
		<a id="select_filemanager_files" class="button">Add files from file manager</a>
		<ul class="qq-upload-list unstyled"></ul>
	</div>
</script>

<script type="text/x-jquery-tmpl" id="attachment-list-item-tmpl">
	<tr id="attachments_inner_\${index}" class="attachment-list-item clearfix" data-id="\${id}" data-uuid="\${uuid}">
		<td>
			<i class="icon-file icon-large icon-gray"></i>
			<input type="hidden" name="attachments[\${index}].id" value="\${id}"/>
			<input type="hidden" name="attachments[\${index}].uuid" value="\${uuid}"/>
			<input type="hidden" name="attachments[\${index}].name" value="\${file_name}"/>
		</td>

		<td>
			{{if is_upload}}
				<input type="hidden" name="attachments[\${index}].isUpload" value="1"/>
				<a class="attachment-link" href="/asset/downloadTemp/\${uuid}">\${file_name}</a>
			{{else}}
				<input type="hidden" name="attachments[\${index}].isUpload" value="0"/>
				<a class="attachment-link" href="/asset/download/\${uuid}">\${file_name}</a>
			{{/if}}
		</td>

		<td>
			{{if is_upload}}
				<input type="text" name="attachments[\${index}].description" value="\${description}" placeholder="Description" class="span3" />
			{{else}}
				\${description}
			{{/if}}
		</td>

		<td><i class="icon-remove icon-large icon-gray remove-attachment"></i></td>
	</tr>
</script>
