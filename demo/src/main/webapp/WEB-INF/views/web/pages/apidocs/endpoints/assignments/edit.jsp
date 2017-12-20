<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core" %>
<h2>Edit Assignment</h2>
<p><em>https://www.workmarket.com/api/v1/<strong>assignments/{id}/edit</strong></em></p>

<p><strong>Note</strong>: the assignment ID is passed in the URL, not as a body parameter.</p>

<p>Edit an existing assignment in Draft or Sent status.  <strong>Important</strong>: custom fields, parts, and location contacts are created from scratch if you edit them- existing values will be removed.</p>

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

<h3>Parameters <small>All parameters are optional, unless otherwise indicated.</small></h3>
<table>
	<tbody>
		<tr>
			<td><code>title</code></td>
			<td>API Demo</td>
			<td>Assignment's title</td>
		</tr>
		<tr>
			<td><code>description</code></td>
			<td>Demonstrate API to developers</td>
			<td>Assignment's description</td>
		</tr>
		<tr>
			<td><code>instructions</code></td>
			<td>Read the documentation</td>
			<td></td>
		</tr>
		<tr>
			<td><code>desired_skills</code></td>
			<td>HTTP, REST, JSON</td>
			<td></td>
		</tr>
		<tr>
			<td><code>industry_id</code></td>
			<td>1000</td>
			<td>Industry ID</td>
		</tr>
		<tr>
			<td><code>owner_id</code></td>
			<td>8823578</td>
			<td>The assignment owner's Work Market user number</td>
		</tr>
		<tr>
			<td><code>owner_email</code></td>
			<td>you@company.com</td>
			<td>The assignment owner's Work Market login / email address. Can be used instead of <code>owner_id</code>.</td>
		</tr>
		<tr>
			<td><code>support_contact_id</code></td>
			<td>2627769</td>
			<td>The support contact's Work Market user number</td>
		</tr>
		<tr>
			<td><code>support_contact_email</code></td>
			<td>you@workmarket.com</td>
			<td>The support contact's Work Market login / email address. Can be used instead of <code>support_contact_id</code>.</td>
		</tr>
		<tr>
			<td><code>client_id</code></td>
			<td>1038</td>
			<td>The client identifier</td>
		</tr>
		<tr>
			<td><code>project_id</code></td>
			<td>1005</td>
			<td>Project ID</td>
		</tr>
	</tbody>
</table>

<h4>Schedule <small>Use whichever of the two listed formats you prefer to set schedule info.  Either Unix Time or human-readable. You do not need to provide both.</small></h4>
<table>
	<tbody>
		<tr>
			<td><code>scheduled_start</code></td>
			<td>1310744363</td>
			<td>Start time of an assignment in <a href="http://en.wikipedia.org/wiki/Unix_time">Unix time</a></td>
		</tr>
		<tr>
			<td><code>scheduled_start_date</code></td>
			<td>2012/12/24 4:00 PM EST</td>
			<td>Start time of an assignment in <em>yyyy/MM/dd hh:mm a z</em> or <em>MM/dd/yyyy hh:mm a z</em> format. <strong>Note:</strong> if you do not provide an explicit time zone, assignment local time will be assumed.  If assignment is virtual, your company local time zone will be used.</td>
		</tr>
		<tr>
			<td><code>scheduled_end</code></td>
			<td>1310745878</td>
			<td>If the schedule is a range, end time of an assignment in <a href="http://en.wikipedia.org/wiki/Unix_time">Unix time</a></td>
		</tr>
		<tr>
			<td><code>scheduled_end_date</code></td>
			<td>2012/12/25 4:00 PM GMT-05:00</td>
			<td>If the schedule is a range, end time of an assignment in <em>yyyy/MM/dd hh:mm a z</em>  or <em>MM/dd/yyyy hh:mm a z</em> format. <strong>Note:</strong> if you do not provide an explicit time zone, assignment local time will be assumed.  If assignment is virtual, your company local time zone will be used.</td>
		</tr>
	</tbody>
</table>

<h4>Pricing</h4>
<table>
	<tbody>
		<tr>
			<td><code>pricing_mode</code></td>
			<td>pay</td>
			<td>One of <code>spend</code> or <code>pay</code></td>
		</tr>
		<tr>
			<td><code>pricing_type</code></td>
			<td>internal</td>
			<td><span class="required"></span> One of <code>flat</code>, <code>per_hour</code>, <code>per_unit</code>, <code>blended_per_hour</code> or <code>internal</code></td>
		</tr>
		<tr>
			<td><code>pricing_flat_price</code></td>
			<td>100.00</td>
			<td></td>
		</tr>
		<tr>
			<td><code>pricing_per_hour_price</code></td>
			<td>10.00</td>
			<td></td>
		</tr>
		<tr>
			<td><code>pricing_max_number_of_hours</code></td>
			<td>5</td>
			<td></td>
		</tr>
		<tr>
			<td><code>pricing_per_unit_price</code></td>
			<td>10.00</td>
			<td></td>
		</tr>
		<tr>
			<td><code>pricing_max_number_of_units</code></td>
			<td>5</td>
			<td></td>
		</tr>
		<tr>
			<td><code>pricing_initial_per_hour_price</code></td>
			<td>10.00</td>
			<td></td>
		</tr>
		<tr>
			<td><code>pricing_initial_number_of_hours</code></td>
			<td>5</td>
			<td></td>
		</tr>
		<tr>
			<td><code>pricing_additional_per_hour_price</code></td>
			<td>15.00</td>
			<td></td>
		</tr>
		<tr>
			<td><code>pricing_max_blended_number_of_hours</code></td>
			<td>10</td>
			<td></td>
		</tr>
	</tbody>
