<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<div class="clearfix">
	<label for="badge_included_on_printout" class="strong">Print Configuration</label>
	<div class="input">
		<ul class="inputs-list">
			<li>
				<label>
					<form:checkbox path="badge_included_on_printout" value="1" />
					Display badge on printout
				</label>
			</li>
		</ul>
	</div>
</div>