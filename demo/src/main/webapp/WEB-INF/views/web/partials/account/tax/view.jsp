<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="wmfmt" uri="http://www.workmarket.com/taglib/fmt" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<script id="tmpl-tax-view" type="text/x-jquery-tmpl">

{{if country === 'usa'}}
<p><fmt:message key="account_taxes.updated_info"/></p>

<div>
	<table>
		<tbody>
		{{if first_name }}
		<tr>
			<td><fmt:message key="global.name"/></td>
			<td>
				{{if business_flag}}
				\${last_name}
				{{else}}
				\${first_name} \${middle_name} \${last_name}
				{{/if}}
			</td>
		</tr>
		{{else}}
		<tr>
			<td><fmt:message key="global.name"/></td>
			<td>
				\${tax_name}
			</td>
		</tr>
		{{/if}}

		{{if business_name_flag && business_name}}
		<tr>
			<td><fmt:message key="global.business_name"/></td>
			<td>\${business_name}</td>
		</tr>
		{{/if}}

		{{if address}}
		<tr>
			<td><fmt:message key="global.address"/></td>
			<td> \${address ? address : '' }</td>
		</tr>
		{{/if}}

		{{if state || city || postal_code}}
		<tr>
			<td><fmt:message key="global.state_city_zip"/></td>
			<td> \${city ? city + ',' : ''} \${state ? state + ',' : ''} \${postal_code ? postal_code : ''}</td>
		</tr>
		{{/if}}

		{{if tax_entity_type_code}}
		{{if business_flag}}
		<tr>
			<td><fmt:message key="account_taxes.federal_tax_classification"/></td>
			<td>
				{{if tax_entity_type_code === 'llc-c-corp'}}
				<fmt:message key="account_taxes.limited_liability_company_c_corporation"/>
				{{else tax_entity_type_code === 'llc-s-corp'}}
				<fmt:message key="account_taxes.limited_liability_company_s_corporation"/>
				{{else tax_entity_type_code === 'llc-part'}}
				<fmt:message key="account_taxes.limited_liability_company_partnership"/>
				{{else tax_entity_type_code === 'llc-corp'}}
				<fmt:message key="account_taxes.limited_liability_company_corporation"/>
				{{else tax_entity_type_code === 'individual'}}
				<fmt:message key="account_taxes.individual_sole_proprietor"/>
				{{else tax_entity_type_code === 'c_corp'}}
				<fmt:message key="account_taxes.c_corporation"/>
				{{else tax_entity_type_code === 's_corp'}}
				<fmt:message key="account_taxes.s_corporation"/>
				{{else tax_entity_type_code === 'partnership' || tax_entity_type_code === 'partner'}}
				<fmt:message key="account_taxes.partnership"/>
				{{else tax_entity_type_code === 'trust'}}
				<fmt:message key="account_taxes.trust"/>
				{{else tax_entity_type_code === 'llc-dis'}}	<%-- historical data only --%>
				<fmt:message key="account_taxes.limited_liability_company_disregarded_entity"/>
				{{else tax_entity_type_code === 'exempt'}} 	<%-- historical data only --%>
				<fmt:message key="account_taxes.exempt"/>
				{{else tax_entity_type_code === 'corp'}} 	<%-- historical data only --%>
				<fmt:message key="account_taxes.corporation"/>
				{{/if}}
			</td>
		</tr>

		{{else}}

		<tr>
			<td><fmt:message key="account_taxes.federal_tax_classification"/></td>
			<td><fmt:message key="account_taxes.individual_sole_proprietor"/></td>
		</tr>
		{{/if}}
		{{/if}}



		{{if tax_verification_status_code}}
			{{if tax_verification_status_code === 'approved'}}
				<tr class="success">
			{{else tax_verification_status_code === 'unverified'}}
				<tr class="warning">
			{{else}}
				<tr class="error">
			{{/if}}
				<td><fmt:message key="account_taxes.internal_revenue_service_status"/></td>
				<td>
					{{if tax_verification_status_code === 'approved'}}
						<span class="label label-success"><fmt:message key="global.approved"/></span>
					{{else tax_verification_status_code === 'unverified'}}
						<span class="label label-warning"><fmt:message key="global.unverified"/></span>
					{{else}}
						<span class="label label-important"><fmt:message key="global.rejected"/></span>
					{{/if}}
				</td>
			</tr>
		{{/if}}
		</tbody>
	</table>


{{if tax_number}}
<h3><fmt:message key="account_taxes.part_taxpayer_identification_number"/></h3>

