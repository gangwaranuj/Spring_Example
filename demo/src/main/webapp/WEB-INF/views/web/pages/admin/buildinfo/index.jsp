<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="wm" tagdir="/WEB-INF/tags" %>

<%--Scripting is disabled within custom tags, hence why they are set as variables above the tag here--%>
<c:set var="tomcatVersion"><%= request.getParameter(application.getServerInfo()) %></c:set>
<c:set var="servletVersion"><%= application.getMajorVersion() %>.<%= application.getMinorVersion() %></c:set>
<c:set var="jspVersion"><%=JspFactory.getDefaultFactory().getEngineInfo().getSpecificationVersion() %></c:set>

<c:set var="pageScript" value="wm.pages.admin.buildinfo.index" scope="request"/>

<wm:admin pagetitle="Build Info">

<div class="row_sidebar_left">
	<div class="sidebar admin">
	<c:import url="/WEB-INF/views/web/partials/admin/quick_links.jsp" />
	</div>

<div class="container">
<div class="content inner-container">
	<h2>Build Information</h2>
	<hr/>
	<c:choose>
		<c:when test="${not empty git}">
			<h3>Version</h3>
			<table class="table table-striped table-hover">
				<thead></thead>
				<tbody>
				<tr>
					<td>Git Branch</td>
					<td><strong><c:out value="${git['git.branch']}" /></strong></td>
				</tr>
				<tr>
					<td>Last Commit Description</td>
					<td><c:out value="${git['git.commit.message.full']}" /></td>
				</tr>
				<tr>
					<td>Committed By</td>
					<td><c:out value="${git['git.commit.user.name']}" /></td>
				</tr>
				<tr>
					<td>Last Commit SHA</td>
					<td><c:out value="${git['git.commit.id.abbrev']}" /></td>
				</tr>
				<tr>
					<td>Commit Time</td>
					<td><c:out value="${git['git.commit.time']}" /></td>
				</tr>
				<tr>
					<td>Diff from current branch HEAD</td>
					<td><a href="https://github.com/workmarket/application/compare/${git['git.commit.id.abbrev']}...${git['git.branch']}">Compare <c:out value="${git['git.commit.id.abbrev']}" /> &rarr; <c:out value="${git['git.branch']}" /></a><br>
						<span class="help-block">Note: This will not work for fork deploys. Just change the URL from 'workmarket' to 'your_fork_name'.</span>
					</td>
				</tr>
				</tbody></table>
			<h3>Build</h3>
			<table class="table table-striped table-hover"><tbody>
			<tr>
				<td>Built By</td>
				<td><c:out value="${git['git.build.user.name']}" /></td>
			</tr>
			<tr>
				<td>Build Time</td>
				<td><c:out value="${git['git.build.time']}" /></td>
			</tr>
			</tbody></table>
		</c:when>
		<c:otherwise>
			<p>No git repository information available.</p>
		</c:otherwise>
	</c:choose>
	<h3>Latest <span id="numberOfMigrations"></span> Migrations</h3>
	<table class="table table-striped table-hover">
		<thead>
		  <tr>
				<th>Script</th>
				<th>Status</th>
				<th>Installed On</th>
				<th>Execution Time (ms)</th>
		  </tr>
		</thead>
		<tbody id="latest_migrations">
		<tr>
			<td id="migrations_ajax_loader" style="border: none;">
				<img src="${mediaPrefix}/images/loading.gif" height="16" width="16" />
			</td>
		</tr>
		</tbody>
	</table>

	<h3>Other Version Info</h3>
	<table class="table table-striped table-hover">
		<tbody>
		<tr>
			<td>Tomcat Version</td>
			<td>${tomcatVersion}</td>
		</tr>
		<tr>
			<td>Servlet Specification Version</td>
			<td>${servletVersion}</td>
		</tr>
		<tr>
			<td>JSP version</td>
			<td>${jspVersion}</td>
		</tr>
		</tbody>
	</table>
</div>
</div>
</div>

<script id="migrationInfoTemplate" type="text/html">
	{{ _.each(migrationInfos, function(migrationInfo,key,list) { }}
	<tr>
		<td>{{= migrationInfo.script }}</td>
		<td>{{= migrationInfo.state }}</td>
		<td>{{= migrationInfo.installedOn }}</td>
		<td>{{= migrationInfo.executionTime }}</td>
	</tr>
	{{ }); }}
</script>

</wm:admin>
