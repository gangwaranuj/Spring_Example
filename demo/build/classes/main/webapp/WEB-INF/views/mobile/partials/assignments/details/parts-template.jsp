<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="wm-csrf" uri="http://www.workmarket.com/taglib/csrf" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="wm" tagdir="/WEB-INF/tags" %>

<script type="text/template" id="locationTemplate">
	<p class="parts-table--address">
		{{= location ? location : "Location Unspecified" }}
	</p>
</script>

<script type="text/template" id="partsTable">
	<div class="parts-table
		<c:if test="${!(work.status.code == workStatusTypes['DRAFT'] || work.status.code == workStatusTypes['SENT'])}">-active-worker-exists</c:if>
		<c:if test="${isOwner or isAdmin}">-is-company</c:if>">
		<div class="parts-table--part-links">
			{{ _.each(parts, function(part) { }}
			<a class="parts-table--part-link" data-id="{{= part.id }}" href="{{= part.providerUrl }}" target="_blank">
				<span class="parts-table--link-bit">{{= part.displayName }}</span>
					<span class="_provider_ parts-table--link-bit {{= part.hasProviderIcon ? '-' + part.shippingProvider.toLowerCase() : '-untracked' }}">
						<wm:icon name="fedex"/>
						<wm:icon name="dhl"/>
						<wm:icon name="usps"/>
						<wm:icon name="ups"/>
						<wm:icon name="truck"/>
					</span>
					<span class="-status parts-table--link-bit parts-table--tracking-status {{= '-' + part.trackingStatus.toLowerCase().replace('_', '-') }}">
						{{= part.trackingStatus.replace('_', ' ') }}
					</span>
				<!-- if the part is being delivered via a supported provider -->
				{{ if (part.hasProviderIcon) { }}
					<c:import url="/WEB-INF/views/mobile/partials/svg-icons/icon-clicker-right-orange.jsp"/>
				{{ } }}
			</a>
			{{ }); }}
		</div>
	</div>
</script>
