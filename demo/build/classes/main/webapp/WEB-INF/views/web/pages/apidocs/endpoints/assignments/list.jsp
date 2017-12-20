<h2>List Assignments</h2>
<p><em>https://www.workmarket.com/api/v1/<strong>assignments/list</strong></em></p>

<p>List assignments.</p>

<table>
	<tr>
		<td>HTTP Method</td>
		<td><code>GET</code></td>
	</tr>
	<tr>
		<td>Requires Authentication</td>
		<td>Yes</td>
	</tr>
</table>

<h3>Parameters <small>All parameters are optional, unless otherwise indicated.</small></h3>

<table>
	<tbody>
		<tr>
			<td><code>status</code></td>
			<td>active</td>
			<td>One of <code>draft</code>, <code>sent</code>, <code>void</code>, <code>declined</code>, <code>active</code>, <code>cancelled</code>, <code>inprogress</code>, <code>complete</code>,  <code>paymentPending</code>, <code>paid</code>, <code>refunded</code>, or <code>exception</code>
				<p>See <a href="/apidocs/endpoints/assignments/statuses">Assignments &raquo; Statuses</a> for a full list</p>
				<p><strong>Note:</strong> <code>exception</code> is a pseudo-status that will return any assignment with a label that is configured as an "alert" label. As such, the actual status of returned assignments can vary.</p>
			</td>
		</tr>
		<tr>
			<td><code>labels</code></td>
			<td>2315,3931</td>
			<td>
				Filters for only assignments that have the at least one of the provided label IDs (comma-separated). See <a href="/apidocs/endpoints/assignments/labels/list">List Labels</a>.
			</td>
		</tr>
		<tr>
			<td><code>client_id</code></td>
			<td>5317</td>
			<td>
				Filters for only assignments associated with the provided client ID. This ID corresponds to the "id" field returned by the <a href="/apidocs/endpoints/crm/clients/list">List Clients</a> endpoint, <em>not</em> the "customer_id" field.
			</td>
		</tr>
		<tr>
			<td><code>start</code></td>
			<td>0</td>
			<td></td>
		</tr>
		<tr>
			<td><code>limit</code></td>
			<td>25</td>
			<td></td>
		</tr>
		<tr>
			<td><code>sort_dir</code></td>
			<td>ASC</td>
			<td>
				Sort direction (either <code>ASC</code> or <code>DESC</code>, default is <code>ASC</code>).  Sort is always based on assignment start date/time.
			</td>
		</tr>
	</tbody>
</table>

<h3>Response fields</h3>
<table>
	<tbody>
		<tr>
			<td><code>total_results</code></td>
			<td>Total number of rows in the result set.</td>
		</tr>
		<tr>
			<td><code>count</code></td>
			<td>Total number of rows returned.</td>
		</tr>
		<tr>
			<td><code>start</code></td>
			<td>Row number to begin fetching results from.</td>
		</tr>
		<tr>
			<td><code>limit</code></td>
			<td>Total number of results to return per call. Max is 25 rows.</td>
		</tr>
		<tr>
			<td><code>data</code></td>
			<td>The list of assignments.</td>
		</tr>
	</tbody>
</table>

<h4>Row Data</h4>
<table>
	<tbody>
		<tr>
			<td><code>id</code></td>
			<td>Assignment Number</td>
		</tr>
		<tr>
			<td><code>title</code></td>
			<td>Assignment Title</td>
		</tr>
		<tr>
			<td><code>scheduled_start</code></td>
			<td>Assignment Start</td>
		</tr>
		<tr>
			<td><code>scheduled_end</code></td>
			<td>End of assignment window of time</td>
		</tr>
		<tr>
			<td><code>city</code></td>
			<td>Location city</td>
		</tr>
		<tr>
			<td><code>state</code></td>
			<td>Location state/province</td>
		</tr>
		<tr>
			<td><code>postal_code</code></td>
			<td>Location postal code</td>
		</tr>
		<tr>
			<td><code>location_id</code></td>
			<td>ID of the location</td>
		</tr>
		<tr>
			<td><code>spend_limit</code></td>
			<td>Max price of the assignment</td>
		</tr>
		<tr>
			<td><code>modified_status</code></td>
			<td>Displayable Status</td>
		</tr>
		<tr>
			<td><code>status</code></td>
			<td>Assignment Status. See <a href="/apidocs/endpoints/assignments/statuses">Assignments &raquo; Statuses</a></td>
		</tr>
		<tr>
			<td><code>substatuses</code></td>
			<td>Sub statuses associated with the assignment <span class="label important">deprecated</span></td>
		</tr>
		<tr>
			<td><code>labels</code></td>
			<td>Labels associated with the assignment</td>
		</tr>
		<tr>
			<td><code>internal_owner</code></td>
			<td>Owner of the assignment</td>
		</tr>
		<tr>
			<td><code>client</code></td>
			<td>Company the assignment is for</td>
		</tr>
		<tr>
			<td><code>paid_date</code></td>
			<td>Date the assignment was paid</td>
		</tr>
		<tr>
			<td><code>total_cost</code></td>
			<td>Total cost of the assignment including WM Fees</td>
		</tr>
		<tr>
			<td><code>resource_company_name</code></td>
			<td>Company name of the worker assigned</td>
		</tr>
		<tr>
			<td><code>resource_user_number</code></td>
			<td>User number of the worker assigned</td>
		</tr>
		<tr>
			<td><code>resource_full_name</code></td>
			<td>Full name of the worker assigned</td>
		</tr>
		<tr>
			<td><code>last_modified_on</code></td>
			<td>Date the assignment was last modified</td>
		</tr>
		<tr>
			<td><code>modifier_first_name</code></td>
			<td>First name of the user who last modified the assignment</td>
		</tr>
		<tr>
			<td><code>modifier_last_name</code></td>
			<td>Last name of the user who last modified the assignment</td>
		</tr>
	</tbody>
</table>