</table>

<h4>Location</h4>
<table>
	<tbody>
		<tr>
			<td><code>location_offsite</code></td>
			<td>1</td>
			<td>Whether or not the location is at an offsite location.</td>
		</tr>
		<tr>
			<td><code>location_id</code></td>
			<td>1</td>
			<td>By passing an <code>id</code>, an existing location will be associated with this assignment; if set, all other location fields are ignored.</td>
		</tr>
		<tr>
			<td><code>location_name</code></td>
			<td>Work Market HQ</td>
			<td></td>
		</tr>
		<tr>
			<td><code>location_number</code></td>
			<td>WMHQ</td>
			<td></td>
		</tr>
		<tr>
			<td><code>location_address1</code></td>
			<td>123 Main Street</td>
			<td></td>
		</tr>
		<tr>
			<td><code>location_address2</code></td>
			<td>Suite 1234</td>
			<td></td>
		</tr>
		<tr>
			<td><code>location_city</code></td>
			<td>Anytown</td>
			<td></td>
		</tr>
		<tr>
			<td><code>location_state</code></td>
			<td>NY</td>
			<td>2-letter state code.</td>
		</tr>
		<tr>
			<td><code>location_zip</code></td>
			<td>12345</td>
			<td></td>
		</tr>
		<tr>
			<td><code>location_country</code></td>
			<td>USA</td>
			<td><span class="required"></span> 3-letter country code. One of <code>USA</code> or <code>CAN</code></td>
		</tr>
		<tr>
			<td><code>location_type</code></td>
			<td>1</td>
			<td><span class="required"></span> Required if manually creating location via API.<br/>
			See <a href="<c:url value="/apidocs/endpoints/constants/location_types"/>">Constants &raquo; Location Types</a></td>
		</tr>
	</tbody>
</table>

<h4>Location Contacts <small>An assignment can have as many as two location contacts. <strong>Important</strong>: setting location contact parameters will remove any previous location contact information on the assignment.  Indexes (i.e. "N") are zero-based.</small></h4>
<table>
	<tbody>
		<tr>
			<td><code>location_contacts[<em>&lt;N&gt;</em>][id]</code></td>
			<td>1</td>
			<td>By passing an <code>id</code>, an existing contact will be associated with this assignment; if set, all other location contact fields are ignored.</td>
		</tr>
		<tr>
			<td><code>location_contacts[<em>&lt;N&gt;</em>][first_name]</code></td>
			<td>Shiba</td>
			<td></td>
		</tr>
		<tr>
			<td><code>location_contacts[<em>&lt;N&gt;</em>][last_name]</code></td>
			<td>Mayes</td>
			<td></td>
		</tr>
		<tr>
			<td><code>location_contacts[<em>&lt;N&gt;</em>][email]</code></td>
			<td>shiba@workmarket.com</td>
			<td></td>
		</tr>
		<tr>
			<td><code>location_contacts[<em>&lt;N&gt;</em>][phone]</code></td>
			<td>2125559663</td>
			<td></td>
		</tr>
		<tr>
			<td><code>location_contacts[<em>&lt;N&gt;</em>][phone_extension]</code></td>
			<td>123</td>
			<td></td>
		</tr>
	</tbody>
</table>

<h4>Custom Fields <small>An assignment can have more than custom field group. <strong>Important</strong>: setting custom fields parameters will remove any previous custom fields information on the assignment.  Indexes (i.e. "N") are zero-based.</small></h4>
<table>
	<tbody>
		<tr>
			<td><code>custom_field_groups[<em>&lt;N&gt;</em>][id]</code></td>
			<td>1</td>
			<td>Custom field group identifier</td>
		</tr>
		<tr>
			<td><code>custom_field_groups[<em>&lt;N&gt;</em>][fields][<em>&lt;N&gt;</em>][id]</code></td>
			<td>1</td>
			<td>Custom field identifier</td>
		</tr>
		<tr>
			<td><code>custom_field_groups[<em>&lt;N&gt;</em>][fields][<em>&lt;N&gt;</em>][value]</code></td>
			<td>Some value</td>
			<td>Custom field value</td>
		</tr>
	</tbody>
</table>


