<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>

<a name="bundle"></a>
<div class="inner-container">
	<div class="page-header routing-page-header">
		<h4>Bundle</h4>
	</div>
	<div class="form-actions">
		This assignment is part of a bundle called: "${bundleParent.title}".
		You can click <a href="/assignments/view_bundle/${bundleParent.id}">here</a> (abandoning your changes) to see this bundle.
	</div>
</div>
