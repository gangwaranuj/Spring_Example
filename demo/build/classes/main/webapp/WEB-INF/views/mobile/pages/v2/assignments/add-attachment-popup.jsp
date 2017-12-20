<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="wm-csrf" uri="http://www.workmarket.com/taglib/csrf" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<%-- Add attachment popup --%>
<div id="add-attachment-popup" class="popup-content grid wrap">
	<jsp:include page="/WEB-INF/views/mobile/partials/nav.jsp">
		<jsp:param name="title" value="Add Attachment" />
	</jsp:include>
	<div class="unit whole">
		<div class="mobile-signature" style="display: none">
			<h3>Option 1: Select document</h3>
		</div>
		<c:set var="extensions" value=".${fn:join(validExtensions, ', .')}"/>

		<form id="add-attachment-form" action="/mobile/assignments/dialogs/add_deliverable/${work.workNumber}" method="post" enctype="multipart/form-data">
			<wm-csrf:csrfToken />

			<input type="hidden" name="id" value="${work.workNumber}" />
			<input type="hidden" class="asset_type" name="asset_type" value="" />
			<input type="hidden" class="deliverable_group_id" name="deliverable_requirement_id" value="" />
			<input type="hidden" class="position" name="position" value="" />
			<input type="file" name="file" id="upload-file" accept="${extensions}" />

			<c:if test="${empty work.deliverableRequirementGroupDTO.deliverableRequirementDTOs}">
			<div class="description-section">
				<label for="new-file-description">Enter file description (optional)</label>
				<textarea id="new-file-description" name="description"></textarea>
			</div>
			</c:if>

			<a href="javascript:void(0)" class="popup-close close-button">Cancel</a>
			<input type="submit" name="submit" value="Upload" class="upload-button" disabled/>
		</form>

	</div><%--unit--%>
	<c:if test="${allowMobileSignature}">
		<div class="unit whole mobile-signature" style="display: none">
			<h3>Option 2: Get signature via Phone</h3>
			<a href="javascript:void(0)" class="default-button get-signature">Sign with Phone</a>
		</div>
	</c:if>

</div>