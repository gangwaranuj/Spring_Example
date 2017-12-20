<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ page isELIgnored="true" %>

<div id="assignment_buyer_statuses_container">
	<h5>Statuses</h5>
	<ul id="assignment_buyer_statuses" class="stacked-nav"></ul>
</div>

<div id="assignment_buyersubstatuses_container">
	<h5>Labels</h5>
	<ul id="assignment_buyersubstatuses" class="stacked-nav assignment_labels"></ul>
	<ul id="assignment_buyersubstatuses_inactive" class="stacked-nav assignment_labels"></ul>
	<ul id="assignment_buyersubstatuses_more" class="dn stacked-nav assignment_labels">
		<li class="toggle_more">
			<a href="javascript:void(0);" class="cta-toggle-more">Show Unused</a>
		</li>
	</ul>
</div>

<c:if test="${currentUser.buyer}">
	<a class="small" href="/mmw/manage/labels?ref=dash">Manage Labels</a>
</c:if>

<script id="tmpl-statuses_list" type="text/x-jquery-tmpl">
	<div>
		{{if count > 0}}
		<li id="status_${id}_${type}" class="{{if substatus}}assignment_labels{{/if}}">
			<a title="${description}"
				href="#{{if substatus}}substatus/${id}/status/${parent}{{else}}status/${id}{{/if}}/${type}"
				class="{{if substatus}}dragAdd{{/if}}">
				<span class="overflow">
					${description}
				{{if submenu}}
					<span class="toggler"></span>
				{{/if}}
				</span>
				<span class="label {{if id == 'exception'}}important{{/if}} fr {{if id == 'active'}} assigned {{else}} ${id} {{/if}}" {{if color_rgb}}style="background-color: #${color_rgb};"{{/if}}>
				${count}
				</span>
			</a>
		</li>
		{{else}}
		<li>
			<a>
				<span class="overflow">${description}</span>
			</a>
		</li>
		{{/if}}
	</div>
</script>

<script id="tmpl-substatuses_list" type="text/x-jquery-tmpl">
	<div>
		<li id="substatus_${id}_${type}">
			<a title="${description}" class="ellipsis dragAdd" href="#substatus/${id}/${type}">
				<span class="overflow">${description}</span>
				<span class="label fr" {{if color_rgb}}style="background-color:
				#${color_rgb};"{{/if}}>${count}</span>
			</a>
		</li>
	</div>
</script>

