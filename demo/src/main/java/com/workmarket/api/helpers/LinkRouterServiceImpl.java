package com.workmarket.api.helpers;

import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.workmarket.api.v2.model.PaginationPage;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by ianha on 4/7/15.
 */
@Service
public class LinkRouterServiceImpl implements LinkRouterService {
    @Autowired HttpServletRequestAwareService servletService;

    @Override
    public List<Map<String,String>> buildLinks(PaginationPage pagination) {
        List<Map<String,String>> links = new ArrayList<>();
        if (pagination.getTotalRecordCount() == 0) {
            return links;
        }

        String requestUrl = servletService.getRequestUrl();
        String requestQueryString = servletService.getQueryString();

        Joiner.MapJoiner joiner = Joiner.on("&").withKeyValueSeparator("=");
        Map<String,String> pairs = Maps.newHashMap();

        if (StringUtils.isNotEmpty(requestQueryString)) {
            pairs.putAll(Splitter.on("&").withKeyValueSeparator("=").split(requestQueryString));
        }

        pairs.put("pageSize", String.valueOf(pagination.getPageSize()));

        pairs.put("page", "1");
        links.add(ImmutableMap.of("first", requestUrl + "?" + joiner.join(pairs)));

        if (pagination.getTotalPageCount() > 1) {
            pairs.put("page", String.valueOf(pagination.getTotalPageCount()));
            links.add(ImmutableMap.of("last", requestUrl + "?" + joiner.join(pairs)));
        }

        if (pagination.getPage() > 1) {
            pairs.put("page", String.valueOf(pagination.getPage()-1));
            links.add(ImmutableMap.of("prev", requestUrl + "?" + joiner.join(pairs)));
        }

        if (pagination.getPage() < pagination.getTotalPageCount()) {
            pairs.put("page", String.valueOf(pagination.getPage()+1));
            links.add(ImmutableMap.of("next", requestUrl + "?" + joiner.join(pairs)));
        }

        return links;
    }

    @Override
    public String buildFullPath(String relativeUrl) {
        return servletService.getServerPath() + relativeUrl;
    }
}
