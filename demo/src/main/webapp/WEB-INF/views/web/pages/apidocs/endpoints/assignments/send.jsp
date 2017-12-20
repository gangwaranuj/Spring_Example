<h2>Send Assignment</h2>
<p><em>https://www.workmarket.com/api/v1/<strong>assignments/{id}/send</strong></em></p>

<strong>Note:</strong> the assignment ID is passed in the URL, not as a body parameter.</p>

<p>Sends the assignment to the specified group and/or worker IDs.

<table>
	<tr>
		<td>HTTP Method</td>
		<td><code>POST</code></td>
	</tr>
	<tr>
		<td>Requires Authentication</td>
		<td>Yes</td>
	</tr>
</table>

<h3>Parameters</h3>

<table>
	<tbody>
		<tr>
			<td><code>group_id</code> (multiple permitted)</td>
			<td>1234</td>
			<td>The group ID to send to.  <strong>You can specify multiple groups by passing in more than 1 <code>group_id=x</code> parameter.</strong></td>
		</tr>
		<tr>
			<td><code>send_radius</code></td>
			<td>25</td>
			<td>Group Send only.  The maximum distance from the assignment location (in miles).  Group workers outside this range will not be invited.  Default is 60 miles, max is 100 miles.</td>
		</tr>
		<tr>
			<td><code>resource_id</code> (multiple permitted)</td>
			<td>87654321</td>
			<td>Worker ID <strong>or</strong> email address to send the assignment to.  No distance filter is applied. <strong>You can specify multiple worker IDs by passing in more than 1 <code>resource_id=x</code> parameter.</strong></td>
		</tr>
		<tr>
			<td><code>auto_invite</code></td>
			<td>1</td>
			<td>Automatically determine the best workers for your assignment and send to them.</td>
		</tr>
</table>

<h3>Response fields</h3>
<table>
	<tr>
		<td><code>successful</code></td>
		<td><code>true</code> or <code>false</code>. Send calls are considered successful if <strong>all</strong> group send attempts or <strong>at least 1</strong> worker send attempt are successful.
			Note that because <strong>group</strong> send is asynchronous, you only know if it was truly successful (i.e. if there was least one eligible worker) if the assignment status transitions to "Sent".</td>
	</tr>
	<tr>
		<td><code>invalid_groups</code></td>
		<td>Invalid group IDs are listed here.</td>
	</tr>
	<tr>
		<td><code>resource_send_results</code></td>
		<td>Lists the send outcome for each worker specified by "resource_id" parameters.  Shows if they were successful or otherwise, and a general reason why if not.</td>
	</tr>
</table>
