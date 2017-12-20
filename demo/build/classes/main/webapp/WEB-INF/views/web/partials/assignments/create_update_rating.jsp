<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="wm-csrf" uri="http://www.workmarket.com/taglib/csrf" %>
<%@ taglib prefix="wmfmt" uri="http://www.workmarket.com/taglib/fmt" %>
<%@ taglib prefix="wmfn" uri="http://www.workmarket.com/taglib/functions" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<div class="rating-result <c:if test="${empty rating}">dn</c:if>">
	<c:if test="${not empty rating and value > 0}">
		<div class="alert alert-info">
			This rating is not final until the assignment is paid. You can edit the rating until the assignment is paid.
		</div>
		<button class="edit-rating button pull-right">Edit</button>
	</c:if>
	<div>
		<div class="span2 text-right">Overall </div> <div value="<c:out value="${value}" />" class="span2 text-left overall <c:out value="${wmfn:ratingCode(value)}"/>"> <c:out value="${wmfn:ratingLevels(value)}" /> </div> <br/>
		<c:if test="${wmfn:ratingCode(quality) != 'not-applicable'}">
			<div class="span2 text-right">Quality </div> <div value="<c:out value="${quality}" />" class="span2 text-left quality <c:out value="${wmfn:ratingCode(quality)}"/>"> <c:out value="${wmfn:ratingLevels(quality)}" /> </div> <br/>
			<div class="span2 text-right">Professionalism </div> <div value="<c:out value="${professionalism}" />" class="span2 text-left professionalism <c:out value="${wmfn:ratingCode(professionalism)}"/>"> <c:out value="${wmfn:ratingLevels(professionalism)}" /> </div><br/>
			<div class="span2 text-right">Communication </div> <div value="<c:out value="${communication}" />" class="span2 text-left communication <c:out value="${wmfn:ratingCode(communication)}"/>"> <c:out value="${wmfn:ratingLevels(communication)}" /> </div> <br/>
		</c:if>
		<c:if test="${not empty review}">
			<div class="span2 text-right">Review </div> <div class="span5 text-left"> <blockquote><em class="review"><c:out value="${review}"/></em></blockquote></div> <br/>
		</c:if>
	</div>
	<small class="fr">Last rated by ${raterUserName} on ${modified_on}</small>
</div>

<div class="rating-form <c:if test="${not empty rating}">dn</c:if>">
	<form id="feedback_form"  action="/assignments/create_update_rating/${workNumber}" class="form-stacked" method="post" accept-charset="utf-8">
		<wm-csrf:csrfToken />
		<div class="control-group">
			<input type="hidden" name="workId" value="${workId}"/>
			<input type="hidden" name="ratedUserId" value="${ratedUserId}"/>
			<div>
				<div class="row-fluid">
					<div class="span4">
						<strong>Overall</strong>
					</div>
					<div class="span10 three-level">
						<c:choose>
							<c:when test="${not empty rating and value > 0}">
								<div class="span6"><input class="rating-value" type="radio" name="rating.value"  value="1" <c:if test="${value eq 1}">checked="checked"</c:if> /> Unsatisfied  </div>
								<div class="span6"><input class="rating-value" type="radio" name="rating.value"  value="2" <c:if test="${value eq 2}">checked="checked"</c:if> /> Satisfied </div>
								<input class="rating-value" type="radio" name="rating.value"  value="3"  <c:if test="${value eq 3}">checked="checked"</c:if> /> Excellent
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
							<c:when test="${not empty rating and quality > 0}">
								<div class="span6"><input type="radio" name="rating.quality"  value="1" <c:if test="${quality eq 1}">checked="checked"</c:if> /> Unsatisfied </div>
								<div class="span6"><input type="radio" name="rating.quality"  value="2" <c:if test="${quality eq 2}">checked="checked"</c:if> /> Satisfied </div>
								<input type="radio" name="rating.quality"  value="3" <c:if test="${quality eq 3}">checked="checked"</c:if> /> Excellent
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
							<c:when test="${not empty rating and professionalism > 0}">
								<div class="span6"><input type="radio" name="rating.professionalism"  value="1"  <c:if test="${professionalism eq 1}">checked="checked"</c:if> /> Unsatisfied </div>
								<div class="span6"><input type="radio" name="rating.professionalism"  value="2"  <c:if test="${professionalism eq 2}">checked="checked"</c:if> /> Satisfied </div>
								<input type="radio" name="rating.professionalism"  value="3"  <c:if test="${professionalism eq 3}">checked="checked"</c:if> /> Excellent
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
							<c:when test="${not empty rating and communication > 0}">
								<div class="span6"><input type="radio" name="rating.communication"  value="1" <c:if test="${communication eq 1}">checked="checked"</c:if> /> Unsatisfied </div>
								<div class="span6"><input type="radio" name="rating.communication"  value="2" <c:if test="${communication eq 2}">checked="checked"</c:if> /> Satisfied </div>
								<input type="radio" name="rating.communication"  value="3" <c:if test="${communication eq 3}">checked="checked"</c:if> /> Excellent
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
					Block <c:out value="${ratedUserNumber}" /> from future work with your company
				</label>
			</div>
		</div>

		<div class="control-group">
			<label class="normal control-label">Additional feedback for <c:out value="${activeResource.fullName}" /></label>
			<div class="controls">
				<textarea name='rating.review' rows="2" class="input-block-level"><c:out value="${review}"/></textarea>
			</div>
		</div>
		<div class="wm-action-container">
			<button type="submit" class="button">Save</button>
		</div>
	</form>
</div>

<script type="text/javascript">
	$(function () {
		$('.edit-rating').on('click', function () {
			$('.rating-result').hide();
			$('.rating-form').show();
			$.colorbox.resize();
		});
	});
</script>
