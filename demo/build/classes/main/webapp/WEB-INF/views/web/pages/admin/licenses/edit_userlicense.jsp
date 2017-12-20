<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="wm-csrf" uri="http://www.workmarket.com/taglib/csrf" %>
<%@ taglib prefix="wm" tagdir="/WEB-INF/tags" %>

<wm:admin pagetitle="View">

<c:set var="pageScript" value="wm.pages.admin.review" scope="request" />

<div class="sidebar admin">
	<c:import url="/WEB-INF/views/web/partials/admin/quick_links.jsp" />
</div>

<div class="content">
	<c:import url="/WEB-INF/views/web/partials/general/notices.jsp">
		<c:param name="bundle" value="${bundle}" />
	</c:import>

	<h3>Review License</h3>
	<form action="/admin/licenses/edit_userlicense" method="post" class="form-horizontal" id="certs_form" enctype="multipart/form-data">
		<wm-csrf:csrfToken />
		<input type="hidden" name="id" value="<c:out value="${id}" />" />
		<input type="hidden" name="userId" value="<c:out value="${user_id}" />" />
		<input type="hidden" name="action" value="" id="action_to_take" />
		<input type="hidden" name="note" value="" id="note_to_send" />

			<div class="control-group">
				<label class="control-label">User</label>
				<div class="controls">
					<a href="<c:url value="/admin/manage/profiles/index/${user_info.userNumber}"/>"><c:out value="${user_info.fullName}"/></a>
				</div>
			</div>

			<div class="control-group">
				<label class="control-label">State</label>
				<div class="controls"><c:out value="${user_license.license.state}"/></div>
			</div>

			<div class="control-group">
				<label class="control-label">License Name</label>
				<div class="controls"><c:out value="${user_license.license.name}"/></div>
			</div>

			<div class="control-group">
				<label class="control-label">License Number</label>
				<div class="controls">
					<input type="text" name="licenseNumber" value="${user_license.licenseNumber}" maxlength="50" id="license_number" />
				</div>
			</div>

			<div class="control-group">
				<label class="control-label">Issue Date</label>
				<div class="controls">
					<input type="text" name="issueDate" value="${issue_date}" maxlength="10" id="issue_date" />
				</div>
			</div>

			<div class="control-group">
				<label class="control-label">Expiration Date</label>
				<div class="controls">
					<input type="text" name="expirationDate" value="${expiration_date}" maxlength="10" id="expiration_date" />
				</div>
			</div>

			<div class="control-group">
				<c:if test="${not empty assets}">
					<div id="license_attachment">
						<label class="control-label">Attachment</label>
						<div class="controls">
							<ul>
								<c:forEach var="item" items="${assets}">
									<li><a href="<c:out value="${item.uri}" />"><c:out value="${item.name}" /></a></li>
								</c:forEach>
							</ul>
						</div>
					</div>
				</c:if>
			</div>

			<div class="control-group">
				<label class="control-label">Attachment</label>
				<div class="controls">
					<input type="file" name="file" id="attachment" />
				</div>
			</div>

		<div class="wm-action-container">
			<a class="button" href="<c:url value="/admin/licenses/review"/>">Cancel</a>
			<a class="button action_cta" id="decline">Decline</a>
			<a class="button action_cta" id="need_info">Need Info</a>
			<a class="button action_cta" id="on_hold">On Hold</a>
			<a class="button action_cta" id="unverified">Leave Unverified</a>
			<a class="button action_cta" id="approve">Approve</a>
		</div>

	</form>
</div>

<div class="dn">
	<div id="decline_note_popup">
		<p>Send a note to the user regarding their declined profile item.</p>
		<textarea name="decline_note" id="decline_note" class="span8" rows="4"></textarea>

		<div class="wm-action-container">
			<a class="button" id="decline_nonote_action_cta">Decline - No Note</a>
			<a class="button" id="decline_action_cta">Send</a>
		</div>
	</div>

	<div id="more_info_popup">
		<p>Send a note to the user requesting more information.</p>
		<textarea name="more_information_note" id="more_information_note" class="span8" rows="4"></textarea>

		<div class="wm-action-container">
			<a class="button" id="more_info_action_cta">Send</a>
		</div>
	</div>
</div>

</wm:admin>
