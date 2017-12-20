<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles" %>
<%@ taglib prefix="wm" tagdir="/WEB-INF/tags" %>

<wm:public pagetitle="Error" bodyclass="page-public">

	<div class="container">
		<h3>Sorry, the page you are looking for cannot be found.</h3>
		<p> Try one of the links below.</p>
		<br/>
		<div>
			<ul>
				<li><a href="/assignments">Dashboard</a></li>
				<li><a href="/assignments/add">New Assignment</a></li>
				<li><a href="/addressbook">Address Book</a></li>
				<li><a href="/projects">Projects</a></li>
				<li><a href="/assignments/upload">WorkUpload&trade;</a></li>
				<li><a href="/realtime">Realtime</a></li>
				<li><a href="/search">Find Workers</a></li>
				<li><a href="/groups">Talent Pools</a></li>
				<li><a href="/campaigns">Recruiting</a></li>
				<li><a href="/lms/manage/list_view">Tests</a></li>
				<li><a href="/lms/manage/surveys">Surveys</a></li>
				<li><a href="/invitations">Invitations</a></li>
				<li><a href="/notifications">Notifications</a></li>
				<li><a href="/reports">Reports</a></li>
				<li><a href="/reports/custom/manage">New Report</a></li>
			</ul>
		</div>
		<div>
			<a href="javascript:void(0);" onclick="javascript: history.go(-1);" class="button -primary">Go back</a>
		</div>
	</div>

</wm:public>
