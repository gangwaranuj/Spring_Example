<h2>Assignment Details</h2>
<p><em>https://www.workmarket.com/api/v1/<b>assignments/get</b></em></p>

<p>Get assignment details</p>

<table>
	<tr>
		<td>HTTP Method</td>
		<td><code>GET</code></td>
	</tr>
	<tr>
		<td>Requires Authentication</td>
		<td>Yes</td>
	</tr>
</table>

<h3>Parameters</h3>
<p>All parameters are optional, unless otherwise indicated.</p>

<table>
	<tbody>
		<tr>
			<td><code>id</code></td>
			<td>6029116324</td>
			<td><span class="required"></span> Assignment ID</td>
		</tr>
	</tbody>
</table>

<h3>Response fields</h3>
<table>
	<tbody>
		<tr>
			<td><code>id</code></td>
			<td>Assignment number</td>
		</tr>
		<tr>
			<td><code>title</code></td>
			<td>Assignment title</td>
		</tr>
		<tr>
			<td><code>description</code></td>
			<td>Assignment description</td>
		</tr>
		<tr>
			<td><code>instructions</code></td>
			<td>Assignment instructions</td>
		</tr>
		<tr>
			<td><code>desired_skills</code></td>
			<td>Desired skills for a worker</td>
		</tr>
		<tr>
			<td><code>industry</code></td>
			<td>Assignment industry. See <a href="/apidocs/endpoints/constants/industries">Constants &raquo; Industries</a></td>
		</tr>
		<tr>
			<td><code>short_url</code></td>
			<td>Shortened URL useful for sharing</td>
		</tr>
		<tr>
			<td><code>status</code></td>
			<td>Assignment's current status. See <a href="/apidocs/endpoints/assignments/statuses">Assignments &raquo; Statuses</a></td>
		</tr>
		<tr>
			<td><code>substatuses</code></td>
			<td>Sub statuses associated with the assignment <span class="label important">deprecated</span></td>
		</tr>
		<tr>
			<td><code>labels</code></td>
			<td>Labels associated with the assignment</td>
		</tr>
		<tr>
			<td><code>project</code></td>
			<td>Associated project</td>
		</tr>
		<tr>
			<td><code>client</code></td>
			<td>Company the assignment is for</td>
		</tr>
		<tr>
			<td><code>internal_owner</code></td>
			<td>Owner of the assignment</td>
		</tr>
		<tr>
			<td><code>assignment_window_start</code></td>
			<td>Assignment start date and time</td>
		</tr>
		<tr>
			<td><code>assignment_window_start_date</code></td>
			<td>Assignment start date and time in readable form</td>
		</tr>
		<tr>
			<td><code>assignment_window_end</code></td>
			<td>End of scheduled window</td>
		</tr>
		<tr>
			<td><code>assignment_window_end_date</code></td>
			<td>Assignment end date and time in readable form</td>
		</tr>
		<tr>
			<td><code>scheduled_time</code></td>
			<td>Appointment time within a scheduled window</td>
		</tr>
		<tr>
			<td><code>scheduled_time_date</code></td>
			<td>Appointment time within a scheduled window date in readable form</td>
		</tr>
		<tr>
			<td><code>scheduled_start</code></td>
			<td>Assignment start date and time <span class="label important">deprecated</span></td>
		</tr>
		<tr>
			<td><code>scheduled_end</code></td>
			<td>End of scheduled window <span class="label important">deprecated</span></td>
		</tr>
		<tr>
			<td><code>reschedule_request</code></td>
			<td>Included if there is a pending reschedule request from the assigned worker.  This object contains <code>request_scheduled_start</code> and <code>request_scheduled_end</code> if a time window has been proposed, otherwise <code>request_scheduled_time</code>. Also contains <code>note</code>, <code>requested_by_resource</code>, and <code>requested_on</code>.</td>
		</tr>
		<tr>
			<td><code>time_zone</code></td>
			<td>Time zone in which the assignment is occurring</td>
		</tr>
		<tr>
			<td><code>resolution</code></td>
			<td>Message provided by the worker at time of completion</td>
		</tr>
		<tr>
			<td><code>required_attachments</code></td>
			<td>Number of required attachments</td>
		</tr>
		<tr>
			<td><code>location</code></td>
			<td>An object containing an <code>id</code></td>
		</tr>
		<tr>
			<td><code>location_contact</code></td>
			<td>An array containing user objects with a <code>first_name</code>, <code>last_name</code>, <code>email</code>, and an array of <code>phone_numbers</code> with <code>phone</code>, <code>extension</code> and <code>type</code></td>
		</tr>
		<tr>
			<td><code>support_contact</code></td>
			<td>An array containing user objects with a <code>first_name</code>, <code>last_name</code>, <code>email</code>, and an array of <code>phone_numbers</code> with <code>phone</code>, <code>extension</code> and <code>type</code></td>
		</tr>
		<tr>
			<td><code>active_resource</code></td>
			<td>An object containing <code>id</code>, <code>first_name</code>, <code>last_name</code>, <code>company_name</code>, <code>email</code>, an array of <code>phone_numbers</code> with <code>phone</code>, <code>extension</code> and <code>type</code>, <code>address</code> with <code>address1</code>, <code>address2</code>, <code>city</code>, <code>state</code>, <code>zip</code> and <code>country</code>, <code>confirmed_onsite</code>, <code>confirmed_date</code>, an array of <code>check_in_out</code> object with <code>checked_in_on</code> and <code>checked_out_on</code>, <code>rating</code> and <code>number_of_ratings</code></td>
		</tr>
		<tr>
			<td><code>pricing</code></td>
			<td>An object containing <code>type</code>, <code>spend_limit</code>, <code>budget_increases</code>, <code>expense_reimbursements</code>, <code>bonuses</code> and <code>additional_expenses</code>. Additionally contains some of the following, conditional on the <code>type</code> of pricing: <code>flat_price</code>, <code>per_hour_price</code>, <code>max_number_of_hours</code>, <code>per_unit_price</code>, <code>max_number_of_units</code>, <code>initial_per_hour_price</code>, <code>initial_number_of_hours</code>, <code>additional_per_hour_price</code> and <code>max_blended_number_of_hours</code></td>
		</tr>
		<tr>
			<td><code>payment</code></td>
			<td>An object containing <code>max_spend_limit</code>, <code>actual_spend_limit</code>, <code>buyer_fee</code>, <code>total_cost</code>, <code>hours_worked</code>, <code>paid_on</code> and <code>payment_due_on</code></td>
		</tr>
		<tr>
			<td><code>attachments</code></td>
			<td>An array of attachment objects containing <code>name</code>, <code>description</code> and <code>relative_uri</code></td>
		</tr>
		<tr>
			<td><code>history</code></td>
			<td>An array of objects containing <code>date</code> and <code>text</code>. For history events that include the addition or removal of a label, additional fields include <code>label_id</code>, <code>label_name</code> and either <code>set_by</code> or <code>resolved_by</code>.</td>
		</tr>
		<tr>
			<td><code>custom_fields</code></td>
			<td>An object containing <code>id</code>, <code>name</code> and an array of <code>fields</code>. <code>fields</code> contains custom field objects with an <code>id</code>, <code>name</code>, <code>value</code>, <code>default</code> and <code>required</code></td>
		</tr>
		<tr>
			<td><code>notes</code></td>
			<td>An array of objects containing <code>date</code>, <code>text</code>, <code>is_private</code>and <code>created_by</code></td>
		</tr>
		<tr>
			<td><code>pending_offers</code></td>
			<td>An array of objects containing <code>id</code>, an array for the <code>resource</code>, and optionally <code>note</code>, a <code>scheduling</code> object, a <code>pricing</code> object, <code>expires_on</code>, and <code>expired</code>.
				<br/><br/>
				<code>scheduling</code> will contain <code>request_window_start</code> and <code>request_window_end</code> if a time window has been proposed, otherwise <code>request_scheduled_time</code>.
				<br/><br/>
				<code>pricing</code> will be of the same format as the <code>pricing</code> object on the assignment.
				<br/><br/>
				<code>resource</code> will contain <code>id</code>, <code>first_name</code>, <code>last_name</code>, <code>email</code>, <code>company_name</code>, <code>rating</code>, <code>number_of_ratings</code>, and an array of <code>phone_numbers</code> containing <code>country_code</code>, <code>phone</code>, <code>extension</code>, <code>type</code>.</td>
		</tr>
		<tr>
			<td><code>declined_resources</code></td>
			<td>An array of objects containing <code>id</code>, <code>first_name</code>, <code>last_name</code>, <code>email</code>, and an array of <code>phone_numbers</code> containing <code>country_code</code>, <code>phone</code>, <code>extension</code>, <code>type</code>.</td>
		</tr>
		<tr>
			<td><code>questions</code></td>
			<td>An array of objects containing <code>id</code>, <code>question</code>, and <code>answer</code> if it exists.</td>
		</tr>
	</tbody>
</table>