<h4>Parts Logistics <small><strong>Important</strong>: setting parts parameters will remove any previous parts information on the assignment.</small></h4>
<table>
	<tbody>
		<tr>
			<td><code>parts[supplied_by_resource]</code></td>
			<td>1</td>
			<td>Whether or not the part is provided by the worker.</td>
		</tr>
		<tr>
			<td><code>parts[distribution_method]</code></td>
			<td>shipped</td>
			<td>How will the parts be distributed. One of <code>shipped</code>, <code>onsite</code>, <code>pickup</code>. <strong>Note:</strong> required if parts[supplied_by_resource] is 1.</td>
		</tr>
		<tr>
			<td><code>parts[pickup_location_id]</code></td>
			<td>1</td>
			<td>By passing an <code>id</code>, an existing location will be associated with this assignment; if set, all other location fields are ignored.</td>
		</tr>
		<tr>
			<td><code>parts[pickup_location_name]</code></td>
			<td>Work Market HQ</td>
			<td></td>
		</tr>
		<tr>
			<td><code>parts[pickup_location_number]</code></td>
			<td>WMHQ</td>
			<td></td>
		</tr>
		<tr>
			<td><code>parts[pickup_location_address1]</code></td>
			<td>123 Main Street</td>
			<td></td>
		</tr>
		<tr>
			<td><code>parts[pickup_location_address2]</code></td>
			<td>Suite 1234</td>
			<td></td>
		</tr>
		<tr>
			<td><code>parts[pickup_location_city]</code></td>
			<td>Anytown</td>
			<td></td>
		</tr>
		<tr>
			<td><code>parts[pickup_location_state]</code></td>
			<td>NY</td>
			<td>2-letter state code.</td>
		</tr>
		<tr>
			<td><code>parts[pickup_location_zip]</code></td>
			<td>12345</td>
			<td></td>
		</tr>
		<tr>
			<td><code>parts[pickup_location_country]</code></td>
			<td>USA</td>
			<td>3-letter country code. One of <code>USA</code> or <code>CAN</code></td>
		</tr>
		<tr>
			<td><code>parts[pickup_location_type]</code></td>
			<td>1</td>
			<td>See <a href="<c:url value="/apidocs/endpoints/constants/location_types"/>">Constants &raquo; Location Types</a></td>
		</tr>
		<tr>
			<td><code>parts[pickup_tracking_number]</code></td>
			<td>123456789</td>
			<td>Shipping provider&rsquo; tracking number</td>
		</tr>
		<tr>
			<td><code>parts[pickup_shipping_provider]</code></td>
			<td>fedex</td>
			<td>Shipping provider. One of <code>fedex</code>, <code>ups</code>, <code>dhl</code>, <code>other</code></td>
		</tr>
		<tr>
			<td><code>parts[pickup_part_value]</code></td>
			<td>500.00</td>
			<td>Value of the shipped parts.</td>
		</tr>
		<tr>
			<td><code>parts[return_required]</code></td>
			<td>1</td>
			<td>Whether or not the parts will be returned by the worker.</td>
		</tr>
		<tr>
			<td><code>parts[return_location_id]</code></td>
			<td>1</td>
			<td>By passing an <code>id</code>, an existing location will be associated with this assignment; if set, all other location fields are ignored.</td>
		</tr>
		<tr>
			<td><code>parts[return_location_name]</code></td>
			<td>Work Market HQ</td>
			<td></td>
		</tr>
		<tr>
			<td><code>parts[return_location_number]</code></td>
			<td>WMHQ</td>
			<td></td>
		</tr>
		<tr>
			<td><code>parts[return_location_address1]</code></td>
			<td>123 Main Street</td>
			<td></td>
		</tr>
		<tr>
			<td><code>parts[return_location_address2]</code></td>
			<td>Suite 1234</td>
			<td></td>
		</tr>
		<tr>
			<td><code>parts[return_location_city]</code></td>
			<td>Anytown</td>
			<td></td>
		</tr>
		<tr>
			<td><code>parts[return_location_state]</code></td>
			<td>NY</td>
			<td>2-letter state code.</td>
		</tr>
		<tr>
			<td><code>parts[return_location_zip]</code></td>
			<td>12345</td>
			<td></td>
		</tr>
		<tr>
			<td><code>parts[return_location_country]</code></td>
			<td>USA</td>
			<td>3-letter country code. One of <code>USA</code></td>
		</tr>
		<tr>
			<td><code>parts[return_location_type]</code></td>
			<td>1</td>
			<td>See <a href="<c:url value="/apidocs/endpoints/constants/location_types"/>">Constants &raquo; Location Types</a></td>
		</tr>
		<tr>
			<td><code>parts[return_tracking_number]</code></td>
			<td>123456789</td>
			<td>Shipping provider&rsquo; tracking number</td>
		</tr>
		<tr>
			<td><code>parts[return_shipping_provider]</code></td>
			<td>fedex</td>
			<td>Shipping provider. One of <code>fedex</code>, <code>ups</code>, <code>dhl</code>, <code>other</code></td>
		</tr>
		<tr>
			<td><code>parts[return_part_value]</code></td>
			<td>500.00</td>
			<td>Value of the returned parts.</td>
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
