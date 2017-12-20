<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="wmfmt" uri="http://www.workmarket.com/taglib/fmt" %>
<%@ taglib prefix="wmfn" uri="http://www.workmarket.com/taglib/functions" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>

<c:choose>
	<c:when test="${eligibility.eligible}">
		<c:choose>
			<c:when test="${work.configuration.assignToFirstResource}">
				<c:choose>
					<c:when test="${scoreCard.hasBadScore()}">
						<a class="default-button accept-action popup-open" data-popup-selector="#buyer-warning-popup">${doWorkTitle} <span class="terms-tag ${termsTagClass}">(${termsTagText})</span></a>
					</c:when>
					<c:otherwise>
						<a class="accept-terms-button accept-action spin" href="<c:out value="${doWorkUri}" />">${doWorkTitle} <span class="terms-tag ${termsTagClass}">(${termsTagText})</span></a>
					</c:otherwise>
				</c:choose>
				<a class="counteroffer spin" href="/mobile/assignments/negotiate/${work.workNumber}">Counteroffer</a>
				<a class="decline decline-action spin" href="/mobile/assignments/reject/${work.workNumber}" >Decline</a>
			</c:when>
			<c:otherwise>
				<c:choose>
					<c:when test="${scoreCard.hasBadScore()}">
						<a class="default-button accept-action popup-open" data-popup-selector="#buyer-warning-popup">${doWorkTitle} <span class="terms-tag ${termsTagClass}">(${termsTagText})</span></a>
					</c:when>
					<c:otherwise>
						<a href="<c:out value="${doWorkUri}" />" class="accept-terms-button accept-action spin" >${doWorkTitle} <span class="terms-tag ${termsTagClass}">(${termsTagText})</span></a>
					</c:otherwise>
				</c:choose>
				<a class="decline decline-action spin" href="/mobile/assignments/reject/${work.workNumber}" >Decline</a>
			</c:otherwise>
		</c:choose>
	</c:when>
	<c:otherwise>
		<a href="/assignments/details/${work.workNumber}?site_preference=normal" class="full-site" >View on Full Site &raquo;</a>
	</c:otherwise>
</c:choose>
