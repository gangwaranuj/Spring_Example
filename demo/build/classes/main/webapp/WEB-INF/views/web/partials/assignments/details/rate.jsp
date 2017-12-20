<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="wmfmt" uri="http://www.workmarket.com/taglib/fmt" %>
<%@ taglib prefix="wmfn" uri="http://www.workmarket.com/taglib/functions" %>

<dl style="margin-bottom:20px">

	<dd class="clear">
		<c:choose>
			<c:when test="${not empty rating and rating.value > 0}">
				<div>
					<div class="span2 text-right">Overall </div> <div value="<c:out value="${rating.value}" />" class="span2 text-left overall <c:out value="${wmfn:ratingCode(rating.value)}"/>"> <c:out value="${wmfn:ratingLevels(rating.value)}" /> </div> <br/>
					<c:if test="${wmfn:ratingCode(rating.quality) != 'not-applicable'}">
						<div class="span2 text-right">Quality </div> <div value="<c:out value="${rating.quality}" />" class="span2 text-left quality <c:out value="${wmfn:ratingCode(rating.quality)}"/>"> <c:out value="${wmfn:ratingLevels(rating.quality)}" /> </div> <br/>
						<div class="span2 text-right">Professionalism </div> <div value="<c:out value="${rating.professionalism}" />" class="span2 text-left professionalism <c:out value="${wmfn:ratingCode(rating.professionalism)}"/>"> <c:out value="${wmfn:ratingLevels(rating.professionalism)}" /> </div><br/>
						<div class="span2 text-right">Communication </div> <div value="<c:out value="${rating.communication}" />" class="span2 text-left communication <c:out value="${wmfn:ratingCode(rating.communication)}"/>"> <c:out value="${wmfn:ratingLevels(rating.communication)}" /> </div> <br/>
					</c:if>
					<c:if test="${not empty rating.review}">
						<div class="span2 text-right">Review </div> <div class="span5 text-left"> <blockquote><em class="review"><c:out value="${rating.review}"/></em></blockquote></div> <br/>
					</c:if>
					<div class="type" value="<c:out value="${is_admin ? 'resource' : 'client'}"/>" />
					<div class="rating dn create_${is_admin ? "resource" : "client"}_rating"></div>
				</div>
			</c:when>
			<c:otherwise>
				<c:choose>
					<c:when test="${allowCreate}">
						<p class="allow-rate">Please rate your experiences with this <c:out value="${is_admin ? 'resource' : 'client'}" /></p>
						<div class="rating create_${is_admin ? "resource" : "client"}_rating"></div>
					</c:when>
					<c:otherwise><em>Not yet rated</em></c:otherwise>
				</c:choose>
			</c:otherwise>
		</c:choose>
	</dd>

	<c:if test="${(not empty rating) && ((is_admin && rating.buyerRating) || (is_active_resource && !rating.buyerRating))}">
		<dt><dd><small class="meta"><a class="flag-rating" data-id="${rating.id}">Flag this rating</a> if you believe there is a problem</small></dd></dt>
	</c:if>

</dl>
