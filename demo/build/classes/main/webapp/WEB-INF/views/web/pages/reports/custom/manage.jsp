<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="wm-csrf" uri="http://www.workmarket.com/taglib/csrf" %>
<%@ taglib prefix="wmfmt" uri="http://www.workmarket.com/taglib/fmt" %>
<%@ taglib prefix="wm" tagdir="/WEB-INF/tags" %>

<wm:app bodyclass="accountSettings page-reports-creation" pagetitle="Build a Custom Report" breadcrumbSection="Reports" breadcrumbSectionURI="/reports" breadcrumbPage="New Report" webpackScript="reports">

	<script>
		var config = {
			mode: 'manage',
			savedReportKey: '${wmfmt:escapeJavaScript(saved_report_key)}',
			name: '${wmfmt:escapeJavaScript(report_name)}',
			filterTypes: {
				dateRange: parseInt('${wmfmt:escapeJavaScript(work_date_range)}')
			}
		};
	</script>


	<div id="report_messages">
		<c:import url="/WEB-INF/views/web/partials/message.jsp" />
	</div>

	<div id="reporting_work_display">
		<div class="control-group dn" id="work_report_types_main">
			<label for="work_report_types">Please select a Report Type:</label>
			<div class="controls">
				<select id="work_report_types" name="workReportTypes">
					<option value="-1">Select</option>
					<c:forEach var="item" items="${workReportTypes}">
						<option value="${item.key}"><c:out value="${item.value}" /></option>
					</c:forEach>
				</select>
			</div>
		</div>

		<div class="inner-container">
			<form action="/reports/custom/generate_report" method="POST" id="work_report_entity_bucket_form" name="work_report_entity_bucket_form" accept-charset="utf-8" class="dn">
				<wm-csrf:csrfToken />
				<span name="model"></span>
				<div class="page-header clear">
					<h3>
						<c:choose>
							<c:when test="${not empty report_name}"><span class="report-type">Edit Report</span>: ${report_name}</c:when>
							<c:otherwise>Build a Custom Report</c:otherwise>
						</c:choose>
					</h3>
				</div>

				<div class="alert alert-info">
					Select which fields to include on your report, and specify filters to limit your results. Once you&rsquo;ve selected the data you need, click Next to view, save or download the results.
					<strong><a href="https://workmarket.zendesk.com/hc/en-us/articles/210052687" target="_blank"> Learn more <i class="icon-info-sign"></i></a></strong>
				</div>

				<div id="work_report_entity_buckets_main">
					<p><img src="${mediaPrefix}/images/loading.gif" alt="Loading" height="16" width="16" class="vab"/> Loading report fields</p>
				</div>

				<div class="wm-action-container">
					<a class="button" href="/reports">Back</a>
					<button type="button" id="work_report_entity_bucket_next" class="button">Next Step</button>
				</div>
			</form>

			<form action="/" method="post" id="work_report_filter_form" name="work_report_filter_form" accept-charset="utf-8" class="clear dn">
				<wm-csrf:csrfToken />
				<div id="fixed_filters_wrap" class="panel">
					<div id="fixed_filters" class="clear">
						<div id="work_report_filter_main" class="panel-heading"></div>
						<div>
							<a href="javascript:void(0);" id="work_report_export" class="button"><i class="icon-share-alt"></i> Export to CSV</a>
							<a href="javascript:void(0);" id="toggle-recurrence" class="button"><i class="icon-user"></i> Schedule Report</a>
							<a href="javascript:void(0);" id="work_report_save" class="button copy-report">Save Report</a>
						</div>
					</div>


					<div id="reporting-recurrence-settings" class="dn">
						<c:import url="/WEB-INF/views/web/partials/reports/recurrence.jsp" />
					</div>
				</div>

				<div class="pull-left">
					Show
					<select name="rows_per_page" class="rows_per_page span2">
						<option value="50" selected="selected">50</option>
						<option value="100">100</option>
						<option value="150">150</option>
						<option value="200">200</option>
					</select>
					assignments per page
				</div>

				<div class="pagination pull-right">
					<ul>
						<li class="prev"><a>&laquo; Previous</a></li>
						<li class="status"><span>Page <span class="current_page">1</span> of <span class="num_pages">1</span></span></li>
						<li class="next"><a>Next &raquo;</a></li>
					</ul>
				</div>


				<div id="work_report_display" class="dataTables_wrapper clear panel"></div>

				<div class="pagination pull-left">
					Show
					<select name="rows_per_page" class="rows_per_page span2">
						<option value="50" selected="selected">50</option>
						<option value="100">100</option>
						<option value="150">150</option>
						<option value="200">200</option>
					</select>
					assignments per page
				</div>

				<div class="pull-right">
					<div class="pagination pull-right">
						<ul>
							<li class="prev"><a href="javascript:void(0)">&laquo; Previous</a></li>
							<li class="status"><span>Page <span class="current_page">1</span> of <span class="num_pages">1</span> &mdash; <span id="assignments_paging_total"></span> assignments total</span></li>
							<li class="next"><a href="javascript:void(0)">Next &raquo;</a></li>
						</ul>
					</div>
				</div>


			</form>

			<iframe name="download_iframe" id="download_iframe" class="dn"></iframe>


			<div id="work_report_save_modal" class="modal hide" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
				<div class="modal-header">
					<h3 class="copy-report">Save Report</h3>
				</div>
				<div class="modal-body">
					<div class="control-group">
						<label class="control-label" for="report_name">Report Name</label>
						<div class="controls">
							<input type="text" class="report_name" id="report_name" name="report_name" value=""/>
						</div>
					</div>
				</div>
				<div class="wm-action-container">
					<button class="button" data-dismiss="modal" aria-hidden="true">Close</button>
					<button class="button copy-report" id="work_report_save_submit">Save Report</button>
				</div>
			</div>

			<div id="work_report_export_show" class="modal hide" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
				<div class="modal-header">
					<h3>Email Report</h3>
				</div>
				<div class="modal-body" id="export-report-message"></div>
				<div class="wm-action-container">
					<button class="button" data-dismiss="modal" aria-hidden="true">Close</button>
				</div>
			</div>
		</div>
	</div>




	<script type="text/javascript">
		function isSelect (value,selectedList) {
			for (i in selectedList) {
				if (selectedList[i] === value) {
					return 'selected="selected"';
				}
			}
		}
	</script>

