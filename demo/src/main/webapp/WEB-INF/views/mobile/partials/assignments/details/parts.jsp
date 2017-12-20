<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="wmfmt" uri="http://www.workmarket.com/taglib/fmt" %>
<%@ taglib prefix="wmfn" uri="http://www.workmarket.com/taglib/functions" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="wm" tagdir="/WEB-INF/tags" %>

<c:set var="webpackScript" value="mobileparts" scope="request"/>

<script>
	var config = {
		workNumber: ${work.workNumber},
		partGroup: ${partGroup},
		partsConstants: ${partsConstantsJson},
		isNotSentOrDraft: ${!(work.status.code == workStatusTypes['DRAFT'] || work.status.code == workStatusTypes['SENT'])},
		isOwnerOrAdmin: ${is_owner or is_admin},
		isSuppliedByWorker: ${not empty work.partGroup && work.partGroup.isSuppliedByWorker()}
	};
</script>

<div class="parts-page">
	<c:choose>
		<c:when test="${not empty work.activeResource}">
			<a class="show details-list-button active" href="/mobile/assignments/part_details/${work.workNumber}">
				Parts and Logistics
				<c:import url="/WEB-INF/views/mobile/partials/svg-icons/icon-clicker-right-orange.jsp"/>
			</a>
		</c:when>
		<c:otherwise>
			<a class="show " href="javascript:void(0);">Parts and Logistics
				<wm:icon name="truck"/>
				<c:import url="/WEB-INF/views/mobile/partials/svg-icons/plus-icon.jsp"/>
				<c:import url="/WEB-INF/views/mobile/partials/svg-icons/minus-icon.jsp"/>
			</a>
			<div class="tell _partsRoot_">
				<div id="partsSent"></div>
				<div id="partsReturn"></div>
			</div>
		</c:otherwise>
	</c:choose>
</div>

<jsp:include page="/WEB-INF/views/mobile/partials/assignments/details/parts-template.jsp"/>
