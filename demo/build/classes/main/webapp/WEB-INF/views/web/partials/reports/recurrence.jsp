<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="nested" uri="http://struts.apache.org/tags-nested" %>
<%@ taglib prefix="wm-csrf" uri="http://www.workmarket.com/taglib/csrf" %>

<script id="work_report_recurrence-tmpl" type="text/x-jquery-tmpl">

	<form action="/recurrence" method="post" id="recurrenceForm" name="recurrenceForm" accept-charset="utf-8">
		<wm-csrf:csrfToken />
		<fieldset class="recurrence form-stacked well" id="recurrence-settings">

			<div id="recurrence_message"></div>

			<h4>Scheduled Report Delivery</h4>
			<ul class="unstyled">
				<li><input type="radio" id="recurrence-off" data-button-type="recurrence-enabled" name="recurrence-enabled" {{if !recurrence_enabled_flag}}checked="checked"{{/if}}/><span class="ml">OFF</span></li>
				<li><input type="radio" id="recurrence-on" data-button-type="recurrence-enabled" name="recurrence-enabled" {{if recurrence_enabled_flag}}checked="checked"{{/if}}/><span class="ml">ON</span></li>
			</ul>
			<input type="hidden" name="recurrence_enabled_flag"/>
			<input type="hidden" name="report_key" value="${saved_report_key}"/>

			<div class="content">
				<br/>
				<div class="row">

					<div class="span5">
						<label for="recipients">Recipients</label>

						<textarea id="recipients" class="email-recipient-list">{{if recipients}}\${recipients}{{/if}}</textarea>

						<div>
							<small class="meta">Use comma (,) or semicolon (;) as separator</small>
						</div>
					</div>

					<div class="span6">
						<label>Frequency</label>
						<ul class="unstyled">
							<li><input type="radio" id="frequency-daily" data-button-type="recurrence-type" name="recurrence-type" {{if recurrence_type == 'daily'}}checked="checked"{{/if}}/><span class="ml">Daily</span></li>
							<li><input type="radio" id="frequency-weekly" data-button-type="recurrence-type" name="recurrence-type" {{if recurrence_type == 'weekly'}}checked="checked"{{/if}}/><span class="ml">Weekly</span></li>
							<li><input type="radio" id="frequency-monthly" data-button-type="recurrence-type" name="recurrence-type" {{if recurrence_type == 'monthly'}}checked="checked"{{/if}}/><span class="ml">Monthly</span></li>
						</ul>
						<hr/>
						<input type="hidden" name="recurrence_type" value="\${recurrence_type}"/>

						<div id="recurrence-daily-options">
							<ul class="unstyled">
								<li><input type="radio" name="daily_weekdays_only_flag" value="false" class="daily-recurrence-type" {{if daily_weekdays_only_flag === false}}checked="checked"{{/if}}/><span class="ml">Every Day</span></li>
								<li><input type="radio" name="daily_weekdays_only_flag" value="true" class="daily-recurrence-type" {{if daily_weekdays_only_flag}}checked="checked"{{/if}}/><span class="ml">Every Week Day</span></li>
							</ul>
						</div>

						<div id="recurrence-weekly-options">

							<p>Send every week on:</p>
							<ul class="unstyled">
								<c:forEach var="weekday" items="${weekdays}" varStatus="status">
									<li><input type="checkbox" name="weekly_days[]"
									           value="${status.index}"/><span class="ml"><c:out value="${weekday}" /></span></li>
								</c:forEach>
							</ul>
						</div>

						<div id="recurrence-monthly-options">

							<ul class="unstyled">
								<li style="margin-bottom:5px;">
									<input type="radio" name="monthly_use_day_of_month_flag" value="true"
									       class="monthly-recurrence-type"
									{{if monthly_use_day_of_month_flag}}checked="checked"{{/if}} /> <span>On day
							<select class='date-select' name="monthly_frequency_day">
								<c:forEach var="i" begin="1" end="31">
									<option value="${i}"
									{{if monthly_frequency_day == ${i} }}selected="selected"{{/if}}>${i}</option>
								</c:forEach>
							</select>
							of every month</span>
								</li>
								<li>
									<input type="radio" name="monthly_use_day_of_month_flag" value="false"
									       class="monthly-recurrence-type"
									{{if monthly_use_day_of_month_flag === false}}checked="checked"{{/if}} /> <span>On the

							<select class="weekday-ordinal-select" name="monthly_frequency_weekday_ordinal">
								<option value="1"
								{{if monthly_frequency_weekday_ordinal == 1}}selected="selected"{{/if}}>1st</option>
								<option value="2"
								{{if monthly_frequency_weekday_ordinal == 2}}selected="selected"{{/if}}>2nd</option>
								<option value="3"
								{{if monthly_frequency_weekday_ordinal == 3}}selected="selected"{{/if}}>3rd</option>
								<option value="4"
								{{if monthly_frequency_weekday_ordinal == 4}}selected="selected"{{/if}}>4th</option>
							</select>

							<select style="day-of-week-select" name="monthly_frequency_weekday">
								<c:forEach var="day" items="${weekdays}" varStatus="status">
									<%-- these are 1-indexed for consistency --%>
									<option value="${status.index + 1}"
									{{if ${status.index} === monthly_frequency_weekday}}selected="selected"{{/if}}><c:out value="${day}" />
									</option>
								</c:forEach>
							</select>
							of every month</span>
								</li>
							</ul>
						</div>
					</div>
					<div class="span4">
						<label for="time_morning_flag">Time of Day
							<span class="tooltipped tooltipped-n info" aria-label="We will build and send your report at the selected time slot. If volume is very high, there may be a delay."><i class="wm-icon-question-filled"></i></span>
						</label>
						<select name="time_morning_flag">
							{{if time_morning_flag}}
							<option value="true" selected="selected">Early Morning (4-5 AM)</option>
							<option value="false">Late Evening (10-11 PM)</option>
							{{else}}
							<option value="true">Early Morning (4-5 AM)</option>
							<option value="false" selected="selected">Late Evening (10-11 PM)</option>
							{{/if}}
						</select>
						<label for="time_zone_id">Time Zone</label>
						<select name="time_zone_id">
							<c:forEach var="timeZone" items="${timeZoneMap}">
								<option value="${timeZone.key.timeZoneId}"
								{{if '${timeZone.key.timeZoneId}' === time_zone_id}}selected="selected"{{/if}}>${timeZone.key.timeZoneId}</option>
							</c:forEach>
						</select>
					</div>
				</div>

				<div>
					{{if savedReportKey}}
						<a class="button" id="save-recurrence">Update Scheduled Report</a>
					{{else}}
						<a class="button" id="recurrence-save-and-schedule-modal">Save and Schedule Report</a>
					{{/if}}
				</div>
			</div>
		</fieldset>
	</form>

	<%-- technically this is invalid HTML but it will be rendered by .tmpl() which makes it OK--%>
	<style type="text/css">
		#recurrenceForm ul.unstyled li {
		color: black;
		}
		#recurrence_message ul.unstyled li {
		color:white;
		}
	</style>
</script>
