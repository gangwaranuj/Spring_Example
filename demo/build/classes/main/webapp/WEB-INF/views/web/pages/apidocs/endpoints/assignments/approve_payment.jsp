<h2>Approve Assignment for Payment</h2>
<p><em>https://www.workmarket.com/api/v1/<strong>assignments/approve_payment</strong></em></p>

<p>Transitions the assignment from "Pending Approval" status to either "Paid" or "Invoices", conditional on the payment terms of the assignment.</p>

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
			<td><code>id</code></td>
			<td>3198792069</td>
			<td><span class="required"></span> Assignment number</td>
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
