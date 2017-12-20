<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="wmfmt" uri="http://www.workmarket.com/taglib/fmt" %>

<strong>Computation of Tax</strong>
<p>
	You are responsible for your tax obligations for services and materials.
	If you want to report an amount other than 0%, please <a id="collect_tax">click here</a>.
</p>


<div class="dn" id="collect_tax_section">
	<input type="hidden" name='collect_tax' value='1'/>
	<table class="tax-summary">
		<tbody>
			<tr>
				<td width="80%">
					<small name='tax_percent'>
						Enter the <strong>tax rate (%)</strong>
					</small>
				</td>
				<td class="irs">
					<input type="text" class="fr span1 tax_percent" name='tax_percent' value="0" />
				</td>
			</tr>
			<tr>
				<td>
					<small class="taxrate">
						This is the <strong>actual assignment value</strong>
			   	</small>
				</td>
				<td class="irs"><div class="tax_to_diff fr"></div></td>
			</tr>
			<tr>
				<td>
					<small class="taxrate" name='tax_to_report'>
						This is your total <strong>tax obligation</strong>
						<span class="tooltipped tooltipped-n" aria-label="This amount will not be deducted from your earnings. At any time, you can run an earnings report that includes tax obligation data.">
							<i class="wm-icon-question-filled"></i>
						</span>
					</small>
				</td>
				<td class="irs">
					<div class="tax_to_report fr">$ <c:out value="${not empty work.payment.salesTaxCollected ? work.payment.salesTaxCollected : '0.00'}"/></div>
				</td>
			</tr>
		</tbody>
	</table>
	<p>
		<small>
			<strong>Taxes:</strong>
			If taxes are due on the services or materials you provided, you are
			responsible for keeping track of the amount reportable to federal, state,
			and local authorities. The assignment value received is always inclusive of
			any tax due. Check your <a href="/reports">earnings report</a> to view a
			summary of all your tax obligations.
		</small>
	</p>
</div>
