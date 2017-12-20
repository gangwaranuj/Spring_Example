<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="wm" tagdir="/WEB-INF/tags" %>

<wm:admin pagetitle="Approval Queue" webpackScript="admin">

	<script>
		var config = {
			mode: 'subscriptionApprovalQueue'
		};
	</script>

	<div class="sidebar admin">
		<c:import url="/WEB-INF/views/web/partials/admin/quick_links.jsp" />
	</div>

	<div class="content">
		<div id="dynamic_messages"></div>

		<c:import url="/WEB-INF/views/web/partials/general/notices.jsp"/>

		<div class="row">
			<div class="span12">
				<h1>Subscription Approval Queue</h1>
			</div>
		</div>

		<form id="approval_queue_form">
			<table id="approval_queue_table" class="table-bordered zebra-striped">
				<thead>
				<tr>
					<th class="check"><input type="checkbox" name="select-all" id="select_all"></th>
					<th>ID</th>
					<th>Type</th>
					<th>Company Name</th>
					<th>Submitted by</th>
					<th>Effective/Renewal Date</th>
					<th>VOR</th>
					<th>Auto Renewal</th>
					<th>Payment Period</th>
					<th>Terms (months)</th>
					<th>Payment Period Amount</th>
					<th>VOR Period Amount</th>
					<th>Setup/Cancellation Fee</th>
					<th>Total Discount</th>
					<th>Total Add-on</th>
					<th>MRR</th>
					<th>ARR</th>
				</tr>
				</thead>
				<tbody>
					<%-- Filled with DataTables --%>
				</tbody>
			</table>
		</form>

		<hr/>

		<div class="wm-action-container">
			<button id="subscriptions_queue_reject" class="button">Reject</button>
			<button id="subscriptions_queue_approve" class="button">Approve</button>
		</div>
	</div>

	<%-- Templates --%>
	<script id="subscription-checkbox-tmpl" type="text/x-jquery-tmpl">
		<div><input type="checkbox" name="subscription_id[]" value="\${data}"/></div>
	</script>

	<script id="subscription-id-tmpl" type="text/x-jquery-tmpl">
		<div><a href="/admin/manage/company/pricing/\${meta.company_id}">\${meta.id}</a></div>
	</script>

	<script id="currency-tmpl" type="text/x-jquery-tmpl">
		<div>
			 {{if data === null}}
				 n/a
			 {{else}}
				 $\${data}
			 {{/if}}
		 </div>
	</script>

</wm:admin>
