<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="wmfmt" uri="http://www.workmarket.com/taglib/fmt" %>
<%@ taglib prefix="wm-csrf" uri="http://www.workmarket.com/taglib/csrf" %>
<%@ taglib prefix="wm" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<fmt:message key="devices.manage_devices" var="devices_manage_devices"/>
<wm:app
	pagetitle="${devices_manage_devices}"
	bodyclass="accountSettings"
	webpackScript="settings"
>

	<script>
		var config = {
			mode: 'devices'
		};
	</script>

	<jsp:include page="/WEB-INF/views/web/partials/message.jsp"/>

	<div class="inner-container">
		<div class="page-header clear">
			<h3><fmt:message key="devices.my_mobile_devices"/></h3>
		</div>

		<c:choose>
			<c:when test="${not empty devices}" >
			<table id="devices">
				<thead>
				<tr>
						<th><fmt:message key="devices.os"/></th>
						<th><fmt:message key="devices.identifier"/></th>
						<th><fmt:message key="global.added"/></th>
						<th><fmt:message key="global.remove"/>?</th>
					</tr>
				</thead>
				<tbody>
					<c:forEach var="item" items="${devices}">
						<tr>
							<td><c:out value="${item.deviceType}"/></td>
							<td class="device_uid"><span class="device_uid_text"><c:out value="${item.deviceUid}"/></span></td>
							<td>${wmfmt:formatCalendar("MM/dd/yyyy", item.createdOn)}</td>
							<td><button class="btn btn-danger"><fmt:message key="global.remove"/></button></td>
						</tr>
					</c:forEach>
				</tbody>
			</table>
			</c:when>
			<c:otherwise>
				<p><fmt:message key="devices.no_mobile_connected"/></p>
			</c:otherwise>
		</c:choose>
	</div>

</wm:app>
