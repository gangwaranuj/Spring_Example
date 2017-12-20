<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="wmfmt" uri="http://www.workmarket.com/taglib/fmt" %>
<%@ taglib prefix="wmfn" uri="http://www.workmarket.com/taglib/functions" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>

<c:set scope="request" var='rating' value="${workResponse.resourceRatingForWork}"/>
<c:set var="resourceName" value="${wmfn:fullName(work.activeResource.user.name.firstName, work.activeResource.user.name.lastName)}" />
<c:set var="resourceRating" value="${workResponse.resourceRatingForWork.value}"/>

<%-- This form is for assignment detail page --%>
<h4>Please rate <c:out value="${resourceName}" /></h4>

<c:if test="${not empty work.location}">
	<c:set var="arrivedOnTimeDefault" value="${true}" />
	<c:forEach var="resourceLabel" items="${work.activeResource.labels}">
		<c:if test="${resourceLabel.code eq 'late'}">
			<c:set var="arrivedOnTimeDefault" value="${resourceLabel.confirmed or resourceLabel.ignored}" />
		</c:if>
	</c:forEach>

	<div class="control-group">
		<label class="normal">Did <c:out value="${resourceName}" /> <strong>arrive on time</strong>?</label>
		<div class="controls">
			<label class="dib normal radio inline">
				<input type="radio" name="arrivedOnTime" value="true" <c:if test="${arrivedOnTimeDefault}">checked="checked"</c:if> /> Yes
			</label>
			<label class="dib normal radio inline">
				<input type="radio" name="arrivedOnTime" value="false" <c:if test="${not arrivedOnTimeDefault}">checked="checked"</c:if> /> No
			</label>
		</div>
	</div>
</c:if>

<c:set var="deliverablesLate" value="${false}" />
<c:forEach var="resourceLabel" items="${work.activeResource.labels}">
	<c:if test="${resourceLabel.code eq 'late_deliverable'}">
		<c:set var="deliverablesLate" value="${resourceLabel.confirmed or (not resourceLabel.ignored)}" />
	</c:if>
</c:forEach>

<div class="control-group">
	<label class="normal">Did <c:out value="${resourceName}" /> <strong>submit all the required deliverables</strong> on time?</label>
	<div class="controls">
		<label class="dib normal radio inline">
			<input type="radio" name="completedOnTime" value="true" <c:if test="${not deliverablesLate}">checked="checked"</c:if> /> Yes
		</label>
		<label class="dib normal radio inline">
			<input type="radio" name="completedOnTime" value="false" <c:if test="${deliverablesLate}">checked="checked"</c:if> /> No
		</label>
	</div>
</div>

