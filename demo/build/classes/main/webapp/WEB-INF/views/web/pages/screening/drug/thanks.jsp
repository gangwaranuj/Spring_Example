<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib prefix="wm" tagdir="/WEB-INF/tags" %>

<wm:app pagetitle="Thank You" bodyclass="screening">

	<%@ page isELIgnored="false" %>

	<div class="content">
		<div class="inner-container">
			<h4>Thank you for requesting a drug screen</h4>

			<p>Your drug screen is now 'Pending'. Your account will be charged and you will receive an email notification
				from SterlingBackcheck who will help you identify a testing site near you.</p>
			<a class="button" href="<c:url value="/profile"/>">Return to your profile</a>
		</div>
	</div>

</wm:app>
