<h2>Projects</h2>
<p><em>https://www.workmarket.com/api/v1/<strong>projects/list</strong></em></p>

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

<h3>Parameters <small>All parameters are optional, unless otherwise indicated.</small></h3>
<table>
	<tbody>
		<tr>
			<td><code>client_id</code></td>
			<td>12345</td>
			<td><span class="required"></span> Client ID</td>
		</tr>
	</tbody>
</table>

<h3>Response fields</h3>
<table>
	<tr>
		<td><code>response</code></td>
		<td>An array of objects representing projects. Each project contains an <code>id</code> such as <em>5</em> and a <code>name</code> such as <em class="ital">National POS Rollout</em>.</td>
	</tr>
</table>