<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="wmfn" uri="http://www.workmarket.com/taglib/functions" %>
<%@ taglib prefix="wm" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="wmfmt" uri="http://www.workmarket.com/taglib/fmt" %>

<wm:app pagetitle="Manage Tests" bodyclass="lms" webpackScript="lms">

	<script>
		var config = {
			mode: 'boxesManage'
		};
	</script>

	<sec:authorize access="hasRole('PERMISSION_ASSESSMENTS')" var="hasAssessmentPermission" />

	<script type="text/x-jquery-tmpl" id="n_items">
		<span class="nitems">\${n}</span>
	</script>

	<script type="text/x-jquery-tmpl" id="box_template">
		<div class="span4">
			<li>
				<div class="box-wrapper">
					<div class="box-upper">
						<h3>
							<strong><a href="/lms/view/details/\${assessmentId}">\${assessmentName}</a></strong>
						</h3>

						{{if companyLogo != null}}
							<div class="tac" style="height:90px; padding-top:10px">
								<img src="\${(companyLogo)}" alt="Photo" style="max-height:60px; max-width:140px" />
							</div>
						{{/if}}
					</div>
					<div class="box-details">
						<div class="tac">
							{{if status == 'inactive'}}
								<a class="status-label reqs">Inactive</a>
							{{else status == 'draft'}}
								<a class="status-label reqs">Draft</a>
							{{/if}}

							{{if gradePendingCount > 0}}
							<a class="status-label reqs" href="/lms/view/details/\${assessmentId}">
								Grading Required: \${gradePendingCount}
							</a>
							{{/if}}

							{{if passedCount > 0}}
								<div style="margin-top:10px">
									<div class="wm-icon-user"></div>
									\${passedCount} Workers Passed
								</div>
							{{/if}}
						</div>
					</div>
					{{if approximateMinutesDuration > 0 || passingScore > 0}}
						<div class="box-stats">
							<ul class="unstyled">
								<li class="test-duration">
									<i class="wm-icon-clock"/>
									{{if approximateMinutesDuration > 0}}
									<span class="">\${approximateMinutesDuration}</span>
									<span class="">Mins</span>
									{{else}}
									Untimed
									{{/if}}
								</li>
								{{if passingScore > 0}}
								<li class="test-score">
									<span>\${passingScore}%</span>
									<span>to pass</span>
								</li>
								{{/if}}
							</ul>
						</div>
					{{/if}}
				</div>
			</li>
		</div>
	</script>

	<div id="manage-tests" class="page-header">
		<strong><a href="/lms/manage/step1" class="button pull-right">New Test</a></strong>
		<h3 class="pull-left">Tests
			<small class="meta">
				<span><a href="<c:url value="/lms/manage/list_view?ref=listview"/>">List View</a></span> |
				<span class="selected-view">Grid View</span>
			</small>
		</h3>
		<div id="filter_buttons" class="pull-right">
			<ul class="nav" style="display: inline-flex;">
				<li><strong><a class="button option active-option-manage-tests" id="activeFilter">Active</a></strong></li>
				<li><strong><a class="button option" id="testIOwnFilter">Tests I Own</a></strong></li>
				<li><strong><a class="button option" id="inactiveFilter">Inactive</a></strong></li>
				<li><strong><a class="button option" id="allFilter">All Tests</a></strong></li>
			</ul>
		</div>
	</div>


	<div id="message_no_tests" class="hero-unit well-b2 dn">
		<h2>Tests</h2>
		<p>are useful in a few different ways. Primarily tests are used as a screening tool and as an educational tool.
			As a screening tool, you can build a test  that can be placed as a  requirement to join a Talent Pool or you can use Requirement Sets and place a test as a prerequisite to accept an assignment.
			We believe that confirming skills and knowledge is an important process that drives high quality work engagements.  As an educational tool tests can be very effective as well.  As our world evolves new skills are required to successfully complete work.  You can embed videos and documents into a test and then ask questions related to its content.
			The power to educate your curated Talent Pools of talent is a very powerful way to drive quality and engagement into your workflows.
			<br/>
			<br/>
			Tests can be set as private or public.  Public tests will be viewable by registered Work Market users.  You can Invite people to private tests.

			<a style="text-decoration: none;" href="https://workmarket.zendesk.com/hc/en-us/articles/209336528" target="_blank"> Learn more <i class="icon-info-sign"></i></a>
		</p>
		<p>
			<a class="button" href="/lms/manage/step1">Create a Test</a>
		</p>
	</div>

	<div>
		<ul class="items" style="list-style:none;" id="items"></ul>
	</div>

</wm:app>
