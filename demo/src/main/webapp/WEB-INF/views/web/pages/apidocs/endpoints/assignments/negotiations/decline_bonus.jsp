<h2>Decline Bonus</h2>
<p><em>https://www.workmarket.com/api/v1/<strong>assignments/decline_bonus</strong></em></p>

<p>Decline a bonus request.</p>

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
			<td><code>id</code></td>
			<td>3198792069</td>
			<td><span class="required"></span> Assignment number</td>
		</tr>
		<tr>
			<td><code>reason</code></td>
			<td>This is a sample note</td>
			<td>A note explaining why the bonus was declined.</td>
		</tr>
	</tbody>
</table>

<h3>Response fields</h3>
<table>
	<tr>
		<td><code>successful</code></td>
		<td><code>true</code> or <code>false</code></td>
	</tr>
</table>
