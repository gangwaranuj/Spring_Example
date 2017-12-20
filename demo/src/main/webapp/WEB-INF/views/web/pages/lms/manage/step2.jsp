<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="wmfn" uri="http://www.workmarket.com/taglib/functions" %>
<%@ taglib prefix="wm-csrf" uri="http://www.workmarket.com/taglib/csrf" %>
<%@ taglib prefix="wmfmt" uri="http://www.workmarket.com/taglib/fmt" %>
<%@ taglib prefix="wm" tagdir="/WEB-INF/tags" %>

<wm:app pagetitle="Questions: ${assessment.name}" bodyclass="lms" webpackScript="lms">

	<script>
		var config = {
			mode: 'manageQuestions',
			id: '${wmfmt:escapeJavaScript(assessment.id)}',
			questions: ${assessmentItems},
			questionTypes: {
				'${wmfmt:escapeJavaScript(AssessmentItemType.SINGLE_LINE_TEXT)}'    : 'singleline',
				'${wmfmt:escapeJavaScript(AssessmentItemType.MULTIPLE_LINE_TEXT)}'  : 'multiline',
				'${wmfmt:escapeJavaScript(AssessmentItemType.SINGLE_CHOICE_RADIO)}' : 'radio',
				'${wmfmt:escapeJavaScript(AssessmentItemType.MULTIPLE_CHOICE)}'     : 'checkboxes',
				'${wmfmt:escapeJavaScript(AssessmentItemType.SINGLE_CHOICE_LIST)}'  : 'dropdown',
				'${wmfmt:escapeJavaScript(AssessmentItemType.DIVIDER)}'             : 'segment',
				'${wmfmt:escapeJavaScript(AssessmentItemType.DATE)}'                : 'date',
				'${wmfmt:escapeJavaScript(AssessmentItemType.PHONE)}'               : 'phonenumber',
				'${wmfmt:escapeJavaScript(AssessmentItemType.EMAIL)}'               : 'email',
				'${wmfmt:escapeJavaScript(AssessmentItemType.NUMERIC)}'             : 'numeric',
				'${wmfmt:escapeJavaScript(AssessmentItemType.ASSET)}'               : 'asset'
			}
		};
	</script>

	<c:set var="isSurvey" value="${assessment.type.value eq AssessmentType.SURVEY}" />
	<div class="inner-container">
	<div class="page-header">
		<h2>Questions: <c:out value="${assessment.name}" /></h2>
	</div>

	<c:import url="/WEB-INF/views/web/partials/general/notices_js.jsp">
		<c:param name="containerId" value="dynamic_messages" />
	</c:import>

	<div id="lms-manage-questions-ui">
		<div class="row">
			<div class="span9">
				<form action="/lms/manage/save_question/${assessment.id}" id="manage-questions-form" method="post">
					<wm-csrf:csrfToken />
					<div id="question-form" class="form-stacked"></div>
				</form>
			</div>
			<div class="span6">
				<form action="/lms/manage/reorder_questions/${assessment.id}" id="manage-questions-reorder" class="sidebar" method="post">
					<wm-csrf:csrfToken />
					<div id="question-list">
						<h5>Questions</h5>
						<div id="question-list-container"></div>
						<p><a href="javascript:void(0);" id="cta-add-question">Add another question</a></p>
					</div>
				</form>
			</div>
		</div>
	</div>

	<script id="tmpl-question-form" type="text/x-jquery-tmpl">
		<c:if test="${assessment.type.value == AssessmentType.GRADED}">
			<div id="manual-grading-notice" class="alert-message warning dn">This question will require manual grading.</div>
		</c:if>

		<div class="page-header"><p id="question-title"><strong>\${heading}</strong></p></div>

		<input type="hidden" name="id" id="question_id" />

		<div class="clearfix">
			<label>Question Type</label>
			<div class="input">
				<select name="type" id="question_type">
				<option value="${AssessmentItemType.SINGLE_CHOICE_RADIO}">Multiple Choice</option>
				<option value="${AssessmentItemType.MULTIPLE_CHOICE}">Checkboxes</option>
				<option value="${AssessmentItemType.SINGLE_CHOICE_LIST}">Dropdown</option>
				<option value="${AssessmentItemType.SINGLE_LINE_TEXT}">Single Line Text</option>
				<option value="${AssessmentItemType.MULTIPLE_LINE_TEXT}">Paragraph Text</option>
				<option value="${AssessmentItemType.DIVIDER}">Segment Break</option>
				<option value="${AssessmentItemType.DATE}">Date</option>
				<option value="${AssessmentItemType.PHONE}">Phone</option>
				<option value="${AssessmentItemType.EMAIL}">Email</option>
				<option value="${AssessmentItemType.NUMERIC}">Number</option>
				<option value="${AssessmentItemType.ASSET}">Photo Answer</option>
				</select>
			</div>
		</div>

		<div class="clearfix">
			<label for="question_text" class="required" >Question Text</label>
			<div class="input">
				<textarea name="prompt" id="question_text" class="span8" rows="5"></textarea>
			</div>
		</div>

		<div id="question-specific-fields"></div><br />

		<div class="clearfix">
			<label>Attachments</label>
			<div class="input">
				<c:import url="/WEB-INF/views/web/partials/general/notices_js.jsp">
					<c:param name="containerId" value="attachment-messages" />
				</c:import>
				<ul id="question-assets" class="unstyled"></ul>
				<div id="attachment-uploader">
					<noscript>
						<input type="file" name="resume_file" id="resume_file" />
					</noscript>
				</div>
			</div>
		</div>

		<div class="form-actions">
			<button type="button" id="cta-save-question" class="button">Save Question</button>
			<a href="/lms/view/details/${assessment.id}" class="button pull-right">I'm Done. Back to ${assessment.type.value == AssessmentType.SURVEY ? 'Survey' : 'Test'} Details</a>
		</div>
	</script>

	<script id="tmpl-answers-table" type="text/x-jquery-tmpl">
		<label class="required" >Answers</label>
		<table class="answers-table group-list" width="100%" cellpadding="0" cellspacing="0">
			<thead>
				<tr>
					<th colspan="2">&nbsp</th>
					<c:if test="${assessment.type.value == AssessmentType.GRADED}">
						<th class="tac" width="">Correct</th>
					</c:if>
					<th class="tac" width=""></th>
				</tr>
			</thead>
			<tbody></tbody>
			<tfoot>
				<tr valign="middle">
					<td colspan="5"><a href="javascript:void(0);" class="cta-add-choice">Add another answer</a></td>
				</tr>
			</tfoot>
		</table>
	</script>

	<script id="tmpl-answer-row" type="text/x-jquery-tmpl">
		<tr valign="middle">
			<td width="30" class="index"></td>
			<td>
				<input type="hidden" name="choices[\${index}].id" class="choice-id-outlet" />
				<input type="hidden" name="choices[\${index}].position" class="choice-position-outlet" />
				<input type="text" name="choices[\${index}].value" class="choice-value-outlet" />
			</td>
			<c:if test="${assessment.type.value == AssessmentType.GRADED}">
				<td class="tac">
					<input type="hidden" name="choices[\${index}].correct" value="0" class="choice-correct-outlet" />
					<input type="radio" name="correctChoice" value="\${index}" class="single-choice-correct-outlet" />
				</td>
			</c:if>

			<td class="tac">
				<a href="javascript:void(0);" class="cta-remove-choice tooltipped tooltipped-n" aria-label="Delete Answer">
					<i class="wm-icon-trash"></i>
				</a>
				<a href="javascript:void(0);" class="sort_handle tooltipped tooltipped-n" aria-label="Change answer order">
					<i class="wm-icon-sort"></i>
				</a>
			</td>
		</tr>
	</script>

	<script id="tmpl-answer-row-multiple" type="text/x-jquery-tmpl">
		<tr valign="middle">
			<td width="30" class="index"></td>
			<td>
				<input type="hidden" name="choices[\${index}].id" class="choice-id-outlet" />
				<input type="hidden" name="choices[\${index}].position" value="\${index}" class="choice-position-outlet" />
				<input type="text" name="choices[\${index}].value" class="choice-value-outlet" />
			</td>
			<c:if test="${assessment.type.value == AssessmentType.GRADED}">
				<td class="tac"><input type="checkbox" name="choices[\${index}].correct" class="choice-correct-outlet" /></td>
			</c:if>
			<td class="tac">
				<a href="javascript:void(0);" class="cta-remove-choice tooltipped tooltipped-n" aria-label="Delete Answer">
					<i class="wm-icon-trash"></i>
				</a>
				<a href="javascript:void(0);" class="sort_handle tooltipped tooltipped-n" aria-label="Change answer order">
					<i class="wm-icon-sort"></i>
				</a>
			</td>
		</tr>
	</script>

	<script id="tmpl-options-singleline" type="text/x-jquery-tmpl">
		<div class="clearfix">
			<label>Options</label>
			<div class="input">
				<ul class="inputs-list">
					<li>{{tmpl "#tmpl-option-maxchars"}}</li>
					<li>{{tmpl "#tmpl-option-providehint"}}</li>
					<li>{{tmpl "#tmpl-option-provideexplaination"}}</li>
					<c:if test="${assessment.type.value == AssessmentType.GRADED}">
						<li>{{tmpl "#tmpl-option-dontscore"}}</li>
					</c:if>
				</ul>
			</div>
		</div>
	</script>

	<script id="tmpl-options-multiline" type="text/x-jquery-tmpl">
		<div class="clearfix">
			<label>Options</label>
			<div class="input">
				<ul class="inputs-list">
					<li>{{tmpl "#tmpl-option-maxchars"}}</li>
					<li>{{tmpl "#tmpl-option-providehint"}}</li>
					<li>{{tmpl "#tmpl-option-provideexplaination"}}</li>
					<c:if test="${assessment.type.value == AssessmentType.GRADED}">
						<li>{{tmpl "#tmpl-option-dontscore"}}</li>
					</c:if>
				</ul>
			</div>
		</div>
	</script>

	<script id="tmpl-options-radio" type="text/x-jquery-tmpl">
		<div class="clearfix">
			<label>Options</label>
			<div class="input">
				<ul class="inputs-list">
					<li>{{tmpl "#tmpl-option-allowother"}}</li>
					<li>{{tmpl "#tmpl-option-provideexplaination"}}</li>
					<c:if test="${assessment.type.value == AssessmentType.GRADED}">
						<li>{{tmpl "#tmpl-option-dontscore"}}</li>
						<li>{{tmpl "#tmpl-option-showanswerfeedback"}}</li>
					</c:if>
				</ul>
			</div>
		</div>
	</script>

	<script id="tmpl-options-checkboxes" type="text/x-jquery-tmpl">
		<div class="clearfix">
			<label>Options</label>
			<div class="input">
				<ul class="inputs-list">
					<li>{{tmpl "#tmpl-option-allowother"}}</li>
					<li>{{tmpl "#tmpl-option-provideexplaination"}}</li>
					<c:if test="${assessment.type.value == AssessmentType.GRADED}">
						<li>{{tmpl "#tmpl-option-dontscore"}}</li>
						<li>{{tmpl "#tmpl-option-showanswerfeedback"}}</li>
					</c:if>
				</ul>
			</div>
		</div>
	</script>

	<script id="tmpl-options-dropdown" type="text/x-jquery-tmpl">
		<div class="clearfix">
			<label>Options</label>
			<div class="input">
				<ul class="inputs-list">
					<li>{{tmpl "#tmpl-option-allowother"}}</li>
					<li>{{tmpl "#tmpl-option-provideexplaination"}}</li>
					<c:if test="${assessment.type.value == AssessmentType.GRADED}">
						<li>{{tmpl "#tmpl-option-dontscore"}}</li>
						<li>{{tmpl "#tmpl-option-showanswerfeedback"}}</li>
					</c:if>
				</ul>
			</div>
		</div>
	</script>

	<script id="tmpl-options-date" type="text/x-jquery-tmpl">
		<div class="clearfix">
			<label>Options</label>
			<div class="input">
				<ul class="inputs-list">
					<li>{{tmpl "#tmpl-option-providehint"}}</li>
					<li>{{tmpl "#tmpl-option-provideexplaination"}}</li>
				</ul>
			</div>
		</div>
	</script>

	<script id="tmpl-options-phonenumber" type="text/x-jquery-tmpl">
		<div class="clearfix">
			<label>Options</label>
			<div class="input">
				<ul class="inputs-list">
					<li>{{tmpl "#tmpl-option-providehint"}}</li>
					<li>{{tmpl "#tmpl-option-provideexplaination"}}</li>
				</ul>
			</div>
		</div>
	</script>

	<script id="tmpl-options-email" type="text/x-jquery-tmpl">
		<div class="clearfix">
			<label>Options</label>
			<div class="input">
				<ul class="inputs-list">
					<li>{{tmpl "#tmpl-option-providehint"}}</li>
					<li>{{tmpl "#tmpl-option-provideexplaination"}}</li>
				</ul>
			</div>
		</div>
	</script>

	<script id="tmpl-options-numeric" type="text/x-jquery-tmpl">
		<div class="clearfix">
			<label>Options</label>
			<div class="input">
				<ul class="inputs-list">
					<li>{{tmpl "#tmpl-option-providehint"}}</li>
					<li>{{tmpl "#tmpl-option-provideexplaination"}}</li>
					<c:if test="${assessment.type.value == AssessmentType.GRADED}">
						<li>{{tmpl "#tmpl-option-dontscore"}}</li>
					</c:if>
				</ul>
			</div>
		</div>
	</script>

	<script id="tmpl-options-asset" type="text/x-jquery-tmpl">
		<div class="clearfix">
			<label>Options</label>
			<div class="input">
				<ul class="inputs-list">
					<li>{{tmpl "#tmpl-option-provideexplaination"}}</li>
					<c:if test="${assessment.type.value == AssessmentType.GRADED}">
						<li>{{tmpl "#tmpl-option-dontscore"}}</li>
					</c:if>
				</ul>
			</div>
		</div>
	</script>

	<script id="tmpl-option-maxchars" type="text/x-jquery-tmpl">
		<div>
			<label>
				<input type="checkbox" name="hasMaxLength" value="1" />
				Set character limit
				<span class="tooltipped tooltipped-n" aria-label="Set a number of characters allowed for answers to this question">
					<i class="wm-icon-question-filled"></i>
				</span>
			</label>
			<div class="additional_fields dn">
				<input type="text" name="maxLength" class="small" maxlength="3" />
			</div>
		</div>
	</script>

	<script id="tmpl-option-providehint" type="text/x-jquery-tmpl">
		<div>
			<label>
				<input type="checkbox" name="hasHint" value="1" />
				Provide a hint
				<span class="tooltipped tooltipped-n" aria-label="Give the user a few words show with the question to help them">
					<i class="wm-icon-question-filled"></i>
				</span>
			</label>
			<div class="additional_fields dn">
				<input type="text" name="hint" maxlength="255" />
			</div>
		</div>
	</script>

	<script id="tmpl-option-provideexplaination" type="text/x-jquery-tmpl">
		<div>
			<label>
				<input type="checkbox" name="hasDescription" value="1" />
				Provide an additional explanation
				<span class="tooltipped tooltipped-n" aria-label="Add additional text to give more context to your question. Displays below the question.">
					<i class="wm-icon-question-filled"></i>
				</span>
			</label>
			<div class="additional_fields dn">
				<textarea name="description" class="span9"></textarea>
			</div>
		</div>
	</script>

	<script id="tmpl-option-dontscore" type="text/x-jquery-tmpl">
		<div>
			<label>
				<input type="checkbox" name="notGraded" value="1" />
				Do not score this question
				<span class="tooltipped tooltipped-n" aria-label="Collect an answer but do not count the question towards a pass/fail grade">
					<i class="wm-icon-question-filled"></i>
				</span>
			</label>
		</div>
	</script>

	<script id="tmpl-option-showanswerfeedback" type="text/x-jquery-tmpl">
		<div>
			<label>
				<input type="checkbox" name="hasIncorrectFeedback" value="1" />
				Show answer feedback
				<span class="tooltipped tooltipped-n" aria-label="Answer feedback will be shown to the training taker once they finish. This is a good place to explain the correct answer.">
					<i class="wm-icon-question-filled"></i>
				</span>
			</label>
			<div class="additional_fields dn">
				<textarea name="incorrectFeedback" id="show_answer_feedback_val" maxlength="250"></textarea><br/>
				<span class="help-inline">max 250 characters</span>
			</div>
		</div>
	</script>

	<script id="tmpl-option-allowother" type="text/x-jquery-tmpl">
		<div>
			<label>
				<input type="checkbox" name="otherAllowed" value="1" />
				Allow an "other" option
				<span class="tooltipped tooltipped-n" aria-label="Let the user enter their own value as an answer. <c:if test="${assessment.type.value == AssessmentType.GRADED}">If selected, the question must be graded manually.</c:if>">
					<i class="wm-icon-question-filled"></i>
				</span>
			</label>
		</div>
	</script>

	<script id="tmpl-asset-item" type="text/x-jquery-tmpl">
		<li>
			<input type="hidden" name="assets" value="\${id}" />
			<a href="/asset/\${uuid}" target="_blank">\${name}</a> -
			<a href="javascript:void(0);" class="cta-remove-attachment">Remove</a>
		</li>
	</script>

	<script id="tmpl-upload-item" type="text/x-jquery-tmpl">
		<li>
			<input type="hidden" name="uploads[\${index}].uuid" value="\${uuid}" />
			<input type="hidden" name="uploads[\${index}].name" value="\${name}" />
			<input type="hidden" name="uploads[\${index}].description" value="Question Materials" />
			<a href="/upload/download/\${uuid}" target="_blank">\${name}</a> -
			<a href="javascript:void(0);" class="cta-remove-attachment">Remove</a>
		</li>
	</script>

	<script id="tmpl-cta-attachment" type="text/x-jquery-tmpl">
		<div class="tabbable">
			<ul id="attachment-tabs" class="nav nav-tabs">
				<li class="active"><a href="#tab-upload-file" id="upload-file-tab" data-toggle="tab">Upload a File</a></li>
				<li><a href="#tab-embed" id="embed-tab" data-toggle="tab">Embed a Video</a></li>
			</ul>

			<div class="tab-content">
				<div id="tab-upload-file" class="tab-pane active">
					<div class="qq-uploader">
						<div class="qq-upload-drop-area"><span>Drop attachment here to upload</span></div>
						<ul class="qq-upload-list br" id="attachment-list"></ul>
						<p><a href="javascript:void(0);" class="qq-upload-button ml">Upload a file for use with this question</a></p>
						<span class="help-block">Upload videos, documents, training manuals, or other materials related to this question. Attachments will be presented to viewers as part of answering the question. File size limit is 150MB. Supported media formats: .mp4, .m4v, .f4v, .mov, .flv, .m4a, .f4a, .mp3</span>
					</div>
				</div>

				<div id="tab-embed" class="tab-pane">
					<p>You can upload your video to YouTube and paste the embed url here. <small class='meta'>http://www.youtube.com/watch?v=rZBVRw9frhE</small></p>
					<input type="text" name="embedLink" size="80"/>
				</div>
			</div>
		</div>
	</script>

	<script id="tmpl-question-preview" type="text/x-jquery-tmpl">
		<div class="question-container">
			<input type="hidden" name="question_ids" value="\${id}" />

			<div class="pa" style="right: 10px;">
				<a href="javascript:void(0);" class="cta-edit-question icon-edit tooltipped tooltipped-n" aria-label="Edit Question">Edit</a>
				<a href="javascript:void(0);" class="cta-remove-question icon-delete tooltipped tooltipped-n" aria-label="Delete Question">Remove</a>
				<a href="javascript:void(0);" class="icon-sort tooltipped tooltipped-n" aria-label="Change question order">Sort Choice</a>
			</div>
			<div class="question-number"></div>
			<div class="page-header"><p class="question_prompt question_preview" >\${prompt}</p></div>
			{{if description}}
				<p class="question_description">\${description}</p>
			{{/if}}

			{{if type == '${AssessmentItemType.SINGLE_LINE_TEXT}'}}
				<input type="text" name="answer" placeholder="\${hint}" />
			{{else type == '${AssessmentItemType.MULTIPLE_LINE_TEXT}'}}
				<textarea name="answer" placeholder="\${hint}"></textarea>
			{{else type == '${AssessmentItemType.SINGLE_CHOICE_RADIO}'}}
				{{each choices}}
					<input type="radio" name="answer_\${position + 1}" /> \${$value.value}<br />
				{{/each}}
				{{if otherAllowed}}
					<label><input type="radio" name="answer" value="other" /> Other:</label>
					<input type="text" name="other" value="" />
				{{/if}}
			{{else type == '${AssessmentItemType.MULTIPLE_CHOICE}'}}
				{{each choices}}
					<input type="checkbox" name="answer" /> \${$value.value}<br />
				{{/each}}
				{{if otherAllowed}}
					<label><input type="checkbox" name="answer[other]" value="other" /> Other:</label>
					<input type="text" name="other" value="" />
				{{/if}}
			{{else type == '${AssessmentItemType.SINGLE_CHOICE_LIST}'}}
				<select name="answer">
					{{each choices}}
						<option>\${$value.value}</option>
					{{/each}}
					{{if otherAllowed}}
						<option value="other">Other:</option>
					{{/if}}
				</select>
				{{if otherAllowed}}
					<input type="text" name="other" id="other_option" class="dn" value="" />
				{{/if}}
			{{else type == '${AssessmentItemType.DIVIDER}'}}
			{{else type == '${AssessmentItemType.DATE}'}}
				<input type="text" name="answer" placeholder="\${hint}" />
			{{else type == '${AssessmentItemType.PHONE}'}}
				<input type="text" name="answer" placeholder="\${hint}" />
			{{else type == '${AssessmentItemType.EMAIL}'}}
				<input type="text" name="answer" placeholder="\${hint}" />
			{{else type == '${AssessmentItemType.NUMERIC}'}}
				<input type="text" name="answer" placeholder="\${hint}" />
			{{else type == '${AssessmentItemType.ASSET}'}}
				(Worker will be asked to upload photo as answer)
			{{/if}}

			{{if incorrectFeedback}}
				<h6>Answer Feedback:</h6>
				<p>\${incorrectFeedback}</p>
			{{/if}}

			{{if links}}
				<h6>Embedded Links:</h6>
				<ul class="unstyled">
					{{each(i, link) links}}
						<li><a class="wordwrap" href="\${link.remote_uri}" target="_blank">\${link.remote_uri}</a></li>
					{{/each}}
				</ul>
			{{/if}}

			{{if assets}}
				<h6>Attachments:</h6>
				<ul class="unstyled">
					{{each(i, asset) assets}}
						<li><a class="wordwrap" href="/asset/\${asset.uuid}" target="_blank">\${asset.name}</a></li>
					{{/each}}
				</ul>
			{{/if}}
		</div>
	</script>
	</div>

</wm:app>
