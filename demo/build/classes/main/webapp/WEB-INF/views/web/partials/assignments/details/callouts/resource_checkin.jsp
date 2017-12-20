<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="wmfmt" uri="http://www.workmarket.com/taglib/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<c:set var="showCheckInHelper" value="${!(work.activeResource.checkedOut || (!work.configuration.checkinRequiredFlag && !work.checkinCallRequired)) and (fn:length(work.activeResource.timeTrackingLog) > 1) }" />

<div class="media completion">
	<div class="completion-icon">
		<jsp:include page="/WEB-INF/views/web/partials/svg-icons/workdetails/checkin-icon.jsp"/>
	</div>
		<div class="media-body">
		<h4>
			Check In and Check Out
			<c:if test="${(work.configuration.checkinRequiredFlag || work.checkinCallRequired) && work.status.code == workStatusTypes['ACTIVE']}">
				<span class="incomplete" id="checkout_required">
					<small class="meta"><span class="label label-important">Required</small>
				</span>
			</c:if>

			<div class="fr dn completed" id="checkout_completed">
				<div class="checkin-checkout-complete">
					<jsp:include page="/WEB-INF/views/web/partials/svg-icons/workdetails/complete-icon.jsp"/>
				</div>
				<small class="meta">completed</small>
			</div>

		</h4>

		<div>
			<c:choose>
				<c:when test="${work.checkinCallRequired}">
					<c:choose>
						<c:when test="${!work.configuration.checkinRequiredFlag}">
							<c:choose>
								<c:when test="${is_active_resource}">
									<p>
										<strong>
											This is a phone-based check in assignment. For this assignment, please contact
											<c:out value="${work.checkinContactName}"/> at
											<c:out value="${work.checkinContactPhone}"/> when checking in and out.
										</strong>
									</p>
								</c:when>
								<c:otherwise>
									<p>This is a phone-based check in assignment. Check in and check out for your worker.</p>
									<div id="checkin-todo">
										<button class="button resource-checkin-toggle pull-right completion_button">Check In and Check Out</button>
									</div>
								</c:otherwise>
							</c:choose>
						</c:when>
						<c:otherwise>
							<c:choose>
								<c:when test="${is_active_resource}">
									<p>
										<strong>
											For this assignment, please contact
											<c:out value="${work.checkinContactName}"/> at
											<c:out value="${work.checkinContactPhone}"/> when checking in and out.
										</strong>
									</p>
								</c:when>
								<c:otherwise>
									<p>Check in and check out for your worker.</p>
								</c:otherwise>
							</c:choose>
							<c:if test="${work.status.code == workStatusTypes['ACTIVE'] or work.status.code == workStatusTypes['COMPLETE']}">
								<div id="checkin-todo">
									<a class="button resource-checkin-toggle completion_button pull-right">Check In and Check Out</a>
								</div>
							</c:if>
						</c:otherwise>
					</c:choose>
				</c:when>
				<c:otherwise>
					<c:choose>
						<c:when test="${is_active_resource && showCheckInHelper}">
							<div class="alert">
								Please <c:if test="${!work.activeResource.checkedIn}">check in and</c:if> check out again in order to complete the assignment for approval.
							</div>
						</c:when>
						<c:when test="${is_active_resource && !showCheckInHelper}">
							<p>
								You should only check in when you are starting work or updating your check in information. Checking in
								and out will timestamp your activity on this assignment.
							</p>
						</c:when>
						<c:otherwise>
							<p>Check in and check out for your worker.</p>
						</c:otherwise>
					</c:choose>
					<c:if test="${work.status.code == workStatusTypes['ACTIVE'] or work.status.code == workStatusTypes['COMPLETE']}">
						<div id="checkin-todo">
							<a class="button resource-checkin-toggle completion_button pull-right">Check In and Check Out</a>
						</div>
					</c:if>
				</c:otherwise>
			</c:choose>

			<div id="checkin" class="dn">
				<div id="timetracking">
					<div class="messages"></div>
					<table>
						<thead>
							<tr>
								<th>Checked In</th>
								<th>Checked Out</th>
								<c:if test="${work.status.code == workStatusTypes['ACTIVE'] or work.status.code == workStatusTypes['COMPLETE']}">
									<th>Delete</th>
								</c:if>
							</tr>
						</thead>
						<tbody id="timetracking-entries"></tbody>
					</table>
					<c:if test="${work.status.code == workStatusTypes['ACTIVE'] or work.status.code == workStatusTypes['COMPLETE']}">
						<a class="button add_action pull-right completion_button">Check In Again</a>
					</c:if>

					<div class="dn">
						<div id="add_checkout_note_container"></div>
					</div>

					<c:choose>
						<c:when test="${work.status.code == workStatusTypes['ACTIVE'] or work.status.code == workStatusTypes['COMPLETE']}">
							<script id="tmpl-time-tracking-entry" type="text/x-jquery-tmpl">
								<td class="checkin-details"></td>
								<td class="checkout-details"></td>
								<td class="row-delete"><i class="wm-icon-trash icon-gray"></i></td>
							</script>
						</c:when>
						<c:otherwise>
							<script id="tmpl-time-tracking-entry" type="text/x-jquery-tmpl">
								<td class="checkin-details"></td>
								<td class="checkout-details"></td>
							</script>
						</c:otherwise>
					</c:choose>

					<c:if test="${not empty work.activeResource.timeTrackingDuration}">
						<p class="ml">
							<strong>Estimated Time Spent:</strong>
							<span><c:out value="${wmfmt:getDurationBreakdown(work.activeResource.timeTrackingDuration)}"/></span>
						</p>
					</c:if>
					<div class="clearfix"></div>

					<c:choose>
						<c:when test="${work.status.code == workStatusTypes['ACTIVE'] or work.status.code == workStatusTypes['COMPLETE']}">
							<script id="tmpl-time-tracking-entry" type="text/x-jquery-tmpl">
								<table>
									<tbody>
									<tr>
										<td class="checkin-details"></td>
										<td class="checkout-details"></td>
										<td class="row-delete"><i class="delete wm-icon-trash icon-gray"></i></td>
									</tr>
									</tbody>
								</table>
							</script>
						</c:when>
						<c:otherwise>
							<script id="tmpl-time-tracking-entry" type="text/x-jquery-tmpl">
								<table>
									<tbody>
									<tr>
										<td class="checkin-details"></td>
										<td class="checkout-details"></td>
									</tr>
									</tbody>
								</table>
							</script>
						</c:otherwise>
					</c:choose>

					<script id="tmpl-time-tracking-entry-checkin" type="text/x-jquery-tmpl">
						<c:choose>
							<c:when test="${!work.configuration.checkinRequiredFlag && work.checkinCallRequired && is_active_resource}">
							</c:when>
							<c:otherwise>
								<a class="checkin_action button">Check In</a>
							</c:otherwise>
						</c:choose>
					</script>

					<script id="tmpl-time-tracking-entry-checkin-status" type="text/x-jquery-tmpl">
						\${date} at \${time} <c:out value="${assignment_tz}"/>
						<c:choose>
							<c:when test="${is_active_resource}">
								<c:choose>
									<c:when test="${!work.configuration.checkinRequiredFlag && work.checkinCallRequired}">
									</c:when>
									<c:when test="${work.status.code == workStatusTypes['ACTIVE'] || work.status.code == workStatusTypes['COMPLETE']}">
										(<a class="edit">edit</a>)
									</c:when>
								</c:choose>
							</c:when>
							<c:when test="${work.status.code == workStatusTypes['ACTIVE'] or work.status.code == workStatusTypes['COMPLETE']}">
								(<a class="edit">edit</a>)
							</c:when>
						</c:choose>
						<i class="daylight-savings-alert icon-info-sign" title="WorkMarket automatically adjusts times for daylight savings. This will not affect the number of hours recorded for your work."></i>
					</script>

					<script id="tmpl-time-tracking-entry-checkin-distance-status" type="text/x-jquery-tmpl">
						\${date} at \${time} <c:out value="${assignment_tz}"/>
						<c:if test="${work.configuration.checkinRequiredFlag || !work.checkinCallRequired || !is_active_resource}">
							(<a class="edit">edit</a>)
						</c:if>
						(\${distance} mi)
						<i class="daylight-savings-alert icon-info-sign" title="WorkMarket automatically adjusts times for daylight savings. This will not affect the number of hours recorded for your work."></i>
					</script>

					<script id="tmpl-time-tracking-entry-checkout" type="text/x-jquery-tmpl">
						<c:choose>
							<c:when test="${!work.configuration.checkinRequiredFlag && work.checkinCallRequired && is_active_resource}">
							</c:when>
							<c:otherwise>
								<a class="button checkout_<c:if test='${work.showCheckoutNotesFlag}'>with_note_</c:if>action">Check Out</a>
							</c:otherwise>
						</c:choose>
					</script>

					<script id="tmpl-time-tracking-entry-checkout-disabled" type="text/x-jquery-tmpl">
						Please check in first.
					</script>

					<script id="tmpl-time-tracking-entry-checkout-status" type="text/x-jquery-tmpl">
						\${date} at \${time} <c:out value="${assignment_tz}"/>
						<c:choose>
							<c:when test="${is_active_resource}">
								<c:choose>
									<c:when test="${!work.configuration.checkinRequiredFlag && work.checkinCallRequired}">
									</c:when>
									<c:when test="${work.status.code == workStatusTypes['ACTIVE'] || work.status.code == workStatusTypes['COMPLETE']}">
										(<a class="edit">edit</a>)
									</c:when>
								</c:choose>
							</c:when>
							<c:when test="${work.status.code == workStatusTypes['ACTIVE'] or work.status.code == workStatusTypes['COMPLETE']}">
								(<a class="edit">edit</a>)
							</c:when>
						</c:choose>
						<i class="daylight-savings-alert icon-info-sign" title="WorkMarket automatically adjusts times for daylight savings. This will not affect the number of hours recorded for your work."></i>
					</script>

					<script id="tmpl-time-tracking-entry-checkout-distance-status" type="text/x-jquery-tmpl">
						\${date} at \${time} <c:out value="${assignment_tz}"/>
						<c:if test="${work.configuration.checkinRequiredFlag || !work.checkinCallRequired || !is_active_resource}">
							(<a class="edit">edit</a>)
						</c:if>
						(\${distance} mi)
						<i class="daylight-savings-alert icon-info-sign" title="WorkMarket automatically adjusts times for daylight savings. This will not affect the number of hours recorded for your work."></i>
					</script>

					<script id="tmpl-time-tracking-entry-form" type="text/x-jquery-tmpl">
						<div>
							<input name="time_tracking_date" value="\${date}" class="time_tracking_date span2"
								   placeholder='MM/DD/YYYY'/>
							<input name="time_tracking_time" value="\${time}" class="time_tracking_time span2"/>
							<br/><br/>
							<button class="button cancel">Cancel</button>
							<button class="button update">Update</button>
							<button class="button delete">Delete</button>
						</div>
					</script>
				</div>
			</div>
		</div>
	</div>
</div>
