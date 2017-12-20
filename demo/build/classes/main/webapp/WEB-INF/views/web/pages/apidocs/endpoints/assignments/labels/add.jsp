<h2>Add Label</h2>
<p><em>https://www.workmarket.com/api/v1/<strong>assignments/labels/add</strong></em></p>

<p>Add a label to an assignment</p>

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
			<td><code>substatus_id</code></td>
			<td>2</td>
			<td><span class="required"></span> The substatus identifier</td>
		</tr>
		<tr>
			<td><code>note</code></td>
			<td>This is a sample note</td>
			<td>Include a message.  <strong>Note:</strong> depending on the configuration of the label, note may be required.</td>
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
