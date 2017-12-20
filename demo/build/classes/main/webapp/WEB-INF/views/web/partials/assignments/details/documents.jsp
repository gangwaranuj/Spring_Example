<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="wmfmt" uri="http://www.workmarket.com/taglib/fmt" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="wm" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<div id="documents">
	<div class="accordion media completion" id="documentsAccordion">
		<div class="completion-icon">
			<jsp:include page="/WEB-INF/views/web/partials/svg-icons/workdetails/documents-icon.jsp"/>
		</div>
		<div class="media-body">
			<div class="accordion-heading">
				<a data-toggle="collapse" data-parent="#documentsAccordion" href="#documents-well">
					<h4 class="muted">Documents
						<i class="toggle-icon pull-right icon-minus-sign"></i>
					</h4>
				</a>
				<c:if test="${!is_admin && work.assetsSize gt 1}">
					<a title="Download All" class="documents-download-all-icon" href="/assignments/download_attachment_assets/${work.workNumber}">
						<jsp:include page="/WEB-INF/views/web/partials/svg-icons/deliverables/icon-download-all.jsp"/>
					</a>
				</c:if>
			</div>

			<div id="documents-well" class="accordion-body collapse <c:if test="${!(is_admin || isInternal) || (work.status.code == workStatusTypes['DRAFT'] || work.status.code == workStatusTypes['SENT'])}">in</c:if>">
				<div class="message"></div>

				<c:if test="${is_active_resource}">
					<p>Please review the following documents before starting work.</p>
				</c:if>
				<div class="documents"></div>

				<c:if test="${is_admin}">
					<div class="upload-documents">
						<form>
							<input type="file" name="files[]" multiple/>
							<a class="upload-documents-trigger" href="javascript:void(0);">Add Document</a>
						</form>
					</div>
				</c:if>
			</div>
		</div>

		<div class="dn">
			<div id="deliverables_details">
				<div id="file_details" align="middle">
					<span id="filename"></span>
					<span id="filesize"></span>
					<span id="filedescription"></span>
				</div>

				<div id="mediadetail" align="middle">
					<img class="deliverable_image" src=""/>
				</div>

				<div class="wm-action-container">
					<a href="/assignments/download_deliverable_assets/${work.workNumber}" class="button">Download All</a>
					<a id="download_link" href="/asset/download/" class="button">Download Deliverable</a>
				</div>
			</div>
		</div>

	</div>
</div>
