<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<div class="alert" id="schedule-requires-approval">
<p><strong>Note:</strong> If the worker declines your reschedule request, they will be unassigned from this assignment.</p>
</div>

<div class="clearfix">
	<div class="input">
		<div class="inline-inputs">
			<label class="dib normal">
				<input type="radio" id="new_time" name="reschedule_option" value="time"  />
				<span id="label_specific_time">Propose</span> <strong>time</strong>
			</label>
			<label class="dib normal">
				<input type="radio" id="new_window" name="reschedule_option" value="window"  />
				<span id="label_time_window">Propose</span> <strong>time window</strong>
			</label>
		</div>
	</div>
</div>

<div class="clearfix">
	<div class="input">
		<div class="inline-inputs">
				<input type="text" name="from" value='' class='span2' placeholder='Select Date'/>
				<input type="text" name="fromtime" value='' class='span2' placeholder='Select Time'/>
			<span class="to-date" style="display:block;">
				to
					<input type="text" name="to" value='' class='span2' placeholder='Select Date'/>
					<input type="text" name="totime" value='' class='span2' placeholder='Select Time'/>
			</span>
		</div>
	</div>
</div>

<div class="alert-message block-message dn" id="appointment-requires-approval">
	<strong>Note:</strong> The selected date &amp; time is outside the current window and will require approval. If the client declines your request, you will be unassigned from this assignment. Times within the current window are automatically approved.
</div>