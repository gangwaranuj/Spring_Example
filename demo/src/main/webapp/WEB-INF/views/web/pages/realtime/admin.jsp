<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="wm" tagdir="/WEB-INF/tags" %>

<wm:app pagetitle="Realtime Admin" bodyclass="realtime admin" fluid="true" webpackScript="realtime">

	<script>
		var config = {
			'realtime': {
				name: 'realtime',
				features: {
					update_interval: 120000,
					count_interval: 120000,
					stagger_update: 5000,
					page_length: 25
				}
			}
		};
	</script>

	<div>
		<button type="button" id="toggle_all" class="button">Expand/Collapse Rows</button>
		<button type="button" id="toggle_filters" class="button">Show/Hide Filters</button>
	</div>

	<div id="realtime_counts">
		<div>
			<strong id="total_count">-</strong> Assignments on WM Realtime Right Now
		</div>

		<div>
			<strong>Today:</strong>
			<span><strong id="today_sent">-</strong> Sent</span>
			<span><strong id="today_created">-</strong> Created</span>
			<span><strong id="today_voided">-</strong> Voided</span>
			<span><strong id="today_cancelled">-</strong> Cancelled</span>
			<span><strong id="today_accepted">-</strong> Accepted</span>
			<span><strong id="gcc_bank_accounts">-</strong>WM Visa Cards</span>
		</div>
	</div>

	<form id="realtime-counts" action="/realtime/admin_counts" method="POST"></form>

	<form id="realtime-filters" action="/realtime/admin_update" method="POST">
		<div class="well">
			<div class="row">
				<div class="span5">
					<div class="control-group">
						<label class="control-label">Time to Appointment:</label>
						<div class="controls">
							<select name="filters[timetoappt][comparison]" class="span3">
								<option value="gt">Greater than</option>
								<option value="lt">Less than</option>
							</select>
							<select name="filters[timetoappt][value]" class="span2">
								<option value="">Select</option>
								<option value="1800">30m</option>
								<option value="3600">1hr</option>
								<option value="14400">4hr</option>
								<option value="28800">8hr</option>
								<option value="43200">12hr</option>
								<option value="86400">24hr</option>
								<option value="129600">36hr</option>
								<option value="172800">48hr</option>
								<option value="259200">72hr</option>
							</select>
						</div>
					</div>
					<div class="control-group">
						<label>Time Expired:</label>
						<div class="controls">
							<select name="filters[timeexpired][comparison]" class="span3">
								<option value="gt">Greater than</option>
								<option value="lt">Less than</option>
							</select>
							<select name="filters[timeexpired][value]" class="span2">
								<option value="">Select</option>
								<option value="600">10m</option>
								<option value="1800">30m</option>
								<option value="3600">1hr</option>
								<option value="28800">8hr</option>
								<option value="86400">24hr</option>
							</select>
						</div>
					</div>
				</div>
				<div class="span4">
					<div class="control-group">
						<label>% Workers Counter Offered: <span class="filter_value">0</span></label>
						<div class="controls">
							<input type="hidden" name="filters[offers]" id="filter_offers" value=""/>
							<div id="filter_slider_offers"></div>
						</div>
					</div>
					<div class="control-group">
						<label>% Workers Declined: <span class="filter_value">0</span></label>
						<div class="controls">
							<input type="hidden" name="filters[rejections]" id="filter_rejections" value=""/>
							<div id="filter_slider_rejections"></div>
						</div>
					</div>
				</div>
				<div class="span4">
					<div class="control-group">
						<label># Unanswered Questions: <span class="filter_value">0</span></label>
						<div class="controls">
							<input type="hidden" name="filters[questions]" id="filter_questions" value=""/>
							<div id="filter_slider_questions"></div>
						</div>
					</div>
					<div class="control-group">
						<label>% Who Viewed: <span class="filter_value">0</span></label>
						<div class="controls">
							<input type="hidden" name="filters[viewed]" id="filter_viewed" value=""/>
							<div id="filter_slider_viewed"></div>
						</div>
					</div>
				</div>
				<div class="span4">
					<div class="control-group">
						<label for="projects">Internal Owner:</label>
						<div class="controls">
							<select name="filters[internal_owners][]" id='internal_owners' multiple='multiple'>
								<c:forEach items="${filters['internal_owners']}" var="internal_owners">
									<option><c:out value="${internal_owners}"/></option>
								</c:forEach>
							</select>
						</div>
					</div>
					<div class="control-group">
						<label for="projects">Projects:</label>
						<div class="controls">
							<select name="filters[projects][]" id='projects' multiple='multiple'>
								<c:forEach items="${filters['projects']}" var="project">
									<option><c:out value="${project}"/></option>
								</c:forEach>
							</select>
						</div>
					</div>
				</div>
				<div class="span4">
					<div class="control-group">
						<label for="clients">Clients:</label>
						<div class="controls">
							<select name="filters[clients][]" id='clients' multiple='multiple'>
								<c:forEach items="${filters['clients']}" var="client">
									<option><c:out value="${client}"/></option>
								</c:forEach>
							</select>
						</div>
					</div>
				</div>
			</div>
		</div>
	</form>

	<div id="realtime_messages"></div>

	<table id="realtime_monitor">
		<thead>
			<tr>
				<th class="expand-col"></th>
				<th class="company-col">Company</th>
				<th class="appt-time-col">
					<a id="sort_time_to_appt" href="#/sort/time_to_appt">Time To Appt. <span class="ui-icon ui-icon-triangle-2-n-s"></span></a>
				</th>
				<th class="age-col">
					<a id="sort_order_age" href="#/sort/order_age">Order Age <span class="ui-icon ui-icon-triangle-2-n-s"></span></a>
				</th>
				<th class="updated-col">
					<a id="sort_last_updated" href="#/sort/last_updated">Last Updated <span class="ui-icon ui-icon-triangle-2-n-s"></span></a>
				</th>
				<th class="details-col">
					<a id="sort_details" href="#/sort/details">Details <span class="ui-icon ui-icon-triangle-2-n-s"></span></a>
				</th>
				<th class="invited-col">Invited Workers</th>
				<th class="working-on-it-col">Working on it</th>
			</tr>
		</thead>
		<tbody id="realtime_results">
			<tr>
				<td colspan="8">Loading Results...</td>
			</tr>
		</tbody>
	</table>

	<div class="pagination">
		<ul>
			<li class="prev"><a><i class="icon-double-angle-left"></i> Previous</a></li>
			<li><span>Page <span class="current_page">1</span> of <span class="num_pages">1</span></span></li>
			<li class="next"><a>Next <i class="icon-double-angle-right"></i></a></li>
		</ul>
	</div>

	<c:import url="/WEB-INF/views/web/partials/assignments/realtime_resource.jsp"/>
	<c:import url="/WEB-INF/views/web/partials/assignments/resource_action_modal.jsp">
		<c:param name="showHistory" value="1"/>
	</c:import>

	<script id="tmpl-realtime_row" type="text/x-jquery-tmpl">
		<td class="expand">
			<a href="javascript:void(0);" class="expand-assignment">[+]</a>
		</td>
		<td class="company-name">
			<a href="/admin/manage/company/overview/\${company_id}">\${company_name}</a>
		</td>
		<td class="time-to-appointment">
			\${time_to_appt}
		</td>
		<td class="age">
			\${age}
		</td>
		<td class="last-modified">
			\${last_modified_on}<br/>by \${modifier_first_name}. \${modifier_last_name}
		</td>
		<td>
			<strong><a href="/assignments/details/\${work_number}" target="_blank">\${work_number} - \${title}</a></strong> &nbsp;
			<a class="notes_action tooltipped tooltipped-n" href="/realtime/work_notes/\${work_number}" aria-label="Notes"></a>
			<a class="history_action tooltipped tooltipped-n" aria-label="History" href="/realtime/work_history/\${work_number}"></a>
			{{if group_sent}}
				<img src="${mediaPrefix}/images/icons/group-send-24x24.png" width="16" aria-label="Sent to Talent Pool" class="tooltipped tooltipped-n group-send-icon"/>
			{{/if}}
			<br/>
			\${date_time} <strong class="spend_limit">(\${spend_limit})</strong><br/>
			{{if questions > 0}}
				<span class="questions label label-warning tooltipped tooltipped-n" aria-label="Question">Q</span> \${questions} &nbsp;
			{{/if}}
			{{if offers > 0}}
				<span class="offers label label-success tooltipped tooltipped-n" aria-label="Offer">O</span> \${offers} &nbsp;
			{{/if}}
			{{if declines > 0}}
				<span class="declines label label-important tooltipped tooltipped-n" aria-label="Declines">D</span> \${declines} &nbsp;
			{{/if}}
			<!--
			numberOfUnansweredQuestions: \${number_of_unanswered_questions}
			percentWithOffers: \${percent_with_offers}
			percentWithRejections: \${percent_with_rejections}
			-->
			<div class="expanded-content">
				<a href="/realtime/cancel_work/\${work_number}" class="button -small void_action">Void</a>
				<a href="/assignments/edit_price/\${work_number}" class="button -small reprice_action">Change Price</a>
				<a href="/realtime/reschedule_work/\${work_number}" class="button -small reschedule_action">Reschedule</a>
				{{if !is_work_notify_available}}
				<a href="/realtime/workNotify/\${work_number}" title="None of the workers have opted to receive notifications." class="button -small work_notify_action disabled">Work Notify</a>
				{{else !is_work_notify_allowed}}
				<a href="/realtime/workNotify/\${work_number}" title="Notifying workers is limited to once per hour." class="button -small work_notify_action disabled">Work Notify</a>
				{{else}}
				<a href="/realtime/workNotify/\${work_number}" class="button -small work_notify_action">Work Notify</a>
				{{/if}}
				<a href="/realtime/resend/\${work_number}" class="button -small resend_action">Send Again: All</a>
			</div>
		</td>
		<td>
			<div class="collapsed-content">
				{{each(i, resource) resources}}
					{{if i < 8}}
						{{tmpl({resource: resource}) "#tmpl-resource"}}
						{{if resource_iterator(i + 1)}}
							<br clear="both"/>
						{{/if}}
					{{else i == 8}}
						<em>\${resource_count - 8} more resources</em>
					{{/if}}
				{{/each}}
			</div>
			<div class="expanded-content">
				{{each(i, resource) resources}}
					{{tmpl({resource: resource}) "#tmpl-resource"}}
					{{if resource_iterator(i + 1)}}
						<br clear="both"/>
					{{/if}}
				{{/each}}
			</div>
		</td>
		<td>
			{{tmpl({work_number: work_number, user_working_on_it: user_working_on_it}) "#tmpl-working_on_it"}}
		</td>
	</script>

	<script id="tmpl-working_on_it" type="text/x-jquery-tmpl">
		{{if user_working_on_it != null}}
			<div class="pull-left">
				\${user_working_on_it.first_name} \${user_working_on_it.last_name}<br/>
				{{if user_working_on_it.user_id == '${currentUser.id}'}}
					<a href="/realtime/toggle_workingonit/\${work_number}/0" class="button toggle_workingonit">Done</a>
				{{/if}}
			</div>
			<img src="${mediaPrefix}/images/icons/traffic-cone-48x48.png" height="36" width="36" alt="Working" class="working-on-it-image" title="I'm working on it" />
		{{else}}
			<a href="/realtime/toggle_workingonit/\${work_number}/1" class="button toggle_workingonit">Work on it</a>
		{{/if}}
	</script>

	<script id="tmpl-noresults_row" type="text/x-jquery-tmpl">
		<td colspan="8">There are currently no assignments.</td>
	</script>

	<div id="legend">
		<div class="row">
			<div class="span3">
				<dl>
					<dt><span class="label tooltipped tooltipped-n" aria-label="Employee">E</span></dt>
					<dd>Employee</dd>
					<dt><span class="label label-warning tooltipped tooltipped-n" aria-label="Question">Q</span></dt>
					<dd>Question</dd>
					<dt><span class="label label-success tooltipped tooltipped-n" aria-label="Offer">O</span></dt>
					<dd>Offer</dd>
					<dt><span class="label label-important tooltipped tooltipped-n" aria-label="Declined">D</span></dt>
					<dd>Declined</dd>
					<dt><img src="${mediaPrefix}/images/icons/eye-icon-16x16.png" alt="Viewed" style="vertical-align: top;"/></dt>
					<dd>Viewed</dd>
					<dt><img src="${mediaPrefix}/images/icons/pencil.png" alt="Note" style="vertical-align: top;"/></dt>
					<dd>Note</dd>
				</dl>
			</div>
			<div class="span4">
				<dl class="legend-labels">
					<dt class="accepted"></dt>
					<dd>Accepted</dd>
					<dt class="highlight_bad"></dt>
					<dd>Issue: Declined, Expiring, Offers</dd>
					<dt class="highlight_workingonit"></dt>
					<dd>Working On</dd>
					<dt class="highlight_bad highlight_workingonit"></dt>
					<dd>Issue</dd>
				</dl>
			</div>
		</div>
	</div>

</wm:app>
