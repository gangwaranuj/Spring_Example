<h2>Void</h2>
<p><em>https://www.workmarket.com/api/v1/<strong>assignments/void</strong></em></p>

<p>Void an assignment</p>

<p><strong>Note:</strong> only assignments that are currently in 'Draft' or 'Sent' status can be voided.  Use 'cancel' for 'Assigned' or 'In Progress' assignments.</p>

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
