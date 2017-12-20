<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="wmfmt" uri="http://www.workmarket.com/taglib/fmt" %>
<%@ taglib prefix="wmfn" uri="http://www.workmarket.com/taglib/functions" %>
<%@ taglib prefix="wm-csrf" uri="http://www.workmarket.com/taglib/csrf" %>

<c:set var="pageScript" value="wm.pages.mobile.assignments.addLabel" scope="request" />
<c:set var="pageScriptParams" value="'${wmfmt:escapeJavaScript(param.label_id)}', '${labelsJson}'" scope="request" />

<div class="wrap add-label-page">
	<jsp:include page="/WEB-INF/views/mobile/partials/nav.jsp">
		<jsp:param name="title" value="Add Label" />
	</jsp:include>

	<div class="content grid">
		<div id="public-message">
			<c:import url="/WEB-INF/views/mobile/partials/notices.jsp" />
		</div>

		<div class="unit whole">
			<form action="/mobile/assignments/add_label/${work.workNumber}" method="post" id="add-label-form">
				<wm-csrf:csrfToken />

				<input type="hidden" name="id" value="${work.workNumber}" />

				<label for="label_id" class="required">Label:</label>

				<select name="label_id" id="label_id">
					<option value='' selected disabled>Select...</option>
					<c:forEach var="item" items="${labels}">
						<option value="${item.value.id}"><c:out value="${item.value.description}" /></option>
					</c:forEach>
				</select>
				<c:import url="/WEB-INF/views/mobile/partials/svg-icons/icon-select-arrow-down.jsp" />

				<label for="label_note">Note:</label>
				<textarea name="note" id="label_note"></textarea>

				<div id="label_reschedule" class="whole" style="display: none;">
					<h2>Reschedule</h2>

					<div class="notice">
						<c:choose>
							<c:when test="${isActiveResource}">
								<small>Note: If the client declines your reschedule request, you will be unassigned from this assignment.</small>
							</c:when>
							<c:otherwise>
								<small>Note: If the worker declines your reschedule request, they will be unassigned from this assignment.</small>
							</c:otherwise>
						</c:choose>
					</div>

					<dl>
						<c:choose>
							<c:when test="${not empty work.schedule and not empty work.schedule.from}">
								<c:choose>
									<c:when test="${work.schedule.range}">
										<c:choose>
											<c:when test="${work.schedule.through - work.schedule.from < 24 * 60 * 60 * 1000}">
												<c:set var="fromFmt" value="EEEE, MMM d, YYYY h:mma" />
												<c:set var="throughFmt" value="h:mma z" />
											</c:when>
											<c:otherwise>
												<c:set var="fromFmt" value="EEEE, MMM d, YYYY h:mma" />
												<c:set var="throughFmt" value="EEEE, MMM d, YYYY h:mma z" />
											</c:otherwise>
										</c:choose>
										<dt>Current Window:</dt>
										<dd>
											<span class="schedule-from" data-timestamp="${work.schedule.from}"><c:out value="${wmfmt:formatMillisWithTimeZone(fromFmt, work.schedule.from, work.timeZone)}"/></span> to
											<span class="schedule-through" data-timestamp="${work.schedule.through}"><c:out value="${wmfmt:formatMillisWithTimeZone(throughFmt, work.schedule.through, work.timeZone)}"/></span>
										</dd>
									</c:when>
									<c:otherwise>
										<dt>Current Time:</dt>
										<dd>
											<span class="schedule-from" data-timestamp="${work.schedule.from}"><c:out value="${wmfmt:formatMillisWithTimeZone('EEEE, MMM d, YYYY h:mma z', work.schedule.from, work.timeZone)}"/></span>
										</dd>
									</c:otherwise>
								</c:choose>
							</c:when>
							<c:otherwise>
								Not set.
							</c:otherwise>
						</c:choose>
					</dl>

					<label class="header-label">
						<input class="radio" type="radio" id="new_time" name="reschedule_option" value="time" checked/>
						<span id="label_specific_time">Propose</span> <strong>time</strong>
					</label>
					<label class="header-label">
						<input class="radio" type="radio" id="new_window" name="reschedule_option" value="window" />
						<span id="label_time_window">Propose</span> <strong>time window</strong>
					</label>

					<div class="nine-tenths">
						<div class="inner-tag">
							<div class="half">
								<input name="from" id="from" class="pickadate" placeholder="date" readonly/>
							</div>
							<div class="half">
								<input name="fromtime" id="fromtime" class="timepicker" placeholder="time" readonly/>
							</div>
						</div>
						<div class="to-date" hidden>
							<span class="header-label">TO</span>
							<div class="inner-tag">
								<div class="half">
									<input name="to" id="to" class="pickadate" placeholder="date" readonly/>
								</div>
								<div class="half">
									<input name="totime" id="totime" class="timepicker" placeholder="time" readonly/>
								</div>
							</div>
						</div>
					</div>
				</div>
				<input type="submit" id="add-label" class="update-button" disabled name="submit" value="Update" />
				<a href="/mobile/assignments/details/${work.workNumber}" class="cancel-button">Cancel</a>
			</form>
		</div>

	</div>
</div>