<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="wm-csrf" uri="http://www.workmarket.com/taglib/csrf" %>
<%@ taglib prefix="wmfmt" uri="http://www.workmarket.com/taglib/fmt" %>
<%@ taglib prefix="wm" tagdir="/WEB-INF/tags" %>

<wm:admin pagetitle="Edit">

<c:set var="pageScript" value="wm.pages.admin.certifications.editcertifications" scope="request"/>
<c:set var="pageScriptParams" value="'${wmfmt:escapeJavaScript(vendor.certificationType.id)}','${wmfmt:escapeJavaScript(vendor.id)}'" scope="request"/>
<c:set var="pageScript" value="wm.pages.admin.review" scope="request" />

<div class="sidebar admin">
	<c:import url="/WEB-INF/views/web/partials/admin/quick_links.jsp" />
</div>

<div class="content">
	<c:import url="/WEB-INF/views/web/partials/general/notices.jsp">
		<c:param name="bundle" value="${bundle}" />
	</c:import>

	<form action="<c:url value="/admin/certifications/editcertifications"/>" method="post" id="certs_form" class="form-horizontal">
		<wm-csrf:csrfToken />
		<input type="hidden" name="id" value="${id}" />
		<input type="hidden" name="action" id="action_to_take" value="" />
		<input type="hidden" name="note" id="note_to_send" value="" />

		<fieldset>
			<div class="controls">
				<label class="control-label">Vendor</label>
				<div class="controls">
					<c:out value="${vendor.certificationType.name}" />
					<span class="help-block">Need to change the Vendor? If so, select the new vendor from the list below.</span>
				</div>
			</div>

			<div id="select_provider" class="controls dn">
				<label class="control-label">Approved Vendors</label>
				<div class="controls"></div>
			</div>

			<div class="controls" style="clear: both;">
				<label class="control-label" for="currentcertification">Unapproved Certification</label>
				<div class="controls">
					<input type="text" name="currentcertification" id="currentcertification" value="<c:choose><c:when test="${!empty param.currentcertification}"><c:out value="${param.currentcertification}" /></c:when><c:otherwise><c:out value="${certification.name}" /></c:otherwise></c:choose>" />
				</div>
			</div>
		</fieldset>

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