<script id="work_report_entity_bucket-tmpl" type="text/x-jquery-tmpl">
	<div class="bucket">
		{{if mode == 'edit'}}
			<a href="javascript:void(0)" id="work_report_regenerate" class="button pull-right">Refresh Results</a>
		{{/if}}
		<h4 id="work-report-display-name">
			{{if mode == 'edit'}}
			<span class="report-type">\${displayName}</span>
			<small><a id="add_additional_filters" href="javascript:void(0);" class="add_additional_filters">(Add more fields and filters)</a></small>
			{{else}}
			\${displayName}
			<span class="fr"><small><a href="javascript:void(0);" class="expand_collapse">Expand/Collapse</a></small></span>
			{{/if}}
		</h4>
		{{if filteringEntityResponses.length > 0}}
		<div class="section-wrapper {{if mode != 'edit'}}dn{{/if}}">
			{{if mode != 'edit'}}
			<div class="controls">
				<ul class="reports-inputs-list">
					<li>
						<label>
							<input type="checkbox" name="select_all" value="${workReportColumnTypes['WORK_SELECT_ALL']}" class="toggle_selectall"/>
							<span>Select All</span>
						</label>
					</li>
				</ul>
			</div>
			{{/if}}
			{{each(i, entity) filteringEntityResponses}}
			<div class="clear">
				<ul class="reports-inputs-list">
					<li>
						{{if mode == 'edit'}}
								<span class="span3">
								{{else}}
								<span class="span5">
								{{/if}}
								<label>
									{{if mode == 'edit'}}
									<input type="hidden" name="\${entity.workReportEntityResponse.keyName}_\${entity.workReportEntityResponse.workReportColumnType}" value="\${entity.workReportEntityResponse.workReportColumnType}" />
									<span class="">\${entity.workReportEntityResponse.displayName}</span>
									{{else}}
									{{if entity.isDisplay}}
									<input type="checkbox" name="\${entity.workReportEntityResponse.keyName}_\${entity.workReportEntityResponse.workReportColumnType}" value="\${entity.workReportEntityResponse.workReportColumnType}" class="toggle_field" checked="checked" />
									{{else}}
									<input type="checkbox" name="\${entity.workReportEntityResponse.keyName}_\${entity.workReportEntityResponse.workReportColumnType}" value="\${entity.workReportEntityResponse.workReportColumnType}" class="toggle_field" />
									{{/if}}
									\${entity.workReportEntityResponse.displayName}
									{{/if}}
									{{if entity.toolTip != null}}
									<a href="javascript:void(0);" aria-label="\${entity.toolTip}" class="tooltipped tooltipped-n"><i class="wm-icon-question-filled muted"></i></a>
									{{/if}}
									{{if entity.workReportEntityResponse.keyName == 'workCustomFields'}}
									<a href="javascript:void(0);" class="tooltipped tooltipped-n" aria-label="If no fields are specified, all fields with at least one value will be included in the report."><i class="wm-icon-question-filled muted"></i></a>
									{{/if}}
								</label>
								</span>
							{{if entity.workReportEntityResponse.keyName == 'workCustomFields'}}
							<select name="selectedWorkCustomFieldIds_multiselect" data-placeholder="Select Custom Fields..." class="custom-fields-multi-select span5" multiple="multiple" tabindex="-1">
								<option value disabled>Select Custom Fields...</option>
								<c:forEach var="group" items="${customReportCustomFieldGroups}">
									<optgroup style="font-weight: bold" value="group-${group.id}" label="${group.name}${group.deleted ? ' [Deleted]' : ''}">
										<c:forEach var="field" items="${group.customFields}">
											<option class="group-option" data-group-id="${group.id}" value=${field.id} ${field.reportingCriteriaFilter ? 'selected' : ''}>${field.name}${field.deleted ? ' [Deleted]' : ''}</option>
										</c:forEach>
									</optgroup>
								</c:forEach>
							</select>
							{{/if}}
                        {{if entity.workReportEntityResponse.filterable}}
                        {{if entity.htmlTagTypeThrift == ${htmlTagTypes['INPUT_TEXT']}}}
                        <input type="text" placeholder="\${entity.workReportEntityResponse.displayName}" name="${htmlTagTypes['INPUT_TEXT']}_\${entity.workReportEntityResponse.workReportColumnType}_\${entity.workReportEntityResponse.keyName}" class="span2" value="{{if inputValues}}\${inputValues.fieldValue}{{/if}}" />
                        {{/if}}
                        {{if entity.htmlTagTypeThrift == ${htmlTagTypes['SELECT_OPTION']}}}
                        <select class="span3" name="${htmlTagTypes['SELECT_OPTION']}_\${workReportEntityResponse.workReportColumnType}_\${workReportEntityResponse.keyName}_select">
							{{each selectOptionThrifts}}
							{{if inputValues}}
							<option value="\${value}" {{if value == inputValues.fieldValue}}selected="selected"{{/if}}>\${label}</option>
							{{else}}
							<option value="\${value}">\${label}</option>
							{{/if}}
							{{/each}}
						</select>
                        {{/if}}
                        {{if entity.htmlTagTypeThrift == ${htmlTagTypes['MULTI_SELECT_OPTION']}}}
                        <select class="span5" name="${htmlTagTypes['MULTI_SELECT_OPTION']}_\${entity.workReportEntityResponse.workReportColumnType}_\${workReportEntityResponse.keyName}_multiselect" multiple="multiple">
							{{each selectOptionThrifts}}
								{{if value === "pleaseSelect"}}
									<option value disabled>\${label}</option>
								{{else}}
									{{if inputValues}}
										<option value="\${value}" \${isSelect(value,inputValues.fieldValue)}>\${label}</option>
									{{else}}
										<option value="\${value}">\${label}</option>
									{{/if}}
								{{/if}}
							{{/each}}
						</select>
                        {{/if}}
                        {{if entity.htmlTagTypeThrift == ${htmlTagTypes['DATE']}}}
                        <input type="text" name="${htmlTagTypes['DATE']}_\${workReportEntityResponse.workReportColumnType}_\${workReportEntityResponse.keyName}" class="input-small date" placeholder="MM/DD/YYYY" value="{{if inputValues}}\${inputValues.fieldValue}{{/if}}" />
                        {{/if}}
                        {{if entity.htmlTagTypeThrift == ${htmlTagTypes['TO_FROM_DATES']}}}
                        <select class="span3 date-filter" name="${htmlTagTypes['TO_FROM_DATES']}_\${workReportEntityResponse.workReportColumnType}_select_from" {{if inputValues}} "" {{else}} disabled="disabled"{{/if}}>
							{{if inputValues}}
								{{if !entity.workReportEntityResponse.future}}
									{{tmpl({selected: inputValues.filteringTypeThrift}) "#work_report_past_filtering_type_thrift_select_en-tmpl"}}
								{{else}}
									{{tmpl({selected: inputValues.filteringTypeThrift}) "#work_report_filtering_type_thrift_select_en-tmpl"}}
								{{/if}}
							{{else}}
								{{if !entity.workReportEntityResponse.future}}
									{{tmpl({selected: ''}) "#work_report_past_filtering_type_thrift_select_en-tmpl"}}
								{{else}}
									{{tmpl({selected: ''}) "#work_report_filtering_type_thrift_select_en-tmpl"}}
								{{/if}}
							{{/if}}
						</select>
						{{if inputValues}}
						{{if inputValues.filteringTypeThrift === 4 || inputValues.filteringTypeThrift === "WORK_DATE_RANGE"}}
						<span class="date-section">
						{{else}}
						<span class="date-section" style="display: none;">
						{{/if}}
						{{else}}
						<span class="date-section" style="display: none;">
						{{/if}}
						<input type="text" readonly style="background-color: transparent" name="${htmlTagTypes['TO_FROM_DATES']}_\${workReportEntityResponse.workReportColumnType}_\${workReportEntityResponse.keyName}_from" class="input-small date" placeholder="MM/DD/YYYY" value="{{if inputValues}}\${inputValues.fromValue}{{/if}}" />
						<input type="text" readonly style="background-color: transparent" name="${htmlTagTypes['TO_FROM_DATES']}_\${workReportEntityResponse.workReportColumnType}_\${workReportEntityResponse.keyName}_to" class="input-small date" placeholder="MM/DD/YYYY" value="{{if inputValues}}\${inputValues.toValue}{{/if}}" />
						</span>
						{{/if}}
						{{if entity.htmlTagTypeThrift == ${htmlTagTypes['NUMERIC']}}}
						<select class="span3" name="${htmlTagTypes['NUMERIC']}_\${workReportEntityResponse.workReportColumnType}_select_filter">
							{{if inputValues}}
							{{tmpl({selected: inputValues.filteringTypeThrift}) "#work_report_relational_operator_select_en-tmpl"}}
							{{else}}
							{{tmpl({selected: ''}) "#work_report_relational_operator_select_en-tmpl"}}
							{{/if}}
						</select>
						<input type="text" name="${htmlTagTypes['NUMERIC']}_\${workReportEntityResponse.workReportColumnType}_\${workReportEntityResponse.keyName}_filter" class="input-small" />
						{{/if}}
						{{if entity.htmlTagTypeThrift == ${htmlTagTypes['NUMERIC_RANGE']}}}
						<select class="span3" name="${htmlTagTypes['NUMERIC_RANGE']}_\${workReportEntityResponse.workReportColumnType}_select_from">
							{{if inputValues}}
							{{tmpl({selected: inputValues.fromOperator}) "#work_report_relational_operator_select_en-tmpl"}}
							{{else}}
							{{tmpl({selected: ''}) "#work_report_relational_operator_select_en-tmpl"}}
							{{/if}}
						</select>
                        <input type="text" name="${htmlTagTypes['NUMERIC_RANGE']}_\${workReportEntityResponse.workReportColumnType}_\${workReportEntityResponse.keyName}_from" class="input-small" value="{{if inputValues}}\${inputValues.fromValue}{{/if}}" />
                        <select class="span3" name="${htmlTagTypes['NUMERIC_RANGE']}_\${workReportEntityResponse.workReportColumnType}_select_to">
							{{if inputValues}}
							{{tmpl({selected: inputValues.toOperator}) "#work_report_relational_operator_select_en-tmpl"}}
							{{else}}
							{{tmpl({selected: ''}) "#work_report_relational_operator_select_en-tmpl"}}
							{{/if}}
						</select>
                        <input type="text" name="${htmlTagTypes['NUMERIC_RANGE']}_\${workReportEntityResponse.workReportColumnType}_\${workReportEntityResponse.keyName}_to" class="input-small" value="{{if inputValues}}\${inputValues.toValue}{{/if}}" />
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
	<option value="${requestScope.relationalOperators['WORK_PLEASE_SELECT']}" {{if selected == '${requestScope.relationalOperators['WORK_PLEASE_SELECT']}'}}selected="selected"{{/if}}>Select to Filter</option>
	<option value="${requestScope.relationalOperators['WORK_EQUAL_TO']}" {{if selected == '${requestScope.relationalOperators['WORK_EQUAL_TO']}'}}selected="selected"{{/if}}>Equal To</option>
	<option value="${requestScope.relationalOperators['WORK_GREATER_THAN_EQUAL_TO']}" {{if selected == '${requestScope.relationalOperators['WORK_GREATER_THAN_EQUAL_TO']}'}}selected="selected"{{/if}}>Greater Than Equal To</option>
	<option value="${requestScope.relationalOperators['WORK_LESS_THAN']}" {{if selected == '${requestScope.relationalOperators['WORK_LESS_THAN']}'}}selected="selected"{{/if}}>Less Than</option>
