package com.workmarket.api.v2.worker.model;

/**
 * Created by ianha on 4/7/15.
 */
public class Link {
    // API TODO - normalize into API v2 response?
    String rel;
    String href;
    String method;

    public String getHref() {
        return href;
    }

    public Link setHref(String href) {
        this.href = href;
        return this;
    }

    public String getRel() {
        return rel;
    }

    public Link setRel(String rel) {
        this.rel = rel;
        return this;
    }

    public String getMethod() {
        return method;
    }

    public Link setMethod(String method) {
        this.method = method;
        return this;
    }
}
