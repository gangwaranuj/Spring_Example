<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<style type="text/css">
	#attached_sets ul {padding-left: 155px;}
</style>

<a name="customfieldsanchor"></a>
<div id="assignment-custom-fields" class="inner-container">
	<div class="page-header">
		<h4>Custom Fields</h4>
	</div>

	<div id="custom-fields-container">
		<div class="control-group">
			<label for="custom-fields-dropdown" class="control-label">Select a field set</label>
			<div class="controls">
				<form:select path="" id="custom-fields-dropdown">
					<form:option value="">Select</form:option>
					<form:options items="${customfields}"/>
				</form:select>
				<a href="#" id="add-field-set-button" class="button" onclick='return false'>Add</a>
			</div>
		</div>

		<div id="attached_sets_input" class="clearfix dn"></div>

		<div id="attached_sets" class="clearfix dn">
			<fieldset>
				<legend>Attached Field Sets</legend>
				<ul id="attached_field_sets_holder" style="list-style: none;"></ul>
			</fieldset>
		</div>

		<div id="buyer_custom" class="clearfix dn">
			<fieldset>
				<legend>
					Your Fields
					<span class="help-inline">(only you can edit)</span>
				</legend>
				<table id="buyer_custom_fields_holder"><tbody></tbody></table>
			</fieldset>
		</div>

		<div id="resource_custom" class="clearfix dn">
			<fieldset>
				<legend>
					Worker Fields
					<span class="help-inline">(assigned worker can edit)</span>
				</legend>
				<table id="resource_custom_fields_holder"><tbody></tbody></table>
			</fieldset>
		</div>
	</div>
</div>

<script id="customfields_template" type="text/x-jquery-tmpl">
	<div>
		<div class="control-group">
			<label for="customfields[\${parentId}][\${id}]"
				   class="control-label {{if required && type == 'owner'}} required{{/if}}">
				\${name}
			</label>
			<div class="controls">
				<textarea class="field_value" name="customfields[\${parentId}][\${id}]" id="customfields[\${parentId}][\${id}]" maxlength="1000"
				value= {{if defaults && defaults.length && !value.length}}"\${defaults}"{{else}}"\${value}"{{/if}}>{{if defaults && defaults.length && !value.length}}\${defaults}{{else}}\${value}{{/if}}</textarea>
				{{if required && type == 'resource'}}<span class="meta help-inline">Required</span>{{/if}}
			</div>
		</div>
	</div>
</script>

<script id="customfields_dropdown_template" type="text/x-jquery-tmpl">
	<div>
		<div class="control-group">
			<label for="customfields[\${parentId}][\${id}]"
				   class="control-label {{if required && type == 'owner'}} required{{/if}}">
				\${name}
			</label>
			<div class="controls">
				<select name="customfields[\${parentId}][\${id}]" id="customfields[\${parentId}][\${id}]">
					<option value="">Select</option>
					{{each(i,v) options}}
					<option value="\${v}" {{if v == value}}selected="selected"{{/if}}>\${v}</option>
					{{/each}}
				</select>
				{{if required && type == 'resource'}}<span class="meta help-inline">Required</span>{{/if}}
			</div>
		</div>
	</div>
</script>

<script id="attached_field_sets_template" type="text/x-jquery-tmpl">
	<li {{if required}} class="requiredSet" {{/if}}>
	<div id="\${id}">
		\${name}
		{{if required}}
		<small class="meta">(Required)</small>
		{{/if}}
		{{if !required}}
		<a href="javascript:void(0);" class="icon-delete cta-remove-choice" title="Delete Answer">Delete Choice</a>
		<a href="javascript:void(0);" class="sort_handle icon-sort" title="Change answer order">Sort Choice</a>
		{{/if}}
	</div>
	</li>
</script>