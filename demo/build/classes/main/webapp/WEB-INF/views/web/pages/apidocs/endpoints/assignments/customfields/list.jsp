<h2>Custom Fields List</h2>
<p><em>https://www.workmarket.com/api/v1/<strong>assignments/customfields/list</strong></em></p>

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

<h3>Response fields <small>Returns an array of objects with the following fields:</small></h3>
<table>
	<tr>
		<td><code>id</code></td>
		<td>Custom field group identifier</td>
	</tr>
	<tr>
		<td><code>name</code></td>
		<td>Custom field group name</td>
	</tr>
	<tr>
		<td><code>required</code></td>
		<td>Custom field group required flag</td>
	</tr>
	<tr>
		<td><code>fields</code></td>
		<td>List of the custom field objects. Each custom field contains an <code>id</code>, <code>name</code>, a <code>default</code> value and a boolean flag <code>required</code></td>
	</tr>
</table>
