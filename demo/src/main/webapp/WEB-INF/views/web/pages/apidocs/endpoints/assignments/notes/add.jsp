<h2>Add Note</h2>
<p><em>https://www.workmarket.com/api/v1/<strong>assignments/add_note</strong></em></p>

<p>Add a note to an assignment</p>

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
			<td><code>content</code></td>
			<td>This is a sample note</td>
			<td><span class="required"></span> This is the note content</td>
		</tr>
		<tr>
			<td><code>is_private</code></td>
			<td>1</td>
			<td>Flag indicating if this note is private or public. Accepts <code>1</code> or <code>0</code></td>
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
