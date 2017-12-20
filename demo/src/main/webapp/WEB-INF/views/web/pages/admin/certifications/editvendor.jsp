<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="wm-csrf" uri="http://www.workmarket.com/taglib/csrf" %>
<%@ taglib prefix="wm" tagdir="/WEB-INF/tags" %>

<wm:admin pagetitle="Edit Vendor">

<c:set var="pageScript" value="wm.pages.admin.review" scope="request" />

<div class="sidebar">
	<c:import url="/WEB-INF/views/web/partials/admin/quick_links.jsp" />
</div>

<div class="content">
	<h3>Edit Certification Vendor (Unverified)</h3>

	<c:import url="/WEB-INF/views/web/partials/general/notices.jsp">
		<c:param name="bundle" value="${bundle}" />
	</c:import>

	<form action="<c:url value="/admin/certifications/editvendor"/>" method="post" id="certs_form" class="form-horizontal">
		<wm-csrf:csrfToken />
		<input type="hidden" name="id" value="${id}" />
		<input type="hidden" name="action" id="action_to_take" value="" />
		<input type="hidden" name="note" id="note_to_send" value="" />

		<div class="control-group">
			<label class="control-label">Industry</label>
			<div class="controls"><c:out value="${vendor.certificationType.name}" /></div>
		</div>

		<div class="control-group">
			<label class="control-label">Unapproved Vendor</label>
			<div class="controls">
				<input type="text" name="currentvendor" id="currentvendor" value="<c:choose><c:when test="${!empty param.currentvendor}"><c:out value="${param.currentvendor}" /></c:when><c:otherwise><c:out value="${vendor.name}" /></c:otherwise></c:choose>" />
			</div>
		</div>

		<div id="save_button" class="wm-action-container">
			<a class="button action_cta" id="approve">Approve</a>
			<a class="button action_cta" id="decline">Decline</a>
			<a class="button action_cta" id="need_info">Need Info</a>
			<a class="button action_cta" id="on_hold">On Hold</a>
			<a class="button action_cta" id="unverified">Leave Unverified</a>
			<a class="button" href="<c:url value="/admin/certifications/review"/>">Cancel</a>
		</div>
	</form>

</div>

<div class="dn">
	<div id="decline_note_popup">
		<p>Optional: Send a note to the user regarding their declined profile item.</p>
		<textarea name="decline_note" id="decline_note" class="span8" rows="4"></textarea>

		<div class="wm-action-container">
			<a class="button" id="decline_action_cta">Decline</a>
		</div>
	</div>

	<div id="more_info_popup">
		<p>Optional: Send a note to the user requesting more information.</p>
		<textarea name="more_information_note" id="more_information_note" class="span8" rows="4"></textarea>

		<div class="wm-action-container">
			<a class="button" id="more_info_action_cta">Need Info</a>
		</div>
	</div>

</div>

</wm:admin>
