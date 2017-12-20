<h2>Contacts</h2>
<p><em>https://www.workmarket.com/api/v1/<strong>crm/contacts/list</strong></em></p>

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
			<td>Client ID</td>
		</tr>
	</tbody>
</table>

<h3>Response fields</h3>
<table>
	<tr>
		<td><code>response</code></td>
		<td>An array of objects representing saved contacts. Each contact contains an <code>id</code> such as <em>5</em> and a <code>name</code> such as <em>John Doe</em>.</td>
	</tr>
</table>