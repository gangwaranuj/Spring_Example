<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="wm-csrf" uri="http://www.workmarket.com/taglib/csrf" %>
<%@ taglib prefix="wmfmt" uri="http://www.workmarket.com/taglib/fmt" %>
<%@ taglib prefix="wm" tagdir="/WEB-INF/tags" %>

<wm:app bodyclass="accountSettings" pagetitle="Custom Report" breadcrumbSection="Reports" breadcrumbSectionURI="/reports" breadcrumbPage="New Report" webpackScript="reports">

	<script>
		var config = {
			mode: 'results',
			savedReportKey: '${wmfmt:escapeJavaScript(report_id)}',
			name: '${wmfmt:escapeJavaScript(report_name)}',
			filterTypes: {
				dateRange: parseInt('${wmfmt:escapeJavaScript(work_date_range)}')
			}
		};
	</script>

	<div id="report_messages">
		<c:import url="/WEB-INF/views/web/partials/message.jsp"/>
	</div>

	<div id="reporting_work_display">
		<div class="clearfix dn" id="work_report_types_main">
			<label for="work_report_types">Please select a Report Type:</label>

			<div class="inputs">
				<select id="work_report_types" name="workReportTypes">
					<option value="-1">Select</option>
					<c:forEach var="item" items="${workReportTypes}">
						<option value="${item.key}"><c:out value="${item.value}" /></option>
					</c:forEach>
				</select>
			</div>
		</div>

		<form action="/" method="post" id="work_report_filter_form" name="work_report_filter_form" accept-charset="utf-8" class="clear dn">
			<wm-csrf:csrfToken />
			<div id="fixed_filters_wrap" class="panel">
				<div id="fixed_filters" class="clear">
					<div id="work_report_filter_main"></div>
				</div>
				<a href="javascript:void(0);" id="work_report_export" class="button"><i class="icon-share-alt"></i> Export to CSV</a>
				<a href="javascript:void(0);" id="toggle-recurrence" class="button"><i class="icon-user"></i></a>
				<a href="/reports/custom/manage?reportKey=${report_id}" class="button"><i class="icon-edit"></i> Edit Report</a>
			</div>
		</form>

		<div id="work_report_export_show" class="modal hide" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
			<div class="modal-header">
				<h3>Email Report</h3>
			</div>
			<div class="modal-body" id="export-report-message"></div>
			<div class="wm-action-container">
				<button class="button" data-dismiss="modal" aria-hidden="true">Close</button>
			</div>
		</div>

		<div id="reporting-recurrence-settings" class="dn">
			<c:import url="/WEB-INF/views/web/partials/reports/recurrence.jsp"/>
		</div>

		<div id="report_list_container">
			<div id="report_display_header_wrap"></div>

			<img id="results_spinner" src="${mediaPrefix}/images/loading.gif" alt="Loading" height="20"
				 width="20" class="vab dn"/>
			<table id="report_display" class="table-report panel">
				<thead></thead>
				<tbody></tbody>
			</table>

		</div>

	</div>

<script type="text/javascript">
		function isSelect(value, selectedList) {
			for (var i in selectedList) {
				if (selectedList[i] === value) {
					return 'selected="selected"';
				}
			}
			return '';
		}
</script>

