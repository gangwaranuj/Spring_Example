<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="wm-csrf" uri="http://www.workmarket.com/taglib/csrf" %>

<div class="wrapper grey">
	<div class="container">
		<div class="row topcontent">
			<div class="offset3 span6">
				<c:import url="/WEB-INF/views/web/partials/message.jsp" />

				<form action="/user/optout" method="post">
					<wm-csrf:csrfToken />
					<input type="hidden" name="email" value="${email}" id="email" />

					<p>This message was sent by a Work Market user who entered your email address.</p>

					<p>If you prefer NOT to receive emails when other people invite you to Work Market, click "Continue".</p>

					<div class="actions">
						<button type="submit" class="button orange small-radius">Continue</button>
					</div>

				</form>
			</div>
		</div>
	</div>
</div>