<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core" %>
<h2>Request Access Token</h2>
<p><em>https://www.workmarket.com/api/v1/<strong>authorization/request</strong></em></p>

<p>Generate a temporary access token.</p>

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
			<td><code>token</code></td>
			<td>JoWxXsK6Yd3hnUkuqkY5</td>
			<td><span class="required"></span> <a href="<c:url value="/mmw/api"/>">API token</a></td>
		</tr>
		<tr>
			<td><code>secret</code></td>
			<td>AwpF9SnersuscUS6ChPmjYemiIOWZaq7K34Ua7ud</td>
			<td><span class="required"></span> <a href="<c:url value="/mmw/api"/>">API secret</a></td>
		</tr>
	</tbody>
</table>

<h3>Response fields</h3>
<table>
	<tr>
		<td><code>access_token</code></td>
		<td>Access token used for all subsequent API requests</td>
	</tr>
</table>