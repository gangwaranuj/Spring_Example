<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<a name="dateandtime"></a>
<div class="inner-container">
	<div class="page-header">
		<h4>Scheduling</h4>
	</div>

	<div id="date-and-time-container">
		<div id="schedule-type" class="control-group">
			<label for="scheduling1" class="control-label">Schedule</label>
			<div class="controls">
				<label class="inline nowrap">
					<form:radiobutton path="scheduling" value="0" id="scheduling1"/>
					At a specific time
				</label>
				<label>
					<form:radiobutton path="scheduling" value="1" id="scheduling2"/>
					Set arrival window
					<span class="tooltipped tooltipped-n" aria-label="The worker is expected to start the assignment between the specified times.">
						<i class="wm-icon-question-filled"></i>
					</span>
				</label>
			</div>
		</div>

		<div id="scheduling_specific">
			<div class="control-group">
				<div class="controls required">
					<form:input path="from" id="from" cssClass="span2" placeholder="Select Date"/>
					<form:input path="fromtime" id="fromtime" cssClass="span2" placeholder="Select Time"/>
				</div>
			</div>
		</div>

		<div id="scheduling_variable">
			<div class="control-group">
				<div class="controls">
					<div class="required">
						<form:input path="variable_from" id="from2" cssClass="span2" placeholder="Select Date"/>
						<form:input path="variable_fromtime" id="fromtime2" cssClass="span2" placeholder="Select Time"/>
					</div>
					to
					<div class="required">
						<form:input path="to" id="to" cssClass="span2" placeholder="Select Date"/>
						<form:input path="totime" id="totime" cssClass="span2" placeholder="Select Time"/>
					</div>
				</div>
			</div>
		</div>

		<div class="control-group">
			<label class="control-label">Options</label>
			<div class="controls">
				<label>
					<form:checkbox path="resource_confirmation" value="1" id="resource_confirmation"/>
					Require confirmation
					<form:input path="resource_confirmation_hours" id="resource_confirmation_hours" cssClass="span1 ml mr"/>
					hour(s) before start time.
				</label>

				<label class="intro-require-check-in">
					<form:checkbox path="check_in" id="check-in-required"/>
					Require check in and check out via WM when starting and finishing work
					<span class="tooltipped tooltipped-n" aria-label="If you set Check In/Check Out required, the assignment will not enter In Progress status until the worker is checked in.">
						<i class="wm-icon-question-filled"></i>
					</span>
				</label>

				<label>
					<form:checkbox path="check_in_call_required" id="check-in-phone"/>
					Instruct worker to call
					<form:input path="check_in_contact_name" id="check-in-contact-name" type="text" placeholder="Contact Name"/><br />
					<span style="margin-left:12px"> at the following number</span>
					<form:input path="check_in_contact_phone" id="check-in-contact-phone" type="text" class="span3" maxlength="20" placeholder="Phone" alt="phone-us"/>
					<span class="tooltipped tooltipped-n" aria-label="The Support Contact number will be displayed as required to call for check IN and check OUT. The Support Contact can then record the check in/out on the system.">
						<i class="wm-icon-question-filled"></i>
					</span>
				</label>

				<label>
					<form:checkbox path="show_check_out_notes" value="1" id="show_check_out_notes"/>
					Show
					<form:select path="check_out_notes_requiredness" name="check_out_notes_requiredness" class="span3">
						<form:option value="optional" selected="selected">Optional</form:option>
						<form:option value="required">Required</form:option>
					</form:select>
					notes field on check out
					<c:if test="${mmw.ivrEnabledFlag}">
						<span class="tooltipped tooltipped-n" aria-label="Check out via IVR will not include notes."><i class="wm-icon-question-filled"></i></span>
					</c:if>
				</label>

				<label for="check_out_notes_instructions">
					<em>(Optional)</em> Include instructions for note field
				</label>
				<form:textarea path="check_out_notes_instructions" id="check_out_notes_instructions" cssClass="xlarge span7" rows="3" cols="30" placeholder='Enter instructions for note field here. For example: "Please describe the work completed."'/>
			</div>
		</div>
	</div>
</div>
