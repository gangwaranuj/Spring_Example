<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<h4>In Bundle</h4>
<div class="form-actions">
	This assignment is part of a bundle called: "${workResponse.workBundleParent.title}".
	You can click <a href="/assignments/view_bundle/${workResponse.workBundleParent.id}">here</a> to see this bundle.
</div>