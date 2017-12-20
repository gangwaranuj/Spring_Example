<h2>Add Contact to Client</h2>
<p><em>https://www.workmarket.com/api/v1/<strong>addressbook/clients/contacts/add</strong></em></p>

<p>Create a new contact and add to an existing client.</p>

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
			<td><code>client_id</code></td>
			<td></td>
			<td><span class="required"></span> The client identifier</td>
		</tr>
		<tr>
			<td><code>first_name</code></td>
			<td></td>
			<td></td>
		</tr>
		<tr>
			<td><code>last_name</code></td>
			<td></td>
			<td></td>
		</tr>
		<tr>
			<td><code>title</code></td>
			<td></td>
			<td></td>
		</tr>
		<tr>
			<td><code>manager</code></td>
			<td>1</td>
			<td>Flag indicating that the user is a manager. Accepts <code>1</code> or <code>0</code></td>
		</tr>
	</tbody>
</table>

<h4>Emails <small>Note: although this parameter is in array format, only 1 email can be passed in.. Note: indexes (i.e. "N") are zero-based.</small></h4>
<table>
	<tbody>
		<tr>
			<td><code>emails[0]</code></td>
			<td>contact@workmarket.com</td>
			<td></td>
		</tr>
	</tbody>
</table>

<h4>Phone Numbers <small>A contact can have one <code>work</code> and one <code>home</code> number at most. Note: indexes (i.e. "N") are zero-based.</small></h4>
<table>
	<tbody>
		<tr>
			<td><code>phones[<em>&lt;N&gt;</em>]</code></td>
			<td>2122299675</td>
			<td></td>
		</tr>
		<tr>
			<td><code>phones[<em>&lt;N&gt;</em>][ext]</code></td>
			<td></td>
			<td></td>
		</tr>
		<tr>
			<td><code>phones[<em>&lt;N&gt;</em>][type]</code></td>
			<td>work</td>
			<td>One of <code>work</code>, <code>home</code>, <code>other</code></td>
		</tr>
	</tbody>
</table>

<h3>Response fields</h3>
<table>
	<tr>
		<td><code>id</code></td>
		<td>The new contact's unique identifier</td>
	</tr>
</table>
