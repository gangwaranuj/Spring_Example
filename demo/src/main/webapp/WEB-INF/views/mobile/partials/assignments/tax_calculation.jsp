<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="wmfmt" uri="http://www.workmarket.com/taglib/fmt" %>

<div class="tax-section">
	<ul class="collect-tax-section">
		<li><h3>Computation of Tax</h3></li>
		<li class="tax-responsibility">
			<p>You are responsible for your tax obligations for services and materials.</p>
			<input type="checkbox" class="collect-tax pull-right" name="collect_tax"/>
			<small class="pull-left">Check this box to report a tax rate other than 0%.</small>
		</li>
		<li style="display: none;">
			<div>Enter the tax rate</div>
			<div>
				<label><input type="text" name='tax_percent' class="tax-percent" />%</label>
			</div>
		</li>
		<li style="display: none;">
			<div>Actual assignment value</div>
			<div class="tax-to-diff">$ 0.00</div>
		</li>
		<li style="display: none;">
			<div>Your total tax obligation</div>
			<div class="tax-to-report">$ <c:out value="${not empty work.payment.salesTaxCollected ? work.payment.salesTaxCollected : '0.00'}"/></div>
		</li>
		<li style="display: none;">
			<p class="tax-note"><strong>Note:</strong> This amount will not be deducted from your earnings. At any time, you can run an earnings report that includes tax obligation data.</p>
		</li>
	</ul>
</div>
