<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="wm-csrf" uri="http://www.workmarket.com/taglib/csrf" %>
<%@ taglib prefix="wm" tagdir="/WEB-INF/tags" %>

<wm:app pagetitle="Ratings" bodyclass="accountSettings" breadcrumbSection="Work" breadcrumbSectionURI="/assignments" breadcrumbPage="Ratings" webpackScript="ratings">

	<script>
		var config = ${contextJson};
	</script>

	<c:import url="/WEB-INF/views/web/partials/message.jsp"/>
	<c:import url="/WEB-INF/views/web/partials/reports/review.jsp"/>

	<div class="inner-container">
		<h2>Rate and Review The Companies You've Worked For</h2>

		<p>
			Now that you've been paid for the assignments below, please rate and review your overall experience with the company.<br/>
			Your feedback will help companies and other freelancers make better decisions.
		</p>

		<div id="rr_table">
			<table id="rr_list">
				<thead>
				<tr>
					<th width="40%">Details</th>
					<th width="60%">Rating</th>
				</tr>
				</thead>
				<tbody>
				<tr>
					<td colspan="3" class="dataTables_empty">Loading data from server</td>
				</tr>
				</tbody>
			</table>
		</div>
	</div>

</wm:app>
