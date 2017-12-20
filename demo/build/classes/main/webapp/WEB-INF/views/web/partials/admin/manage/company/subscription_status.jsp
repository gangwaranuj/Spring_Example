<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<c:choose>
	<%-- Subscription mode --%>
	<c:when test="${isSubscription}">
		<c:choose>
			<c:when test="${subStatus.hasCancelPending}">
				<c:set var="subscription_status" scope="request" value="cancellation_pending"/>
			</c:when>

			<c:when test="${subStatus.hasCancelApproved}">
				<c:set var="subscription_status" scope="request" value="cancellation_approved"/>
			</c:when>

			<c:otherwise>
				<c:set var="subscription_status" scope="request" value="effective"/>
			</c:otherwise>
		</c:choose>
	</c:when>

	<%-- Transactional mode --%>
	<c:otherwise>
		<c:choose>
			<c:when test="${subStatus.isActive}">
				<c:choose>
					<c:when test="${subStatus.hasCancelPending}">
						<c:set var="subscription_status" scope="request" value="cancellation_pending"/>
					</c:when>

					<c:when test="${subStatus.hasCancelApproved}">
						<c:set var="subscription_status" scope="request" value="cancellation_approved"/>
					</c:when>

					<c:otherwise>
						<c:set var="subscription_status" scope="request" value="active"/>
					</c:otherwise>
				</c:choose>
			</c:when>

			<c:otherwise>
				<c:choose>
					<c:when test="${subStatus.pendingApproval}">
						<c:set var="subscription_status" scope="request" value="pending_approval"/>
					</c:when>

					<c:otherwise>
						<c:set var="subscription_status" scope="request" value=""/>
					</c:otherwise>
				</c:choose>
			</c:otherwise>
		</c:choose>
	</c:otherwise>
</c:choose>