<script id="work_report_entity_bucket-tmpl" type="text/x-jquery-tmpl">
	<div class="bucket">
		<div class="panel-heading">
		<a id="work_report_regenerate" class="button pull-right">Run Report</a>
		<h3 class="display-report-name">
			\${display_name}
		</h3>
		</div>
		{{if filtering_entity_responses.length > 0}}
		<div class="section-wrapper">
			{{each(i, entity) filtering_entity_responses}}
			<div class="clear">
				<ul class="reports-inputs-list">
					<li>
							<span class="span3">
							<label class="">
								<input type="hidden"
									   name="\${entity.work_report_entity_response.key_name}_\${entity.work_report_entity_response.work_report_column_type}"
									   value="\${entity.work_report_entity_response.work_report_column_type}"/>
								<span>\${entity.work_report_entity_response.display_name}</span>
							</label>
							</span>
						{{if entity.work_report_entity_response.filterable}}
						{{if entity.html_tag_type_thrift == 'INPUT_TEXT'}}
						<input class="span6" type="text" name="${htmlTagTypes['INPUT_TEXT']}_\${entity.work_report_entity_response.work_report_column_type}_\${entity.work_report_entity_response.key_name}" value="{{if input_values}}\${input_values.field_value}{{/if}}" />
						{{/if}}
						{{if entity.html_tag_type_thrift == 'SELECT_OPTION'}}
						<select class="span3"
								name="${htmlTagTypes['SELECT_OPTION']}_\${work_report_entity_response.work_report_column_type}_\${work_report_entity_response.key_name}_select">
							{{each select_option_thrifts}}
							{{if input_values}}
							<option value="\${value}"
							\${isSelect(value,input_values.field_value)}>\${label}</option>
							{{else}}
							<option value="\${value}">\${label}</option>
							{{/if}}
							{{/each}}
						</select>
						{{/if}}
						{{if entity.html_tag_type_thrift == 'MULTI_SELECT_OPTION'}}
						<select class="span3"
								name="${htmlTagTypes['MULTI_SELECT_OPTION']}_\${entity.work_report_entity_response.work_report_column_type}_\${work_report_entity_response.key_name}_multiselect"
								multiple="multiple">
							{{each select_option_thrifts}}
								{{if value === "pleaseSelect"}}
									<option value disabled>\${label}</option>
								{{else}}
									<option value="\${value}"\${isSelect(value,input_values.field_value)}>\${label}</option>
								{{/if}}
							{{/each}}
						</select>
						{{/if}}
						{{if entity.html_tag_type_thrift == 'DATE'}}
						<input type="text"
							   name="${htmlTagTypes['DATE']}_\${work_report_entity_response.work_report_column_type}_\${work_report_entity_response.key_name}"
							   class="small date" placeholder="MM/DD/YYYY" value="{{if input_values}}\${input_values.field_value}{{/if}}"/>
						{{/if}}
						{{if entity.html_tag_type_thrift == 'TO_FROM_DATES'}}
						<select class="span3 date-filter" name="${htmlTagTypes['TO_FROM_DATES']}_\${work_report_entity_response.work_report_column_type}_select_from">
							{{if input_values}}
								{{if !entity.work_report_entity_response.future}}
									{{tmpl({selected: input_values.filtering_type_thrift}) "#work_report_past_filtering_type_thrift_select_en-tmpl"}}
								{{else}}
									{{tmpl({selected: input_values.filtering_type_thrift}) "#work_report_filtering_type_thrift_select_en-tmpl"}}
								{{/if}}
							{{else}}
								{{if !entity.work_report_entity_response.future}}
									{{tmpl({selected: ''}) "#work_report_past_filtering_type_thrift_select_en-tmpl"}}
								{{else}}
									{{tmpl({selected: ''}) "#work_report_filtering_type_thrift_select_en-tmpl"}}
								{{/if}}
							{{/if}}
						</select>
						{{if input_values}}
						{{if input_values.filtering_type_thrift === 4 || input_values.filtering_type_thrift === "WORK_DATE_RANGE"}}
						<span class="date-section">
						{{else}}
						<span class="date-section" style="display: none;">
						{{/if}}
						{{else}}
						<span class="date-section" style="display: none;">
						{{/if}}
						<input type="text" readonly
							   style="background-color: transparent"
							   name="${htmlTagTypes['TO_FROM_DATES']}_\${work_report_entity_response.work_report_column_type}_\${work_report_entity_response.key_name}_from"
							   class="input-small date" placeholder="MM/DD/YYYY" value="{{if input_values}}\${input_values.from_value}{{/if}}"/>
						<input type="text" readonly
							   style="background-color: transparent"
							   name="${htmlTagTypes['TO_FROM_DATES']}_\${work_report_entity_response.work_report_column_type}_\${work_report_entity_response.key_name}_to"
							   class="input-small date" placeholder="MM/DD/YYYY" value="{{if input_values}}\${input_values.to_value}{{/if}}"/>
						</span>
						{{/if}}
						{{if entity.html_tag_type_thrift == 'NUMERIC'}}
						<select class="span3" name="${htmlTagTypes['NUMERIC']}_\${work_report_entity_response.work_report_column_type}_select_filter">
							{{if input_values}}
							{{tmpl({selected: input_values.filtering_type_thrift}) "#work_report_relational_operator_select_en-tmpl"}}
							{{else}}
							{{tmpl({selected: ''}) "#work_report_relational_operator_select_en-tmpl"}}
							{{/if}}
						</select>
						<input type="text"
							   name="${htmlTagTypes['NUMERIC']}_\${work_report_entity_response.work_report_column_type}_\${work_report_entity_response.key_name}_filter"
							   class="small"/>
						{{/if}}
						{{if entity.html_tag_type_thrift =='NUMERIC_RANGE'}}
						<select class="span3" name="${htmlTagTypes['NUMERIC_RANGE']}_\${work_report_entity_response.work_report_column_type}_select_from">
							{{if input_values}}
							{{tmpl({selected: input_values.from_operator}) "#work_report_relational_operator_select_en-tmpl"}}
							{{else}}
							{{tmpl({selected: ''}) "#work_report_relational_operator_select_en-tmpl"}}
							{{/if}}
						</select>
						<input type="text"
							   name="${htmlTagTypes['NUMERIC_RANGE']}_\${work_report_entity_response.work_report_column_type}_\${work_report_entity_response.key_name}_from"
							   class="small" value="{{if input_values}}\${input_values.from_value}{{/if}}"/>
						<select class="span3" name="${htmlTagTypes['NUMERIC_RANGE']}_\${work_report_entity_response.work_report_column_type}_select_to">
							{{if input_values}}
							{{tmpl({selected: input_values.to_operator}) "#work_report_relational_operator_select_en-tmpl"}}
							{{else}}
							{{tmpl({selected: ''}) "#work_report_relational_operator_select_en-tmpl"}}
							{{/if}}
						</select>
						<input type="text"
							   name="${htmlTagTypes['NUMERIC_RANGE']}_\${work_report_entity_response.work_report_column_type}_\${work_report_entity_response.key_name}_to"
							   class="small" value="{{if input_values}}\${input_values.to_value}{{/if}}"/>
						{{/if}}
						{{/if}}
					</li>
				</ul>
			</div>
			{{/each}}
		</div>
		<hr style="margin:15px 0"/>
		{{else}}

		{{/if}}
	</div>
