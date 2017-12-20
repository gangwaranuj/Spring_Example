<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="wm" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<fmt:message key="global.labels" var="global_labels"/>
<wm:app
	pagetitle="${global_labels}"
	bodyclass="page-settings-labels"
	webpackScript="settings"
>

	<script>
		var config = {
			mode: 'labels'
		};
	</script>

	<div class="sidebar">
		<c:set var="selected_navigation_link" value="/settings/manage/labels" scope="request"/>
		<jsp:include page="/WEB-INF/views/web/partials/mmw/mmw_sidebar.jsp"/>
	</div>

	<div class="content" id="labels_dashboard">
		<div class="inner-container">
			<jsp:include page="/WEB-INF/views/web/partials/message.jsp" />
			<div class="page-header clear">
				<a class="button -primary pull-right cta-manage-label" href="javascript:void(0);" rel="add"><fmt:message key="labels.new_label"/></a>
				<h3><fmt:message key="global.labels"/></h3>
			</div>

			<div class="alert alert-info">
				<div class="row-fluid"><fmt:message key="labels.labels_details"/>
					<strong><a href="https://workmarket.zendesk.com/hc/en-us/articles/209336778" target="_blank"><fmt:message key="global.learn_more"/> <i class="icon-info-sign"></i></a></strong>
				</div>
			</div>

			<table>
				<thead>
				<tr>
					<th><fmt:message key="global.color"/></th>
					<th>Name</th>
					<th class="text-center"><fmt:message key="labels.dashboard_visibility"/></th>
					<th class="text-center"><fmt:message key="global.edit"/></th>
					<th class="text-center"><fmt:message key="global.delete"/></th>
				</tr>
				</thead>

				<tbody>
				<c:choose>
					<c:when test="${not empty(requestScope.customDashboard.workSubStatusList)}">
						<c:forEach var="item" items="${requestScope.customDashboard.workSubStatusList}">
							<tr>
								<td>
									<c:choose>
										<c:when test="${!empty item.customColorRgb}">
											<a href="javascript:void(0);" class="colorpicker" data-id="${item.id}" data-color="#${item.customColorRgb}" style="background-color: #${item.customColorRgb};"></a>
										</c:when>
										<c:otherwise>
											<a href="javascript:void(0);" class="colorpicker" data-id="${item.id}" data-color="#C2C2C2" style="background-color: #C2C2C2;"></a>
										</c:otherwise>
									</c:choose>
								</td>
								<td>
									<div <c:if test="${not item.active}">class="semi-transparent"</c:if>>
									<span data-toggle="popover" rel="popover-container" data-original-title="<c:out value="${item.description}" />">
										<c:out value="${item.description}" />
										<div  rel="popover-content" class="dn popover<c:if test="${not item.active}"> semi-transparent</c:if>">
											<fmt:message key="labels.worker_access"/>
											<c:choose>
												<c:when test="${item.resourceVisible and item.resourceEditable}"><fmt:message key="global.view"/>, <fmt:message key="global.edit"/></c:when>
												<c:when test="${item.resourceVisible}"><fmt:message key="global.view"/></c:when>
												<c:when test="${item.resourceEditable}"><fmt:message key="global.edit"/></c:when>
												<c:otherwise>-</c:otherwise>
											</c:choose>
											<br />

											<fmt:message key="labels.notifications"/>
											<c:choose>
												<c:when test="${item.notifyClientEnabled and item.notifyResourceEnabled}"><fmt:message key="labels.io"/>, <fmt:message key="labels.r"/></c:when>
												<c:when test="${item.notifyClientEnabled}"><fmt:message key="global.io"/></c:when>
												<c:when test="${item.notifyResourceEnabled}"><fmt:message key="global.r"/></c:when>
												<c:otherwise>-</c:otherwise>
											</c:choose>
											<br />

											<fmt:message key="labels.alert"/>
											<c:choose>
												<c:when test="${item.alert}"><fmt:message key="global.yes"/></c:when>
												<c:otherwise><fmt:message key="global.no"/></c:otherwise>
											</c:choose>
											<br />

											<fmt:message key="labels.actions"/>
											<c:choose>
												<c:when test="${item.scheduleRequired and item.noteRequired}"><fmt:message key="global.reschedule"/>, <fmt:message key="global.note"/></c:when>
												<c:when test="${item.scheduleRequired}"><fmt:message key="global.reschedule"/></c:when>
												<c:when test="${item.noteRequired}"><fmt:message key="global.note"/></c:when>
												<c:otherwise>-</c:otherwise>
											</c:choose>
											<br />
										</div>
									</span>
									</div>
								</td>
								<td class="actions">
									<a class="cta-success muted tooltipped tooltipped-n <c:if test="${item.dashboardDisplayType eq 'SHOW'}">text-success</c:if>" href="<c:url value="/settings/manage/label_dashboard_display/${item.id}?type=SHOW"/>" aria-label="<fmt:message key="labels.shown_on_dashboard"/>"><i class="icon-ok-sign icon-large"></i></a>
									<a class="cta-error tooltipped tooltipped-n muted <c:if test="${item.dashboardDisplayType eq 'HIDE'}">text-error</c:if>" href="<c:url value="/settings/manage/label_dashboard_display/${item.id}?type=HIDE"/>" aria-label="<fmt:message key="labels.hidden_on_dashboard"/>"><i class="icon-remove-sign icon-large"></i></a>
									<a class="cta-change-display-label tooltipped tooltipped-n <c:if test="${item.dashboardDisplayType == 'SHOW_IF_ACTIVE'}">labels_selected_display_type</c:if>" href="<c:url value="/settings/manage/label_dashboard_display/${item.id}?type=SHOW_IF_ACTIVE"/>" data-placement="right" aria-label="<fmt:message key="labels.shown_on_dashboard_if"/>"><fmt:message key="global.other"/></a>
								</td>
								<td class="actions">
									<a href="<c:url value="/settings/manage/labels_manage?id=${item.id}"/>" rel="edit" class="cta-manage-label tooltipped tooltipped-n" aria-label="<fmt:message key="global.edit"/>"><i class="wm-icon-edit icon-large muted"></i></a>
								</td>
								<td class="actions">
									<a class="cta-delete-label tooltipped tooltipped-n" aria-label="<fmt:message key="global.delete"/>" href="<c:url value="/settings/manage/label_delete/${item.id}"/>"><i class="wm-icon-trash icon-large muted" data-action="trash"></i></a>
								</td>
							</tr>
						</c:forEach>
					</c:when>
					<c:otherwise>
						<tr>
							<td colspan="6"><fmt:message key="labels.no_custom_labels_configured"/></td>
						</tr>
					</c:otherwise>
				</c:choose>
				</tbody>

			</table>
			<br/>
			<h2 class="sidebar-card--title"><fmt:message key="labels.system_labels"/></h2>
			<p><fmt:message key="labels.system_labels_details"/></p>
			<table>
				<thead>
				<tr>
					<th><fmt:message key="global.color"/></th>
					<th><fmt:message key="global.name"/></th>
					<th class="text-center"><fmt:message key="labels.dashboard_visibility"/></th>
				</tr>
				</thead>
				<tbody>
				<c:forEach var="item" items="${requestScope.dashboard.workSubStatusList}">
					<tr>
						<td>
							<c:choose>
								<c:when test="${!empty item.customColorRgb}">
									<a href="javascript:void(0);" class="colorpicker" data-id="${item.id}" data-color="#${item.customColorRgb}" style="background-color: #${item.customColorRgb};"></a>
								</c:when>
								<c:otherwise>
									<a href="javascript:void(0);" class="colorpicker" data-id="${item.id}" data-color="#C2C2C2" style="background-color: #C2C2C2;"></a>
								</c:otherwise>
							</c:choose>
						</td>
						<td>
							<span data-toggle="popover" rel="popover-container" data-original-title="<c:out value="${item.description}"/>">
								<c:out value="${item.description}"/>
								<div class="dn">
									<div rel="popover-content" class="popover <c:if test="${not item.active}"> semi-transparent</c:if>">
										<fmt:message key="global.worker_access"/>:
										<c:choose>
											<c:when test="${item.resourceVisible and item.resourceEditable}"><fmt:message key="global.view"/>, <fmt:message key="global.edit"/></c:when>
											<c:when test="${item.resourceVisible}"><fmt:message key="global.view"/></c:when>
											<c:when test="${item.resourceEditable}"><fmt:message key="global.edit"/></c:when>
											<c:otherwise>-</c:otherwise>
										</c:choose>
										<br />

										<fmt:message key="labels.notifications"/>
										<c:choose>
											<c:when test="${item.notifyClientEnabled and item.notifyResourceEnabled}"><fmt:message key="labels.io"/>, <fmt:message key="labels.r"/></c:when>
											<c:when test="${item.notifyClientEnabled}"><fmt:message key="labels.io"/></c:when>
											<c:when test="${item.notifyResourceEnabled}"><fmt:message key="labels.r"/></c:when>
											<c:otherwise>-</c:otherwise>
										</c:choose>
										<br />

										<fmt:message key="labels.alert"/>
										<c:choose>
											<c:when test="${item.alert}"><fmt:message key="global.yes"/></c:when>
											<c:otherwise><fmt:message key="global.no"/></c:otherwise>
										</c:choose>
										<br />

										<fmt:message key="label.actions"/>
										<c:choose>
											<c:when test="${item.scheduleRequired and item.noteRequired}"><fmt:message key="global.reschedule"/>, <fmt:message key="global.note"/></c:when>
											<c:when test="${item.scheduleRequired}"><fmt:message key="global.reschedule"/></c:when>
											<c:when test="${item.noteRequired}"><fmt:message key="global.note"/></c:when>
											<c:otherwise>-</c:otherwise>
										</c:choose>
										<br />
									</div>
								</div>
							</span>
						</td>
						<td class="text-center">
							<a class="cta-success tooltipped tooltipped-n muted <c:if test="${item.dashboardDisplayType == 'SHOW'}">text-success</c:if>" href="<c:url value="/settings/manage/label_dashboard_display/${item.id}?type=SHOW"/>" aria-label="<fmt:message key="labels.shown_on_dashboard"/>"><i class="icon-ok-sign icon-large"></i></a>
							<a class="cta-error tooltipped tooltipped-n muted <c:if test="${item.dashboardDisplayType == 'HIDE'}">text-error</c:if>" href="<c:url value="/settings/manage/label_dashboard_display/${item.id}?type=HIDE"/>" aria-label="<fmt:message key="labels.hidden_on_dashboard"/>"><i class="icon-remove-sign icon-large"></i></a>
							<a class="cta-change-display-label tooltipped tooltipped-n <c:if test="${item.dashboardDisplayType == 'SHOW_IF_ACTIVE'}">labels_selected_display_type</c:if>" href="<c:url value="/settings/manage/label_dashboard_display/${item.id}?type=SHOW_IF_ACTIVE"/>" aria-label="<fmt:message key="labels.shown_on_dashboard"/>"><fmt:message key="global.other"/></a>
						</td>
					</tr>
				</c:forEach>
				</tbody>
			</table>
		</div>
	</div>

</wm:app>
