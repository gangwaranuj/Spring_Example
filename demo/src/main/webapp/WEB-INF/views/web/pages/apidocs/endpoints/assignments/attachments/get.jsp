<h2>Get Attachment</h2>
<p><em>https://www.workmarket.com/api/v1/<strong>assignments/attachments/get</strong></em></p>

<p>Get a specific assignment attachment</p>

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
			<td><code>uuid</code></td>
			<td>4cdfe530-a174-4552-a53a-a8377f8d4ab6</td>
			<td><span class="required"></span> Attachment unique identifier</td>
		</tr>
	</tbody>
</table>

<h3>Response fields</h3>
<table>
	<tr>
		<td><code>response</code></td>
		<td>An object with the fields <code>filename</code>, <code>size</code>, <code>description</code> and <code>attachment</code> which is the <a href="http://en.wikipedia.org/wiki/Base64">Base64 encoded</a> representation of the binary attachment data.</td>
	</tr>
</table>
