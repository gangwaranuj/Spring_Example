<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib prefix="wm" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="wmfn" uri="http://www.workmarket.com/taglib/functions" %>

<c:set var="hasNewScreening" value="false" />
<sec:authorize access="hasFeature('hasNewScreening')">
	<c:set var="hasNewScreening" value="true" />
</sec:authorize>

<wm:app pagetitle="Order Drug Test" bodyclass="screening" isBootstrapDisabled="${hasNewScreening}" webpackScript="settings">
	<%@ page isELIgnored="false" %>

	<script>
		var config = {
			mode: 'screening',
			usaPrice: '${screeningPrices['bkgrdchk']}',
			intlPrice:  '${screeningPrices['bkgrdchkIN']}',
			price: '${screeningPrice}',
			firstName: '${screeningForm.firstName}',
			lastName: '${screeningForm.lastName}',
			address1: '${screeningForm.address1}',
			address2: '${screeningForm.address2}',
			city: '${screeningForm.city}',
			country: '${screeningForm.country}',
			state: '${screeningForm.state}',
			postalCode: '${screeningForm.postalCode}',
			email: '${screeningForm.email}',
			availableFunds: '${availableFunds}',
			isInternational: ${wmfn:boolean(intlMessage, true, false)},
			drugTestPassed: ${wmfn:boolean(drugTestPassed, true, false)},
			drugTestFailed: ${wmfn:boolean(drugTestFailed, true, false)},
			drugTestPending: ${wmfn:boolean(drugTestPending, true, false)}
		};
	</script>

	<c:import url="/WEB-INF/views/web/partials/general/notices.jsp">
		<c:param name="bundle" value="${bundle}"/>
	</c:import>

	<c:choose>
		<c:when test="${hasNewScreening}">
			<div id="wm-screening"></div>
		</c:when>
		<c:otherwise>
			<c:if test="${drugTestFailed}">
				<div class="content">
					<div class="inner-container">
						<div class="page-header clear">
							<h3>Drug Screening</h3>
						</div>

						<p><img src="${mediaPrefix}/images/failed_screen.png"/> Your drug screen indicated one or more infractions.</p>

						<p>Your profile is suspended on the Work Market platform. Anyone you have worked with at Work Market will no
							longer be able to send work to you or view your profile. They cannot see that you are suspended or that
							there may be issues with your drug test. You are still able to manage your profile and accounts and add work to the Work Market platform.
						</p>

						<p>At your request, Sterling will send you a copy of your report via email within 48 hours. Work Market will not
							see the report or the specific reason for an infraction on your drug screen.</p>

						<p>If you feel the information on your report is incomplete or inaccurate, please contact the Sterling
							Compliance Department at 1-800-853-3228 or email <a href="mailto:askcompliancemail@sterlinginfosystems.com">askcompliancemail@sterlinginfosystems.com</a>.
						</p>

						<p><strong>If you are unable to resolve this issue with SterlingBackcheck, you can contact Work Market at 1-212-229-WORK (9675) for further assistance.</strong></p>
						<c:if test="${not empty previousDrugList}">
						<p>
							<a href="#" class="view_previous">View previous screening result(s)</a>
							<div class="dn list_previous">
								<c:forEach var="d" items="${previousDrugList}">
								<c:if test="${d.screeningStatusType eq 'expired'}">
						<p><img src="${mediaPrefix}/images/passed_icon.png"/> Your Drug Screening was "CLEAR" on <fmt:formatDate value="${d.responseDate.time}" pattern="MM/dd/yyyy"/></p>
						</c:if>
						</c:forEach>
					</div>
					</p>
					</c:if>

					<div class="wm-action-container">
						<a href="/profile" class="button">Back to profile</a>
					</div>
				</div>
				</div>
			</c:if>

			<c:if test="${drugTestPassed}">
				<div class="content">
					<div class="inner-container">
						<div class="page-header clear">
							<h3>Drug Screening</h3>
						</div>

						<p><img src="${passedLabel}"/>Your Drug Screening check was "CLEAR" and your profile now shows an icon indicating that you passed your drug screening</p>

						<c:if test="${drugTestOld}">
							<div class="alert">
								Your Drug Screening test result is more than a year old. Many companies on Work Market prefer workers to get their drug screening taken once per year.
								We recommend you to retake your drug screening test in order to keep your status current.
							</div>
						</c:if>

						<c:if test="${not empty previousDrugList}">
						<p>
							<a href="#" class="view_previous">View previous screening result(s)</a>
							<div class="dn list_previous">
								<c:forEach var="d" items="${previousDrugList}">
								<c:if test="${d.screeningStatusType eq 'expired'}">
						<p><img src="${mediaPrefix}/images/passed_icon.png"/> Your Drug Screening was "CLEAR" on <fmt:formatDate value="${d.responseDate.time}" pattern="MM/dd/yyyy"/></p>
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
			</c:if>

			<c:if test="${drugTestPending}">
				<div class="content">
					<div class="inner-container">
						<div class="page-header clear">
							<h3>Drug Screening</h3>
						</div>

						<div class="alert"><img src="${mediaPrefix}/images/pending_icon.png"/> Your new Drug Screening Test is still pending. Once the result is available, you will be notified by email.</div>

						<c:if test="${not empty previousDrugList}">
						<p>
							<a href="#" class="view_previous">View previous screening result(s)</a>
							<div class="dn list_previous">
								<c:forEach var="d" items="${previousDrugList}">
								<c:if test="${d.screeningStatusType eq 'passed'}">
						<p><img src="${passedLabel}"/> Your Drug Screening was "CLEAR" on <fmt:formatDate value="${d.responseDate.time}" pattern="MM/dd/yyyy"/>
							and your profile now shows an icon indicating that you passed your drug screening
						</p>
						</c:if>
						</c:forEach>
					</div>
					</p>
					</c:if>

					<div class="wm-action-container">
						<a href="/profile" class="button">Back to profile</a>
						<button class="button" disabled>Retake Now</button>
					</div>
				</div>
				</div>
			</c:if>

			<c:if test="${drugTestPurchase}">
				<div id="renewForm" class="<c:if test="${drugTestPassed}">dn</c:if>">
					<div class="row_sidebar_right">
						<div class="content">
							<div class="inner-container">

								<div class="page-header clear">
									<h2 class="fl">Order a Drug Screen (<span id="price"></span><fmt:formatNumber value="${screeningPrice}" currencySymbol="$" type="currency"/>)</h2>
									<a href="/profile" class="fr"><small class="meta">&laquo; Back to profile</small></a>
								</div>

								<div class="alert-message alert-error dn" id="availability-alert">
									<p>Drug screening is not available in your country at this time.</p>
								</div>

								<p>
									Many organizations require drug testing to ensure a safer and more productive work environment. Work
									Market handles your private information with the highest security standards. Data entered here that was
									not already part of your profile is provided securely to Sterling and is not saved with your profile
									going forward.
								</p>

								<p><strong>Please enter your information exactly as it appears in legal documents.</strong></p>

								<c:set var="formBody" scope="request">
									<h5>Terms and Conditions</h5>

									<div class="scroll-box">
										<p>By submitting this information, you affirm that all of the information in this application is
											correct, complete, and applicable to you. Furthermore, you agree to comply with Work Market's
											Terms of Use and you authorize Work Market to obtain a consumer report from SterlingBackcheck that may contain information on your criminal history, address history,
											character, general reputation, personal characteristics, and mode of living. You understand that
											Work Market may review this report and that Work Market retains the right to remove your profile
											and account from the Work Market site based on this report subject to any applicable laws. If
											the results of your report are "CLEAR", you consent to the automatic, public release of your
											report's results. If the results of your report are "CLEAR" or if you decide to share this
											report publicly after receiving results that are not "CLEAR", you understand that the
											information in this report may factor into other party's decisions to engage you as a provider.
											You agree to indemnify and hold harmless Work Market from any loss or liability that may result
											from your sharing this report publicly. If you would like a copy of this form and your
											authorization, please print this page.</p>
									</div>

									<p>By submitting this form, you are agreeing to the Terms and Conditions.</p>

									<div class="wm-action-container">
										<a class="button" href="/profile-edit">Cancel</a>
										<button type="submit" class="button">Submit</button>
									</div>
								</c:set>

								<c:import url="/WEB-INF/views/web/partials/screening/screening_form.jsp"/>
							</div>
						</div>
						<div class="sidebar">
							<div class="well-b2">
								<h3>About Sterling</h3>
								<div class="well-content">
									<p>
										Work Market has partnered with Sterling to provide background screening tools for the
										people on our network.
									</p>

									<p>
										With your permission, Work Market will initiate the drug screening process that requires you to
										physically visit a drug testing facility and provide a specimen for testing. Sterling will
										provide details on testing locations near you, required procedure and paperwork to successfully
										complete the drug test.
									</p>
								</div>
							</div>

							<div class="well-b2">
								<h3>What Happens Next</h3>
								<div class="well-content">
									<p>
										If the results of your drug test are "CLEAR", meaning there are no infractions of any kind in
										your report, you will receive a status and icon on your Work Market profile indicating that you
										passed the drug test.
									</p>

									<p>
										If the results of your drug test include any infractions, you will have the opportunity to view
										your report and contest the results with Sterling.
									</p>
								</div>
							</div>
						</div>
					</div>
				</div>
			</c:if>
		</c:otherwise>
	</c:choose>
</wm:app>
