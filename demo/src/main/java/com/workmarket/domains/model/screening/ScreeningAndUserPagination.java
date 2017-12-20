package com.workmarket.domains.model.screening;

import com.workmarket.domains.model.AbstractPagination;
import com.workmarket.domains.model.Pagination;
import com.workmarket.service.business.screening.ScreeningAndUser;

public class ScreeningAndUserPagination extends AbstractPagination<ScreeningAndUser> implements Pagination<ScreeningAndUser> {

    public enum FILTER_KEYS {}

    public enum SORTS {}

    public ScreeningAndUserPagination() {}

    public ScreeningAndUserPagination(boolean returnAllRows) {
    }
}
