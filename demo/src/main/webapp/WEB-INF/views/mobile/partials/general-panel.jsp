<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<div id="panel-background"></div><%--panel-background--%>

<div id="wm-panel-page">
	<div class="panel-content">
		<ul>
			<li><a href="/mobile/assignments/available">Available Work</a></li>
			<li><a href="<c:url value="/mobile/assignments/list/${WorkStatusType.ACTIVE}"/>" class="spin">Assigned</a></li>
			<li><a href="<c:url value="/mobile/assignments/list/${WorkStatusType.INPROGRESS}"/>" class="spin" >In Progress</a></li>
			<li><a href="<c:url value="/mobile/assignments/list/${WorkStatusType.COMPLETE}"/>" class="spin">Pending Approval</a></li>
			<li><a href="<c:url value="/mobile/assignments/list/${WorkStatusType.PAYMENT_PENDING}"/>" class="spin">Invoiced</a></li>
			<li><a href="<c:url value="/mobile/assignments/list/${WorkStatusType.PAID}"/>" class="spin">Paid</a></li>
			<li><a href="/mobile/funds" class="spin">Funds</a></li>
		</ul>
		<hr>
		<div class="unit whole bottom-section">
			<div class="whole unit">
				<div class="one-third">
					<a class="spin" href="javascript:history.go(-1);" class="spin">Back</a>
				</div>
				<div class="one-third">
					<a class="spin" href="/mobile" class="spin">Home</a>
				</div>
				<div class="one-third">
					<a class="spin" href="/mobile/help" class="spin">Help</a>
				</div>
			</div>
			<div class="one-half">
				<c:import url="/WEB-INF/views/mobile/partials/svg-icons/icon-signout.jsp"/>
				<a class="spin" href="/logout" id="logout">Sign Out</a>
			</div>
		</div><%--bottom section whole--%>
	</div><%--panel content--%>
	<div class="rocket-container">
		<c:import url="/WEB-INF/views/mobile/partials/svg-icons/icon-rocket.jsp"/>
		<small>Designed and Engineered in NYC</small>
	</div><%--rocket container--%>
</div><%--panel page--%>