</script>

<script id="work_report_relational_operator_select_en-tmpl" type="text/x-jquery-tmpl">
	<option value="${requestScope.relationalOperators['WORK_PLEASE_SELECT']}"
	{{if selected == 'WORK_PLEASE_SELECT'}}selected="selected"{{/if}}>Select to Filter</option>
	<option value="${requestScope.relationalOperators['WORK_EQUAL_TO']}"
	{{if selected == 'WORK_tEQUAL_TO'}}selected="selected"{{/if}}>Equal To</option>
	<option value="${requestScope.relationalOperators['WORK_GREATER_THAN_EQUAL_TO']}"
	{{if selected == 'WORK_GREATER_THAN_EQUAL_TO'}}selected="selected"{{/if}}>Greater Than Equal To</option>
	<option value="${requestScope.relationalOperators['WORK_LESS_THAN']}"
	{{if selected == 'WORK_LESS_THAN'}}selected="selected"{{/if}}>Less Than</option>
</script>

<script id="work_report_filtering_type_thrift_select_en-tmpl" type="text/x-jquery-tmpl">
	<option value="${requestScope.relationalOperators['WORK_PLEASE_SELECT']}"
	{{if selected == 'WORK_PLEASE_SELECT'}}selected="selected"{{/if}}>Select to Filter</option>
	<option value="${requestScope.filterTypes['WORK_NEXT_1_DAY']}"
	{{if selected == 'WORK_NEXT_1_DAY'}}selected="selected"{{/if}}>Next 24 Hours</option>
	<option value="${requestScope.filterTypes['WORK_NEXT_7_DAYS']}"
	{{if selected == 'WORK_NEXT_7_DAYS'}}selected="selected"{{/if}}>Next 7 Days</option>
	<option value="${requestScope.filterTypes['WORK_NEXT_30_DAYS']}"
	{{if selected == 'WORK_NEXT_30_DAYS'}}selected="selected"{{/if}}>Next 30 Days</option>
	<option value="${requestScope.filterTypes['WORK_NEXT_60_DAYS']}"
	{{if selected == 'WORK_NEXT_60_DAYS'}}selected="selected"{{/if}}>Next 60 Days</option>
	<option value="${requestScope.filterTypes['WORK_NEXT_90_DAYS']}"
	{{if selected == 'WORK_NEXT_90_DAYS'}}selected="selected"{{/if}}>Next 90 Days</option>
	<option value="${requestScope.filterTypes['WORK_LAST_1_DAY']}"
	{{if selected == 'WORK_LAST_1_DAY'}}selected="selected"{{/if}}>Last 24 Hours</option>
	<option value="${requestScope.filterTypes['WORK_LAST_7_DAYS']}"
	{{if selected == 'WORK_LAST_7_DAYS'}}selected="selected"{{/if}}>Last 7 Days</option>
	<option value="${requestScope.filterTypes['WORK_LAST_30_DAYS']}"
	{{if selected == 'WORK_LAST_30_DAYS'}}selected="selected"{{/if}}>Last 30 Days</option>
	<option value="${requestScope.filterTypes['WORK_LAST_60_DAYS']}"
	{{if selected == 'WORK_LAST_60_DAYS'}}selected="selected"{{/if}}>Last 60 Days</option>
	<option value="${requestScope.filterTypes['WORK_LAST_90_DAYS']}"
	{{if selected == 'WORK_LAST_90_DAYS'}}selected="selected"{{/if}}>Last 90 Days</option>
	<option value="${requestScope.filterTypes['WORK_LAST_365_DAYS']}"
	{{if selected == 'WORK_LAST_365_DAYS'}}selected="selected"{{/if}}>Last 365 Days</option>
	<option value="${requestScope.filterTypes['WORK_THIS_YEAR_TO_DATE']}"
	{{if selected == 'WORK_THIS_YEAR_TO_DATE'}}selected="selected"{{/if}}>Year to Date</option>
	<option value="${requestScope.filterTypes['WORK_DATE_RANGE']}"
	{{if selected == 'WORK_DATE_RANGE'}}selected="selected"{{/if}}>Date Range</option>
