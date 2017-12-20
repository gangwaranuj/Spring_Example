<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="wm" tagdir="/WEB-INF/tags" %>

<wm:admin pagetitle="Feature Toggles" webpackScript="admin">

	<script>
		var config = {
			mode: 'devFeatureToggles'
		};
	</script>

	<div class="sidebar admin">
		<c:import url="/WEB-INF/views/web/partials/admin/quick_links.jsp"/>
	</div>

	<div class="content">
		<h1 class="strong">Feature Toggles</h1>

		<c:import url="/WEB-INF/views/web/partials/general/notices.jsp">
			<c:param name="bundle" value="${bundle}"/>
		</c:import>

		<div class="row">
			<div class="span8">
				<div class="well">
					<select id="feature_toggle_action">
						<option value="no_action">Choose Action</option>
						<option value="add_feature">Add Feature</option>
						<option value="add_segment">Add Segment</option>
						<option value="add_segment_reference">Add Segment Reference</option>
						<option value="update_feature">Update Feature</option>
						<option value="remove_feature">Remove Feature</option>
						<option value="remove_segment">Remove Segment</option>
						<option value="remove_reference">Remove Reference</option>
					</select>
					<div id="add_feature" class="dn toggle_div"></div>
					<div id="add_segment" class="dn toggle_div"></div>
					<div id="add_segment_reference" class="dn toggle_div"></div>
					<div id="update_feature" class="dn toggle_div"></div>
					<div id="remove_feature" class="dn toggle_div"></div>
					<div id="remove_segment" class="dn toggle_div"></div>
					<div id="remove_reference" class="dn toggle_div"></div>
				</div>
			</div>
		</div>

		<div class="row-fluid">
			<div class="span16">
				<div id="features"></div>
			</div>
		</div>
	</div>

	<script type="text/template" id="add_feature_template">
		<form id="add_feature_form" class="ajax_form form-horizontal">
			<fieldset>
				<legend>Add Feature</legend>
				<div class="control-group">
					<label for="add_feature_feature_name" class="required control-label">Feature Name: </label>
					<div class="controls">
						<input id="add_feature_feature_name" name="feature_name" type="text"/>
					</div>
				</div>
				<div class="control-group">
					<label for="add_feature_allowed" class="required control-label">Allowed?: </label>
					<div class="controls">
						<select id="add_feature_allowed" name="is_allowed">
							<option>true</option>
							<option>false</option>
						</select>
					</div>
				</div>
				<div class="control-group">
					<label for="add_feature_segment_name" class="control-label">Segment Name: </label>
					<div class="controls">
						<input id="add_feature_segment_name" name="segment_name" type="text"/>
					</div>
				</div>
				<div class="control-group">
					<label for="add_feature_reference_value" class="control-label">Reference Value: </label>
					<div class="controls">
						<input id="add_feature_reference_value" name="reference_value" type="text"/>
					</div>
				</div>
				<div class="wm-action-container">
					<input type="button" class="button form_button" data-action="/admin/features/add_feature" value="Add Feature">
				</div>
			</fieldset>
		</form>
	</script>

	<script type="text/template" id="add_segment_template">
		<form id="add_segment_form" class="ajax_form form-horizontal">
			<fieldset>
				<legend>Add Segment</legend>
				<div class="control-group">
					<label for="add_segment_feature_name" class="required control-label">Feature Name: </label>
					<div class="controls">
						<select id="add_segment_feature_name" name="feature_name">
							<option>Select Feature</option>
							{{ _.chain(data).keys().sortBy(function (feature) { return feature.toLowerCase(); }).each(function (featureName) { }}
							<option>{{= featureName }}</option>
							{{ }); }}
						</select>
					</div>
				</div>
				<div class="control-group">
					<label for="add_segment_segment_name" class="required control-label">Segment Name: </label>
					<div class="controls">
						<input id="add_segment_segment_name" name="segment_name" type="text">
					</div>
				</div>
				<div class="control-group">
					<label for="add_segment_reference_value" class="required control-label">Reference Value: </label>
					<div class="controls">
						<input id="add_segment_reference_value" name="reference_value" type="text">
					</div>
				</div>
				<div class="form-actions">
					<input type="button" class="button form_button" data-action="/admin/features/add_segment" value="Add Segment" />
				</div>
			</fieldset>
		</form>
	</script>

	<script type="text/template" id="add_segment_reference_template">
		<form id="add_segment_reference_form" class="ajax_form form-horizontal">
			<fieldset>
				<legend>Add Segment Reference</legend>
				<div class="control-group">
					<label for="add_segment_reference_feature_name" class="required control-label">Feature Name: </label>
					<div class="controls">
						<select id="add_segment_reference_feature_name" name="feature_name">
							<option>Select Feature</option>
							{{ _.chain(data).keys().sortBy(function (feature) { return feature.toLowerCase(); }).each(function (featureName) { }}
							<option>{{= featureName }}</option>
							{{ }); }}
						</select>
					</div>
				</div>
				<div class="control-group">
					<label for="add_segment_reference_segment_name" class="required control-label">Segment Name: </label>
					<div class="controls">
						<select id="add_segment_reference_segment_name" name="segment_name">
							<option>Select Segment</option>
						</select>
					</div>
				</div>
				<div class="control-group">
					<label for="add_segment_reference_reference_value" class="required control-label">Reference Value: </label>
					<div class="controls">
						<input id="add_segment_reference_reference_value" name="reference_value" type="text"/>
					</div>
				</div>
				<div class="wm-action-container">
					<input type="button" class="button form_button" data-action="/admin/features/add_segment" value="Add Segment Reference"/>
				</div>
			</fieldset>
		</form>
	</script>

	<script type="text/template" id="update_feature_template">
		<form id="update_feature_form" class="ajax_form form-horizontal">
			<fieldset>
				<legend>Update Feature</legend>
				<div class="control-group">
					<label for="update_feature_feature_name" class="required control-label">Feature Name: </label>
					<div class="controls">
						<select id="update_feature_feature_name" name="feature_name">
							<option>Select Feature</option>
							{{ _.chain(data).keys().sortBy(function (feature) { return feature.toLowerCase(); }).each(function (featureName) { }}
							<option>{{= featureName }}</option>
							{{ }); }}
						</select>
					</div>
				</div>
				<div class="control-group">
					<label for="update_feature_allowed" class="required control-label">Allowed?: </label>
					<div class="controls">
						<select id="update_feature_allowed" name="is_allowed">
							<option>true</option>
							<option>false</option>
						</select>
					</div>
				</div>
				<div class="wm-action-container">
					<input type="button" class="button form_button" data-action="/admin/features/update_feature" value="Update Feature"/>
				</div>
			</fieldset>
		</form>
	</script>

	<script type="text/template" id="remove_feature_template">
		<form id="remove_feature_form" class="ajax_form form-horizontal">
			<fieldset>
				<legend>Remove Feature</legend>
				<div class="control-group">
					<label for="remove_feature_feature_name" class="required control-label">Feature Name: </label>
					<div class="controls">
						<select id="remove_feature_feature_name" name="feature_name">
							<option>Select Feature</option>
							{{ _.chain(data).keys().sortBy(function (feature) { return feature.toLowerCase(); }).each(function (featureName) { }}
							<option>{{= featureName }}</option>
							{{ }); }}
						</select>
					</div>
				</div>
				<div class="wm-action-container">
					<input type="button" class="button form_button" data-action="/admin/features/remove_feature"  value="Remove Feature"/>
				</div>
			</fieldset>
		</form>
	</script>

	<script type="text/template" id="remove_segment_template">
		<form id="remove_segment_form" class="ajax_form form-horizontal">
			<fieldset>
				<legend>Remove Segment</legend>
				<div class="control-group">
					<label for="remove_segment_feature_name" class="required control-label">Feature Name: </label>
					<div class="controls">
						<select id="remove_segment_feature_name" name="feature_name">
							<option>Select Feature</option>
							{{ _.chain(data).keys().sortBy(function (feature) { return feature.toLowerCase(); }).each(function (featureName) { }}
							<option>{{= featureName }}</option>
							{{ }); }}
						</select>
					</div>
				</div>
				<div class="control-group">
					<label for="remove_segment_segment_name" class="required control-label">Segment Name: </label>
					<div class="controls">
						<select id="remove_segment_segment_name" name="segment_name">
							<option>Select Segment</option>
						</select>
					</div>
				</div>
				<div class="wm-action-container">
					<input type="button" class="button form_button" data-action="/admin/features/remove_segment" value="Remove Segment"/>
				</div>
			</fieldset>
		</form>
	</script>

	<script type="text/template" id="remove_reference_template">
		<form id="remove_reference_form" class="ajax_form form-horizontal">
			<fieldset>
				<legend>Remove Reference</legend>
				<div class="control-group">
					<label for="remove_reference_feature_name" class="required control-label">Feature Name: </label>
					<div class="controls">
						<select id="remove_reference_feature_name" name="feature_name">
							<option>Select Feature</option>
							{{ _.chain(data).keys().sortBy(function (feature) { return feature.toLowerCase(); }).each(function (featureName) { }}
							<option>{{= featureName }}</option>
							{{ }); }}
						</select>
					</div>
				</div>
				<div class="control-group">
					<label for="remove_reference_segment_name" class="required control-label">Segment Name: </label>
					<div class="controls">
						<select id="remove_reference_segment_name" name="segment_name">
							<option>Select Segment</option>
						</select>
					</div>
				</div>
				<div class="control-group">
					<label for="remove_reference_reference_value" class="required control-label">Reference Value: </label>
					<div class="controls">
						<select id="remove_reference_reference_value" name="reference_value">
							<option>Select Reference</option>
						</select>
					</div>
				</div>
				<div class="form-actions">
					<input type="button" class="button form_button" data-action="/admin/features/remove_reference" value="Remove Reference"/>
				</div>
			</fieldset>
		</form>
	</script>

	<script type="text/javascript">
		function isEmpty(object) {
			for (var property in object) {
				if (object.hasOwnProperty(property))
					return false;
			}
			return true;
		}
	</script>

	<script type="text/template" id="features_template">
		<table class="table table-striped">
			<thead>
			<tr>
				<th>Feature</th>
				<th>Enabled</th>
				<th>Segments</th>
			</tr>
			</thead>
			<tbody>
			{{ _.chain(data).keys().sortBy(function (feature) { return feature.toLowerCase(); }).each(function (featureName) { }}
			<tr>
				<td>{{= featureName }}</td>
				<td>{{= data[featureName].Enabled }}</td>
				<td>
					{{ var segments = Object.keys(data[featureName].segments); }}
					{{ if (!isEmpty(segments)) { }}
					{{ _.each(segments, function(segmentName) { }}
					<strong>{{= segmentName }}:</strong> {{= data[featureName].segments[segmentName] }}<br/>
					{{ }); }}
					{{ } else { }}
					<strong>Global</strong>
					{{ } }}
				</td>
			</tr>
			{{ }); }}
			</tbody>
		</table>
	</script>

</wm:admin>
