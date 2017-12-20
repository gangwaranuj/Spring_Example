<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="sql_rt" uri="http://java.sun.com/jstl/sql_rt" %>
<%@ taglib prefix="wm-csrf" uri="http://www.workmarket.com/taglib/csrf" %>
<%@ taglib prefix="wm" tagdir="/WEB-INF/tags" %>

<wm:app pagetitle="Security Access" bodyclass="accountSettings" breadcrumbSection="Work" breadcrumbSectionURI="/settings" breadcrumbPage="Security Access" webpackScript="settings">

	<script>
		var config = {
			mode: 'security',
			ipsJson: ${ipsJson}
		};
	</script>

	<div class="row_wide_sidebar_left">
		<div class="sidebar">
			<c:set var="selected_navigation_link" value="/mmw/security" scope="request"/>
			<jsp:include page="/WEB-INF/views/web/partials/mmw/mmw_sidebar.jsp"/>
		</div>

		<div class="content">
			<div class="inner-container">
				<jsp:include page="/WEB-INF/views/web/partials/message.jsp"/>

				<div class="page-header">
					<h3>Security Access</h3>
				</div>

				<form:form modelAttribute="securityForm">
					<wm-csrf:csrfToken />
					<label class="control-group">
						<form:checkbox path="authorizeByInetAddress"/>
						Enable Secure IP Address Access
						<span class="help-block"><i class="icon-info-sign"></i> Work Market allows you to limit your employee's access to Work Market to defined IP addresses.</span>
					</label>


					<fieldset class="settings <c:if test="${not form.authorizeByInetAddress}">dn</c:if>">
						<div class="form-inline">
							<label>IP Address </label>
							<input type="text" name="address"/>
							<submit type="button" class="button -small" data-behavior="add-ip">Add</submit>
						</div>

						<div class="media">
							<table>
								<thead>
									<tr>
										<th>IP Address</th>
										<th>Date Modified</th>
										<th>Action</th>
									</tr>
								</thead>
								<tbody>
									<c:forEach var="ip" items="${securityForm.ips}">
										<tr>
											<td>
												<input type="hidden" name="ips" value="${ip.inetAddress}"/>
												<c:out value="${ip.inetAddress}" />
											</td>
											<td><fmt:formatDate value="${ip.modifiedOn.time}" pattern="MMM d, yyyy h:mma z" timeZone="${currentUser.timeZoneId}"/></td>
											<td><a data-behavior="delete">Delete</a></td>
										</tr>
									</c:forEach>
								</tbody>
							</table>

							<p class="no-results">0 IP addresses configured</p>
						</div>
					</fieldset>

					<div class="wm-action-container">
						<a href="/mmw/security" class="button">Cancel</a>
						<button type="submit" class="button">Save Changes</button>
					</div>

				</form:form>
			</div>
		</div>
	</div>

	<script id="ip-item" type="text/x-jquery-tmpl">
		<tr>
			<td>
				<input type="hidden" name="ips" value="\${inetAddress}"/>
				\${inetAddress}
			</td>
			<td><span class="label label-success">New</span></td>
			<td><a data-behavior="delete">Delete</a></td>
		</tr>
	</script>

</wm:app>