</script>

	<script id="work_report_past_filtering_type_thrift_select_en-tmpl" type="text/x-jquery-tmpl">
	<option value="${requestScope.relationalOperators['WORK_PLEASE_SELECT']}"
	{{if selected == 'WORK_PLEASE_SELECT'}}selected="selected"{{/if}}>Select to Filter</option>
	<option value="${requestScope.filterTypes['WORK_LAST_1_DAY']}"
	{{if selected == 'WORK_LAST_1_DAY'}}selected="selected"{{/if}}>Last 24 Hours</option>
	<option value="${requestScope.filterTypes['WORK_LAST_7_DAYS']}"
	{{if selected == 'WORK_LAST_7_DAYS'}}selected="selected"{{/if}}>Last 7 Days</option>
	<option value="${requestScope.filterTypes['WORK_LAST_30_DAYS']}"
	{{if selected == 'WORK_LAST_30_DAYS'}}selected="selected"{{/if}}>Last 30 Days</option>
	<option value="${requestScope.filterTypes['WORK_LAST_60_DAYS']}"
	{{if selected == 'WORK_LAST_60_DAYS'}}selected="selected"{{/if}}>Last 60 Days</option>
	<option value="${requestScope.filterTypes['WORK_LAST_90_DAYS']}"
	{{if selected == 'WORK_LAST_90_DAYS'}}selected="selected"{{/if}}>Last 90 Days</option>
	<option value="${requestScope.filterTypes['WORK_LAST_365_DAYS']}"
	{{if selected == 'WORK_LAST_365_DAYS'}}selected="selected"{{/if}}>Last 365 Days</option>
	<option value="${requestScope.filterTypes['WORK_THIS_YEAR_TO_DATE']}"
	{{if selected == 'WORK_THIS_YEAR_TO_DATE'}}selected="selected"{{/if}}>Year to Date</option>
	<option value="${requestScope.filterTypes['WORK_DATE_RANGE']}"
	{{if selected == 'WORK_DATE_RANGE'}}selected="selected"{{/if}}>Date Range</option>
</script>

<script id="cell-title-tmpl" type="text/x-jquery-tmpl">
	<div>
		\${data}
	</div>
</script>

</wm:app>
