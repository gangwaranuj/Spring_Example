<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="wm-csrf" uri="http://www.workmarket.com/taglib/csrf" %>
<%@ taglib prefix="wm" tagdir="/WEB-INF/tags" %>

<wm:admin pagetitle="Add">

<div class="sidebar admin">
	<c:import url="/WEB-INF/views/web/partials/admin/quick_links.jsp" />
</div>

<div class="content">
	<form action="<c:url value="/admin/certifications/add"/>" method="post" id="form_certificationsadd">
		<wm-csrf:csrfToken />
		<c:import url="/WEB-INF/views/web/partials/general/notices.jsp">
			<c:param name="bundle" value="${bundle}" />
		</c:import>

		<fieldset>
			<div class="clearfix">
				<label for="industry">Industry</label>
				<div class="input">
					<select name="industry" id="industry">
						<option value=""></option>
						<c:forEach var="i" items="${industries}">
							<option value="<c:out value="${i.id}" />"><c:out value="${i.name}" /></option>
						</c:forEach>
					</select>
				</div>
			</div>

			<div class="clearfix">
				<label for="provider">Certification Provider</label>
				<div class="input">
					<div id="provider_swap">
						<div id="select_provider" class="help-block">Select an industry to display list of available providers</div>
					</div>
				</div>
			</div>

			<div class="clearfix" id="custom_provider_line">
				<label for="custom_provider">New Provider</label>
				<div class="input">
					<input type="text" name="custom_provider" id="custom_provider" maxlength="200" />
				</div>
			</div>

			<div class="clearfix">
				<label for="name">Certification Name</label>
				<div class="input">
					<input type="text" name="name" id="name" maxlength="255" />
				</div>
			</div>
		</fieldset>

		<div class="wm-action-container">
			<button type="submit" class="button">Save</button>
			<a class="button" href="<c:url value="/admin/certifications/review"/>">Cancel</a>
		</div>

	</form>
</div>


<script type="text/javascript">

	var current_industryval = '';

	$(document).ready(function() {

		$('select#industry').change(function(){
			update_industry();
		});
		update_industry();

		$('select#provider').on('change', function() {
			if ($('select#provider').val() == 'other')
			{
				$('#custom_provider_line').show();
			}
			else
			{
				$('#custom_provider_line').hide();
			}
		});
	});

	/**
	 * Update the industry select box.
	 */
	function update_industry() {
		var industryval = $('select#industry').val();

		// Check the option "- Select -" case
		if (industryval == "")
		{
			$('#custom_provider_line').hide();
			$('#select_provider').html('Select an industry to display list of available providers.');
			return;
		}

		// See if we need to fire off an ajax call to look up licenses for state.
		if (current_industryval != industryval)
		{
			current_industryval = industryval;
			$('#select_provider').html('Updating list');
			// Do the AJAX call
			$.ajax({
				url: "<c:url value="/profile-edit/certificationslist"/>",
				global: false,
				type: "GET",
				data: ({industry : industryval}),
				dataType: "json",
				success: function(data){
					update_provider(data);
				}
			});
		}
	}

	/**
	 * Update the vendor select box.
	 *
	 * @param data
	 */
	function update_provider(data) {
		var select = '<select name="provider" id="provider">';
		var options = '';
		if (data.length > 0)
		{
			$('#custom_provider_line').hide();
			for (var i = 0; i < data.length; i++)
			{
				options += '<option value="' + data[i].id + '">' + data[i].name + '</option>';
			}
		}
		options += '<option value="other">Other</option>';
		select += options + '</select>';
		$('#select_provider').html(select);

		if (data.length == 0)
		{
			$('#custom_provider_line').show();
		}
	}

</script>

</wm:admin>
