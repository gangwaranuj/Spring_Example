<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="wmfmt" uri="http://www.workmarket.com/taglib/fmt" %>
<%@ taglib prefix="wmfn" uri="http://www.workmarket.com/taglib/functions" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>

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

<c:import url="/WEB-INF/views/mobile/partials/notices.jsp"/>

<div class="parts-page">
	<jsp:include page="/WEB-INF/views/mobile/partials/nav.jsp">
		<jsp:param name="title" value="${title}" />
		<jsp:param name="isNotPaid" value="${work.status.code ne WorkStatusType.PAID}" />
	</jsp:include>
	<div class="wrap">
		<div class="_partsRoot_">
			<div id="partsSent"></div>
			<div id="partsReturn"></div>
		</div>
		<c:import url="/WEB-INF/views/mobile/partials/assignments/details/parts-template.jsp"/>
	</div>
</div>
