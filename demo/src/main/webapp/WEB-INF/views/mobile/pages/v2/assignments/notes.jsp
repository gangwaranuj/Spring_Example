<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="wmfmt" uri="http://www.workmarket.com/taglib/fmt" %>
<%@ taglib prefix="wmfn" uri="http://www.workmarket.com/taglib/functions" %>

<c:set var="pageScript" value="wm.pages.mobile.assignments.notes" scope="request"/>

<div class="notes-page">
	<jsp:include page="/WEB-INF/views/mobile/partials/nav.jsp">
		<jsp:param name="title" value="Messages" />
	</jsp:include>

	<div class="content">
		<div class="unit whole" id="public-message">
			<c:import url="/WEB-INF/views/mobile/partials/notices.jsp" />
		</div><%--unit whole--%>

		<%-- list notes --%>
		<c:if test="${isAdmin or isActiveResource or currentUser.internal}">
			<div class="add-note">
				<a href="javascript:void(0);" class="popup-open add-note-link" data-popup-selector="#add-note-popup">
					<c:import url="/WEB-INF/views/mobile/partials/svg-icons/pencil.jsp"/>Add Message
				</a>
			</div>
		</c:if>
		<c:choose>
			<c:when test="${not empty work.notes}">
				<div class="notes">
					<c:forEach var="note" items="${work.notes}">
						<div class="grid">
							<div class="unit whole">
								<c:set var="creatorIsActiveResource" value="${work.activeResource.user.id == note.creator.id}" />
								<div class="note-card ${creatorIsActiveResource ? "seller" : "buyer"}">
									<p>${wmfmt:escapeHtmlAndnl2br(note.text)}</p>
								</div><%--note card--%>
								<c:if test="${not empty note.onBehalfOf}">
									<p class="onbehalfof ${creatorIsActiveResource ? "seller" : "buyer"} ">(by
										<c:out value="${note.onBehalfOf.name.firstName}"/>
										<c:out value="${note.onBehalfOf.name.lastName}"/>
										<c:if test="${note.onBehalfOf.isWorkMarketEmployee}">
											<span class="label warning" title="Action by WM employee">WM</span>
										</c:if>)
									</p>
								</c:if>
								<div class="sender-info <c:if test="${not creatorIsActiveResource}">buyer</c:if> ">
									<strong><c:out value="${note.creator.name.firstName}" /> <c:out value="${note.creator.name.lastName}" /></strong><br>
									<small class="meta">
										${wmfmt:formatMillisWithTimeZone("MM/dd/yy h:mmaa z", note.createdOn, work.timeZone)}
										<c:if test="${note.isPrivate}"><span class="icon-lock"></span></c:if>
									</small>
								</div><%--sender info--%>
							</div><%--unit--%>
						</div><%--grid--%>
					</c:forEach>
				</div><%--notes--%>
			</c:when>
			<c:otherwise>
				<c:if test="${empty work.notes}">
					<p class="empty-notes-message">There are no notes for this assignment.</p>
				</c:if>
			</c:otherwise>
		</c:choose>
	</div><%--content--%>
</div><%--notes page--%>

<c:import url="/WEB-INF/views/mobile/pages/v2/assignments/add_note_popup.jsp"/>
