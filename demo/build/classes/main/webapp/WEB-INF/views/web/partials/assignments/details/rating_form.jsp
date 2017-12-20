<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="wmfmt" uri="http://www.workmarket.com/taglib/fmt" %>
<%@ taglib prefix="wmfn" uri="http://www.workmarket.com/taglib/functions" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>


<c:set var="resourceName" value="${wmfn:fullName(work.activeResource.user.name.firstName, work.activeResource.user.name.lastName)}" />
<c:set scope="request" var='rating' value="${workResponse.resourceRatingForWork}"/>


<%-- This form is for assignment detail page profile tab --%>
<h5>Please rate <c:out value="${resourceName}" /></h5>
<form:form id="feedback_form" cssClass="completion-well">

<div class="control-group">
	<input type="hidden" name="workId" value="${work.id}"/>
	<input type="hidden" name="ratedUserId" value="${work.activeResource.user.id}"/>
	<div>
		<div class="row-fluid">
			<div class="span4">
				<strong>Overall</strong>
			</div>
			<div class="span10 three-level">
				<c:choose>
					<c:when test="${not empty rating and rating.value > 0}">
						<div class="span6"><input class="rating-value" type="radio" name="rating.value"  value="1" <c:if test="${rating.value eq 1}">checked="checked"</c:if> /> Unsatisfied  </div>
						<div class="span6"><input class="rating-value" type="radio" name="rating.value"  value="2" <c:if test="${rating.value eq 2}">checked="checked"</c:if> /> Satisfied </div>
						<input class="rating-value" type="radio" name="rating.value"  value="3"  <c:if test="${rating.value eq 3}">checked="checked"</c:if> /> Excellent
					</c:when>
					<c:otherwise>
						<div class="span6"><input class="rating-value" type="radio" name="rating.value"  value="1" /> Unsatisfied  </div>
						<div class="span6"><input class="rating-value" type="radio" name="rating.value"  value="2" checked="checked" /> Satisfied </div>
						<input class="rating-value" type="radio" name="rating.value"  value="3"  /> Excellent
					</c:otherwise>
				</c:choose>
			</div>
		</div>
		<div class="row-fluid">
			<div class="span4">
				<strong>Quality</strong>
			</div>
			<div class="span10">
				<c:choose>
					<c:when test="${not empty rating and rating.quality > 0}">
						<div class="span6"><input type="radio" name="rating.quality"  value="1" <c:if test="${rating.quality eq 1}">checked="checked"</c:if> /> Unsatisfied </div>
						<div class="span6"><input type="radio" name="rating.quality"  value="2" <c:if test="${rating.quality eq 2}">checked="checked"</c:if> /> Satisfied </div>
						<input type="radio" name="rating.quality"  value="3" <c:if test="${rating.quality eq 3}">checked="checked"</c:if> /> Excellent
					</c:when>
					<c:otherwise>
						<div class="span6"><input type="radio" name="rating.quality"  value="1" /> Unsatisfied </div>
						<div class="span6"><input type="radio" name="rating.quality"  value="2" checked="checked" /> Satisfied </div>
						<input type="radio" name="rating.quality"  value="3" /> Excellent
					</c:otherwise>
				</c:choose>
			</div>
		</div>
		<div class="row-fluid">
			<div class="span4">
				<strong>Professionalism</strong>
			</div>
			<div class="span10">
				<c:choose>
					<c:when test="${not empty rating and rating.professionalism > 0}">
						<div class="span6"><input type="radio" name="rating.professionalism"  value="1"  <c:if test="${rating.professionalism eq 1}">checked="checked"</c:if> /> Unsatisfied </div>
						<div class="span6"><input type="radio" name="rating.professionalism"  value="2"  <c:if test="${rating.professionalism eq 2}">checked="checked"</c:if> /> Satisfied </div>
						<input type="radio" name="rating.professionalism"  value="3"  <c:if test="${rating.professionalism eq 3}">checked="checked"</c:if> /> Excellent
					</c:when>
					<c:otherwise>
						<div class="span6"><input type="radio" name="rating.professionalism"  value="1" /> Unsatisfied </div>
						<div class="span6"><input type="radio" name="rating.professionalism"  value="2" checked="checked" /> Satisfied </div>
						<input type="radio" name="rating.professionalism"  value="3" /> Excellent
					</c:otherwise>
				</c:choose>
			</div>
		</div>
		<div class="row-fluid">
			<div class="span4">
				<strong>Communication</strong>
			</div>
			<div class="span10">
				<c:choose>
					<c:when test="${not empty rating and rating.communication > 0}">
						<div class="span6"><input type="radio" name="rating.communication"  value="1" <c:if test="${rating.communication eq 1}">checked="checked"</c:if> /> Unsatisfied </div>
						<div class="span6"><input type="radio" name="rating.communication"  value="2" <c:if test="${rating.communication eq 2}">checked="checked"</c:if> /> Satisfied </div>
						<input type="radio" name="rating.communication"  value="3" <c:if test="${rating.communication eq 3}">checked="checked"</c:if> /> Excellent
					</c:when>
					<c:otherwise>
						<div class="span6"><input type="radio" name="rating.communication"  value="1" /> Unsatisfied </div>
						<div class="span6"><input type="radio" name="rating.communication"  value="2" checked="checked" /> Satisfied </div>
						<input type="radio" name="rating.communication"  value="3" /> Excellent
					</c:otherwise>
				</c:choose>
			</div>
		</div>
	</div>
</div>

<div class="control-group dn block-if-low-rating-outlet">
	<div class="controls">
		<label class="checkbox inline">
			<input type="checkbox" name="blockResource" value="true" />
			Block <c:out value="${resourceName}" /> from future work with your company
		</label>
	</div>
</div>

<div class="control-group">
	<label class="normal control-label">Additional feedback for <c:out value="${resourceName}" /></label>
	<div class="controls">
		<textarea name='rating.review' rows="2" class="input-block-level"><c:out value="${workResponse.resourceRatingForWork.review}"/></textarea>
	</div>
</div>
</form:form>

<button id="submit-feedback" type="submit" class="button">Save</button>
