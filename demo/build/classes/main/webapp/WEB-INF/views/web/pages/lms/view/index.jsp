<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="wmfn" uri="http://www.workmarket.com/taglib/functions" %>
<%@ taglib prefix="wm" tagdir="/WEB-INF/tags" %>

<wm:app pagetitle="Learning Center" bodyclass="lms" breadcrumbSection="Profile" breadcrumbSectionURI="/profile" breadcrumbPage="Tests" webpackScript="lms">

	<script>
		var config = {
			mode: 'boxesView'
		};
	</script>

	<div id="mytest_div" class="content-box-ui">
			<div class="boxUI top">
				<div class="page-header clear" style="margin-bottom:30px">
					<h2 class="h1-view pull-left">My Tests</h2>
					<div id="filter_buttons" class="pull-right">
						<button class="button option active-option-manage-tests" id="invitedFilter">Invited</button>
						<button class="button option" id="inProgressFilter">In Progress</button>
						<button class="button option" id="passedFilter">Passed</button>
						<button class="button option" id="gradePendingFilter">Grade Pending</button>
						<button class="button option" id="failedFilter">Failed</button>
					</div>
				</div>

				<div id="message_invited" class="mytest-message dn"> You do not have any outstanding invitations</div>
				<div id="message_inProgress" class="mytest-message dn"> You do not have any tests in progress </div>
				<div id="message_passed" class="mytest-message dn"> You have not passed any tests </div>
				<div id="message_gradePending" class="mytest-message dn"> You do not have any tests that are pending a grade </div>
				<div id="message_failed" class="mytest-message dn"> You do not have any failed tests </div>

				<ul class="mytest items items-view-tests" id="mytest_invited"></ul>
				<ul class="mytest items items-view-tests dn" id="mytest_inProgress"></ul>
				<ul class="mytest items items-view-tests dn" id="mytest_passed"></ul>
				<ul class="mytest items items-view-tests dn" id="mytest_gradePending"></ul>
				<ul class="mytest items items-view-tests dn" id="mytest_failed">'</ul>
			</div>
		</div>

	<div id="boxui-tests-view">
		<div id="recommended_div" class="content-box-ui">
			<div class="boxUI">
				<div class="page-header" style="margin-bottom:30px">
					<h2 class="h1-view">Recommended Tests</h2>
				</div>
				<ul class="items items-view-tests" id="recommended"></ul>
			</div>
		</div>

		<div id="browse_div" class="content-box-ui">
			<div class="boxUI">
				<div class="page-header" style="margin-bottom:30px">
					<h2 class="h1-view">Browse Tests</h2>
				</div>
				<ul class="items items-view-tests" id="browse"></ul>
			</div>
		</div>

	</div>

	<script type="text/x-jquery-tmpl" id="n_items">
		<span class="nitems">\${n}</span>
	</script>

	<script type="text/x-jquery-tmpl" id="box_template">
		<div class="span4">
			<li class="">
				<div class="box-wrapper">
					<div class="box-upper">
							<h3>
								<strong><a href="/lms/view/details/\${assessmentId}">\${assessmentName}</a></strong>
							</h3>
						<p class="company-name">\${companyName}</p>

						{{if companyLogo != null}}
							<div class="tac" style="height:90px; padding-top:10px">
								<img src="\${(companyLogo)}" alt="Photo" style="max-height:60px; max-width:140px" />
							</div>
						{{/if}}
					</div>
					<div class="box-details">
						<div class="tac">
							{{if attemptStatusTypeCode == 'graded'}}
							{{if passed == true}}
								<a class="status-label reqs" style="pointer-events: none;"><img src="${mediaPrefix}/images/icons/checkmark.png" alt=""/> Test Passed</a>
							{{/if}}
						{{else attemptStatusTypeCode == 'inprogress'}}
							<a class="status-label reqs" href="/lms/view/take/\${assessmentId}">Continue Test: \${Math.round(progress*100)}%</a>
						{{else attemptStatusTypeCode == 'gradePending'}}
							<a class="status-label reqs" style="pointer-events: none;">Score Pending</a>
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

</wm:app>
