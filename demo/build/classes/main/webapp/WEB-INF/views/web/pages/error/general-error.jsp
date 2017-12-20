<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles" %>
<%@ taglib prefix="wm" tagdir="/WEB-INF/tags" %>

<wm:public bodyclass="page-public">

	<div class="container">
		<h1>Sorry, there was an error.</h1>
		<div>
			<p>Sorry, there was an error and we are unable to complete your request.</p>
			<p><a href="javascript:void(0);" onclick="javascript: history.go(-1);" class="button -primary">Go back</a></p>
		</div>
	</div>

</wm:public>
