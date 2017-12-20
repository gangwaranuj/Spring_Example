<h2>Labels</h2>
<p><em>https://www.workmarket.com/api/v1/<strong>assignments/labels/list</strong></em></p>

<p>Retrieve a list of available labels</p>

<table>
	<tr>
		<td>HTTP Method</td>
		<td><code>GET</code></td>
	</tr>
	<tr>
		<td>Requires Authentication</td>
		<td>No</td>
	</tr>
</table>

<h3>Response fields</h3>
<table>
	<tr>
		<td><code>response</code></td>
		<td>An array of objects representing labels. Each label contains an <code>id</code>, a <code>code</code> such as <em>parts_back_ordered</em> and a human readable <code>name</code> such as <em class="ital">Parts on back order</em>.</td>
	</tr>
</table>
