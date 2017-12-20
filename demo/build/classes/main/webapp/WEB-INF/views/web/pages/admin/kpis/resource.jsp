<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="wmfmt" uri="http://www.workmarket.com/taglib/fmt" %>
<%@ taglib prefix="wm" tagdir="/WEB-INF/tags" %>

<wm:admin pagetitle="Resource">

<div class="sidebar admin">
	<c:import url="/WEB-INF/views/web/partials/admin/kpis/kpis_sidebar.jsp" />
</div>

<div class="content">
	<h2>Worker Report</h2>

	<form method="get" enctype="multipart/form-data">
		<div class="row">
			<div class="span4">
				<h4>Industry</h4>
				<input type="checkbox" value="true" id="industry" name="industry">Break out by industry
			</div>
			<div class="span6">
				<h4>Funding Type Filter</h4>
				<div class="row">
					<div class="span4">
						<select id="fundingType"  name="fundingType">
							<option value="">Any</option>
							<option value="cash">Cash</option>
							<option value="terms">Terms</option>
						</select>
					</div>
					<div class="span2">
						<input type="submit" value="Go" class="button" />
					</div>
				</div>
			</div>
		</div>
	</form>

	<h4>Workers Receiving Assignment (Monthly)</h4>
	<table id="monthlyResourceReceivingAssignment" class="table table-striped">
		<thead>
			<tr>
				<c:forEach items="${monthList}" var="month">
					<th><c:out value="${month}" /></th>
				</c:forEach>
			</tr>
		</thead>
		<tbody>
		</tbody>
	</table>

	<h4>Workers Receiving Assignment (Weekly)</h4>
	<table id="weeklyResourceReceivingAssignment" class="table table-striped">
		<thead>
			<tr>
				<th></th>
				<c:forEach items="${weekList}" var="week">
					<th><c:out value="${week}" /></th>
				</c:forEach>
			</tr>
		</thead>
		<tbody>
		</tbody>
	</table>

	<h4>Workers Receiving Assignment TTM (Weekly)</h4>
	<table id="weeklyResourceReceivingAssignmentTTM" class="table table-striped">
		<thead>
			<tr>
				<th></th>
				<c:forEach items="${weekList}" var="week">
					<th><c:out value="${week}" /></th>
				</c:forEach>
			</tr>
		</thead>
		<tbody>
		</tbody>
	</table>
</div>

<script type="text/javascript">
	$(wm.pages.admin.kpis.resource('${wmfmt:escapeJavaScript(fundingType)}', '${wmfmt:escapeJavaScript(industry)}'));
</script>

</wm:admin>
