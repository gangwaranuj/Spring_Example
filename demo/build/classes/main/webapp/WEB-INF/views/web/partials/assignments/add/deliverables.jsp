<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="wm" tagdir="/WEB-INF/tags" %>

<a name="closeoutanchor"></a>
<div id="assignment-internal" class="inner-container dn">
	<div class="page-header">
		<h4>Deliverables</h4>
	</div>

	<div class="control-group">
		<label for="instructions" class="control-label">Instructions</label>
		<div class="controls">
			<input id="deliverableRequirementId" type="hidden" name="resourceCompletionForm.id" value=""/>
			<form:textarea data-richtext="wysiwyg" path="resourceCompletionForm.instructions" id="instructions" cssClass="input-block-level" rows="10" cols="49"/>
			<div class="help-block">Attachments required or other closeout instructions</div>
		</div>
	</div>
	<div class="control-group">
		<label class="control-label">Deliverable Requirements</label>
		<div class="controls">
			<select id="deliverableRequirementTypes"></select>
			<div class="input-append">
				<input id="numberOfFiles" type="text" class="span1 only-numbers text-center" maxlength="2" placeholder="1"/>
				<span class="add-on"># of Files</span>
			</div>
			<a href="javascript:void(0);" id="addDeliverableRequirement" class="add-deliverable-requirement-button" disabled>
				Add
			</a>
			<ul id="deliverableRequirementsList"></ul>
			<div id="deliverableRequirementHelpText" class="help-block">Drag and drop requirements to set the order you prefer</div>
			<div class="help-block required">Short descriptions for each deliverable type are required.</div>
		</div>
	</div>
	<div class="control-group deliverables-deadline-section" style="display: none;">
		<label class="control-label">Deliverable Due Date</label>
		<div class="controls">
			<div class="input-append">
				<form:select path="resourceCompletionForm.hoursToComplete" id='${"hoursToComplete"}'>
					<form:options items="${deliverableFulfillmentDurationsMap}"/>
				</form:select>
			</div>
			<div class="help-block">Countdown begins at assignment end time if present, otherwise at start time.</div>
		</div>
	</div>
</div>
