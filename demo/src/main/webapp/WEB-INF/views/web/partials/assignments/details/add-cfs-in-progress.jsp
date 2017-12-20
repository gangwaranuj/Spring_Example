<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>

<a class="toggle-add-set" href="javascript:void(0)" style="width: auto;"><small>Show add custom field set</small></a>

<div class="controls dn" id="add-cf-container">

	<select id="custom-fields-dropdown" class="cfs-dropdown">
		<option value="" disabled selected>- Select -</option>
		<c:forEach items="${client_only_cf_sets}" var="option">
			<option value='${option.key}'><c:out value="${option.value}" /></option>
		</c:forEach>
	</select>
	<a href="javascript:void(0);" id="add-client-field-set" class="button add-client-field-set">Add</a>

	<div class="form-horizontal">
		<div id="attached_sets_input" class="dn"></div>

		<div id="attached_sets" class="dn attached-sets">
			<fieldset>
				<legend>New Pending Field Sets Order</legend>
				<ul id="attached_field_sets_holder" class="cfs-pending-holder"></ul>
			</fieldset>
		</div>

		<div id="buyer_custom" class="dn buyer_cfs">
			<fieldset>
				<legend>
					New Pending Custom Fields
				</legend>
				<table id="buyer_custom_fields_holder"><tbody></tbody></table>
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
				<textarea
				 	class="customfields[\${parentId}] field_value"
				 	data-id="\${id}"
				 	data-required="\${required}"
				 	data-parent="\${parentId}"
				 	data-name="\${cfName}"
				 	data-position="\${cfPosition}"
				 	name="customfields[\${parentId}][\${id}]"
				 	id="customfields[\${parentId}][\${id}]"
					value= {{if defaults && defaults.length && !value.length}}"\${defaults}"{{else}}"\${value}"{{/if}}
				>{{if defaults && defaults.length && !value.length}}\${defaults}{{else}}\${value}{{/if}}</textarea>
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
				<select
					class="customfields[\${parentId}]"
					name="customfields[\${parentId}][\${id}]"
					data-required="\${required}"
					data-name="\${cfName}"
					data-parent="\${parentId}"
					data-dropdown="true"
					data-position="\${cfPosition}"
					data-id="\${id}"
					id="customfields[\${parentId}][\${id}]"
				>
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