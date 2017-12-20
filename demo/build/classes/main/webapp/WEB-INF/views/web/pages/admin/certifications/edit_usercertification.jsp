<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="wm-csrf" uri="http://www.workmarket.com/taglib/csrf" %>
<%@ taglib prefix="wm" tagdir="/WEB-INF/tags" %>

<wm:admin pagetitle="Edit Certifications">

<c:set var="pageScript" value="wm.pages.admin.review" scope="request" />

<div class="sidebar admin">
	<c:import url="/WEB-INF/views/web/partials/admin/quick_links.jsp" />
</div>

<div class="content">
	<h1>Unverified Certification</h1>

	<c:import url="/WEB-INF/views/web/partials/general/notices.jsp">
		<c:param name="bundle" value="${bundle}" />
	</c:import>

	<form action="<c:url value="/admin/certifications/edit_usercertification"/>" enctype="multipart/form" class="form-horizontal" method="post" id="certs_form">
		<wm-csrf:csrfToken />
		<input type="hidden" name="id" value="${id}" />
		<input type="hidden" name="user_id" value="${user_id}" />
		<input type="hidden" name="action" id="action_to_take" value="" />
		<input type="hidden" name="note" id="note_to_send" value="" />


		<div class="control-group">
			<label class="control-label">User</label>
			<div class="controls">
				<a target="_blank" href="<c:url value="/admin/manage/profiles/index/${user_info.userNumber}"/>"><c:out value="${user_info.fullName}" /></a>
			</div>
		</div>

		<div class="control-group">
			<label class="control-label">Industry</label>
			<div class="controls">
				<c:out value="${vendor.certificationType.name}" />
			</div>
		</div>

		<div id="select_provider" class="control-group">
			<label class="control-label">Company</label>
			<div class="controls">
				<c:out value="${vendor.name}" />
			</div>
		</div>

		<div id="select_certification" class="control-group">
			<label class="control-label">Certification</label>
			<div class="controls">
				<c:out value="${certification.name}" />
			</div>
		</div>

		<div id="certification_number" class="control-group">
			<label class="control-label">Certification Number</label>
			<div class="controls">
				<input type="text" name="number" id="number" maxlength="50" value="<c:choose><c:when test="${!empty param.number}"><c:out value="${param.number}" /></c:when><c:otherwise><c:out value="${user_certification.certificationNumber}" /></c:otherwise></c:choose>" />
			</div>
			<div id="provider_instructions"></div>
		</div>

		<div class="control-group">
			<label class="control-label">Issue Date</label>
			<div class="controls">
				<input type="text" name="issue_date" id="issue_date" maxlength="10" value="<c:choose><c:when test="${!empty param.issue_date}"><c:out value="${param.issue_date}" /></c:when><c:otherwise><fmt:formatDate value="${user_certification.issueDate.time}" pattern="MM/dd/yyyy"/></c:otherwise></c:choose>" />
			</div>
		</div>

		<div class="control-group">
			<label class="control-label">Expiration Date</label>
			<div class="controls">
				<input type="text" name="expiration_date" id="expiration_date" maxlength="10" value="<c:choose><c:when test="${!empty param.expiration_date}"><c:out value="${param.expiration_date}" /></c:when><c:otherwise><fmt:formatDate value="${user_certification.expirationDate.time}" pattern="MM/dd/yyyy"/></c:otherwise></c:choose>" />
			</div>
		</div>

		<c:if test="${!empty user_certification.assets}">
			<div id="certification_attachment" class="control-group">
				<label class="control-label">Attachments</label>
				<div class="text">
					<ul class="unstyled">
						<c:forEach var="item" items="${user_certification.assets}">
							<li><a href="<c:out value="${item.uri}" />"><c:out value="${item.name}" /></a></li>
						</c:forEach>
					</ul>
				</div>
			</div>
		</c:if>

		<div id="certification_attachment" class="control-group">
			<label class="control-label">Additional Attachments</label>
			<div class="controls">
				<input type="file" name="attachment" id="attachment" />
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
		<div class="input">
			<textarea name="decline_note" id="decline_note" class="span8" rows="4"></textarea>
		</div>

		<div class="wm-action-container">
			<a class="button" id="decline_action_cta">Decline</a>
		</div>
	</div>

	<div id="more_info_popup">
		<p>Optional: Send a note to the user requesting more information.</p>
		<div class="input">
			<textarea name="more_information_note" id="more_information_note" class="span8" rows="4"></textarea>
		</div>

		<div class="wm-action-container">
			<a class="button" id="more_info_action_cta">Need Info</a>
		</div>
	</div>
</div>

</wm:admin>
