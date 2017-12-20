<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="sf" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="wm-csrf" uri="http://www.workmarket.com/taglib/csrf" %>
<%@ taglib prefix="wm" tagdir="/WEB-INF/tags" %>

<wm:app pagetitle="Industries" bodyclass="accountSettings page-profile-industries" webpackScript="profileedit">

	<script>
		var config = {
			type: 'industries'
		}
	</script>

	<jsp:include page="/WEB-INF/views/web/partials/message.jsp" />

	<div class="row_sidebar_left">
		<div class="sidebar">
			<jsp:include page="/WEB-INF/views/web/partials/profile/profile_edit_sidebar.jsp" />
		</div>

		<div class="content">
			<div class="inner-container">
				<div class="page-header">
					<h3>Available Industries</h3>
				</div>

				<sf:form action="/profile-edit/save_industries" method="POST" id="form_industries">
					<wm-csrf:csrfToken />

					<c:if test="${currentUser.seller || currentUser.dispatcher}">
						<p>When looking for work, select the industries where you are most experienced. This information is shown on your profile and is available to clients searching for workers with certain expertise. You will be eligible to receive assignments in each industry you select. Examples include:</p>
						<ul>
							<li>Technology: Requests to upgrade computer software, install ethernet networks, repair TV/Video equipment, work on Cisco servers, replace parts in Xerox printers</li>
							<li>Field Marketing: Brand ambassador assignments, mystery shopping, trade show setup / takedown</li>
							<li>Healthcare: Per diem at home health aide, day nurse, dietary counseling</li>
							<li>Legal: Document review, copy editing, in-office assistant and paralegal work</li>
						</ul>
					</c:if>
					<c:if test="${currentUser.buyer}">
						<p>When sending work out to workers, industry expertise is tied to their profiles. Each assignment has an industry and the selection helps make sure the right workers get invited to your assignment. For example, if you require Field Marketing experts to perform work for you, then enable the Field Marketing industry for your account.</p>
					</c:if>

					<hr/>

					<div class="clearfix">
						<table id="profile_industries_list">
							<tbody>
							<c:forEach var="item" items="${industries}">
								<tr class="industry_row">
									<td>
										<wm:settings-switch name="industry" value="industry_${item.key.id}" checked="${item.value}"/>
									</td>
									<td>
										<div class="industry_name">
											<strong><c:out value="${item.key.name}" /></strong>
										</div>
									</td>
								</tr>
							</c:forEach>
							</tbody>
						</table>
					</div>

					<div class="wm-action-container">
						<button id="save_button" class="button" type="submit">Save Changes</button>
					</div>
				</sf:form>
			</div>
		</div>
	</div>

</wm:app>