<table>
	<tbody>
	{{if business_flag}}

	<tr>
		<td><fmt:message key="account_taxes.employee_identification_number"/></td>
		<td class="input">\${this.formatEin(tax_number)}</td>
	</tr>
	{{else}}
	<tr>
		<td>{{if tax_number[0] === '9'}}
			<fmt:message key="account_taxes.individual_tax_information_number"/>
			{{else}}
			<fmt:message key="account_taxes.social_security_number"/>
			{{/if}}
		</td>
		<td> \${this.formatSsn(tax_number)}</td>
	</tr>
	{{/if}}
	</tbody>
</table>
{{/if}}


{{if signature && signature_date_string}}
<h3><fmt:message key="account_taxes.part_certification"/></h3>


<small>
	<fmt:message key="account_taxes.under_penalties_of_perjury"/>
	<ol>
		<li><fmt:message key="account_taxes.correct_tin"/></li>
		<li><fmt:message key="account_taxes.no_backup_withholding"/></li>
		<li><fmt:message key="account_taxes.is_us_citizen"/></li>
		<li><fmt:message key="account_taxes.the_facta_codes_entered"/></li>
	</ol>
</small>

<table>
	<tbody>
	<tr>
		<td><fmt:message key="account_taxes.signature_of_us_person"/></td>
		<td> \${signature}</td>
		<td> \${signature_date_string.substr(0, 10)} <%-- hide the time --%>
		</td>
	</tr>
	</tbody>
</table>

{{/if}}


{{else country === 'canada'}}


<div>
	<table>
		<tbody>
		{{if tax_number}}
		{{if business_flag}}
		<tr>
			<td><fmt:message key="account_taxes.business_numbers"/></td>
			<td class="input">\${this.formatBn(tax_number)}</td>
		</tr>
		{{else}}
		<tr>
			<td><fmt:message key="account_taxes.social_insurance_number"/></td>
			<td>\${this.formatSin(tax_number)}</td>
		</tr>
		{{/if}}
		{{/if}}

		{{if tax_name}}
		{{if business_flag}}
		<tr>
			<td><fmt:message key="global.company_name"/></td>
			<td>\${tax_name}</td>
		</tr>
		{{else}}
		<tr>
			<td><fmt:message key="global.name"/></td>
			<td>\${tax_name}</td>
		</tr>
		{{/if}}
		{{/if}}

		{{if address && state && city && postal_code}}
		<tr>
			<td><fmt:message key="global.address"/></td>
			<td> \${address}, \${city} \${state}, \${postal_code}</td>
		</tr>
		{{/if}}

		<tr>
			<td><fmt:message key="global.country"/></td>
			<td><fmt:message key="global.canada"/></td>
		</tr>
		</tbody>
	</table>


	{{else country === 'other'}}

	<div>
		<table>
			<tbody>

			{{if tax_name}}
			{{if business_flag}}
			<tr>
				<td><fmt:message key="global.company_name"/></td>
				<td>\${tax_name}</td>
			</tr>
			{{else}}
			<tr>
				<td><fmt:message key="global.name"/></td>
				<td>\${tax_name}</td>
			</tr>
			{{/if}}
			{{/if}}

			{{if tax_entity_type_code}}
			<tr>
				<td><fmt:message key="account_taxes.type_of_beneficial_owner"/></td>
				<td>{{if tax_entity_type_code === 'individual'}}
					<fmt:message key="account_taxes.individual"/>
					{{else tax_entity_type_code === 'corp'}}
					<fmt:message key="account_taxes.corporation"/>
					{{else tax_entity_type_code === 'partnership'}}
					<fmt:message key="account_taxes.partnership"/>
					{{else tax_entity_type_code === 'llc-dis'}}
					<fmt:message key="account_taxes.disregarded_entity"/>
					{{/if}}
				</td>
			</tr>
			{{/if}}

			{{if address}}
			<tr>
				<td><fmt:message key="account_taxes.permanent_residence_address"/></td>
				<td> \${address}</td>
			</tr>
			{{/if}}

			{{if city}}
			<tr>
				<td><fmt:message key="global.city_town"/></td>
				<td> \${city}</td>
			</tr>
			{{/if}}

			{{if state}}
			<tr>
				<td><fmt:message key="global.state_or_province"/></td>
				<td>\${state}</td>
			</tr>
			{{/if}}

			{{if postal_code}}
			<tr>
				<td><fmt:message key="global.postal_code"/></td>
				<td>\${postal_code}</td>
			</tr>
			{{/if}}

			{{if country_of_incorporation}}
			<tr>
				<td><fmt:message key="global.country"/></td>
				<td>\${country_of_incorporation}</td>
			</tr>
			{{/if}}

			{{if tax_number}}
			<tr>
				<td><fmt:message key="account_taxes.tax_identification_number"/></td>
				<td class="input">\${this.formatOther(tax_number)}</td>
			</tr>
			{{/if}}
			</tbody>
		</table>

		{{/if}}
</script>
