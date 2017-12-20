<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="wmfmt" uri="http://www.workmarket.com/taglib/fmt" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<div>
	<p>
		Completing for Worker allows you to close out the assignment on behalf of
		the resource and simultaneously approve it for payment.
	</p>
	<p>
		If appropriate, please make sure you have confirmed with the worker that
		all the information is accurate, especially final price including approved
		budget increases or expense reimbursements.
	</p>
	<div class="wm-action-container">
		<div class="control-group fl">
			<div class="controls">
				<label class="checkbox nowrap">
					<input type="checkbox" id="hide_complete_on_behalf"/>
					Don't show this popup anymore
				</label>
			</div>
		</div>

		<div class="fr">
			<input class="button close_modal" type="button" value="Cancel"/>
			<input class="button complete_on_behalf" type="button" value="Continue" />
		</div>
	</div>
</div>

