<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="wm" tagdir="/WEB-INF/tags" %>

<wm:app pagetitle="Add Project" bodyclass="projects" webpackScript="projects">

	<jsp:include page="/WEB-INF/views/web/partials/message.jsp" />

	<script type="text/javascript">
		var config = ${contextJson};
	</script>

	<div class="inner-container">
		<div class="page-header">
			<h2>Add Project</h2>
		</div>

		<jsp:include page="/WEB-INF/views/web/partials/projects/add_form.jsp">
			<jsp:param name="form_uri" value="/projects/add"/>
		</jsp:include>
	</div>
</wm:app>
