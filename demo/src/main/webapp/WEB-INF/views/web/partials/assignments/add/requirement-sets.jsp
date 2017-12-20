<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>

<div class="inner-container" id="work-form-requirements">
	<div class="page-header">
		<h4>Worker Requirements</h4>
	</div>
	<p>
		Use Requirement Sets to screen workers who apply to your assignments!<br/>
		<small class="meta">Only workers who meet all the requirements
		that you specified will be eligible for this assignment. First, create your Requirement Sets in <a href="/settings/manage/requirement_sets">settings</a>
		and then apply them on your assignment. </small>
	</p>

	<div class="row">
		<div class="span5">
			<div class="well-b2">
				<h5>
					Available Requirement Sets
					<span class="tooltipped tooltipped-n" aria-label="Requirement Sets provide logical groups of requirements. Create Requirement Sets in your company settings.">
						<i class="wm-icon-question-filled"></i>
					</span>
				</h5>
				<div class="well-content">
					<select id="requirement-sets" name="requirementSetId" class="input-block-level" data-select="requirement-set">
						<option value="" class="prompt">- Select -</option>
						<c:forEach items="${requirementSets}" var="requirementSet">
							<option value="${requirementSet.id}" data-required="${requirementSet.required}"><c:out value="${requirementSet.name}" /></option>
						</c:forEach>
					</select>
				</div>

				<div class="well-content dn" data-placeholder="provided-requirements">
				</div>

				<div class="well-content">
					<button class="button" data-action="add-requirement-set" disabled="disabled">Add</button>
				</div>
			</div>
		</div>

		<div class="span5">
			<div class="well-b2">
				<h5>
					Selected Requirements
					<span class="tooltipped tooltipped-n" aria-label="These are the requirements provided by your selected requirement sets.">
						<i class="wm-icon-question-filled"></i>
					</span>
				</h5>
				<div class="well-content">
					<div data-placeholder="current-requirements">
					</div>
				</div>
			</div>
		</div>
	</div>

	<div class="row">
		<div class="span10">
			<fieldset>
				<legend>Selected Requirement Sets</legend>
				<div data-placeholder="current-requirement-sets">
				</div>
			</fieldset>
		</div>
	</div>
</div>


