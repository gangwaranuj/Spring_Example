<h2>Cancel</h2>
<p><em>https://www.workmarket.com/api/v1/<strong>assignments/cancel</strong></em></p>
<p>Cancel an assignment</p>
<p><strong>Note:</strong> only assignments that are currently 'Active' or 'In Progress' can be cancelled.  Use 'delete' or 'void' for 'Draft', or 'void' for 'Sent' assignments.</p>

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
		<tr>
			<td><code>amount</code></td>
			<td>25.00</td>
			<td><span class="required"></span> Amount to pay worker</td>
		</tr>
		<tr>
			<td><code>reason</code></td>
			<td>personal_emergency</td>
			<td><span class="required"></span> Reason for the cancellation. One of <code>end_user_cancelled</code>, <code>resource_no_show</code>, <code>personal_emergency</code>, <code>other</code></td>
		</tr>
		<tr>
			<td><code>note</code></td>
			<td>This is a sample note</td>
			<td>This is the note content</td>
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
