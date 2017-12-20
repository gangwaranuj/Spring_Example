<h2>Complete (on behalf of worker)</h2>
<p><em>https://www.workmarket.com/api/v1/<strong>assignments/{id}/complete</strong></em></p>

<p><strong>Note</strong>: the assignment ID is passed in the URL, not as a body parameter.</p>

<p>Marks the assignment complete, as if the assigned worker submitted it for your approval.  You have the option to simultaneously approve the assignment for payment as well.</p>

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
		<td><code>resolution</code></td>
		<td>Successfully completed the work.</td>
		<td><span class="required"></span> A comment on the resolution of the assignment.</td>
	</tr>
	<tr>
		<td><code>hours_worked</code></td>
		<td>3.3</td>
		<td> (Hourly assignments only) The number of hours worked.  This provides the basis for the cost of hourly assignments.</td>
	</tr>
	<tr>
		<td><code>units</code></td>
		<td>4</td>
		<td> (Per Unit assignments only) The number of units processed.  This provides the basis for the cost of "per unit" assignments.</td>
	</tr>
	<tr>
		<td><code>override_additional_expenses</code></td>
		<td>150</td>
		<td> Use to override the current amount of approved expenses.  <strong>Note:</strong> can only provide a value that is <em>lower</em> than the current amount of approved expenses.</td>
	</tr>
	<tr>
		<td><code>override_price</code></td>
		<td>220</td>
		<td> Use to override the base price (i.e. excluding expenses and bonuses).  <strong>Note:</strong> can only provide a value that is <em>lower</em> than the current base price.</td>
	</tr>
	<tr>
		<td><code>tax_rate</code></td>
		<td>30</td>
		<td> The tax rate for this particular worker for this assignment, i.e. "30" for 30 percent.<strong>Note:</strong> the worker is responsible for tax obligations for services and materials.  No taxes will be deducted from worker earnings. At any time, workers can run an earnings report that includes tax obligation data.</td>
	</tr>
	<tr>
		<td><code>approve_payment</code></td>
		<td>1</td>
		<td> 1 or 0.  Set to 1 to simultaneously approve the assignment for payment.</td>
	</tr>
</table>

<h3>Response fields</h3>
<table>
	<tr>
		<td><code>successful</code></td>
		<td><code>true</code> or <code>false</code></td>
	</tr>
</table>
