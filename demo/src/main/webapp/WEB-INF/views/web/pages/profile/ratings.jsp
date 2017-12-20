<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="wmfmt" uri="http://www.workmarket.com/taglib/fmt" %>

<div>
	<table id="ratings-datatable" class="user-ratings--table">
		<thead>
			<tr>
				<th></th>
				<th class="text-center" width="250px"></th>
			</tr>
		</thead>
		<tbody></tbody>
	</table>
	<p>No ratings information available.</p>
</div>

<script id="cell-title-tmpl" type="text/x-jquery-tmpl">
	<div>
		<p>
			{{if meta.workNumber}}
			<a href="/assignments/details/\${meta.workNumber}">\${meta.workTitle}</a><small> (\${meta.workSchedule}) </small><br/>
			{{else}}
			<strong>\${meta.workTitle}</strong>
			{{/if}}
			{{each(key, value) meta.resourceLabels}}
			<span class="label important tooltipped tooltipped-n" aria-label="\${value}">\${key}</span>
			{{/each}}

				<c:if test="${isOwner}">
					{{if meta.companyName}}
						\${meta.companyName}<br/>
					{{/if}}
				</c:if>
			<small></small>
		</p>

		{{if meta.ratingReview}}
			<blockquote>
				<span>\${meta.ratingReview}</span>
			</blockquote>
		{{/if}}
	</div>
</script>

<script id="cell-feedback-tmpl" type="text/x-jquery-tmpl">
	<div>
		<div class="span text-right">Overall </div> <div class="span text-right pull-right \${meta.ratingCode}"> \${meta.ratingValue} </div> </br>
		{{if meta.qualityValue != 'Not applicable'}}
		<div class="span text-right">Quality </div> <div class="span text-right pull-right \${meta.qualityCode}">\${meta.qualityValue} </div> </br>
		<div class="span text-right">Professionalism </div> <div class="span text-right pull-right \${meta.professionalismCode}"> \${meta.professionalismValue} </div> </br>
		<div class="span text-right">Communication </div> <div class="span text-right pull-right \${meta.communicationCode}"> \${meta.communicationValue} </div>
		{{/if}}
	</div>
</script>
