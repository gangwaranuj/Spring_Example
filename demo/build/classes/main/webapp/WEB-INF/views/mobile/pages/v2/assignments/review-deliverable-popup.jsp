<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="wm-csrf" uri="http://www.workmarket.com/taglib/csrf" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="wm" tagdir="/WEB-INF/tags" %>

<%-- Review Deliverable Popup/Carousel --%>
<div id="review-deliverable-popup" class="popup-content grid wrap">
	<div class="grid nav">
		<div class="unit whole">
			<h2 class="deliverable-title">
				Deliverable Details
			</h2>
			<a href="javascript:void(0);" class="popup-close deliverable-cancel">x</a>
		</div>
	</div>

	<div class="unit whole">
		<input type="hidden" name="id" value="${work.workNumber}" />
		<input type="hidden" class="asset_type" name="asset_type" value="" />
		<input type="hidden" class="deliverable_group_id" name="deliverable_requirement_id" value="" />
		<input type="hidden" class="position" name="position" value="" />

		<div class="preview-container">
			<div class="deliverable-details">
				<div class="empty-details" style="display: none;">No deliverable yet: click button below to upload.</div>
				<div class="uploaded-details" style="display: none;">
					<div>Name: <span class="upload-name"></span></div>
					<div>Uploaded by: <span class="uploaded-by"></span></div>
					<div>Date: <span class="upload-date"></span></div>
					<div class="preview-not-available">Preview Not Available</div>
				</div>
			</div>
			<div class="deliverable-image-container">
				<div class="deliverable-border-preview"></div>
				<div class="deliverable-preview" style="display: none;"></div>
				<div class="rejected-popup-container">
					<c:import url="/WEB-INF/views/mobile/partials/svg-icons/icon-deliverables-rejected.jsp" />
				</div>
				<div class="updated-popup-container">
					<c:import url="/WEB-INF/views/mobile/partials/svg-icons/icon-deliverables-updated.jsp" />
				</div>
				<a class="carousel-deliv left-arrow-next" data-direction="left">
					<wm:icon name="left-arrow" active="true"/>
				</a>
				<a class="carousel-deliv right-arrow-next" data-direction="right">
					<wm:icon name="right-arrow" active="true"/>
				</a>
			</div>
			<div class="rejected-details" style="display: none;">
				<c:import url="/WEB-INF/views/mobile/partials/svg-icons/icon-ban.jsp" /><span class="rejected-header">Rejection Notes</span>
				<div class="rejected-date"></div>
				<div class="rejected-reason"></div>
			</div>
		</div>
		<button class="delete-button remove-deliverable" style="display: none;">Delete File</button>
		<button class="upload-replacement-button popup-open-replacement" data-popup-selector="#add-attachment-popup" style="display: none;">
			<img class="upload-icon" src="${mediaPrefix}/images/live_icons/assignments/upload-icon.svg">Upload Replacement File
		</button>
		<button class="upload-original-button popup-open-replacement" data-popup-selector="#add-attachment-popup" style="display: none;">Upload Deliverable</button>
		<a href="#" class="download-original-button" >Download Attachment</a>

	</div><%--unit--%>
</div>
