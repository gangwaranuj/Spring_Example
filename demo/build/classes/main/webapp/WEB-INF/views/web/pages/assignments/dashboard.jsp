<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="wmfn" uri="http://www.workmarket.com/taglib/functions" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="wm-csrf" uri="http://www.workmarket.com/taglib/csrf" %>
<%@ taglib prefix="wmfmt" uri="http://www.workmarket.com/taglib/fmt" %>
<%@ taglib prefix="wm" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="vr" uri="http://www.workmarket.com/taglib/velvet-rope" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<wm:app pagetitle="${pagetitle}" bodyclass="dashboard" breadcrumbSection="Work" breadcrumbSectionURI="/assignments" breadcrumbPage="${breadcrumbPage}" webpackScript="dashboard">

<c:set var="hidePricing" value="${currentUser.companyHidesPricing or currentUser.employeeWorker}" />
<sec:authorize access="hasAnyRole('ACL_ADMIN', 'ACL_MANAGER', 'ACL_DISPATCHER')">
	<c:set var="hidePricing" value="false" />
</sec:authorize>
<sec:authorize access="hasAnyRole('ACL_ADMIN', 'ACL_MANAGER')">
	<c:set var="isAdmin" value="true" />
</sec:authorize>

	<sec:authorize access="!principal.approveWorkCustomAuth or principal.isMasquerading()">
		<c:set var="isNotAuthorizedForPayment" value="true" />
	</sec:authorize>

	<script>
		var context = ${contextJson};
		var config = {
			name: context.name,
			options: context.features
		};
	</script>

	<c:import url="/WEB-INF/views/web/partials/message.jsp"/>

	<c:import url="/WEB-INF/views/web/partials/general/notices_js.jsp">
		<c:param name="containerId" value="general_assignment_messages"/>
	</c:import>

	<div class="page-header clear">
		<c:if test="${currentUser.seller || currentUser.dispatcher}">
			<h1>My Work</h1>
		</c:if>
		<div class="dashboard-quick-actions">
			<c:if test="${currentUser.seller || currentUser.dispatcher}">
				<a class="button" id="calendar-sync">Sync to Calendar</a>
			</c:if>
			<wm:button icon="expanded" id="show_list" tooltip="Display your assignments in a list view" classlist="map-action-button" />
			<wm:button icon="calendar" id="show_calendar" tooltip="Display your assignments in a calendar view" />
			<a class="button tooltipped tooltipped-n -new" id="show_map" href="/map" data-container=".dashboard-actions" aria-label="Display your assignments in a map view">
				<div class="button--content wm-icon-location"></div>
			</a>
			<c:if test="${currentUser.buyer}">
				<wm:button id="show_creation" classlist="dashboard--new-assignment">New Assignment</wm:button>
				<select class="gear-dropdown action-menu">
					<option value="">- Select -</option>
					<option value="/settings/manage/customfields">Custom Fields</option>
					<option value="/settings/manage/labels">Labels</option>
					<option value="/lms/manage/surveys">Surveys</option>
					<option value="/settings/manage/templates">Templates</option>
				</select>
			</c:if>
		</div>
	</div>


	<div class="dn">
		<wm-csrf:csrfToken />
		<div id="add-cal-sync-tabs">
			<div class="row">
				<div class="span9">
					<c:import url="/WEB-INF/views/web/partials/profile/calendar_sync_modal.jsp"/>
				</div>
			</div>
		</div>
	</div>

	<div id="assignment_container">
		<div id="assignment_statuses_container" class="assignments-sidebar">
			<c:import url="/WEB-INF/views/web/partials/assignments/dashboard/filters.jsp"/>
			<c:import url="/WEB-INF/views/web/partials/assignments/dashboard/status.jsp"/>
		</div>
		<div class="assignments-content">
			<div id="assignment_results_container">
				<sec:authorize access="hasAnyRole('ACL_ADMIN', 'ACL_MANAGER')">
					<div class="dashboard-actions">
						<input type="checkbox" name="select-all" id="select-all" class="select-all-visible-outlet"/>
						<div class="btn-group">
							<a id="assignment_actions_dropdown" data-toggle="dropdown" class="button -new btn dropdown-toggle disabled tooltipped tooltipped-n" data-container=".dashboard-actions" disabled href="javascript:void(0)" aria-label="Select at least one assignment to enable bulk assignment actions">
								<div class="button--content">Actions</div>
							</a>
							<ul class="dropdown-menu">
								<li><a class="assignment_actions" id="add_note_bulk_action" href="">Add Note</a></li>
								<li><a class="assignment_actions" id="add_attachment_bulk_action" href="">Add Attachment</a></li>
								<sec:authorize access="principal.approveWorkCustomAuth and !principal.isMasquerading()">
									<li id="approve_for_payment_bulk_action_wrapper" class="dn">
										<a class="assignment_actions" id="approve_for_payment_bulk_action" href="javascript:void(0);">Approve for Payment</a>
									</li>
								</sec:authorize>
								<sec:authorize access="!principal.approveWorkCustomAuth or principal.isMasquerading()">
									<li class="dn disabled"><a class="disabled" title="You are not authorized to take this action" style="text-decoration:none; color: #ccc;">Approve for Payment</a></li>
								</sec:authorize>
								<li><a class="assignment_actions" id="download_closeout_attachments_bulk_action" href="javascript:void(0);">Download Attachments</a></li>
								<li><a class="assignment_actions" id="remove_attachments_bulk_action" href="javascript:void(0);">Remove Attachments</a></li>
								<li><a class="assignment_actions" id="routing_action" href="javascript:void(0);">Route Assignments</a></li>
								<li><a class="assignment_actions" id="delete_void_bulk_action" href="javascript:void(0);">Delete / Void Assignments</a></li>
								<c:if test="${!reserveFundsEnabledFlag}">
									<li><a class="assignment_actions" id="edit_projects_bulk_action" href="javascript:void(0);">Edit Clients / Projects</a></li>
								</c:if>
								<li><a class="assignment_actions" id="custom_fields_bulk_action" href="javascript:void(0);">Edit Custom Fields</a></li>
								<li><a class="assignment_actions" id="add_label_bulk_action" href="javascript:void(0);" data-tile="Bulk Add Label">Add Label</a></li>
								<li><a class="assignment_actions" id="remove_label_bulk_action" href="javascript:void(0);">Remove Label</a></li>
								<li><a class="assignment_actions" id="create_assignment_bundle" href="javascript:void(0);">Create Bundle (drafts only)</a></li>
								<li><a class="assignment_actions" id="reschedule_bulk_action" href="javascript:void(0);">Reschedule Assignments</a></li>
								<li><a class="assignment_actions" id="reprice_bulk_action" href="javascript:void(0);">Reprice Assignments</a></li>
								<li><a class="assignment_actions" id="cancel_works_bulk_action" href="javascript:void(0);">Cancel Assignments</a></li>
							</ul>
						</div>
					</div>
				</sec:authorize>
				<form:form modelAttribute="form" class="dashboard-sort">
					<strong>Show</strong>
					<form:select method="GET" path="pageSize" id="assignment_list_size" class="span2" items="${form.pageSizes}">
						<c:forEach items="${form.pageSizes}" var="ps">
							<option value="${ps}" <c:out value="${(ps == form.pageSize) ? 'selected=selected' : ''}"/>><c:out value="${ps}"/></option>
						</c:forEach>
					</form:select>
					<strong>Sort by:</strong>
					<select name="assignment_list_sorting" id="assignment_list_sorting" class="span3">
						<option value="modified_date">Updated Date</option>
						<option value="created_date">Created Date</option>
						<option value="scheduled_date">Scheduled Date</option>
						<option value="sent_date">Sent Date</option>
						<option value="completed_date">Completed Date</option>
						<option value="approved_date">Approved Date</option>
						<option value="paid_date">Paid Date</option>
						<option value="state">State</option>
						<option value="title">Title</option>
					</select>
					<div class="btn-group sort">
						<a id="assignment_list_sorting_desc" class="btn toggle_selected"><i class="icon-arrow-down"></i></a>
						<a id="assignment_list_sorting_asc" class="btn"><i class="icon-arrow-up"></i></a>
					</div>
				</form:form>
				<div class="full-select-all-msg alert alert-info dn text-center">All assignments on this page are selected. <a href="#" id="full-select-all">Select all assignments that match this search</a></div>
				<div class="clear-full-select-all-msg alert alert-info dn text-center">All assignments in this search are selected. <a href='#' id="clear-full-select-all">Clear selection</a></div>

				<div id="assignment_list_results" class="results-list assignments-results"></div>

				<div class="assignments-results-meta">
					<small><strong>Updated</strong> <span id="refresh_last_updated"><fmt:formatDate value="${lastUpdated.time}" pattern="MMM dd, yyyy 'at' hh:mm a" timeZone="${currentUser.timeZoneId}" /></span></small>
					<wm:pagination min="1" max="10" />
				</div>
			</div>
			<div id="calendar"></div>
			</div>
		</div>
	</div>

	<div class="dn">
		<div id="followers_container">
			<div class="help-block">Select names of one or more people who should get notifications related to this assignment.</div>
			<br/>
			<div id="followers_list"></div>
			<br/>
			<div>
				<select name="followers" id="followers" data-placeholder="Begin Typing To Add Follower">
					<%--Here for placeholder text--%>
					<option>-</option>
					<c:forEach var="follower" items="${followers}">
						<option value="<c:out value="${follower.key}"/>"><c:out value="${follower.value}" /></option>
					</c:forEach>
				</select>
			</div>
			<div class="wm-action-container">
				<a class="button" data-modal-close>Close</a>
				<a class="button" id="new_followers_save">Save Changes</a>
			</div>
		</div>
	</div>

	<script id="tmpl-assignment_list" type="text/x-jquery-tmpl">
		<div class="assignmentId" id="\${id}" style="visibility: hidden;"/>
			<div class="row work-row">
				{{if is_owners_company && !is_me}}
					<a href="/assignments/toggleFollow/\${id}" class="tooltipped tooltipped-n follow
						{{if is_following}}
							follow-true" aria-label="Unfollow this assignment"
						{{else}}
							" aria-label="Follow this assignment"
						{{/if}}
						>
					</a>
				{{/if}}
			<div class="results-select">
				{{if is_admin}}
					<input type="checkbox" name="work_ids[]" id="results_workids" value="\${id}" />
				{{/if}}
			</div>
			<div style="float: left;">
				<strong>
					<a class="tooltipped tooltipped-n" aria-label="\${title}" href="/assignments/details/\${id}">
						<!-- TODO: Make the included JSP conditional -->
						<span class="title" style="display: inline-block;">
							\${title_short}
						</span>
					</a>
				</strong>
				<br/>
				<small class="meta client">
					{{if client}}
					{{if (is_me || is_admin) && type == 'managing'}}
					<span>Client: \${client}</span><br/>
					{{/if}}
					{{/if}}
					{{if is_resource}}
					<span>\${owner_company_name}</span>
					{{/if}}
				</small>
				{{if parent_id}}
				<div class="bundle_info">
					<small class="meta">
						<span class="label label-bundle tooltipped tooltipped-n" aria-label="Part of a Bundle">B</span>
					</small>
					<small class="meta">
						 <a class="tooltipped tooltipped-n" aria-label="Click to view bundle" href="/assignments/view_bundle/{{= parent_id}}">{{= parent_title }}</a>
					</small>
					<br/><br/>
				</div>
				{{/if}}
			</div>
			<div class="status">
				<small class="meta">
					<strong>\${status}</strong><br/>
					{{if is_applied}}<span class="label">Applied</span>{{/if}}
					{{if is_applications_pending && raw_status == 'sent' && type == 'managing' && (is_me || is_admin)}}<span class="label">Open Offers</span>{{/if}}
					<br/>
				</small>
			</div>
			<div class="contact">
				<small class="meta">
					{{if resource_full_name && ! (resource_id == '<sec:authentication property="principal.id" />')}}
					<a href="/profile/\${resource_user_number}" data-number="\${resource_user_number}" class="open-user-profile-popup">\${resource_full_name}</a>
					<br/>
					{{if resource_mobile_phone}}
					M:<span>\${resource_mobile_phone}</span>
					{{/if}}
					<br/>
					{{if resource_work_phone}}
					W:<span>\${resource_work_phone}</span>
					{{/if}}
					{{/if}}
					&nbsp;
				</small>
			</div>
			<div class="date">
				<small class="meta">
					<span style="display: inline-block">
						{{if recurrenceUUID && is_owners_company}}
						<jsp:include page="/WEB-INF/views/web/partials/svg-icons/workdetails/recurrence.jsp"/>
						{{/if}}
						\${scheduled_date}
					</span> 
				</small><br/>
			</div>
			<div class="location">
				<small class="meta">{{if address}}\${address}{{/if}}</small>&nbsp;
			</div>
			<c:choose>
				<c:when test="${isWorkerCompany}" >
					<c:if test="${!hidePricing}">
						<div class="price">
						<small class="meta">\${price}</small>
						</div>
					</c:if>
				</c:when>
				<c:otherwise>
					<c:if test="${!hidePricing}">
						<div class="price">
						<small class="meta">\${price}</small>
						</div>
					</c:if>
				</c:otherwise>
			</c:choose>
		</div>
		<div class="row">
			<div class="work-details">
				<c:import url="/WEB-INF/views/web/partials/general/notices_js.jsp" />
				{{if (is_me || is_admin) && type == 'managing'}}
				<small class="meta">
					{{if location_name}}
					Location: \${location_name}
					{{/if}}
					{{if location_number}}
					(\${location_number})
					{{/if}}
					{{if location_name || location_number}}</br>{{/if}}
				</small>
				{{/if}}
				{{if typeof candidates !== "undefined" && candidates.length > 0}}
				<small class="meta candidates" title="\${candidates.join(", ")}">
					Candidates: \${candidates.join(", ")}
				</small>
				{{/if}}
				<small class="meta">
					Internal Owner: \${buyer}
				</small>
				<div class="assignment_labels">
					{{if raw_status == 'sent' && is_assign_to_first_resource}}
					<span class="tooltipped tooltipped-n" aria-label="Auto-assign first worker to accept."><span class="label success nowrap">Auto Assign</span></span>
					{{/if}}
					{{if substatuses && ! ((raw_status == 'missed' || raw_status == 'cancelled') && is_resource)}}
					{{if raw_status == 'paymentPending' && auto_pay_enabled && is_admin}}
					<span class="label success nowrap">Auto Pay</span>
					{{/if}}
					{{each(key, value) substatuses}}
					<a href="/assignments#substatus/\${value.workSubStausTypeId}/managing">
						<span class="label nowrap dragRemove \${id}" {{if value.colorRgb}}style="background-color: #\${value.colorRgb};"{{/if}}>\${value.description}</span>
					</a>
					{{/each}}
					{{/if}}
				</div>
				{{if custom_fields}}
				{{each(key, value) custom_fields}}
				<small class="meta">\${key}: \${value}</small><br/>
				{{/each}}
				{{/if}}
			</div>
		</div>
		{{if raw_status == 'complete'}}
			<div class="payment_info_container dn"></div>
		{{/if}}
		{{if (is_me || is_admin) && type == 'managing'}}
			<small class="meta">
				<ul class="inline-nav assignment-actions clear" data-assignment-number="\${id}">
					<div class="actions-hover dn fl">
						{{if raw_status == 'draft' || raw_status == 'sent'}}
						<li><a id="assignment-actions-edit" href="/assignments/edit/\${id}">Edit</a> <span class="separator">|</span></li>
						{{/if}}
						{{if type == 'managing' && (raw_status == 'draft' || raw_status == 'sent') && !parent_id}}
						<li><a href="/assignments/contact/\${id}" class="js-dashboard-invite-more-workers">Invite More Workers</a>  <span class="separator">|</span></li>
						{{/if}}
						<li><a class="add_note_action">Add Note</a> <span class="separator">|</span></li>
						<li><a class="view_line_item_modal" href="/assignments/get_assignment_notes/\${id}" data-title="Notes History">View Notes</a> <span class="separator">|</span></li>
						<li><a class="view_line_item_modal" href="/assignments/get_assignment_history/\${id}" data-title="Assignment History">View History</a> <span class="separator">|</span></li>
						<li><a id="assignment-actions-copy" href="/assignments/copy/\${id}">Copy</a></li>
						{{if is_owners_company }}
						<li><span class="separator">|</span> <a class="add_followers_action">Add Followers</a></li>
						{{/if}}
						{{if raw_status == 'complete' && can_approve}}
						<li><span class="separator">|</span> <a class="show_payment_details_action">View Payment</a></li>
						<li><a class="payment_button_hide_action dn">Hide Payment</a></li>
						{{/if}}
					</div>
					<li class="fr" style="font-size:10px">
						<em>
							{{if last_modified_on}}
							Updated \${last_modified_on} ago, \${modifier_first_name}. \${modifier_last_name} &nbsp; | &nbsp;
							{{/if}}
							Assign. ID: \${id}
						</em>
					</li>
				</ul>
			</small>
		{{else}}
			<small class="meta">
				<ul class="inline-nav assignment-actions clear" data-assignment-number="\${id}">
					<div class="actions-hover fl">
						{{if raw_status == 'sent' && !parent_id}}
						{{if is_assign_to_first_resource}}
						<li><a href="/assignments/details/\${id}">Accept</a></li>
						{{else}}
						<li><a href="/assignments/details/\${id}">Apply</a></li>
						{{/if}}
						{{/if}}
					</div>
					<li class="fr" style="font-size:10px">
						<em>
							Assign. ID: \${id}
						</em>
					</li>
				</ul>
			</small>
		{{/if}}
	</script>
</wm:app>
