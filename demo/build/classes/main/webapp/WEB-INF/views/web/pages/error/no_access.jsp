<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="wm" tagdir="/WEB-INF/tags" %>

<wm:public pagetitle="No Access" bodyclass="page-public">

	<div class="container">
		<h1>You don't have access to this page.</h1>
		<div>
			<p><a href="javascript:void(0);" onclick="javascript: history.go(-1);" class="button -primary">Go back</a></p>
			<p>If you believe that this is a mistake, please contact your company administrator for access.</p>
		</div>
	</div>

</wm:public>
