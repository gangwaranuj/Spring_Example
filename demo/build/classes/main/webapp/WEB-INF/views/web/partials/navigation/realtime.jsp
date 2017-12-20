<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<div class="fill">
	<div class="container-fluid">
		<a class="brand" href="/"><fmt:message key="realtime.workmarket" /></a>
		<ul class="nav secondary-nav">
			<li><a>Last updated: <span id="last_updated">n/a</span></a></li>
		</ul>
	</div>
</div>