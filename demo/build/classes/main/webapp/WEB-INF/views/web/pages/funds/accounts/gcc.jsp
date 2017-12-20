<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="wm-csrf" uri="http://www.workmarket.com/taglib/csrf" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="wm" tagdir="/WEB-INF/tags" %>

<wm:app pagetitle="Request a Work Market Visa card" bodyclass="accountSettings gccPage" breadcrumbSection="Payments" breadcrumbSectionURI="/payments" breadcrumbPage="WM Visa Card" webpackScript="payments">

	<script>
		var config = {
			'payments': ${contextJson}
		};
	</script>

	<div class="page-header clearfix">
		<h3 class="fl">Apply for a Work Market Visa Card *For US Citizens Only*</h3>
	</div>

	<div class="row_wide_sidebar_right">
		<div class="content">
			<p>
				<strong>Get faster access to your funds.</strong>
				Complete the form below to apply for a Work Market Visa Card. Once approved, you will receive your card in the
				mail within 7-10 business days.
			</p>
			<p>
				After receiving your card, you can set up automatic withdrawal, and use your card
				anywhere Visa is accepted.
				For more information,
				<a target="_blank" href="http://j.mp/16V5MH7">visit the Help Center.</a>
			</p>
			<hr/>
			<form:form modelAttribute="accountForm" action="/funds/accounts/gcc" method="POST">
				<wm-csrf:csrfToken/>
				<form:hidden path="type"/>
				<form:hidden path="bankAccountTypeCode"/>

				<c:import url='/WEB-INF/views/web/partials/message.jsp'/>
				<c:import url='/WEB-INF/views/web/partials/general/notices_js.jsp'>
					<c:param name="containerId" value="validation_messages"/>
				</c:import>
				<h4>Permanent Address</h4>
				<br/>
				<div class="clearfix">
					<div class="fl">
						<form:label path="firstName"><strong><fmt:message key="first_name" /></strong></form:label>
						<form:input path="firstName"/>
					</div>
					<div class="fl offset1">
						<form:label path="lastName"><strong><fmt:message key="last_name" /></strong></form:label>
						<form:input path="lastName"/>
					</div>
				</div>

				<div class="clearfix">
					<div class="fl">
						<form:label path="country"><strong><fmt:message key="country" /></strong></form:label>
						<form:select path="country" cssClass="country" items="${countries}" title="- Select Country -" data-related-state="state" data-related-province="province"/>
					</div>
				</div>

				<div class="clearfix">
					<div class="fl">
						<form:label path="address1"><strong><fmt:message key="address" /></strong></form:label>
						<form:input path="address1"/>
					</div>
					<div class="fl offset1">
						<form:label path="address2"><strong><fmt:message key="address2" /></strong></form:label>
						<form:input path="address2"/>
					</div>
				</div>

				<div class="clearfix">
					<div class="fl">
						<form:label path="city"><strong><fmt:message key="city" /></strong></form:label>
						<form:input path="city"/>
					</div>
					<div class="fl offset1">
						<form:label path="state"><strong><fmt:message key="state" /></strong></form:label>
						<select <c:if test="${accountForm.country eq 'USA'}">name="state"</c:if> class="xsmall state" <c:if test="${accountForm.country ne 'USA'}">style="display:none" </c:if>>
							<option value="">- Select State -</option>
							<c:forEach var="state" items="${states}">
								<option value="${state.key}"
										<c:if test="${accountForm.state == state.key }">selected="selected"</c:if>>
									<c:out value="${state.value}"/>
								</option>
							</c:forEach>
						</select>
						<select <c:if test="${accountForm.country eq 'CAN'}">name="state"</c:if> class="xsmall province" <c:if test="${accountForm.country ne 'CAN'}">style="display:none"</c:if>>
							<option value="">- Select Province -</option>
							<c:forEach var="province" items="${provinces}">
								<option value="${province.key}"
										<c:if test="${accountForm.state == province.key }">selected="selected"</c:if>>
									<c:out value="${province.value}"/>
								</option>
							</c:forEach>
						</select>
						<input type="text" class="state"
							   <c:if test="${accountForm.country ne 'CAN' and accountForm.country ne 'USA'}">name="state"</c:if>
								<c:if test="${accountForm.country eq 'CAN' or accountForm.country eq 'USA'}"> style="display: none"</c:if> />
					</div>
				</div>
				<div class="clearfix">
					<div class="fl">
						<form:label path="postalCode"><strong><fmt:message key="postalCode" /></strong></form:label>
						<form:input path="postalCode" class="input-small"></form:input>
					</div>
				</div>
				<div class="clearfix">
					<div class="fl">
						<form:label path="mainAddressIsDifferentThenPermanent">
							<form:checkbox path="mainAddressIsDifferentThenPermanent" cssClass="checkbox add_mailing_address" />
							Mailing address is different then permanent address
						</form:label>
					</div>
				</div>
				<div class="container" id="mailing_address" <c:if test="${not accountForm.mainAddressIsDifferentThenPermanent}">style="display:none"</c:if>>
					<h4>Mailing Address</h4>
					<div class="clearfix">
						<div class="fl">
							<form:label path="firstName2"><strong><fmt:message key="first_name" /></strong></form:label>
							<form:input path="firstName2" name="first_name"/>
						</div>
						<div class="fl offset1">
							<form:label path="lastName2"><strong><fmt:message key="last_name" /></strong></form:label>
							<form:input path="lastName2"/>
						</div>
					</div>

					<div class="clearfix">
						<div class="fl">
							<form:label path="country2"><strong><fmt:message key="country" /></strong></form:label>
							<form:select path="country2" cssClass="country" data-related-state="state2" data-related-province="province2">
								<form:option value="">- Select Country -</form:option>
								<form:options items="${countries}" />
							</form:select>
						</div>
					</div>

					<div class="clearfix">
						<div class="fl">
							<form:label path="alternativeAddress"><strong><fmt:message key="address" /></strong></form:label>
							<form:input path="alternativeAddress"/>
						</div>
						<div class="fl offset1">
							<form:label path="alternativeAddress2"><strong><fmt:message key="address2" /></strong></form:label>
							<form:input path="alternativeAddress2"/>
						</div>
					</div>

					<div class="clearfix">
						<div class="fl">
							<form:label path="city2"><strong><fmt:message key="city" /></strong></form:label>
							<form:input path="city2"/>
						</div>
						<div class="fl offset1">
							<form:label path="state2"><strong><fmt:message key="state" /></strong></form:label>

							<select <c:if test="${accountForm.country2 eq 'USA'}">name="state2"</c:if> class="xsmall state2" <c:if test="${accountForm.country2 ne 'USA'}">style="display:none" </c:if>>
								<option value="">- Select State -</option>
								<c:forEach var="state" items="${states}">
									<option value="${state.key}"
											<c:if test="${accountForm.state == state.key }">selected="selected"</c:if>>
										<c:out value="${state.value}"/>
									</option>
								</c:forEach>
							</select>
							<select <c:if test="${accountForm.country2 eq 'CAN'}">name="state2"</c:if> class="xsmall province2" <c:if test="${accountForm.country2 ne 'CAN'}">style="display:none"</c:if>>
								<option value="">- Select Province -</option>
								<c:forEach var="province" items="${provinces}">
									<option value="${province.key}"
											<c:if test="${accountForm.state == province.key }">selected="selected"</c:if>>
										<c:out value="${province.value}"/>
									</option>
								</c:forEach>
							</select>
							<input type="text" class="state2"
								   <c:if test="${accountForm.country2 ne 'CAN' and accountForm.country2 ne 'USA'}">name="state2"</c:if>
									<c:if test="${accountForm.country2 eq 'CAN' or accountForm.country2 eq 'USA'}"> style="display: none"</c:if> />
						</div>
					</div>
					<div class="clearfix">
						<div class="fl">
							<form:label path="postalCode2"><strong><fmt:message key="postalCode" /></strong></form:label>
							<form:input path="postalCode2" class="input-small"></form:input>
						</div>
					</div>
				</div>
				<br/>
				<h4>Identification</h4>
				<br/>
				<div class="clearfix">
					<div class="fl">
						<form:label path="govIdType"><strong><fmt:message key="government_id_type" /></strong></form:label>
						<form:select path="govIdType">
							<option value="" selected="selected">- Select ID Type-</option>
							<c:forEach items="${govIdTypes}" var="entry">
								<form:option value="${entry.key}">${entry.value}</form:option>
							</c:forEach>
						</form:select>
					</div>
					<div class="fl offset1">
						<form:label path="govId"><strong><fmt:message key="government_id" /></strong></form:label>
						<form:input id="govId" path="govId"/>
					</div>
				</div>

				<div class="clearfix">
					<label><strong><fmt:message key="date_of_birth" /></strong></label>
					<form:select path="dobMonth" class="input-small">
						<option value="" selected="selected">- Month -</option>
						<c:forEach items="${months}" var="month" varStatus="loop">
							<form:option value='${loop.count}'><c:out value="${month}"/></form:option>
						</c:forEach>
					</form:select>

					<form:select path="dobDay" class="input-small">
						<option value="" selected="selected">- Day -</option>
						<c:forEach begin="1" end="31" varStatus="loop">
							<form:option value='${loop.count}'><c:out value="${loop.count}"/></form:option>
						</c:forEach>
					</form:select>
					<form:select path="dobYear" class="input-small">
						<option value="" selected="selected">- Year -</option>
						<c:forEach begin="${minYear}" end="${maxYear}" varStatus="yearLoop">
							<form:option value='${yearLoop.index}'><c:out value="${yearLoop.index}"/></form:option>
						</c:forEach>
					</form:select>
				</div>

				<div class="wm-action-container">
					<button type="submit" class="button">Apply</button>
				</div>
			</form:form>

		</div>
		<div class="sidebar">
			<div class="config">
				<div>
					<div id="gcc_card_display">
						<img id="gcc_card" src="${mediaPrefix}/images/wm_card_website.png"/>
						<p id="card_name"><c:out value="${currentUser.firstName}"/> <c:out value="${currentUser.lastName}"/></p>
					</div>
					<div id="gcc_card_benefits">
						<h4><strong>Benefits of your new <br/>
							Work Market Visa Card:</strong></h4>
						<ul>
							<li>Set up direct deposit for fastest payment</li>
							<li>Use the card anywhere in the world where Visa is accepted</li>
							<li>Free to sign up</li>
						</ul>
						<p><strong>Questions before signing up?</strong></p>
						<p>Call 1-866-395-9200</p>
					</div>
				</div>
			</div>
		</div>
	</div>
	</div>

</wm:app>
