<h2>Add Attachment</h2>
<p><em>https://www.workmarket.com/api/v1/<strong>assignments/attachments/add</strong></em></p>

<p>Add an attachment to an assignment</p>

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
			<td><code>attachment</code></td>
			<td>
				<pre>
R0lGODlhUAAPAKIAAAsLav///88PD9WqsYmApm
ZmZtZfYmdakyH5BAQUAP8ALAAAAABQAA8AAAPb
WLrc/jDKSVe4OOvNu/9gqARDSRBHegyGMahqO4
R0bQcjIQ8E4BMCQc930JluyGRmdAAcdiigMLVr
ApTYWy5FKM1IQe+Mp+L4rphz+qIOBAUYeCY4p2
tGrJZeH9y79mZsawFoaIRxF3JyiYxuHiMGb5KT
kpFvZj4ZbYeCiXaOiKBwnxh4fnt9e3ktgZyHhr
ChinONs3cFAShFF2JhvCZlG5uchYNun5eedRxM
AF15XEFRXgZWWdciuM8GCmdSQ84lLQfY5R14wD
B5Lyon4ubwS7jx9NcV9/j5+g4JADs=
</pre>
			</td>
			<td><span class="required"></span> This is the <a href="http://en.wikipedia.org/wiki/Base64">Base64 encoded</a> binary attachment data</td>
		</tr>
		<tr>
			<td><code>filename</code></td>
			<td>This is a sample note</td>
			<td><span class="required"></span> The filename of the attachment</td>
		</tr>

		<tr>
			<td><code>description</code></td>
			<td>Complete work documention</td>
			<td>Descriptive text of the attachment</td>
		</tr>
	</tbody>
</table>

<h3>Response fields</h3>
<table>
	<tr>
		<td><code>uuid</code></td>
		<td>The unique identifier of the newly created attachment</td>
	</tr>
</table>
