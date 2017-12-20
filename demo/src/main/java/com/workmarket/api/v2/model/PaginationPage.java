package com.workmarket.api.v2.model;

import java.util.List;

/**
 * Created by ianha on 4/7/15.
 */
public class PaginationPage {
    Integer page;
    Integer pageSize;
    Integer totalPageCount;
    Integer totalRecordCount;
    List results;

    public List getResults() {
        return results;
    }

    public PaginationPage setResults(List results) {
        this.results = results;
        return this;
    }

    public Integer getPage() {
        return page;
    }

    public PaginationPage setPage(Integer page) {
        this.page = page;
        return this;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public PaginationPage setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
        return this;
    }

    public Integer getTotalRecordCount() {
        return totalRecordCount;
    }

    public PaginationPage setTotalRecordCount(Integer totalRecordCount) {
        this.totalRecordCount = totalRecordCount;
        return this;
    }

    public PaginationPage setTotalPageCount(Integer totalPageCount) {
        this.totalPageCount = totalPageCount;
        return this;
    }

    public Integer getTotalPageCount() {
        return totalPageCount;
    }
}
