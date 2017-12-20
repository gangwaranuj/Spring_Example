<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="sf" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="wm-csrf" uri="http://www.workmarket.com/taglib/csrf" %>
<%@ taglib prefix="wm" tagdir="/WEB-INF/tags" %>

<wm:app pagetitle="Generate an Invoice" bodyclass="accountSettings" breadcrumbSection="Work" breadcrumbSectionURI="/settings" breadcrumbPage="Generate an Invoice" webpackScript="payments">

	<script>
		var config = {
			'payments': ${contextJson}
		};
	</script>

	<style type="text/css">
		img.ss {
			background-color: #fff;
			border: 1px solid #ddd;
			box-shadow: 0 3px 5px rgba(0, 0, 0, 0.2);
			-moz-box-shadow: 0 2px 9px rgba(0, 0, 0, 0.2);
			-webkit-box-shadow: 0 2px 9px rgba(0, 0, 0, 0.2);
		}
	</style>

	<div class="row_sidebar_left">
		<div class="sidebar">
			<c:import url='/WEB-INF/views/web/partials/mmw/mmw_sidebar.jsp' />
		</div>
		<div class="content">
			<div class="inner-container">
				<div class="page-header clear">
					<h3>Generate an Invoice</h3>
				</div>
				<p>Generate an invoice for your accounting department to request funds for deposit with Work Market.</p>

				<div class="row">
					<div class="span6">
						<sf:form action='/funds/invoice' method="POST" modelAttribute="generateInvoiceForm" cssClass="form-stacked" accept-charset="utf-8">
							<wm-csrf:csrfToken />

							<c:import url="/WEB-INF/views/web/partials/general/notices.jsp">
								<c:param name="bundle" value="${bundle}"/>
							</c:import>

							<div class="clearfix">
								<label for='po_number' class='required'>Purchase Order #</label>

								<div class="input">
									<sf:input path="po_number" value="${po_number}" id="po_number" maxlength="20"/>
								</div>
							</div>
							<div class="clearfix">
								<label for='amount' class='required'>Amount</label>

								<div class="input">
									<sf:input path="amount" type="number" step="any" value="${amount}" id="amount" maxlength="10"/>
								</div>
							</div>
							<div class="clearfix">
								<label for='first_name' class='required'>First Name</label>

								<div class="input">
									<sf:input path="first_name" value="${first_name}" id="first_name" maxlength="255"/>
								</div>
							</div>
							<div class="clearfix">
								<label for='last_name' class='required'>Last Name</label>

								<div class="input">
									<sf:input path="last_name" value="${last_name}" id="last_name" maxlength="255"/>
								</div>
							</div>

							<div class="clearfix">
								<label for='address1' class='required'>Address</label>

								<div class="input">
									<sf:input path="address1" value="${address1}" id="address1" maxlength="255" size="30"/>
								</div>
							</div>
							<div class="clearfix">
								<label for='address2'>Address Line 2</label>

								<div class="input">
									<sf:input path="address2" value="${address2}" id="address2" maxlength="255" size="30"/>
								</div>
							</div>
							<div class="clearfix">
								<label for='city' class='required'>City</label>

								<div class="input">
									<sf:input path="city" value="${city}" id="city" maxlength="255"/>
								</div>
							</div>
							<div class="clearfix">
								<label for='state' class='required'>State/Province</label>

								<div class="input">
									<sf:select path="state" value="${state}" id="state">
										<option value="">- Select -</option>
										<c:forEach var="country" items="${statesCountries}">
											<optgroup label="${country.key}">
												<c:forEach var="state" items="${country.value}">
													<sf:option value="${state.value}" label="${state.key}"/>
												</c:forEach>
											</optgroup>
										</c:forEach>
									</sf:select>
								</div>
							</div>
							<div class="clearfix">
								<label class="required">Postal Code</label>

								<div class="input">
									<sf:input path="postalCode" value="${postalCode}" maxlength="7" id="postal_code"/>
								</div>
							</div>

							<div class="clearfix">
								<label for='phone' class='required'>Phone</label>

								<div class="input">
									<sf:input path="phone" value="${phone}" type="tel" id="phone" maxlength='255' alt="phone-us"/>
								</div>
							</div>
							<div class="wm-action-container">
								<button type="submit" class="button">Generate Invoice</button>
							</div>
						</sf:form>
					</div>
					<div class="span5">
						<p><em>Sample of what your invoice will look like:</em></p>
						<img src="${mediaPrefix}/images/invoice-ss.png" class="ss" width="300"/>
					</div>
				</div>
			</div>
		</div>
	</div>

</wm:app>
