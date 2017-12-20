<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="wm-csrf" uri="http://www.workmarket.com/taglib/csrf" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="wm" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="vr" uri="http://www.workmarket.com/taglib/velvet-rope" %>

<wm:app pagetitle="Edit ${user.fullName}" bodyclass="accountSettings" webpackScript="settings">

	<script>
		var config = {
			mode: 'users',
			isLastDispatcher: ${isLastDispatcher || false}
		};
	</script>

	<c:set var="workerRole" value="false" />
	<vr:rope>
		<vr:venue name="EMPLOYEE_WORKER_ROLE">
			<c:set var="workerRole" value="true" />
		</vr:venue>
	</vr:rope>

	<div class="row_wide_sidebar_left">

		<div class="sidebar">
			<c:import url="/WEB-INF/views/web/partials/mmw/mmw_sidebar.jsp"/>
		</div>

		<div class="content">
			<div class="inner-container">
				<c:import url="/WEB-INF/views/web/partials/message.jsp"/>

				<form:form modelAttribute="user" action="${param.form_uri}" method="post" id="form_user" acceptCharset="utf-8" class="form-horizontal">
				<wm-csrf:csrfToken />

				<form:hidden path="id"/>
				<div class="page-header">
					<c:choose>
						<c:when test="${not empty user.userNumber}">
							<h3>Employee Profile</h3>
						</c:when>
						<c:otherwise>
							<h3>Add New Employee</h3>
						</c:otherwise>
					</c:choose>
				</div>
				<fieldset>
					<div class="control-group">
						<label path="firstName" class="control-label required">First Name</label>

						<div class="controls">
							<form:input path="firstName" id="firstName" maxlength="50" cssClass="input-xlarge" placeholder="First Name"/>
						</div>
					</div>

					<div class="control-group">
						<label path="lastName" class="control-label required">Last Name</label>

						<div class="controls">
							<form:input path="lastName" id="lastName" maxlength="50" cssClass="input-xlarge" placeholder="Last Name"/>
						</div>
					</div>

					<div class="control-group">
						<label path="email" class="control-label required">Email Address</label>

						<div class="controls">
							<form:input path="email" id="email" maxlength="255" cssClass="input-xlarge" placeholder="Email"/>
						</div>
						<c:if test="${not empty user.userNumber and not empty user.changedEmail}">
							<div class="alert-message warning">
								<ul>
									<li>The email for this user has been changed to <em><c:out value="${user.changedEmail}"/></em>.</li>
									<li>Until the new email address is confirmed, the old email <em><c:out value="${user.email}"/></em>
										should be used to log in to the site.
									</li>
								</ul>
							</div>
						</c:if>
					</div>

					<div class="control-group">
						<label path="profile.workPhone" class="control-label required">Work Phone</label>

						<div class="controls controls-row">
							<form:input path="profile.workPhone" id="workPhone" type="tel" class="span3" alt="phone-us" placeholder="Phone Number"/>
							<form:input path="profile.workPhoneExtension" id="workPhoneExtension" maxlength="5" cssClass="span1" placeholder="Ext"/>
							<form:select path="profile.workPhoneInternationalCode" cssClass="pull-right" id="profile.workPhoneInternationalCode" multiple="false">
								<form:options items="${callingCodesList}" itemValue="id" itemLabel="name"/>
							</form:select>
						</div>
					</div>

					<div class="control-group">
						<label path="profile.jobTitle" class="control-label">Job Title</label>

						<div class="controls">
							<form:input path="profile.jobTitle" id="jobTitle" maxlength="45" cssClass="input-xlarge" placeholder="Job Title"/>

						</div>
					</div>

					<c:if test="${empty user.userNumber}">
						<div class="control-group">
							<label path="profile.industries" class="control-label required">Industry</label>

							<div class="controls">
								<select id="industry_list" name="industry">
									<option value="">- Select Industry -</option>
									<c:forEach items="${industries}" var="industry">
										<option value="${industry.id}">
											${industry.name}
										</option>
									</c:forEach>
								</select>
							</div>
						</div>
					</c:if>

					<div class="control-group">
						<label class="control-label">Select from the following roles for this user</label>
						<div class="controls">
							<table>
								<thead>
									<tr>
										<th class="actions">Enabled</th>
										<th>Role<a class="fr hidden" id="toggle-all">Expand All</a></th>
									</tr>
								</thead>
								<tbody class="toggleEmployeeWorker">
									<tr>
										<td class="actions">
											<c:choose>
												<c:when test="${requestScope.isAdmin and not requestScope.isEmployeeWorker}">
													<input type="checkbox" name="isAdmin" checked="checked"/>
												</c:when>
												<c:otherwise>
													<input type="checkbox" name="isAdmin"/>
												</c:otherwise>
											</c:choose>
										</td>
										<td class="accordion" id="adminAccordion">
											<div class="accordion-heading">
												<strong>Admin</strong>
												<a class="fr show" data-toggle="collapse" data-parent="#adminAccordion" href="#admin_overview">Learn More</a>
											</div>
											<div id="admin_overview" class="accordion-body collapse">Has access to all features on Work Market.
												Only Admins can add employees, edit employee permissions including: access to payment center,
												management of bank accounts and specific assignment approval actions on.
											</div>
										</td>
									</tr>
									<tr>
										<td class="actions">
											<c:choose>
												<c:when test="${requestScope.isManager and not requestScope.isEmployeeWorker}">
													<input type="checkbox" name="isManager" checked="checked"/>
												</c:when>
												<c:otherwise>
													<input type="checkbox" name="isManager"/>
												</c:otherwise>
											</c:choose>
										</td>
										<td class="accordion" id="managerAccordion">
											<div class="accordion-heading">
												<strong>Manager</strong>
												<a class="fr show" data-toggle="collapse" data-parent="#managerAccordion" href="#manager_overview">Learn More</a>
											</div>
											<div id="manager_overview" class="accordion-body collapse">
												Has access to all features on Work Market except the payment center, bank account information, and entering/editing tax information.
												Can manage all company assignments. Can also create and manage talent pools and tests.
												Managers can send invitations to new users and build recruiting campaigns.
											</div>
										</td>
									</tr>
									<tr>
										<td class="actions">
											<c:choose>
												<c:when test="${requestScope.isController and not requestScope.isEmployeeWorker}">
													<input type="checkbox" name="isController" checked="checked"/>
												</c:when>
												<c:otherwise>
													<input type="checkbox" name="isController"/>
												</c:otherwise>
											</c:choose>
										</td>
										<td class="accordion" id="controllerAccordion">
											<div class="accordion-heading">
												<strong>Controller</strong>
												<a class="fr show" data-toggle="collapse" data-parent="#controllerAccordion" href="#controller_overview">Learn More</a>
											</div>
											<div id="controller_overview" class="accordion-body collapse">
												This role is given access to the payment center and can approve monetary transactions and transfers.
												Controller can issue a stop payment on an invoiced assignment if the invoice has not been printed.
											</div>
										</td>
									</tr>
									<tr>
										<td class="actions">
											<c:choose>
												<c:when test="${requestScope.isUser and not requestScope.isEmployeeWorker}">
													<input type="checkbox" name="isUser" checked="checked"/>
												</c:when>
												<c:otherwise>
													<input type="checkbox" name="isUser"/>
												</c:otherwise>
											</c:choose>
										</td>
										<td class="accordion" id="userAccordion">
											<div class="accordion-heading">
												<strong>User</strong>
												<a class="fr show" data-toggle="collapse" data-parent="#userAccordion" href="#user_overview">Learn More</a>
											</div>
											<div id="user_overview" class="accordion-body collapse">
												Main role for managing assignments that are created by the individual user.
												Can follow assignments created by another user. No access to payment permissions unless assigned by administrator.
											</div>
										</td>
									</tr>
									<tr>
										<td class="actions">
											<c:choose>
												<c:when test="${requestScope.isViewOnly and not requestScope.isEmployeeWorker}">
													<input type="checkbox" name="isViewOnly" checked="checked"/>
												</c:when>
												<c:otherwise>
													<input type="checkbox" name="isViewOnly"/>
												</c:otherwise>
											</c:choose>
										</td>
										<td class="accordion" id="viewonlyAccordion">
											<div class="accordion-heading">
												<strong>View Only</strong>
												<a class="fr show" data-toggle="collapse" data-parent="#viewonlyAccordion" href="#viewonly_overview">Learn More</a>
											</div>
											<div id="viewonly_overview" class="accordion-body collapse">
												Has access to company reports, but cannot view or edit other company information.
											</div>
										</td>
									</tr>
									<tr>
										<td class="actions">
											<c:choose>
												<c:when test="${requestScope.isStaff and not requestScope.isEmployeeWorker}">
													<input type="checkbox" name="isStaff" checked="checked"/>
												</c:when>
												<c:otherwise>
													<input type="checkbox" name="isStaff"/>
												</c:otherwise>
											</c:choose>
										</td>
										<td class="accordion" id="staffAccordion">
											<div class="accordion-heading">
												<strong>Staff</strong>
												<a class="fr show" data-toggle="collapse" data-parent="#staffAccordion" href="#staff_overview">Learn More</a>
											</div>
											<div id="staff_overview" class="accordion-body collapse">
												This role can only view assignment related content.  No access to payment center.
												To create a user that you want to give limited permissions on the site but send assignments, use staff and enable to receive internal assignments.
											</div>
										</td>
									</tr>
									<tr>
										<td class="actions">
											<c:choose>
												<c:when test="${requestScope.isDeputy and not requestScope.isEmployeeWorker}">
													<input type="checkbox" name="isDeputy" checked="checked"/>
												</c:when>
												<c:otherwise>
													<input type="checkbox" name="isDeputy"/>
												</c:otherwise>
											</c:choose>
										</td>
										<td class="accordion" id="deputyAccordion">
											<div class="accordion-heading">
												<strong>Deputy</strong>
												<a class="fr show" data-toggle="collapse" data-parent="#deputyAccordion" href="#deputy_overview">Learn More</a>
											</div>
											<div id="deputy_overview" class="accordion-body collapse">
												This role gives the ability to take actions on behalf of workers via WM Realtime and Assignment Detail pages
											</div>
										</td>
									</tr>
									<tr>
										<td class="actions">
											<c:choose>
												<c:when test="${requestScope.isDispatcher and not requestScope.isEmployeeWorker}">
													<input type="checkbox" name="isDispatcher" checked="checked"/>
												</c:when>
												<c:otherwise>
													<input type="checkbox" name="isDispatcher"/>
												</c:otherwise>
											</c:choose>
										</td>
										<td class="accordion" id="dispatcherAccordion">
											<div class="accordion-heading">
												<strong>Team Agent</strong>
												<small>(Vendor Only)</small>
												<a class="fr show" data-toggle="collapse" data-parent="#dispatcherAccordion" href="#dispatcher_overview">Learn More</a>
											</div>
											<div id="dispatcher_overview" class="accordion-body collapse">
												Team Agents have the ability to accept, apply, and to take other actions on behalf of team members.
												Use the Team Agent role to manage a team of workers more efficiently.
											</div>
										</td>
									</tr>
									<c:if test="${workerRole}">
										<tr id="employee-worker">
											<td class="actions">
												<c:choose>
													<c:when test="${requestScope.isEmployeeWorker}">
														<input type="checkbox" name="isEmployeeWorker" checked="checked"/>
													</c:when>
													<c:otherwise>
														<input type="checkbox" name="isEmployeeWorker"/>
													</c:otherwise>
												</c:choose>
											</td>
											<td class="accordion" id="employeeWorkerAccordion">
												<div class="accordion-heading">
													<strong>Employee Worker</strong>
													<a class="fr show" data-toggle="collapse" data-parent="#employeeWorkerAccordion" href="#employeeWorker_overview">Learn More</a>
												</div>
												<div id="employeeWorker_overview" class="accordion-body collapse">
													Employee worker can ONLY perform work, other permissions are limited.
												</div>
											</td>
										</tr>
									</c:if>
								</tbody>
							</table>
						</div>
					</div>

				</fieldset>

				<fieldset>
					<div id="spendlimit_container">

						<div class="control-group">
							<label path="spendLimit" class="control-label">Max Authorized Budget</label>

							<div class="controls">
								<div class="input-prepend">
									<span class="add-on">$</span><form:input path="spendLimit" id="spendLimit" maxlength="255" cssClass="span2 tar"/>
								</div>
								<span class="help-block">Set the max value per assignment that this user can spend.</span>
							</div>
						</div>
					</div>

					<div class="control-group">
						<label class="control-label">Payment and Funds Access</label>
						<div class="controls toggleEmployeeWorker">
							<label>
								<input id="payment-access-check" type="checkbox" name="hasPaymentAccess"
										<c:out value="${hasPaymentCenterAndEmailAccess? 'checked=\"checked\"' : ''}"/>/>
								Access Payment Center and payment related emails <span class="tooltipped tooltipped-n" aria-label="Give user the ability to access the payment center, pay assignments, and configure their payment related emails">
								<i class="wm-icon-question-filled"></i>
							</span>
							</label>
							<label>
								<input id="funds-access-check" type="checkbox" name="hasFundsAccess" 
										<c:out value="${hasManageBankAndFundsAccess? 'checked=\"checked\"' : ''}"/> />
								Manage bank accounts, add and withdraw funds <span class="tooltipped tooltipped-n" aria-label="Give user the ability to add and remove bank accounts and to add and withdraw funds on behalf of the account.">
								<i class="wm-icon-question-filled"></i>
							</span>
							</label>
							<label>
								<input id="counteroffer-access-check" type="checkbox" name="hasCounterOfferAccess" 
										<c:out value="${hasCounterOfferAccess? 'checked=\"checked\"' : ''}"/> />
								Authorized to approve or decline counteroffers/applications in sent status
							</label>
							<label>
								<input id="edit-pricing-access-check" type="checkbox" name="hasEditPricingAccess" 
										<c:out value="${hasEditPricingAccess? 'checked=\"checked\"' : ''}"/> />
								Authorized to approve or decline budget changes, expense reimbursements and bonus requests. Includes ability to edit pricing for in progress work.
							</label>
							<label>
								<input id="work-approve-access-check" type="checkbox" name="hasWorkApproveAccess" 
										<c:out value="${hasWorkApproveAccess? 'checked=\"checked\"' : ''}"/> />
								Authorized to approve assignments for payment (or send back for rework)</br>
								<small class="meta"><i class="icon-info-sign"></i> By default, all employees have the ability to send work back if not satisfactory.
									Enable this feature to allow this employee to approve assignments for payment.</small>
							</label>
						</div>
					</div>

					<div class="control-group">
						<label class="control-label" for="workStatus">Work Status</label>
						<div class="controls">
							<select id="workStatus" name="workStatus">
								<option value="UNAVAILABLE"<c:out value="${workStatus eq 'UNAVAILABLE' ? ' selected' : ''}"/>>
									Not available for work
								</option>
								<option value="UNLISTED"<c:out value="${workStatus eq 'UNLISTED' ? ' selected' : ''}"/>>
									Available, but not publicly listed in search
								</option>
								<option value="PUBLIC"<c:out value="${workStatus eq 'PUBLIC' ? ' selected' : ''}"/>>
									Publicly available in search
								</option>
							</select>
						</div>
					</div>

					<sec:authorize access="hasFeature('projectPermission')">
						<div class="control-group">
							<label class="control-label">Projects</label>

							<div class="controls">
								<label>
									<input id="project-permission-check" type="checkbox" name="hasProjectAccess"
											<c:out value="${hasProjectAccess ? 'checked=\"checked\"' : ''}"/> />
									Authorize to create and modify projects
								</label>
							</div>
						</div>
					</sec:authorize>

				</fieldset>


				<div id="help-notice" class="wm-action-container">
					<c:if test="${not empty user.userNumber}">
						<c:choose>
							<c:when test="${user.userStatusType.code ne 'deactivate'}">
								<a class="button" id="deactivate-user-outlet" href="/users/reassign/${user.userNumber}">Deactivate User</a>
							</c:when>
							<c:otherwise>
								<a class="button" id="reactivate-user-outlet"><i class="wm-icon-user"></i> Reactivate User</a>
							</c:otherwise>
						</c:choose>
					</c:if>
					<a class="button" href="/users">Cancel</a>
					<c:choose>
						<c:when test="${not empty user.userNumber}">
							<a id="add-user-outlet" class="button"><i class="wm-icon-user"></i> Update User</a>
						</c:when>
						<c:otherwise>
							<a id="add-user-outlet" class="button"><i class="wm-icon-user"></i> Add User</a>
						</c:otherwise>
					</c:choose>
				</div>

			</div>
		</div>
		</form:form>
	</div>

	<c:if test="${not empty user.userNumber}">
		<div class="clearfix">
			<form action="/users/reactivate" method="post" id="deactivate-form">
				<wm-csrf:csrfToken />
				<div class="input">
					<input type="hidden" name="id" value="${user.id}"/>
					<input type="hidden" name="status" value="${toggleStatusTo}"/>
				</div>
			</form>
		</div>
	</c:if>
</wm:app>
