<h2>Spend Limit Increase <span class="label important">deprecated</span></h2>
<p><em>https://www.workmarket.com/api/v1/<strong>assignments/increase_spendlimit</strong></em></p>

<p>Spend Limit Increase</p>

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
			<td><code>type</code></td>
			<td>flat</td>
			<td><span class="required"></span>  One of <code>flat</code>, <code>per_hour</code>, <code>per_unit</code> or <code>blended_per_hour</code></td>
		</tr>
		<tr>
			<td><code>flat_price</code></td>
			<td>100.00</td>
			<td></td>
		</tr>
		<tr>
			<td><code>per_hour_price</code></td>
			<td>10.00</td>
			<td></td>
		</tr>
		<tr>
			<td><code>max_number_of_hours</code></td>
			<td>5</td>
			<td></td>
		</tr>
		<tr>
			<td><code>per_unit_price</code></td>
			<td>10.00</td>
			<td></td>
		</tr>
		<tr>
			<td><code>max_number_of_units</code></td>
			<td>5</td>
			<td></td>
		</tr>
		<tr>
			<td><code>initial_per_hour_price</code></td>
			<td>10.00</td>
			<td></td>
		</tr>
		<tr>
			<td><code>initial_number_of_hours</code></td>
			<td>5</td>
			<td></td>
		</tr>
		<tr>
			<td><code>additional_per_hour_price</code></td>
			<td>15.00</td>
			<td></td>
		</tr>
		<tr>
			<td><code>max_blended_number_of_hours</code></td>
			<td>10</td>
			<td></td>
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
