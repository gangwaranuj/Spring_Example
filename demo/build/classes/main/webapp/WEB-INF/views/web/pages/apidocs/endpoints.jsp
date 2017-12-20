<%@ taglib prefix="wm" tagdir="/WEB-INF/tags" %>

<wm:public pagetitle="Endpoints" bodyclass="page-api">
<div class="container">
	<h1>Work Market API / Endpoints</h1>

	<div class="row">
		<div class="sidebar">
			<jsp:include page="../../partials/general/api_endpoint_sidebar.jsp"/>
		</div>
		<div class="content">
			<jsp:include page="/WEB-INF/views/${apiView}.jsp" />
		</div>
	</div>
</div>
</wm:public>
