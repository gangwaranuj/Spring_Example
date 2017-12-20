<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="wmfmt" uri="http://www.workmarket.com/taglib/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="wm-csrf" uri="http://www.workmarket.com/taglib/csrf" %>

<c:set var="pageScript" value="wm.pages.mobile.assignments.deliverables" scope="request"/>
<c:set var="pageScriptParams" value="${work.workNumber}, ${deliverableBaseJson}, ${allowMobileSignature eq true}" scope="request"/>
<c:set var="deliverables" value="${work.deliverableRequirementGroupDTO.deliverableRequirementDTOs}" scope="request" />
<c:set var="backUrl" value="/mobile/assignments/details/${work.workNumber}" scope="request"/>

<div class="wrap deliverables-page">
	<jsp:include page="/WEB-INF/views/mobile/partials/nav.jsp">
		<jsp:param name="title" value="Deliverables" />
	</jsp:include>

	<div class="grid content">
		<div class="unit whole" id="public-message">
			<c:import url="/WEB-INF/views/mobile/partials/notices.jsp" />
		</div>
		<div class="deliverable-header">
			<div class="deliverables-timer-text">
				The following deliverables are due <b>${work.deliverableRequirementGroupDTO.hoursToComplete} hours</b> after the assignment starts.
			</div>

			<c:if test="${!work.deliverableRequirementGroupDTO.instructions.isEmpty()}">
				<div class="instructions-help">
					${work.deliverableRequirementGroupDTO.instructions}
				</div>
			</c:if>
		</div>
		<div class="deliverable-base"></div>
	</div>

	<script id="new-deliverable-section" type="text/template">
		{{ _.each( deliverableBaseReqs, function (deliverableGroup) { }}
		<div class="deliverables-wrap">
			<h3><span class="badge">{{= deliverableGroup.number_of_files }}</span>
				{{ if (deliverableGroup.type === 'photos') {  }}Photos {{  }}
				{{ } else if (deliverableGroup.type === 'sign_off') {  }} Signoff {{ }}
				{{ } else if (deliverableGroup.type === 'other') {  }}Other Deliverables {{ } }}
				required
				<div class="completed-{{= deliverableGroup.id }} pull-right" style="display: none;">
					<jsp:include page="/WEB-INF/views/web/partials/svg-icons/workdetails/complete-icon.jsp"/>
				</div>
			</h3>
			<p class="{{= deliverableGroup.id }}-count missing-deliverables"></p>
			<p class="instructions-help">{{= deliverableGroup.instructions }}</p>
			<div class="individual-deliverable-container-{{= deliverableGroup.id }} deliverable-table">
				{{ var position = 0; }}
				{{ _(deliverableGroup.number_of_files).times(function() { }}
				<div class="{{= deliverableGroup.id + '-' + position}} deliverable-block {{= deliverableGroup.id}}-block">
					<div class="-{{= deliverableGroup.type }}-instance icon-instance popup-open-deliv deliverable-border empty"
						 data-position="{{= position }}"
						 data-parent-id="{{= deliverableGroup.id }}"
						 data-type="{{= deliverableGroup.type }}"
						 data-popup-selector="#add-attachment-popup"
						>
						<jsp:include page="/WEB-INF/views/web/partials/svg-icons/deliverables/liveicon-deliverables.jsp"/>
					</div>
				</div>
				{{ position++; }}
				{{ }); }}
			</div>
			<div class="-{{= deliverableGroup.id }}-tap tap-help">Tap icon above to upload.</div>
			<div class="-{{= deliverableGroup.id }}-click help" style="display: none;">Click on a image to view more details</div>
			<div class="upload-button additional-{{= deliverableGroup.id }} popup-open-additional"
				 data-popup-selector="#add-attachment-popup"
				 data-parent-id="{{= deliverableGroup.id }}"
				 data-type="{{= deliverableGroup.type }}"
				 style="display: none;">
				<img class="upload-icon" src="${mediaPrefix}/images/live_icons/assignments/upload-icon.svg">
				Upload Additional Files</div>
		</div>
		{{ }); }}
	</script>

	<script id="resource-deliverables-list-template" type="text/template">
		<div class="uploaded-{{= asset.type }} deliverable-border {{ if (asset.rejected === true) { }} rejected {{ } }} {{ if (asset.updated === true) { }} updated {{ } }}">
			<a href="javascript:void(0);"
			   data-position="{{= asset.position }}"
			   data-parent-id="{{= asset.deliverableRequirementId }}"
			   data-uploaded-by="{{= asset.uploadedBy }}"
			   data-upload-date="{{= asset.uploadDate }}"
			   data-name="{{= asset.name }}"
			   data-uuid="{{= asset.uuid }}"
			   data-type="{{= asset.type }}"
			   data-mime-type="{{= asset.mimeType }}"
			   data-rejected="{{= asset.rejected }}"
			   data-rejected-on="{{= asset.rejectedOn }}"
			   data-rejected-reason="{{= asset.rejectionReason }}"
			   data-updated="{{= asset.updated }}"
			   data-uri="{{= asset.uri }}"
			   data-transformLargeUuid="{{= asset.transformLargeUuid }}"
			   data-id="{{= asset.id }}"
			   class="asset-details popup-open-deliv"
			   data-popup-selector="#review-deliverable-popup">
				{{if (asset.mimeType === 'image/jpeg' || asset.mimeType === 'image/pjpeg' || asset.mimeType === 'image/jpg' || asset.mimeType === 'image/gif' || asset.mimeType === 'image/tiff' || asset.mimeType === 'image/png') {  }}
					<img src="/asset/
					{{ if (asset.transformLargeUuid && asset.transformLargeUuid.length) { }}
					{{= asset.transformLargeUuid }}
					{{ } else { }}
					{{= asset.uuid }}
					{{ } }}"
						class="deliverable-thumbnail"/>
				{{ } else { }}
					{{ if (asset.rejected !== true) {  }}
						<c:import url="/WEB-INF/views/mobile/partials/svg-icons/icon-green-checkmark.jsp" />
					{{ } }}
					<jsp:include page="/WEB-INF/views/web/partials/svg-icons/deliverables/upload-file-type-icon.jsp"/>
					<div class="filename">{{= asset.name }}</div>
				{{ } }}
			</a>
			{{ if (asset.rejected === true) {  }}
				<div class="status-container rejected-{{= asset.deliverableRequirementId }}">
					<c:import url="/WEB-INF/views/mobile/partials/svg-icons/icon-deliverables-rejected.jsp" />
				</div>
			{{ } else if (asset.updated === true) { }}
				<div class="status-container updated-{{= asset.deliverableRequirementId }}">
					<c:import url="/WEB-INF/views/mobile/partials/svg-icons/icon-deliverables-updated.jsp" />
				</div>
			{{ } }}
		</div>
	</script>

	<script id="additional-deliverable-container-template" type="text/template">
		<div class="{{= asset.deliverableRequirementId + '-' + asset.position}} deliverable-block"></div>
	</script>

</div>


<%-- Add attachment popup --%>
<c:import url="/WEB-INF/views/mobile/pages/v2/assignments/add-attachment-popup.jsp" />

<c:import url="/WEB-INF/views/mobile/pages/v2/assignments/review-deliverable-popup.jsp" />
