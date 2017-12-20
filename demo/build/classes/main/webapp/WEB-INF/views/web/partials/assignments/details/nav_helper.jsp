<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="wmfn" uri="http://www.workmarket.com/taglib/functions" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="vr" uri="http://www.workmarket.com/taglib/velvet-rope" %>

<c:set var="hidePricing" value="${currentUser.companyHidesPricing}" />
<sec:authorize access="hasAnyRole('ACL_ADMIN', 'ACL_MANAGER', 'ACL_DISPATCHER')">
	<c:set var="hidePricing" value="false" />
</sec:authorize>

<c:if test="${not(not workResponse.workBundle && is_resource && work.status.code == workStatusTypes['SENT']) && nav.size() > 0}">
	<div data-dropdown="dropdown" data-placement="left" class="dropdown pull-right assign-drop">
		<a class="dropdown-toggle more-actions" data-toggle="dropdown"><i class="wm-icon-gear icon-large"></i><span class="caret"></span></a>
			<ul class="dropdown-menu pull-right">
			<c:forEach var="n" items="${nav}">
				<c:choose>
					<c:when test="${wmfn:instanceOf(n, 'java.lang.String')}">
						<c:set var="mapping" value="${navMap[n]}"/>
						<c:set var="url" value="${mapping['url']}"/>
						<c:set var="title" value="${mapping['title']}"/>
						<li><a href="${fn:replace(url, "%s", work.workNumber)}" class="${n}_action"><c:out value="${title}" /></a></li>
							<li class="divider"></li>
					</c:when>
					<c:when test="${wmfn:instanceOf(n, 'java.util.List')}">
						<c:if test="${fn:length(n) > 0}">
							<c:forEach var="m" items="${n}">
								<c:set var="mapping2" value="${navMap[m]}"/>
								<c:set var="url" value="${mapping2['url']}"/>
								<c:set var="title" value="${mapping2['title']}"/>
								<c:choose>
									<c:when test="${m eq '-'}">
										<li class="divider"></li>
									</c:when>
									<c:when test="${m eq 'unblockclient'}">
										<c:if test="${displayUnblock}">
											<li><a href="${fn:replace(url, "%s", work.workNumber)}" class="${m}_action"><c:out value="${fn:replace(title, '%s', companyName)}" /></a></li>
										</c:if>
									</c:when>
									<c:when test="${m eq 'blockclient'}">
										<c:if test="${not displayUnblock}">
											<li><a href="#" class="${m}_action"><c:out value="${fn:replace(title, '%s', companyName)}" /></a></li>
										</c:if>
									</c:when>
									<c:when test="${m eq 'masquerade_owner'}">
										<li><a href="${fn:replace(fn:replace(url,"%s",wmfn:urlEncode(ownerEmail,'utf-8')),"%t",wmfn:urlEncode(ownerFullName,'utf-8'))}" class="${m}_action"><c:out value="${title}" /></a></li>
									</c:when>
									<c:when test="${m eq 'masquerade_resource'}">
										<c:if test="${hasActiveResource}">
											<li><a href="${fn:replace(fn:replace(url,"%s",wmfn:urlEncode(resourceEmail,'utf-8')),"%t",wmfn:urlEncode(resourceFullName,'utf-8'))}" class="${m}_action"><c:out value="${title}" /></a></li>
										</c:if>
									</c:when>
									<c:when test="${m eq 'reindex_assignment'}">
										<li>
											<a href="${fn:replace(url,"%s",work.workNumber)}" class="${m}_action"><c:out value="${title}" /></a>
										</li>
									</c:when>
									<c:when test="${m eq 'activate_now'}">
										<li>
											<a href="${fn:replace(url,"%s", work.workNumber)}" class="${m}_action"><c:out value="${title}" /></a>
										</li>
									</c:when>
									<c:otherwise>
										<sec:authorize access="principal.editPricingCustomAuth">
											<c:choose>
												<c:when test="${m eq 'budget_increase' or m eq 'reimbursement' or m eq 'bonus'}">
													<c:choose>
														<c:when test="${isWorkerCompany}" >
															<c:if test="${!hidePricing}">
																<li>
																	<a href="${fn:replace(url,"%s", work.workNumber)}" class="${m}_action">
																		<c:out value="${title}" />
																	</a>
																</li>
															</c:if>
														</c:when>
														<c:otherwise>
															<li>
																<a href="${fn:replace(url,"%s", work.workNumber)}" class="${m}_action">
																	<c:out value="${title}" />
																</a>
															</li>
														</c:otherwise>
													</c:choose>
												</c:when>
												<c:otherwise>
													<c:if test="${not (m eq 'negotiate' and currentUser.dispatcher)}">
														<li>
															<a href="${fn:replace(url,"%s", work.workNumber)}" class="${m}_action">
																<c:out value="${title}" />
															</a>
														</li>
													</c:if>
												</c:otherwise>
											</c:choose>
										</sec:authorize>
										<sec:authorize access="!principal.editPricingCustomAuth">
											<c:choose>
												<c:when test="${(!is_in_work_company and !isBuyerAuthorizedToEditPrice and title eq 'Budget Increase') or
														title eq 'Edit Pricing' or title eq 'Budget Increase' or title eq 'Expense Reimbursement' or title eq 'Bonus'}">

													<c:choose>
														<c:when test="${isWorkerCompany}" >
															<c:if test="${!hidePricing}">
																<li>
																	<a href="${fn:replace(url,"%s", work.workNumber)}" class="${m}_action disabled"><c:out value="${title}" /></a>
																</li>
															</c:if>
														</c:when>
														<c:otherwise>
															<li>
																<a href="${fn:replace(url,"%s", work.workNumber)}" class="${m}_action disabled"><c:out value="${title}" /></a>
															</li>
														</c:otherwise>
													</c:choose>

												</c:when>
												<c:otherwise>
													<li>
														<a href="${fn:replace(url,"%s", work.workNumber)}" class="${m}_action"><c:out value="${title}" /></a>
													</li>
												</c:otherwise>
											</c:choose>
										</sec:authorize>
									</c:otherwise>
								</c:choose>
							</c:forEach>
						</c:if>
					</c:when>
				</c:choose>
			</c:forEach>
		</ul>
	</div>
</c:if>

<c:if test="${!isCompanyResource}">
	<jsp:include page="/WEB-INF/views/web/partials/general/block_client.jsp"/>
</c:if>
<jsp:include page="/WEB-INF/views/web/partials/general/schedule_conflict.jsp"/>
