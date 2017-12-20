<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="wm" tagdir="/WEB-INF/tags" %>

<wm:app pagetitle="Insurance Powered By Insureon" bodyclass="screening">

	<div id="insureon_more" class="content">
		<div class="page-header">
			<h2>Insurance Powered by Insureon</h2>
		</div>
		<div class="well" data-behavior="info-toggle">
			<p>
				Work Market has partnered with Insureon to help you meet your business liability insurance needs. Complete
				the forms below to receive a quote in as little as 24 hours. To receive your complimentary insurance quotes,
				you will be asked to fill out Insureon's online application, which includes questions about your business
				operations, the risks you face, and the type of insurance you're interested in purchasing. Once you've
				completed the application, you will receive insurance policy quotes in your email inbox.
			</p>

			<div class="info-toggler">More About Insureon</div>
			<div class="dn">

				<p>
					The nation's leading online insurance agent, Insureon specializes in insuring small and micro businesses,
					including sole proprietors and those with 25 or fewer employees.
					Insureon's innovative, all-online model allows it to offer competitive quotes from top-rated insurers
					much more efficiently than brick-and-mortar insurance agents are able to - often, business owners can
					have proof of insurance in as little as 24 hours.
				</p>

				<p>
					Since its founding in 2000, Insureon has provided coverage for more than 30,000 small-business owners
					around the United States, and it continues to grow as American entrepreneurs increasingly value the
					convenience of purchasing and managing their insurance policies online.
				</p>

				<p>
					The application process can take as little as 15 minutes. If you have questions at any time, an Insureon
					agent is available to answer them and offer any additional guidance you need.
				</p>
			</div>
		</div>
		<iframe class="insureon" src="https://workmarket.insureon.com/quote/?esource=21697&wmid=${companyID}"></iframe>
	</div>

</wm:app>
