<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="wm" tagdir="/WEB-INF/tags" %>

<wm:app pagetitle="Order Background Check" bodyclass="screening" webpackScript="settings">
	<%@ page isELIgnored="false" %>

	<script>
		var config = {
			mode: 'screening',
			usaPrice: '${screeningPrices['bkgrdchk']}',
			intlPrice:  '${screeningPrices['bkgrdchkIN']}'
		};
	</script>

	<c:import url="/WEB-INF/views/web/partials/general/notices.jsp">
		<c:param name="bundle" value="${bundle}"/>
	</c:import>

	<c:if test="${bkgrndCheckFailed}">
		<div id="status_failed">
			<div class="content">
				<div class="inner-container">
					<div class="page-header clear">
						<h3>Background Check</h3>
					</div>

					<p><img src="${mediaPrefix}/images/failed_screen.png"/> Your background check indicated one or more infractions.</p>

					<p>At your request, SterlingBackcheck will send you a copy of your report via email.
						Work Market will not see the report or the specific reason for an infraction on your background check.</p>

					<p>If you feel the information on your report is incomplete or inaccurate, please contact the Sterling Compliance Department at 1-800-853-3228 or email
						<a href="mailto:askcompliancemail@sterlinginfosystems.com">askcompliancemail@sterlinginfosystems.com</a>.
					</p>

					<p><strong>If you are unable to resolve this issue with SterlingBackcheck, you can contact Work Market at 1-212-229-WORK (9675) for further assistance.</strong></p>
					<br/>
					<c:if test="${not empty previousBackgroundList}">
					<p>
						<a href="#" class="view_previous">View previous Background Check(s) status(es)</a>
						<div class="dn list_previous">
							<c:forEach var="bg" items="${previousBackgroundList}">
							<c:if test="${bg.screeningStatusType eq 'expired' || bg.screeningStatusType eq 'passed'}">
					<p>
						<img src="${mediaPrefix}/images/passed_icon.png"/>
						Your Background check was "CLEAR" on <fmt:formatDate value="${bg.responseDate.time}" pattern="MM/dd/yyyy"/>
					</p>
					</c:if>
					<c:if test="${bg.screeningStatusType eq 'failed'}">
						<p>
							<img src="${mediaPrefix}/images/failed_screen.png"/>
							You FAILED a Background on <fmt:formatDate value="${bg.responseDate.time}" pattern="MM/dd/yyyy"/>
						</p>
					</c:if>
					</c:forEach>
				</div>
				</p>
				</c:if>

				<div class="wm-action-container">
					<a href="/profile" class="button">Back to profile</a>
					<button class="button renewal">Retake Now</button>
				</div>
			</div>
		</div>
		</div>
	</c:if>

	<c:if test="${bkgrndCheckPassed}">
		<div id="status_passed">
			<div class="content">
				<div class="inner-container">
					<div class="page-header clear">
						<h3>Background Check</h3>
					</div>

					<p><img src="${mediaPrefix}/images/passed_icon.png"/> Your background check was "CLEAR" and your profile now shows an icon indicating that you conducted a background check and there were no infractions.</p>

					<c:if test="${bkgrndCheckOld}">
						<div class="alert">
							Your background check is more than a year old.
							Many companies on Work Market prefer workers to get their background checked once a year. We recommend you to renew your background check in order to keep your status current.
						</div>
					</c:if>

					<c:if test="${not empty previousBackgroundList}">
					<p>
						<a href="#" class="view_previous">View previous Background Check(s) status(es)</a>
						<div class="dn list_previous">
							<c:forEach var="bg" items="${previousBackgroundList}">
							<c:if test="${bg.screeningStatusType eq 'expired' || bg.screeningStatusType eq 'passed'}">
					<p>
						<img src="${mediaPrefix}/images/passed_icon.png"/>
						Your Background check was "CLEAR" on <fmt:formatDate value="${bg.responseDate.time}" pattern="MM/dd/yyyy"/>
					</p>
					</c:if>
					<c:if test="${bg.screeningStatusType eq 'failed'}">
						<p>
							<img src="${mediaPrefix}/images/failed_screen.png"/>
							You FAILED a Background on <fmt:formatDate value="${bg.responseDate.time}" pattern="MM/dd/yyyy"/>
						</p>
					</c:if>
					</c:forEach>
				</div>
				</p>
				</c:if>
				<div class="wm-action-container">
					<a href="/profile" class="button">Back to profile</a>
					<button class="button renewal">Renew Now</button>
				</div>
			</div>
		</div>
		</div>
	</c:if>

	<c:if test="${bkgrndCheckPending}">
		<div id="status_pending">
			<div class="content">
				<div class="inner-container">
					<div class="page-header clear">
						<h3>Background Check</h3>
					</div>

					<div class="alert"><i class="wm-icon-plus"></i><span>PENDING</span><br>
						Your background check is still pending. Background checks typically take five to
						ten business days to return results. If you have any questions regarding your background check, please contact the
						Sterling Compliance Department at 1-800-853-3228 or email <a href="mailto:askcompliancemail@sterlinginfosystems.com">
							askcompliancemail@sterlinginfosystems.com</a>.
					</div>
					<c:forEach var="bg" items="${previousBackgroundList}">
						<c:if test="${bg.screeningStatusType eq 'passed'}">
							<p>
								<img src="${passedLabel}"/>
								Your Background check was "CLEAR" on <fmt:formatDate value="${bg.responseDate.time}" pattern="MM/dd/yyyy"/>  and your profile now
								shows an icon indicating that you conducted a background check and there were no infractions.
							</p>
						</c:if>
						<c:if test="${bg.screeningStatusType eq 'failed'}">
							<p>
								<img src="${mediaPrefix}/images/failed_screen.png"/>
								You previously FAILED a Background on <fmt:formatDate value="${bg.responseDate.time}" pattern="MM/dd/yyyy"/>
							</p>
						</c:if>
					</c:forEach>
					<div class="wm-action-container">
						<a href="/profile" class="muted">Back to profile</a>
						<button class="button" disabled>Renew Now</button>
					</div>
				</div>
			</div>
		</div>
	</c:if>

	<c:if test="${bkgrndCheckCancelled}">
		<div id="status_cancelled">
			<div class="content">
				<div class="inner-container">
					<div class="page-header clear">
						<h3>Background Check</h3>
					</div>

					<div class="alert"><i class="wm-icon-x"></i><span>CANCELLED</span><br>
						Your background check was cancelled. If you have any questions
						regarding your background check, please contact the Sterling Compliance Department at 1-800-853-3228 or email <a href="mailto:askcompliancemail@sterlinginfosystems.com">
							askcompliancemail@sterlinginfosystems.com</a>.
					</div>
					<c:forEach var="bg" items="${previousBackgroundList}">
						<c:if test="${bg.screeningStatusType eq 'passed'}">
							<p>
								<img src="${passedLabel}"/>
								Your Background check was "CLEAR" on <fmt:formatDate value="${bg.responseDate.time}" pattern="MM/dd/yyyy"/>  and your profile now
								shows an icon indicating that you conducted a background check and there were no infractions.
							</p>
						</c:if>
						<c:if test="${bg.screeningStatusType eq 'failed'}">
							<p>
								<img src="${mediaPrefix}/images/failed_screen.png"/>
								You previously FAILED a Background on <fmt:formatDate value="${bg.responseDate.time}" pattern="MM/dd/yyyy"/>
							</p>
						</c:if>
					</c:forEach>
					<div class="wm-action-container">
						<a href="/profile" class="button">Back to profile</a>
						<button class="button renewal">Renew Now</button>
					</div>
				</div>
			</div>
		</div>
	</c:if>



	<c:if test="${bkgrndCheckPurchase}">
		<div id="renewForm" class="<c:if test="${bkgrndCheckPassed || bkgrndCheckFailed || bkgrndCheckCancelled}">dn</c:if>">
			<div class="row_sidebar_right">
				<div class="content">
					<div class="inner-container">
						<div class="page-header clear">
							<h2 class="fl">Order a Background Check (<span class="screening-cost"><fmt:formatNumber value="${screeningPrices[screeningType]}" currencySymbol="$" type="currency"/></span>)</h2>
						</div>
						<c:if test="${!intlMessage}">
						<div class="row page-subheader">
							<p class="span7">
								<b>Fact:</b> Workers with a background check make lots more money. For just $40, you tell potential employers that you are trustworthy and will start getting more work.
								<c:if test="${accountSummary.withdrawableCash >= 40.00}">
									You currently have <fmt:formatNumber value="${accountSummary.withdrawableCash}" currencySymbol="$" type="currency"/> in your Work Market account. You can pay by earnings or by credit card, which will be collected on the next screen.
								</c:if>
								<c:if test="${accountSummary.withdrawableCash < 40.00}">Your credit card details will be collected on the next screen.</c:if>
							</p>
							<div class="span3 buyer-badge">
								<img class="screening-header-icon" src="${mediaPrefix}/images/live_icons/assignments/passed_check_2.svg">
								<p class="buyer-badge--text">Work Market clients trust this badge.</p>
							</div>
						</div>
						<p><strong>Please enter your information exactly as it appears in legal documents.</strong>
							</c:if>

							<c:if test="${intlMessage}">
						<p>
							Verify your identity and have your background researched to maximize opportunities for Work
							Market projects.
						</p>

						<p>
							<i>Work Market handles your private information with the highest security standards. Data
								collected here that
								were not already part of your profile is securely transferred to Sterling and will not
								saved.</i>
						<p>
						<p><strong>Step 1: Please enter your information exactly as it appears in legal documents.</strong>
						</p>
						</c:if>

						<c:set var="formBody" scope="request">
							<h5>Terms and Conditions</h5>

							<div class="terms-and-conditions scroll-box">
								<p>
									By submitting this information, you affirm that all of the information in this application
									is
									correct, complete, and applicable to you. Furthermore, you agree to comply with Work
									Market's
									Terms of Use and you authorize Work Market to obtain a consumer report from SterlingBackcheck that may contain information on your criminal history, address history,
									character, general reputation, personal characteristics, and mode of living. You understand
									that
									Work Market may review this report and that Work Market retains the right to remove your
									profile
									and account from the Work Market site based on this report subject to any applicable laws.
									If
									the results of your report are "CLEAR", you consent to the automatic, public release of your
									report's results. If the results of your report are "CLEAR" or if you decide to share this
									report publicly after receiving results that are not "CLEAR", you understand that the
									information in this report may factor into other party's decisions to engage you as a
									provider.
									You agree to indemnify and hold harmless Work Market from any loss or liability that may
									result
									from your sharing this report publicly. If you would like a copy of this form and your
									authorization, please print this page.
								</p>
							</div>

							<p>By submitting this form, you are agreeing to the Terms and Conditions.</p>

							<div class="wm-action-container">
								<a class="button" href="/profile-edit">Cancel</a>
								<button type="submit" class="button" id="submit_btn">Submit</button>
							</div>

						</c:set>

						<c:import url="/WEB-INF/views/web/partials/screening/screening_form.jsp"/>
					</div>
				</div>

				<c:import url="/WEB-INF/views/web/partials/screening/about.jsp"/>
			</div>
		</div>
	</c:if>
</wm:app>