</script>

<script id="work_report_filtering_type_thrift_select_en-tmpl" type="text/x-jquery-tmpl">
	<option value="${requestScope.filterTypes['WORK_PLEASE_SELECT']}" {{if selected == '${requestScope.filterTypes['WORK_PLEASE_SELECT']}'}}selected="selected"{{/if}}>Select to Filter</option>
	<option value="${requestScope.filterTypes['WORK_NEXT_1_DAY']}" {{if selected == '${requestScope.filterTypes['WORK_NEXT_1_DAY']}'}}selected="selected"{{/if}}>Next 24 Hours</option>
	<option value="${requestScope.filterTypes['WORK_NEXT_7_DAYS']}" {{if selected == '${requestScope.filterTypes['WORK_NEXT_7_DAYS']}'}}selected="selected"{{/if}}>Next 7 Days</option>
	<option value="${requestScope.filterTypes['WORK_NEXT_30_DAYS']}" {{if selected == '${requestScope.filterTypes['WORK_NEXT_30_DAYS']}'}}selected="selected"{{/if}}>Next 30 Days</option>
	<option value="${requestScope.filterTypes['WORK_NEXT_60_DAYS']}" {{if selected == '${requestScope.filterTypes['WORK_NEXT_60_DAYS']}'}}selected="selected"{{/if}}>Next 60 Days</option>
	<option value="${requestScope.filterTypes['WORK_NEXT_90_DAYS']}" {{if selected == '${requestScope.filterTypes['WORK_NEXT_90_DAYS']}'}}selected="selected"{{/if}}>Next 90 Days</option>
	<option value="${requestScope.filterTypes['WORK_LAST_1_DAY']}" {{if selected == '${requestScope.filterTypes['WORK_LAST_1_DAY']}'}}selected="selected"{{/if}}>Last 24 Hours</option>
	<option value="${requestScope.filterTypes['WORK_LAST_7_DAYS']}" {{if selected == '${requestScope.filterTypes['WORK_LAST_7_DAYS']}'}}selected="selected"{{/if}}>Last 7 Days</option>
	<option value="${requestScope.filterTypes['WORK_LAST_30_DAYS']}" {{if selected == '${requestScope.filterTypes['WORK_LAST_30_DAYS']}'}}selected="selected"{{/if}}>Last 30 Days</option>
	<option value="${requestScope.filterTypes['WORK_LAST_60_DAYS']}" {{if selected == '${requestScope.filterTypes['WORK_LAST_60_DAYS']}'}}selected="selected"{{/if}}>Last 60 Days</option>
	<option value="${requestScope.filterTypes['WORK_LAST_90_DAYS']}" {{if selected == '${requestScope.filterTypes['WORK_LAST_90_DAYS']}'}}selected="selected"{{/if}}>Last 90 Days</option>
	<option value="${requestScope.filterTypes['WORK_LAST_365_DAYS']}" {{if selected == '${requestScope.filterTypes['WORK_LAST_365_DAYS']}'}}selected="selected"{{/if}}>Last 365 Days</option>
	<option value="${requestScope.filterTypes['WORK_THIS_YEAR_TO_DATE']}" {{if selected == '${requestScope.filterTypes['WORK_THIS_YEAR_TO_DATE']}'}}selected="selected"{{/if}}>Year to Date</option>
	<option value="${requestScope.filterTypes['WORK_DATE_RANGE']}" {{if selected == '${requestScope.filterTypes['WORK_DATE_RANGE']}'}}selected="selected"{{/if}}>Date Range</option>
