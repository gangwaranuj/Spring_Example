package com.workmarket.api.helpers;

import com.workmarket.api.v2.model.PaginationPage;

import java.util.List;
import java.util.Map;

public interface LinkRouterService {
    List<Map<String,String>> buildLinks(PaginationPage response);
    String buildFullPath(String relativeUrl);
}
