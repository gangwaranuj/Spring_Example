<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<c:set var="pageScript" value="wm.pages.mobile.home" scope="request"/>

<div class="wrap home">
	<jsp:include page="/WEB-INF/views/mobile/partials/nav.jsp"/>

	<c:import url="/WEB-INF/views/mobile/partials/general-panel.jsp"/>
	<div class="grid content">

		<div class="grid">
			<div class="unit whole" id="public-message">
				<c:import url="/WEB-INF/views/mobile/partials/notices.jsp" />
			</div><%--unit whole--%>
		</div><%--grid--%>

		<div class="section important">
			<div class="unit whole mobile-list">
				<input name="invited-count" id="invited-count" type="hidden"
				       value="${counts[WorkStatusType.AVAILABLE].statusCount}"/>
				<a class="spin" id="find-work" href="/mobile/assignments/available">
					<div id="available-count" class="count"><c:out value="${counts[WorkStatusType.AVAILABLE].statusCount}" default="0"/></div>
					<div class="status">Available Work</div>
					<c:import url="/WEB-INF/views/mobile/partials/svg-icons/icon-clicker-right-orange.jsp"/>
				</a>
			</div><%--unit whole--%>
		</div><%--important--%>

		<div class="section less-important">
			<div class="unit whole mobile-list">
				<a class="spin" id="assigned-count"
				   href="<c:url value="/mobile/assignments/list/${WorkStatusType.ACTIVE}"/>">
					<div class="count"><c:out value="${counts[WorkStatusType.ACTIVE].statusCount}" default="0"/></div>
					<div class="status">Assigned</div>
					<c:import url="/WEB-INF/views/mobile/partials/svg-icons/icon-clicker-right-gray.jsp"/>

				</a>
			</div><%--unit whole--%>
			<div class="unit whole mobile-list">
				<a class="spin" id="inprogress-count"
				   href="<c:url value="/mobile/assignments/list/${WorkStatusType.INPROGRESS}"/>">
					<div class="count"><c:out value="${counts[WorkStatusType.INPROGRESS].statusCount}" default="0"/></div>
					<div class="status">In Progress</div>
					<c:import url="/WEB-INF/views/mobile/partials/svg-icons/icon-clicker-right-gray.jsp"/>
				</a>
			</div><%--unit whole--%>

			<div class="unit whole mobile-list">
				<a class="spin" id="pending-count"
				   href="<c:url value="/mobile/assignments/list/${WorkStatusType.COMPLETE}"/>">
					<div class="count"><c:out value="${counts[WorkStatusType.COMPLETE].statusCount}" default="0"/></div>
					<div class="status">Pending Approval</div>
					<c:import url="/WEB-INF/views/mobile/partials/svg-icons/icon-clicker-right-gray.jsp"/>
				</a>
			</div><%--unit whole--%>
		</div><%--less-important--%>

		<div class="section late-stage">
			<div class="unit whole mobile-list">
				<a class="spin" id="invoiced-count"
				   href="<c:url value="/mobile/assignments/list/${WorkStatusType.PAYMENT_PENDING}"/>">
					<div class="count"><c:out value="${counts[WorkStatusType.PAYMENT_PENDING].statusCount}" default="0"/></div>
					<div class="status">Invoiced</div>
					<c:import url="/WEB-INF/views/mobile/partials/svg-icons/icon-clicker-right-gray.jsp"/>
				</a>
			</div>
			<%--unit whole--%>

			<div class="unit whole mobile-list">
				<a class="spin" id="paid-count" href="<c:url value="/mobile/assignments/list/${WorkStatusType.PAID}"/>">
					<div class="count"><c:out value="${counts[WorkStatusType.PAID].statusCount}" default="0"/></div>
					<div class="status">Paid</div>
					<c:import url="/WEB-INF/views/mobile/partials/svg-icons/icon-clicker-right-gray.jsp"/>
				</a>
			</div>
			<%--unit whole--%>
		</div>

		<div class="section funds-stage">
			<div class="unit whole mobile-list">
				<a class="spin" id="funds" href="/mobile/funds">
					<div class="count">
						<span class="funds-count">
							<fmt:formatNumber value="${availableFunds}" type="currency" />
						</span>
					</div>
					<div class="status">Funds</div>
					<c:import url="/WEB-INF/views/mobile/partials/svg-icons/icon-clicker-right-gray.jsp"/>
				</a>
			</div>
		</div>

		<div class="unit whole home-footer-links">
			<a class="home-help-link spin" href="/mobile/help">Need some help?</a>
			<c:import url="/WEB-INF/views/mobile/partials/svg-icons/icon-signout.jsp"/>
			<a href="/logout" id="logout">Sign Out</a>
			<c:if test="${empty cookie['wm-app-platform'].value}">
				<%-- Only show full site link if we are NOT using an app --%>
				<p><a href="/?site_preference=normal">View Desktop Site</a></p>
			</c:if>
		</div><%--unit whole--%>


	</div><%--grid--%>
</div><%--wrap--%>
