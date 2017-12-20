<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="wmfmt" uri="http://www.workmarket.com/taglib/fmt" %>

<style lang="text/css">
	input.input-text-other {
		margin-bottom: 0;
	}
	label.radio input, label.checkbox input {
		vertical-align: middle;
	}
</style>
<form action="#" id="assessment-container">
	<div id="assessment-question" class="clear"></div>

	<div class="wm-action-container">
		<c:choose>
			<c:when test="${requestScope.survey}">
				<a href="javascript:void(0);" id="cta-submit" class="button" data-loading-text="Submitting...">Submit Answers</a>
			</c:when>
			<c:otherwise>
				<a href="javascript:void(0);" id="cta-prev-question" class="button" data-direction="back">&laquo; Previous</a>
				<a href="javascript:void(0);" id="cta-next-question" class="button" data-direction="forward">Next Question &raquo;</a>
				<a href="javascript:void(0);" id="cta-submit" class="button<c:if test="${not requestScope.allQuestionsAnswered}"> hidden</c:if>">Submit Answers</a>
			</c:otherwise>
		</c:choose>
		<c:if test="${requestScope.graded}">
			<p class="pull-left" id="assessment-progress">Question 1 of 1</p>
		</c:if>
	</div>
</form>

<script id="tmpl-question-item" type="text/x-jquery-tmpl">
	<div>
		<div class="question-container" data-itemId="\${id}" data-itemIndex="\${itemIndex}">

			<jsp:include page="/WEB-INF/views/web/partials/general/notices_js.jsp"/>

			<input type="hidden" name="responses[\${itemIndex}].itemId" value="\${id}"/>

			<div class="question-number">\${position + 1}</div>
			<div class="page-header"><p class="question_prompt"><strong>\${prompt}</strong></p></div>
			{{if description}}
				<p class="question_description">\${description}</p>
			{{/if}}

			{{if type == '${AssessmentItemType.SINGLE_LINE_TEXT}'}}
				<div class="controls">
				<input type="text" name="responses[\${itemIndex}].value" value="" placeholder="\${hint}" class="span8"/>
				</div>
			{{else type == '${AssessmentItemType.MULTIPLE_LINE_TEXT}'}}
				<div class="controls">
				<textarea name="responses[\${itemIndex}].value" placeholder="\${hint}" class="span8"></textarea>
					</div>
			{{else type == '${AssessmentItemType.SINGLE_CHOICE_RADIO}'}}

				{{each(i, choice) choices}}

				<label><input type="radio" name="responses[\${\$data.itemIndex}].choices" value="\${choice.id}"/>
					\${choice.value}</label>
				{{/each}}
				{{if otherAllowed}}
					<label class="radio inline">
						<input type="radio" name="responses[\${itemIndex}].choices" value="other"/> Other:
					</label>
					<input type="text" class="span7 input-text-other" name="responses[\${itemIndex}].value" value=""/>
					{{/if}}
			{{else type == '${AssessmentItemType.MULTIPLE_CHOICE}'}}
				<p>(Select all that apply)</p>
				<div class="controls">
				{{each(i, choice) choices}}
					<label><input type="checkbox" name="responses[\${\$data.itemIndex}].choices[\${i}]" value="\${choice.id}"/>
					\${choice.value}</label>
				{{/each}}

				{{if otherAllowed}}
				<label class="inline"><input type="checkbox" name="responses[\${itemIndex}].choices[\${choices.length}]" value="other"/> Other:</label>
				<input type="text" class="span7 input-text-other" name="responses[\${itemIndex}].value" value=""/>
				{{/if}}
				</div>
			{{else type == '${AssessmentItemType.SINGLE_CHOICE_LIST}'}}
				<div class="controls">

				<select name="responses[\${itemIndex}].choices">
				<option value=""></option>
				{{each(i, choice) choices}}
					<option value="\${choice.id}">\${choice.value}</option>
				{{/each}}
				{{if otherAllowed}}
					<option value="other">Other:</option>
				{{/if}}
				</select>
				{{if otherAllowed}}
					<input type="text" name="responses[\${itemIndex}].value" class="other_option dni" value=""/>
				{{/if}}
				</div>
			{{else type == '${AssessmentItemType.DIVIDER}'}}
			{{else type == '${AssessmentItemType.DATE}'}}
				<div class="controls">
				<input type="text" name="responses[\${itemIndex}].value" value="" placeholder="\${hint}" class="span8"/>
				</div>
			{{else type == '${AssessmentItemType.PHONE}'}}
				<input type="text" name="responses[\${itemIndex}].value" value="" placeholder="\${hint}" class="span8"/>
			{{else type == '${AssessmentItemType.EMAIL}'}}
				<input type="text" name="responses[\${itemIndex}].value" value="" placeholder="\${hint}" class="span8"/>
			{{else type == '${AssessmentItemType.NUMERIC}'}}
				<input type="text" name="responses[\${itemIndex}].value" value="" placeholder="\${hint}" class="span8"/>
			{{else type == '${AssessmentItemType.ASSET}'}}
				<div data-behavior="uploader"></div>
				<ul class="unstyled" data-behavior="uploader-list"></ul>
				<p>Note: Max file size per attachment is 150MB</p>
			{{/if}}
			{{if assets}}
				<h6>Attachments:</h6>
				<ul class="unstyled">
				{{each(i, asset) assets}}
					<li><a href="/asset/\${asset.uuid}" target="_blank" class="wordwrap">\${asset.name}</a></li>
				{{/each}}
				</ul>
			{{/if}}
			<div>
				<div id="videoplayer_\${id}"></div>
			</div>
		</div>
	</div>
</script>

<script id="tmpl-assessment-confirm" type="text/x-jquery-tmpl">
	<div class="span8"><p class="alert alert-success">Congratulations, you're done! If you need to change any answers, go back now, otherwise click "Submit Answers"</p></div>
</script>

<script id="tmpl-asset-item" type="text/x-jquery-tmpl">
	<li>
		<input type="hidden" name="responses[\${itemIndex}].assets[\${attachmentIndex}].id" value="\${id}"/>
		<a href="/asset/\${uuid}" target="_blank">\${name}</a> -
		<a href="javascript:void(0);" class="cta-remove-attachment">Remove</a><br/>
		<input type="text" name="responses[\${itemIndex}].assets[\${attachmentIndex}].description" value="\${description}" placeholder="Caption" class="span8"/>
	</li>
</script>

<script id="tmpl-upload-item" type="text/x-jquery-tmpl">
	<li>
		<input type="hidden" name="responses[\${itemIndex}].uploads[\${attachmentIndex}].uuid" value="\${uuid}"/>
		<input type="hidden" name="responses[\${itemIndex}].uploads[\${attachmentIndex}].name" value="\${name}"/>
		<a href="/upload/download/\${uuid}" target="_blank">\${name}</a> -
		<a href="javascript:void(0);" class="cta-remove-attachment">Remove</a><br/>
		<input type="text" name="responses[\${itemIndex}].uploads[\${attachmentIndex}].description" value="\${description}" placeholder="Caption" class="span8"/>
	</li>
</script>

<script id="tmpl-cta-attachment" type="text/x-jquery-tmpl">
	<div class="qq-uploader">
		<div class="qq-upload-drop-area"><span>Drop attachment here to upload</span></div>
		<p><a href="javascript:void(0);" class="qq-upload-button" id="cta-add-attachment">Upload photo response</a></p>
		<ul class="unstyled qq-upload-list"></ul>
	</div>
</script>
