<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="wmfmt" uri="http://www.workmarket.com/taglib/fmt" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="wm-csrf" uri="http://www.workmarket.com/taglib/csrf" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="wm" tagdir="/WEB-INF/tags" %>

<wm:app
	pagetitle="Contact Manager"
	bodyclass="page-address-book accountSettings"
	webpackScript="addressbook"
>

	<script>
		var config = {
			'addressbook': ${contextJson}
		}
	</script>

	<div id="custom_message">
		<div class="message alert alert-error error dn">
			<a class="close">x</a>
			<div></div>
		</div>

		<div class="message alert alert-success success dn">
			<a class="close">x</a>
			<div></div>
		</div>
	</div>

	<div class="page-header clear">
		<h3 class="pull-left">Contact Manager</h3>
	</div>

	<ul class="wm-tabs">
		<li class="wm-tab -active" id="locations-tab" data-content="#locations">Locations</li>
		<li class="wm-tab" id="clients-tab" data-content="#clients">Clients</li>
		<li class="wm-tab" id="contacts-tab" data-content="#contacts">Contacts</li>
	</ul>
	<div class="button-group">
		<a id="map-reset-all" class="button">Reset Map</a>
		<a class="button cta-import">Import</a>
		<a class="button cta-manage-location" href="/addressbook/location/manage" rel="add">New Location</a>
		<a class="button cta-manage-client" href="/addressbook/client/manage" rel="add">New Client</a>
		<a class="button cta-manage-contact" href="/addressbook/contact/manage" rel="add">New Contact</a>
	</div>

	<div class="wm-tab--content -active" id="locations">
		<div id="map">
			<div class="map-controls">
				<select id="client-filter-location" class="clientFilter controls" data-placeholder="Overlay Client Locations"></select>
				<select id="select-groups" name="group" class="groupFilter controls" data-placeholder="Overlay Talent Pool Coverage"></select>
				<input id="map-address" class="map-address" type="text" placeholder="Zoom to City, State, or Zipcode" value="">
				<wm:button classlist="map-action-button" icon="search"></wm:button>
			</div>
			<div id="location-map" class="container">
				<input type="hidden" id="status" value="all" />
				<wm:spinner />
				<div id="map-canvas"></div>
			</div>
		</div>
		<div class="button-group">
			<a id="delete-selected-locations-button" class="button" rel="delete" style="display:none">Delete Selected</a>
		</div>
		<table id="locations_list" class="contact-manager">
			<thead>
				<tr>
					<th width="10%"><input type="checkbox" id="select-all-clients"/> Select All</th>
					<th>Location Name</th>
					<th>Location Number</th>
					<th>Client Name</th>
					<th width="30%">Address</th>
					<th>Type</th>
					<th width="15%">Contacts</th>
					<th width="3%">Edit</th>
					<th width="3%">Delete</th>
				</tr>
			</thead>
			<tbody>
				<tr>
					<td colspan="9" class="dataTables_empty">Loading data from server</td>
				</tr>
			</tbody>
		</table>
	</div>
	<div class="wm-tab--content" id="clients">
		<table id="client_list" class="contact-manager" style="width: 100%">
			<thead>
				<tr role="row">
					<th>Client Name</th>
					<th>Client Number</th>
					<th>Region</th>
					<th>Division</th>
					<th>Industry ID</th>
					<th>Website</th>
					<th>Work Phone</th>
					<th width="3%">Edit</th>
					<th width="3%">Delete</th>
				</tr>
			</thead>
			<tbody>
				<tr>
					<td colspan="7" class="dataTables_empty">Loading data from server</td>
				</tr>
			</tbody>
		</table>
	</div>
	<div class="wm-tab--content" id="contacts">
		<table id="contacts_list" class="contact-manager">
			<thead>
				<tr>
					<th>Contact Name</th>
					<th>Title</th>
					<th>
						<select id="client-filter-contact" class="client-filter-contact" data-placeholder="Filter by Client"></select>
					</th>
					<th>Email</th>
					<th>Work Number</th>
					<th>Mobile Number</th>
					<th>Locations</th>
					<th>Manager</th>
					<th width="3%">Edit</th>
					<th width="3%">Delete</th>
				</tr>
			</thead>
			<tbody>
				<tr>
					<td colspan="7" class="dataTables_empty">Loading data from server</td>
				</tr>
			</tbody>
		</table>
	</div>

	<div class="dn">
		<div id="client-form">
			<c:import url="/WEB-INF/views/web/partials/addressbook/client_form.jsp"/>
		</div>
	</div>

	<script id="name-client-cell-tmpl" type="text/template">
		<div>
			<a href="/addressbook/client/manage?id=\${meta.id}" rel="edit" class="cta-manage-client">\${meta.name}</a>
		</div>
	</script>

	<script id="edit-client-cell-tmpl" type="text/template">
		<div>
			<a href="<c:url value="/addressbook/client/manage?id=\${meta.id}"/>" rel="edit" class="cta-manage-client tooltipped tooltipped-n" aria-label="Edit">
				<i class="wm-icon-edit icon-large muted"></i>
			</a>
		</div>
	</script>

	<script id="delete-client-cell-tmpl" type="text/template">
		<div>
			<a class="cta-delete-client tooltipped tooltipped-n" aria-label="Delete" href="/addressbook/client/delete/\${meta.id}" rel="\${row}"><i class="wm-icon-trash icon-large muted "></i></a>
		</div>
	</script>

	<script id="edit-location-cell-tmpl" type="text/template">
		<div>
			<a href="<c:url value="/addressbook/location/manage?id=\${meta.id}"/>" client="\${meta.clientId}" rel="edit" class="cta-manage-location tooltipped tooltipped-n" aria-label="Edit">
				<i class="wm-icon-edit icon-large muted"></i>
			</a>
		</div>
	</script>

	<script id="delete-location-cell-tmpl" type="text/template">
		<div>
			<a href="<c:url value="/addressbook/location/delete/\${meta.id}"/>" value="\${meta.id}" client="\${meta.clientId}" class="cta-delete-location tooltipped tooltipped-n" rel="Location" aria-label="Delete">
				<i class="wm-icon-trash icon-large muted"></i>
			</a>
		</div>
	</script>

	<script id="contact-location-cell-tmpl" type="text/template">
		<div>
			\${meta.contactName}
			{{if meta.moreContacts > 0 }}
			(+\${meta.moreContacts})
			{{/if}}
		</div>
	</script>

	<script id="edit-contact-cell-tmpl" type="text/template">
		<div>
			<a href="<c:url value="/addressbook/contact/manage?id=\${meta.id}"/>" rel="edit" class="cta-manage-contact tooltipped tooltipped-n" aria-label="Edit">
				<i class="wm-icon-edit icon-large muted"></i>
			</a>
		</div>
	</script>

	<script id="delete-contact-cell-tmpl" type="text/template">
		<div>
			<a href="<c:url value="/addressbook/contact/delete/\${meta.id}"/>" class="cta-delete-contact tooltipped tooltipped-n" rel="Contact" aria-label="Delete">
				<i class="wm-icon-trash icon-large muted"></i>
			</a>
		</div>
	</script>

	<script id="location-contact-cell-tmpl" type="text/template">
		<div>
			\${meta.locationName}
			{{if meta.moreLocations > 0 }}
			(+\${meta.moreLocations})
			{{/if}}
		</div>
	</script>

	<script id="name-location-cell-tmpl" type="text/template">
		<div>
			<a href="/addressbook/location/manage?id=\${meta.id}" rel="edit" client="\${meta.clientId}" class="cta-manage-location">\${meta.name}</a><br/>
		</div>
	</script>

	<script id="select-location-cell-tmpl" type="text/template">
		<div>
			<input type="checkbox" value="\${meta.id}" class="select-location"/>
		</div>
	</script>

	<script id="name-contact-cell-tmpl" type="text/template">
		<div>
			<a href="/addressbook/contact/manage?id=\${meta.id}" rel="edit" class="cta-manage-contact">\${meta.name}</a><br/>
		</div>
	</script>

	<script id="qq-uploader-tmpl" type="text/template">
		<div class="qq-uploader">
			<div class="qq-upload-drop-area"><span>Drop photo here to upload</span></div>
			<a href="javascript:void(0);" class="qq-upload-button button -small"><span>Upload File</span></a>
			<ul class="ustyled qq-upload-list"></ul>
		</div>
	</script>

	<script src="https://maps.googleapis.com/maps/api/js?v=3.23&key=AIzaSyAWD12qVRbpnGyNF_fmYMERR0gyvdbHNvE&libraries=places" type="text/javascript"></script>

</wm:app>
