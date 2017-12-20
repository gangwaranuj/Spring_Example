<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="wmfmt" uri="http://www.workmarket.com/taglib/fmt" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<%-- currently used for USA only --%>

<script id="tmpl-tax-sign" type="text/x-jquery-tmpl">

	<div id="w9-bg" class="pr">
		{{if business_flag}}
			<div id="tax-name">\${last_name}</div>
		{{else}}
			<div id="tax-name">\${first_name} \${middle_name} \${last_name}</div>
		{{/if}}

		<div id="business-name">\${business_name}</div>

		<div id="individual">
			<input type="checkbox" disabled {{if tax_entity_type_code && tax_entity_type_code ===
			'individual'}}checked="checked"{{/if}}/>
		</div>
		<div id="c-corporation">
			<input type="checkbox" disabled {{if tax_entity_type_code && tax_entity_type_code ===
			'c_corp'}}checked="checked"{{/if}}/>
		</div>
		<div id="s-corporation">
			<input type="checkbox" disabled {{if tax_entity_type_code && tax_entity_type_code ===
			's_corp'}}checked="checked"{{/if}}/>
		</div>
		<div id="partnership">
			<input type="checkbox" disabled {{if tax_entity_type_code && tax_entity_type_code ===
			'partner'}}checked="checked"{{/if}}/>
		</div>
		<div id="trust">
			<input type="checkbox" disabled {{if tax_entity_type_code && tax_entity_type_code ===
			'trust'}}checked="checked"{{/if}}/>
		</div>
		<div id="llc">
			<input type="checkbox" disabled {{if tax_entity_type_code && (tax_entity_type_code ===
			'llc-c-corp' || tax_entity_type_code === 'llc-s-corp' || tax_entity_type_code ===
			'llc-part')}}checked="checked"{{/if}}/>
		</div>

		{{if tax_entity_type_code && tax_entity_type_code === 'llc-c-corp'}}
		<div class="llc-type">C</div>
		{{/if}}

		{{if tax_entity_type_code && tax_entity_type_code === 'llc-s-corp'}}
		<div class="llc-type">S</div>
		{{/if}}

		{{if tax_entity_type_code && tax_entity_type_code === 'llc-part'}}
		<div class="llc-type">P</div>
		{{/if}}

		<div id="address">
			\${address}
		</div>

		<div id="city-state-postal_code">
			\${city}, \${state}, \${postal_code}
		</div>

		<div id="sign-here">
			<input name="signature" maxlength="255" class="large" style="background-color: #ffc"/>
		</div>

		{{if business_flag}}
			<div id="ein">
				\${tax_number}
			</div>
		{{else}}
			<div id="ssn">
				\${tax_number}
			</div>
		{{/if}}
		<div id="sign-date">
			<input name="signature_date_string" maxlength="10" class="small" style="background-color: #ffc"/>
		</div>
	</div>

	<br/>

	<div class="wm-action-container">
		<button type="button" class="button" id="go-back-edit-form"><fmt:message key="global.go_back"/></button>
		<button type="button" class="button" id="save-signed-tax-form"><fmt:message key="global.submit"/></button>
	</div>


</script>
