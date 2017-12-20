<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="wm" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="vr" uri="http://www.workmarket.com/taglib/velvet-rope" %>

<wm:app pagetitle="Third-Party Integrations" bodyclass="accountSettings" breadcrumbSection="Work" breadcrumbSectionURI="/settings" breadcrumbPage="Third Party Integrations">

	<div class="row_wide_sidebar_left">
		<div class="sidebar">
			<c:set var="selected_navigation_link" value="/mmw/integrations" scope="request"/>
			<c:import url="/WEB-INF/views/web/partials/mmw/mmw_sidebar.jsp"/>
		</div>

		<div class="content">
			<div class="inner-container">
				<div class="page-header">
					<h3>Third-Party Integrations</h3>
				</div>
				<div>
					<p>Work Market has enabled a number of third party integrations to enable you to run your business more
						efficiently. From <a href="/mmw/api">custom API integration</a> to specific platform tools like
						Autotask, you can keep your process and business in sync. We continue to build out workflow,
						payment, and document integration tools. Contact your account executive for more information.</p>
					<ul class="unstyled">
						<vr:rope>
							<vr:venue name="WEBHOOKS">
								<li><a href="/mmw/integration/webhooks">Webhooks <span class="label warning label-warning">BETA</span></a></li>
							</vr:venue>
						</vr:rope>
						<vr:rope>
							<vr:venue name="SALESFORCE_WEBHOOKS">
								<li><a href="/mmw/integration/salesforce">Salesforce</a> <span class="label warning label-warning">BETA</span></li>
							</vr:venue>
						</vr:rope>
						<li><a href="/mmw/integration/autotask">Autotask</a></li>
					</ul>
				</div>
			</div>
		</div>
	</div>
</wm:app>
