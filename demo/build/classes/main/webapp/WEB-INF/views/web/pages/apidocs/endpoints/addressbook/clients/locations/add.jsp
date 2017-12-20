<h2>Add Location to Client</h2>
<p><em>https://www.workmarket.com/api/v1/<strong>addressbook/clients/locations/add</strong></em></p>

<p>Create a new location and add to an existing client.</p>

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
			<td>1038</td>
			<td><span class="required"></span> The client identifier</td>
		</tr>
		<tr>
			<td><code>location_name</code></td>
			<td>WM HQ</td>
			<td><span class="required"></span> </td>
		</tr>
		<tr>
			<td><code>address1</code></td>
			<td>20 West 20th Street</td>
			<td><span class="required"></span> </td>
		</tr>
		<tr>
			<td><code>address2</code></td>
			<td>Suite 402</td>
			<td></td>
		</tr>
		<tr>
			<td><code>city</code></td>
			<td>New York</td>
			<td><span class="required"></span> </td>
		</tr>
		<tr>
			<td><code>state</code></td>
			<td>NY</td>
			<td><span class="required"></span> 2-letter state code</td>
		</tr>
		<tr>
			<td><code>postal_code</code></td>
			<td>11238</td>
			<td><span class="required"></span> </td>
		</tr>
		<tr>
			<td><code>country</code></td>
			<td>USA</td>
			<td><span class="required"></span> 3-letter country code. One of <code>USA</code> or <code>CAN</code></td>
		</tr>
		<tr>
			<td><code>location_type</code></td>
			<td>1</td>
			<td><span class="required"></span> See <a href="/apidocs/endpoints/constants/location_types">Constants &raquo; Location Types</a></td>
		</tr>
		<tr>
			<td><code>location_number</code></td>
			<td>WM0001</td>
			<td></td>
		</tr>
	</tbody>
</table>

<h4>Location Contact <small>Optionally add a location contact as well</small></h4>
<table>
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

<h3>Response fields</h3>
<table>
	<tr>
		<td><code>id</code></td>
		<td>The new location's unique identifier</td>
	</tr>
</table>
