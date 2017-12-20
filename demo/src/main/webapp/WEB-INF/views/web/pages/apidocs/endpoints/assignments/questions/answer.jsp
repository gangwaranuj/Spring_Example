<h2>Answer Question</h2>
<p><em>https://www.workmarket.com/api/v1/<strong>assignments/questions/answer</strong></em></p>

<p>Answer a question on an assignment</p>

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
			<td><span class="required"></span> Question ID</td>
		</tr>
		<tr>
			<td><code>answer</code></td>
			<td>This is a sample answer</td>
			<td><span class="required"></span> This is the answer content</td>
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