</script>

<script id="work_report_past_filtering_type_thrift_select_en-tmpl" type="text/x-jquery-tmpl">
	<option value="${requestScope.filterTypes['WORK_PLEASE_SELECT']}" {{if selected == '${requestScope.filterTypes['WORK_PLEASE_SELECT']}'}}selected="selected"{{/if}}>Select to Filter</option>
	<option value="${requestScope.filterTypes['WORK_LAST_1_DAY']}" {{if selected == '${requestScope.filterTypes['WORK_LAST_1_DAY']}'}}selected="selected"{{/if}}>Last 24 Hours</option>
	<option value="${requestScope.filterTypes['WORK_LAST_7_DAYS']}" {{if selected == '${requestScope.filterTypes['WORK_LAST_7_DAYS']}'}}selected="selected"{{/if}}>Last 7 Days</option>
	<option value="${requestScope.filterTypes['WORK_LAST_30_DAYS']}" {{if selected == '${requestScope.filterTypes['WORK_LAST_30_DAYS']}'}}selected="selected"{{/if}}>Last 30 Days</option>
	<option value="${requestScope.filterTypes['WORK_LAST_60_DAYS']}" {{if selected == '${requestScope.filterTypes['WORK_LAST_60_DAYS']}'}}selected="selected"{{/if}}>Last 60 Days</option>
	<option value="${requestScope.filterTypes['WORK_LAST_90_DAYS']}" {{if selected == '${requestScope.filterTypes['WORK_LAST_90_DAYS']}'}}selected="selected"{{/if}}>Last 90 Days</option>
	<option value="${requestScope.filterTypes['WORK_LAST_365_DAYS']}" {{if selected == '${requestScope.filterTypes['WORK_LAST_365_DAYS']}'}}selected="selected"{{/if}}>Last 365 Days</option>
	<option value="${requestScope.filterTypes['WORK_THIS_YEAR_TO_DATE']}" {{if selected == '${requestScope.filterTypes['WORK_THIS_YEAR_TO_DATE']}'}}selected="selected"{{/if}}>Year to Date</option>
	<option value="${requestScope.filterTypes['WORK_DATE_RANGE']}" {{if selected == '${requestScope.filterTypes['WORK_DATE_RANGE']}'}}selected="selected"{{/if}}>Date Range</option>
</script>

<script id="work_report_display-tmpl" type="text/x-jquery-tmpl">
	<div id="report_display_header_wrap"></div>
	<table id="report_display" style="font-size:11px;">
		<thead>
		<tr>
			{{each(i, item) headers}}
			<th>\${item}</th>
			{{/each}}
		</tr>
		</thead>
		<tbody>
		{{each(i, row) rows}}
		<tr>
			{{each(j, cell) row.reportFields}}
			<td>{{html cell}}</td>
			{{/each}}
		</tr>
		{{/each}}
		</tbody>
	</table>
</script>

</wm:app>
