package com.workmarket.api.helpers;

import javax.servlet.http.HttpServletRequest;

/**
 * Created by ianha on 4/9/15.
 */
public interface HttpServletRequestAwareService {
    String getQueryString();
    String getRequestUrl();
    String getServerPath();
    HttpServletRequest getRequest();
}
