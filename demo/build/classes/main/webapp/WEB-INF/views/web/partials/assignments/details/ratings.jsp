<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="wmfn" uri="http://www.workmarket.com/taglib/functions" %>
<%@ taglib prefix="wmfmt" uri="http://www.workmarket.com/taglib/fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>


<c:set var='rating' value="${workResponse.buyerRatingForWork}"/>
<c:set var='labels' value="${work.activeResource.labels}"/>
<c:set var="rated_user" value="${work.buyer}"/>
<c:set var="allow_create" value="${is_active_resource}"/>

<c:if test="${is_active_resource || work.activeResource.user.laneType.value == 3}">

	<c:set scope="request" var="rating" value="${workResponse.buyerRatingForWork}"/>
	<c:set scope="request" var="ratedUserName" value="${work.buyer.name.firstName} ${work.buyer.name.lastName}"/>
	<c:set scope="request" var="numRatings" value="${work.buyer.ratingSummary.numberOfRatings}"/>
	<c:set scope="request" var="allowCreate" value="${is_active_resource}"/>
	<c:set scope="request" var="review" value="${workResponse.buyerRatingForWork.review}"/>
	<c:set scope="request" var="userRating" value="${work.buyer.ratingSummary.rating}"/>

	<%--prevents the client from seeing the rating the worker gave them and submitting a revenge rating--%>
	<c:if test="${(work.status.code == workStatusTypes['PAID']  && not empty workResponse.resourceRatingForWork) || is_active_resource}">
		<h5>Rating for Buyer: ${work.company.name}</h5>
		<c:if test="${not empty rating and rating.value > 0 and is_active_resource and is_rating_editable}">
			<div class="alert alert-info">This rating is not final until the assignment is paid. You can edit the rating until the assignment is paid.</div> <button class="edit-rating button pull-right">Edit</button>
		</c:if>

		<c:import url="/WEB-INF/views/web/partials/assignments/details/rate.jsp"/>
	</c:if>

	<c:remove var="rating"/>
	<c:remove var="ratedUserName"/>
	<c:remove var="numRatings"/>
	<c:remove var="allowCreate"/>
	<c:remove var="review"/>
	<c:remove var="userRating"/>
</c:if>

<c:if test="${(is_admin && !is_active_resource) || work.activeResource.user.laneType.value == 3}">
	<c:set scope="request" var='rating' value="${workResponse.resourceRatingForWork}"/>
	<c:set scope="request" var="ratedUserName" value="${work.activeResource.user.name.firstName} ${work.activeResource.user.name.lastName}"/>
	<c:set scope="request" var="numRatings" value="${work.activeResource.user.ratingSummary.numberOfRatings}"/>
	<c:set scope="request" var="allowCreate" value="${is_admin}"/>
	<c:set scope="request" var="review" value="${workResponse.resourceRatingForWork.review}"/>
	<c:set scope="request" var="userRating" value="${work.activeResource.user.ratingSummary.rating}"/>

	<%--prevents the worker from seeing the rating the client gave them and submitting a revenge rating--%>
	<c:if test="${(work.status.code == workStatusTypes['PAID'] && not empty workResponse.buyerRatingForWork) || (is_admin && !is_active_resource)}">
		<h5>Rating for Worker: <c:out value="${ratedUserName}"/></h5>
		<c:if test="${is_admin and not empty rating and rating.value > 0}">
			<small class="last-rating-buyer">Last rated by ${workResponse.lastRatingBuyerFullName} on <c:out value="${wmfmt:formatCalendar('MM-dd-yyyy', rating.modifiedOn)}"/></small>
		</c:if>
		<c:if test="${not empty rating and rating.value > 0 and is_admin and is_rating_editable}">
			<div class="alert alert-info">This rating is not final until the assignment is paid. You can edit the rating until the assignment is paid.</div> <button class="edit-rating button pull-right">Edit</button>
		</c:if>

		<c:import url="/WEB-INF/views/web/partials/assignments/details/rate.jsp"/>
	</c:if>

	<c:remove var="rating"/>
	<c:remove var="ratedUserName"/>
	<c:remove var="numRatings"/>
	<c:remove var="allowCreate"/>
	<c:remove var="review"/>
	<c:remove var="userRating"/>
<div>
	<c:forEach items="${labels}" var="label">
		<c:if test="${!label.ignored and label.code != 'completed_ontime'}" >
			<span class="label label-important tooltipped tooltipped-n" aria-label="<c:out value="${label.description}" />">
				<c:out value="${fn:replace(label.code, '_', ' ')}" />
				<c:if test="${currentUser.buyer}">
						<a href="/assignments/${work.workNumber}/resources/${work.activeResource.user.userNumber}/labels/${wmfn:encrypteId(label.id)}/remove" title="${label.description}" class="remove" data-behavior="remove-label">x</a>
				</c:if>
			</span>
		</c:if>
	</c:forEach>
</div>
</c:if>
<c:import url="/WEB-INF/views/web/partials/reports/review.jsp"/>
<c:import url="/WEB-INF/views/web/partials/assignments/details/rating_result.jsp"/>

