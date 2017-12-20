<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<div class="wrapper grey">
	<div class="container">
		<div class="row topcontent">
			<div class="offset3 span6">
				<form action="/user/not_my_account_confirmed/${userNumber}">

					<c:import url="/WEB-INF/views/web/partials/message.jsp" />
					
					<p>If you did not sign up for a Work Market account using the email address <strong class="strong"><c:out value="${user.email}"/></strong>, please confirm so that we may remove your email address from this account.</p>
									
					<div class="actions">
						<p><button type="submit" class="button orange small-radius">I did not sign up for this account</button></p>
						<p><a class="button small-radius" href="/home">Nevermind, this is my account</a></p>
					</div>
						
					<p>If this Work Market account does belong to you, and you wish to unsubscribe from future email notifications, visit your <a href="/mysettings/notifications">Work Market notification settings</a>.</p>

				</form>
			</div>
		</div>
	</div>
</div>