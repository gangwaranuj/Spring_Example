<h2>Budget Increase</h2>
<p><em>https://www.workmarket.com/api/v1/<strong>assignments/{id}/pricing/budget_increase</strong></em></p>
<p><strong>Note</strong>: the assignment ID is passed in the URL, not as a body parameter.</p>
<p>Increase the budget for an assignment.</p>
<p>Use this endpoint to increase the budget for an assignment.  Note that the total assignment budget can only be modified upwards, not downwards, using this endpoint.</p>

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
			<td><code>flat_price</code></td>
			<td>125.00</td>
			<td> The new price for flat price assignments.</td>
		</tr>
		<tr>
			<td><code>max_number_of_hours</code></td>
			<td>3</td>
			<td> The new maximum number of billable hours for hourly price assignments.</td>
		</tr>
		<tr>
			<td><code>max_blended_number_of_hours</code></td>
			<td>4</td>
			<td> The new maximum number of <strong>additional</strong> hours (at the secondary rate) for blended per hour assignments.</td>
		</tr>
		<tr>
			<td><code>max_number_of_units</code></td>
			<td>5</td>
			<td> The new maximum number of units for price per unit assignments.</td>
		</tr>
	</tbody>
</table>

<h3>Response fields</h3>
<table>
	<tr>
		<td><code>successful</code></td>
		<td><code>true</code> or <code>false</code></td>
	</tr>
</table>
