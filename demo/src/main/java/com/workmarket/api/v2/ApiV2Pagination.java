package com.workmarket.api.v2;

import com.google.common.collect.ImmutableMap;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

/**
 * Model for API's pagination JSON payload
 */
public class ApiV2Pagination {

	public static final String NEXT_PAGE_LINK_KEY = "next";
	public static final String PREVIOUS_PAGE_LINK_KEY = "prev";
	public static final String FIRST_PAGE_LINK_KEY = "first";
	public static final String LAST_PAGE_LINK_KEY = "last";

	private Long page;
	private Long pageSize;
	private Long totalPageCount;
	private Long totalRecordCount;
	private Map<String, String> links;

	private ApiV2Pagination(ApiPaginationBuilder builder) {

		this.page = builder.page;
		this.pageSize = builder.pageSize;
		this.totalPageCount = builder.totalPageCount;
		this.totalRecordCount = builder.totalRecordCount;
		this.links = ImmutableMap.copyOf(builder.links);
	}

	public Long getPage() {
		return page;
	}

	public Long getPageSize() {
		return pageSize;
	}

	public Long getTotalPageCount() {
		return totalPageCount;
	}

	public Long getTotalRecordCount() {
		return totalRecordCount;
	}

	public Map<String, String> getLinks() {
		return links;
	}

	public static class ApiPaginationBuilder {

		private Long page;
		private Long pageSize;
		private Long totalPageCount;
		private Long totalRecordCount;
		private Map<String, String> links;

		public ApiPaginationBuilder() {
		}

		public ApiPaginationBuilder page(final Long page) {
			this.page = page;
			return this;
		}

		public ApiPaginationBuilder pageSize(final Long pageSize) {
			this.pageSize = pageSize;
			return this;
		}

		public ApiPaginationBuilder totalPageCount(final Long totalPageCount) {
			this.totalPageCount = totalPageCount;
			return this;
		}

		public ApiPaginationBuilder totalRecordCount(final Long totalRecordCount) {
			this.totalRecordCount = totalRecordCount;
			return this;
		}

		public ApiV2Pagination build() {
			return this.build(null);
		}
		public ApiV2Pagination build(final HttpServletRequest request) {

			this.links = generateLinks(request);

			return new ApiV2Pagination(this);
		}

		private Map<String, String> generateLinks(final HttpServletRequest request) {

			Map<String, String> links = new HashMap<>();

			if (page < totalPageCount) {

				links.put(ApiV2Pagination.NEXT_PAGE_LINK_KEY, link(request, page + 1));
			}

			if (page > 1) {

				links.put(ApiV2Pagination.PREVIOUS_PAGE_LINK_KEY,
									link(request, page <= totalPageCount ? page - 1 : totalPageCount));
			}

			links.put(ApiV2Pagination.FIRST_PAGE_LINK_KEY, link(request, 1L));

			if (totalPageCount != null && totalPageCount > 0) {

				links.put(ApiV2Pagination.LAST_PAGE_LINK_KEY, link(request, totalPageCount));
			}

			return links;
		}

		private String link(final HttpServletRequest request, final Long page) {

			return ServletUriComponentsBuilder.fromRequest(request).replaceQueryParam("page", page).build().toString();
		}
	}
}
