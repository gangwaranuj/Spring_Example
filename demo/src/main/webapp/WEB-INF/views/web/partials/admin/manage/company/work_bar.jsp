<h4>Assignments</h4>

<table>
	<thead>
		<tr>
			<th>Draft</th>
			<th>Sent</th>
			<th>Assigned</th>
		</tr>
	</thead>
	<tbody>
		<tr>
			<td>${requestScope.STATUS_DRAFT}</td>
			<td>${requestScope.STATUS_SENT}</td>
			<td>${requestScope.STATUS_ACTIVE}</td>
		</tr>
	</tbody>
</table>

<table>
	<thead>
		<tr>
			<th>In Progress $</th>
			<th>In Progress Terms</th>
			<th>Complete</th>
			<th>Pay Pending</th>
			<th>Paid/Closed</th>
		</tr>
	</thead>
	<tbody>
		<tr>
			<td>${requestScope.INPROGRESS_PREFUND}</td>
			<td>${requestScope.INPROGRESS_PAYMENT_TERMS}</td>
			<td>${requestScope.COMPLETE}</td>
			<td>${requestScope.PAYMENT_PENDING}</td>
			<td>${requestScope.PAID}</td>
		</tr>
	</tbody>
</table>
