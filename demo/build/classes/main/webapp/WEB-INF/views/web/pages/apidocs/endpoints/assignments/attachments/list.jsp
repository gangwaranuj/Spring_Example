<h2>List Attachments</h2>
<p><em>https://www.workmarket.com/api/v1/<strong>assignments/attachments/list</strong></em></p>

<p>List the attachments of an assignment.</p>

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

<h3>Parameters</h3>
<table>
	<tbody>
		<tr>
			<td><code>id</code></td>
			<td>3198792069</td>
			<td><span class="required"></span> Assignment number</td>
		</tr>
		<tr>
			<td><code>closeout</code></td>
			<td>1</td>
			<td>Flag indicating that only <em>closeout</em> attachments should be listed. Accepts <code>1</code> or <code>0</code></td>
		</tr>
	</tbody>
</table>

<h3>Response fields</h3>
<table>
	<tr>
		<td><code>response</code></td>
		<td>An array of objects representing attachments. Each attachment contains a <code>uuid</code> and the <code>filename</code> of the attachment.</td>
	</tr>
</table>
