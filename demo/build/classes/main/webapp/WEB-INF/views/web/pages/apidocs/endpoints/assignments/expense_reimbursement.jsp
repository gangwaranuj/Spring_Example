<h2>Expense Reimbursement</h2>
<p><em>https://www.workmarket.com/api/v1/<strong>assignments/{id}/pricing/expense_reimbursement</strong></em></p>

<p><strong>Note</strong>: the assignment ID is passed in the URL, not as a body parameter.</p>

<p>Add a new expense reimbursement to the assignment.</p>

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
			<td><code>amount</code></td>
			<td>75.00</td>
			<td><span class="required"></span> The amount of the expense to be reimbursed.  It is <strong>added</strong> to the current expense_reimbursements total, which is available in /assignments/get .</td>
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
