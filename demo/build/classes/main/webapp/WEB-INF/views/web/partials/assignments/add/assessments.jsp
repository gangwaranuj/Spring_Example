<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<div id="assignment-assessments" class="inner-container dn">
	<div class="control-group">
		<div class="page-header">
			<h4>Surveys</h4>
		</div>
		<label for="assessment_id" class="control-label">Surveys</label>
		<div class="controls">
			<form:select path="" id="assessment_id">
				<form:option value="" />
				<form:options items="${surveys}" />
			</form:select>
			<a href="javascript:void(0);" class="button" id="cta-add-survey">Add</a>
			<a href="/lms/manage/surveys">Manage Surveys</a>
			<span class="help-block">
				Add or remove surveys that will be included with this assignment. Your
				surveys are presented to the assigned worker as part of the completion
				process. If taking the survey is required to complete the assignment,
				check the Required box next to the survey title.
			</span>
			<ul id="selected_assessments" class="skill-list"></ul>
		</div>
	</div>
</div>

<script type="text/x-jquery-tmpl" id="tmpl-assessment-item">
	<li class="clear" style="width: 400px;">
		<input type="hidden" name="assessments[\${index}].id" value="\${id}" />
		<span class="fl">\${name}</span>
		<span class="fr">
			{{if required}}
				<label class="dib"><input type="checkbox" name="assessments[\${index}].isRequired" value="1" checked="true" /> Required</label>
			{{else}}
				<label class="dib"><input type="checkbox" name="assessments[\${index}].isRequired" value="1" /> Required</label>
			{{/if}}
			<a href="javascript:void(0);" class="cta-remove-survey">Remove</a>
		</span>
	</li>
</script>
