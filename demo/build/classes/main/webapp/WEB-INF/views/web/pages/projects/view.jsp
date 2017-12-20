<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="wmfmt" uri="http://www.workmarket.com/taglib/fmt" %>
<%@ taglib prefix="wm" tagdir="/WEB-INF/tags" %>

<wm:app pagetitle="${project.name}" bodyclass="page-project-details accountSettings" webpackScript="projects">

	<script type="text/javascript">
		var config = ${contextJson};
	</script>

	<div class="inner-container">
		<div class="page-header">
			<c:choose>
				<c:when test="${project.active}">
					<a class="button pull-right" id="new_project_assignment_action" href="#">New Project Assignment</a>
				</c:when>
				<c:otherwise>
					<a class="button pull-right" disabled>New Project Assignment</a>
				</c:otherwise>
			</c:choose>
			<h3><c:out value="${project.name}" />
				<c:if test="${not empty project.startDate.time and not empty project.dueDate.time}">
					<small>
					(
					<fmt:formatDate value="${project.startDate.time}" pattern="MM/dd/yyyy"/>
					to <fmt:formatDate value="${project.dueDate.time}" pattern="MM/dd/yyyy"/>
					)
					</small>
				</c:if>
			</h3>
		</div>

		<jsp:include page="/WEB-INF/views/web/partials/message.jsp" />

		<div class="row_wide_sidebar_right">
			<div class="content">

				<p><c:out value="${project.description}"/></p>

				<table id="project_list">
					<thead>
					<tr>
						<th width="30%">Title</th>
						<th width="15%">Location</th>
						<th width="15%">Start Date</th>
						<th width="10%">Price</th>
						<th width="15%">Status</th>
						<th width="15%">Worker</th>
					</tr>
					</thead>
					<tbody>
					<tr>
						<td colspan="5" class="dataTables_empty">Loading data from server</td>
					</tr>
					</tbody>
				</table>
			</div>
			<div class="sidebar">
				<div class="well-b2">
					<div>
						<h3 class="fake-h3">Overview</h3>
						<c:choose>
							<c:when test="${hasFeatureProjectPermission}">
								<c:if test="${hasProjectAccess}">
									<div class="dropdown pull-right" id="action">
										<a class="dropdown-toggle" data-toggle="dropdown"><i class="wm-icon-gear icon-large"></i><span class="caret"></span></a>
										<ul class="dropdown-menu right">
											<li><a href="/projects/edit/${project.id}">Edit</a></li>
											<c:choose>
												<c:when test="${project.active}">
													<li><a class="deactivate-project" data-id="${project.id}">Deactivate</a></li>
												</c:when>
												<c:otherwise>
													<li><a href="/projects/activate/${project.id}">Activate</a></li>
												</c:otherwise>
											</c:choose>
										</ul>
									</div>
								</c:if>
							</c:when>
							<c:otherwise>
								<div class="dropdown pull-right" id="action">
									<a class="dropdown-toggle" data-toggle="dropdown"><i class="wm-icon-gear icon-large"></i><span class="caret"></span></a>
									<ul class="dropdown-menu right">
										<li><a href="/projects/edit/${project.id}">Edit</a></li>
										<c:choose>
											<c:when test="${project.active}">
												<li><a class="deactivate-project" data-id="${project.id}">Deactivate</a></li>
											</c:when>
											<c:otherwise>
												<li><a href="/projects/activate/${project.id}">Activate</a></li>
											</c:otherwise>
										</c:choose>
									</ul>
								</div>
							</c:otherwise>
						</c:choose>
					</div>

					<div class="well-content">
						<strong>Project Owner:</strong>
						<p><c:out value="${project.owner.fullName}"/></p>
						<strong>Client:</strong>
						<p><c:out value="${project.clientCompany.name}"/></p>
						<c:if test="${project.budgetEnabledFlag}">
							<strong>Budget:</strong>
							<p><fmt:formatNumber type="currency" currencyCode="USD" value="${project.budget}"/></p>
						</c:if>
						<c:if test="${project.reservedFundsEnabled}">
							<strong>Reserved Cash Balance:</strong>
							<p><fmt:formatNumber type="currency" currencyCode="USD" value="${project.reservedFunds}"/></p>
						</c:if>
						<c:if test="${! project.active}">
							<strong>Status:</strong>
							<p>Inactive</p>
						</c:if>
					</div>
				</div>

				<c:if test="${project.budgetEnabledFlag}">
					<div class="well-b2">
						<h3>Budget Balance Statistics</h3>
						<div class="well-content">
							<i class="icon-tasks icon-2x muted"></i>
							<strong class="project-budget-stat-title"><fmt:formatNumber value="${totalWorkCounts}"/></strong>
							<p class="muted project-budget-stat-content">Assignments</p>
							<i class="icon-money icon-2x muted"></i>
							<strong class="project-budget-stat-title"><fmt:formatNumber type="currency" currencyCode="USD" value="${project.remainingBudget}"/></strong>
							<p class="muted project-budget-stat-content">Remaining Budget</p>

							<div id="project-budget">
								<canvas id="myChart" width="80" height="80"></canvas>
								<div style="margin-top: 15px">
								<p>
										<strong>
											<fmt:formatNumber value="${workInPaidPercentage}"/>%
											( <fmt:formatNumber type="currency" currencyCode="USD" value="${totalWorkValueInPaid}"/> )
										</strong>
										<small style="color: #F38630">In Paid</small>
									</p>
									<p>
										<strong>
											<fmt:formatNumber value="${workInProcessPercentage}"/>%
											( <fmt:formatNumber type="currency" currencyCode="USD" value="${totalWorkValueInProcess}"/> )
										</strong>
										<small style="color: #999966">In Process</small>
									</p>
								</div>
							</div>
						</div>
					</div>
				</c:if>
			</div>
		</div>
	</div>

</wm:app>