<div class="control-group">
	<label class="normal">Please rate your experience with <c:out value="${resourceName}" />.</label>
	<div class="controls container-fluid">
		<div class="row-fluid">
			<div class="span2">
				<strong>Select All</strong>
			</div>
			<div class="span6 three-level">
				<c:choose>
					<c:when test="${not empty rating and rating.value > 0}">
						<div class="span5"><input class="rating-value select-all-radio" rel="unsatisfied" type="radio" name="rating.all"  value="1" <c:if test="${rating.value eq 1}">checked="checked"</c:if> /></div>
						<div class="span5"><input class="rating-value select-all-radio" rel="satisfied" type="radio" name="rating.all"  value="2" <c:if test="${rating.value eq 2}">checked="checked"</c:if> /></div>
						<input class="rating-value select-all-radio" type="radio" rel="excellent" name="rating.all"  value="3"  <c:if test="${rating.value eq 3}">checked="checked"</c:if> />
					</c:when>
					<c:otherwise>
						<div class="span5"><input class="rating-value select-all-radio" rel="unsatisfied" type="radio" name="rating.all"  value="1" /></div>
						<div class="span5"><input class="rating-value select-all-radio" rel="satisfied" type="radio" name="rating.all"  value="2" checked="checked" /></div>
						<input class="rating-value select-all-radio" type="radio" rel="excellent" name="rating.all"  value="3"  />
					</c:otherwise>
				</c:choose>
			</div>
		</div>
		<hr class="ratings_hr">
		<div class="row-fluid">
			<div class="span2">
				<strong>Overall</strong>
			</div>
			<div class="span6 three-level">
				<c:choose>
					<c:when test="${not empty rating and rating.value > 0}">
						<div class="span5"><input class="rating-value" rel="unsatisfied" type="radio" name="rating.value"  value="1" <c:if test="${rating.value eq 1}">checked="checked"</c:if> /> Unsatisfied  </div>
						<div class="span5"><input class="rating-value" rel="satisfied" type="radio" name="rating.value"  value="2" <c:if test="${rating.value eq 2}">checked="checked"</c:if> /> Satisfied </div>
						<input class="rating-value" type="radio" rel="excellent" name="rating.value"  value="3"  <c:if test="${rating.value eq 3}">checked="checked"</c:if> /> Excellent
					</c:when>
					<c:otherwise>
						<div class="span5"><input class="rating-value" rel="unsatisfied" type="radio" name="rating.value"  value="1" /> Unsatisfied  </div>
						<div class="span5"><input class="rating-value" rel="satisfied" type="radio" name="rating.value"  value="2" checked="checked" /> Satisfied </div>
						<input class="rating-value" type="radio" rel="excellent" name="rating.value"  value="3"  /> Excellent
					</c:otherwise>
				</c:choose>
			</div>
		</div>
		<div class="row-fluid">
			<div class="span2">
				<strong>Quality</strong>
			</div>
			<div class="span6">
				<c:choose>
					<c:when test="${not empty rating and rating.quality > 0}">
						<div class="span5"><input type="radio" rel="unsatisfied" name="rating.quality"  value="1" <c:if test="${rating.quality eq 1}">checked="checked"</c:if> /> Unsatisfied </div>
						<div class="span5"><input type="radio" rel="satisfied" name="rating.quality"  value="2" <c:if test="${rating.quality eq 2}">checked="checked"</c:if> /> Satisfied </div>
						<input type="radio" name="rating.quality" rel="excellent"  value="3" <c:if test="${rating.quality eq 3}">checked="checked"</c:if> /> Excellent
					</c:when>
					<c:otherwise>
						<div class="span5"><input type="radio" rel="unsatisfied" name="rating.quality"  value="1" /> Unsatisfied </div>
						<div class="span5"><input type="radio" rel="satisfied" name="rating.quality"  value="2" checked="checked" /> Satisfied </div>
						<input type="radio" name="rating.quality" rel="excellent" value="3" /> Excellent
					</c:otherwise>
				</c:choose>
			</div>
		</div>
		<div class="row-fluid">
			<div class="span2">
				<strong>Professionalism</strong>
			</div>
			<div class="span6">
				<c:choose>
					<c:when test="${not empty rating and rating.professionalism > 0}">
						<div class="span5"><input type="radio" rel="unsatisfied" name="rating.professionalism"  value="1"  <c:if test="${rating.professionalism eq 1}">checked="checked"</c:if> /> Unsatisfied </div>
						<div class="span5"><input type="radio" rel="satisfied" name="rating.professionalism"  value="2"  <c:if test="${rating.professionalism eq 2}">checked="checked"</c:if> /> Satisfied </div>
						<input type="radio" name="rating.professionalism" rel="excellent" value="3"  <c:if test="${rating.professionalism eq 3}">checked="checked"</c:if> /> Excellent
					</c:when>
					<c:otherwise>
						<div class="span5"><input type="radio" rel="unsatisfied" name="rating.professionalism"  value="1" /> Unsatisfied </div>
						<div class="span5"><input type="radio" rel="satisfied" name="rating.professionalism"  value="2" checked="checked" /> Satisfied </div>
						<input type="radio" name="rating.professionalism" rel="excellent"  value="3" /> Excellent
					</c:otherwise>
				</c:choose>
			</div>
		</div>
		<div class="row-fluid">
			<div class="span2">
				<strong>Communication</strong>
			</div>
			<div class="span6">
				<c:choose>
					<c:when test="${not empty rating and rating.communication > 0}">
						<div class="span5"><input type="radio" rel="unsatisfied" name="rating.communication"  value="1" <c:if test="${rating.communication eq 1}">checked="checked"</c:if> /> Unsatisfied </div>
						<div class="span5"><input type="radio" rel="satisfied" name="rating.communication"  value="2" <c:if test="${rating.communication eq 2}">checked="checked"</c:if> /> Satisfied </div>
						<input type="radio" name="rating.communication" rel="excellent" value="3" <c:if test="${rating.communication eq 3}">checked="checked"</c:if> /> Excellent
					</c:when>
					<c:otherwise>
						<div class="span5"><input type="radio" rel="unsatisfied" name="rating.communication"  value="1" /> Unsatisfied </div>
						<div class="span5"><input type="radio" rel="satisfied" name="rating.communication"  value="2" checked="checked" /> Satisfied </div>
						<input type="radio" rel="excellent" name="rating.communication"  value="3" /> Excellent
					</c:otherwise>
				</c:choose>
			</div>
		</div>

	</div>
</div>

<div class="control-group dn block-if-low-rating-outlet">
	<div class="controls">
		<label class="checkbox inline">
			<input type="checkbox" name="blockResource" class="block-resource-input" value="true" />
			Block <c:out value="${resourceName}" /> from future work with your company
		</label>
	</div>
</div>

<div class="control-group">
	<label class="normal control-label" for="rating.review">Additional feedback for <c:out value="${resourceName}" /></label>
	<div class="controls">
		<textarea name='rating.review' rows="2" class="input-block-level"><c:out value="${workResponse.resourceRatingForWork.review}"/></textarea>
	</div>
</div>

<c:if test="${not (work.activeResource.user.laneType.value eq laneTypes['LANE_1'] or work.activeResource.user.laneType.value eq laneTypes['LANE_2'])}">
	<div class="control-group">
		<div class="controls">
			<label>
				<input type="hidden" name="_shareRating" value="off"/>
				<input type="checkbox" name="shareRating" value="true" checked="checked"  />
				Share this review
			</label>
		</div>
	</div>
</c:if>

<c:if test="${is_admin and not empty rating and rating.value > 0}">
	<small class="fr last-rating-buyer">Last rated by ${workResponse.lastRatingBuyerFullName} on <c:out value="${wmfmt:formatCalendar('MM-dd-yyyy', rating.modifiedOn)}"/></small>
</c:if>
<div class="clearfix"></div>
