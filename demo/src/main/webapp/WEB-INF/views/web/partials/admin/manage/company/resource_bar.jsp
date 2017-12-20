<%@ taglib prefix="wmfn" uri="http://www.workmarket.com/taglib/functions" %>

<h4>Resources</h4>

<table>
	<thead>
		<tr>
			<th>Total Resources</th>
			<th>Internal</th>
			<th>External</th>
			<th>Groups</th>
			<th>Invites</th>
			<th>Campaigns</th>
		</tr>
	</thead>
	<tbody>
		<tr>
			<td>${wmfn:nVal(requestScope.peopleSummary.lane1) + wmfn:nVal(requestScope.peopleSummary.lane2) + wmfn:nVal(requestScope.peopleSummary.lane3)}</td>
			<td>${wmfn:nVal(requestScope.peopleSummary.lane1)}</td>
			<td>${wmfn:nVal(requestScope.peopleSummary.lane2) + wmfn:nVal(requestScope.peopleSummary.lane3)}</td>
			<td>${wmfn:nVal(requestScope.peopleSummary.groups)}</td>
			<td>${wmfn:nVal(requestScope.peopleSummary.invitations)}</td>
			<td>${wmfn:nVal(requestScope.peopleSummary.campaigns)}</td>
		</tr>
	</tbody>
</table>
