<h2>Add Client</h2>
<p><em>https://www.workmarket.com/api/v1/<strong>addressbook/clients/add</strong></em></p>

<p>Create a new client and add it to the location manager.</p>

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
			<td><code>company_name</code></td>
			<td>Work Market</td>
			<td><span class="required"></span> The client's name</td>
		</tr>
		<tr>
			<td><code>industry_name</code></td>
			<td>Labor Management</td>
			<td></td>
		</tr>
		<tr>
			<td><code>customer_id</code></td>
			<td>WM</td>
			<td>The client identifier for integration with external systems</td>
		</tr>
		<tr>
			<td><code>region</code></td>
			<td>East coast</td>
			<td></td>
		</tr>
		<tr>
			<td><code>division</code></td>
			<td>Accounting</td>
			<td></td>
		</tr>
	</tbody>
</table>

<h4>Phone Numbers <small>Note: although this parameter is in array format, only 1 phone number can be passed in.</small></h4>
<table>
	<tbody>
		<tr>
			<td><code>phones[0]</code></td>
			<td>2122299675</td>
			<td></td>
		</tr>
		<tr>
			<td><code>phones[0][ext]</code></td>
			<td></td>
			<td></td>
		</tr>
	</tbody>
</table>

<h4>Websites <small>Note: although this parameter is in array format, only 1 website can be passed in.</small></h4>
<table>
	<tbody>
		<tr>
			<td><code>websites[0]</code></td>
			<td>http://workmarket.com</td>
			<td></td>
		</tr>
		<tr>
			<td><code>websites[0][type]</code></td>
			<td>work</td>
			<td>One of <code>work</code>, <code>home</code>, <code>other</code></td>
		</tr>
	</tbody>
</table>

<h3>Response fields</h3>
<table>
	<tr>
		<td><code>id</code></td>
		<td>The new client's unique identifier</td>
	</tr>
</table>
