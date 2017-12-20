<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="wm-csrf" uri="http://www.workmarket.com/taglib/csrf" %>
<%@ taglib prefix="wm" tagdir="/WEB-INF/tags" %>

<c:set var="pageScript" value="wm.pages.admin.insurance.edit_userinsurance" scope="request"/>

<wm:admin pagetitle="Edit">

	<div class="sidebar admin">
		<c:import url="/WEB-INF/views/web/partials/admin/quick_links.jsp" />
	</div>

	<div class="content">
		<c:import url="/WEB-INF/views/web/partials/general/notices.jsp">
			<c:param name="bundle" value="${bundle}" />
		</c:import>

		<form action="/admin/insurance/edit_userinsurance" method="post" id="certs_form" enctype="multipart/form-data">
			<wm-csrf:csrfToken />
			<input type="hidden" name="id" value="${id}" />
			<input type="hidden" name="userId" value="${user_id}" />
			<input type="hidden" name="action" value="" id="action_to_take" />
			<input type="hidden" name="note" value="" id="note_to_send" />

			<div class="control-group">
				<label class="control-label">User</label>
				<div class="text">
					<a href="<c:url value="/admin/manage/profiles/index/${user.userNumber}"/>">${user.fullName}</a>
				</div>
			</div>
			<div class="control-group">
				<label class="control-label">User Name</label>
				<div class="text">${user.fullName}</div>
			</div>

			<div class="control-group">
				<label class="control-label">Industry</label>
				<div class="text">${user_insurance.insurance.industry.name}</div>
			</div>

			<div class="control-group">
				<label class="control-label">Insurance Type</label>
				<div class="text">${user_insurance.insurance.name}</div>
			</div>

			<c:set var="override_section" value="${(user_insurance.insurance.id == workers_comp_insurance_id) ? '' : 'dn'}"/>
			<div class="control-group ${override_section}" id="override-section">
				<input class="${override_section}" type="checkbox" name="notApplicableOverride" id="override" ${user_insurance.notApplicableOverride ? 'checked' : ''}/>
				<span class="${override_section}" id="affirmation">${user.fullName} attests: My state does not require me to carry workers compensation insurance.</span>
			</div>
			<div id="affirm" class="dn">
				<br/>
				<div class="control-group">
					<label class="control-label">Provider</label>
					<div class="input">
						<input type="text" name="provider" value="${user_insurance.provider}" maxlength="255" id="provider" />
					</div>
				</div>

				<div class="control-group">
					<label class="control-label">Policy Number</label>
					<div class="input">
						<input type="text" name="policyNumber" value="${user_insurance.policyNumber}" maxlength="255" id="policy_number" />
					</div>
				</div>

				<div class="control-group">
					<label class="control-label">Coverage</label>
					<div class="input">
						<input type="number" name="coverage" value="${user_insurance.coverage}" min="0" max="9999999999" id="coverage" />
					</div>
				</div>

				<div class="control-group">
					<label class="control-label">Issue Date</label>
					<div class="input">
						<input type="text" name="issueDate" value="${issue_date}" maxlength="10" id="issue_date" />
					</div>
				</div>

				<div class="control-group">
					<label class="control-label">Expiration Date</label>
					<div class="input">
						<input type="text" name="expirationDate" value="${expiration_date}" maxlength="10" id="expiration_date" />
					</div>
				</div>

				<c:if test="${not empty assets}">
					<div id="certification_attachment">
						<div class="clearfix">
							<label class="control-label">Attachment</label>
							<div class="input">
								<ul><c:forEach var="item" items="${assets}">
									<li><a href="${item.uri}">${item.name}</a></li>
								</c:forEach></ul>
							</div>
						</div>
					</div>
				</c:if>
			</div>
			<div class="control-group">
				<label class="control-label">Attachment</label>
				<div class="controls">
					<input type="file" name="file" id="attachment"/>
				</div>
			</div>

			<div class="wm-action-container">
				<a class="button action_cta" id="approve">Approve</a>
				<a class="button action_cta" id="decline">Decline</a>
				<a class="button action_cta" id="need_info">Need Info</a>
				<a class="button action_cta" id="on_hold">On Hold</a>
				<a class="button action_cta" id="unverified">Leave Unverified</a>
				<a class="button" href="<c:url value="/admin/insurance/review"/>">Cancel</a>
			</div>
		</form>
	</div>

	<div class="dn">
		<div id="decline_note_popup">
			<p>Send a note to the user regarding their declined profile item.</p>
			<textarea name="decline_note" id="decline_note" class="span6"></textarea>

			<div class="wm-action-container">
				<a class="button" id="decline_nonote_action_cta">Decline-No Note</a>
				<a class="button" id="decline_action_cta">Send Decline</a>
			</div>
		</div>

		<div id="more_info_popup">
			<p>Send a note to the user requesting more information.</p>

			<div class="input">
				<textarea name="more_information_note" id="more_information_note" class="span6"></textarea>
			</div>

			<div class="wm-action-container">
				<a class="button" id="more_info_action_cta">Send Note</a>
			</div>
		</div>
	</div>

</wm:admin>
