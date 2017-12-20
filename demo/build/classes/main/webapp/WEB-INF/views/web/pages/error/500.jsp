<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles" %>
<%@ taglib prefix="wm" tagdir="/WEB-INF/tags" %>

<wm:public pagetitle="Error" bodyclass="page-public">

	<div class="container">
		<h1>Error 500 - Internal Server Error</h1>
		<div>
			<p>Looks like something went wrong. Sorry about that.</p>
			<p><a href="javascript:void(0);" onclick="history.go(-1);" class="button -primary">Go Back</a></p>
			<a href="/contact-us" class="button -light">Contact Us</a></p>
		</div>
	</div>

</wm:public>
