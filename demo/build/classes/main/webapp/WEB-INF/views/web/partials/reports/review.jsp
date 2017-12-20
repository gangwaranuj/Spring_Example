<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<%-- This form is used for rating page --%>
<script id="details-cell-tmpl" type="text/x-jquery-tmpl">
	<div>
		<a href="/assignments/details/\${meta.work_number}">\${meta.title}</a>
		<small class="meta">(ID: \${meta.work_number})</small>
		<br/>
		{{if meta.address}}\${meta.address}{{/if}}
		<br/>
		Scheduled Date: \${meta.schedule_date}
		<br/>
	    Price: \${meta.price}
		{{if meta.paid_date}}<small class="meta">(Paid Date: \${meta.paid_date})</small>
		{{else}}<small class="meta">(Due Date: \${meta.due_date})</small>{{/if}}
		<br/>
		\${meta.company} &bull; \${meta.buyer}
	</div>
</script>

<script id="rating-form-cell-tmpl" type="text/x-jquery-tmpl">
	<div>
		<div id="assignment_info" class="dn">
			<input type="hidden" name="ratings[\${meta.index}].workId" value='\${meta.work_id}'/>
			<input type="hidden" name="ratings[\${meta.index}].ratedUserId" value='\${meta.buyer_id}'/>
			<input type="hidden" name="ratings[\${meta.index}].raterUserId" value='\${meta.resource_id}'/>
		</div>
		<div>
			<div class="row-fluid">
				<div class="span5">
					<strong>Overall</strong>
				</div>
				<div class="span10">
					<div class="span6"><input type="radio" class="three-level rating-value" name="ratings[\${meta.index}].value"  value="1" {{if meta.value == 1}}checked="checked"{{/if}} /> Unsatisfied  </div>
					<div class="span6"><input type="radio" class="three-level rating-value" name="ratings[\${meta.index}].value"  value="2" {{if meta.value == 2}}checked="checked"{{/if}} /> Satisfied </div>
					<input type="radio" class="three-level rating-value" name="ratings[\${meta.index}].value"  value="3" {{if meta.value == 3}}checked="checked"{{/if}} /> Excellent
				</div>
			</div>
			<div class="row-fluid">
				<div class="span5">
					<strong>Quality</strong>
				</div>
				<div class="span10">
					<div class="span6"><input type="radio" class="three-level rating-quality" name="ratings[\${meta.index}].quality"  value="1" {{if meta.quality == 1}}checked="checked"{{/if}} /> Unsatisfied </div>
					<div class="span6"><input type="radio" class="three-level rating-quality" name="ratings[\${meta.index}].quality"  value="2" {{if meta.quality == 2}}checked="checked"{{/if}} /> Satisfied </div>
					<input type="radio" class="three-level rating-quality" name="ratings[\${meta.index}].quality"  value="3" {{if meta.quality == 3}}checked="checked"{{/if}} /> Excellent
				</div>
			</div>
			<div class="row-fluid">
				<div class="span5">
					<strong>Professionalism</strong>
				</div>
				<div class="span10">
					<div class="span6"><input type="radio" class="three-level rating-professionalism" name="ratings[\${meta.index}].professionalism"  value="1" {{if meta.professionalism == 1}}checked="checked"{{/if}} /> Unsatisfied </div>
					<div class="span6"><input type="radio" class="three-level rating-professionalism" name="ratings[\${meta.index}].professionalism"  value="2" {{if meta.professionalism == 2}}checked="checked"{{/if}} /> Satisfied </div>
					<input type="radio" class="three-level rating-professionalism" name="ratings[\${meta.index}].professionalism"  value="3" {{if meta.professionalism == 3}}checked="checked"{{/if}} /> Excellent
				</div>
			</div>
			<div class="row-fluid">
				<div class="span5">
					<strong>Communication</strong>
				</div>
				<div class="span10">
					<div class="span6"><input type="radio" class="three-level rating-communication" name="ratings[\${meta.index}].communication"  value="1"  {{if meta.communication == 1}}checked="checked"{{/if}} /> Unsatisfied </div>
					<div class="span6"><input type="radio" class="three-level rating-communication" name="ratings[\${meta.index}].communication"  value="2" {{if meta.communication == 2}}checked="checked"{{/if}} /> Satisfied </div>
					<input type="radio" class="three-level rating-communication" name="ratings[\${meta.index}].communication"  value="3" {{if meta.communication == 3}}checked="checked"{{/if}} /> Excellent
				</div>
			</div>
			<div class="rating-text pending ml pull-left"></div>
		</div>
		<div style="clear:both"></div>
		<div class="rating-extras {{if meta.review}}{{else}}dn{{/if}} ">
			<span class="help-block">Optional&#58; Leave a Review</span>
			<div class="rating-note">
				<textarea name="ratings[\${meta.index}].review" style="width:90%" rows="5">\${meta.review}</textarea>
			</div>
			<div style="clear:both"></div>
			<div class="rating-actions pull-right">
				<span class="button -small primary" data-behavior="finish-review">Submit</span>
				{{if meta.value > 0 }}
				{{else}}
					<a class="button -small cancel" data-behavior="cancel-review">Cancel</a>
				{{/if}}
			</div>
		</div>
		<div style="clear:both"></div>
	</div>
</script>

<script id="rating-cell-tmpl" type="text/x-jquery-tmpl">
	<div>
		<div>
			<p>\${meta.rating}
			</p>
		</div>
		<small class="meta">\${meta.feedback_date}</small>
		<br/>
		{{if meta.review && meta.review.length != meta.review_short.length}}
		<p title="\${meta.review}" class="tooltip-info db">
			{{if meta.show_review }}
				\${meta.review_short}
			{{/if}}
		</p>
		{{else}}
			{{if meta.show_review }}
				\${meta.review_short}
			{{/if}}
		{{/if}}
		{{if meta.show_flag }}
			<div>
				{{if meta.is_flagged_for_review}}
					Flagged
				{{else meta.rater_id != '${currentUser.id}'}}
					<a class="flag-rating" data-id="\${meta.id}" title="flag rating">
						<small>Flag Rating</small>
					</a>
				{{/if}}
			</div>
		{{/if}}
	</div>
</script>
