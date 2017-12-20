<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<p><a href="/apidocs">&laquo; <fmt:message key="api_endpoint_sidebar.back_to_api" /></a></p>
<div class="sidebar-card">
	<ul class="unstyled">
		<li><fmt:message key="global.authorization" />
			<ul>
				<li><a href="/apidocs/endpoints/authorization/request"><fmt:message key="global.request" /></a></li>
			</ul>
		</li>
		<li><fmt:message key="global.assignments" />
			<ul>
				<li><a href="/apidocs/endpoints/assignments/create"><fmt:message key="global.create" /></a></li>
				<li><a href="/apidocs/endpoints/assignments/edit"><fmt:message key="global.edit" /></a></li>
				<li><a href="/apidocs/endpoints/assignments/get"><fmt:message key="global.get" /></a></li>
				<li><a href="/apidocs/endpoints/assignments/list"><fmt:message key="global.list" /></a></li>
				<li><a href="/apidocs/endpoints/assignments/list_updated"><fmt:message key="api_endpoint_sidebar.list_update" /></a></li>
				<li><a href="/apidocs/endpoints/assignments/statuses"><fmt:message key="global.statuses" /></a></li>
				<li><a href="/apidocs/endpoints/assignments/customfields/list"><fmt:message key="global.custom_fields_list" /></a></li>
				<li><fmt:message key="api_endpoint_sidebar.workflow_actions" />
					<ul>
						<li><a href="/apidocs/endpoints/assignments/send"><fmt:message key="global.send" /></a></li>
						<li><a href="/apidocs/endpoints/assignments/delete"><fmt:message key="global.delete" /></a></li>
						<li><a href="/apidocs/endpoints/assignments/cancel"><fmt:message key="global.cancel" /></a></li>
						<li><a href="/apidocs/endpoints/assignments/void"><fmt:message key="global.void" /></a></li>
						<li><a href="/apidocs/endpoints/assignments/complete"><fmt:message key="global.complete" /></a></li>
						<li><a href="/apidocs/endpoints/assignments/approve_payment"><fmt:messasge key="api_endpoint_sidebar.approve_for_payment" /></a></li>
						<li><a href="/apidocs/endpoints/assignments/reject_payment"><fmt:messasge key="api_endpoint_sidebar.reject_for_payment" /></a></li>
					</ul>
				</li>
				<li><fmt:message key="api_endpoint_sidebar.offers_negotiations_requests" />
					<ul>
						<li><fmt:message key="global.offers." />
							<ul>
								<li><a href="/apidocs/endpoints/assignments/offers/accept"><fmt:message key="global.accept" /></a></li>
								<li><a href="/apidocs/endpoints/assignments/offers/decline"><fmt:message key="global.decline" /></a></li>
							</ul>
						</li>
						<li><fmt:message key="api_endpoint_sidebar.reschedules" />
							<ul>
								<li><a href="/apidocs/endpoints/assignments/negotiations/reschedule"><fmt:message key="global.create" /></a></li>
								<li><a href="/apidocs/endpoints/assignments/negotiations/accept_reschedule"><fmt:message key="global.accept" /></a></li>
								<li><a href="/apidocs/endpoints/assignments/negotiations/decline_reschedule"><fmt:message key="global.decline" /></a></li>
							</ul>
						</li>
						<li><fmt:message key="api_endpoint_sidebar.budget_increases" />
							<ul>
								<li><a href="/apidocs/endpoints/assignments/budget_increase"><fmt:message key="global.create" /></a></li>
								<li><a href="/apidocs/endpoints/assignments/negotiations/accept_budget_increase"><fmt:message key="global.accept" /></a></li>
								<li><a href="/apidocs/endpoints/assignments/negotiations/decline_budget_increase"><fmt:message key="global.decline" /></a></li>
							</ul>
						</li>
						<li><fmt:message key="api_endpoint_sidebar.expense_reimbursements" />
							<ul>
								<li><a href="/apidocs/endpoints/assignments/expense_reimbursement"><fmt:message key="global.create" /></a></li>
								<li><a href="/apidocs/endpoints/assignments/negotiations/accept_expense_reimbursement"><fmt:message key="global.accept" /></a></li>
								<li><a href="/apidocs/endpoints/assignments/negotiations/decline_expense_reimbursement"><fmt:message key="global.decline" /></a></li>
							</ul>
						</li>
						<li><fmt:message key="api_endpoint_sidebar.bonuses" />
							<ul>
								<li><a href="/apidocs/endpoints/assignments/negotiations/accept_bonus"><fmt:message key="global.accept" /></a></li>
								<li><a href="/apidocs/endpoints/assignments/negotiations/decline_bonus"><fmt:message key="global.decline" /></a></li>
							</ul>
						</li>
					</ul>
				</li>
				<li><fmt:message key="global.questions" />
					<ul>
						<li><a href="/apidocs/endpoints/assignments/questions/answer"><fmt:message key="global.answer" /></a></li>
					</ul>
				</li>
				<li><fmt:message key="global.labels" />
					<ul>
						<li><a href="/apidocs/endpoints/assignments/labels/list"><fmt:message key="global.list" /></a></li>
						<li><a href="/apidocs/endpoints/assignments/labels/add"><fmt:message key="global.add" /></a></li>
						<li><a href="/apidocs/endpoints/assignments/labels/remove"><fmt:message key="global.remove" /></a></li>
					</ul>
				</li>
				<li><fmt:message key="global.attachments" />
					<ul>
						<li><a href="/apidocs/endpoints/assignments/attachments/list"><fmt:message key="global.list" /></a></li>
						<li><a href="/apidocs/endpoints/assignments/attachments/get"><fmt:message key="global.get" /></a></li>
						<li><a href="/apidocs/endpoints/assignments/attachments/add"><fmt:message key="global.add" /></a></li>
						<li><a href="/apidocs/endpoints/assignments/attachments/remove"><fmt:message key="global.remove" /></a></li>
					</ul>
				</li>
				<li><fmt:message key="global.notes" />
					<ul>
						<li><a href="/apidocs/endpoints/assignments/notes/add"><fmt:message key="global.add" /></a></li>
					</ul>
				</li>
				<li><fmt:message key="global.templates" />
					<ul>
						<li><a href="/apidocs/endpoints/assignments/templates/list"><fmt:message key="global.list" /></a></li>
						<li><a href="/apidocs/endpoints/assignments/templates/get"><fmt:message key="global.get" /></a></li>
					</ul>
				</li>
			</ul>
		</li>
		<li><fmt:message key="global.groups" />
			<ul>
				<li><a href="/apidocs/endpoints/groups/list"><fmt:message key="global.list" /></a></li>
			</ul>
		</li>
		<li><fmt:message key="global.projects" />
			<ul>
				<li><a href="/apidocs/endpoints/projects/list"><fmt:message key="global.list" /></a></li>
				<li><a href="/apidocs/endpoints/projects/add"><fmt:message key="global.add" /></a></li>
			</ul>
		</li>
		<li><fmt:message key="global.constants" />
			<ul>
				<li><a href="/apidocs/endpoints/constants/industries"><fmt:message key="api_endpoint_sidebar.industries" /></a></li>
				<li><a href="/apidocs/endpoints/constants/location_types"><fmt:message key="api_endpoint_sidebar.location_types" /></a></li>
				<li><a href="/apidocs/endpoints/constants/dress_codes"><fmt:message key="api_endpoint_sidebar.dress_codes" /></a></li>
			</ul>
		</li>
		<li><fmt:message key="global.address_book" />
			<ul>
				<li><fmt:message key="global.clients" />
					<ul>
						<li><a href="/apidocs/endpoints/crm/clients/list"><fmt:message key="global.list" /></a></li>
						<li><a href="/apidocs/endpoints/addressbook/clients/add"><fmt:message key="global.add" /></a></li>
					</ul>
				</li>
				<li><fmt:message key="global.locations" />
					<ul>
						<li><a href="/apidocs/endpoints/crm/locations/list"><fmt:message key="global.list" /></a></li>
						<li><a href="/apidocs/endpoints/addressbook/clients/locations/add"><fmt:message key="global.add" /></a></li>
					</ul>
				</li>
				<li><fmt:message key="global.contacts" />
					<ul>
						<li><a href="/apidocs/endpoints/crm/contacts/list"><fmt:message key="global.list" /></a></li>
						<li><a href="/apidocs/endpoints/addressbook/clients/contacts/add"><fmt:message key="api_endpoint_sidebar.add_to_client" /></a></li>
						<li><a href="/apidocs/endpoints/addressbook/locations/contacts/add"><fmt:message key="api_endpoint_sidebar.add_to_location" /></a></li>
					</ul>
				</li>
			</ul>
		</li>
	</ul>
</div>
<p><a href="/apidocs">&laquo; <fmt:message key="api_endpoint_sidebar.back_to_api" /></a></p>
