<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="wmfmt" uri="http://www.workmarket.com/taglib/fmt" %>
<%@ taglib prefix="wmfn" uri="http://www.workmarket.com/taglib/functions" %>
<%@ taglib prefix="wm-csrf" uri="http://www.workmarket.com/taglib/csrf" %>
<%@ taglib prefix="wm" tagdir="/WEB-INF/tags" %>

<wm:admin pagetitle="Admin Profile" bodyclass="manage-profile" webpackScript="admin">
	<script>
		var config = {
			mode: 'profile',
			profileId: '${wmfmt:escapeJavaScript(id)}'
		};
	</script>

	<!-- internal CSR profile management page -->
	<div class="sidebar admin">
		<div class="well">
			<div class="page-header">
				<h5>Profile Actions</h5>
			</div>
			<ul class="unstyled">
				<li>
					<a href="<c:url value="/admin/usermanagement/masquerade/start?user=${wmfn:urlEncode(email, 'utf-8')}&user_fullname=${wmfn:urlEncode(user.fullName, 'utf-8')}"/>">Masquerade as this user</a>
				</li>
				<li><a id="change_company_relation">Change Company</a></li>
				<li><a id="reset-password-action" href="/admin/manage/profiles/reset_password/${id}">Reset Password</a></li>
				<li><a id="confirm-account-action" href="/admin/manage/profiles/confirm_account/${id}">Confirm Account</a></li>
				<li><a id="send_message">Send Message</a></li>
				<li><a href="/admin/usermanagement/reindex_user/${user.userNumber}">Reindex User</a></li>
				<c:choose>
					<c:when test="${suspended}">
						<li><a href="<c:url value="/admin/manage/profiles/unsuspend/${id}"/>" onclick="return confirm('Are you sure you want to unsuspend this user?');">Unsuspend</a></li>
					</c:when>
					<c:otherwise>
						<li>
							<form id="suspend_form" method="post" action="/admin/manage/profiles/suspend/${id}">
								<wm-csrf:csrfToken />
								<a id="suspend_profile_link">Suspend</a>
							</form>
						</li>
					</c:otherwise>
				</c:choose>
				<c:if test="${user_status_type.code eq 'pending'}">
					<li><a class="approve-listing">Approve Search Listing</a></li>
					<li><a class="decline-listing">Decline Search Listing</a></li>

				</c:if>
			</ul>
		</div>
		<jsp:include page="/WEB-INF/views/web/partials/admin/quick_links.jsp"/>
	</div>

	<div class="content">
		<div class="row">
			<div class="span12">
				<jsp:include page="/WEB-INF/views/web/partials/general/notices_js.jsp">
					<jsp:param name="containerId" value="dynamic_messages"/>
				</jsp:include>

				<jsp:include page="/WEB-INF/views/web/partials/message.jsp"/>

				<c:if test="${suspended}">
					<div class="alert-message error">
						This user is currently suspended. <a class="button -small" href="<c:url value="/admin/manage/profiles/unsuspend/${id}"/>">Unsuspend</a>
					</div>
				</c:if>
			</div>
		</div>
		<div class="row">
			<div class="span10">
				<div class="row">
					<c:choose>
						<c:when test="${not empty avatar_small}">
							<wm:avatar src="${wmfmt:stripXSS(avatar_small)}" />
						</c:when>
						<c:otherwise>
							<wm:avatar hash="${user.userNumber}" />
						</c:otherwise>
					</c:choose>
					<div class="span8">
						<h2 id="user_fullname"><c:out value="${user.firstName}"/> <c:out value="${user.lastName}"/>
							<small> (<a id="edit_name_link">Edit name</a>)</small>
						</h2>
						<h4>
							<c:if test="${not empty profile.jobTitle}">
								<c:out value="${profile.jobTitle}"/> |
							</c:if>
							<a href="/admin/manage/company/overview/${company.id}"><c:out value="${company.name}"/></a>
						</h4>

						<p>User ID:
							<strong><c:out value="${user.userNumber}"/></strong>
						</p>

						<ul class="unstyled">
							<c:if test="${not empty full_address}">
								<li><c:out value="${full_address}"/></li>
							</c:if>
							<c:if test="${is_owner || lane_association || is_wm_admin}">
								<c:if test="${not empty profile.workPhone}">
									<li>Phone: +<c:out value="${profile.workPhoneInternationalCode.callingCodeId}"/> ${wmfmt:phone(profile.workPhone)}
										<c:if test="${not empty(profile.workPhoneExtension)}">
											x <c:out value="${profile.workPhoneExtension}"/>
										</c:if></li>
								</c:if>
								<li>
									Email: <a href="mailto:<c:out value="${user.email}"/>"><c:out value="${user.email}"/></a>
								</li>
							</c:if>
						</ul>
						<a class="button" href="<c:url value="/profile/${user.userNumber}"/>">
							Full Profile &raquo;
						</a>
					</div>
				</div>
				<div class="row">
					<div class="span5">
						<hr/>
						<div>
							<c:choose>
								<c:when test="${empty(number_ratings)}">
									<strong>Rating:</strong> ${wmfn:ratingStars(100)}
								</c:when>
								<c:otherwise>
									<strong>Rating:</strong> ${wmfn:ratingStars(rating)}
								</c:otherwise>
							</c:choose>
							${wmfn:convertRatingNumberToNumeric(rating)} out of <c:out value="${number_ratings}"/> ratings
						</div>
						<hr/>
					</div>
					<div class="span4">
						<hr/>
						<p>
							<strong>Screening:</strong>
							<c:set var="roleInternal" scope="page">
								<sec:authorize access="hasRole('ROLE_INTERNAL')">true</sec:authorize>
							</c:set>
							<c:choose>
								<c:when test="${pageScope.roleInternal eq 'true'}">
									<c:if test="${not empty(drug_test.status)}">
										<span class="label warning">DT <c:out value="${drug_test.status}" />
											(<fmt:formatDate value="${drug_test.vendorResponseDate.toGregorianCalendar().time}" pattern="MM/dd/yyyy" timeZone="${timeZoneId}"/>)
										</span>
									</c:if>
									<c:if test="${not empty(background_check.status)}">
										<span class="label warning">BC <c:out value="${background_check.status}"/>
											(<fmt:formatDate value="${background_check.vendorResponseDate.toGregorianCalendar().time}" pattern="MM/dd/yyyy" timeZone="${timeZoneId}"/>)
										</span>
									</c:if>
								</c:when>
								<c:otherwise>
									<c:if test="${drug_test.status.code() eq 'passed'}">
										<span class="label success">DT Yes</span>
									</c:if>
									<c:if test="${background_check.status.code() eq 'passed'}">
										<span class="label success">BC Yes</span>
									</c:if>
								</c:otherwise>
							</c:choose>
						</p>

						<c:if test="${not empty linkedin and not empty linkedin.linkedin_id}">
							<span class="label notice">Linked In Verified</span>
						</c:if>
					</div>
				</div>
			</div>
			<!-- /span9 -->
			<div class="span3 well">
				<h5>Industries</h5>
				<ul class="unstyled">
					<c:choose>
						<c:when test="${not empty industries}">
							<li><c:out value='${wmfn:joinPropertyHuman(industries, "name", ", ", "and")}' /></li>
						</c:when>
						<c:otherwise>
							<li>None</li>
						</c:otherwise>
					</c:choose>
				</ul>
				<c:if test="${is_owner}">
					<a class="show-hide" href="<c:url value="/profile-edit/skills"/>">(Edit)</a>
				</c:if>

				<h5>Skills</h5>
				<ul class="unstyled">
					<c:choose>
						<c:when test="${not empty skills}">
							<li><c:out value='${wmfn:joinPropertyHuman(skills, "name", ", ", "and")}' /></li>
						</c:when>
						<c:otherwise>
							<li>None</li>
						</c:otherwise>
					</c:choose>
				</ul>

				<h5>Certifications</h5>
				<ul class="unstyled">
					<c:choose>
						<c:when test="${not empty certifications}">
							<c:forEach var="item" items="${certifications}" varStatus="status">
								<li>
									<c:out value="${item.certification.name}"/>
									<c:if test="${item.verified}"> (WM Verified)</c:if>
									<c:if test="${not status.last}">, </c:if>
								</li>
							</c:forEach>
						</c:when>
						<c:otherwise>
							<li>None</li>
						</c:otherwise>
					</c:choose>
				</ul>

				<h5>Licenses</h5>
				<ul class="unstyled">
					<c:choose>
						<c:when test="${not empty licenses}">
							<c:forEach var="item" items="${licenses}" varStatus="status">
								<li>
									<c:out value="${item.license.name}"/> - <c:out value="${item.license.state}"/>
									<c:if test="${item.verified}"> (WM Verified) </c:if>
									<c:if test="${not status.last}">, </c:if>
								</li>
							</c:forEach>
						</c:when>
						<c:otherwise>
							<li>None</li>
						</c:otherwise>
					</c:choose>
				</ul>

				<h5>Tags</h5>
				<ul class="unstyled">
					<c:choose>
						<c:when test="${not empty tags}">
							<li><c:out value="${wmfn:joinHuman(tags, ', ', 'name')}" /></li>
						</c:when>
						<c:otherwise>
							<li>None</li>
						</c:otherwise>
					</c:choose>
				</ul>

				<c:if test="${is_owner}">
					<a href="<c:url value="/profile-edit/skills"/>" class="show-hide">(Edit)</a>
				</c:if>

				<c:if test="${not is_owner}">
					<h5>Private Tags</h5>
					<ul class="unstyled">
						<c:choose>
							<c:when test="${not empty private_tags}">
								<li><c:out value="${wmfn:joinHuman(private_tags, ', ', 'name')}" /></li>
							</c:when>
							<c:otherwise>
								<li>None</li>
							</c:otherwise>
						</c:choose>

						<c:if test="${allow_tagging}">
							<li><a href="javascript:void(0);" id="edit_company_tags" class="show-hide">(Edit)</a></li>
						</c:if>
					</ul>
				</c:if>
			</div> <!-- /span6 -->
		</div> <!-- /row -->

	<div class="row">
		<div class="span12">
			<div id="pending-modifications-approval">
					<c:if test="${not empty profile_modifications}">
					<h4>Profile changes needing approval</h4>
						<form method="post" action="/admin/manage/profiles/approve_modifications" accept-charset="utf-8">
							<wm-csrf:csrfToken />
							<input type="hidden" name="id" value="${id}"/>
							<table class="table">
								<thead>
									<tr>
										<th>&nbsp;</th>
										<th>New</th>
										<th>Old</th>
										<th class="tac">Approve</th>
										<th class="tac">Deny</th>
									</tr>
								</thead>
								<tbody>
									<c:if test="${not empty profile_modifications['userName']}">
										<tr>
											<td>Name</td>
											<td>
												<input type="text" name="first_name" value="${not empty param['first_name'] ? param['first_name'] : first_name}"/>
												<input type="text" name="last_name" value="${not empty param['last_name'] ? param['last_name'] : last_name}"/>
											</td>
											<td><c:out value="${wmfn:defaultString(first_name_old, 'None')}" /> <c:out value="${wmfn:defaultString(last_name_old, 'None')}" /></td>
											<td class="tac"><input type="checkbox" name="approve[userName]" value="1"/></td>
											<td class="tac"><input type="checkbox" name="deny[userName]" value="1"/></td>
										</tr>
									</c:if>
									<c:if test="${not empty profile_modifications['userPhoto']}">
										<tr>
											<td>User Avatar</td>
											<td>
												<c:choose>
													<c:when test="${avatar_small}">
														<img src="<c:out value="${wmfmt:stripXSS(avatar_small)}" />" class="frame"/>
													</c:when>
													<c:otherwise>None</c:otherwise>
												</c:choose>
											</td>
											<td>
												<c:choose>
													<c:when test="${avatar_small_old}">
														<img src="<c:out value="${wmfmt:stripXSS(avatar_small_old)}" />" class="frame"/>
													</c:when>
													<c:otherwise>None</c:otherwise>
												</c:choose>
											</td>
											<td class="tac"><input type="checkbox" name="approve[userPhoto]" value="1"/></td>
											<td class="tac"><input type="checkbox" name="deny[userPhoto]" value="1"/></td>
										</tr>
									</c:if>
									<c:if test="${not empty profile_modifications['companyPhoto']}">
										<tr class="top">
											<td>Company Avatar</td>
											<td>
												<c:choose>
													<c:when test="${not empty(company_avatar_small)}">
														<img src="<c:out value="${wmfmt:stripXSS(company_avatar_small)}"/>" class="frame"/>
													</c:when>
													<c:otherwise>None</c:otherwise>
												</c:choose>
											</td>
											<td>
												<c:choose>
													<c:when test="${not empty(company_avatar_small_old)}">
														<img src="<c:out value="${wmfmt:stripXSS(company_avatar_small_old)}"/>" class="frame"/>
													</c:when>
													<c:otherwise>None</c:otherwise>
												</c:choose>
											</td>
											<td class="tac"><input type="checkbox" name="approve[companyPhoto]" value="1"/></td>
											<td class="tac"><input type="checkbox" name="deny[companyPhoto]" value="1"/></td>
										</tr>
									</c:if>
									<c:if test="${not empty profile_modifications['workNumber']}">
										<tr>
											<td>Work Phone</td>
											<td>
												<input type="tel" id="work_phone" name="workNumber" alt="phone-us" value="${work_phone}"/>
											</td>
											<td>
												<c:choose>
													<c:when test="${not empty(old_work_phone)}">
														${wmfmt:phone(old_work_phone)}
													</c:when>
													<c:otherwise>
														None
													</c:otherwise>
												</c:choose>
											</td>
											<td class="tac"><input type="checkbox" name="approve[workNumber]" value="1"/></td>
											<td class="tac"><input type="checkbox" name="deny[workNumber]" value="1"/></td>
										</tr>
									</c:if>
									<c:if test="${not empty profile_modifications['mobileNumber']}">
										<tr>
											<td>Mobile Phone</td>
											<td>
												<input type="tel" id="mobile_phone" name="mobileNumber" alt="phone-us" value="${mobile_phone}"/>
											</td>
											<td>
												<c:choose>
													<c:when test="${not empty(old_mobile_phone)}">
														${wmfmt:phone(old_mobile_phone)}
													</c:when>
													<c:otherwise>
														None
													</c:otherwise>
												</c:choose>
											</td>
											<td class="tac"><input type="checkbox" name="approve[mobileNumber]" value="1"/></td>
											<td class="tac"><input type="checkbox" name="deny[mobileNumber]" value="1"/></td>
										</tr>
									</c:if>
									<c:if test="${not empty profile_modifications['smsNumber']}">
										<tr>
											<td>SMS Phone</td>
											<td>
												<input type="tel" id="sms_phone" name="smsNumber" alt="phone-us" value="${not empty(param['smsNumber']) ? param['smsNumber'] : profile_modifications['sms_phone']}"/>
											</td>
											<td>
												<c:choose>
													<c:when test="${not empty(old_sms_phone)}">
														${wmfmt:phone(old_sms_phone)}
													</c:when>
													<c:otherwise>
														None
													</c:otherwise>
												</c:choose>
											</td>
											<td class="tac"><input type="checkbox" name="approve[smsNumber]" value="1"/></td>
											<td class="tac"><input type="checkbox" name="deny[smsNumber]" value="1"/></td>
										</tr>
									</c:if>
									<c:if test="${not empty profile_modifications['summary']}">
										<tr class="top">
											<td>Overview</td>
											<td colspan="2">
												<b>New Text:</b>
												<br/>
												<textarea name="summary"><c:out value="${not empty(param['summary']) ? param['summary'] : user_overview}" /></textarea>
												<br/><br/>
												<b>Old Text:</b><br/>
												<c:choose>
													<c:when test="${not empty(user_overview_old)}">
														<c:out value="${user_overview_old}"/>
													</c:when>
													<c:otherwise>
														None
													</c:otherwise>
												</c:choose>
											</td>
											<td class="tac"><input type="checkbox" name="approve[summary]" value="1"/></td>
											<td class="tac"><input type="checkbox" name="deny[summary]" value="1"/></td>
										</tr>
									</c:if>
									<c:if test="${not empty profile_modifications['companyName']}">
										<tr>
											<td>Company Name</td>
											<td>
												<input type="text" name="companyName" value="${not empty(param['companyName']) ? param['companyName'] : profile_modifications['companyName']}"/>
											</td>
											<td>
												<c:choose>
													<c:when test="${not empty company_name_old }">
														<c:out value="${company_name_old}"/>
													</c:when>
													<c:otherwise>
														None
													</c:otherwise>
												</c:choose>
											</td>
											<td class="tac"><input type="checkbox" name="approve[companyName]" value="1"/></td>
											<td class="tac"><input type="checkbox" name="deny[companyName]" value="1"/></td>
										</tr>
									</c:if>
									<c:if test="${not empty profile_modifications['companyOverview']}">
										<tr class="top">
											<td>Company Overview</td>
											<td colspan="2">
												<b>New Text:</b>
												<br/>
												<textarea name="companyOverview"><c:out value="${not empty param['companyOverview']  ? param['companyOverview'] : company_overview}" /></textarea>
												<br/><br/>
												<b>Old Text:</b>
												<br/>
												<c:choose>
													<c:when test="${not empty company_overview_old }">
														<c:out value="${company_overview_old}"/>
													</c:when>
													<c:otherwise>
														None
													</c:otherwise>
												</c:choose>
											</td>
											<td class="tac"><input type="checkbox" name="approve[companyOverview]" value="1"/></td>
											<td class="tac"><input type="checkbox" name="deny[companyOverview]" value="1"/></td>
										</tr>
									</c:if>
									<c:if test="${not empty profile_modifications['companyWebsite']}">
										<tr>
											<td>Website</td>
											<td><input type="text" name="companyWebsite" value="${not empty param['companyWebsite']  ? param['companyWebsite'] : company_website}"/></td>
											<td>
												<c:choose>
													<c:when test="${not empty company_website_old }">
														<c:out value="${company_website_old}"/>
													</c:when>
													<c:otherwise>
														None
													</c:otherwise>
												</c:choose>
											</td>
											<td class="tac"><input type="checkbox" name="approve[companyWebsite]" value="1"/></td>
											<td class="tac"><input type="checkbox" name="deny[companyWebsite]" value="1"/></td>
										</tr>
									</c:if>
									<c:if test="${not empty profile_modifications['newCertification']}">
										<tr>
											<td colspan="5">
												<a href="/admin/certifications/review">Review unverified certifications</a>
											</td>
										</tr>
									</c:if>
									<c:if test="${not empty profile_modifications['resume']}">
										<tr class="top">
											<td>Resumes</td>
											<td>
												<ul id="resume_list">
													<c:forEach var="resume" items="${resumes}">
														<li id="resume_${resume.id}">
															<a href="<c:url value="/asset/${resume.UUID}"/>"><c:out value="${resume.name}"/></a>
														</li>
													</c:forEach>
												</ul>
											</td>
											<td></td>
											<td class="tac"><input type="checkbox" name="approve[resume]" value="1"/></td>
											<td class="tac"><input type="checkbox" name="deny[resume]" value="1"/></td>
										</tr>
									</c:if>
									<tr valign="middle">
										<c:choose>
											<c:when test="${suspended}">
												<td colspan="5">
													<div style="padding: 10px; margin-bottom: 15px; border: 1px solid #dddddd; background-color: #ffffcc;">
														This user is currently suspended.
														<a href="<c:url value="/admin/manage/profiles/unsuspend/${id}"/>" class="strong">Unsuspend to enable approval</a>
													</div>
												</td>
											</c:when>
											<c:otherwise>
												<td>&nbsp;</td>
												<td colspan="4">
													<button type="submit" id="step2" class="button">Submit</button>
												</td>
											</c:otherwise>
										</c:choose>
									</tr>
								</table>
							</form>
						</c:if>
				</div>

				<!-- pending-modifications-approval -->

				<div id="rating-information">
					<h4>Flagged Ratings &amp; Reviews Given to User</h4>

					<div id="table_ratings">
						<table id="ratings_list" class="table table-striped">
							<thead>
								<tr>
									<th nowrap="nowrap">Action</th>
									<th nowrap="nowrap">Rating</th>
									<th nowrap="nowrap">Date</th>
									<th nowrap="nowrap">Review</th>
									<th nowrap="nowrap">Assignment</th>
								</tr>
							</thead>
							<tbody>
								<tr>
									<td colspan="4" class="dataTables_empty">Loading data from server</td>
								</tr>
							</tbody>
						</table>
					</div>
					<div class="table_ratings_msg"></div>
				</div>

				<div id="changelog">
					<h4>Change Log</h4>
					<table class="table table-striped">
						<thead>
							<tr>
								<th>Description</th>
								<th>Detail</th>
								<th>Timestamp</th>
							</tr>
						</thead>
						<tbody>
							<c:forEach var="c" items="${changelog}">
								<tr>
									<td>
										<c:out value="${c.description}"/>
									</td>
									<td>
										<c:choose>
											<c:when test="${c.changeLogType == 'UserAclRoleAddedChangeLog'}">
												<c:out value="${c.aclRole.name}"/>
											</c:when>
											<c:when test="${c.changeLogType == 'UserAclRoleRemovedChangeLog'}">
												<c:out value="${c.aclRole.name}"/>
											</c:when>
											<c:when test="${c.changeLogType == 'UserAppliedToGroupChangeLog'}">
												<c:out value="${c.group.company.name}/${c.group.name}"/>
											</c:when>
											<c:when test="${c.changeLogType == 'UserGroupMembershipApprovedChangeLog'}">
												<c:out value="${c.group.company.name}/${c.group.name}"/>
											</c:when>
											<c:when test="${c.changeLogType == 'UserGroupMembershipDeclinedChangeLog'}">
												<c:out value="${c.group.company.name}/${c.group.name}"/>
											</c:when>
											<c:when test="${c.changeLogType == 'UserLeftGroupChangeLog'}">
												<c:out value="${c.group.company.name}/${c.group.name}"/>
											</c:when>
											<c:when test="${c.changeLogType == 'UserLaneAddedChangeLog'}">
												<c:out value="${c.laneType.description} for ${c.company.effectiveName}"/>
											</c:when>
											<c:when test="${c.changeLogType == 'UserLaneRemovedChangeLog'}">
												<c:out value="${c.laneType.description} for ${c.company.effectiveName}"/>
											</c:when>
											<c:when test="${c.changeLogType == 'UserPropertyChangeLog'}">
												<c:out value="${c.propertyName} from ${c.oldValue} to ${c.newValue}"/>
											</c:when>
											<c:when test="${c.changeLogType == 'UserStatusChangeLog'}">
												<c:out value="from ${c.oldStatus} to ${c.newStatus}"/>
											</c:when>
											<c:when test="${c.changeLogType == 'UserBlockedByCompanyChangeLog'}">
												<c:out value="${c.blockingCompany.effectiveName}"/>
											</c:when>
										</c:choose>
									</td>
									<td>
										<fmt:formatDate value="${c.createdOn.time}" pattern="E, MMM d yyyy @ hh:mma"/>
									</td>
								</tr>
							</c:forEach>
						</tbody>
					</table>
				</div>
			</div>
		</div>
	</div>


	<div class="dn">
		<div id="popup_change_relation">
			<div id="change_company_help">Enter a current or new company name. To manage roles, go to the employee profile screens.</div>

			<form id="form_change_relation"  method="post" action="/admin/manage/profiles/change_company_relation">
				<wm-csrf:csrfToken />
				<input type="hidden" name="user_id" value="${id}"/>

				<fieldset>
					<div>
						<label for="company_id">Company Name</label>
						<div class="input">
							<select id="company_id" name="company_id">
								<option value="${company.id}" selected>${company.effectiveName}: ${company.id}</option>
							</select>
						</div>
					</div>

					<div>
						<input type="hidden" name="roles" value="${roles}"/>
						<label for="select_roles">Assign Roles</label>
						<div class="input">
							<c:set var="roles" scope="page" value="${not empty(param['roles']) ? param['roles'] : company_roles}" />
							<select id="select_roles" name="roles[]" multiple="multiple">
								<c:forEach var="role" items="${roles}">
									<option value="${role.key}"><c:out value="${role.value}" /></option>
								</c:forEach>
							</select>
						</div>
					</div>
				</fieldset>

				<div class="wm-action-container">
					<button class="button">Submit</button>
				</div>
			</form>
		</div>

		<div id="popup_edit_name">
			<div class="alert-message dn"></div>

			<form method="post" id="form_edit_name" action="/admin/manage/profiles/update_user_fullname" accept-charset="utf-8">
				<wm-csrf:csrfToken />
				<input type="hidden" name="id" value="${id}"/>
				<fieldset>
					<div class="clearfix">
						<label for="first_name">First Name</label>

						<div class="input">
							<input type="text" id="first_name" name="first_name" value="<c:out value="${user.firstName}" />"/>
						</div>
					</div>

					<div class="clearfix">
						<label for="last_name">Last Name</label>

						<div class="input">
							<input type="text" id="last_name" name="last_name" value="<c:out value="${user.lastName}" />"/>
						</div>
					</div>
				</fieldset>

				<div class="wm-action-container">
					<button class="button">Submit</button>
				</div>

			</form>
		</div>

		<div class="dn">
			<c:import url="/WEB-INF/views/web/partials/profile/completeness-lane3-message-form.jsp">
				<c:param name="returnTo" value="/admin/manage/profiles/index/${user.userNumber}"/>
			</c:import>
		</div>
	</div>
</wm:admin>
