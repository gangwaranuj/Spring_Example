<div class="well-b2">
	<div class="scorecard well-content">
		{{if resource_scorecard && resource_scorecard_for_company}}
		<div class="btn-group">
			<button type="button" value="all" class="button active rating_toggle">All Ratings</button>
			<button type="button" value="company" class="button rating_toggle">Your Ratings</button>
		</div>
		{{/if}}

		{{if resource_scorecard.values}}
		<table ref="all">
			<thead>
			<tr>
				<th class="mini-rating">
					{{if resource_scorecard.rating.count > 0}}
					{{else}}
					<small class="pull-left">No ratings yet</small>
					{{/if}}
				</th>
				<th><span class="tooltipped tooltipped-n" aria-label="3 Months">3 Mo.</span></th>
				<th>All</th>
			</tr>
			</thead>
			<tbody>
			<tr>
				<th>Overall Satisfaction %</th>
				{{if resource_scorecard.values['SATISFACTION_OVER_ALL'].net90 > 0}}
					<td>\${(resource_scorecard.values['SATISFACTION_OVER_ALL'].net90 * 100).toFixed(1)}</td>
				{{else}}
					<td>-</td>
				{{/if}}
				{{if resource_scorecard.values['SATISFACTION_OVER_ALL'].all > 0}}
					<td>\${(resource_scorecard.values['SATISFACTION_OVER_ALL'].all * 100).toFixed(1)}</td>
				{{else}}
					<td>-</td>
				{{/if}}
			</tr>
			<tr>
				<th>Assignments</th>
				<td>\${Math.round(resource_scorecard.values['COMPLETED_WORK'].net90).toFixed(0)}</td>
				<td>\${Math.round(resource_scorecard.values['COMPLETED_WORK'].all).toFixed(0)}</td>
			</tr>
			<tr>
				<th>On-Time %</th>
				{{if resource_scorecard.values['ON_TIME_PERCENTAGE'].net90 > 0}}
					<td>\${(resource_scorecard.values['ON_TIME_PERCENTAGE'].net90 * 100).toFixed(1)}</td>
				{{else}}
					<td>-</td>
				{{/if}}
				{{if resource_scorecard.values['ON_TIME_PERCENTAGE'].all > 0}}
					<td>\${(resource_scorecard.values['ON_TIME_PERCENTAGE'].all * 100).toFixed(1)}</td>
				{{else}}
					<td>-</td>
				{{/if}}
			</tr>
			{{if resource_scorecard.values['DELIVERABLE_ON_TIME_PERCENTAGE']}}
			<tr>
				<th>Deliverable On-Time %</th>
				{{if resource_scorecard.values['DELIVERABLE_ON_TIME_PERCENTAGE'].net90 > 0}}
				<td>\${(resource_scorecard.values['DELIVERABLE_ON_TIME_PERCENTAGE'].net90 * 100).toFixed(1)}</td>
				{{else}}
				<td>-</td>
				{{/if}}
				{{if resource_scorecard.values['DELIVERABLE_ON_TIME_PERCENTAGE'].all > 0}}
				<td>\${(resource_scorecard.values['DELIVERABLE_ON_TIME_PERCENTAGE'].all * 100).toFixed(1)}</td>
				{{else}}
				<td>-</td>
				{{/if}}
			</tr>
			{{/if}}
			</tbody>
		</table>

		{{if resource_scorecard_for_company.values}}
		<table ref="company" class="dn">
			<thead>
			<tr>
				<th class="mini-rating">
					{{if resource_scorecard_for_company.rating.count > 0}}
					{{else}}
					<small class="pull-left">No ratings yet</small>
					{{/if}}
				</th>
				<th><span class="tooltipped tooltipped-n" aria-label="3 Months">3 Mo.</span></th>
				<th>All</th>
			</tr>
			</thead>
			<tbody>
			<tr>
				<th>Overall Satisfaction %</th>
				{{if resource_scorecard_for_company.values['SATISFACTION_OVER_ALL'].net90 > 0}}
					<td>\${(resource_scorecard_for_company.values['SATISFACTION_OVER_ALL'].net90 * 100).toFixed(1)}</td>
				{{else}}
					<td>-</td>
				{{/if}}
				{{if resource_scorecard_for_company.values['SATISFACTION_OVER_ALL'].all > 0}}
					<td>\${(resource_scorecard_for_company.values['SATISFACTION_OVER_ALL'].all * 100).toFixed(1)}</td>
				{{else}}
					<td>-</td>
				{{/if}}
			</tr>
			<tr>
				<th>Assignments</th>
				<td>\${Math.round(resource_scorecard_for_company.values['COMPLETED_WORK'].net90).toFixed(0)}</td>
				<td>\${Math.round(resource_scorecard_for_company.values['COMPLETED_WORK'].all).toFixed(0)}</td>
			</tr>
			<tr>
				<th>On-Time %</th>
				{{if resource_scorecard_for_company.values['ON_TIME_PERCENTAGE'].net90 > 0}}
					<td>\${(resource_scorecard_for_company.values['ON_TIME_PERCENTAGE'].net90 * 100).toFixed(1)}</td>
				{{else}}
					<td>-</td>
				{{/if}}
				{{if resource_scorecard_for_company.values['ON_TIME_PERCENTAGE'].all > 0}}
					<td>\${(resource_scorecard_for_company.values['ON_TIME_PERCENTAGE'].all * 100).toFixed(1)}</td>
				{{else}}
					<td>-</td>
				{{/if}}
			</tr>
			{{if resource_scorecard_for_company.values['DELIVERABLE_ON_TIME_PERCENTAGE']}}
			<tr>
				<th>Deliverable On-Time %</th>
				{{if resource_scorecard_for_company.values['DELIVERABLE_ON_TIME_PERCENTAGE'].net90 > 0}}
				<td>\${(resource_scorecard_for_company.values['DELIVERABLE_ON_TIME_PERCENTAGE'].net90 * 100).toFixed(1)}</td>
				{{else}}
				<td>-</td>
				{{/if}}
				{{if resource_scorecard_for_company.values['DELIVERABLE_ON_TIME_PERCENTAGE'].all > 0}}
				<td>\${(resource_scorecard_for_company.values['DELIVERABLE_ON_TIME_PERCENTAGE'].all * 100).toFixed(1)}</td>
				{{else}}
				<td>-</td>
				{{/if}}
			</tr>
			{{/if}}
			</tbody>
		</table>
		{{/if}}
		{{/if}}
	</div>
</div>