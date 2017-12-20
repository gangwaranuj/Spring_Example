<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<div class="dn">
	<div id="decline_negotiation">
		<form action="/assignments/decline_negotiation/${work.workNumber}">
			<input type="hidden" id="decline_negotiation_id" name="id" value="" />

			<c:import url="/WEB-INF/views/web/partials/general/notices_js.jsp" />

			<div class="control-group">
				<label for="decline_negotiation_note" class="control-label">Optional note explaining reason</label>
				<div class="controls">
					<textarea class="input-block-level" id="decline_negotiation_note" name="decline_negotiation_note" rows="5"></textarea>
				</div>
			</div>

			<div class="wm-action-container">
				<button type="button" class="button" id="decline_negotiation_cancel">Cancel</button>
				<button type="submit" class="button -primary" id="decline_negotiation_ok">Submit</button>
			</div>
		</form>
	</div>
</div>