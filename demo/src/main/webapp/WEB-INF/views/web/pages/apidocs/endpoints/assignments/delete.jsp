<h2>Void</h2>
<p><em>https://www.workmarket.com/api/v1/<strong>assignments/delete</strong></em></p>

<p>Delete an assignment</p>

<p><strong>Note:</strong> only assignments in 'Draft' status can be deleted.  Use 'void' for 'Draft' or 'Sent' assignments, and 'cancel' for 'Assigned'/'In Progress' assignments.</p>

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