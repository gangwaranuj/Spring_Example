<h2>Reschedule</h2>
<p><em>https://www.workmarket.com/api/v1/<strong>assignments/reschedule</strong></em></p>

<p>Request to reschedule an assignment.</p>

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
			<td><code>scheduled_start</code></td>
			<td>1310744363</td>
			<td><span class="required"></span> Start time of an assignment in <a href="http://en.wikipedia.org/wiki/Unix_time">Unix time</a>, <em>yyyy/MM/dd hh:mm a z</em> or <em>MM/dd/yyyy hh:mm a z</em> formats. <strong>Note:</strong> if you do not provide an explicit time zone, assignment local time will be assumed.  If assignment is virtual, your company local time zone will be used.</td>
		</tr>
		<tr>
			<td><code>scheduled_end</code></td>
			<td>1310745878</td>
			<td>If the schedule is a range, end time of an assignment in <a href="http://en.wikipedia.org/wiki/Unix_time">Unix time</a>, <em>yyyy/MM/dd hh:mm a z</em> or <em>MM/dd/yyyy hh:mm a z</em> format. <strong>Note:</strong> if you do not provide an explicit time zone, assignment local time will be assumed.  If assignment is virtual, your company local time zone will be used.</td>
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
