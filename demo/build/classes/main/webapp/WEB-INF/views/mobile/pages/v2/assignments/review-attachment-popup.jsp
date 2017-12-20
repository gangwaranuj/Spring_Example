<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="wm-csrf" uri="http://www.workmarket.com/taglib/csrf" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<%-- Review Deliverable Popup/Carousel --%>
<div id="review-attachment-popup" class="popup-content grid wrap">
	<div class="grid nav">
		<div class="unit whole">
			<h2 class="attachment-title">
				Attachment Details
			</h2>
			<a href="javascript:void(0);" class="popup-close attachment-cancel">x</a>
		</div>
	</div>

	<div class="unit whole">
		<input type="hidden" name="id" value="${work.workNumber}" />
		<input type="hidden" class="asset_type" name="asset_type" value="" />
		<input type="hidden" class="uuid" name="uuid" value="" />

		<div class="preview-container">
			<div class="deliverable-details">
				<div class="uploaded-details">
					<div>Uploaded by: <span class="uploaded-by"></span></div>
					<div>Date: <span class="upload-date"></span></div>
				</div>
			</div>
			<div class="deliverable-image-container">
				<div class="deliverable-border-preview"></div>
				<img src="" class="deliverable-preview" style="display: none;"/>
			</div>
			<a class="carousel-deliv" data-direction="left"><img class="left-arrow-next" src="${mediaPrefix}/images/left_arrow2.png"/></a>
			<a class="carousel-deliv" data-direction="right"><img class="right-arrow-next" src="${mediaPrefix}/images/right_arrow2.png"/></a>
		</div>

		<button class="delete-button remove-asset">Delete File</button>
		<a href="#" class="download-original-button">Download Attachment</a>
	</div><%--unit--%>
</div>
