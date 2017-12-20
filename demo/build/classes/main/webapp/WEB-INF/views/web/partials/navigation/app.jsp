<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="wm" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="wmfn" uri="http://www.workmarket.com/taglib/functions" %>
<%@ taglib prefix="wmfmt" uri="http://www.workmarket.com/taglib/fmt" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="vr" uri="http://www.workmarket.com/taglib/velvet-rope" %>

<c:set var="create" value="false" />
<c:set var="dispatch" value="false" />
<c:set var="isAdmin" value="false" />

<vr:rope>
	<vr:venue name="COMPANY">
		<c:set var="create" value="true" />
	</vr:venue>
</vr:rope>

<sec:authorize access="hasAnyRole('ACL_ADMIN', 'ACL_MANAGER')">
	<c:set var="isAdmin" value="true" />
</sec:authorize>

<sec:authorize access="hasRole('ACL_DISPATCHER')">
	<c:set var="dispatch" value="true" />
</sec:authorize>

<header id="wm-main-nav" class=" ${(currentUser.seller || currentUser.dispatcher || currentUser.employeeWorker) ? '-worker' : '-company'}"></header>

<script>
	var navConfig = navConfig || {};
	navConfig.dispatch = ${dispatch};
	navConfig.create = ${create};
	navConfig.isNavPinnedOpen = ${isNavPinnedOpen};
	navConfig.breadcrumbSection = '${breadcrumbSection}';
	navConfig.breadcrumbPage = '${breadcrumbPage}';
	navConfig.uploadStatus = ${uploadStatus};
	navConfig.uploadMessage = {
		start: {
			title: '<fmt:message key="navigation.uploading" />'
		},
		done: {
			title: '<fmt:message key="navigation.uploading_complete" />',
			link: {
				href: '/assignments',
				text: '<fmt:message key="navigation.view_assignments" />'
			}
		}
	}
	navConfig.logoURI = '${mediaPrefix}/images/app/workmarketlogolight.svg';

	navConfig.currentUser = {
		buyer: ${currentUser.buyer},
		seller: ${currentUser.seller},
		dispatcher: ${currentUser.dispatcher},
		employeeWorker: ${currentUser.employeeWorker},
		smallAvatarUri: '${wmfn:stripUriProtocol(currentUser.smallAvatarUri)}' || false,
		email: '${currentUser.email}',
		admin: ${isAdmin},
		userMenu: []
	};

<c:if test="${(currentUser.buyer || currentUser.dispatcher || currentUser.employeeWorker) }">
	navConfig.currentUser.userMenu.push(
		{ href: '/profile?ref=nav', title: '<fmt:message key="global.my_profile" />' },
		{ href: '/forums', title: '<fmt:message key="navigation.forums" />' });
</c:if>

<sec:authorize access="hasAnyRole('PERMISSION_ACCESSMMW')">
	<c:choose>
		<c:when test="${create}">
			navConfig.currentUser.userMenu.push({ href: '/settings', title: '<fmt:message key="global.settings" />'});
		</c:when>
		<c:otherwise>
			navConfig.currentUser.userMenu.push({ href: '/account', title: '<fmt:message key="global.settings" />' });
		</c:otherwise>
	</c:choose>
</sec:authorize>

<c:if test="${orgStructuresData.isEnabled}">
	<sec:authorize access="hasRole('ACL_ADMIN')">
		navConfig.currentUser.userMenu.push({ href: '/org-structure', title: '<fmt:message key="global.org_structures" />' });
	</sec:authorize>

	navConfig.currentUser.savedOrgMode = '${orgStructuresData.activeOrgMode}';
	navConfig.currentUser.orgModes = ${orgStructuresData.orgModesJson};
</c:if>

<c:if test="${create || dispatch}">
	navConfig.currentUser.userMenu.push({ divider: true });
	navConfig.currentUser.userMenu.push({ href: '/home', personatype: 'perform_work', title: '<fmt:message key="global.perform_work" />' });
	<c:if test="${create && not currentUser.employeeWorker}">
		navConfig.currentUser.userMenu.push({ href: '/home', personatype: 'create_work', title: '<fmt:message key="global.create_work" />' });
	</c:if>
	<c:if test="${dispatch}">
		navConfig.currentUser.userMenu.push({ href: '/home', personatype: 'dispatch_work', title: '<fmt:message key="navigation.team_agent" />' });
	</c:if>
	navConfig.currentUser.userMenu.push({ divider: true });
	<c:if test="${(currentUser.buyer || currentUser.dispatcher) }">
		navConfig.currentUser.userMenu.push({ href: 'https://workmarket.zendesk.com/hc/en-us', title: '<fmt:message key="global.help_center" />' });
	</c:if>
	<sec:authorize access="hasRole('ROLE_PREVIOUS_ADMINISTRATOR')">
		navConfig.currentUser.userMenu.push({href: '/admin/usermanagement/masquerade/stop', title: '<fmt:message key="global.stop_masquerading" />' });
	</sec:authorize>
</c:if>
	 navConfig.currentUser.userMenu.push({href: '/logout?ref=nav', title: '<fmt:message key="global.sign_out" />' });
</script>
